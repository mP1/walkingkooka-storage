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
import walkingkooka.ToStringTesting;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class EmptyStorageStoreTest implements StorageStoreTesting<EmptyStorageStore>,
    ToStringTesting<EmptyStorageStore> {

    @Override
    public void testAddSaveWatcherAndSave() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testAddSaveWatcher() {
        EmptyStorageStore.INSTANCE.addSaveWatcher(
            (v) -> {
                throw new UnsupportedOperationException();
            });
    }

    @Override
    public void testAddSaveWatcherAndSaveTwiceFiresOnce() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testAddDeleteWatcherAndDelete() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testAddDeleteWatcher() {
        EmptyStorageStore.INSTANCE.addDeleteWatcher(
            (v) -> {
                throw new UnsupportedOperationException();
            });
    }

    @Test
    public void testLoadMissing() {
        this.loadAndCheck(
            this.createStore(),
            this.id()
        );
    }

    @Test
    public void testSaveFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> EmptyStorageStore.INSTANCE.save(this.value())
        );
    }

    @Test
    public void testDeleteFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> EmptyStorageStore.INSTANCE.delete(this.id())
        );
    }

    @Test
    public void testIdsEmpty() {
        this.idsAndCheck(
            EmptyStorageStore.INSTANCE,
            0,
            999
        );
    }

    @Test
    public void testValuesEmpty() {
        this.valuesAndCheck(
            EmptyStorageStore.INSTANCE,
            0,
            999
        );
    }

    @Test
    public void testBetweenEmpty() {
        this.betweenAndCheck(
            EmptyStorageStore.INSTANCE,
            StoragePath.parse("/1"),
            StoragePath.parse("/999")
            );
    }

    @Test
    public void testCount() {
        this.countAndCheck(
            EmptyStorageStore.INSTANCE,
            0
        );
    }

    @Test
    public void testStorageValueInfos() {
        this.storageValueInfosAndCheck(
            EmptyStorageStore.INSTANCE,
            StoragePath.ROOT,
            0,
            999
        );
    }

    @Override
    public EmptyStorageStore createStore() {
        return EmptyStorageStore.INSTANCE;
    }

    @Override
    public StoragePath id() {
        return StoragePath.parse("/path123");
    }

    @Override
    public StorageValue value() {
        return StorageValue.with(
            this.id(),
            Optional.of("value123")
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            EmptyStorageStore.INSTANCE,
            ""
        );
    }

    // class............................................................................................................

    @Override
    public Class<EmptyStorageStore> type() {
        return EmptyStorageStore.class;
    }
}
