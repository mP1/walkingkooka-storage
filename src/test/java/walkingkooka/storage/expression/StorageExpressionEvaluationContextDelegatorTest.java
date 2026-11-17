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
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.EnvironmentWatcher;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContextDelegator;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.locale.LocaleLanguageTag;
import walkingkooka.logging.LoggerPath;
import walkingkooka.logging.LoggingLevel;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.header.MediaTypeDetectors;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageContext;
import walkingkooka.storage.StorageEnvironmentContext;
import walkingkooka.storage.StorageMountPoint;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.storage.StorageWatcher;
import walkingkooka.storage.Storages;
import walkingkooka.storage.expression.StorageExpressionEvaluationContextDelegatorTest.TestStorageExpressionEvaluationContextDelegator;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
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
import java.nio.charset.Charset;
import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public final class StorageExpressionEvaluationContextDelegatorTest implements StorageExpressionEvaluationContextTesting2<TestStorageExpressionEvaluationContextDelegator>,
    DecimalNumberContextDelegator {

    @Test
    public void testHomeDirectory() {
        this.homeDirectoryAndCheck(
            this.createContext(),
            HOME_DIRECTORY
        );
    }

    @Override
    public TestStorageExpressionEvaluationContextDelegator createContext() {
        return new TestStorageExpressionEvaluationContextDelegator();
    }

    @Override
    public void testEnterScopeGivesDifferentInstance() {
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

    // HasEnvironmentContext............................................................................................

    @Test
    @Override
    public void testEnvironmentContext() {
        final TestStorageExpressionEvaluationContextDelegator context = this.createContext();

        this.environmentContextAndCheck(
            context,
            context.storageExpressionEvaluationContext
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

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = DecimalNumberContexts.american(MathContext.DECIMAL64);

    // class............................................................................................................

    @Override
    public Class<TestStorageExpressionEvaluationContextDelegator> type() {
        return TestStorageExpressionEvaluationContextDelegator.class;
    }

    @Override
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    static class TestStorageExpressionEvaluationContextDelegator implements StorageExpressionEvaluationContextDelegator {

        @Override
        public ExpressionEvaluationContext enterScope(final Function<ExpressionReference, Optional<Optional<Object>>> function) {
            Objects.requireNonNull(function, "function");
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Optional<Object>> reference(final ExpressionReference expressionReference) {
            Objects.requireNonNull(expressionReference, "expressionReference");
            throw new UnsupportedOperationException();
        }

        @Override
        public StorageExpressionEvaluationContext cloneEnvironment() {
            throw new UnsupportedOperationException();
        }

        @Override
        public StorageExpressionEvaluationContext setEnvironmentContext(final EnvironmentContext environmentContext) {
            Objects.requireNonNull(environmentContext, "environmentContext");

            return new TestStorageExpressionEvaluationContextDelegator();
        }

        @Override
        public StorageExpressionEvaluationContext storageExpressionEvaluationContext() {
            return this.storageExpressionEvaluationContext;
        }

        private final TestStorageExpressionEvaluationContext storageExpressionEvaluationContext = new TestStorageExpressionEvaluationContext();

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
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }

    static final class TestStorageExpressionEvaluationContext implements StorageExpressionEvaluationContext,
        DateTimeContextDelegator,
        DecimalNumberContextDelegator,
        JsonNodeMarshallUnmarshallContextDelegator,
        LocaleContextDelegator {

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
            return DateTimeContexts.basic(
                DateTimeSymbols.fromDateFormatSymbols(
                    new DateFormatSymbols(StorageExpressionEvaluationContextDelegatorTest.LOCALE)
                ),
                StorageExpressionEvaluationContextDelegatorTest.LOCALE,
                1950,
                50,
                LocalDateTime::now
            );
        }

        @Override
        public DecimalNumberContext decimalNumberContext() {
            return DECIMAL_NUMBER_CONTEXT;
        }

        @Override
        public MathContext mathContext() {
            return DECIMAL_NUMBER_CONTEXT.mathContext();
        }

        @Override
        public Charset charset() {
            return this.storageEnvironmentContext.charset();
        }

        @Override
        public void setCharset(final Charset charset) {
            this.storageEnvironmentContext.setCharset(charset);
        }
        
        @Override
        public Currency currency() {
            return this.storageEnvironmentContext.currency();
        }

        @Override
        public void setCurrency(final Currency currency) {
            this.storageEnvironmentContext.setCurrency(currency);
        }
        
        @Override
        public Optional<StoragePath> currentWorkingDirectory() {
            return this.storageEnvironmentContext.currentWorkingDirectory();
        }

        @Override
        public void setCurrentWorkingDirectory(final Optional<StoragePath> currentWorkingDirectory) {
            this.storageEnvironmentContext.setCurrentWorkingDirectory(currentWorkingDirectory);
        }

        @Override
        public Optional<StoragePath> homeDirectory() {
            return this.storageEnvironmentContext.homeDirectory();
        }

        @Override
        public void setHomeDirectory(final Optional<StoragePath> homeDirectory) {
            this.storageEnvironmentContext.setHomeDirectory(homeDirectory);
        }

        @Override
        public LineEnding lineEnding() {
            return this.storageEnvironmentContext.lineEnding();
        }

        @Override
        public void setLineEnding(final LineEnding lineEnding) {
            this.storageEnvironmentContext.setLineEnding(lineEnding);
        }

        @Override
        public LocaleContext localeContext() {
            return LocaleContexts.jre(StorageExpressionEvaluationContextDelegatorTest.LOCALE);
        }

        @Override
        public Indentation indentation() {
            return this.storageEnvironmentContext.indentation();
        }

        @Override
        public void setIndentation(final Indentation indentation) {
            this.storageEnvironmentContext.setIndentation(indentation);
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
        public LoggingLevel loggingLevel() {
            return this.storageEnvironmentContext.loggingLevel();
        }

        @Override
        public void setLoggingLevel(final LoggingLevel loggingLevel) {
            this.storageEnvironmentContext.setLoggingLevel(loggingLevel);
        }

        @Override
        public LocalDateTime now() {
            return this.storageEnvironmentContext.now();
        }

        @Override
        public ZoneOffset timeOffset() {
            return this.storageEnvironmentContext.timeOffset();
        }

        @Override
        public void setTimeOffset(final ZoneOffset timeOffset) {
            this.storageEnvironmentContext.setTimeOffset(timeOffset);
        }

        @Override
        public Optional<EmailAddress> user() {
            return this.storageEnvironmentContext.user();
        }

        @Override
        public void setUser(final Optional<EmailAddress> user) {
            this.storageEnvironmentContext.setUser(user);
        }

        @Override
        public StorageExpressionEvaluationContext cloneEnvironment() {
            return new StorageExpressionEvaluationContextTestingTest.TestStorageExpressionEvaluationContext();
        }

        @Override
        public StorageExpressionEvaluationContextTestingTest.TestStorageExpressionEvaluationContext setEnvironmentContext(final EnvironmentContext environmentContext) {
            Objects.requireNonNull(environmentContext, "environmentContext");

            return new StorageExpressionEvaluationContextTestingTest.TestStorageExpressionEvaluationContext();
        }

        @Override
        public <T> Optional<T> environmentValue(final EnvironmentValueName<T> environmentValueName) {
            return this.storageEnvironmentContext.environmentValue(environmentValueName);
        }

        @Override
        public Set<EnvironmentValueName<?>> environmentValueNames() {
            return this.storageEnvironmentContext.environmentValueNames();
        }

        @Override
        public <T> void setEnvironmentValue(final EnvironmentValueName<T> name,
                                            final T value) {
            this.storageEnvironmentContext.setEnvironmentValue(
                name,
                value
            );
        }

        @Override
        public void removeEnvironmentValue(final EnvironmentValueName<?> name) {
            this.storageEnvironmentContext.removeEnvironmentValue(name);
        }

        private final StorageEnvironmentContext storageEnvironmentContext = STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment();

        @Override
        public Runnable addEnvironmentWatcher(final EnvironmentWatcher watcher) {
            return this.storageEnvironmentContext.addEnvironmentWatcher(watcher);
        }

        @Override
        public Runnable addEnvironmentWatcherOnce(final EnvironmentWatcher watcher) {
            return this.storageEnvironmentContext.addEnvironmentWatcherOnce(watcher);
        }

        @Override
        public EnvironmentValueName<?> parseEnvironmentValueName(final String name) {
            return this.storageEnvironmentContext.parseEnvironmentValueName(name);
        }

        @Override
        public void logEnter(final LoggerPath logger) {
            this.storageEnvironmentContext.logEnter(logger);
        }

        @Override
        public void logExit() {
            this.storageEnvironmentContext.logExit();
        }

        @Override
        public void debug(final String message) {
            this.storageEnvironmentContext.debug(message);
        }

        @Override
        public void debug(final String message,
                           final Throwable throwable) {
            this.storageEnvironmentContext.debug(
                    message,
                    throwable
                );
        }

        @Override
        public void info(final String message) {
            this.storageEnvironmentContext.info(message);
        }

        @Override
        public void info(final String message,
                          final Throwable throwable) {
            this.storageEnvironmentContext.info(
                    message,
                    throwable
                );
        }

        @Override
        public void warn(final String message) {
            this.storageEnvironmentContext.warn(message);
        }

        @Override
        public void warn(final String message,
                          final Throwable throwable) {
            this.storageEnvironmentContext.warn(
                    message,
                    throwable
                );
        }

        @Override
        public void error(final String message) {
            this.storageEnvironmentContext.error(message);
        }

        @Override
        public void error(final String message,
                           final Throwable throwable) {
            this.storageEnvironmentContext.error(
                    message,
                    throwable
                );
        }

        @Override
        public void log(final LoggingLevel level,
                         final String message) {
            this.storageEnvironmentContext.log(
                    level,
                    message
                );
        }

        @Override
        public void log(final LoggingLevel level,
                         final String message,
                         final Throwable throwable) {
            this.storageEnvironmentContext.log(
                    level,
                    message,
                    throwable
                );
        }

        @Override
        public boolean isDebugEnabled() {
            return this.storageEnvironmentContext.isDebugEnabled();
        }

        @Override
        public boolean isInfoEnabled() {
            return this.storageEnvironmentContext.isInfoEnabled();
        }

        @Override
        public boolean isWarnEnabled() {
            return this.storageEnvironmentContext.isWarnEnabled();
        }

        @Override
        public boolean isErrorEnabled() {
            return this.storageEnvironmentContext.isErrorEnabled();
        }

        @Override
        public boolean isNoneEnabled() {
            return this.storageEnvironmentContext.isNoneEnabled();
        }

        @Override
        public boolean isLoggingEnabled(final LoggingLevel level) {
            return this.storageEnvironmentContext.isLoggingEnabled(level);
        }

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
        public JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext() {
            return JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT;
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
