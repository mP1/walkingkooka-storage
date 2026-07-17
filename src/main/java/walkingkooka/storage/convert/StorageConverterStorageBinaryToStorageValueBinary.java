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
import walkingkooka.net.header.MediaType;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;

import java.util.Optional;

/**
 * A {@link StorageConverterStorageBinaryToStorageValue} that keeps the {@link Binary} and detects the {@link MediaType}.
 * <br>
 * This should be the last with other {@link StorageConverterStorageBinaryToStorageValue} preceeding it in a collection.
 */
final class StorageConverterStorageBinaryToStorageValueBinary<C extends StorageConverterContext> extends StorageConverterStorageBinaryToStorageValue<C> {

    /**
     * Type safe getter.
     */
    static <C extends StorageConverterContext> StorageConverterStorageBinaryToStorageValueBinary<C> instance() {
        return Cast.to(INSTANCE);
    }

    /**
     * Singleton
     */
    private final static StorageConverterStorageBinaryToStorageValueBinary INSTANCE = new StorageConverterStorageBinaryToStorageValueBinary<>();

    private StorageConverterStorageBinaryToStorageValueBinary() {
        super();
    }

    // match BINARY or missing ContentType
    @Override
    boolean testStorageBinary(final StorageBinary storageBinary) {
        return true; // always
    }

    @Override
    <T> Either<T, String> storageBinaryToStorageValue(final StorageBinary storageBinary,
                                                      final Class<T> type,
                                                      final C context) {
        final StoragePath path = storageBinary.path();
        final Binary binary = storageBinary.binary();

        return this.successfulConversion(
            StorageValue.with(path)
                .setValue(
                    Optional.of(binary)
                ).setContentType(
                    Optional.of(
                        context.detect(
                            path.value(),
                            binary
                        )
                    )
                ),
            type
        );
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "* to " + StorageValue.class.getSimpleName();
    }
}

