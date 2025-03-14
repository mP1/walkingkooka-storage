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

import walkingkooka.reflect.PublicStaticHelper;

/**
 * A collection of {@link StorageStore}.
 */
public final class StorageStores implements PublicStaticHelper {

    /**
     * {@see EmptyStorageStore}
     */
    public static StorageStore empty() {
        return EmptyStorageStore.INSTANCE;
    }

    /**
     * {@see FakeStorageStore}
     */
    public static StorageStore fake() {
        return new FakeStorageStore();
    }

    /**
     * {@see TreeMapStoreStorageStore}
     */
    public static StorageStore tree(final StorageStoreContext context) {
        return TreeMapStoreStorageStore.with(context);
    }

    /**
     * Stop creation
     */
    private StorageStores() {
        throw new UnsupportedOperationException();
    }
}
