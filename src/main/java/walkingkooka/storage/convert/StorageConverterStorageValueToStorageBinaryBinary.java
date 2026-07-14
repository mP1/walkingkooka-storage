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
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StorageValue;

/**
 * If the {@link StorageValue} is a binary then convert to {@link Binary}, ignoring the file extension and content type
 */
final class StorageConverterStorageValueToStorageBinaryBinary<C extends StorageConverterContext> extends StorageConverter<C> {

    /**
     * Type safe getter.
     */
    static <C extends StorageConverterContext> StorageConverterStorageValueToStorageBinaryBinary<C> instance() {
        return Cast.to(INSTANCE);
    }

    /**
     * Singleton
     */
    private final static StorageConverterStorageValueToStorageBinaryBinary INSTANCE = new StorageConverterStorageValueToStorageBinaryBinary<>();

    private StorageConverterStorageValueToStorageBinaryBinary() {
        super();
    }

    @Override
    public boolean canConvert(final Object value,
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
        return context.canConvert(
            storageValue.value()
                .orElse(null),
            Binary.class
        );
    }

    @Override
    public <T> Either<T, String> doConvert(final Object value,
                                           final Class<T> type,
                                           final C context) {
        Either<T, String> storageBinary = null;

        final StorageValue storageValue = (StorageValue) value;

        final Object storageValueValue = storageValue.value()
            .orElse(null);

        // convert StorageValue.value to Binary
        if (null != storageValueValue) {
            final Either<Binary, String> binary = context.convert(
                storageValueValue,
                Binary.class
            );

            if(binary.isLeft()) {
                storageBinary = this.successfulConversion(
                    StorageBinary.with(
                        storageValue.path(),
                        binary.leftValue()
                    ).setContentType(
                        storageValue.contentType()
                    ),
                    type
                );
            }
        }

        return null == storageBinary ?
            this.failConversion(
                value,
                type
            ) :
            storageBinary;
    }

    @Override
    public String toString() {
        return "BinaryFile";
    }
}
