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

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A {@link Storage} that returns a {@link StoragePathList}
 */
final class StorageMountPointPaths<C extends StorageContext> implements StorageDelegator<C> {

    /**
     * Type safe getter
     */
    static <C extends StorageContext> StorageMountPointPaths<C> instance() {
        return INSTANCE;
    }

    /**
     * Singleton instance
     */
    private final static StorageMountPointPaths INSTANCE = new StorageMountPointPaths<>();


    private StorageMountPointPaths() {
        super();

        this.storage = Storages.value(
            (C context) -> StorageValue.with(StoragePath.ROOT)
                .setValue(
                    Optional.of(
                        StoragePathList.EMPTY.setElements(
                            context.storageMountPoints()
                                .stream()
                                .map(StorageMountPoint::path)
                                .collect(Collectors.toList())
                        )
                    )
                )
        );
    }

    // StorageDelegator.................................................................................................

    @Override
    public Storage<C> storage() {
        return this.storage;
    }

    private final Storage<C> storage;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return StoragePathList.class.getSimpleName();
    }
}
