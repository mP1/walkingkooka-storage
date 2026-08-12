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

import walkingkooka.collect.list.Lists;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A {@link Storage} that maps the given {@link Supplier} to {@link StoragePath#ROOT}.
 */
final class StorageShared2Value<C extends StorageContext> extends StorageShared2<C> {

    static <C extends StorageContext> StorageShared2Value<C> with(final Function<C, StorageValue> value) {
        return new StorageShared2Value(
            Objects.requireNonNull(value, "value")
        );
    }

    private StorageShared2Value(final Function<C, StorageValue> value) {
        super();
        this.value = value;
    }

    // Storage..........................................................................................................

    @Override
    boolean canRead0(final StoragePath storagePath,
                     final C context) {
        return isPath(storagePath);
    }

    @Override
    boolean canWrite0(final StoragePath storagePath,
                      final C context) {
        return false;
    }

    @Override
    Optional<StorageValue> load0(final StoragePath path,
                                 final C context) {
        return Optional.ofNullable(
            isPath(path) ?
                this.value.apply(context) :
                null
        );
    }

    private final Function<C, StorageValue> value;

    @Override
    StorageValue save0(final StorageValue value,
                       final C context) {
        throw new UnsupportedOperationException();
    }

    @Override
    void delete0(final StoragePath path,
                 final C context) {
        throw new UnsupportedOperationException();
    }

    @Override
    List<StorageValueInfo> list0(final StoragePath parent,
                                 final int offset,
                                 final int count,
                                 final C context) {
        return Lists.of(
            StorageValueInfo.with(
                StoragePath.ROOT,
                context.createdAuditInfo()
            )
        );
    }

    @Override
    void setAuditInfo0(final StorageValueInfo value,
                       final C context) {
        throw new UnsupportedOperationException();
    }

    private static boolean isPath(final StoragePath path) {
        return StoragePath.ROOT.equals(path);
    }

    // addWatcher.......................................................................................................

    @Override
    Runnable addWatcher0(final StorageWatcher watcher,
                         final C context) {
        return () -> {};
    }

    @Override
    Runnable addWatcherOnce0(final StorageWatcher watcher,
                             final C context) {
        return () -> {};
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.value.toString();
    }
}
