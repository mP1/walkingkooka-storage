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

package walkingkooka.storage.expression.function;

import walkingkooka.Binary;
import walkingkooka.Cast;
import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.currency.CurrencyLocaleContexts;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.environment.CanParseEnvironmentValueName;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.header.MediaType;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageContext;
import walkingkooka.storage.StorageEnvironmentContextTesting;
import walkingkooka.storage.StorageMountPoint;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.storage.expression.FakeStorageExpressionEvaluationContext;
import walkingkooka.storage.expression.StorageExpressionEvaluationContext;
import walkingkooka.storage.expression.function.StorageExpressionFunctionTestCase.TestStorageExpressionEvaluationContext;
import walkingkooka.text.BinaryTextContextTesting;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.convert.TreeConverterContexts;
import walkingkooka.tree.expression.function.ExpressionFunctionTesting2;
import walkingkooka.tree.json.convert.JsonNodeConverterContext;
import walkingkooka.tree.json.convert.JsonNodeConverterContextDelegator;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContextTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public abstract class StorageExpressionFunctionTestCase<F extends StorageExpressionFunction<TestStorageExpressionEvaluationContext, T>, T> implements ExpressionFunctionTesting2<F, T, TestStorageExpressionEvaluationContext>,
    BinaryTextContextTesting,
    JsonNodeMarshallUnmarshallContextTesting,
    StorageEnvironmentContextTesting {

    @Override
    public final void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    final TestStorageExpressionEvaluationContext createContext(final Storage<TestStorageExpressionEvaluationContext> storage) {
        return new TestStorageExpressionEvaluationContext(storage);
    }

    static class TestStorageExpressionEvaluationContext extends FakeStorageExpressionEvaluationContext implements StorageContext,
        JsonNodeConverterContextDelegator,
        EnvironmentContextDelegator {

        TestStorageExpressionEvaluationContext(final Storage<TestStorageExpressionEvaluationContext> storage) {
            super();
            this.storage = storage;
        }

        // StorageContext...................................................................................................

        @Override
        public MediaType detect(final String filename,
                                final Binary content) {
            throw new UnsupportedOperationException();
        }

        @Override
        public EnvironmentValueName<?> parseEnvironmentValueName(final String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public StoragePath parseStoragePath(final String text) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final Optional<StorageValue> loadStorage(final StoragePath path) {
            return this.storage.load(
                path,
                this
            );
        }

        @Override
        public final StorageValue saveStorage(final StorageValue value) {
            return this.storage.save(
                value,
                this
            );
        }

        @Override
        public final void deleteStorage(final StoragePath path) {
            this.storage.delete(
                path,
                this
            );
        }

        @Override
        public final List<StorageValueInfo> listStorage(final StoragePath parent,
                                                        final int offset,
                                                        final int count) {
            return this.storage.list(
                parent,
                offset,
                count,
                this
            );
        }

        @Override
        public void mountStorage(final StorageMountPoint<?> mountPoint) {
            this.storage.mount(
                Cast.to(mountPoint),
                this
            );
        }

        @Override
        public void unmountStorage(final StoragePath path) {
            this.storage.unmount(
                path,
                this
            );
        }

        private final Storage<TestStorageExpressionEvaluationContext> storage;

        // JsonNodeConverterContextDelegator............................................................................

        @Override
        public JsonNodeConverterContext jsonNodeConverterContext() {
            return JsonNodeConverterContexts.basic(
                TreeConverterContexts.basic(
                    Converters.fake(),
                    EXPRESSION_NUMBER_KIND,
                    BinaryNumberConverterFunctions.fake(),
                    ConverterContexts.basic(
                        false, // canNumbersHaveGroupSeparator
                        0L, // dateTimeOffset
                        ',', // valueSeparator
                        Converters.fake(),
                        BinaryNumberConverterFunctions.fake(),
                        BINARY_TEXT_CONTEXT,
                        CurrencyLocaleContexts.fake(),
                        DateTimeContexts.fake(),
                        DecimalNumberContexts.fake()
                    )
                ),
                ENVIRONMENT_CONTEXT,
                JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT
            );
        }

        @Override
        public StorageExpressionEvaluationContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor posProcessor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public StorageExpressionEvaluationContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor preProcessor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final CanParseEnvironmentValueName canParseEnvironmentValueName() {
            return this;
        }

        // EnvironmentContextDelegator..................................................................................

        @Override
        public final TestStorageExpressionEvaluationContext cloneEnvironment() {
            throw new UnsupportedOperationException();
        }

        @Override
        public final TestStorageExpressionEvaluationContext setEnvironmentContext(final EnvironmentContext context) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> void setEnvironmentValue(final EnvironmentValueName<T> name,
                                            final T reference) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeEnvironmentValue(final EnvironmentValueName<?> name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<StoragePath> currentWorkingDirectory() {
            return OPTIONAL_CURRENT_WORKING_DIRECTORY;
        }

        @Override
        public Indentation indentation() {
            return StorageExpressionFunctionTestCase.INDENTATION;
        }

        @Override
        public LineEnding lineEnding() {
            return StorageExpressionFunctionTestCase.LINE_ENDING;
        }

        @Override
        public void setLineEnding(final LineEnding lineEnding) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setLocale(final Locale locale) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final LocalDateTime now() {
            return StorageExpressionFunctionTestCase.NOW;
        }

        @Override
        public final Optional<EmailAddress> user() {
            return OPTIONAL_USER;
        }

        @Override
        public void setUser(final Optional<EmailAddress> user) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final EnvironmentContext environmentContext() {
            return EnvironmentContexts.fake();
        }
    }
}
