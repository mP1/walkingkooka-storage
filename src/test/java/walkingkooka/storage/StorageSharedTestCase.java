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
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class StorageSharedTestCase<S extends StorageShared<C>, C extends StorageContext> implements StorageTesting2<S, C>,
    ToStringTesting<S> {

    StorageSharedTestCase() {
        super();
    }

    @Test
    public final void testMountFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createStorage()
                .mount(
                    StoragePath.parse("/mount"),
                    Storages.fake(),
                    this.createContext()
                )
        );
    }

    // class............................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
