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
import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.currency.CurrencyContexts;
import walkingkooka.currency.CurrencyExchangeRaters;
import walkingkooka.currency.CurrencyLocaleContext;
import walkingkooka.currency.CurrencyLocaleContexts;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.props.Properties;
import walkingkooka.storage.FakeHasUserDirectories;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.convert.StorageConverterContextDelegatorTest.TestStorageConverterContextDelegator;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberBinaryNumberConverterFunctions;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.util.Currency;
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
    public void testSetObjectPostProcessor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetPreProcessor() {
        throw new UnsupportedOperationException();
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
            final ExpressionNumberKind expressionNumberKind = ExpressionNumberKind.DEFAULT;
            final LocaleContext localeContext = LocaleContexts.jre(LOCALE);

            final CurrencyLocaleContext currencyLocaleContext = CurrencyLocaleContexts.basic(
                CurrencyContexts.jre(
                    Currency.getInstance(LOCALE),
                    CurrencyExchangeRaters.properties(
                        Properties.EMPTY,
                        (s, b) -> {
                            throw new UnsupportedOperationException();
                        }
                    ),
                    localeContext
                ),
                localeContext
            );

            this.storageConverterContext = StorageConverterContexts.basic(
                Converters.fake(),
                new FakeHasUserDirectories() {
                    @Override
                    public Optional<StoragePath> currentWorkingDirectory() {
                        return currentWorkingDirectory;
                    }
                },
                MEDIA_TYPE_DETECTOR,
                JsonNodeConverterContexts.basic(
                    ExpressionNumberConverterContexts.basic(
                        Converters.fake(),
                        ExpressionNumberBinaryNumberConverterFunctions.multiply(), // multiplier
                        ConverterContexts.basic(
                            false, // canNumbersHaveGroupSeparator
                            Converters.JAVA_EPOCH_OFFSET, // dateOffset
                            ',', // valueSeparator
                            Converters.fake(),
                            BinaryNumberConverterFunctions.fake(), // multiplier
                            BINARY_TEXT_CONTEXT,
                            currencyLocaleContext,
                            DateTimeContexts.basic(
                                DateTimeSymbols.fromDateFormatSymbols(
                                    new DateFormatSymbols(LOCALE)
                                ),
                                LOCALE,
                                1920, // defaultYear
                                20, // twoDigitYear
                                LocalDateTime::now
                            ),
                            DECIMAL_NUMBER_CONTEXT
                        ),
                        expressionNumberKind
                    ),
                    JsonNodeMarshallUnmarshallContexts.basic(
                        JsonNodeMarshallContexts.basic(),
                        JsonNodeUnmarshallContexts.basic(
                            expressionNumberKind,
                            currencyLocaleContext, // CurrencyCodeLanguageTagContext
                            DECIMAL_NUMBER_CONTEXT.mathContext()
                        )
                    )
                )
            );
        }

        @Override
        public StorageConverterContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
            Objects.requireNonNull(processor, "processor");
            return this;
        }

        @Override
        public StorageConverterContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
            Objects.requireNonNull(processor, "processor");
            return this;
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
