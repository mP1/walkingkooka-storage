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

import java.util.Optional;

public class TreeMapStoreStorageStoreTest implements StorageStoreTesting<TreeMapStoreStorageStore> {

    @Test
    public void testSaveAndLoad() {
        final TreeMapStoreStorageStore store = this.createStore();

        final StorageValue value = this.value();

        store.save(value);

        this.loadAndCheck(
            store,
            value.key(),
            value
        );
    }

    @Override
    public TreeMapStoreStorageStore createStore() {
        return TreeMapStoreStorageStore.empty();
    }

    @Override
    public StorageKey id() {
        return StorageKey.with("key123");
    }

    @Override
    public StorageValue value() {
        return StorageValue.with(
            this.id(),
            Optional.of("Value456")
        );
    }

    // class............................................................................................................

    @Override
    public Class<TreeMapStoreStorageStore> type() {
        return TreeMapStoreStorageStore.class;
    }
}
