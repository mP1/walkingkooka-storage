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
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.convert.ShortCircuitingConverter;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.tree.expression.Expression;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class StorageConverterStorageBinaryToStorageValueSharedExpressionTest extends StorageConverterStorageBinaryToStorageValueSharedTestCase<StorageConverterStorageBinaryToStorageValueSharedExpression<FakeStorageConverterContext>> {

    private final static Charset CHARSET = StandardCharsets.UTF_8;

    private final static Expression EXPRESSION = Expression.add(
        Expression.value(111),
        Expression.value(222)
    );

    @Test
    public void testConvertStorageBinaryExpressionToStorageValueWithExpression() {
        final StoragePath path = StoragePath.parse("/add.expression.txt");

        this.convertAndCheck(
            StorageBinary.with(
                path,
                Binary.with(
                    EXPRESSION.text()
                        .getBytes(CHARSET)
                )
            ),
            StorageValue.class,
            StorageValue.with(path)
                .setContentType(
                    Optional.of(Expression.MEDIA_TYPE)
                ).setValue(
                    Optional.of(EXPRESSION)
                )
        );
    }

    @Override
    public StorageConverterStorageBinaryToStorageValueSharedExpression<FakeStorageConverterContext> createConverter() {
        return StorageConverterStorageBinaryToStorageValueSharedExpression.instance();
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

            private final Converter<ConverterContext> converter = Converters.collection(
                Lists.of(
                    Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString(),
                    Converters.binaryToString(),
                    Converters.simple(),
                    new ShortCircuitingConverter<>() {

                        @Override
                        public boolean canConvert(final Object value,
                                                  final Class<?> type,
                                                  final ConverterContext context) {
                            return (
                                "".equals(value) ||
                                    EXPRESSION.text().equals(value)
                            ) &&
                                Expression.class == type;
                        }

                        @Override
                        public <T> Either<T, String> doConvert(final Object value,
                                                               final Class<T> type,
                                                               final ConverterContext context) {
                            return this.successfulConversion(
                                EXPRESSION,
                                type
                            );
                        }
                    }
                )
            );
        };
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createConverter(),
            "*.expression.txt to StorageValue"
        );
    }

    @Override
    public Class<StorageConverterStorageBinaryToStorageValueSharedExpression<FakeStorageConverterContext>> type() {
        return Cast.to(StorageConverterStorageBinaryToStorageValueSharedExpression.class);
    }
}
