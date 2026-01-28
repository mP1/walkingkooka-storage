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

import walkingkooka.convert.ConverterContext;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.storage.StoragePath;

import java.util.Optional;

/**
 * Factories for Storage {@link StorageConverterContext}.
 */
public final class StorageConverterContexts implements PublicStaticHelper {

    /**
     * {@see BasicStorageConverterContext}
     */
    public static StorageConverterContext basic(final Optional<StoragePath> currentWorkingDirectory,
                                                final ConverterContext context) {
        return BasicStorageConverterContext.with(
            currentWorkingDirectory,
            context
        );
    }

    /**
     * {@see FakeStorageConverterContext}
     */
    public static FakeStorageConverterContext fake() {
        return new FakeStorageConverterContext();
    }

    /**
     * Stop creation
     */
    private StorageConverterContexts() {
        throw new UnsupportedOperationException();
    }
}
