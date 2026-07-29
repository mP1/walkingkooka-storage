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

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class StorageSharedWrapperTestCase<S extends StorageSharedWrapper<C>, C extends StorageContext> extends StorageSharedTestCase<S, C> {

    StorageSharedWrapperTestCase() {
        super();
    }

    @Test
    public final void testWithNullStorageFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStorage(null)
        );
    }

    @Override
    public final S createStorage() {
        return this.createStorage(
            this.createWrappedStorage()
        );
    }

    abstract S createStorage(final Storage<C> storage);

    abstract Storage<C> createWrappedStorage();
}
