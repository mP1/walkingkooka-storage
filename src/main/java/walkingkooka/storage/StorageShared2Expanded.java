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

abstract class StorageShared2Expanded<C extends StorageContext> extends StorageShared2<C> {

    StorageShared2Expanded() {
        super();
    }

    @Override //
    final boolean canRead0(final StoragePath path,
                           final C context) {
        return context.canReadStorage(
            this.expand(
                path,
                context
            ).orElse(path)
        );
    }

    @Override //
    final boolean canWrite0(final StoragePath path,
                            final C context) {
        return context.canWriteStorage(
            this.expand(
                path,
                context
            ).orElse(null)
        );
    }

    @Override //
    final Optional<StorageValue> load0(final StoragePath path,
                                       final C context) {
        final StoragePath replaced = this.expand(
            path,
            context
        ).orElse(null);

        Optional<StorageValue> loaded = context.loadStorage(
            null != replaced ?
                replaced :
                path
        );

        if (loaded.isPresent() && null != replaced && false == path.equals(replaced)) {
            loaded = loaded.map(
                (storageValue) -> {
                    final StoragePath p = storageValue.path();

                    return storageValue.setPath(
                        StorageShared2Expanded.this.unexpand(
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
            saved = context.saveStorage(value);
        } else {
            saved = context.saveStorage(
                value.setPath(replaced)
            );

            final StoragePath savedPath = this.unexpand(
                saved.path(),
                context
            ).orElse(null);

            if (null != savedPath) {
                saved = saved.setPath(savedPath);
            }
        }

        return saved;
    }

    @Override //
    final void delete0(final StoragePath path,
                       final C context) {
        context.deleteStorage(
            this.expand(
                path,
                context
            ).orElse(path)
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

        List<StorageValueInfo> infos = context.listStorage(
            null != replaced ?
                replaced :
                parent,
            offset,
            count
        );

        if (null != replaced) {
            infos = infos.stream()
                .map(
                    (storageValue) -> {
                        final StoragePath p = storageValue.path();
                        final StoragePath u = StorageShared2Expanded.this.unexpand(
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

        context.setAuditInfoStorage(
            value.setPath(
                this.expand(
                    path,
                    context
                ).orElse(path)
            )
        );
    }

    abstract Optional<StoragePath> expand(final StoragePath path,
                                          final C context);

    final Optional<StoragePath> replacePrefixWithEnvironment(final StoragePath path,
                                                             final Optional<StoragePath> environment) {
        return environment.flatMap(
            (StoragePath p) -> path.replacePrefix(
                StoragePath.ROOT,
                p
            )
        );
    }

    abstract Optional<StoragePath> unexpand(final StoragePath path,
                                            final C context);

    final Optional<StoragePath> replaceEnvironment(final StoragePath path,
                                                   final Optional<StoragePath> environment) {
        return environment.flatMap(
            (StoragePath p) -> path.replacePrefix(
                p,
                StoragePath.ROOT
            )
        );
    }

    // addWatcher.......................................................................................................

    @Override //
    final Runnable addWatcher0(final StorageWatcher watcher,
                               final C context) {
        return () -> {};
    }

    @Override //
    final Runnable addWatcherOnce0(final StorageWatcher watcher,
                                   final C context) {
        return () -> {};
    }

    // stop.............................................................................................................

    @Override
    public final void stop() {
        // NOP
    }
}
