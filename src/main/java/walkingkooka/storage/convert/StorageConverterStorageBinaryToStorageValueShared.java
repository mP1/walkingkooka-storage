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

package walkingkooka.storage.convert;

import walkingkooka.Binary;
import walkingkooka.Either;
import walkingkooka.io.FileExtension;
import walkingkooka.net.header.MediaType;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;

import java.util.Optional;

/**
 * Base class for any {@link walkingkooka.convert.Converter} that matches a {@link StoragePath} and converts the
 * {@link Binary} to another type using other {@link walkingkooka.convert.Converter}.
 */
abstract class StorageConverterStorageBinaryToStorageValueShared<C extends StorageConverterContext> extends StorageConverterStorageBinaryToStorageValue<C> {

    StorageConverterStorageBinaryToStorageValueShared() {
        super();
    }

    @Override
    final boolean testStorageBinary(final StorageBinary storageBinary) {
        return this.fileExtension()
            .test(
                storageBinary.path()
                    .fileExtension()
                    .orElse(null)
        ) ||
            this.contentType()
                .test(
                    storageBinary.contentType()
                        .orElse(null)
                );
    }

    abstract FileExtension fileExtension();

    abstract MediaType contentType();

    final <T> Either<T, String> successfulConversion(final StoragePath storagePath,
                                                     final Class<T> type,
                                                     final Object value) {
        return this.successfulConversion(
            StorageValue.with(
                storagePath
            ).setValue(
                Optional.of(value)
            ),
            type
        );
    }

    // Object...........................................................................................................

    // StorageBinary *.csv | text/csv to StorageValue
    @Override
    public final String toString() {
        return StorageBinary.class.getSimpleName() +
            " *." +
            this.fileExtension() +
            " | " +
            this.contentType() +
            " to " +
            StorageValue.class.getSimpleName();
    }
}
