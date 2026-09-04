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

import walkingkooka.naming.HasPath;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link StorageWatcher} that filters events from values with a different {@link StoragePath}.
 */
public final class StoragePathStorageWatcher implements StorageWatcher,
    HasPath<StoragePath> {

    static StoragePathStorageWatcher with(final StorageWatcher watcher,
                                          final StoragePath path) {
        return new StoragePathStorageWatcher(
            Objects.requireNonNull(watcher, "watcher"),
            Objects.requireNonNull(path, "path")
        );
    }

    private StoragePathStorageWatcher(final StorageWatcher watcher,
                                      final StoragePath path) {
        this.watcher = watcher;
        this.path = path;
    }

    // StorageWatcher...................................................................................................

    @Override
    public void onValueChange(final Optional<StorageValue> oldValue,
                              final Optional<StorageValue> newValue) {
        if ((oldValue.isPresent() || newValue.isPresent()) && this.isPathMatch(oldValue) && this.isPathMatch(newValue)) {
            this.watcher.onValueChange(
                oldValue,
                newValue
            );
        }
    }

    // @VisibleForTesting
    final StorageWatcher watcher;

    private boolean isPathMatch(final Optional<StorageValue> storageValue) {
        return storageValue.map(
            (StorageValue s) -> s.path().equals(this.path)
        ).orElse(Boolean.TRUE);
    }

    // HasPath..........................................................................................................

    @Override
    public StoragePath path() {
        return this.path;
    }

    private final StoragePath path;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.path + " " + this.watcher;
    }
}
