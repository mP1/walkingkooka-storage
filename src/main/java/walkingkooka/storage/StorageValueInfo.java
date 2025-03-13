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

import walkingkooka.net.email.EmailAddress;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Holds the metadata for a single {@link StoragePath}.
 */
public final class StorageValueInfo {

    public static StorageValueInfo with(final StoragePath path,
                                        final EmailAddress createdBy,
                                        final LocalDateTime createdTimestamp,
                                        final EmailAddress modifiedBy,
                                        final LocalDateTime modifiedTimestamp) {
        return new StorageValueInfo(
            Objects.requireNonNull(path, "path"),
            Objects.requireNonNull(createdBy, "createdBy"),
            Objects.requireNonNull(createdTimestamp, "createdTimestamp"),
            Objects.requireNonNull(modifiedBy, "modifiedBy"),
            Objects.requireNonNull(modifiedTimestamp, "modifiedTimestamp")
        );
    }

    private StorageValueInfo(final StoragePath path,
                             final EmailAddress createdBy,
                             final LocalDateTime createdTimestamp,
                             final EmailAddress modifiedBy,
                             final LocalDateTime modifiedTimestamp) {
        this.path = path;
        this.createdBy = createdBy;
        this.createdTimestamp = createdTimestamp;
        this.modifiedBy = modifiedBy;
        this.modifiedTimestamp = modifiedTimestamp;

        if (modifiedTimestamp.isBefore(createdTimestamp)) {
            throw new IllegalArgumentException("ModifiedTimestamp " + modifiedTimestamp + " < createdTimestamp " + createdTimestamp);
        }
    }

    // path..............................................................................................................

    public StoragePath path() {
        return this.path;
    }

    public StorageValueInfo setPath(final StoragePath path) {
        return this.path.equals(path) ?
            this :
            new StorageValueInfo(
                Objects.requireNonNull(path, "path"),
                this.createdBy,
                this.createdTimestamp,
                this.modifiedBy,
                this.modifiedTimestamp
            );
    }

    private final StoragePath path;

    // createdBy........................................................................................................

    public EmailAddress createdBy() {
        return this.createdBy;
    }

    private final EmailAddress createdBy;

    public StorageValueInfo setCreatedBy(final EmailAddress createdBy) {
        return this.createdBy.equals(createdBy) ?
            this :
            new StorageValueInfo(
                this.path,
                Objects.requireNonNull(createdBy, "createdBy"),
                this.createdTimestamp,
                this.modifiedBy,
                this.modifiedTimestamp
            );
    }

    // createdTimestamp.................................................................................................

    public LocalDateTime createdTimestamp() {
        return this.createdTimestamp;
    }

    private final LocalDateTime createdTimestamp;

    public StorageValueInfo setCreatedTimestamp(final LocalDateTime createdTimestamp) {
        return this.createdTimestamp.equals(createdTimestamp) ?
            this :
            new StorageValueInfo(
                this.path,
                this.createdBy,
                Objects.requireNonNull(createdTimestamp, "createdTimestamp"),
                this.modifiedBy,
                this.modifiedTimestamp
            );
    }

    // modifiedBy.......................................................................................................

    public EmailAddress modifiedBy() {
        return this.modifiedBy;
    }

    private final EmailAddress modifiedBy;

    public StorageValueInfo setModifiedBy(final EmailAddress modifiedBy) {
        return this.modifiedBy.equals(modifiedBy) ?
            this :
            new StorageValueInfo(
                this.path,
                this.createdBy,
                this.createdTimestamp,
                Objects.requireNonNull(modifiedBy, "modifiedBy"),
                this.modifiedTimestamp
            );
    }

    public LocalDateTime modifiedTimestamp() {
        return this.modifiedTimestamp;
    }

    private final LocalDateTime modifiedTimestamp;

    public StorageValueInfo setModifiedTimestamp(final LocalDateTime modifiedTimestamp) {
        return this.modifiedTimestamp.equals(modifiedTimestamp) ?
            this :
            new StorageValueInfo(
                this.path,
                this.createdBy,
                createdTimestamp,
                this.modifiedBy,
                Objects.requireNonNull(modifiedTimestamp, "modifiedTimestamp")
            );
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.path,
            this.createdBy,
            this.createdTimestamp,
            this.modifiedBy,
            this.modifiedTimestamp
        );
    }

    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof StorageValueInfo && this.equals0((StorageValueInfo) other));
    }

    private boolean equals0(final StorageValueInfo other) {
        return
            this.path.equals(other.path) &&
                this.createdBy.equals(other.createdBy) &&
                this.createdTimestamp.equals(other.createdTimestamp) &&
                this.modifiedBy.equals(other.modifiedBy) &&
                this.modifiedTimestamp.equals(other.modifiedTimestamp);
    }

    @Override
    public String toString() {
        return this.path +
            " " +
            this.createdBy +
            " " +
            this.createdTimestamp +
            " " +
            this.modifiedBy +
            " " +
            this.modifiedTimestamp;
    }
}
