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

import walkingkooka.environment.EnvironmentContext;
import walkingkooka.reflect.PublicStaticHelper;

/**
 * A collection of {@link StorageStore}.
 */
public final class StorageStoreContexts implements PublicStaticHelper {

    /**
     * {@see BasicStorageStoreContext}
     */
    public static StorageStoreContext basic(final EnvironmentContext environmentContext) {
        return BasicStorageStoreContext.with(environmentContext);
    }

    /**
     * {@see FakeStorageStoreContext}
     */
    public static StorageStoreContext fake() {
        return new FakeStorageStoreContext();
    }

    /**
     * Stop creation
     */
    private StorageStoreContexts() {
        throw new UnsupportedOperationException();
    }
}
