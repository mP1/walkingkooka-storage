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
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.storage.FakeHasUserDirectories;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.convert.StorageConverterContextDelegatorTest.TestStorageConverterContextDelegator;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;

import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public final class StorageConverterContextDelegatorTest implements StorageConverterContextTesting<TestStorageConverterContextDelegator>,
    DecimalNumberContextDelegator {

    private final static String CWD = "/current/working/directory/";

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = DecimalNumberContexts.american(MathContext.DECIMAL32);

    @Test
    public void testCurrentWorkingDirectoryWhenEmpty() {
        this.currentWorkingDirectoryAndCheck(
            new TestStorageConverterContextDelegator(
                Optional.empty()
            )
        );
    }

    @Test
    public void testCurrentWorkingDirectoryWhenNotEmpty() {
        final StoragePath path = StoragePath.parse(CWD);

        this.currentWorkingDirectoryAndCheck(
            new TestStorageConverterContextDelegator(
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
                new TestStorageConverterContextDelegator(
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
    public TestStorageConverterContextDelegator createContext() {
        return new TestStorageConverterContextDelegator(
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
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<TestStorageConverterContextDelegator> type() {
        return TestStorageConverterContextDelegator.class;
    }

    final static class TestStorageConverterContextDelegator implements StorageConverterContextDelegator {

        TestStorageConverterContextDelegator(final Optional<StoragePath> currentWorkingDirectory) {
            final Locale locale = Locale.ENGLISH;

            this.storageConverterContext = StorageConverterContexts.basic(
                new FakeHasUserDirectories() {
                    @Override
                    public Optional<StoragePath> currentWorkingDirectory() {
                        return currentWorkingDirectory;
                    }
                },
                ConverterContexts.basic(
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

        // StorageConverterContextDelegator.............................................................................

        @Override
        public StorageConverterContext storageConverterContext() {
            return this.storageConverterContext;
        }

        private final StorageConverterContext storageConverterContext;

        // Object.......................................................................................................

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
