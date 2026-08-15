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
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.net.header.MediaTypeDetector;

import java.util.List;

public interface StorageContext extends StorageEnvironmentContext,
    ConverterLike,
    CanLoadStorage,
    MediaTypeDetector {

    /**
     * {@link Storage#canRead(StoragePath, StorageContext)}
     */
    boolean canReadStorage(final StoragePath path);

    /**
     * {@link Storage#canWrite(StoragePath, StorageContext)}
     */
    boolean canWriteStorage(final StoragePath path);

    /**
     * {@link Storage#save(StorageValue, StorageContext)}.
     */
    StorageValue saveStorage(final StorageValue value);

    /**
     * {@link Storage#delete(StoragePath, StorageContext)
     */
    void deleteStorage(final StoragePath path);

    /**
     * {@link Storage#list(StoragePath, int, int, StorageContext)}
     */
    List<StorageValueInfo> listStorage(final StoragePath parent,
                                       final int offset,
                                       final int count);

    /**
     * {@link Storage#setAuditInfo(StorageValueInfo, StorageContext)}
     */
    void setAuditInfoStorage(final StorageValueInfo info);

    /**
     * {@link Storage#mount(StorageMountPoint, StorageContext)}
     */
    void mountStorage(final StorageMountPoint<?> mountPoint);

    /**
     * {@link Storage#unmount(StoragePath, StorageContext)}
     */
    void unmountStorage(final StoragePath path);

    /**
     * {@link Storage#mountPoints()}
     */
    List<StorageMountPoint<?>> storageMountPoints();

    /**
     * {@link Storage#addWatcher(StorageWatcher, StorageContext)}
     */
    Runnable addStorageWatcher(final StorageWatcher watcher);

    /**
     * {@link Storage#addWatcherOnce(StorageWatcher, StorageContext)}
     */
    Runnable addStorageWatcherOnce(final StorageWatcher watcher);

    @Override
    StorageContext cloneEnvironment();

    @Override
    StorageContext setEnvironmentContext(final EnvironmentContext environmentContext);
}
