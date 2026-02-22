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
import java.util.Optional;
import java.util.stream.Collectors;

final class StorageSharedTreeMap<C extends StorageContext> extends StorageShared<C> {

    static <C extends StorageContext> StorageSharedTreeMap<C> empty() {
        return new StorageSharedTreeMap<>();
    }

    private StorageSharedTreeMap() {
        this.store = Stores.treeMap(
            Comparator.naturalOrder(),
            this::idSetter
        );
    }

    private StorageSharedTreeMapValue idSetter(final StoragePath path,
                                               final StorageSharedTreeMapValue treeMapStoreStorageStoreValue) {
        return treeMapStoreStorageStoreValue.setPath(path);
    }

    @Override
    Optional<StorageValue> load0(final StoragePath path,
                                 final C context) {
        return this.store.load(path)
            .map(StorageSharedTreeMapValue::value);
    }

    @Override
    StorageValue save0(final StorageValue value,
                       final C context) {
        this.saveRootIfNecessary(context);

        final StoragePath path = value.path();

        final Store<StoragePath, StorageSharedTreeMapValue> store = this.store;

        StorageSharedTreeMapValue newSave = store.load(path)
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
            newSave = StorageSharedTreeMapValue.with(
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
                final StorageSharedTreeMapValue parent = store.load(parentPath)
                    .orElse(null);
                if (null != parent) {
                    break;
                }

                // create parent entry
                store.save(
                    StorageSharedTreeMapValue.with(
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
    void delete0(final StoragePath path,
                 final C context) {
        this.store.delete(path);
    }

    @Override
    List<StorageValueInfo> list0(final StoragePath parent,
                                 final int offset,
                                 final int count,
                                 final C context) {
        this.saveRootIfNecessary(context);

        return this.store.all()
            .stream()
            .filter(i -> parent.equals(i.path().parent().orElse(null)))
            .skip(offset)
            .limit(count)
            .map(StorageSharedTreeMapValue::info)
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
                StorageSharedTreeMapValue.with(
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

    private final Store<StoragePath, StorageSharedTreeMapValue> store;

    @Override
    public String toString() {
        return this.store.toString();
    }
}
