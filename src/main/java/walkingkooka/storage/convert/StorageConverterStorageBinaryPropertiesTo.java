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
import walkingkooka.props.Properties;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;


/**
 * Converts *.properties files after converting the {@link StorageBinary#binary()} to {@link String} and then converting to a
 * {@link Properties} and then to the requested target type.
 */
final class StorageConverterStorageBinaryPropertiesTo<C extends StorageConverterContext> extends StorageConverterStorageBinary<C> {

    /**
     * Type safe getter.
     */
    static <C extends StorageConverterContext> StorageConverterStorageBinaryPropertiesTo<C> instance() {
        return Cast.to(INSTANCE);
    }

    private final static StorageConverterStorageBinaryPropertiesTo INSTANCE = new StorageConverterStorageBinaryPropertiesTo<>();

    private StorageConverterStorageBinaryPropertiesTo() {
        super();
    }

    @Override
    boolean isPathAndType(final StoragePath path,
                          final Class<?> type,
                          final C context) {
        return FileExtension.PROPERTIES.equals(
            path.fileExtension()
                .orElse(null)
        ) && context.canConvert(
            "",
            Properties.class
        ) && context.canConvert(
            Properties.EMPTY,
            type
        );
    }

    @Override
    <T> Either<T, String> doConvertStorageBinary(final StorageBinary storageBinary,
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
                // convert String to $type
                result = context.convert(
                    text.leftValue(),
                    type
                );
            }
        }

        return result;
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "*." + FileExtension.PROPERTIES + " AND " + StorageBinary.class.getSimpleName() + " to";
    }
}
