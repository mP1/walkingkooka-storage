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
import walkingkooka.convert.ShortCircuitingConverter;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;

/**
 * Base class for any {@link walkingkooka.convert.Converter} that matches a {@link StoragePath} and converts the
 * {@link Binary} to another type using other {@link walkingkooka.convert.Converter}.
 */
abstract class StorageConverterStorageBinary<C extends StorageConverterContext> extends StorageConverter<C>
    implements ShortCircuitingConverter<C> {

    StorageConverterStorageBinary() {
        super();
    }

    @Override
    public final boolean canConvert(final Object value,
                              final Class<?> type,
                              final C context) {
        return value instanceof StorageBinary &&
            this.isPathAndType(
                ((StorageBinary) value).path(),
                type,
                context
            );
    }

    /**
     * Sub-classes should test the file extension and type are supported.
     */
    abstract boolean isPathAndType(final StoragePath path,
                                   final Class<?> type,
                                   final C context);

    @Override
    public <T> Either<T, String> doConvert(final Object value,
                                           final Class<T> type,
                                           final C context) {
        return this.doConvertStorageBinary(
            (StorageBinary) value,
            type,
            context
        );
    }

    abstract <T> Either<T, String> doConvertStorageBinary(final StorageBinary value,
                                                          final Class<T> type,
                                                          final C context);
}
