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

import walkingkooka.Binary;
import walkingkooka.Cast;
import walkingkooka.convert.ConverterLike;
import walkingkooka.convert.ConverterLikeDelegator;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.header.MediaTypeDetector;

import java.util.List;
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
                                    final MediaTypeDetector mediaTypeDetector,
                                    final Storage<StorageContext> storage,
                                    final EnvironmentContext environmentContext) {
        return new BasicStorageContext(
            Objects.requireNonNull(converterLike, "converterLike"),
            Objects.requireNonNull(mediaTypeDetector, "mediaTypeDetector"),
            Objects.requireNonNull(storage, "storage"),
            Objects.requireNonNull(environmentContext, "environmentContext")
        );
    }

    private BasicStorageContext(final ConverterLike converterLike,
                                final MediaTypeDetector mediaTypeDetector,
                                final Storage<StorageContext> storage,
                                final EnvironmentContext environmentContext) {
        this.converterLike = converterLike;
        this.mediaTypeDetector = mediaTypeDetector;
        this.storage = storage;
        this.environmentContext = environmentContext;
    }

    @Override
    public MediaType detect(final String filename,
                            final Binary content) {
        return this.mediaTypeDetector.detect(
            filename,
            content
        );
    }

    private final MediaTypeDetector mediaTypeDetector;

    // StorageContext...................................................................................................

    @Override
    public boolean canReadStorage(final StoragePath path) {
        return this.storage.canRead(
            path,
            this
        );
    }

    @Override
    public Optional<StorageValue> loadStorage(final StoragePath path) {
        return this.storage.load(
            path,
            this
        );
    }

    @Override
    public StorageValue saveStorage(final StorageValue value) {
        return this.storage.save(
            value,
            this
        );
    }

    @Override
    public void deleteStorage(final StoragePath path) {
        this.storage.delete(
            path,
            this
        );
    }

    @Override
    public List<StorageValueInfo> listStorage(final StoragePath parent,
                                              final int offset,
                                              final int count) {
        return this.storage.list(
            parent,
            offset,
            count,
            this
        );
    }

    @Override
    public void mountStorage(final StorageMountPoint<?> mountPoint) {
        this.storage.mount(
            Cast.to(mountPoint),
            this
        );
    }

    @Override
    public void unmountStorage(final StoragePath path) {
        this.storage.unmount(
            Cast.to(path),
            this
        );
    }

    @Override
    public List<StorageMountPoint<?>> storageMountPoints() {
        return Cast.to(
            this.storage.mountPoints()
        );
    }

    // @VisibleForTesting
    final Storage<StorageContext> storage;

    // StorageEnvironmentContext........................................................................................

    @Override
    public Optional<StoragePath> currentWorkingDirectory() {
        return CURRENT_WORKING_DIRECTORY.getEnvironmentValue(this);
    }

    @Override
    public void setCurrentWorkingDirectory(final Optional<StoragePath> currentWorkingDirectory) {
        CURRENT_WORKING_DIRECTORY.setOrRemoveEnvironmentValue(
            currentWorkingDirectory,
            this
        );
    }

    @Override
    public Optional<StoragePath> homeDirectory() {
        return HOME_DIRECTORY.getEnvironmentValue(this);
    }

    @Override
    public void setHomeDirectory(final Optional<StoragePath> homeDirectory) {
        HOME_DIRECTORY.setOrRemoveEnvironmentValue(
            homeDirectory,
            this
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
        final StorageContext storageContext;

        if (this == environmentContext || this.environmentContext == environmentContext) {
            storageContext = this;
        } else {
            EnvironmentContext wrappedEnvironmentContext = environmentContext;

            if (environmentContext instanceof BasicStorageContext) {
                final BasicStorageContext basicStorageContext = (BasicStorageContext) environmentContext;

                wrappedEnvironmentContext = basicStorageContext.environmentContext;
            }

            Objects.requireNonNull(wrappedEnvironmentContext, "environmentContext");

            storageContext = new BasicStorageContext(
                this.converterLike,
                this.mediaTypeDetector,
                this.storage,
                wrappedEnvironmentContext
            );
        }

        return storageContext;
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
            this.mediaTypeDetector,
            this.storage,
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
            this.mediaTypeDetector.equals(other.mediaTypeDetector) &&
            this.storage.equals(other.storage) &&
            this.environmentContext.equals(other.environmentContext);
    }

    @Override
    public String toString() {
        return this.mediaTypeDetector + " " +
            this.storage + " " +
            this.environmentContext;
    }
}
