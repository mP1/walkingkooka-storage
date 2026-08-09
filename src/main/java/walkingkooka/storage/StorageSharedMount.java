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
import walkingkooka.collect.set.SortedSets;
import walkingkooka.watch.Watchers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.function.Function;

/**
 * A {@link Storage} that wraps another supporting dynamic mount/unmounts at unique {@link StoragePath}.
 */
final class StorageSharedMount<C extends StorageContext> extends StorageShared<C> {

    // assumes a defensive copy was given.
    static <C extends StorageContext> StorageSharedMount<C> with(final Storage<C> storage) {
        return new StorageSharedMount<>(storage);
    }

    private StorageSharedMount(final Storage<C> storage) {
        this.mountings = SortedSets.tree();

        this.mountings.add(
            StorageSharedMountMounting.with(
                StoragePath.ROOT,
                storage
            )
        );
    }

    // Storage..........................................................................................................

    @Override
    boolean canRead0(final StoragePath path,
                     final C context) {
        final StorageSharedMountMounting<C> mounting = this.firstMount(path);

        return mounting.storage.canRead(
            mounting.remove(path),
            context
        );
    }

    @Override
    boolean canWrite0(final StoragePath path,
                      final C context) {
        final StorageSharedMountMounting<C> mounting = this.firstMount(path);

        return mounting.storage.canWrite(
            mounting.remove(path),
            context
        );
    }

    @Override
    Optional<StorageValue> load0(final StoragePath path,
                                 final C context) {
        final StorageSharedMountMounting<C> mounting = this.firstMount(path);

        return mounting.storage.load(
            mounting.remove(path),
            context
        ).map(v -> v.setPath(
            mounting.add(
                v.path()
            )
        ));
    }

    @Override
    StorageValue save0(final StorageValue value,
                       final C context) {
        final StorageSharedMountMounting<C> mounting = this.firstMount(value.path());

        final StorageValue saved = mounting.storage.save(
            value.setPath(
                mounting.remove(
                    value.path()
                )
            ),
            context
        );

        return saved.setPath(
            mounting.add(saved.path())
        );
    }

    @Override
    void delete0(final StoragePath path,
                 final C context) {
        final StorageSharedMountMounting<C> mounting = this.firstMount(path);

        mounting.storage.delete(
            mounting.remove(path),
            context
        );
    }

    // Storage..........................................................................................................

    @Override
    List<StorageValueInfo> list0(final StoragePath parent,
                                 final int offset,
                                 final int count,
                                 final C context) {
        final StorageSharedMountMounting<C> mounting = this.firstMount(parent);

        return mounting.storage.list(
                mounting.remove(parent),
                offset,
                count,
                context
            ).stream()
            .map(i -> i.setPath(
                    mounting.add(
                        i.path()
                    )
                )
            ).collect(ImmutableList.collector());
    }

    /**
     * Selects the first {@link StorageSharedMountMounting} that matches the given path.
     */
    // @VisibleForTesting
    StorageSharedMountMounting<C> firstMount(final StoragePath path) {
        for (final StorageSharedMountMounting<C> possible : this.mountings) {
            if (possible.isMatch(path)) {
                return possible;
            }
        }

        throw new UnsupportedOperationException();
    }

    /**
     * mountings are sorted so in reverse lexical order so longer(ancestor) mounts appear shorter(parent) mounts,
     * other shadowing will occur and the wrong mount would be selected.
     * <pre>
     * /mount1/mount2
     * /mount1
     * </pre>
     */
    private final SortedSet<StorageSharedMountMounting<C>> mountings;

    // setAuditInfo.....................................................................................................

    @Override
    void setAuditInfo0(final StorageValueInfo value,
                       final C context) {
        final StoragePath path = value.path();

        final StorageSharedMountMounting<C> mounting = this.firstMount(path);
        mounting.storage.setAuditInfo(
            value.setPath(
                mounting.remove(path)
            ),
            context
        );
    }

    // mount............................................................................................................

    @Override
    void mount0(final StoragePath path,
                final Storage<C> storage,
                final C context) {
        final Collection<StorageSharedMountMounting<C>> mountings = this.mountings;

        for (final StorageSharedMountMounting<C> mounting : mountings) {
            if (mounting.path.equals(path)) {
                throw path.invalidStoragePathException("Mount exists");
            }
        }

        mountings.add(
            StorageSharedMountMounting.with(
                path,
                storage
            )
        );
    }

    @Override
    void unmount0(final StoragePath path,
                  final C context) {
        if (false == path.isRoot()) {
            final Collection<StorageSharedMountMounting<C>> mountings = this.mountings;

            for (final StorageSharedMountMounting<C> mounting : mountings) {
                if (mounting.path.equals(path)) {
                    mountings.remove(mounting);
                    return;
                }
            }
        }

        throw path.invalidStoragePathException("Invalid mount");
    }

    // addWatcher.......................................................................................................

    @Override
    Runnable addWatcher0(final StorageWatcher watcher,
                         final C context) {
        return this.addWatcherToRoutes(
            (StorageSharedMountMounting<C> mounting) -> mounting.addWatcher(
                watcher,
                context
            )
        );
    }

    @Override
    Runnable addWatcherOnce0(final StorageWatcher watcher,
                             final C context) {
        return this.addWatcherToRoutes(
            (StorageSharedMountMounting<C> mounting) -> mounting.addWatcherOnce(
                watcher,
                context
            )
        );
    }

    private Runnable addWatcherToRoutes(final Function<StorageSharedMountMounting<C>, Runnable> adder) {
        final List<Runnable> removers = Lists.array();

        for (final StorageSharedMountMounting<C> mounting : this.mountings) {
            removers.add(
                adder.apply(mounting)
            );
        }

        return Watchers.runnableCollection(removers);
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.mountings.toString();
    }
}
