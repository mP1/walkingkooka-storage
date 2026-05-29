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
import walkingkooka.Either;
import walkingkooka.io.FileExtension;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;
import walkingkooka.tree.json.JsonNode;


/**
 * Converts *.json files in several steps,
 * <ul>
 * <li>Convert {@link StorageBinary} to a {@link String}, which is expected to hold JSON</li>
 * <li>Parse but not convert {@link String} to a {@link JsonNode}</li>
 * <li>Convert {@link JsonNode} to given type {@link Class}</li>
 * </ul>
 */
final class StorageConverterStorageBinaryToJson<C extends StorageConverterContext> extends StorageConverterStorageBinaryTo<C> {

    /**
     * Type safe getter.
     */
    static <C extends StorageConverterContext> StorageConverterStorageBinaryToJson<C> instance() {
        return Cast.to(INSTANCE);
    }

    private final static StorageConverterStorageBinaryToJson INSTANCE = new StorageConverterStorageBinaryToJson<>();

    private StorageConverterStorageBinaryToJson() {
        super();
    }

    @Override
    boolean isPathAndType(final StoragePath path,
                          final Class<?> type,
                          final C context) {
        return FileExtension.JSON.equals(
            path.fileExtension()
                .orElse(null)
        ) && context.canConvert(
            JsonNode.object(),
            type
        );
    }

    @Override
    <T> Either<T, String> storageBinaryTo(final StorageBinary storageBinary,
                                          final Class<T> type,
                                          final C context) {
        final Either<T, String> result;

        // convert Binary to String
        final Either<String, String> text = context.convert(
            storageBinary,
            String.class
        );
        if (text.isRight()) {
            result = Cast.to(text);
        } else {
            // parse the String into a JsonNode.
            // trying to convert the string to JsonNode will not parse and only create a JsonString.
            // convert JsonNode to $type
            result = context.convert(
                JsonNode.parse(
                    text.leftValue()
                ),
                type
            );
        }

        return result;
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "*." + FileExtension.JSON + " AND " + StorageBinary.class.getSimpleName() + " to";
    }
}
