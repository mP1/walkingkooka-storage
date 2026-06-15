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

    @Override
    public final boolean canRead(final StoragePath path,
                                 final C context) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(context, "context");

        return this.canRead0(
            path,
            context
        );
    }

    abstract boolean canRead0(final StoragePath path,
                              final C context);

    @Override
    public final boolean canWrite(final StoragePath path,
                                  final C context) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(context, "context");

        return this.canWrite0(
            path,
            context
        );
    }

    abstract boolean canWrite0(final StoragePath path,
                               final C context);

    @Override //
    public final Optional<StorageValue> load(final StoragePath path,
                                             final C context) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(context, "context");

        return path.isParent() ?
            NO_STORAGE_VALUE :
            this.load0(
                path,
                context
            );
    }

    abstract Optional<StorageValue> load0(final StoragePath path,
                                          final C context);

    @Override
    public final StorageValue save(final StorageValue value,
                                   final C context) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(context, "context");

        this.failIfParent(
            value.path()
        );

        return this.save0(
            value,
            context
        );
    }

    abstract StorageValue save0(final StorageValue value,
                                final C context);

    @Override
    public final void delete(final StoragePath path,
                             final C context) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(context, "context");

        this.failIfParent(path);

        this.delete0(
            path,
            context
        );
    }

    private void failIfParent(final StoragePath path) {
        if(false == this instanceof RoutingStorage && false == this instanceof StorageSharedPrefixed) {
            if(path.isParent()) {
                throw path.invalidStoragePathException("Invalid parent path");
            }
        }
    }

    abstract void delete0(final StoragePath path,
                          final C context);

    @Override
    public final List<StorageValueInfo> list(final StoragePath parent,
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
