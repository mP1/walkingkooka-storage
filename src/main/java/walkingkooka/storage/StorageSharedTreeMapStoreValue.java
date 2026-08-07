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
import walkingkooka.HasId;

import java.util.Objects;
import java.util.Optional;

/**
 * A value type that combines the audit info and stored value into a single value which will be saved in the wrapped
 * {@link walkingkooka.store.Store}.
 */
final class StorageSharedTreeMapStoreValue implements HasId<Optional<StoragePath>> {

    final static boolean NOT_PARENT = false;

    final static boolean PARENT = true;

    static StorageSharedTreeMapStoreValue with(final boolean parent,
                                               final StorageValueInfo info,
                                               final StorageValue value) {
        return new StorageSharedTreeMapStoreValue(
            parent,
            Objects.requireNonNull(info, "info"),
            Objects.requireNonNull(value, "value")
        );
    }

    private StorageSharedTreeMapStoreValue(final boolean parent,
                                           final StorageValueInfo info,
                                           final StorageValue value) {
        super();

        this.parent = parent;
        this.info = info;
        this.value = value;
    }

    StorageSharedTreeMapStoreValue setPath(final StoragePath path) {
        final StorageValueInfo info = this.info.setPath(path);
        final StorageValue value = this.value.setPath(path);

        return this.info.equals(info) && this.value.equals(value) ?
            this :
            StorageSharedTreeMapStoreValue.with(
                this.parent,
                info,
                value
            );
    }

    // HasId............................................................................................................

    @Override
    public Optional<StoragePath> id() {
        return Optional.of(
            this.path()
        );
    }

    StoragePath path() {
        return this.value.path();
    }

    /**
     * Keeps track whether this entry is a parent of other entries.
     */
    boolean parent;

    // info.............................................................................................................

    StorageValueInfo info() {
        return this.info;
    }

    final StorageValueInfo info;

    StorageSharedTreeMapStoreValue setInfo(final StorageValueInfo info) {
        return this.info.equals(info) ?
            this :
            StorageSharedTreeMapStoreValue.with(
                this.parent,
                info,
                this.value
            );
    }

    // Value............................................................................................................

    StorageValue value() {
        return this.value;
    }

    final StorageValue value;

    StorageSharedTreeMapStoreValue setValue(final StorageValue value) {
        return this.value.equals(value) ?
            this :
            StorageSharedTreeMapStoreValue.with(
                this.parent,
                this.info,
                value
            );
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.parent,
            this.info,
            this.value
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof StorageSharedTreeMapStoreValue &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final StorageSharedTreeMapStoreValue other) {
        return this.parent == other.parent &&
            this.info.equals(other.info) &&
            this.value.equals(other.value);
    }

    @Override
    public String toString() {
        return this.info + " " + this.value.toString();
    }
}
