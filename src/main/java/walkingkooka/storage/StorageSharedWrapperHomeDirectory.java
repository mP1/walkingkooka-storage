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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Wraps another {@link Storage} resolving any paths that begin with {@link StoragePath#HOME_DIRECTORY_PREFIX}
 * replacing that with the actual {@link StorageContext#homeDirectory()}.
 */
final class StorageSharedWrapperHomeDirectory<C extends StorageContext> extends StorageSharedWrapper<C> {

    static <C extends StorageContext> StorageSharedWrapperHomeDirectory<C> with(final Storage<C> storage) {
        return storage instanceof StorageSharedWrapperHomeDirectory ?
            (StorageSharedWrapperHomeDirectory) storage :
            new StorageSharedWrapperHomeDirectory(storage);
    }

    private StorageSharedWrapperHomeDirectory(final Storage<C> storage) {
        super(storage);
    }

    @Override
    boolean canRead0(final StoragePath path,
                     final C context) {
        return this.storage.canRead(
            path.replaceHomeDirectory(context),
            context
        );
    }

    @Override
    boolean canWrite0(final StoragePath path,
                      final C context) {
        return this.storage.canWrite(
            path.replaceHomeDirectory(context),
            context
        );
    }

    @Override
    Optional<StorageValue> load0(final StoragePath path,
                                 final C context) {
        final StoragePath replaced = path.replaceHomeDirectory(context);

        Optional<StorageValue> loaded = this.storage.load(
            replaced,
            context
        );

        if(loaded.isPresent() && false == path.equals(replaced)) {
            loaded = loaded.map(
                (storageValue) -> storageValue.setPath(
                    path.restoreHomeDirectory(context)
                )
            );
        }

        return loaded;
    }

    @Override
    StorageValue save0(final StorageValue value,
                       final C context) {
        final StoragePath path = value.path();
        final StoragePath replaced = path.replaceHomeDirectory(context);

        StorageValue saved;
        if(path.equals(replaced)) {
            saved = this.storage.save(
                value,
                context
            );
        } else {
            saved = this.storage.save(
                value.setPath(
                    path.replaceHomeDirectory(context)
                ),
                context
            );

            saved = saved.setPath(
              saved.path()
                  .restoreHomeDirectory(context)
            );
        }

        return saved;
    }

    @Override
    void delete0(final StoragePath path,
                 final C context) {
        this.storage.delete(
            path.replaceHomeDirectory(context),
            context
        );
    }

    @Override
    List<StorageValueInfo> list0(final StoragePath parent,
                                 final int offset,
                                 final int count,
                                 final C context) {
        final StoragePath replaced = parent.replaceHomeDirectory(context);

        List<StorageValueInfo> infos = this.storage.list(
            replaced,
            offset,
            count,
            context
        );

        if(parent.equals(replaced)) {
            infos = infos.stream()
                .map(
                    (storageValue) -> storageValue.setPath(
                        storageValue
                            .path()
                            .restoreHomeDirectory(context)
                    )
                ).collect(Collectors.toList());
        }

        return infos;
    }

    @Override
    void setAuditInfo0(final StorageValueInfo value,
                       final C context) {
        this.storage.setAuditInfo(
            value.setPath(
                value.path()
                    .replaceHomeDirectory(context)
            ),
            context
        );
    }

    // addWatcher.......................................................................................................

    @Override
    Runnable addWatcher0(final StorageWatcher watcher,
                         final C context) {
        return this.storage.addWatcher(
            watcher,
            context
        );
    }

    @Override
    Runnable addWatcherOnce0(final StorageWatcher watcher,
                             final C context) {
        return this.storage.addWatcherOnce(
            watcher,
            context
        );
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.storage.toString();
    }
}
