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

package walkingkooka.storage;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.ConverterLike;
import walkingkooka.convert.Converters;
import walkingkooka.currency.FakeCurrencyContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.header.MediaTypeDetector;
import walkingkooka.net.header.MediaTypeDetectors;
import walkingkooka.text.Indentation;

import java.math.MathContext;
import java.nio.charset.StandardCharsets;
import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicStorageContextTest implements StorageContextTesting<BasicStorageContext>,
    HashCodeEqualsDefinedTesting2<BasicStorageContext> {

    private final static ConverterLike CONVERTER_LIKE = ConverterContexts.basic(
        false, // canNumbersHaveGroupSeparator
        Converters.EXCEL_1904_DATE_SYSTEM_OFFSET,
        ',', // valueSeparator
        Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString(),
        BinaryNumberConverterFunctions.fake(), // multiplier
        BINARY_TEXT_CONTEXT,
        new FakeCurrencyContext() {
            @Override
            public Optional<Currency> currencyForLocale(final Locale locale) {
                return Optional.of(
                    Currency.getInstance(locale)
                );
            }
        }.setLocaleContext(
            LocaleContexts.jre(LOCALE)
        ),
        DateTimeContexts.basic(
            DateTimeSymbols.fromDateFormatSymbols(
                new DateFormatSymbols(LOCALE)
            ),
            LOCALE,
            1920,
            20,
            LocalDateTime::now
        ),
        DecimalNumberContexts.american(MathContext.DECIMAL32)
    );

    private final static MediaTypeDetector MEDIA_TYPE_DETECTOR = MediaTypeDetectors.binary();

    @Test
    public void testWithNullConverterLikeFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicStorageContext.with(
                null,
                MEDIA_TYPE_DETECTOR,
                ENVIRONMENT_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullMediaTypeDetectorFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicStorageContext.with(
                CONVERTER_LIKE,
                null,
                ENVIRONMENT_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicStorageContext.with(
                CONVERTER_LIKE,
                MEDIA_TYPE_DETECTOR,
                null
            )
        );
    }

    @Test
    public void testSetCurrentWorkingDirectory() {
        final StoragePath path = StoragePath.parse("/path1/path2");
        final BasicStorageContext context = this.createContext();
        this.setCurrentWorkingDirectoryAndCheck(
            context,
            path
        );
    }

    @Test
    public void testSetEnvironmentContext() {
        final BasicStorageContext context = this.createContext();

        final EnvironmentContext environmentContext = EnvironmentContexts.empty(
            StandardCharsets.UTF_8,
            Currency.getInstance("AUD"),
            Indentation.SPACES2,
            LINE_ENDING,
            Locale.GERMAN,
            HAS_NOW,
            Optional.of(DIFFERENT_USER)
        );

        final StorageContext after = context.setEnvironmentContext(environmentContext);
        assertNotSame(
            context,
            after
        );

        this.checkEquals(
            BasicStorageContext.with(
                CONVERTER_LIKE,
                MEDIA_TYPE_DETECTOR,
                environmentContext
            ),
            after
        );
    }

    // ConverterLike....................................................................................................

    @Test
    public void testConvert() {
        this.convertAndCheck(
            this.createContext(),
            "A",
            Character.class,
            'A'
        );
    }

    @Override
    public BasicStorageContext createContext() {
        return BasicStorageContext.with(
            CONVERTER_LIKE,
            MEDIA_TYPE_DETECTOR,
            EnvironmentContexts.map(
                EnvironmentContexts.empty(
                    StandardCharsets.UTF_8,
                    Currency.getInstance("AUD"),
                    Indentation.SPACES2,
                    LINE_ENDING,
                    Locale.FRANCE,
                    HAS_NOW,
                    Optional.of(USER)
                )
            )
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentConverterLike() {
        this.checkNotEquals(
            BasicStorageContext.with(
                ConverterContexts.fake(),
                MEDIA_TYPE_DETECTOR,
                ENVIRONMENT_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentMediaTypeDetector() {
        this.checkNotEquals(
            BasicStorageContext.with(
                CONVERTER_LIKE,
                MediaTypeDetectors.fake(),
                ENVIRONMENT_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentEnvironmentContext() {
        this.checkNotEquals(
            BasicStorageContext.with(
                CONVERTER_LIKE,
                MEDIA_TYPE_DETECTOR,
                EnvironmentContexts.fake()
            )
        );
    }

    @Override
    public BasicStorageContext createObject() {
        return this.createContext();
    }

    // class............................................................................................................

    @Override
    public Class<BasicStorageContext> type() {
        return BasicStorageContext.class;
    }
}
