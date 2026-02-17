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
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.storage.FakeHasUserDirectories;
import walkingkooka.storage.StoragePath;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;

import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicStorageConverterContextTest implements StorageConverterContextTesting<BasicStorageConverterContext>,
    DecimalNumberContextDelegator {

    private final static String CWD = "/current/working/directory/";

    private final static String HOME = "/home/user123";

    private final static ConverterContext CONVERTER_CONTEXT = ConverterContexts.fake();

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = DecimalNumberContexts.american(MathContext.DECIMAL32);

    // with.............................................................................................................

    @Test
    public void testWithNullCurrentWorkingDirectoryFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicStorageConverterContext.with(
                null,
                CONVERTER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullConverterContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicStorageConverterContext.with(
                new FakeHasUserDirectories(),
                null
            )
        );
    }

    // currentWorkingDirectory..........................................................................................

    @Test
    public void testCurrentWorkingDirectory() {
        this.currentWorkingDirectoryAndCheck(
            this.createContext(),
            StoragePath.parse(CWD)
        );
    }

    // parseStoragePath.................................................................................................

    @Test
    public void testParseStoragePath() {
        this.parseStoragePathAndCheck(
            this.createContext(),
            "after123",
            StoragePath.parse(CWD + "/after123")
        );
    }

    @Override
    public BasicStorageConverterContext createContext() {
        final Locale locale = Locale.ENGLISH;

        return BasicStorageConverterContext.with(
            new FakeHasUserDirectories() {

                @Override
                public Optional<StoragePath> currentWorkingDirectory() {
                    return Optional.of(
                        StoragePath.parse(CWD)
                    );
                }
            },
            ConverterContexts.basic(
                (l) -> {
                    Objects.requireNonNull(l, "locale");
                    throw new UnsupportedOperationException();
                }, // canCurrencyForLocale
                (l) -> {
                    Objects.requireNonNull(l, "locale");
                    throw new UnsupportedOperationException();
                }, // canDateTimeSymbolsForLocale
                (l) -> {
                    Objects.requireNonNull(l, "locale");
                    throw new UnsupportedOperationException();
                }, // canDecimalNumberSymbolsForLocale
                false, // canNumbersHaveGroupSeparator
                Converters.EXCEL_1904_DATE_SYSTEM_OFFSET,
                Indentation.SPACES2,
                LineEnding.NL,
                ',', // valueSeparator
                Converters.fake(),
                DateTimeContexts.basic(
                    DateTimeSymbols.fromDateFormatSymbols(
                        new DateFormatSymbols(locale)
                    ),
                    locale,
                    1920,
                    20,
                    LocalDateTime::now
                ),
                DECIMAL_NUMBER_CONTEXT
            )
        );
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public int decimalNumberDigitCount() {
        return DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT;
    }

    @Override
    public MathContext mathContext() {
        return MathContext.DECIMAL32;
    }

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return DECIMAL_NUMBER_CONTEXT;
    }

    // class............................................................................................................

    @Override
    public Class<BasicStorageConverterContext> type() {
        return BasicStorageConverterContext.class;
    }
}
