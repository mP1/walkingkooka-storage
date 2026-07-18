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
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.environment.EnvironmentContextTesting;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.storage.FakeHasUserDirectories;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.convert.StorageConverterContextDelegatorTest.TestStorageConverterContextDelegator;
import walkingkooka.tree.expression.convert.ExpressionNumberBinaryNumberConverterFunctions;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.util.Objects;
import java.util.Optional;

public final class StorageConverterContextDelegatorTest implements StorageConverterContextTesting<TestStorageConverterContextDelegator>,
    DecimalNumberContextDelegator,
    EnvironmentContextTesting {

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
        this.currentWorkingDirectoryAndCheck(
            new TestStorageConverterContextDelegator(OPTIONAL_CURRENT_WORKING_DIRECTORY),
            CURRENT_WORKING_DIRECTORY
        );
    }

    @Test
    public void testCurrentWorkingDirectoryFails() {
        boolean failed = false;

        try {
            this.currentWorkingDirectoryAndCheck(
                new TestStorageConverterContextDelegator(
                    Optional.of(
                        StoragePath.parse(CURRENT_WORKING_DIRECTORY + "different")
                    )
                ),
                CURRENT_WORKING_DIRECTORY
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
        return new TestStorageConverterContextDelegator(OPTIONAL_CURRENT_WORKING_DIRECTORY);
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public int decimalNumberDigitCount() {
        return DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT;
    }

    @Override
    public MathContext mathContext() {
        return MATH_CONTEXT;
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
                            CURRENCY_LOCALE_CONTEXT,
                            DateTimeContexts.basic(
                                DateTimeSymbols.fromDateFormatSymbols(
                                    new DateFormatSymbols(LOCALE)
                                ),
                                LOCALE,
                                1920, // defaultYear
                                20, // twoDigitYear
                                HAS_NOW
                            ),
                            DECIMAL_NUMBER_CONTEXT
                        ),
                        EXPRESSION_NUMBER_KIND
                    ),
                    JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT
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
