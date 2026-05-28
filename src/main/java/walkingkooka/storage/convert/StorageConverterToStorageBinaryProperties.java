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
import walkingkooka.props.Properties;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StorageValue;

/**
 * Converts a {@link StorageValue} into {@link StorageBinary} if the file extension is *.properties and the value can be
 * converted into a {@link Properties} and then text.
 */
final class StorageConverterToStorageBinaryProperties<C extends StorageConverterContext> extends StorageConverterToStorageBinary<C> {

    /**
     * Type safe getter.
     */
    static <C extends StorageConverterContext> StorageConverterToStorageBinaryProperties<C> instance() {
        return Cast.to(INSTANCE);
    }

    private final static StorageConverterToStorageBinaryProperties INSTANCE = new StorageConverterToStorageBinaryProperties<>();

    private StorageConverterToStorageBinaryProperties() {
        super();
    }

    @Override
    boolean testStorageValue(final StorageValue storageValue,
                             final C context) {
        // file extension is *.txt and value can be converted to text
        return FileExtension.PROPERTIES.equals(
            storageValue.path()
                .fileExtension()
                .orElse(null)
        ) &&
            storageValue.value()
                .map((Object value) -> context.canConvert(value, Properties.class))
                .orElse(false) &&
            storageValue.value()
                .map((Object value) -> context.canConvert(Properties.EMPTY, String.class))
                .orElse(false);
    }

    @Override
    <T> Either<T, String> toStorageBinary(final StorageValue storageValue,
                                          final Class<T> type,
                                          final C context) {
        // convert to Properties
        Either<Properties, String> properties = context.convert(
            storageValue.value()
                .orElse(null),
            Properties.class
        );

        Either<T, String> storageBinary;
        if (properties.isRight()) {
            storageBinary = Cast.to(properties);
        } else {
            // convert Properties to String
            Either<String, String> text = context.convert(
                properties.leftValue(),
                String.class
            );

            if (text.isRight()) {
                storageBinary = Cast.to(text);
            } else {
                Either<Binary, String> binary = context.convert(
                    text.leftValue(),
                    Binary.class
                );
                if (binary.isRight()) {
                    storageBinary = Cast.to(binary);
                } else {
                    storageBinary = this.successfulConversion(
                        StorageBinary.with(
                            storageValue.path(),
                            binary.leftValue()
                        ),
                        type
                    );

                }
            }
        }

        return storageBinary;
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "*." + FileExtension.PROPERTIES + " to " + StorageBinary.class.getSimpleName();
    }
}
