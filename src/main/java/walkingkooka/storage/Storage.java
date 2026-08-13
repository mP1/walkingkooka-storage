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

import walkingkooka.Stoppable;
import walkingkooka.environment.AuditInfo;

import java.util.List;
import java.util.Optional;

/**
 * A {@link Storage} that supports storing values including support for tree or directory structure.
 */
public interface Storage<C extends StorageContext> extends Stoppable {

    Optional<StorageValue> NO_STORAGE_VALUE = Optional.empty();

    boolean canRead(final StoragePath path,
                    final C context);

    boolean canWrite(final StoragePath path,
                     final C context);

    Optional<StorageValue> load(final StoragePath path,
                                final C context);

    StorageValue save(final StorageValue value,
                      final C context);

    void delete(final StoragePath path,
                final C context);

    /**
     * Gets the {@link StorageValueInfo} for the given range for a parent {@link StoragePath#isParent()} or a single
     * value. Conceptually equivalent to getting a directory listing for the former.
     */
    List<StorageValueInfo> list(final StoragePath parent,
                                final int offset,
                                final int count,
                                final C context);

    /**
     * Supports replacing the {@link AuditInfo} for the given {@link StoragePath}, throwing an exception for
     * an unknown path.
     */
    void setAuditInfo(final StorageValueInfo value,
                      final C context);

    /**
     * Adds the given {@link Storage} at the given {@link StoragePath} assuming the path is available.
     */
    void mount(final StorageMountPoint<C> mountPoint,
               final C context);

    /**
     * Unmounts a previous mount.
     */
    void unmount(final StoragePath path,
                 final C context);

    /**
     * Returns a list of all {@link StorageMountPoint}.
     */
    List<StorageMountPoint<C>> mountPoints();

    Runnable addWatcher(final StorageWatcher watcher,
                        final C context);

    Runnable addWatcherOnce(final StorageWatcher watcher,
                            final C context);
    /**
     * Returns a {@link Storage} with an additional prefix to all its {@link StoragePath}.
     */
    default Storage<C> setPrefix(final StoragePath prefix) {
        return StorageShared2WrapperPrefixed.with(
            prefix,
            this
        );
    }
}
