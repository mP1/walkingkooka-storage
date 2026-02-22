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

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.storage.StoragePath;
import walkingkooka.tree.json.JsonNode;

public final class StorageConverterStoragePathToClassJsonTest extends StorageConverterStoragePathToClassTestCase<StorageConverterStoragePathToClassJson<FakeStorageConverterContext>> {

    @Test
    public void testConvertStoragePathWithoutFileExtensionToClass() {
        this.convertAndCheck(
            StoragePath.parse("/path1/file"),
            Class.class,
            JsonNode.class
        );
    }

    @Test
    public void testConvertStoragePathWithJsonFileExtensionToClass() {
        this.convertAndCheck(
            StoragePath.parse("/path1/file.json"),
            Class.class,
            JsonNode.class
        );
    }

    @Test
    public void testConvertStoragePathWithCapitalsJsonFileExtensionToClass() {
        this.convertAndCheck(
            StoragePath.parse("/path1/file.JSON"),
            Class.class,
            JsonNode.class
        );
    }

    @Test
    public void testConvertStoragePathWithTxtFileExtensionToClassFails() {
        this.convertFails(
            StoragePath.parse("/path1/file.txt"),
            Class.class
        );
    }

    @Override
    public StorageConverterStoragePathToClassJson<FakeStorageConverterContext> createConverter() {
        return StorageConverterStoragePathToClassJson.instance();
    }

    @Override
    public FakeStorageConverterContext createContext() {
        return new FakeStorageConverterContext();
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createConverter(),
            "*, *.json to Class"
        );
    }

    @Override
    public Class<StorageConverterStoragePathToClassJson<FakeStorageConverterContext>> type() {
        return Cast.to(StorageConverterStoragePathToClassJson.class);
    }
}
