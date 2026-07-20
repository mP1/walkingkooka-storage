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
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.io.FileExtension;
import walkingkooka.net.header.MediaType;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;

/**
 * Base class for any {@link walkingkooka.convert.Converter} that converts a {@link walkingkooka.storage.StorageValue}
 * to a {@link StorageBinary} if the file extension and value object match.
 */
abstract class StorageConverterStorageValueToStorageBinaryShared<C extends StorageConverterContext> extends StorageConverter<C> {

    StorageConverterStorageValueToStorageBinaryShared() {
        super();
    }

    @Override
    public final boolean canConvert(final Object value,
                                    final Class<?> type,
                                    final C context) {
        return value instanceof StorageValue &&
            this.testStorageValue(
                (StorageValue) value,
                context
            );
    }

    private boolean testStorageValue(final StorageValue storageValue,
                                     final C context) {
        final FileExtension fileExtension = storageValue.path()
            .fileExtension()
            .orElse(null);

        final MediaType mediaType = storageValue.contentType()
            .orElse(null);

        final Object value = storageValue.value()
            .orElse(null);

        return (
            this.fileExtension()
                .test(fileExtension) ||
                this.contentType()
                    .test(mediaType)
        ) &&
            null != value &&
            this.testValue(
                value,
                context
            );
    }

    /**
     * The {@link StoragePath} file extension that must be matched.
     */
    abstract FileExtension fileExtension();

    abstract MediaType contentType();

    /**
     * Sub-classes should test non null value is supported.
     */
    abstract boolean testValue(final Object value,
                               final C context);

    @Override
    public <T> Either<T, String> doConvert(final Object value,
                                           final Class<T> type,
                                           final C context) {
        final StorageValue storageValue = (StorageValue) value;

        final Either<Binary, String> binary = this.toBinary(
            storageValue,
            context
        );

        final Either<T, String> storageBinary;
        if (binary.isRight()) {
            storageBinary = Cast.to(binary);
        } else {
            storageBinary = this.successfulConversion(
                StorageBinary.with(
                    storageValue.path(),
                    binary.leftValue()
                ).setContentType(storageValue.contentType()),
                type
            );
        }

        return storageBinary;
    }

    abstract Either<Binary, String> toBinary(final StorageValue storageValue,
                                             final C context);

    // Object...........................................................................................................

    @Override
    public final String toString() {
        return "*." + this.fileExtension() + " to " + StorageBinary.class.getSimpleName();
    }
}
