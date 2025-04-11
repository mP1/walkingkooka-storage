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

import walkingkooka.environment.AuditInfo;

import java.util.Objects;

/**
 * Holds the metadata for a single {@link StoragePath}.
 */
public final class StorageValueInfo {

    public static StorageValueInfo with(final StoragePath path,
                                        final AuditInfo auditInfo) {
        return new StorageValueInfo(
            Objects.requireNonNull(path, "path"),
            Objects.requireNonNull(auditInfo, "auditInfo")
        );
    }

    private StorageValueInfo(final StoragePath path,
                             final AuditInfo auditInfo) {
        this.path = path;
        this.auditInfo = auditInfo;
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
                this.auditInfo
            );
    }

    private final StoragePath path;

    // auditInfo........................................................................................................

    public AuditInfo auditInfo() {
        return this.auditInfo;
    }

    private final AuditInfo auditInfo;

    public StorageValueInfo setAuditInfo(final AuditInfo auditInfo) {
        return this.auditInfo.equals(auditInfo) ?
            this :
            new StorageValueInfo(
                this.path,
                Objects.requireNonNull(auditInfo, "auditInfo")
            );
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.path,
            this.auditInfo
        );
    }

    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof StorageValueInfo && this.equals0((StorageValueInfo) other));
    }

    private boolean equals0(final StorageValueInfo other) {
        return
            this.path.equals(other.path) &&
                this.auditInfo.equals(other.auditInfo);
    }

    @Override
    public String toString() {
        return this.path +
            " " +
            this.auditInfo;
    }
}
