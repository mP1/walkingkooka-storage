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
import walkingkooka.store.Stores;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

final class TreeMapStoreStorage<C extends StorageContext> implements Storage<C> {

    static <C extends StorageContext> TreeMapStoreStorage<C> empty() {
        return new TreeMapStoreStorage<>();
    }

    private TreeMapStoreStorage() {
        this.store = Stores.treeMap(
            Comparator.naturalOrder(),
            this::idSetter
        );
    }

    private TreeMapStoreStorageValue idSetter(final StoragePath path,
                                              final TreeMapStoreStorageValue treeMapStoreStorageStoreValue) {
        return treeMapStoreStorageStoreValue.setPath(path);
    }

    @Override
    public Optional<StorageValue> load(final StoragePath path,
                                       final C context) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(context, "context");

        return this.store.load(path)
            .map(TreeMapStoreStorageValue::value);
    }

    @Override
    public StorageValue save(final StorageValue value,
                             final C context) {
        Objects.requireNonNull(value, "storageValue");
        Objects.requireNonNull(context, "context");

        this.saveRootIfNecessary(context);

        final StoragePath path = value.path();

        final Store<StoragePath, TreeMapStoreStorageValue> store = this.store;

        TreeMapStoreStorageValue newSave = store.load(path)
            .orElse(null);

        if (null != newSave) {
            // update modify
            final AuditInfo auditInfo = newSave.info.auditInfo();

            newSave = newSave.setInfo(
                newSave.info.setAuditInfo(
                    context.refreshModifiedAuditInfo(auditInfo)
                )
            );
        } else {
            // set creator and modified
            newSave = TreeMapStoreStorageValue.with(
                StorageValueInfo.with(
                    path,
                    context.createdAuditInfo()
                ),
                value
            );

            // create parent directories as necessary
            StoragePath parentPath = path.parent()
                .orElse(null);

            while (null != parentPath && false == parentPath.isRoot()) {
                final TreeMapStoreStorageValue parent = store.load(parentPath)
                    .orElse(null);
                if (null != parent) {
                    break;
                }

                // create parent entry
                store.save(
                    TreeMapStoreStorageValue.with(
                        StorageValueInfo.with(
                            parentPath,
                            context.createdAuditInfo()
                        ),
                        StorageValue.with(
                            parentPath,
                            StorageValue.NO_VALUE
                        )
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
    public void delete(final StoragePath path,
                       final C context) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(context, "context");

        this.store.delete(path);
    }

    @Override
    public List<StorageValueInfo> list(final StoragePath parent,
                                       final int offset,
                                       final int count,
                                       final C context) {
        Objects.requireNonNull(parent, "parent");
        Store.checkOffsetAndCount(offset, count);
        Objects.requireNonNull(context, "context");

        this.saveRootIfNecessary(context);

        return this.store.all()
            .stream()
            .filter(i -> parent.equals(i.path().parent().orElse(null)))
            .skip(offset)
            .limit(count)
            .map(TreeMapStoreStorageValue::info)
            .collect(
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    StorageValueInfoList::with
                )
            );
    }

    private void saveRootIfNecessary(final StorageContext context) {
        if (this.store.count() == 0) {
            this.store.save(
                TreeMapStoreStorageValue.with(
                    StorageValueInfo.with(
                        StoragePath.ROOT,
                        context.createdAuditInfo()
                    ),
                    StorageValue.with(
                        StoragePath.ROOT,
                        StorageValue.NO_VALUE
                    )
                )
            );
        }
    }

    private final Store<StoragePath, TreeMapStoreStorageValue> store;

    @Override
    public String toString() {
        return this.store.toString();
    }
}
