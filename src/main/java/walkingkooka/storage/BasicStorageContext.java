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

import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;

import java.util.Objects;
import java.util.Optional;

final class BasicStorageContext implements StorageContext, EnvironmentContextDelegator {

    static BasicStorageContext with(final EnvironmentContext environmentContext) {
        return new BasicStorageContext(
            Objects.requireNonNull(environmentContext, "environmentContext")
        );
    }

    private BasicStorageContext(final EnvironmentContext environmentContext) {
        this.environmentContext = environmentContext;
    }

    // StorageContext...................................................................................................

    @Override
    public StoragePath parseStoragePath(final String text) {
        return StoragePath.parse(text);
    }

    // StorageEnvironmentContext........................................................................................

    @Override
    public Optional<StoragePath> currentWorkingDirectory() {
        return this.environmentValue(CURRENT_WORKING_DIRECTORY);
    }

    // EnvironmentContext...............................................................................................

    @Override
    public StorageContext cloneEnvironment() {
        return this.setEnvironmentContext(
            this.environmentContext.cloneEnvironment()
        );
    }

    @Override
    public StorageContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        // only re-create if different instance
        return this.environmentContext == environmentContext ?
            this :
            with(environmentContext);
    }

    @Override
    public EnvironmentContext environmentContext() {
        return environmentContext;
    }

    private final EnvironmentContext environmentContext;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.environmentContext.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof BasicStorageContext &&
                this.equals0((BasicStorageContext) other));
    }

    private boolean equals0(final BasicStorageContext other) {
        return this.environmentContext.equals(other.environmentContext);
    }

    @Override
    public String toString() {
        return this.environmentContext.toString();
    }
}
