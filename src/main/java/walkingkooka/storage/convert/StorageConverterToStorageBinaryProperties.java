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
 * Converts a {@link StorageValue} into {@link StorageBinary} if the file extension is {@link FileExtension#PROPERTIES}
 * and the {@link StorageValue#value()} can be converted into a {@link Properties} and then {@link String} and then
 * {@link StorageBinary}.
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
        return FileExtension.PROPERTIES.equals(
            storageValue.path()
                .fileExtension()
                .orElse(null)
        ) &&
            storageValue.value()
                .map((Object value) -> context.canConvert(value, Properties.class) && context.canConvert(Properties.EMPTY, Binary.class))
                .orElse(false);
    }

    @Override
    Either<Binary, String> toBinary(final StorageValue storageValue,
                                    final C context) {
        Either<Properties, String> properties = context.convert(
            storageValue.value()
                .orElse(null),
            Properties.class
        );

        return properties.isLeft() ?
            context.convert(
                properties.leftValue(),
                Binary.class
            ) :
            Cast.to(properties);
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "*." + FileExtension.PROPERTIES + " to " + StorageBinary.class.getSimpleName();
    }
}
