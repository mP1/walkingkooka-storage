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
import walkingkooka.build.BuilderTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class RoutingStorageStoreBuilderTest implements BuilderTesting<RoutingStorageStoreBuilder, StorageStore> {

    private final static StoragePath PATH = StoragePath.ROOT;

    private final static StorageStore STORE = new FakeStorageStore() {

        @Override
        public String toString() {
            return FakeStorageStore.class.getSimpleName();
        }
    };

    // startsWith.......................................................................................................

    @Test
    public void testStartWithWithNullPathFails() {
        assertThrows(
            NullPointerException.class,
            () -> RoutingStorageStoreBuilder.empty()
                .startsWith(
                    null,
                    STORE
                )
        );
    }

    @Test
    public void testStartWithWithNullStoreFails() {
        assertThrows(
            NullPointerException.class,
            () -> RoutingStorageStoreBuilder.empty()
                .startsWith(
                    PATH,
                    null
                )
        );
    }

    @Test
    public void testStartWithShadowedFails() {
        final RoutingStorageStoreBuilder builder = RoutingStorageStoreBuilder.empty()
            .startsWith(
                PATH,
                STORE
            );

        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> builder.startsWith(
                    PATH.append(StorageName.with("222")),
                    STORE
                )
        );

        this.checkEquals(
            "Invalid path \"/222/*\" would be shadowed by \"/*\" FakeStorageStore",
            thrown.getMessage()
        );
    }

    @Test
    public void testStartWithShadowedFails2() {
        final StoragePath path = StoragePath.parse("/mount111");

        final RoutingStorageStoreBuilder builder = RoutingStorageStoreBuilder.empty()
            .startsWith(
                path,
                STORE
            );

        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> builder.startsWith(
                path.append(StorageName.with("222")),
                STORE
            )
        );

        this.checkEquals(
            "Invalid path \"/mount111/222/*\" would be shadowed by \"/mount111/*\" FakeStorageStore",
            thrown.getMessage()
        );
    }

    @Test
    public void testStartWithShadowedFails3() {
        final StoragePath path = StoragePath.parse("/mount222");

        final RoutingStorageStoreBuilder builder = RoutingStorageStoreBuilder.empty()
            .startsWith(
                StoragePath.parse("/mount111"),
                STORE
            ).startsWith(
                    path,
                    STORE
            );

        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> builder.startsWith(
                path.append(StorageName.with("999")),
                STORE
            )
        );

        this.checkEquals(
            "Invalid path \"/mount222/999/*\" would be shadowed by \"/mount222/*\" FakeStorageStore",
            thrown.getMessage()
        );
    }

    @Test
    public void testStartsWith() {
        final RoutingStorageStoreBuilder builder = RoutingStorageStoreBuilder.empty()
            .startsWith(
                StoragePath.parse("/mount111"),
                STORE
            ).startsWith(
                StoragePath.parse("/mount222"),
                STORE
            );
    }

    @Test
    public void testStartsWith2() {
        final RoutingStorageStoreBuilder builder = RoutingStorageStoreBuilder.empty()
            .startsWith(
                StoragePath.parse("/mount111/xyz"),
                STORE
            ).startsWith(
                StoragePath.parse("/mount111"),
                STORE
            );
    }

    // Builder..........................................................................................................

    @Test
    public void testBuild() {
        final RoutingStorageStoreBuilder builder = RoutingStorageStoreBuilder.empty()
            .startsWith(
                StoragePath.parse("/mount111/xyz"),
                STORE
            ).startsWith(
                StoragePath.parse("/mount111"),
                STORE
            );
        builder.build();
    }

    @Override
    public RoutingStorageStoreBuilder createBuilder() {
        return RoutingStorageStoreBuilder.empty();
    }

    @Override
    public Class<StorageStore> builderProductType() {
        return StorageStore.class;
    }

    // class............................................................................................................

    @Override
    public Class<RoutingStorageStoreBuilder> type() {
        return RoutingStorageStoreBuilder.class;
    }
}
