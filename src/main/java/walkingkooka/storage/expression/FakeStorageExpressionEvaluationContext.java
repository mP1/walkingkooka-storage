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
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.EnvironmentWatcher;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.header.MediaType;
import walkingkooka.storage.StorageMountPoint;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.storage.StorageWatcher;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonString;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.nio.charset.Charset;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class FakeStorageExpressionEvaluationContext extends FakeExpressionEvaluationContext implements StorageExpressionEvaluationContext {

    public FakeStorageExpressionEvaluationContext() {
        super();
    }

    // StorageConverterContext..........................................................................................

    @Override
    public MediaType detect(final String filename,
                            final Binary content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<StoragePath> currentWorkingDirectory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCurrentWorkingDirectory(final Optional<StoragePath> currentWorkingDirectory) {
        Objects.requireNonNull(currentWorkingDirectory, "currentWorkingDirectory");
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<StoragePath> homeDirectory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setHomeDirectory(final Optional<StoragePath> homeDirectory) {
        Objects.requireNonNull(homeDirectory, "homeDirectory");
        throw new UnsupportedOperationException();
    }

    @Override
    public StoragePath parseStoragePath(final String text) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unmarshall(final JsonNode jsonNode,
                            final Class<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Enum<T>> Set<T> unmarshallEnumSet(final JsonNode jsonNode,
                                                        final Class<T> type,
                                                        final Function<String, T> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Optional<T> unmarshallOptional(final JsonNode jsonNode,
                                              final Class<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Optional<T> unmarshallOptionalWithType(final JsonNode jsonNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<T> unmarshallList(final JsonNode jsonNode,
                                      final Class<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Set<T> unmarshallSet(final JsonNode jsonNode,
                                    final Class<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K, V> Map<K, V> unmarshallMap(final JsonNode jsonNode,
                                          final Class<K> key,
                                          final Class<V> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unmarshallWithType(final JsonNode jsonNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<T> unmarshallListWithType(final JsonNode jsonNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Set<T> unmarshallSetWithType(final JsonNode jsonNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K, V> Map<K, V> unmarshallMapWithType(final JsonNode jsonNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonNode marshall(final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonNode marshallEnumSet(final Set<? extends Enum<?>> set) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonNode marshallWithType(final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonNode marshallOptional(final Optional<?> optional) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonNode marshallOptionalWithType(final Optional<?> optional) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonNode marshallCollection(final Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonNode marshallMap(final Map<?, ?> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonNode marshallCollectionWithType(final Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonNode marshallMapWithType(final Map<?, ?> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Class<?>> registeredType(final JsonString jsonString) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<JsonString> typeName(final Class<?> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StorageExpressionEvaluationContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StorageExpressionEvaluationContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        throw new UnsupportedOperationException();
    }

    // StorageExpressionEvaluationContext...............................................................................

    @Override
    public boolean canReadStorage(final StoragePath path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canWriteStorage(final StoragePath path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<StorageValue> loadStorage(final StoragePath path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StorageValue saveStorage(final StorageValue value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteStorage(final StoragePath path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<StorageValueInfo> listStorage(final StoragePath parent,
                                              final int offset,
                                              final int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAuditInfoStorage(final StorageValueInfo info) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void mountStorage(final StorageMountPoint<?> mountPoint) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unmountStorage(final StoragePath path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<StorageMountPoint<?>> storageMountPoints() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addStorageWatcher(final StorageWatcher watcher) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addStorageWatcherOnce(final StorageWatcher watcher) {
        throw new UnsupportedOperationException();
    }

    // StorageContext...................................................................................................

    @Override
    public StorageExpressionEvaluationContext cloneEnvironment() {
        throw new UnsupportedOperationException();
    }

    @Override
    public StorageExpressionEvaluationContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        throw new UnsupportedOperationException();
    }

    @Override 
    public Currency currency() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<EnvironmentValueName<?>> environmentValueNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void setEnvironmentValue(final EnvironmentValueName<T> name,
                                        final T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeEnvironmentValue(final EnvironmentValueName<?> environmentValueName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCharset(final Charset charset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCurrency(final Currency currency) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setIndentation(final Indentation indentation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLineEnding(final LineEnding lineEnding) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ZoneOffset timeOffset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTimeOffset(final ZoneOffset zoneOffset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<EmailAddress> user() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUser(final Optional<EmailAddress> optional) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addEnvironmentWatcher(final EnvironmentWatcher environmentWatcher) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addEnvironmentWatcherOnce(final EnvironmentWatcher environmentWatcher) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EnvironmentValueName<?> parseEnvironmentValueName(final String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Optional<T> environmentValue(final EnvironmentValueName<T> environmentValueName) {
        throw new UnsupportedOperationException();
    }
}
