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
import walkingkooka.Cast;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageSharedEmptyTest extends StorageSharedTestCase<StorageSharedEmpty<StorageContext>, StorageContext> {

    @Test
    public void testCanReadRoot() {
        this.canReadAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            this.createContext(),
            true
        );
    }

    @Test
    public void testCanReadNonRoot() {
        this.canReadAndCheck(
            this.createStorage(),
            StoragePath.parse("/unknown.txt"),
            this.createContext(),
            false
        );
    }

    @Test
    public void testCanWriteRoot() {
        this.canWriteAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            this.createContext(),
            false
        );
    }

    @Test
    public void testCanWriteNonRoot() {
        this.canWriteAndCheck(
            this.createStorage(),
            StoragePath.parse("/file.txt"),
            this.createContext(),
            false
        );
    }

    @Test
    public void testLoadMissing() {
        this.loadAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            this.createContext()
        );
    }

    @Test
    public void testSaveFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> StorageSharedEmpty.instance()
                .save(
                    StorageValue.with(
                        StoragePath.parse("/hello")
                    ),
                    this.createContext()
                )
        );
    }

    @Test
    public void testDeleteFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> StorageSharedEmpty.instance()
                .delete(
                    StoragePath.ROOT,
                    this.createContext()
                )
        );
    }

    @Test
    public void testList() {
        this.listAndCheck(
            StorageSharedEmpty.instance(),
            StoragePath.ROOT,
            0,
            999,
            this.createContext()
        );
    }

    @Override
    public StorageSharedEmpty<StorageContext> createStorage() {
        return StorageSharedEmpty.instance();
    }

    @Override
    public StorageContext createContext() {
        return StorageContexts.fake();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            StorageSharedEmpty.instance(),
            ""
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageSharedEmpty<StorageContext>> type() {
        return Cast.to(StorageSharedEmpty.class);
    }
}
