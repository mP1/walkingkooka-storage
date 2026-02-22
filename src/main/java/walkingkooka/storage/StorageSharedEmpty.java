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

import java.util.List;
import java.util.Optional;

/**
 * A {@link Storage} that is always empty and does not support saving, and deletes will fail.
 */
final class StorageSharedEmpty<C extends StorageContext> extends StorageShared<C> {

    static <C extends StorageContext> StorageSharedEmpty<C> instance() {
        return INSTANCE;
    }

    /**
     * Singleton instance
     */
    private final static StorageSharedEmpty INSTANCE = new StorageSharedEmpty();

    private StorageSharedEmpty() {
        super();
    }

    // Storage..........................................................................................................

    @Override
    Optional<StorageValue> load0(final StoragePath path,
                                 final C context) {
        return Optional.empty();
    }

    @Override
    StorageValue save0(final StorageValue value,
                       final C context) {
        throw new UnsupportedOperationException();
    }

    @Override
    void delete0(final StoragePath path,
                 final C context) {
        throw new UnsupportedOperationException();
    }

    @Override
    List<StorageValueInfo> list0(final StoragePath parent,
                                 final int offset,
                                 final int count,
                                 final C context) {

        // always returns nothing
        return Lists.of();
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "";
    }
}
