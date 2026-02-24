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
import walkingkooka.props.Properties;
import walkingkooka.storage.StoragePath;

public final class StorageConverterStoragePathToClassPropertiesTest extends StorageConverterStoragePathToClassTestCase<StorageConverterStoragePathToClassProperties<FakeStorageConverterContext>> {

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
    public void testConvertStoragePathWithPropertiesFileExtensionToClass() {
        this.convertAndCheck(
            StoragePath.parse("/path1/file.properties"),
            Class.class,
            Properties.class
        );
    }

    @Test
    public void testConvertStoragePathWithCapitalsPropertiesFileExtensionToClass() {
        this.convertAndCheck(
            StoragePath.parse("/path1/file.PROPERTIES"),
            Class.class,
            Properties.class
        );
    }

    @Override
    public StorageConverterStoragePathToClassProperties<FakeStorageConverterContext> createConverter() {
        return StorageConverterStoragePathToClassProperties.instance();
    }

    @Override
    public FakeStorageConverterContext createContext() {
        return new FakeStorageConverterContext();
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createConverter(),
            "*.properties to Class<Properties>"
        );
    }

    @Override
    public Class<StorageConverterStoragePathToClassProperties<FakeStorageConverterContext>> type() {
        return Cast.to(StorageConverterStoragePathToClassProperties.class);
    }
}
