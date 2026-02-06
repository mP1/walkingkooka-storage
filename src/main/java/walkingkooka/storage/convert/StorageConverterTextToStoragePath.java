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

import walkingkooka.Cast;
import walkingkooka.convert.Converter;
import walkingkooka.convert.TextToTryingShortCircuitingConverter;
import walkingkooka.storage.StoragePath;

/**
 * A {@link Converter} that converts {@link String} to {@link StoragePath}.<br>
 * If a relative path is given the {@link StorageConverterContext#currentWorkingDirectory()} is prepended.
 */
final class StorageConverterTextToStoragePath<C extends StorageConverterContext> extends StorageConverter<C>
    implements TextToTryingShortCircuitingConverter<C> {

    /**
     * Type safe getter.
     */
    static <C extends StorageConverterContext> StorageConverterTextToStoragePath<C> instance() {
        return Cast.to(INSTANCE);
    }

    private final static StorageConverterTextToStoragePath<?> INSTANCE = new StorageConverterTextToStoragePath<>();

    private StorageConverterTextToStoragePath() {
        super();
    }

    @Override
    public boolean isTargetType(final Object value,
                                final Class<?> type,
                                final C context) {
        return StoragePath.class == type;
    }

    @Override
    public Object parseText(final String text,
                            final Class<?> type,
                            final C context) {
        return StoragePath.parseMaybeRelative(
            text,
            context.currentWorkingDirectory()
        );
    }

    @Override
    public String toString() {
        return "String -> StoragePath";
    }
}
