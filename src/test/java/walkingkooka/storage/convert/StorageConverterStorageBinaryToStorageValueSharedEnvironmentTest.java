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
import walkingkooka.currency.CurrencyCode;
import walkingkooka.currency.CurrencyLocaleContextTesting;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContextTesting;
import walkingkooka.environment.Environment;
import walkingkooka.environment.EnvironmentContextTesting;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.convert.EnvironmentConverterContext;
import walkingkooka.environment.convert.EnvironmentConverters;
import walkingkooka.environment.convert.FakeEnvironmentConverterContext;
import walkingkooka.locale.LocaleLanguageTag;
import walkingkooka.math.DecimalNumberContextTesting;
import walkingkooka.net.convert.NetConverters;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.text.LineEnding;

import java.nio.charset.Charset;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

public final class StorageConverterStorageBinaryToStorageValueSharedEnvironmentTest extends StorageConverterStorageBinaryToStorageValueSharedTestCase<StorageConverterStorageBinaryToStorageValueSharedEnvironment<FakeStorageConverterContext>>
    implements HasCharsetTesting,
    CurrencyLocaleContextTesting,
    DateTimeContextTesting,
    DecimalNumberContextTesting,
    EnvironmentContextTesting {

    @Test
    public void testConvertStorageBinaryEnvironmentWithCurrencyToStorageValue() {
        final Environment environment = Environment.empty()
            .set(
                EnvironmentValueName.CURRENCY,
                CURRENCY
            );

        final StoragePath storagePath = StoragePath.parse("/currency.env");

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
                )
        );
    }

    @Test
    public void testConvertStorageBinaryEnvironmentWithAllEnvironmentContextEntriesToStorageValue() {
        final Environment environment = ENVIRONMENT_CONTEXT.environment();

        final StoragePath storagePath = StoragePath.parse("/EnvironmentContext.env");

        final String text = new FakeEnvironmentConverterContext() {

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

            private final Converter<FakeEnvironmentConverterContext> converter = Converters.collection(
                Lists.of(
                    Converters.localTimeToString(
                        (c) -> DateTimeFormatter.ofPattern("ss:mm:hh")
                    ),
                    Converters.localeToString(),
                    EnvironmentConverters.environmentToString(),
                    Converters.objectToString()
                )
            );
        }.convertOrFail(
            environment,
            String.class
        );

        this.convertAndCheck(
            StorageBinary.with(
                storagePath,
                Binary.with(
                    text.getBytes(CHARSET)
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
            public EnvironmentValueName<?> parseEnvironmentValueName(final String name) {
                return ENVIRONMENT_CONTEXT.parseEnvironmentValueName(name);
            }

            @Override
            public Charset charset() {
                return CHARSET;
            }

            @Override
            public Locale locale() {
                return LOCALE;
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
            public Optional<Currency> currencyForCurrencyCode(final CurrencyCode currencyCode) {
                return CURRENCY_CONTEXT.currencyForCurrencyCode(currencyCode);
            }

            @Override
            public Optional<Locale> localeForLanguageTag(final LocaleLanguageTag languageTag) {
                return LOCALE_CONTEXT.localeForLanguageTag(languageTag);
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
                    Converters.textToCharset(),
                    Converters.textToCurrencyCode(),
                    Converters.textToCurrency(),
                    Converters.textToIndentation(),
                    Converters.textToLocalDateTime(
                        (DateTimeContext dateTimeContext) -> DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                    ),
                    Converters.textToLineEnding(),
                    Converters.textToLocale(),
                    NetConverters.textToEmailAddress(),
                    Converters.textToZoneOffset(),
                    EnvironmentConverters.textToEnvironment(),
                    EnvironmentConverters.textToEnvironmentValueName()
                )
            );
        };
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createConverter(),
            "StorageBinary *.env | text/x-env to StorageValue"
        );
    }

    @Override
    public Class<StorageConverterStorageBinaryToStorageValueSharedEnvironment<FakeStorageConverterContext>> type() {
        return Cast.to(StorageConverterStorageBinaryToStorageValueSharedEnvironment.class);
    }
}
