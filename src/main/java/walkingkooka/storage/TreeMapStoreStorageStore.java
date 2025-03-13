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

import walkingkooka.net.email.EmailAddress;
import walkingkooka.store.Store;
import walkingkooka.store.Stores;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

final class TreeMapStoreStorageStore implements StorageStore {

    static TreeMapStoreStorageStore with(final StorageStoreContext context) {
        return new TreeMapStoreStorageStore(
            Objects.requireNonNull(context, "context")
        );
    }

    private TreeMapStoreStorageStore(final StorageStoreContext context) {
        this.store = Stores.treeMap(
            Comparator.naturalOrder(),
            this::idSetter
        );
        this.context = context;
    }

    private TreeMapStoreStorageStoreValue idSetter(final StoragePath path,
                                                   final TreeMapStoreStorageStoreValue treeMapStoreStorageStoreValue) {
        return treeMapStoreStorageStoreValue.setPath(path);
    }

    @Override
    public Optional<StorageValue> load(final StoragePath storagePath) {
        return this.store.load(storagePath)
            .map(TreeMapStoreStorageStoreValue::value);
    }

    @Override
    public StorageValue save(final StorageValue storageValue) {
        Objects.requireNonNull(storageValue, "storageValue");

        final StoragePath path = storageValue.path();

        final StorageStoreContext context = this.context;
        final EmailAddress user = context.userOrFail();
        final LocalDateTime now = context.now();

        final Store<StoragePath, TreeMapStoreStorageStoreValue> store = this.store;

        TreeMapStoreStorageStoreValue newSave = store.load(path)
            .orElse(null);

        if (null != newSave) {
            // update modify
            newSave = newSave.setInfo(
                newSave.info.setModifiedBy(user)
                    .setModifiedTimestamp(now)
            );
        } else {
            // set creator and modified
            newSave = TreeMapStoreStorageStoreValue.with(
                StorageValueInfo.with(
                    path,
                    user, // creator
                    now, // created-timestamp
                    user, // modified by
                    now // modified-timestamp
                ),
                storageValue
            );
        }

        return this.store.save(newSave)
            .value;
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<StorageValue> watcher) {
        Objects.requireNonNull(watcher, "watcher");

        return this.store.addSaveWatcher(
            (v) -> watcher.accept(v.value)
        );
    }

    @Override
    public void delete(final StoragePath storagePath) {
        this.store.delete(storagePath);
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<StoragePath> watcher) {
        return this.store.addDeleteWatcher(watcher);
    }

    @Override
    public int count() {
        return this.store.count();
    }

    @Override
    public Set<StoragePath> ids(final int offset,
                               final int count) {
        return this.store.ids(
            offset,
            count
        );
    }

    @Override
    public List<StorageValue> values(final int offset,
                                     final int count) {
        return toStorageValues(
            this.store.values(
                offset,
                count
            )
        );
    }

    @Override
    public List<StorageValue> between(final StoragePath from,
                                      final StoragePath to) {
        return toStorageValues(
            this.store.between(
                from,
                to
            )
        );
    }

    @Override
    public List<StorageValueInfo> storageValueInfos(final StoragePath parent,
                                                    final int offset,
                                                    final int count) {
        Objects.requireNonNull(parent, "parent");
        Store.checkOffsetAndCount(offset, count);

        return this.store.all()
            .stream()
            .filter(i -> parent.equals(i.path().parent().orElse(null)))
            .skip(offset)
            .limit(count)
            .map(TreeMapStoreStorageStoreValue::info)
            .collect(Collectors.toList());
    }

    private final Store<StoragePath, TreeMapStoreStorageStoreValue> store;

    private final StorageStoreContext context;

    @Override
    public String toString() {
        return this.store.toString();
    }

    // helpers..........................................................................................................

    private static List<StorageValue> toStorageValues(final List<TreeMapStoreStorageStoreValue> values) {
        return values.stream()
            .map(TreeMapStoreStorageStoreValue::value)
            .collect(Collectors.toList());
    }
}
