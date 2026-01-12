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
import walkingkooka.store.Store;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link Storage} that is always empty and does not support saving, and deletes will fail.
 */
final class EmptyStorage<C extends StorageContext> implements Storage<C> {

    static <C extends StorageContext> EmptyStorage<C> instance() {
        return INSTANCE;
    }

    /**
     * Singleton instance
     */
    private final static EmptyStorage INSTANCE = new EmptyStorage();

    private EmptyStorage() {
        super();
    }

    // Storage..........................................................................................................

    @Override
    public Optional<StorageValue> load(final StoragePath path,
                                       final C context) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(context, "context");

        return Optional.empty();
    }

    @Override
    public StorageValue save(final StorageValue value,
                             final C context) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(context, "context");

        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(final StoragePath path,
                       final C context) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(context, "context");

        throw new UnsupportedOperationException();
    }

    @Override
    public List<StorageValueInfo> list(final StoragePath parent,
                                       final int offset,
                                       final int count,
                                       final C context) {
        Objects.requireNonNull(parent, "parent");
        Store.checkOffsetAndCount(offset, count);
        Objects.requireNonNull(context, "context");

        // always returns nothing
        return Lists.of();
    }

    @Override
    public String toString() {
        return "";
    }
}
