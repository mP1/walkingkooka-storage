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

import walkingkooka.collect.list.Lists;
import walkingkooka.convert.ConverterLikeTesting;
import walkingkooka.net.header.MediaTypeDetectorTesting;

import java.util.List;
import java.util.Optional;

public interface StorageContextTesting extends StorageEnvironmentContextTesting,
    CanParseStoragePathTesting,
    ConverterLikeTesting,
    MediaTypeDetectorTesting {

    Storage<StorageContext> STORAGE = Storages.empty();

    StorageContext STORAGE_CONTEXT = StorageContexts.basic(
        CONVERTER_LIKE,
        MEDIA_TYPE_DETECTOR,
        STORAGE,
        STORAGE_ENVIRONMENT_CONTEXT
    );

    StorageContext DIFFERENT_STORAGE_CONTEXT = StorageContexts.basic(
        CONVERTER_LIKE,
        MEDIA_TYPE_DETECTOR,
        STORAGE,
        DIFFERENT_STORAGE_ENVIRONMENT_CONTEXT
    );

    // loadStorage......................................................................................................

    default void loadStorageAndCheck(final StorageContext context,
                                     final StoragePath path) {
        this.loadStorageAndCheck(
            context,
            path,
            Optional.empty()
        );
    }

    default void loadStorageAndCheck(final StorageContext context,
                                     final StoragePath path,
                                     final StorageValue expected) {
        this.loadStorageAndCheck(
            context,
            path,
            Optional.of(expected)
        );
    }

    default void loadStorageAndCheck(final StorageContext context,
                                     final StoragePath path,
                                     final Optional<StorageValue> expected) {
        this.checkEquals(
            expected,
            context.loadStorage(path),
            () -> " loadStorage " + path
        );
    }

    // saveStorage......................................................................................................

    default void saveStorageAndCheck(final StorageContext context,
                                     final StorageValue value,
                                     final StorageValue expected) {
        this.checkEquals(
            expected,
            context.saveStorage(value),
            () -> " saveStorage " + value
        );
    }

    // listStorage......................................................................................................

    default void listStorageAndCheck(final StorageContext context,
                                     final StoragePath parent,
                                     final int offset,
                                     final int count,
                                     final StorageValueInfo... expected) {
        this.listStorageAndCheck(
            context,
            parent,
            offset,
            count,
            Lists.of(expected)
        );
    }

    default void listStorageAndCheck(final StorageContext context,
                                     final StoragePath parent,
                                     final int offset,
                                     final int count,
                                     final List<StorageValueInfo> expected) {
        this.checkEquals(
            expected,
            context.listStorage(
                parent,
                offset,
                count
            ),
            () -> "listStorage parent=" + parent + " offset=" + offset + " count=" + count
        );
    }

    // storageMountPoints...............................................................................................

    default void storageMountPointsAndCheck(final StorageContext context,
                                            final StorageMountPoint<?>... expected) {
        this.storageMountPointsAndCheck(
            context,
            Lists.of(expected)
        );
    }

    default void storageMountPointsAndCheck(final StorageContext context,
                                            final List<StorageMountPoint<?>> expected) {
        this.checkEquals(
            expected,
            context.storageMountPoints(),
            context::toString
        );
    }
}
