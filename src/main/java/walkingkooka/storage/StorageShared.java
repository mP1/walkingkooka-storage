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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

abstract class StorageShared<C extends StorageContext> implements Storage<C> {

    StorageShared() {
        super();
    }
    // Storage..........................................................................................................

    @Override final public Optional<StorageValue> load(final StoragePath path,
                                                       final C context) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(context, "context");

        return this.load0(
            path,
            context
        );
    }

    abstract Optional<StorageValue> load0(final StoragePath path,
                                          final C context);

    @Override
    public StorageValue save(final StorageValue value,
                             final C context) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(context, "context");

        return this.save0(
            value,
            context
        );
    }

    abstract StorageValue save0(final StorageValue value,
                                final C context);

    @Override
    public void delete(final StoragePath path,
                       final C context) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(context, "context");

        this.delete0(
            path,
            context
        );
    }

    abstract void delete0(final StoragePath path,
                          final C context);

    @Override
    public List<StorageValueInfo> list(final StoragePath parent,
                                       final int offset,
                                       final int count,
                                       final C context) {
        Objects.requireNonNull(parent, "parent");
        Store.checkOffsetAndCount(offset, count);
        Objects.requireNonNull(context, "context");

        return this.list0(
            parent,
            offset,
            count,
            context
        );
    }

    abstract List<StorageValueInfo> list0(final StoragePath parent,
                                          final int offset,
                                          final int count,
                                          final C context);
}
