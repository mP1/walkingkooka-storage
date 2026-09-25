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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Wraps a {@link StorageContext}, logging all input and outputs for only storage methods at {@link walkingkooka.logging.LoggingLevel#INFO}.
 */
final class StorageContextLogging implements StorageContextDelegator {

    static StorageContextLogging with(final StorageContext storageContext) {
        final StorageContextLogging storageContextLogging;

        if (storageContext instanceof StorageContextLogging) {
            storageContextLogging = (StorageContextLogging) storageContext;
        } else {
            storageContextLogging = new StorageContextLogging(
                Objects.requireNonNull(storageContext, "storageContext")
            );
        }

        return storageContextLogging;
    }

    private StorageContextLogging(final StorageContext storageContext) {
        super();
        this.storageContext = storageContext;
    }
    // StorageContext...................................................................................................

    @Override
    public boolean canReadStorage(final StoragePath path) {
        return this.logEnterAndExitStorage(
            () -> {
                if (this.isInfoEnabled()) {
                    this.info("canRead " + path);
                }
                final boolean canReadStorage = this.storageContext.canReadStorage(path);
                if (this.isInfoEnabled()) {
                    this.info("canRead " + path + "=" + canReadStorage);
                }
                return canReadStorage;
            }
        );
    }

    @Override
    public boolean canWriteStorage(final StoragePath path) {
        return this.logEnterAndExitStorage(
            () -> {
                if (this.isInfoEnabled()) {
                    this.info("canWrite " + path);
                }
                final boolean canWriteStorage = this.storageContext.canWriteStorage(path);
                if (this.isInfoEnabled()) {
                    this.info("canWrite " + path + "=" + canWriteStorage);
                }
                return canWriteStorage;
            }
        );
    }

    @Override
    public Optional<StorageValue> loadStorage(final StoragePath path) {
        return this.logEnterAndExitStorage(
            () -> {
                if (this.isInfoEnabled()) {
                    this.info("load " + path);
                }
                Optional<StorageValue> storageValue = this.storageContext.loadStorage(path);
                if (this.isInfoEnabled()) {
                    this.info("load " + path + "=" + storageValue);
                }
                return storageValue;
            }
        );
    }

    @Override
    public StorageValue saveStorage(final StorageValue value) {
        return this.logEnterAndExitStorage(
            () -> {
                if (this.isInfoEnabled()) {
                    this.info("save " + value);
                }
                return this.storageContext.saveStorage(value);
            }
        );
    }

    @Override
    public void deleteStorage(final StoragePath path) {
        this.logEnterAndExitStorage(
            () -> {
                if (this.isInfoEnabled()) {
                    this.info("delete " + path);
                }
                this.storageContext.deleteStorage(path);
                return null;
            }
        );
    }

    @Override
    public List<StorageValueInfo> listStorage(final StoragePath parent,
                                              final int offset,
                                              final int count) {
        return this.logEnterAndExitStorage(
            () -> {
                if (this.isInfoEnabled()) {
                    this.info("list " + parent + " " + offset + " " + count);
                }
                final List<StorageValueInfo> storageValueInfos = this.storageContext.listStorage(
                    parent,
                    offset,
                    count
                );
                if (this.isInfoEnabled()) {
                    this.info("list " + parent + " " + offset + " " + count + " " + storageValueInfos);
                }
                return storageValueInfos;
            }
        );
    }

    @Override
    public void setAuditInfoStorage(final StorageValueInfo info) {
        this.logEnterAndExitStorage(
            () -> {
                if (this.isInfoEnabled()) {
                    this.info("setAuditInfo " + info);
                }
                this.storageContext.setAuditInfoStorage(info);
                return null;
            }
        );
        this.storageContext.setAuditInfoStorage(info);
    }

    @Override
    public void mountStorage(final StorageMountPoint<?> mountPoint) {
        this.logEnterAndExitStorage(
            () -> {
                if (this.isInfoEnabled()) {
                    this.info("mount " + mountPoint);
                }
                this.storageContext.mountStorage(mountPoint);
                return null;
            }
        );
    }

    @Override
    public void unmountStorage(final StoragePath path) {
        this.logEnterAndExitStorage(
            () -> {
                if (this.isInfoEnabled()) {
                    this.info("unmount " + path);
                }
                this.storageContext.unmountStorage(path);
                return null;
            }
        );
    }

    @Override
    public List<StorageMountPoint<?>> storageMountPoints() {
        return this.logEnterAndExitStorage(
            () -> {
                final List<StorageMountPoint<?>> storageMountPoints = this.storageContext.storageMountPoints();
                if (this.isInfoEnabled()) {
                    this.info("mountPoints " + storageMountPoints);
                }
                return storageMountPoints;
            }
        );
    }

    @Override
    public Runnable addStorageWatcher(final StorageWatcher watcher) {
        return this.logEnterAndExitStorage(
            () -> {
                if (this.isInfoEnabled()) {
                    this.info("addWatcher " + watcher);
                }
                return this.storageContext.addStorageWatcher(watcher);
            }
        );
    }

    @Override
    public Runnable addStorageWatcherOnce(final StorageWatcher watcher) {
        return this.logEnterAndExitStorage(
            () -> {
                if (this.isInfoEnabled()) {
                    this.info("addWatcherOnce " + watcher);
                }
                return this.storageContext.addStorageWatcherOnce(watcher);
            }
        );
    }

    // EnvironmentContext...............................................................................................

    @Override
    public StorageContext cloneEnvironment() {
        final StorageContext before = this.storageContext;
        final StorageContext after = before.cloneEnvironment();

        return before == after ?
            this :
            with(after);
    }

    @Override
    public StorageContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        StorageContext storageContext;

        if (environmentContext instanceof StorageContext) {
            storageContext = with(
                (StorageContext) environmentContext
            );
        } else {
            if (environmentContext instanceof StorageContext) {
                storageContext = (StorageContext) environmentContext;
            } else {
                final StorageContext before = this.storageContext;
                final StorageContext after = before.setEnvironmentContext(environmentContext);

                storageContext = before == after ?
                    this :
                    with(after);
            }
        }

        return storageContext;
    }

    // StorageContextDelegator..........................................................................................

    @Override
    public StorageContext storageContext() {
        return this.storageContext;
    }

    private final StorageContext storageContext;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.storageContext.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof StorageContextLogging &&
                this.equals0((StorageContextLogging) other));
    }

    private boolean equals0(final StorageContextLogging other) {
        return this.storageContext.equals(other.storageContext);
    }

    @Override
    public String toString() {
        return this.storageContext.toString();
    }
}
