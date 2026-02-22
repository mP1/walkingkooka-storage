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

package walkingkooka.storage;

import walkingkooka.Cast;
import walkingkooka.environment.AuditInfo;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.store.Store;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A {@link Storage} that translates {@link StoragePath#name()} into {@link EnvironmentValueName} and gets/sets/remove
 * values from the {@link StorageContext}, using the environment methods.
 */
final class EnvironmentStorage<C extends StorageContext> implements Storage<C> {

    /**
     * Type safe getter
     */
    static <C extends StorageContext> EnvironmentStorage<C> instance() {
        return INSTANCE;
    }

    /**
     * Singleton instance
     */
    private final static EnvironmentStorage INSTANCE = new EnvironmentStorage();

    private EnvironmentStorage() {
        super();
    }

    // Storage..........................................................................................................

    @Override
    public Optional<StorageValue> load(final StoragePath path,
                                       final C context) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(context, "context");

        Optional<Object> value;
        try {
            final EnvironmentValueName<?> environmentValueName = environmentValueName(path);
            value = Cast.to(
                context.environmentValue(environmentValueName)
            );
        } catch (final IllegalArgumentException cause) {
            value = Optional.empty();
        }

        return value.map(v -> StorageValue.with(
                path,
                Optional.of(v)
            )
        );
    }

    @Override
    public StorageValue save(final StorageValue value,
                             final C context) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(context, "context");

        final StoragePath path = value.path();
        final EnvironmentValueName<?> environmentValueName = environmentValueName(path);

        context.setOrRemoveEnvironmentValue(
            environmentValueName,
            Cast.to(
                value.value()
            )
        );

        return StorageValue.with(
            path,
            Cast.to(
                context.environmentValue(environmentValueName)
            )
        );
    }

    @Override
    public void delete(final StoragePath path,
                       final C context) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(context, "context");

        try {
            context.removeEnvironmentValue(
                this.environmentValueName(path)
            );
        } catch (final IllegalArgumentException cause) {
            // invalid EnvironmentValueName do nothing
        }
    }

    private static EnvironmentValueName<?> environmentValueName(final StoragePath path) {
        return EnvironmentValueName.with(
            path.name()
                .value(),
            Object.class
        );
    }

    @Override
    public List<StorageValueInfo> list(final StoragePath parent,
                                       final int offset,
                                       final int count,
                                       final C context) {
        Objects.requireNonNull(parent, "parent");
        Store.checkOffsetAndCount(offset, count);
        Objects.requireNonNull(context, "context");

        String prefix = parent.value();
        if (prefix.startsWith(StoragePath.SEPARATOR_STRING)) {
            prefix = prefix.substring(
                StoragePath.SEPARATOR_STRING.length()
            );
        }

        final String finalPrefix = prefix;

        final AuditInfo auditInfo = context.createdAuditInfo();

        // always returns nothing
        return context.environmentValueNames()
            .stream()
            .filter(n -> EnvironmentValueName.CASE_SENSITIVITY.startsWith(n.value(), finalPrefix))
            .skip(offset)
            .limit(count)
            .map(n ->
                StorageValueInfo.with(
                    StoragePath.ROOT.append(
                        StorageName.with(
                            n.value()
                        )
                    ),
                    auditInfo
                )
            ).collect(Collectors.toList());
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return EnvironmentStorage.class.getSimpleName();
    }
}
