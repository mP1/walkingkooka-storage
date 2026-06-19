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

import org.junit.jupiter.api.Test;
import walkingkooka.Binary;
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.convert.ShortCircuitingConverter;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.tree.expression.Expression;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class StorageConverterToStorageBinaryExpressionTest extends StorageConverterToStorageBinaryTestCase<StorageConverterToStorageBinaryExpression<FakeStorageConverterContext>> {

    private final static Charset CHARSET = StandardCharsets.UTF_8;

    private final static Expression EXPRESSION = Expression.add(
        Expression.value(111),
        Expression.value(222)
    );

    private final static String EXPRESSION_STRING = "111+222";

    @Test
    public void testConvertStorageValueTextToStorageBinary() {
        final StoragePath storagePath = StoragePath.parse("/dir/add.expression.txt");

        this.convertAndCheck(
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(EXPRESSION)
                ),
            StorageBinary.with(
                storagePath,
                Binary.with(
                    EXPRESSION_STRING.getBytes(CHARSET)
                )
            )
        );
    }

    @Override
    public StorageConverterToStorageBinaryExpression<FakeStorageConverterContext> createConverter() {
        return StorageConverterToStorageBinaryExpression.instance();
    }

    @Override
    public FakeStorageConverterContext createContext() {
        return new FakeStorageConverterContext() {

            @Override
            public Charset charset() {
                return CHARSET;
            }

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> type) {
                return this.converter.canConvert(
                    value,
                    type,
                    this
                );
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return this.converter.convert(
                    value,
                    target,
                    this
                );
            }

            private final Converter<StorageConverterContext> converter = Converters.collection(
                Lists.of(
                    Converters.simple(),
                    Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString(),
                    new ShortCircuitingConverter<>() {

                        @Override
                        public boolean canConvert(final Object value,
                                                  final Class<?> type,
                                                  final StorageConverterContext context) {
                            return value instanceof Expression &&
                                String.class == type;
                        }

                        @Override
                        public <T> Either<T, String> doConvert(final Object value,
                                                               final Class<T> type,
                                                               final StorageConverterContext storageConverterContext) {
                            return this.successfulConversion(
                                value.toString(),
                                type
                            );
                        }

                    },
                    Converters.textToBinary()
                )
            );
        };
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createConverter(),
            "*.expression.txt to StorageBinary"
        );
    }

    @Override
    public Class<StorageConverterToStorageBinaryExpression<FakeStorageConverterContext>> type() {
        return Cast.to(StorageConverterToStorageBinaryExpression.class);
    }
}
