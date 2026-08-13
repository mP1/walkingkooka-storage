/*
 * Copyright 2025 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.storage;

import walkingkooka.environment.AuditInfo;
import walkingkooka.store.Store;
import walkingkooka.store.StoreWatcher;
import walkingkooka.store.Stores;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * A {@link Storage} that uses a {@link Stores#treeMap(Comparator, BiFunction)} to hold {@link StoragePath} to
 * entries.
 */
final class StorageShared2TreeMapStore<C extends StorageContext> extends StorageShared2<C>
    implements TreePrintable {

    static <C extends StorageContext> StorageShared2TreeMapStore<C> empty() {
        return new StorageShared2TreeMapStore<>();
    }

    private StorageShared2TreeMapStore() {
        this.store = Stores.treeMap(
            Comparator.naturalOrder(),
            this::idSetter
        );
    }

    private StorageShared2TreeMapStoreValue idSetter(final StoragePath path,
                                                     final StorageShared2TreeMapStoreValue treeMapStoreStorageStoreValue) {
        return treeMapStoreStorageStoreValue.setPath(path);
    }

    @Override
    boolean canRead0(final StoragePath path,
                     final C context) {
        return this.store.load(path)
            .isPresent();
    }

    @Override
    boolean canWrite0(final StoragePath path,
                      final C context) {
        return true;
    }

    @Override
    Optional<StorageValue> load0(final StoragePath path,
                                 final C context) {
        return path.isParent() ?
            Optional.empty() :
            this.store.load(path)
                .map(StorageShared2TreeMapStoreValue::value);
    }

    @Override
    StorageValue save0(final StorageValue value,
                       final C context) {
        final StoragePath path = value.path();
        if(false == path.isValue() && value.value().isPresent()) {
            throw path.invalidStoragePathException("Invalid path for a value");
        }

        this.saveRootIfNecessary(context);

        final Store<StoragePath, StorageShared2TreeMapStoreValue> store = this.store;

        StorageShared2TreeMapStoreValue newSave = store.load(path)
            .orElse(null);

        if (null != newSave) {
            // update modify
            final AuditInfo auditInfo = newSave.info.auditInfo();

            newSave = newSave.setValue(value)
                .setInfo(
                    newSave.info.setAuditInfo(
                        context.refreshModifiedAuditInfo(auditInfo)
                    )
                );
        } else {
            // set creator and modified
            newSave = StorageShared2TreeMapStoreValue.with(
                StorageShared2TreeMapStoreValue.NOT_PARENT,
                StorageValueInfo.with(
                    path,
                    context.createdAuditInfo()
                ),
                value
            );

            // create parent directories as necessary
            StoragePath parentPath = path.parent()
                .orElse(null);

            while (null != parentPath && parentPath.isNotRoot()) {
                final StoragePath parentPathWithoutSlash = parentPath;

                final StorageShared2TreeMapStoreValue parent = store.load(parentPathWithoutSlash)
                    .orElse(null);
                if (null != parent) {
                    break;
                }

                // create parent entry
                store.save(
                    StorageShared2TreeMapStoreValue.with(
                        StorageShared2TreeMapStoreValue.PARENT,
                        StorageValueInfo.with(
                            parentPathWithoutSlash,
                            context.createdAuditInfo()
                        ),
                        StorageValue.with(parentPathWithoutSlash)
                    )
                );

                parentPath = parentPath.parent()
                    .orElse(null);
            }
        }

        return store.save(newSave)
            .value;
    }

    @Override
    void delete0(final StoragePath path,
                 final C context) {
        if(path.isParent()) {
            throw path.invalidStoragePathException("Invalid parent path");
        }

        final StorageShared2TreeMapStoreValue value = this.store.load(path)
            .orElse(null);
        if(null !=value) {
            if(value.parent) {
                throw path.invalidStoragePathException("Invalid parent path");
            }

            this.store.delete(path);
        }
    }

    @Override
    List<StorageValueInfo> list0(final StoragePath parent,
                                 final int offset,
                                 final int count,
                                 final C context) {
        this.saveRootIfNecessary(context);

        final StoragePath parentWithSlash = parent.withoutTrailingSeparator();

        StorageShared2TreeMapStoreValue value = this.store.load(parentWithSlash)
            .orElse(null);

        StorageValueInfoList storageValueInfoList = StorageValueInfoList.EMPTY;

        if(null != value) {
            if(value.parent) {
                storageValueInfoList = this.store.all()
                    .stream()
                    .filter(i -> parentWithSlash.equals(i.path().parent().orElse(null)))
                    .skip(offset)
                    .limit(count)
                    .map(StorageShared2TreeMapStoreValue::info)
                    .collect(
                        Collectors.collectingAndThen(
                            Collectors.toList(),
                            StorageValueInfoList::with
                        )
                    );

            } else {
                storageValueInfoList = StorageValueInfoList.EMPTY.concat(value.info);
            }
        } else {
            storageValueInfoList = StorageValueInfoList.EMPTY;
        }

        return storageValueInfoList;
    }

    private void saveRootIfNecessary(final StorageContext context) {
        if (this.store.count() == 0) {
            this.store.save(
                StorageShared2TreeMapStoreValue.with(
                    true, // parent
                    StorageValueInfo.with(
                        StoragePath.ROOT,
                        context.createdAuditInfo()
                    ),
                    StorageValue.with(StoragePath.ROOT)
                )
            );
        }
    }

    @Override
    void setAuditInfo0(final StorageValueInfo value,
                       final C context) {
        this.store.save(
            this.store.loadOrFail(
                value.path()
            ).setInfo(value)
        );
    }

    // addWatcherXXX....................................................................................................

    @Override
    Runnable addWatcher0(final StorageWatcher watcher,
                         final C context) {
        return this.store.addStoreWatcher(
            toStoreWatcher(watcher)
        );
    }

    @Override
    Runnable addWatcherOnce0(final StorageWatcher watcher,
                             final C context) {
        return this.store.addStoreWatcherOnce(
            toStoreWatcher(watcher)
        );
    }

    private static StoreWatcher<StorageShared2TreeMapStoreValue> toStoreWatcher(final StorageWatcher watcher) {
        return new StoreWatcher<>() {

            @Override
            public void onValueChange(final Optional<StorageShared2TreeMapStoreValue> oldValue,
                                      final Optional<StorageShared2TreeMapStoreValue> newValue) {
                // filter #saveRootIfNecessary
                if(false == isRoot(oldValue) && false == isRoot(newValue)) {
                    watcher.onValueChange(
                        toStorageValue(oldValue),
                        toStorageValue(newValue)
                    );
                }
            }

            // Object...................................................................................................

            @Override
            public String toString() {
                return watcher.toString();
            }
        };
    }

    private static boolean isRoot(final Optional<StorageShared2TreeMapStoreValue> value) {
        return value.map(
            (StorageShared2TreeMapStoreValue v) -> v.path().isRoot()
        ).orElse(false);
    }

    private static Optional<StorageValue> toStorageValue(final Optional<StorageShared2TreeMapStoreValue> storeValue) {
        return storeValue.map(
            StorageShared2TreeMapStoreValue::value
        );
    }

    // @VisibleForTesting
    final Store<StoragePath, StorageShared2TreeMapStoreValue> store;

    // Stoppable........................................................................................................

    @Override
    public void stop() {
        // store.stop()
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.store.hashCode();
    }

    public boolean equals(Object other) {
        return this == other || other instanceof StorageShared2TreeMapStore && this.equals0((StorageShared2TreeMapStore<?>) other);
    }

    private boolean equals0(StorageShared2TreeMapStore<?> other) {
        return this.store.equals(other.store);
    }

    @Override
    public String toString() {
        return this.store.toString();
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
        printer.indent();
        {
            TreePrintable.printTreeOrToString(
                this.store,
                printer
            );
        }
        printer.outdent();
    }
}
