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

import walkingkooka.collect.list.ImmutableList;
import walkingkooka.collect.list.Lists;
import walkingkooka.store.Store;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link Storage} that uses the mounts defined by an instance of {@link RoutingStorageBuilder}.
 */
final class RoutingStorage implements Storage {

    // assumes a defensive copy was given.
    static <C extends StorageContext> RoutingStorage with(final List<RoutingStorageRoute> routes) {
        return new RoutingStorage(routes);
    }

    private RoutingStorage(final List<RoutingStorageRoute> routes) {
        this.routes = routes;
    }

    // Store............................................................................................................

    @Override
    public Optional<StorageValue> load(final StoragePath path,
                                       final StorageContext context) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(context, "context");

        Optional<StorageValue> value = Optional.empty();
        RoutingStorageRoute route = this.firstRouteStartingWith(path);
        if (null != route) {
            value = route.store.load(
                route.remove(path),
                context
            ).map(v -> v.setPath(
                route.add(
                    v.path()
                )
            ));
        }

        return value;
    }

    @Override
    public StorageValue save(final StorageValue value,
                             final StorageContext context) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(context, "context");

        RoutingStorageRoute route = this.firstRouteStartingWith(value.path());
        if (null != route) {
            final StorageValue saved = route.store.save(
                value.setPath(
                    route.remove(
                        value.path()
                    )
                ),
                context
            );

            return saved.setPath(
                route.add(saved.path())
            );
        } else {
            throw new UnsupportedOperationException("Storing " + value + " is not supported");
        }
    }

    @Override
    public void delete(final StoragePath path,
                       final StorageContext context) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(context, "context");

        RoutingStorageRoute route = this.firstRouteStartingWith(path);
        if (null != route) {
            route.store.delete(
                route.remove(path),
                context
            );
        } else {
            throw new UnsupportedOperationException("Deleting " + path + " is not supported");
        }
    }

    // Storage.....................................................................................................

    @Override
    public List<StorageValueInfo> list(final StoragePath parent,
                                       final int offset,
                                       final int count,
                                       final StorageContext context) {
        Objects.requireNonNull(parent, "parent");
        Store.checkOffsetAndCount(
            offset,
            count
        );
        Objects.requireNonNull(context, "context");

        final List<StorageValueInfo> storageValueInfos;

        RoutingStorageRoute route = this.firstRouteStartingWith(parent);
        if (null != route) {
            storageValueInfos = route.store.list(
                    route.remove(parent),
                    offset,
                    count,
                    context
                ).stream()
                .map(i -> i.setPath(
                    route.add(
                        i.path()
                    )
                )).collect(ImmutableList.collector());
        } else {
            storageValueInfos = Lists.empty();
        }

        return storageValueInfos;
    }

    /**
     * Selects the first {@link RoutingStorageRoute} that matches the given path, returning null if none matched.
     */
    private RoutingStorageRoute firstRouteStartingWith(final StoragePath path) {
        RoutingStorageRoute dest = null;

        for (final RoutingStorageRoute possible : this.routes) {
            if (possible.isMatch(path)) {
                dest = possible;
                break;
            }
        }

        return dest;
    }

    private final List<RoutingStorageRoute> routes;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.routes.toString();
    }
}
