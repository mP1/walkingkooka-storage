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

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.store.Store;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A {@link StorageStore} that is always empty and does not support saving, and deletes will fail.
 */
final class EmptyStorageStore implements StorageStore {

    /**
     * Singleton instance
     */
    final static EmptyStorageStore INSTANCE = new EmptyStorageStore();

    private EmptyStorageStore() {
        super();
    }

    // Store............................................................................................................

    @Override
    public Optional<StorageValue> load(final StoragePath storageNames) {
        Objects.requireNonNull(storageNames, "storageNames");

        return Optional.empty();
    }

    @Override
    public StorageValue save(final StorageValue storageValue) {
        Objects.requireNonNull(storageValue, "storageValue");

        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<StorageValue> watcher) {
        Objects.requireNonNull(watcher, "watcher");

        return () -> {};
    }

    @Override
    public void delete(final StoragePath storageNames) {
        Objects.requireNonNull(storageNames, "storageNames");

        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<StoragePath> watcher) {
        Objects.requireNonNull(watcher, "watcher");

        return () -> {};
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public Set<StoragePath> ids(final int offset,
                                final int count) {
        Store.checkOffsetAndCount(offset, count);
        return Sets.of();
    }

    @Override
    public List<StorageValue> values(final int offset,
                                     final int count) {
        Store.checkOffsetAndCount(offset, count);
        return Lists.of();
    }

    @Override
    public List<StorageValue> between(final StoragePath from,
                                      final StoragePath to) {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");

        return Lists.of();
    }

    // StorageStore.....................................................................................................

    @Override
    public List<StorageValueInfo> storageValueInfos(final StoragePath parent,
                                                    final int offset,
                                                    final int count) {
        Objects.requireNonNull(parent, "parent");
        Store.checkOffsetAndCount(offset, count);

        // always returns nothing
        return Lists.of();
    }

    @Override
    public String toString() {
        return "";
    }
}
