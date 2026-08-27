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
import walkingkooka.environment.Environment;
import walkingkooka.io.FileExtension;
import walkingkooka.net.header.MediaType;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StorageValue;

/**
 * Converts a {@link StorageValue} into {@link StorageBinary} if the file extension is {@link FileExtension#ENV}.
 */
final class StorageConverterStorageValueToStorageBinarySharedEnvironment<C extends StorageConverterContext> extends StorageConverterStorageValueToStorageBinaryShared<C> {

    /**
     * Type safe getter.
     */
    static <C extends StorageConverterContext> StorageConverterStorageValueToStorageBinarySharedEnvironment<C> instance() {
        return Cast.to(INSTANCE);
    }

    /**
     * Singleton
     */
    private final static StorageConverterStorageValueToStorageBinarySharedEnvironment INSTANCE = new StorageConverterStorageValueToStorageBinarySharedEnvironment<>();

    private StorageConverterStorageValueToStorageBinarySharedEnvironment() {
        super();
    }

    @Override
    FileExtension fileExtension() {
        return Environment.FILE_EXTENSION;
    }

    @Override
    MediaType contentType() {
        return Environment.CONTENT_TYPE;
    }

    @Override
    boolean testValue(final Object value,
                      final C context) {
        return context.canConvert(value, Environment.class) &&
            context.canConvert(
                ENVIRONMENT,
                Binary.class
            );
    }

    private final static Environment ENVIRONMENT = Environment.empty();

    @Override
    Either<Binary, String> toBinary(final StorageValue storageValue,
                                    final C context) {
        Either<Binary, String> result;

        final Either<Environment, String> environment = context.convert(
            storageValue.value()
                .orElse(null),
            Environment.class
        );

        if (environment.isLeft()) {
            final Either<String, String> string = context.convert(
                environment.leftValue(),
                String.class
            );

            if (string.isLeft()) {
                result = context.convert(
                    string.leftValue(),
                    Binary.class
                );
            } else {
                result = Cast.to(string);
            }
        } else {
            result = Cast.to(environment);
        }

        return result;
    }
}
