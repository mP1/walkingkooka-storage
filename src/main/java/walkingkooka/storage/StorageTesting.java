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
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface StorageTesting<S extends Storage, C extends StorageContext> extends ClassTesting<S>,
    TreePrintableTesting {

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

    default void loadAndCheck(final Storage<C> store,
                              final StoragePath path,
                              final C context) {
        this.loadAndCheck(
            store,
            path,
            context,
            Optional.empty()
        );
    }

    default void loadAndCheck(final Storage<C> store,
                              final StoragePath path,
                              final C context,
                              final StorageValue expected) {
        this.loadAndCheck(
            store,
            path,
            context,
            Optional.of(expected)
        );
    }

    default void loadAndCheck(final Storage<C> store,
                              final StoragePath path,
                              final C context,
                              final Optional<StorageValue> expected) {
        this.checkEquals(
            expected,
            store.load(
                path,
                context
            ),
            () -> " store load " + path
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

    default void saveAndCheck(final Storage<C> store,
                              final StorageValue value,
                              final C context,
                              final StorageValue expected) {
        this.checkEquals(
            expected,
            store.save(
                value,
                context
            ),
            () -> " storage save " + value
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

    default void listAndCheck(final Storage<C> store,
                              final StoragePath parent,
                              final int offset,
                              final int count,
                              final C context,
                              final StorageValueInfo... expected) {
        this.listAndCheck(
            store,
            parent,
            offset,
            count,
            context,
            Lists.of(expected)
        );
    }

    default void listAndCheck(final Storage<C> store,
                              final StoragePath parent,
                              final int offset,
                              final int count,
                              final C context,
                              final List<StorageValueInfo> expected) {
        this.checkEquals(
            expected,
            store.list(
                parent,
                offset,
                count,
                context
            ),
            () -> "list parent=" + parent + " offset=" + offset + " count=" + count
        );
    }

    S createStorage();

    C createContext();
}
