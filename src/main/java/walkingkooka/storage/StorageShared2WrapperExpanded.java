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

abstract class StorageShared2WrapperExpanded<C extends StorageContext> extends StorageShared2Wrapper<C> {

    StorageShared2WrapperExpanded(final Storage<C> storage) {
        super(storage);
    }

    @Override //
    final boolean canRead0(final StoragePath path,
                           final C context) {
        return this.storage.canRead(
            this.expand(
                path,
                context
            ).orElse(path),
            context
        );
    }

    @Override //
    final boolean canWrite0(final StoragePath path,
                            final C context) {
        return this.storage.canWrite(
            this.expand(
                path,
                context
            ).orElse(null),
            context
        );
    }

    @Override //
    final Optional<StorageValue> load0(final StoragePath path,
                                       final C context) {
        final StoragePath replaced = this.expand(
            path,
            context
        ).orElse(null);

        Optional<StorageValue> loaded = this.storage.load(
            null != replaced ?
                replaced :
                path,
            context
        );

        if (loaded.isPresent() && null != replaced && false == path.equals(replaced)) {
            loaded = loaded.map(
                (storageValue) -> {
                    final StoragePath p = storageValue.path();

                    return storageValue.setPath(
                        StorageShared2WrapperExpanded.this.unexpand(
                            p,
                            context
                        ).orElse(p)
                    );
                }
            );
        }

        return loaded;
    }

    @Override //
    final StorageValue save0(final StorageValue value,
                             final C context) {
        final StoragePath path = value.path();
        final StoragePath replaced = this.expand(
            path,
            context
        ).orElse(null);

        StorageValue saved;
        if (null == replaced || path.equals(replaced)) {
            saved = this.storage.save(
                value,
                context
            );
        } else {
            saved = this.storage.save(
                value.setPath(replaced),
                context
            );

            final StoragePath savedPath = this.unexpand(
                saved.path(),
                context
            ).orElse(null);

            if(null != savedPath) {
                saved = saved.setPath(savedPath);
            }
        }

        return saved;
    }

    @Override //
    final void delete0(final StoragePath path,
                       final C context) {
        this.storage.delete(
            this.expand(
                path,
                context
            ).orElse(path),
            context
        );
    }

    @Override //
    final List<StorageValueInfo> list0(final StoragePath parent,
                                       final int offset,
                                       final int count,
                                       final C context) {
        final StoragePath replaced = this.expand(
            parent,
            context
        ).orElse(null);

        List<StorageValueInfo> infos = this.storage.list(
            null != replaced ?
                replaced :
                parent,
            offset,
            count,
            context
        );

        if (null != replaced && parent.equals(replaced)) {
            infos = infos.stream()
                .map(
                    (storageValue) -> {
                        final StoragePath p = storageValue.path();
                        final StoragePath u = StorageShared2WrapperExpanded.this.unexpand(
                            p,
                            context
                        ).orElse(null);

                        return null != u ?
                            storageValue.setPath(u) :
                            storageValue;
                    }
                ).collect(Collectors.toList());
        }

        return infos;
    }

    @Override //
    final void setAuditInfo0(final StorageValueInfo value,
                             final C context) {
        final StoragePath path = value.path();

        this.storage.setAuditInfo(
            value.setPath(
                this.expand(
                    path,
                    context
                ).orElse(path)
            ),
            context
        );
    }

    abstract Optional<StoragePath> expand(final StoragePath path,
                                          final C context);

    abstract Optional<StoragePath> unexpand(final StoragePath path,
                                            final C context);

    // addWatcher.......................................................................................................

    @Override //
    final Runnable addWatcher0(final StorageWatcher watcher,
                               final C context) {
        return this.storage.addWatcher(
            watcher,
            context
        );
    }

    @Override //
    final Runnable addWatcherOnce0(final StorageWatcher watcher,
                                   final C context) {
        return this.storage.addWatcherOnce(
            watcher,
            context
        );
    }
}
