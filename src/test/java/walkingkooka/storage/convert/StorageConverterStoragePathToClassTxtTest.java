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

public final class StorageConverterStoragePathToClassTxtTest extends StorageConverterStoragePathToClassTestCase<StorageConverterStoragePathToClassTxt<FakeStorageConverterContext>> {

    @Test
    public void testConvertStoragePathWithoutFileExtensionToClassFails() {
        this.convertFails(
            StoragePath.parse("/path1/file"),
            Class.class
        );
    }

    @Test
    public void testConvertStoragePathJsonFileExtensionToClassFails() {
        this.convertFails(
            StoragePath.parse("/path1/file.json"),
            Class.class
        );
    }

    @Test
    public void testConvertStoragePathWithTxtFileExtensionToClass() {
        this.convertAndCheck(
            StoragePath.parse("/path1/file.txt"),
            Class.class,
            String.class
        );
    }

    @Test
    public void testConvertStoragePathWithCapitalsTxtFileExtensionToClass() {
        this.convertAndCheck(
            StoragePath.parse("/path1/file.TXT"),
            Class.class,
            String.class
        );
    }

    @Test
    public void testConvertStoragePathWithTxtFileExtensionToClassFails() {
        this.convertAndCheck(
            StoragePath.parse("/path1/file.txt"),
            Class.class,
            String.class
        );
    }

    @Override
    public StorageConverterStoragePathToClassTxt<FakeStorageConverterContext> createConverter() {
        return StorageConverterStoragePathToClassTxt.instance();
    }

    @Override
    public FakeStorageConverterContext createContext() {
        return new FakeStorageConverterContext();
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createConverter(),
            "*.txt to Class<String>"
        );
    }

    @Override
    public Class<StorageConverterStoragePathToClassTxt<FakeStorageConverterContext>> type() {
        return Cast.to(StorageConverterStoragePathToClassTxt.class);
    }
}
