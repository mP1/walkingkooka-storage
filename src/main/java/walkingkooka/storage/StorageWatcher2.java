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

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link StorageWatcher} that routes each event to add/remove/update.
 */
public interface StorageWatcher2 extends StorageWatcher {

    @Override
    default void onStorageValueChange(final StoragePath path,
                                      final Optional<?> oldValue,
                                      final Optional<?> newValue) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(oldValue, "oldValue");
        Objects.requireNonNull(newValue, "newValue");

        final boolean oldEmpty = oldValue.isEmpty();
        final boolean newEmpty = newValue.isEmpty();

        if (oldEmpty) {
            this.onStorageValueAdd(
                path,
                newValue.get()
            );
        } else {
            if (newEmpty) {
                this.onStorageValueRemove(
                    path,
                    oldValue.get()
                );
            } else {
                this.onStorageValueUpdate(
                    path,
                    oldValue.get(),
                    newValue.get()
                );
            }
        }
    }

    void onStorageValueAdd(final StoragePath path,
                           final Object newValue);

    void onStorageValueRemove(final StoragePath path,
                              final Object oldValue);

    void onStorageValueUpdate(final StoragePath path,
                              final Object oldValue,
                              final Object newValue);
}
