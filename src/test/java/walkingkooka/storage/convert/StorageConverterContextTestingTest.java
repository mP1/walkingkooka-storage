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
import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.header.MediaType;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.convert.StorageConverterContextTestingTest.TestStorageConverterContext;
import walkingkooka.tree.expression.convert.ExpressionNumberBinaryNumberConverterFunctions;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContext;
import walkingkooka.tree.json.convert.JsonNodeConverterContextDelegator;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.util.Objects;
import java.util.Optional;

public final class StorageConverterContextTestingTest implements StorageConverterContextTesting<TestStorageConverterContext>,
    DecimalNumberContextDelegator {

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
        this.currentWorkingDirectoryAndCheck(
            new TestStorageConverterContext(
                OPTIONAL_CURRENT_WORKING_DIRECTORY
            ),
            CURRENT_WORKING_DIRECTORY
        );
    }

    @Test
    public void testCurrentWorkingDirectoryFails() {
        boolean failed = false;

        try {
            this.currentWorkingDirectoryAndCheck(
                new TestStorageConverterContext(
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
    public TestStorageConverterContext createContext() {
        return new TestStorageConverterContext(OPTIONAL_CURRENT_WORKING_DIRECTORY);
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
    public Class<TestStorageConverterContext> type() {
        return TestStorageConverterContext.class;
    }

    final static class TestStorageConverterContext implements StorageConverterContext,
        JsonNodeConverterContextDelegator {

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
        public MediaType detect(final String filename,
                                final Binary content) {
            return MEDIA_TYPE_DETECTOR.detect(
                    filename,
                    content
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

        // JsonNodeConverterContextDelegator............................................................................

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

        @Override
        public JsonNodeConverterContext jsonNodeConverterContext() {
            return JsonNodeConverterContexts.basic(
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
                            DEFAULT_YEAR,
                            TWO_DIGIT_YEAR,
                            HAS_NOW
                        ),
                        DecimalNumberContexts.american(MATH_CONTEXT)
                    ),
                    EXPRESSION_NUMBER_KIND
                ),
                JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT
            );
        }

        // Object.......................................................................................................

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
