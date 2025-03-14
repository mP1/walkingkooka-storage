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
import walkingkooka.collect.set.Sets;
import walkingkooka.store.Store;
import walkingkooka.watch.Watchers;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A {@link StorageStore} that uses the mounts defined by an instance of {@link RoutingStorageStoreBuilder}.
 */
final class RoutingStorageStore implements StorageStore {

    // assumes a defensive copy was given.
    static RoutingStorageStore with(final List<RoutingStorageStoreRoute> routes) {
        return new RoutingStorageStore(routes);
    }

    private RoutingStorageStore(final List<RoutingStorageStoreRoute> routes) {
        this.routes = routes;
    }

    // Store............................................................................................................

    @Override
    public Optional<StorageValue> load(final StoragePath path) {
        Objects.requireNonNull(path, "path");

        Optional<StorageValue> value = Optional.empty();
        RoutingStorageStoreRoute route = this.firstRouteStartingWith(path);
        if (null != route) {
            value = route.store.load(
                route.remove(path)
            ).map(v -> v.setPath(
                route.add(
                    v.path()
                )
            ));
        }

        return value;
    }

    @Override
    public StorageValue save(final StorageValue storageValue) {
        Objects.requireNonNull(storageValue, "storageValue");

        RoutingStorageStoreRoute route = this.firstRouteStartingWith(storageValue.path());
        if (null != route) {
            final StorageValue saved = route.store.save(
                storageValue.setPath(
                    route.remove(
                        storageValue.path()
                    )
                )
            );

            return saved.setPath(
                route.add(saved.path())
            );
        } else {
            throw new UnsupportedOperationException("Storing " + storageValue + " is not supported");
        }
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<StorageValue> watcher) {
        Objects.requireNonNull(watcher, "watcher");

        final List<Runnable> removers = Lists.array();

        for (final RoutingStorageStoreRoute route : this.routes) {
            removers.add(
                route.store.addSaveWatcher(
                    v -> watcher.accept(v)
                )
            );
        }

        return Watchers.runnableCollection(removers);
    }

    @Override
    public void delete(final StoragePath path) {
        Objects.requireNonNull(path, "path");

        RoutingStorageStoreRoute route = this.firstRouteStartingWith(path);
        if (null != route) {
            route.store.delete(
                route.remove(path)
            );
        } else {
            throw new UnsupportedOperationException("Deleting " + path + " is not supported");
        }
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<StoragePath> watcher) {
        Objects.requireNonNull(watcher, "watcher");

        final List<Runnable> removers = Lists.array();

        for (final RoutingStorageStoreRoute route : this.routes) {
            removers.add(
                route.store.addDeleteWatcher(
                    v -> watcher.accept(v)
                )
            );
        }

        return Watchers.runnableCollection(removers);
    }

    @Override
    public int count() {
        int count = 0;

        for (final RoutingStorageStoreRoute route : this.routes) {
            count += route.store.count();
        }

        return count;
    }

    @Override
    public Set<StoragePath> ids(final int offset,
                                final int count) {
        Store.checkOffsetAndCount(
            offset,
            count
        );

        final Set<StoragePath> paths = Sets.ordered();
        int i = offset;

        for (final RoutingStorageStoreRoute route : this.routes) {
            final StorageStore store = route.store;
            final int storeCount = store.count();

            if (i < storeCount) {
                final Set<StoragePath> storePaths = store.ids(
                    i,
                    count - paths.size()
                );

                for (final StoragePath path : storePaths) {
                    paths.add(
                        route.add(path)
                    );
                }

                i = 0;
            } else {
                i = i - storeCount;
            }

            if (count <= 0) {
                break;
            }
        }

        return Sets.readOnly(paths);
    }

    @Override
    public List<StorageValue> values(final int offset,
                                     final int count) {
        return this.ids(
            offset,
            count
        ).stream()
            .map(p -> this.load(p).orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public List<StorageValue> between(final StoragePath from,
                                      final StoragePath to) {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");

        return this.all()
            .stream()
            .filter(v -> {
                    final StoragePath path = v.path();
                    return from.compareTo(path) <= 0 && to.compareTo(path) >= 0;
                }
            ).collect(Collectors.toList());
    }

    // StorageStore.....................................................................................................

    @Override
    public List<StorageValueInfo> storageValueInfos(final StoragePath parent,
                                                    final int offset,
                                                    final int count) {
        Objects.requireNonNull(parent, "parent");
        Store.checkOffsetAndCount(
            offset,
            count
        );

        final List<StorageValueInfo> storageValueInfos;

        RoutingStorageStoreRoute route = this.firstRouteStartingWith(parent);
        if (null != route) {
            storageValueInfos = route.store.storageValueInfos(
                    route.remove(parent),
                    offset,
                    count
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
     * Selects the first {@link RoutingStorageStoreRoute} that matches the given path, returning null if none matched.
     */
    private RoutingStorageStoreRoute firstRouteStartingWith(final StoragePath path) {
        RoutingStorageStoreRoute dest = null;

        for (final RoutingStorageStoreRoute possible : this.routes) {
            if (possible.isMatch(path)) {
                dest = possible;
                break;
            }
        }

        return dest;
    }

    private final List<RoutingStorageStoreRoute> routes;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.routes.toString();
    }
}
