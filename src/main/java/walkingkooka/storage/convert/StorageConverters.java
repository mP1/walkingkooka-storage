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

import walkingkooka.convert.Converter;
import walkingkooka.reflect.PublicStaticHelper;

/**
 * Factories for Storage {@link walkingkooka.convert.Converter}.
 */
public final class StorageConverters implements PublicStaticHelper {

    /**
     * {@see StorageConverterSharedStorageValueInfoListToText}
     */
    public static <C extends StorageConverterContext> Converter<C> storageValueInfoListToText() {
        return StorageConverterSharedStorageValueInfoListToText.instance();
    }

    /**
     * {@see StorageConverterSharedTextToStoragePath}
     */
    public static <C extends StorageConverterContext> Converter<C> textToStoragePath() {
        return StorageConverterSharedTextToStoragePath.instance();
    }

    /**
     * Stop creation
     */
    private StorageConverters() {
        throw new UnsupportedOperationException();
    }
}
