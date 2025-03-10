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

import walkingkooka.store.Store;
import walkingkooka.store.Stores;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

final class TreeMapStoreStorageStore implements StorageStore {

    static TreeMapStoreStorageStore empty() {
        return new TreeMapStoreStorageStore();
    }

    private TreeMapStoreStorageStore() {
        this.store = Stores.treeMap(
            Comparator.naturalOrder(),
            TreeMapStoreStorageStore::idSetter
        );
    }

    private static StorageValue idSetter(final StorageKey id,
                                         final StorageValue value) {
        return value.setKey(id);
    }

    @Override
    public Optional<StorageValue> load(final StorageKey storageKey) {
        return this.store.load(storageKey);
    }

    @Override
    public StorageValue save(final StorageValue storageValue) {
        return this.store.save(storageValue);
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<StorageValue> watcher) {
        return this.store.addSaveWatcher(watcher);
    }

    @Override
    public void delete(final StorageKey storageKey) {
        this.store.delete(storageKey);
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<StorageKey> watcher) {
        return this.store.addDeleteWatcher(watcher);
    }

    @Override
    public int count() {
        return this.store.count();
    }

    @Override
    public Set<StorageKey> ids(final int offset,
                               final int count) {
        return this.store.ids(
            offset,
            count
        );
    }

    @Override
    public List<StorageValue> values(final int offset,
                                     final int count) {
        return this.store.values(
            offset,
            count
        );
    }

    @Override
    public List<StorageValue> between(final StorageKey from,
                                      final StorageKey to) {
        return this.store.between(
            from,
            to
        );
    }

    private final Store<StorageKey, StorageValue> store;

    @Override
    public String toString() {
        return this.store.toString();
    }
}
