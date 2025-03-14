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

import java.util.Objects;

/**
 * A single mount within a {@link RoutingStorageStore}.
 */
final class RoutingStorageStoreRoute {

    static RoutingStorageStoreRoute with(final StoragePath path,
                                         final StorageStore store) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(store, "store");

        return new RoutingStorageStoreRoute(
            path,
            store
        );
    }

    private RoutingStorageStoreRoute(final StoragePath path,
                                     final StorageStore store) {
        super();
        this.path = path;
        this.store = store;

        this.storagePathSlash = path.isRoot() ?
            path.value() :
            path.value()
                .concat(StoragePath.SEPARATOR.string());
    }

    // /mount111 vs /mount111 -> true
    // /mount111 vs /mount111/under222 -> true
    // /mount222 vs /mount333/under333 -> false
    // / vs /under444 -> true
    boolean isMatch(final StoragePath path) {
        return this.path.equals(path) ||
            path.toString().startsWith(this.storagePathSlash);
    }

    private String storagePathSlash;

    // /mount1/path2/path3
    //
    // /path/path2
    StoragePath remove(final StoragePath path) {
        final StoragePath thisStoragePath = this.path;

        return thisStoragePath.isRoot() ?
            path :
            thisStoragePath.equals(path) ?
                StoragePath.ROOT :
                StoragePath.parse(
                    path.value()
                        .substring(
                            thisStoragePath.value().length()
                        )
                );
    }

    StoragePath add(final StoragePath path) {
        return this.path.append(path);
    }

    final StoragePath path;

    final StorageStore store;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.path,
            this.store
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof RoutingStorageStoreRoute && this.equals0((RoutingStorageStoreRoute) other);
    }

    private boolean equals0(final RoutingStorageStoreRoute other) {
        return this.path.equals(other.path) &&
            this.store.equals(other.store);
    }

    @Override
    public String toString() {
        return this.path.quotedAppendedWithStar() + " " + this.store;
    }
}
