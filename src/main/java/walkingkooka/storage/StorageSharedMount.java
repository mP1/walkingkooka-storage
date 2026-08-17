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

import walkingkooka.Cast;
import walkingkooka.collect.list.ImmutableList;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.watch.Watchers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.function.Function;

/**
 * A {@link Storage} that wraps another supporting dynamic mount/unmounts at unique {@link StoragePath}.
 * <br>
 * Note that mount {@link StoragePath} with or without trailing {@link StoragePath#SEPARATOR} are equivalent.
 * <pre>
 * /mount1
 * /mount1/
 * </pre>
 */
final class StorageSharedMount<C extends StorageContext> extends StorageShared<C>
    implements TreePrintable {

    // assumes a defensive copy was given.
    static <C extends StorageContext> StorageSharedMount<C> with(final Storage<C> storage) {
        return new StorageSharedMount<>(storage);
    }

    private StorageSharedMount(final Storage<C> storage) {
        this.mountPoints = SortedSets.tree();

        this.mountPoints.add(
            StorageMountPoint.with(
                StoragePath.ROOT,
                storage
            )
        );
    }

    // Storage..........................................................................................................

    @Override
    boolean canRead0(final StoragePath path,
                     final C context) {
        final StorageMountPoint<C> mount = this.firstMount(path);

        return mount.storage.canRead(
            mount.remove(path),
            context
        );
    }

    @Override
    boolean canWrite0(final StoragePath path,
                      final C context) {
        final StorageMountPoint<C> mount = this.firstMount(path);

        return mount.storage.canWrite(
            mount.remove(path),
            context
        );
    }

    @Override
    Optional<StorageValue> load0(final StoragePath path,
                                 final C context) {
        final StorageMountPoint<C> mount = this.firstMount(path);

        return mount.storage.load(
            mount.remove(path),
            context
        ).map(v -> v.setPath(
            mount.add(
                v.path()
            )
        ));
    }

    @Override
    StorageValue save0(final StorageValue value,
                       final C context) {
        final StorageMountPoint<C> mount = this.firstMount(value.path());

        final StorageValue saved = mount.storage.save(
            value.setPath(
                mount.remove(
                    value.path()
                )
            ),
            context
        );

        return saved.setPath(
            mount.add(saved.path())
        );
    }

    @Override
    void delete0(final StoragePath path,
                 final C context) {
        final StorageMountPoint<C> mounting = this.firstMount(path);

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
        final StorageMountPoint<C> mount = this.firstMount(parent);

        return mount.storage.list(
                mount.remove(parent),
                offset,
                count,
                context
            ).stream()
            .map(i -> i.setPath(
                    mount.add(
                        i.path()
                    )
                )
            ).collect(ImmutableList.collector());
    }

    /**
     * Selects the first {@link StorageMountPoint} that matches the given path.
     */
    // @VisibleForTesting
    StorageMountPoint<C> firstMount(final StoragePath path) {
        for (final StorageMountPoint<C> possible : this.mountPoints) {
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
    private final SortedSet<StorageMountPoint<C>> mountPoints;

    // setAuditInfo.....................................................................................................

    @Override
    void setAuditInfo0(final StorageValueInfo value,
                       final C context) {
        final StoragePath path = value.path();

        final StorageMountPoint<C> mount = this.firstMount(path);
        mount.storage.setAuditInfo(
            value.setPath(
                mount.remove(path)
            ),
            context
        );
    }

    // mount............................................................................................................

    @Override
    void mount0(final StorageMountPoint<C> mountPoint,
                final C context) {
        final StoragePath mountPath = mountPoint.path()
            .withoutTrailingSeparator();
        final Collection<StorageMountPoint<C>> mountPoints = this.mountPoints;

        for (final StorageMountPoint<C> possible : mountPoints) {
            final StoragePath path = mountPoint.path();
            if (possible.path.equals(mountPath)) {
                throw path.invalidStoragePathException("Mount exists");
            }
        }

        mountPoints.add(mountPoint);
    }

    @Override
    void unmount0(final StoragePath path,
                  final C context) {
        if (path.isNotRoot()) {
            final StoragePath mountPath = path.withoutTrailingSeparator();

            final Collection<StorageMountPoint<C>> mountPoints = this.mountPoints;

            for (final StorageMountPoint<C> mounting : mountPoints) {
                if (mounting.path.equals(mountPath)) {
                    mountPoints.remove(mounting);
                    return;
                }
            }
        }

        throw path.invalidStoragePathException("Invalid mount");
    }

    @Override
    public List<StorageMountPoint<C>> mountPoints() {

        // reverse necessary because StorageMountPointPaths are reversed
        return Cast.to(
            StorageMountPointList.EMPTY
                .setElements(
                    Cast.to(this.mountPoints)
                ).reverse()
        );
    }

    // addWatcher.......................................................................................................

    @Override
    Runnable addWatcher0(final StorageWatcher watcher,
                         final C context) {
        return this.addWatcherToMounts(
            (StorageMountPoint<C> mount) -> mount.addWatcher(
                watcher,
                context
            )
        );
    }

    @Override
    Runnable addWatcherOnce0(final StorageWatcher watcher,
                             final C context) {
        return this.addWatcherToMounts(
            (StorageMountPoint<C> mount) -> mount.addWatcherOnce(
                watcher,
                context
            )
        );
    }

    private Runnable addWatcherToMounts(final Function<StorageMountPoint<C>, Runnable> adder) {
        final List<Runnable> removers = Lists.array();

        for (final StorageMountPoint<C> mount : this.mountPoints) {
            removers.add(
                adder.apply(mount)
            );
        }

        return Watchers.runnableCollection(removers);
    }

    @Override
    public void stop() {
        this.mountPoints.forEach(
            (StorageMountPoint<?> p) -> p.storage.stop()
        );
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.mountPoints.toString();
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
        printer.indent();
        {
            for(final StorageMountPoint<C> mount : this.mountPoints) {
                mount.printTree(printer);
            }
        }
        printer.outdent();
    }
}
