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

import walkingkooka.Either;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ShortCircuitingConverter;
import walkingkooka.io.FileExtension;
import walkingkooka.storage.StoragePath;

/**
 * A templated {@link Converter} where the file extension test and target type are abstract and implemented by sub-classes.
 */
abstract class StorageConverterStoragePathToClass<C extends StorageConverterContext> extends StorageConverter<C>
    implements ShortCircuitingConverter<C> {

    StorageConverterStoragePathToClass() {
        super();
    }

    @Override
    public final boolean canConvert(final Object value,
                              final Class<?> type,
                              final C c) {
        boolean canConvert = false;

        if (value instanceof StoragePath &&
            Class.class == type) {
            final StoragePath storagePath = (StoragePath) value;
            final FileExtension fileExtensionOrNull = storagePath.name()
                .fileExtension()
                .orElse(null);

            canConvert = this.testFileExtension(fileExtensionOrNull);
        }

        return canConvert;
    }

    abstract boolean testFileExtension(final FileExtension fileExtension);

    @Override
    public final <T> Either<T, String> doConvert(final Object value,
                                                 final Class<T> type,
                                                 final C context) {
        return this.successfulConversion(
            this.type(),
            type
        );
    }

    abstract Class<?> type();

    // class............................................................................................................

    @Override
    public abstract String toString();
}
