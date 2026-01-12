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
import walkingkooka.build.BuilderTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class RoutingStorageBuilderTest implements BuilderTesting<RoutingStorageBuilder, Storage> {

    private final static StoragePath PATH = StoragePath.ROOT;

    private final static Storage STORE = new FakeStorage() {

        @Override
        public String toString() {
            return FakeStorage.class.getSimpleName();
        }
    };

    // startsWith.......................................................................................................

    @Test
    public void testStartWithWithNullPathFails() {
        assertThrows(
            NullPointerException.class,
            () -> RoutingStorageBuilder.empty()
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
            () -> RoutingStorageBuilder.empty()
                .startsWith(
                    PATH,
                    null
                )
        );
    }

    @Test
    public void testStartWithShadowedFails() {
        final RoutingStorageBuilder builder = RoutingStorageBuilder.empty()
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
            "Invalid path \"/222/*\" would be shadowed by \"/*\" FakeStorage",
            thrown.getMessage()
        );
    }

    @Test
    public void testStartWithShadowedFails2() {
        final StoragePath path = StoragePath.parse("/mount111");

        final RoutingStorageBuilder builder = RoutingStorageBuilder.empty()
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
            "Invalid path \"/mount111/222/*\" would be shadowed by \"/mount111/*\" FakeStorage",
            thrown.getMessage()
        );
    }

    @Test
    public void testStartWithShadowedFails3() {
        final StoragePath path = StoragePath.parse("/mount222");

        final RoutingStorageBuilder builder = RoutingStorageBuilder.empty()
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
            "Invalid path \"/mount222/999/*\" would be shadowed by \"/mount222/*\" FakeStorage",
            thrown.getMessage()
        );
    }

    @Test
    public void testStartsWith() {
        final RoutingStorageBuilder builder = RoutingStorageBuilder.empty()
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
        final RoutingStorageBuilder builder = RoutingStorageBuilder.empty()
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
        final RoutingStorageBuilder builder = RoutingStorageBuilder.empty()
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
    public RoutingStorageBuilder createBuilder() {
        return RoutingStorageBuilder.empty();
    }

    @Override
    public Class<Storage> builderProductType() {
        return Cast.to(Storage.class);
    }

    // class............................................................................................................

    @Override
    public Class<RoutingStorageBuilder> type() {
        return Cast.to(RoutingStorageBuilder.class);
    }
}
