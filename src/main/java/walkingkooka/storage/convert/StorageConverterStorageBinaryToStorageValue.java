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

import walkingkooka.Either;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StorageValue;

/**
 * Base class for any {@link walkingkooka.convert.Converter} that handles converting some {@link StorageBinary} to
 * a {@link StorageValue}.
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

    abstract boolean testStorageBinary(final StorageBinary storageBinary);

    @Override
    public final <T> Either<T, String> doConvert(final Object value,
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
}
