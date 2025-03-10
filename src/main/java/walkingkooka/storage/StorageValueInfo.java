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
 * Holds the metadata for a single {@link StorageKey}.
 */
public final class StorageValueInfo {

    public static StorageValueInfo with(final StorageKey key,
                                        final EmailAddress createdBy,
                                        final LocalDateTime createTimestamp,
                                        final EmailAddress modifiedBy,
                                        final LocalDateTime modifyTimestamp) {
        return new StorageValueInfo(
            Objects.requireNonNull(key, "key"),
            Objects.requireNonNull(createdBy, "createdBy"),
            Objects.requireNonNull(createTimestamp, "createTimestamp"),
            Objects.requireNonNull(modifiedBy, "modifiedBy"),
            Objects.requireNonNull(modifyTimestamp, "modifyTimestamp")
        );
    }

    private StorageValueInfo(final StorageKey key,
                             final EmailAddress createdBy,
                             final LocalDateTime createTimestamp,
                             final EmailAddress modifiedBy,
                             final LocalDateTime modifyTimestamp) {
        this.key = key;
        this.createdBy = createdBy;
        this.createTimestamp = createTimestamp;
        this.modifiedBy = modifiedBy;
        this.modifyTimestamp = modifyTimestamp;

        if (modifyTimestamp.isBefore(createTimestamp)) {
            throw new IllegalArgumentException("ModifyTimestamp " + modifyTimestamp + " < createTimestamp " + createTimestamp);
        }
    }

    // key..............................................................................................................

    public StorageKey key() {
        return this.key;
    }

    public StorageValueInfo setKey(final StorageKey key) {
        return this.key.equals(key) ?
            this :
            new StorageValueInfo(
                Objects.requireNonNull(key, "key"),
                this.createdBy,
                this.createTimestamp,
                this.modifiedBy,
                this.modifyTimestamp
            );
    }

    private final StorageKey key;

    // createdBy........................................................................................................

    public EmailAddress createdBy() {
        return this.createdBy;
    }

    private final EmailAddress createdBy;

    public StorageValueInfo setCreatedBy(final EmailAddress createdBy) {
        return this.createdBy.equals(createdBy) ?
            this :
            new StorageValueInfo(
                this.key,
                Objects.requireNonNull(createdBy, "createdBy"),
                this.createTimestamp,
                this.modifiedBy,
                this.modifyTimestamp
            );
    }

    // createTimestamp..................................................................................................

    public LocalDateTime createTimestamp() {
        return this.createTimestamp;
    }

    private final LocalDateTime createTimestamp;

    public StorageValueInfo setCreateTimestamp(final LocalDateTime createTimestamp) {
        return this.createTimestamp.equals(createTimestamp) ?
            this :
            new StorageValueInfo(
                this.key,
                this.createdBy,
                Objects.requireNonNull(createTimestamp, "createTimestamp"),
                this.modifiedBy,
                this.modifyTimestamp
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
                this.key,
                this.createdBy,
                this.createTimestamp,
                Objects.requireNonNull(modifiedBy, "modifiedBy"),
                this.modifyTimestamp
            );
    }

    public LocalDateTime modifyTimestamp() {
        return this.modifyTimestamp;
    }

    private final LocalDateTime modifyTimestamp;

    public StorageValueInfo setModifyTimestamp(final LocalDateTime modifyTimestamp) {
        return this.modifyTimestamp.equals(modifyTimestamp) ?
            this :
            new StorageValueInfo(
                this.key,
                this.createdBy,
                createTimestamp,
                this.modifiedBy,
                Objects.requireNonNull(modifyTimestamp, "modifyTimestamp")
            );
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.key,
            this.createdBy,
            this.createTimestamp,
            this.modifiedBy,
            this.createTimestamp
        );
    }

    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof StorageValueInfo && this.equals0((StorageValueInfo) other));
    }

    private boolean equals0(final StorageValueInfo other) {
        return
            this.key.equals(other.key) &&
                this.createdBy.equals(other.createdBy) &&
                this.createTimestamp.equals(other.createTimestamp) &&
                this.modifiedBy.equals(other.modifiedBy) &&
                this.modifyTimestamp.equals(other.modifyTimestamp);
    }

    @Override
    public String toString() {
        return this.key +
            " " +
            this.createdBy +
            " " +
            this.createTimestamp +
            " " +
            this.modifiedBy +
            " " +
            this.modifyTimestamp;
    }
}
