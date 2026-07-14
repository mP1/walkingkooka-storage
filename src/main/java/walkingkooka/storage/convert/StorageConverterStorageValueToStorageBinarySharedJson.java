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

import walkingkooka.Binary;
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.io.FileExtension;
import walkingkooka.net.header.MediaType;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StorageValue;
import walkingkooka.tree.json.JsonNode;

/**
 * Converts a {@link StorageValue} into {@link StorageBinary} if the file extension is {@link FileExtension#JSON} and
 * the {@link StorageValue#value()} can be converted into a {@link JsonNode} and then {@link String} and then {@link StorageBinary}.
 */
final class StorageConverterStorageValueToStorageBinarySharedJson<C extends StorageConverterContext> extends StorageConverterStorageValueToStorageBinaryShared<C> {

    /**
     * Type safe getter.
     */
    static <C extends StorageConverterContext> StorageConverterStorageValueToStorageBinarySharedJson<C> instance() {
        return Cast.to(INSTANCE);
    }

    private final static StorageConverterStorageValueToStorageBinarySharedJson INSTANCE = new StorageConverterStorageValueToStorageBinarySharedJson<>();

    private StorageConverterStorageValueToStorageBinarySharedJson() {
        super();
    }

    @Override
    FileExtension fileExtension() {
        return FileExtension.JSON;
    }

    @Override
    MediaType contentType() {
        return MediaType.APPLICATION_JSON;
    }

    @Override
    boolean testValue(final Object value,
                      final C context) {
        return context.canConvert(value, JsonNode.class) &&
            context.canConvert(JSON_OBJECT, String.class);
    }

    private final static JsonNode JSON_OBJECT = JsonNode.object();

    @Override
    Either<Binary, String> toBinary(final StorageValue storageValue,
                                    final C context) {
        Either<JsonNode, String> jsonNode = context.convert(
            storageValue.value()
                .orElse(null),
            JsonNode.class
        );

        return jsonNode.isLeft() ?
            context.convert(
                jsonNode.leftValue(),
                Binary.class
            ) :
            Cast.to(jsonNode);
    }
}
