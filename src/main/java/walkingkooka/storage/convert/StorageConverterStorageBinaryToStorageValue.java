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
abstract class StorageConverterStorageBinaryToStorageValue<C extends StorageConverterContext> extends StorageConverter<C> {

    StorageConverterStorageBinaryToStorageValue() {
        super();
    }

    @Override
    public final boolean canConvert(final Object value,
                                    final Class<?> type,
                                    final C context) {
        return value instanceof StorageBinary &&
            StorageValue.class == type &&
            this.testStorageBinary(
                (StorageBinary) value
            );
    }

    private boolean testStorageBinary(final StorageBinary storageBinary) {
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

    @Override
    public <T> Either<T, String> doConvert(final Object value,
                                           final Class<T> type,
                                           final C context) {
        return this.storageBinaryToStorageValue(
            (StorageBinary) value,
            type,
            context
        );
    }

    abstract <T> Either<T, String> storageBinaryToStorageValue(final StorageBinary value,
                                                               final Class<T> type,
                                                               final C context);

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

    @Override
    public final String toString() {
        return "*." + this.fileExtension() + " AND " + StorageBinary.class.getSimpleName() + " to";
    }
}
