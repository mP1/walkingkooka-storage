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

import java.util.List;
import java.util.Optional;

/**
 * A {@link Storage} that uses the mounts defined by an instance of {@link RoutingStorageBuilder}.
 */
final class RoutingStorage<C extends StorageContext> extends StorageShared<C> {

    // assumes a defensive copy was given.
    static <C extends StorageContext> RoutingStorage<C> with(final List<RoutingStorageRoute<C>> routes) {
        return new RoutingStorage<>(routes);
    }

    private RoutingStorage(final List<RoutingStorageRoute<C>> routes) {
        this.routes = routes;
    }

    // Store............................................................................................................

    @Override
    Optional<StorageValue> load0(final StoragePath path,
                                 final C context) {
        Optional<StorageValue> value = Optional.empty();
        RoutingStorageRoute<C> route = this.firstRouteStartingWith(path);
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
    StorageValue save0(final StorageValue value,
                       final C context) {
        RoutingStorageRoute<C> route = this.firstRouteStartingWith(value.path());
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
    void delete0(final StoragePath path,
                 final C context) {
        RoutingStorageRoute<C> route = this.firstRouteStartingWith(path);
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
    List<StorageValueInfo> list0(final StoragePath parent,
                                 final int offset,
                                 final int count,
                                 final C context) {
        final List<StorageValueInfo> storageValueInfos;

        RoutingStorageRoute<C> route = this.firstRouteStartingWith(parent);
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
    private RoutingStorageRoute<C> firstRouteStartingWith(final StoragePath path) {
        RoutingStorageRoute<C> dest = null;

        for (final RoutingStorageRoute<C> possible : this.routes) {
            if (possible.isMatch(path)) {
                dest = possible;
                break;
            }
        }

        return dest;
    }

    private final List<RoutingStorageRoute<C>> routes;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.routes.toString();
    }
}
