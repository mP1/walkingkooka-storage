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
import walkingkooka.collect.list.Lists;
import walkingkooka.environment.AuditInfo;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.EnvironmentValueNameAndValue;
import walkingkooka.environment.EnvironmentWatcher;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A {@link Storage} that translates {@link StoragePath#name()} for paths with the root as their parent into
 * {@link EnvironmentValueName} and gets/sets/remove values from the {@link StorageContext}, using the environment methods.
 * Paths with one or more path components will fail with a {@link InvalidStoragePathException}.
 * <pre>
 * /path/LineEnding
 * </pre>
 */
final class StorageSharedEnvironment<C extends StorageContext> extends StorageShared<C> {

    /**
     * Type safe getter
     */
    static <C extends StorageContext> StorageSharedEnvironment<C> instance() {
        return INSTANCE;
    }

    /**
     * Singleton instance
     */
    private final static StorageSharedEnvironment INSTANCE = new StorageSharedEnvironment<>();

    private StorageSharedEnvironment() {
        super();
    }

    // Storage..........................................................................................................

    @Override
    boolean canRead0(final StoragePath storagePath,
                     final C context) {
        return this.load(
            storagePath,
            context
        ).isPresent();
    }

    @Override
    boolean canWrite0(final StoragePath path,
                      final C context) {
        environmentValueName(path);

        return true;
    }

    @Override
    Optional<StorageValue> load0(final StoragePath path,
                                 final C context) {
        Optional<Object> value = Optional.empty();

        if (isParentRoot(path)) {
            try {
                final EnvironmentValueName<?> environmentValueName = environmentValueName(path);
                value = Cast.to(
                    context.environmentValue(environmentValueName)
                );
            } catch (final IllegalArgumentException cause) {
                // ignore
            }
        }

        return value.map(v -> StorageValue.with(path)
            .setValue(
                Optional.of(v)
            )
        );
    }

    @Override
    StorageValue save0(final StorageValue value,
                       final C context) {
        final StoragePath path = value.path();
        final EnvironmentValueName<?> environmentValueName = environmentValueName(path);

        context.setOrRemoveEnvironmentValue(
            environmentValueName,
            Cast.to(
                value.value()
            )
        );

        return StorageValue.with(path)
            .setValue(
                Cast.to(
                    context.environmentValue(environmentValueName)
                )
            );
    }

    @Override
    void delete0(final StoragePath path,
                 final C context) {
        context.removeEnvironmentValue(
            environmentValueName(path)
        );
    }

    private static EnvironmentValueName<?> environmentValueName(final StoragePath path) {
        if (false == isParentRoot(path)) {
            throw path.invalidStoragePathException("Invalid path");
        }
        return EnvironmentValueName.with(
            path.name()
                .value(),
            Object.class
        );
    }

    @Override
    List<StorageValueInfo> list0(final StoragePath parent,
                                 final int offset,
                                 final int count,
                                 final C context) {
        final List<StorageValueInfo> listing;

        if (parent.isRoot() || isParentRoot(parent)) {
            String prefix = parent.value();
            if (prefix.startsWith(StoragePath.SEPARATOR_STRING)) {
                prefix = prefix.substring(
                    StoragePath.SEPARATOR_STRING.length()
                );
            }

            final String finalPrefix = prefix;

            final AuditInfo auditInfo = context.createdAuditInfo();

            // always returns nothing
            listing = context.environmentValueNames()
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
        } else {
            listing = Lists.empty();
        }

        return listing;
    }

    private static boolean isParentRoot(final StoragePath path) {
        return StoragePath.ROOT.equals(
            path.parent()
                .orElse(null)
        );
    }

    // addWatcher................................................................................................

    @Override
    Runnable addWatcher0(final StorageWatcher watcher,
                         final C context) {
        return context.addEnvironmentWatcher(
            toEnvironmentWatcher(watcher)
        );
    }

    @Override
    Runnable addWatcherOnce0(final StorageWatcher watcher,
                             final C context) {
        return context.addEnvironmentWatcherOnce(
            toEnvironmentWatcher(watcher)
        );
    }

    private static EnvironmentWatcher toEnvironmentWatcher(final StorageWatcher watcher) {
        return new EnvironmentWatcher() {
            @Override
            public void onValueChange(final Optional<EnvironmentValueNameAndValue<?>> oldValue,
                                      final Optional<EnvironmentValueNameAndValue<?>> newValue) {
                watcher.onValueChange(
                    toStorageValue(oldValue),
                    toStorageValue(newValue)
                );
            }
        };
    }

    private static Optional<StorageValue> toStorageValue(final Optional<EnvironmentValueNameAndValue<?>> environmentValueNameAndValue) {
        return environmentValueNameAndValue.map(
            (EnvironmentValueNameAndValue<?> e) -> StorageValue.with(
                StoragePath.ROOT.append(
                    StorageName.with(
                        e.name()
                            .value()
                    )
                )
            ).setValue(
                Optional.of(
                    e.value()
                )
            )
        );
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return StorageSharedEnvironment.class.getSimpleName();
    }
}
