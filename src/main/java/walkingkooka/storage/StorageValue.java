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
import walkingkooka.ToStringBuilder;
import walkingkooka.Value;

import java.util.Objects;
import java.util.Optional;

/**
 * A value type that holds the storage value and some extra meta data.
 * Instances are not meant be marshalled to JSON or serializable.
 */
public final class StorageValue implements Value<Optional<Object>>,
    HasId<Optional<StorageKey>>,
    Comparable<StorageValue>{

    public static StorageValue with(final StorageKey key,
                                  final Optional<Object> value) {
        return new StorageValue(
            Objects.requireNonNull(key, "key"),
            Objects.requireNonNull(value, "value")
        );
    }

    private StorageValue(final StorageKey key,
                         final Optional<Object> value) {
        this.key = key;
        this.value = value;
    }

    // Value............................................................................................................

    @Override
    public Optional<Object> value() {
        return this.value;
    }

    private final Optional<Object> value;

    /**
     * Would be setter that returns a StorageValue with the given value creating a new instance if necessary.
     */
    public StorageValue setValue(final Optional<Object> value) {
        return this.value.equals(value) ?
            this :
            new StorageValue(
                this.key,
                Objects.requireNonNull(value, "value")
            );
    }

    // HasId............................................................................................................

    @Override
    public Optional<StorageKey> id() {
        return Optional.of(
            this.key()
        );
    }

    public StorageKey key() {
        return this.key;
    }

    public StorageValue setKey(final StorageKey key) {
        return this.key.equals(key) ?
            this :
            new StorageValue(
                Objects.requireNonNull(key, "key"),
                this.value
            );
    }

    private final StorageKey key;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.key,
            this.value
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof StorageValue &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final StorageValue other) {
        return this.key.equals(other.key) &&
            this.value.equals(other.value);
    }

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .label(this.key.toString())
            .separator("=")
            .value(this.value)
            .build();
    }

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final StorageValue other) {
        return this.key.compareTo(other.key());
    }
}
