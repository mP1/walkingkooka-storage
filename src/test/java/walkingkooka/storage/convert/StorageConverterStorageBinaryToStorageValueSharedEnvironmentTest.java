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
import walkingkooka.HasCharsetTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.currency.CurrencyContextTesting;
import walkingkooka.environment.Environment;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.convert.EnvironmentConverterContext;
import walkingkooka.environment.convert.EnvironmentConverters;
import walkingkooka.net.header.MediaType;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;

import java.nio.charset.Charset;
import java.util.Optional;

public final class StorageConverterStorageBinaryToStorageValueSharedEnvironmentTest extends StorageConverterStorageBinaryToStorageValueSharedTestCase<StorageConverterStorageBinaryToStorageValueSharedEnvironment<FakeStorageConverterContext>>
    implements HasCharsetTesting,
    CurrencyContextTesting {

    @Test
    public void testConvertStorageBinaryEnvironmentToStorageValueWithTextPlainContentType() {
        final Environment environment = Environment.empty()
            .set(
                EnvironmentValueName.CURRENCY,
                CURRENCY
            );

        final StoragePath storagePath = StoragePath.parse("/dateTimeSymbols.env");

        this.convertAndCheck(
            StorageBinary.with(
                storagePath,
                Binary.with(
                    "currency=AUD".getBytes(CHARSET)
                )
            ),
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(environment)
                ).setContentType(
                    Optional.of(MediaType.TEXT_PLAIN)
                )
        );
    }

    @Test
    public void testConvertStorageBinaryEnvironmentToStorageValue() {
        final Environment environment = Environment.empty()
            .set(
                EnvironmentValueName.CURRENCY,
                CURRENCY
            );

        final StoragePath storagePath = StoragePath.parse("/dateTimeSymbols.env");

        this.convertAndCheck(
            StorageBinary.with(
                storagePath,
                Binary.with(
                    "currency=AUD".getBytes(CHARSET)
                )
            ),
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(environment)
                ).setContentType(
                    Optional.of(Environment.CONTENT_TYPE)
                )
        );
    }

    @Override
    public StorageConverterStorageBinaryToStorageValueSharedEnvironment<FakeStorageConverterContext> createConverter() {
        return StorageConverterStorageBinaryToStorageValueSharedEnvironment.instance();
    }

    @Override
    public FakeStorageConverterContext createContext() {
        return new FakeStorageConverterContext() {

            @Override
            public Charset charset() {
                return CHARSET;
            }

//            @Override
//            public char valueSeparator() {
//                return ',';
//            }

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

            private final Converter<EnvironmentConverterContext> converter = Converters.collection(
                Lists.of(
                    Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString(),
                    Converters.binaryToString(),
                    EnvironmentConverters.textToEnvironment()
                )
            );
        };
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createConverter(),
            "StorageBinary *.env | text/x-java-properties to StorageValue"
        );
    }

    @Override
    public Class<StorageConverterStorageBinaryToStorageValueSharedEnvironment<FakeStorageConverterContext>> type() {
        return Cast.to(StorageConverterStorageBinaryToStorageValueSharedEnvironment.class);
    }
}
