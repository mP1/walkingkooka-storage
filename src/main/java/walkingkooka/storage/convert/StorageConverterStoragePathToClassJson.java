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
import walkingkooka.io.FileExtension;
import walkingkooka.storage.StoragePath;
import walkingkooka.tree.json.JsonNode;

/**
 * A {@link Converter} that converts a {@link StoragePath} by examining its file extension to a {@link Class} which can
 * then be used to convert a value to text.
 */
final class StorageConverterStoragePathToClassJson<C extends StorageConverterContext> extends StorageConverterStoragePathToClass<C> {

    /**
     * Type safe getter.
     */
    static <C extends StorageConverterContext> StorageConverterStoragePathToClassJson<C> instance() {
        return Cast.to(INSTANCE);
    }

    private final static StorageConverterStoragePathToClassJson<?> INSTANCE = new StorageConverterStoragePathToClassJson<>();

    private StorageConverterStoragePathToClassJson() {
        super();
    }

    @Override
    boolean testFileExtension(final FileExtension fileExtensionOrNull) {
        return fileExtensionOrNull == null ||
            fileExtensionOrNull.equals(FILE_EXTENSION);
    }

    @Override
    Class<?> type() {
        return JsonNode.class;
    }

    /**
     * Matches *.json file extension.
     */
    private final static FileExtension FILE_EXTENSION = FileExtension.JSON;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "*, *.json to " + Class.class.getSimpleName();
    }
}
