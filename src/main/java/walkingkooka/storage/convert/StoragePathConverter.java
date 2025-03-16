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
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.storage.StoragePath;

import java.util.Objects;

/**
 * A {@link Converter} that converts {@link String} to {@link StoragePath}.
 */
final class StoragePathConverter<C extends ConverterContext> implements Converter<C> {

    /**
     * Type safe getter.
     */
    static <C extends ConverterContext> StoragePathConverter<C> instance() {
        return Cast.to(INSTANCE);
    }

    private final static StoragePathConverter<?> INSTANCE = new StoragePathConverter<>();

    private StoragePathConverter() {
        super();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final C context) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(context, "context");

        return value instanceof String && type == StoragePath.class;
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type,
                                         final C context) {
        return this.canConvert(
            value,
            type,
            context
        ) ?
            this.convertString(
                (String) value,
                type
            ) :
            this.failConversion(
                value,
                type
            );
    }

    private <T> Either<T, String> convertString(final String value,
                                                final Class<T> type) {
        return this.successfulConversion(
            StoragePath.parse(value),
            type
        );
    }

    @Override
    public String toString() {
        return "String -> StoragePath";
    }
}
