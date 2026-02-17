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
import walkingkooka.convert.ConverterContextDelegator;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.convert.StorageConverterContextTestingTest.TestStorageConverterContext;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;

import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public final class StorageConverterContextTestingTest implements StorageConverterContextTesting<TestStorageConverterContext>,
    DecimalNumberContextDelegator {

    private final static String CWD = "/current/working/directory/";

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = DecimalNumberContexts.american(MathContext.DECIMAL32);

    @Test
    public void testCurrentWorkingDirectoryWhenEmpty() {
        this.currentWorkingDirectoryAndCheck(
            new TestStorageConverterContext(
                Optional.empty()
            )
        );
    }

    @Test
    public void testCurrentWorkingDirectoryWhenNotEmpty() {
        final StoragePath path = StoragePath.parse(CWD);

        this.currentWorkingDirectoryAndCheck(
            new TestStorageConverterContext(
                Optional.of(path)
            ),
            path
        );
    }

    @Test
    public void testCurrentWorkingDirectoryFails() {
        final StoragePath path = StoragePath.parse(CWD);

        boolean failed = false;

        try {
            this.currentWorkingDirectoryAndCheck(
                new TestStorageConverterContext(
                    Optional.of(
                        StoragePath.parse(CWD + "different")
                    )
                ),
                path
            );
        } catch (final AssertionError e) {
            failed = true;
        }

        this.checkEquals(
            true,
            failed
        );
    }

    @Override
    public TestStorageConverterContext createContext() {
        return new TestStorageConverterContext(
            Optional.of(
                StoragePath.parse(CWD)
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
    public Class<TestStorageConverterContext> type() {
        return TestStorageConverterContext.class;
    }

    final static class TestStorageConverterContext implements StorageConverterContext,
        ConverterContextDelegator {

        TestStorageConverterContext(final Optional<StoragePath> currentWorkingDirectory) {
            this.currentWorkingDirectory = currentWorkingDirectory;
        }

        @Override
        public StoragePath parseStoragePath(final String text) {
            return StoragePath.parseSpecial(
                text,
                this
            );
        }

        @Override
        public Optional<StoragePath> currentWorkingDirectory() {
            return this.currentWorkingDirectory;
        }

        private final Optional<StoragePath> currentWorkingDirectory;

        @Override
        public Optional<StoragePath> homeDirectory() {
            throw new UnsupportedOperationException();
        }

        // ConverterContextDelegator....................................................................................

        @Override
        public ConverterContext converterContext() {
            final Locale locale = Locale.ENGLISH;

            return ConverterContexts.basic(
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
            );
        }

        // Object.......................................................................................................

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
