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
import walkingkooka.reflect.ClassTesting;
import walkingkooka.text.printer.TreePrintableTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface StorageTesting2<S extends Storage<C>, C extends StorageContext> extends StorageTesting,
    ClassTesting<S>,
    TreePrintableTesting {

    // canRead..........................................................................................................

    @Test
    default void testCanReadWithNullIdFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStorage()
                .canRead(
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testCanReadWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStorage()
                .canRead(
                    StoragePath.ROOT,
                    null
                )
        );
    }

    // canWrite.........................................................................................................

    @Test
    default void testCanWriteWithNullPathFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStorage()
                .canWrite(
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testCanWriteWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStorage()
                .canWrite(
                    StoragePath.ROOT,
                    null
                )
        );
    }
    
    // load.............................................................................................................

    @Test
    default void testLoadWithNullIdFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStorage()
                .load(
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testLoadWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStorage()
                .load(
                    StoragePath.ROOT,
                    null
                )
        );
    }

    @Test
    default void testLoadRoot() {
        this.loadAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            this.createContext()
        );
    }

    // save.............................................................................................................

    @Test
    default void testSaveWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStorage()
                .save(
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testSaveWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStorage()
                .save(
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testSaveRootFails() {
        assertThrows(
            InvalidStoragePathException.class,
            () -> this.createStorage()
                .save(
                    StorageValue.with(StoragePath.ROOT),
                    this.createContext()
                )
        );
    }

    @Test
    default void testSaveParentFails() {
        assertThrows(
            InvalidStoragePathException.class,
            () -> this.createStorage()
                .save(
                    StorageValue.with(
                        StoragePath.parse("/parent1/")
                    ),
                    this.createContext()
                )
        );
    }
    
    // delete...........................................................................................................

    @Test
    default void testDeleteWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStorage()
                .delete(
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testDeleteWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStorage()
                .delete(
                    StoragePath.ROOT,
                    null // context
                )
        );
    }

    @Test
    default void testDeleteRootFails() {
        assertThrows(
            InvalidStoragePathException.class,
            () -> this.createStorage()
                .delete(
                    StoragePath.ROOT,
                    this.createContext()
                )
        );
    }

    @Test
    default void testDeleteParentFails() {
        assertThrows(
            InvalidStoragePathException.class,
            () -> this.createStorage()
                .delete(
                    StoragePath.parse("/parent1/"),
                    this.createContext()
                )
        );
    }

    // list.............................................................................................................

    @Test
    default void testListWithNullParentFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStorage()
                .list(
                    null,
                    0,
                    0,
                    this.createContext()
                )
        );
    }

    @Test
    default void testListWithNegativeOffsetFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createStorage()
                .list(
                    StoragePath.ROOT,
                    -1,
                    1,
                    this.createContext()
                )
        );

        this.checkEquals(
            "Invalid offset -1 < 0",
            thrown.getMessage()
        );
    }

    @Test
    default void testListWithNegativeCountFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createStorage()
                .list(
                    StoragePath.ROOT,
                    0,
                    -1,
                    this.createContext()
                )
        );

        this.checkEquals(
            "Invalid count -1 < 0",
            thrown.getMessage()
        );
    }

    @Test
    default void testListWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStorage()
                .list(
                    StoragePath.ROOT,
                    0,
                    1,
                    null // context
                )
        );
    }

    // addWatcher.......................................................................................................

    @Test
    default void testAddWatcherWithNullWatcherFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStorage()
                .addWatcher(
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testAddWatcherWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStorage()
                .addWatcher(
                    new FakeStorageWatcher(),
                    null
                )
        );
    }
    @Test
    default void testAddWatcherOnceWithNullWatcherFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStorage()
                .addWatcherOnce(
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testAddWatcherOnceWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStorage()
                .addWatcherOnce(
                    new FakeStorageWatcher(),
                    null
                )
        );
    }

    S createStorage();

    C createContext();
}
