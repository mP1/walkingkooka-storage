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


import walkingkooka.watch.ValueChangeWatcher;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * A {@link ValueChangeWatcher} that receives all {@link Storage} value change events.
 */
public interface StorageWatcher extends ValueChangeWatcher<StorageValue> {

    /**
     * Returns a {@link StorageWatcher} with the given prefix {@link StoragePath}.
     */
    default StorageWatcher setPathPrefix(final StoragePath path) {
        Objects.requireNonNull(path, "path");

        return path.isRoot() ?
            this :
            new StorageWatcher() {
                @Override
                public void onValueChange(final Optional<StorageValue> oldValue,
                                          final Optional<StorageValue> newValue) {
                    StorageWatcher.this.onValueChange(
                        storageValueSetPathPrefix(
                            oldValue,
                            path
                        ),
                        storageValueSetPathPrefix(
                            newValue,
                            path
                        )
                    );
                }

                private Optional<StorageValue> storageValueSetPathPrefix(final Optional<StorageValue> storageValue,
                                                                         final StoragePath path) {
                    return storageValue.map(
                        (StorageValue sv) -> sv.setPath(
                            sv.path()
                                .prepend(path)
                        )
                    );
                }

                // Object...............................................................................................

                @Override
                public String toString() {
                    return path + " " + StorageWatcher.this;
                }
            };
    }

    /**
     * Wraps this {@link StorageWatcher} with a {@link Predicate} filtering all events that are not matched.
     */
    default StorageWatcher setFilter(final Predicate<StoragePath> filter) {
        Objects.requireNonNull(filter, "filter");

        return new StorageWatcher() {
            @Override
            public void onValueChange(final Optional<StorageValue> oldValue,
                                      final Optional<StorageValue> newValue) {
                if (this.test(oldValue) || this.test(newValue)) {
                    StorageWatcher.this.onValueChange(
                        oldValue,
                        newValue
                    );
                }
            }

            private boolean test(final Optional<StorageValue> value) {
                return value.map((StorageValue sv) -> filter.test(sv.path())).orElse(false);
            }

            // Object...................................................................................................

            @Override
            public String toString() {
                return "if " + filter + " " + StorageWatcher.this;
            }
        };
    }
}
