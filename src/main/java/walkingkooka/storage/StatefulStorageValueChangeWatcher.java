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
import walkingkooka.HasValue;
import walkingkooka.Stoppable;
import walkingkooka.naming.HasPath;
import walkingkooka.watch.ValueChangeWatcher;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link StorageWatcher} that remembers the last value for a given {@link StoragePath}.
 */
public final class StatefulStorageValueChangeWatcher<T> implements HasPath<StoragePath>,
    HasValue<Optional<T>>,
    Stoppable {

    public static <T> StatefulStorageValueChangeWatcher<T> with(final StoragePath path,
                                                                final ValueChangeWatcher<T> watcher,
                                                                final StorageContext context) {
        return new StatefulStorageValueChangeWatcher(
            Objects.requireNonNull(path, "path"),
            Objects.requireNonNull(watcher, "watcher"),
            Objects.requireNonNull(context, "context")
        );
    }

    private StatefulStorageValueChangeWatcher(final StoragePath path,
                                              final ValueChangeWatcher<T> watcher,
                                              final StorageContext context) {
        super();

        this.path = path;
        this.value = Optional.empty();

        this.valueChangeWatcher = watcher;

        this.canLoadStorage = context;

        this.remover = context.addStorageWatcher(
            new StorageWatcher() {
                @Override
                public void onValueChange(final Optional<StorageValue> oldStorageValue,
                                          final Optional<StorageValue> newStorageValue) {
                    final Optional<T> newValue = Cast.to(
                        newStorageValue.flatMap(StorageValue::value)
                    );

                    StatefulStorageValueChangeWatcher.this.value = newValue;

                    StatefulStorageValueChangeWatcher.this.valueChangeWatcher.onValueChange(
                        Cast.to(
                            oldStorageValue.flatMap(StorageValue::value)
                        ),
                        Cast.to(newValue)
                    );
                }

                // Object...............................................................................................

                @Override
                public String toString() {
                    return path.toString();
                }
            }.setPath(path)
        );

        this.load();
    }

    private final ValueChangeWatcher<T> valueChangeWatcher;

    /**
     * Load the {@link StorageValue} for the {@link StoragePath}.
     */
    public Optional<T> load() {
        final Optional<T> value = Cast.to(
            this.canLoadStorage.loadStorage(this.path)
            .flatMap(StorageValue::value)
        );

        this.value = value;

        this.valueChangeWatcher.onValueChange(
            value,
            value
        );

        return value;
    }

    private final CanLoadStorage canLoadStorage;

    // HasPath..........................................................................................................

    @Override
    public StoragePath path() {
        return this.path;
    }

    private final StoragePath path;

    // HasValue.........................................................................................................

    @Override
    public Optional<T> value() {
        return this.value;
    }

    // @VisibleForTesting
    Optional<T> value;

    // Stoppable........................................................................................................

    /**
     * Removes the registered {@link StorageWatcher}.
     */
    @Override
    public void stop() {
        this.remover.run();
    }

    private final Runnable remover;

    // toString.........................................................................................................

    @Override
    public String toString() {
        return this.path + " " + this.valueChangeWatcher;
    }
}
