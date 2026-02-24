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

import walkingkooka.convert.ConverterLike;
import walkingkooka.convert.ConverterLikeDelegator;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;

import java.util.Objects;
import java.util.Optional;

/**
 * The provider of the {@link ConverterLike} should watch and recreate or modify itself when the {@link EnvironmentContext}
 * changes as necessary.
 */
final class BasicStorageContext implements StorageContext,
    ConverterLikeDelegator,
    EnvironmentContextDelegator {

    static BasicStorageContext with(final ConverterLike converterLike,
                                    final EnvironmentContext environmentContext) {
        return new BasicStorageContext(
            Objects.requireNonNull(converterLike, "converterLike"),
            Objects.requireNonNull(environmentContext, "environmentContext")
        );
    }

    private BasicStorageContext(final ConverterLike converterLike,
                                final EnvironmentContext environmentContext) {
        this.converterLike = converterLike;
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

    @Override
    public void setCurrentWorkingDirectory(final Optional<StoragePath> currentWorkingDirectory) {
        this.setOrRemoveEnvironmentValue(
            CURRENT_WORKING_DIRECTORY,
            currentWorkingDirectory
        );
    }

    @Override
    public Optional<StoragePath> homeDirectory() {
        return this.environmentValue(CURRENT_WORKING_DIRECTORY);
    }

    @Override
    public void setHomeDirectory(final Optional<StoragePath> homeDirectory) {
        this.setOrRemoveEnvironmentValue(
            CURRENT_WORKING_DIRECTORY,
            homeDirectory
        );
    }

    // StorageContext...................................................................................................

    @Override
    public ConverterLike converterLike() {
        return this.converterLike;
    }

    private final ConverterLike converterLike;

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
            with(
                this.converterLike,
                environmentContext
            );
    }

    @Override
    public EnvironmentContext environmentContext() {
        return environmentContext;
    }

    private final EnvironmentContext environmentContext;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.converterLike,
            this.environmentContext
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof BasicStorageContext &&
                this.equals0((BasicStorageContext) other));
    }

    private boolean equals0(final BasicStorageContext other) {
        return this.converterLike.equals(other.converterLike) &&
            this.environmentContext.equals(other.environmentContext);
    }

    @Override
    public String toString() {
        return this.environmentContext.toString();
    }
}
