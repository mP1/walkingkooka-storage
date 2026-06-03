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

import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.io.FileExtension;
import walkingkooka.net.header.MediaType;
import walkingkooka.props.Properties;
import walkingkooka.storage.StorageBinary;


/**
 * Converts *.properties files after converting the {@link StorageBinary#binary()} to {@link String} and then converting
 * that {@link String} to a {@link Properties}.
 */
final class StorageConverterStorageBinaryToStorageValueProperties<C extends StorageConverterContext> extends StorageConverterStorageBinaryToStorageValue<C> {

    /**
     * Type safe getter.
     */
    static <C extends StorageConverterContext> StorageConverterStorageBinaryToStorageValueProperties<C> instance() {
        return Cast.to(INSTANCE);
    }

    private final static StorageConverterStorageBinaryToStorageValueProperties INSTANCE = new StorageConverterStorageBinaryToStorageValueProperties<>();

    private StorageConverterStorageBinaryToStorageValueProperties() {
        super();
    }

    @Override
    FileExtension fileExtension() {
        return FileExtension.PROPERTIES;
    }

    @Override
    MediaType contentType() {
        return MediaType.TEXT_PROPERTIES;
    }

    @Override
    <T> Either<T, String> storageBinaryToStorageValue(final StorageBinary storageBinary,
                                                      final Class<T> type,
                                                      final C context) {
        final Either<T, String> result;

        // convert Binary to String
        final Either<String, String> text = context.convert(
            storageBinary,
            String.class
        );
        if (text.isRight()) {
            result = Cast.to(text);
        } else {
            final Either<Properties, String> properties = context.convert(
                text.leftValue(),
                Properties.class
            );
            if (properties.isRight()) {
                result = Cast.to(properties);
            } else {
                result = this.successfulConversion(
                    storageBinary.path(),
                    type,
                    properties.leftValue()
                );
            }
        }

        return result;
    }
}
