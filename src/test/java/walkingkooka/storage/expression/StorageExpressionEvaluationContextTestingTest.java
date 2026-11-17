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

package walkingkooka.storage.expression;

import org.junit.jupiter.api.Test;
import walkingkooka.Binary;
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.currency.CurrencyCode;
import walkingkooka.currency.CurrencyExchange;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContextDelegator;
import walkingkooka.environment.CanParseEnvironmentValueName;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContextDelegator;
import walkingkooka.locale.LocaleLanguageTag;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.header.MediaTypeDetectors;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageContext;
import walkingkooka.storage.StorageEnvironmentContext;
import walkingkooka.storage.StorageEnvironmentContextDelegator;
import walkingkooka.storage.StorageMountPoint;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.storage.StorageWatcher;
import walkingkooka.storage.Storages;
import walkingkooka.storage.expression.StorageExpressionEvaluationContextTestingTest.TestStorageExpressionEvaluationContext;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContextDelegator;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public final class StorageExpressionEvaluationContextTestingTest implements StorageExpressionEvaluationContextTesting2<TestStorageExpressionEvaluationContext>,
    DecimalNumberContextDelegator {

    @Override
    public TestStorageExpressionEvaluationContext createContext() {
        return new TestStorageExpressionEvaluationContext();
    }

    @Override
    public void testEnterScopeGivesDifferentInstance() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEnterScopeWithNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEnvironmentContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEvaluateExpressionUnknownFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testIsPureNullNameFails() {
        throw new UnsupportedOperationException();
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
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testCurrentWorkingDirectory() {
        this.currentWorkingDirectoryAndCheck(
            this.createContext(),
            CURRENT_WORKING_DIRECTORY
        );
    }

    // DecimalNumberContext.............................................................................................

    @Override
    public int decimalNumberDigitCount() {
        return DECIMAL_NUMBER_CONTEXT.decimalNumberDigitCount();
    }

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return DECIMAL_NUMBER_CONTEXT;
    }

    @Override
    public MathContext mathContext() {
        return DECIMAL_NUMBER_CONTEXT.mathContext();
    }

    // class............................................................................................................

    @Override
    public Class<TestStorageExpressionEvaluationContext> type() {
        return TestStorageExpressionEvaluationContext.class;
    }

    static final class TestStorageExpressionEvaluationContext implements StorageExpressionEvaluationContext,
        DateTimeContextDelegator,
        DecimalNumberContextDelegator,
        StorageEnvironmentContextDelegator,
        LocaleContextDelegator,
        JsonNodeMarshallUnmarshallContextDelegator {

        @Override
        public boolean isText(final Object value) {
            return false;
        }

        @Override
        public CaseSensitivity stringEqualsCaseSensitivity() {
            return CaseSensitivity.SENSITIVE;
        }

        @Override
        public Object evaluate(final String expression) {
            Objects.requireNonNull(expression, "expression");
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Optional<Object>> reference(final ExpressionReference reference) {
            Objects.requireNonNull(reference, "reference");
            throw new UnsupportedOperationException();
        }

        @Override
        public Object handleException(final RuntimeException thrown) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T prepareParameter(final ExpressionFunctionParameter<T> parameter,
                                      final Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ExpressionEvaluationContext enterScope(final Function<ExpressionReference, Optional<Optional<Object>>> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isPure(final ExpressionFunctionName functionName) {
            return false;
        }

        @Override
        public ExpressionFunction<?, ExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName functionName) {
            Objects.requireNonNull(functionName, "functionName");
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<CurrencyExchange> currencyExchanges() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Number> currencyExchangeRate(final CurrencyExchange currencyExchange,
                                                     final Optional<LocalDateTime> dateTime) {
            Objects.requireNonNull(currencyExchange, "currencyExchange");
            Objects.requireNonNull(dateTime, "dateTime");

            throw new UnsupportedOperationException();
        }

        @Override
        public <N extends Number> N multiply(final Number left,
                                             final Number right,
                                             final Class<N> type) {
            Objects.requireNonNull(left, "left");
            Objects.requireNonNull(right, "right");
            Objects.requireNonNull(type, "type");

            throw new UnsupportedOperationException();
        }

        @Override
        public DateTimeContext dateTimeContext() {
            return DATE_TIME_CONTEXT;
        }

        @Override
        public MathContext mathContext() {
            return this.decimalNumberContext()
                .mathContext();
        }

        @Override
        public DecimalNumberContext decimalNumberContext() {
            return DECIMAL_NUMBER_CONTEXT;
        }

        @Override
        public LocaleContext localeContext() {
            return LOCALE_CONTEXT;
        }

        @Override
        public Locale locale() {
            return this.storageEnvironmentContext.locale();
        }

        @Override
        public void setLocale(final Locale locale) {
            this.storageEnvironmentContext.setLocale(locale);
        }

        @Override
        public LocalDateTime now() {
            return this.storageEnvironmentContext.now();
        }

        @Override
        public StorageExpressionEvaluationContext cloneEnvironment() {
            return new TestStorageExpressionEvaluationContext();
        }

        @Override
        public TestStorageExpressionEvaluationContext setEnvironmentContext(final EnvironmentContext environmentContext) {
            Objects.requireNonNull(environmentContext, "environmentContext");

            return new TestStorageExpressionEvaluationContext();
        }

        @Override
        public CanParseEnvironmentValueName canParseEnvironmentValueName() {
            return StorageEnvironmentContextDelegator.super.canParseEnvironmentValueName();
        }

        @Override
        public StorageEnvironmentContext storageEnvironmentContext() {
            return this.storageEnvironmentContext;
        }

        private final StorageEnvironmentContext storageEnvironmentContext = STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment();
        
        // StorageContext...............................................................................................
        
        @Override
        public boolean canReadStorage(final StoragePath path) {
            return this.storage.canRead(
                path,
                this
            );
        }

        @Override
        public boolean canWriteStorage(final StoragePath path) {
            return this.storage.canWrite(
                path,
                this
            );
        }

        @Override
        public Optional<StorageValue> loadStorage(final StoragePath path) {
            return this.storage.load(
                path,
                this
            );
        }

        @Override
        public StorageValue saveStorage(final StorageValue value) {
            return this.storage.save(
                value,
                this
            );
        }

        @Override
        public void deleteStorage(final StoragePath path) {
            this.storage.delete(
                path,
                this
            );
        }

        @Override
        public List<StorageValueInfo> listStorage(final StoragePath parent,
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
        public void setAuditInfoStorage(final StorageValueInfo info) {
            this.storage.setAuditInfo(
                info,
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

        @Override
        public List<StorageMountPoint<?>> storageMountPoints() {
            return Cast.to(
                this.storage.mountPoints()
            );
        }

        @Override
        public Runnable addStorageWatcher(final StorageWatcher watcher) {
            return this.storage.addWatcher(
                watcher,
                this
            );
        }

        @Override
        public Runnable addStorageWatcherOnce(final StorageWatcher watcher) {
            return this.storage.addWatcherOnce(
                watcher,
                this
            );
        }

        private final Storage<StorageContext> storage = Storages.treeMapStore();

        @Override
        public MediaType detect(final String filename,
                                final Binary content) {
            return MediaTypeDetectors.binary()
                .detect(
                    filename,
                    content
                );
        }

        @Override
        public <T> Either<T, String> convert(final Object value,
                                             final Class<T> type) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean canConvert(final Object value,
                                  final Class<?> type) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Currency> currencyForCurrencyCode(final CurrencyCode currencyCode) {
            Objects.requireNonNull(currencyCode, "currencyCode");
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Currency> currencyForLocale(final Locale locale) {
            Objects.requireNonNull(locale, "locale");
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean canNumbersHaveGroupSeparator() {
            return false;
        }

        @Override
        public long dateOffset() {
            return 0;
        }

        @Override
        public ExpressionNumberKind expressionNumberKind() {
            return ExpressionNumberKind.BIG_DECIMAL;
        }

        @Override
        public StoragePath parseStoragePath(final String text) {
            return StoragePath.parseSpecial(
                text,
                this
            );
        }

        @Override
        public char valueSeparator() {
            return ',';
        }

        @Override
        public JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext() {
            return JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT;
        }

        @Override
        public StorageExpressionEvaluationContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
            Objects.requireNonNull(processor, "processor");
            return this;
        }

        @Override
        public StorageExpressionEvaluationContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
            Objects.requireNonNull(processor, "processor");
            return this;
        }

        @Override
        public Optional<Locale> localeForLanguageTag(final LocaleLanguageTag languageTag) {
            Objects.requireNonNull(languageTag, "languageTag");
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
