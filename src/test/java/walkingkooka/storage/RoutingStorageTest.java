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
import walkingkooka.collect.list.Lists;
import walkingkooka.environment.AuditInfo;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.JavaVisibility;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public final class RoutingStorageTest implements StorageTesting<RoutingStorage, StorageContext> {

    private final static LocalDateTime NOW = LocalDateTime.of(
        1999,
        12,
        31,
        12,
        58
    );

    private final static EmailAddress USER = EmailAddress.parse("user@example.com");

    private final static String VALUE1 = "value1";

    private final static StorageContext CONTEXT = new FakeStorageContext() {
        @Override
        public LocalDateTime now() {
            return RoutingStorageTest.NOW;
        }

        @Override
        public Optional<EmailAddress> user() {
            return Optional.of(RoutingStorageTest.USER);
        }
    };

    @Test
    public void testSave() {
        final Storage store1 = Storages.tree();

        final RoutingStorage routingStorageStore = this.createStorage(
            store1,
            Storages.fake()
        );

        routingStorageStore.save(
            storageValue(
                "/mount1/file1",
                VALUE1
            ),
            CONTEXT
        );

        this.loadAndCheck(
            store1,
            StoragePath.parse("/file1"),
            CONTEXT,
            storageValue(
                "/file1",
                VALUE1
            )
        );
    }

    @Test
    public void testSaveExtraLongPath() {
        final Storage store1 = Storages.tree();

        final RoutingStorage routingStorageStore = this.createStorage(
            store1,
            Storages.fake()
        );

        routingStorageStore.save(
            storageValue(
                "/mount1/dir1/dir11/dir111/file1",
                VALUE1
            ),
            CONTEXT
        );

        this.loadAndCheck(
            store1,
            StoragePath.parse("/dir1/"),
            CONTEXT,
            StorageValue.with(
                StoragePath.parse("/dir1/"),
                StorageValue.NO_VALUE
            )
        );

        this.loadAndCheck(
            store1,
            StoragePath.parse("/dir1/dir11/"),
            CONTEXT,
            StorageValue.with(
                StoragePath.parse("/dir1/dir11/"),
                StorageValue.NO_VALUE
            )
        );

        this.loadAndCheck(
            store1,
            StoragePath.parse("/dir1/dir11/dir111/"),
            CONTEXT,
            StorageValue.with(
                StoragePath.parse("/dir1/dir11/dir111/"),
                StorageValue.NO_VALUE
            )
        );

        this.loadAndCheck(
            store1,
            StoragePath.parse("/dir1/dir11/dir111/file1"),
            CONTEXT,
            StorageValue.with(
                StoragePath.parse("/dir1/dir11/dir111/file1"),
                Optional.of(VALUE1)
            )
        );
    }

    @Test
    public void testSave2ndMount() {
        final Storage store1 = Storages.tree();

        final RoutingStorage routingStorageStore = this.createStorage(
            Storages.fake(),
            store1
        );

        routingStorageStore.save(
            storageValue(
                "/mount2/file1",
                VALUE1
            ),
            CONTEXT
        );

        this.loadAndCheck(
            store1,
            StoragePath.parse("/file1"),
            CONTEXT,
            storageValue(
                "/file1",
                VALUE1
            )
        );
    }

    @Test
    public void testSaveAndLoad() {
        final Storage store1 = Storages.tree();

        final RoutingStorage routingStorageStore = this.createStorage(
            store1,
            Storages.fake()
        );

        final StorageValue value = storageValue(
            "/mount1/file1",
            VALUE1
        );

        routingStorageStore.save(
            value,
            CONTEXT
        );

        this.loadAndCheck(
            routingStorageStore,
            value.path(),
            CONTEXT,
            value
        );
    }

    @Test
    public void testSaveAndLoadExtraLongPath() {
        final Storage store1 = Storages.tree();

        final RoutingStorage routingStorageStore = this.createStorage(
            store1,
            Storages.fake()
        );

        final StorageValue value = storageValue(
            "/mount1/dir1/dir11/file1",
            VALUE1
        );

        routingStorageStore.save(
            value,
            CONTEXT
        );

        this.loadAndCheck(
            routingStorageStore,
            value.path(),
            CONTEXT,
            value
        );
    }

    @Test
    public void testSaveAndDelete() {
        final Storage store1 = Storages.tree();

        final RoutingStorage routingStorageStore = this.createStorage(
            store1,
            Storages.fake()
        );

        routingStorageStore.save(
            storageValue(
                "/mount1/file1",
                VALUE1
            ),
            CONTEXT
        );

        this.loadAndCheck(
            store1,
            StoragePath.parse("/file1"),
            CONTEXT,
            storageValue(
                "/file1",
                VALUE1
            )
        );

        routingStorageStore.delete(
            StoragePath.parse("/mount1/file1"),
            CONTEXT
        );

        this.loadAndCheck(
            store1,
            StoragePath.parse("/file1"),
            CONTEXT
        );
    }

    @Test
    public void testAddDeleteSaveAndDelete() {
        final Storage store1 = Storages.tree();
        final Storage store2 = Storages.tree();

        final RoutingStorage routingStorageStore = this.createStorage(
            store1,
            store2
        );

        routingStorageStore.save(
            storageValue(
                "/mount1/file1",
                VALUE1
            ),
            CONTEXT
        );

        this.loadAndCheck(
            store1,
            StoragePath.parse("/file1"),
            CONTEXT,
            storageValue(
                "/file1",
                VALUE1
            )
        );

        routingStorageStore.delete(
            StoragePath.parse("/mount1/file1"),
            CONTEXT
        );

        this.listAndCheck(
            store1,
            StoragePath.ROOT,
            0,
            100,
            CONTEXT
        );

        this.listAndCheck(
            store2,
            StoragePath.ROOT,
            0,
            100,
            CONTEXT
        );
    }

    @Test
    public void testList() {
        final Storage store1 = Storages.tree();
        final Storage store2 = Storages.tree();

        final RoutingStorage routingStorageStore = this.createStorage(
            store1,
            store2
        );

        final StorageValue value1 = storageValue(
            "/mount1/file1",
            "value1"
        );

        routingStorageStore.save(
            value1,
            CONTEXT
        );

        final StorageValue value2 = storageValue(
            "/mount1/file2",
            "value2"
        );

        routingStorageStore.save(
            value2,
            CONTEXT
        );

        final StorageValue value3 = storageValue(
            "/mount2/file3",
            "value3"
        );

        routingStorageStore.save(
            value3,
            CONTEXT
        );

        final StorageValue value4 = storageValue(
            "/mount2/file4",
            "value4"
        );

        routingStorageStore.save(
            value4,
            CONTEXT
        );

        this.listAndCheck(
            routingStorageStore,
            StoragePath.parse("/mount1"),
            0,
            3,
            CONTEXT,
            storageValueInfo("/mount1/file1"),
            storageValueInfo("/mount1/file2")
        );

        this.listAndCheck(
            routingStorageStore,
            StoragePath.parse("/mount2"),
            0,
            3,
            CONTEXT,
            storageValueInfo("/mount2/file3"),
            storageValueInfo("/mount2/file4")
        );
    }

    @Test
    public void testListSubDirectoryOfMount() {
        final Storage store1 = Storages.tree();
        final Storage store2 = Storages.tree();

        final RoutingStorage routingStorageStore = this.createStorage(
            store1,
            store2
        );

        final StorageValue value1 = storageValue(
            "/mount1/dir1/file1",
            "value1"
        );

        routingStorageStore.save(
            value1,
            CONTEXT
        );

        final StorageValue value2 = storageValue(
            "/mount1/dir1/file2",
            "value2"
        );

        routingStorageStore.save(
            value2,
            CONTEXT
        );

        final StorageValue value3 = storageValue(
            "/mount1/dir2/file3",
            "value3"
        );

        routingStorageStore.save(
            value3,
            CONTEXT
        );

        final StorageValue value4 = storageValue(
            "/mount2/file4",
            "value4"
        );

        routingStorageStore.save(
            value4,
            CONTEXT
        );

        this.listAndCheck(
            routingStorageStore,
            StoragePath.parse("/mount1/dir1/"),
            0,
            3,
            CONTEXT,
            storageValueInfo("/mount1/dir1/file1"),
            storageValueInfo("/mount1/dir1/file2")
        );
    }

    @Override
    public RoutingStorage createStorage() {
        return this.createStorage(
            Storages.tree(),// /mount1
            Storages.tree() // /mount2
        );
    }

    private RoutingStorage createStorage(final Storage... stores) {
        return this.createStorage(
            Lists.of(stores)
        );
    }

    private RoutingStorage createStorage(final List<Storage> stores) {
        final RoutingStorageBuilder b = RoutingStorageBuilder.empty();

        int i = 1;
        for (Storage store : stores) {
            b.startsWith(
                StoragePath.parse("/mount" + i),
                store
            );

            i++;
        }

        return (RoutingStorage) b.build();
    }

    @Override
    public StorageContext createContext() {
        return CONTEXT;
    }

    private static StorageValue storageValue(final String path,
                                             final String value) {
        return StorageValue.with(
            StoragePath.parse(path),
            Optional.of(value)
        );
    }

    private static StorageValueInfo storageValueInfo(final String path) {
        return StorageValueInfo.with(
            StoragePath.parse(path),
            AuditInfo.create(
                USER,
                NOW
            )
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {

    }

    // class............................................................................................................

    @Override
    public Class<RoutingStorage> type() {
        return Cast.to(RoutingStorage.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
