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

package walkingkooka.storage;

import org.junit.jupiter.api.Test;
import walkingkooka.convert.ConverterLikeTesting2;
import walkingkooka.net.header.MediaTypeDetectorTesting2;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface StorageContextTesting2<C extends StorageContext> extends StorageContextTesting,
    StorageEnvironmentContextTesting2<C>,
    ConverterLikeTesting2<C>,
    CanParseStoragePathTesting2<C>,
    MediaTypeDetectorTesting2<C> {

    // CanReadStorage...................................................................................................

    @Test
    default void testCanReadStorageWithNullPathFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .canReadStorage(null)
        );
    }

    // CanWriteStorage..................................................................................................

    @Test
    default void testCanWriteStorageWithNullPathFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .canWriteStorage(null)
        );
    }

    // load.............................................................................................................

    @Test
    default void testLoadWithNullStoragePathFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .loadStorage(null)
        );
    }

    // saveStorage......................................................................................................

    @Test
    default void testSaveStorageWithNullStorageValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .saveStorage(null)
        );
    }

    // SetAuditInfoStorage..............................................................................................

    @Test
    default void testSetAuditInfoStorageWithNullPathFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .setAuditInfoStorage(null)
        );
    }

    // CanParseStoragePath..............................................................................................

    @Override
    default C createCanParseStoragePath() {
        return this.createContext();
    }

    // ConverterLike....................................................................................................

    @Override
    default C createConverterLike() {
        return this.createContext();
    }

    // MediaTypeDetectorTesting.........................................................................................

    @Override
    default C createMediaTypeDetector() {
        return this.createContext();
    }

    // class............................................................................................................

    @Override
    default String typeNameSuffix() {
        return StorageContext.class.getSimpleName();
    }
}
