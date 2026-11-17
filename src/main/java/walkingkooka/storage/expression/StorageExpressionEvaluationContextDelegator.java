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

import walkingkooka.Binary;
import walkingkooka.currency.CurrencyCode;
import walkingkooka.environment.CanParseEnvironmentValueName;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.expression.EnvironmentExpressionEvaluationContext;
import walkingkooka.environment.expression.EnvironmentExpressionEvaluationContextDelegator;
import walkingkooka.locale.LocaleLanguageTag;
import walkingkooka.logging.LoggingContext;
import walkingkooka.net.header.MediaType;
import walkingkooka.storage.StorageEnvironmentContext;
import walkingkooka.storage.StorageEnvironmentContextDelegator;
import walkingkooka.storage.StorageMountPoint;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.storage.StorageWatcher;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContextDelegator;

import java.math.MathContext;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface StorageExpressionEvaluationContextDelegator extends StorageExpressionEvaluationContext,
    EnvironmentExpressionEvaluationContextDelegator,
    JsonNodeMarshallUnmarshallContextDelegator,
    StorageEnvironmentContextDelegator {

    @Override
    default Optional<Currency> currencyForCurrencyCode(final CurrencyCode currencyCode) {
        return this.storageExpressionEvaluationContext()
            .currencyForCurrencyCode(currencyCode);
    }

    @Override
    default ExpressionNumberKind expressionNumberKind() {
        return this.storageExpressionEvaluationContext()
            .expressionNumberKind();
    }

    @Override
    default MathContext mathContext() {
        return this.storageExpressionEvaluationContext()
            .mathContext();
    }

    @Override
    default StoragePath parseStoragePath(final String text) {
        return this.storageExpressionEvaluationContext()
            .parseStoragePath(text);
    }

    // CanParseEnvironmentValueNameDelegator............................................................................

    @Override
    default CanParseEnvironmentValueName canParseEnvironmentValueName() {
        return this.environmentContext();
    }

    // EnvironmentExpressionEvaluationContextDelegator..................................................................

    @Override
    default EnvironmentExpressionEvaluationContext environmentExpressionEvaluationContext() {
        return this.storageExpressionEvaluationContext();
    }

    // JsonNodeMarshallUnmarshallContextDelegator.......................................................................

    @Override
    default Optional<Locale> localeForLanguageTag(final LocaleLanguageTag languageTag) {
        return this.storageExpressionEvaluationContext()
            .localeForLanguageTag(languageTag);
    }

    @Override
    default JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext() {
        return this.storageExpressionEvaluationContext();
    }

    // StorageEnvironmentContextDelegator...............................................................................

    @Override
    default StorageEnvironmentContext storageEnvironmentContext() {
        return this.storageExpressionEvaluationContext();
    }

    @Override
    default EnvironmentContext environmentContext() {
        return this.storageEnvironmentContext();
    }

    @Override
    default Charset charset() {
        return this.environmentContext()
            .charset();
    }

    @Override
    default CurrencyCode currencyCode() {
        return this.environmentContext()
            .currencyCode();
    }

    @Override
    default Indentation indentation() {
        return this.environmentContext()
            .indentation();
    }

    @Override
    default LineEnding lineEnding() {
        return this.environmentContext()
            .lineEnding();
    }

    @Override
    default Locale locale() {
        return this.environmentContext()
            .locale();
    }

    @Override
    default void setLocale(final Locale locale) {
        this.environmentContext()
            .setLocale(locale);
    }

    @Override
    default LocalDateTime now() {
        return this.environmentContext()
            .now();
    }

    // StorageExpressionEvaluationContext...............................................................................

    @Override
    default MediaType detect(final String filename,
                             final Binary content) {
        return this.storageExpressionEvaluationContext()
            .detect(
                filename,
                content
            );
    }

    @Override
    default boolean canReadStorage(final StoragePath path) {
        return this.storageExpressionEvaluationContext()
            .canReadStorage(path);
    }

    @Override
    default boolean canWriteStorage(final StoragePath path) {
        return this.storageExpressionEvaluationContext()
            .canWriteStorage(path);
    }

    @Override
    default Optional<StorageValue> loadStorage(final StoragePath path) {
        return this.storageExpressionEvaluationContext()
            .loadStorage(path);
    }

    @Override
    default StorageValue saveStorage(final StorageValue value) {
        return this.storageExpressionEvaluationContext()
            .saveStorage(value);
    }

    @Override
    default void deleteStorage(final StoragePath path) {
        this.storageExpressionEvaluationContext()
            .deleteStorage(path);
    }

    @Override
    default List<StorageValueInfo> listStorage(final StoragePath parent,
                                               final int offset,
                                               final int count) {
        return this.storageExpressionEvaluationContext()
            .listStorage(
                parent,
                offset,
                count
            );
    }

    @Override
    default void setAuditInfoStorage(final StorageValueInfo info) {
        this.storageExpressionEvaluationContext()
            .setAuditInfoStorage(info);
    }

    @Override
    default void mountStorage(final StorageMountPoint<?> mountPoint) {
        this.storageExpressionEvaluationContext()
            .mountStorage(mountPoint);
    }

    @Override
    default void unmountStorage(final StoragePath path) {
        this.storageExpressionEvaluationContext()
            .unmountStorage(path);
    }

    @Override
    default List<StorageMountPoint<?>> storageMountPoints() {
        return this.storageExpressionEvaluationContext()
            .storageMountPoints();
    }

    @Override
    default Runnable addStorageWatcher(final StorageWatcher watcher) {
        return this.storageExpressionEvaluationContext()
            .addStorageWatcher(watcher);
    }

    @Override
    default Runnable addStorageWatcherOnce(final StorageWatcher watcher) {
        return this.storageExpressionEvaluationContext()
            .addStorageWatcherOnce(watcher);
    }

    @Override
    default LoggingContext loggingContext() {
        return this.storageExpressionEvaluationContext();
    }

    StorageExpressionEvaluationContext storageExpressionEvaluationContext();
}
