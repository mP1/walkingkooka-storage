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

/**
 * A read only {@link Storage}, with save and delete throwing {@link InvalidStoragePathException}.
 */
final class ReadOnlyStorage<C extends StorageContext> implements StorageDelegator<C> {

    static <C extends StorageContext> ReadOnlyStorage<C> with(final Storage<C> storage) {
        Objects.requireNonNull(storage, "storage");

        ReadOnlyStorage<C> readOnlyStorage;

        if (storage instanceof ReadOnlyStorage) {
            readOnlyStorage = (ReadOnlyStorage<C>) storage;
        } else {
            readOnlyStorage = new ReadOnlyStorage<>(storage);
        }

        return readOnlyStorage;
    }

    private ReadOnlyStorage(final Storage<C> storage) {
        super();
        this.storage = storage;
    }

    @Override
    public boolean canRead(final StoragePath path,
                           final C context) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(context, "context");
        return false;
    }

    @Override
    public boolean canWrite(final StoragePath path,
                            final C context) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(context, "context");
        return false;
    }

    @Override
    public StorageValue save(final StorageValue value,
                             final C context) {
        Objects.requireNonNull(value, "path");
        Objects.requireNonNull(context, "context");

        throw value.path()
            .invalidStoragePathException("Read only");
    }

    @Override
    public void delete(final StoragePath path,
                       final C context) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(context, "context");

        throw path.invalidStoragePathException("Read only");
    }

    // StorageDelegator.................................................................................................

    @Override
    public Storage<C> storage() {
        return this.storage;
    }

    // @VisibleForTesting
    final Storage<C> storage;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "ReadOnly " + this.storage;
    }
}
