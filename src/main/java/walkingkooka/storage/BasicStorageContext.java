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
    StorageEnvironmentContextDelegator {

    static BasicStorageContext with(final ConverterLike converterLike,
                                    final MediaTypeDetector mediaTypeDetector,
                                    final Storage<StorageContext> storage,
                                    final StorageEnvironmentContext storageEnvironmentContext) {
        return new BasicStorageContext(
            Objects.requireNonNull(converterLike, "converterLike"),
            Objects.requireNonNull(mediaTypeDetector, "mediaTypeDetector"),
            Objects.requireNonNull(storage, "storage"),
            Objects.requireNonNull(storageEnvironmentContext, "storageEnvironmentContext")
        );
    }

    private BasicStorageContext(final ConverterLike converterLike,
                                final MediaTypeDetector mediaTypeDetector,
                                final Storage<StorageContext> storage,
                                final StorageEnvironmentContext storageEnvironmentContext) {
        this.converterLike = converterLike;
        this.mediaTypeDetector = mediaTypeDetector;
        this.storage = storage;
        this.storageEnvironmentContext = storageEnvironmentContext;
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
    public boolean canWriteStorage(final StoragePath path) {
        return this.storage.canWrite(
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
    public void setAuditInfoStorage(final StorageValueInfo info) {
        this.storage.setAuditInfo(
            info,
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

    @Override
    public Runnable addStorageWatcher(final StorageWatcher watcher) {
        return this.storage.addWatcher(
            watcher,
            this
        );
    }

    @Override
    public Runnable addStorageWatcherOnce(final StorageWatcher watcher) {
        return this.storage.addWatcherOnce(
            watcher,
            this
        );
    }

    // @VisibleForTesting
    final Storage<StorageContext> storage;

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
            this.storageEnvironmentContext.cloneEnvironment()
        );
    }

    @Override
    public StorageContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        final StorageEnvironmentContext before = this.storageEnvironmentContext;
        final StorageEnvironmentContext after = before.setEnvironmentContext(environmentContext);

        return before == after ?
            this :
            new BasicStorageContext(
                this.converterLike,
                this.mediaTypeDetector,
                this.storage,
                after
            );
    }

    @Override
    public StorageEnvironmentContext storageEnvironmentContext() {
        return storageEnvironmentContext;
    }

    private final StorageEnvironmentContext storageEnvironmentContext;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.converterLike,
            this.mediaTypeDetector,
            this.storage,
            this.storageEnvironmentContext
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
            this.storageEnvironmentContext.equals(other.storageEnvironmentContext);
    }

    @Override
    public String toString() {
        return this.mediaTypeDetector + " " +
            this.storage + " " +
            this.storageEnvironmentContext;
    }
}
