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
import walkingkooka.datetime.DateTimeContextTesting;
import walkingkooka.environment.Environment;
import walkingkooka.environment.EnvironmentContextTesting;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.convert.EnvironmentConverters;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.text.LineEnding;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Optional;

public final class StorageConverterStorageValueToStorageBinarySharedEnvironmentTest extends StorageConverterStorageValueToStorageBinarySharedTestCase<StorageConverterStorageValueToStorageBinarySharedEnvironment<FakeStorageConverterContext>>
    implements DateTimeContextTesting,
    EnvironmentContextTesting {

    @Test
    public void testConvertStorageValueEnvironmentWithCurrencyAndEnvFileExtensionWithoutContentTypeToStorageBinary() {
        final StoragePath storagePath = StoragePath.parse("/path123/EnvironmentContext.env");

        this.convertAndCheck(
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(
                        Environment.empty()
                            .set(
                                EnvironmentValueName.CURRENCY,
                                CURRENCY
                            )
                    )
                ).clearContentType(),
            StorageBinary.with(
                storagePath,
                Binary.with(
                    "currency=AUD\n".getBytes(CHARSET)
                )
            ).setContentType(
                Optional.of(Environment.CONTENT_TYPE)
            )
        );
    }

    @Test
    public void testConvertStorageValueEnvironmentWithEnvFileExtensionWithoutContentTypeToStorageBinary() {
        final StoragePath storagePath = StoragePath.parse("/path123/EnvironmentContext.env");

        this.convertAndCheck(
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(ENVIRONMENT_CONTEXT.environment())
                ).clearContentType(),
            StorageBinary.with(
                storagePath,
                Binary.with(
                    "charset=UTF-8\ncurrency=AUD\nindentation=\"  \"\nlineEnding=\"\\n\"\nlocale=en_AU\nnow=1999-12-31T12:58:59\ntimeOffset=Z\nuser=user123@example.com\n"
                        .getBytes(CHARSET)
                )
            ).setContentType(
                Optional.of(Environment.CONTENT_TYPE)
            )
        );
    }

    @Override
    public StorageConverterStorageValueToStorageBinarySharedEnvironment<FakeStorageConverterContext> createConverter() {
        return StorageConverterStorageValueToStorageBinarySharedEnvironment.instance();
    }

    @Override
    public FakeStorageConverterContext createContext() {
        return new FakeStorageConverterContext() {

            @Override
            public Charset charset() {
                return CHARSET;
            }

            @Override
            public Locale locale() {
                return LOCALE;
            }

            @Override
            public LineEnding lineEnding() {
                return LINE_ENDING;
            }

            @Override
            public int twoDigitYear() {
                return TWO_DIGIT_YEAR;
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
                    EnvironmentConverters.environmentToString(),
                    Converters.textToCurrencyCode(),
                    Converters.textToCurrency(),
                    Converters.textToLineEnding(),
                    Converters.textToLocale(),
                    Converters.textToBinary(),
                    Converters.objectToString() // eg Currency & Locale -> String
                )
            );
        };
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createConverter(),
            "*.env to StorageBinary"
        );
    }

    @Override
    public Class<StorageConverterStorageValueToStorageBinarySharedEnvironment<FakeStorageConverterContext>> type() {
        return Cast.to(StorageConverterStorageValueToStorageBinarySharedEnvironment.class);
    }
}
