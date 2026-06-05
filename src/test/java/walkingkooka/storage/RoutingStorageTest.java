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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public final class RoutingStorageTest extends StorageSharedTestCase<RoutingStorage<StorageContext>, StorageContext> {

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
    public void testCanWriteNew() {
        final Storage<StorageContext> storage1 = Storages.treeMapStore();

        final RoutingStorage<StorageContext> routingStorage = this.createStorage(
            storage1,
            Storages.fake()
        );

        this.canWriteAndCheck(
            storage1,
            StoragePath.parse("/file1"),
            CONTEXT,
            true
        );
    }

    @Test
    public void testCanWrite() {
        final Storage<StorageContext> storage1 = Storages.treeMapStore();

        final RoutingStorage<StorageContext> routingStorage = this.createStorage(
            storage1,
            Storages.fake()
        );

        routingStorage.save(
            storageValue(
                "/mount1/file1",
                VALUE1
            ),
            CONTEXT
        );

        this.canWriteAndCheck(
            storage1,
            StoragePath.parse("/file1"),
            CONTEXT,
            true
        );
    }

    @Override
    public void testSaveRootFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSaveParentFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testSave() {
        final Storage<StorageContext> storage1 = Storages.treeMapStore();

        final RoutingStorage<StorageContext> routingStorage = this.createStorage(
            storage1,
            Storages.fake()
        );

        routingStorage.save(
            storageValue(
                "/mount1/file1",
                VALUE1
            ),
            CONTEXT
        );

        this.loadAndCheck(
            storage1,
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
        final Storage<StorageContext> storage1 = Storages.treeMapStore();

        final RoutingStorage<StorageContext> routingStorage = this.createStorage(
            storage1,
            Storages.fake()
        );

        routingStorage.save(
            storageValue(
                "/mount1/dir1/dir11/dir111/file1",
                VALUE1
            ),
            CONTEXT
        );

        this.loadAndCheck(
            storage1,
            StoragePath.parse("/dir1/"),
            CONTEXT
        );

        this.loadAndCheck(
            storage1,
            StoragePath.parse("/dir1/dir11/"),
            CONTEXT
        );

        this.loadAndCheck(
            storage1,
            StoragePath.parse("/dir1/dir11/dir111/"),
            CONTEXT
        );

        this.loadAndCheck(
            storage1,
            StoragePath.parse("/dir1/dir11/dir111/file1"),
            CONTEXT,
            StorageValue.with(
                StoragePath.parse("/dir1/dir11/dir111/file1")
            ).setValue(
                Optional.of(VALUE1)
            )
        );
    }

    @Test
    public void testSave2ndMount() {
        final Storage<StorageContext> storage1 = Storages.treeMapStore();

        final RoutingStorage<StorageContext> routingStorage = this.createStorage(
            Storages.fake(),
            storage1
        );

        routingStorage.save(
            storageValue(
                "/mount2/file1",
                VALUE1
            ),
            CONTEXT
        );

        this.loadAndCheck(
            storage1,
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
        final Storage<StorageContext> storage1 = Storages.treeMapStore();

        final RoutingStorage<StorageContext> routingStorage = this.createStorage(
            storage1,
            Storages.fake()
        );

        final StorageValue value = storageValue(
            "/mount1/file1",
            VALUE1
        );

        routingStorage.save(
            value,
            CONTEXT
        );

        this.loadAndCheck(
            routingStorage,
            value.path(),
            CONTEXT,
            value
        );
    }

    @Test
    public void testSaveAndLoadExtraLongPath() {
        final Storage<StorageContext> storage1 = Storages.treeMapStore();

        final RoutingStorage<StorageContext> routingStorage = this.createStorage(
            storage1,
            Storages.fake()
        );

        final StorageValue value = storageValue(
            "/mount1/dir1/dir11/file1",
            VALUE1
        );

        routingStorage.save(
            value,
            CONTEXT
        );

        this.loadAndCheck(
            routingStorage,
            value.path(),
            CONTEXT,
            value
        );
    }

    @Test
    public void testSaveAndDelete() {
        final Storage<StorageContext> storage1 = Storages.treeMapStore();

        final RoutingStorage<StorageContext> routingStorage = this.createStorage(
            storage1,
            Storages.fake()
        );

        routingStorage.save(
            storageValue(
                "/mount1/file1",
                VALUE1
            ),
            CONTEXT
        );

        this.loadAndCheck(
            storage1,
            StoragePath.parse("/file1"),
            CONTEXT,
            storageValue(
                "/file1",
                VALUE1
            )
        );

        routingStorage.delete(
            StoragePath.parse("/mount1/file1"),
            CONTEXT
        );

        this.loadAndCheck(
            storage1,
            StoragePath.parse("/file1"),
            CONTEXT
        );
    }

    @Test
    public void testAddDeleteSaveAndDelete() {
        final Storage<StorageContext> storage1 = Storages.treeMapStore();
        final Storage<StorageContext> store2 = Storages.treeMapStore();

        final RoutingStorage<StorageContext> routingStorage = this.createStorage(
            storage1,
            store2
        );

        routingStorage.save(
            storageValue(
                "/mount1/file1",
                VALUE1
            ),
            CONTEXT
        );

        this.loadAndCheck(
            storage1,
            StoragePath.parse("/file1"),
            CONTEXT,
            storageValue(
                "/file1",
                VALUE1
            )
        );

        routingStorage.delete(
            StoragePath.parse("/mount1/file1"),
            CONTEXT
        );

        this.listAndCheck(
            storage1,
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
        final Storage<StorageContext> storage1 = Storages.treeMapStore();
        final Storage<StorageContext> store2 = Storages.treeMapStore();

        final RoutingStorage<StorageContext> routingStorage = this.createStorage(
            storage1,
            store2
        );

        final StorageValue value1 = storageValue(
            "/mount1/file1",
            "value1"
        );

        routingStorage.save(
            value1,
            CONTEXT
        );

        final StorageValue value2 = storageValue(
            "/mount1/file2",
            "value2"
        );

        routingStorage.save(
            value2,
            CONTEXT
        );

        final StorageValue value3 = storageValue(
            "/mount2/file3",
            "value3"
        );

        routingStorage.save(
            value3,
            CONTEXT
        );

        final StorageValue value4 = storageValue(
            "/mount2/file4",
            "value4"
        );

        routingStorage.save(
            value4,
            CONTEXT
        );

        this.listAndCheck(
            routingStorage,
            StoragePath.parse("/mount1"),
            0,
            3,
            CONTEXT,
            storageValueInfo("/mount1/file1"),
            storageValueInfo("/mount1/file2")
        );

        this.listAndCheck(
            routingStorage,
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
        final Storage<StorageContext> storage1 = Storages.treeMapStore();
        final Storage<StorageContext> store2 = Storages.treeMapStore();

        final RoutingStorage<StorageContext> routingStorage = this.createStorage(
            storage1,
            store2
        );

        final StorageValue value1 = storageValue(
            "/mount1/dir1/file1",
            "value1"
        );

        routingStorage.save(
            value1,
            CONTEXT
        );

        final StorageValue value2 = storageValue(
            "/mount1/dir1/file2",
            "value2"
        );

        routingStorage.save(
            value2,
            CONTEXT
        );

        final StorageValue value3 = storageValue(
            "/mount1/dir2/file3",
            "value3"
        );

        routingStorage.save(
            value3,
            CONTEXT
        );

        final StorageValue value4 = storageValue(
            "/mount2/file4",
            "value4"
        );

        routingStorage.save(
            value4,
            CONTEXT
        );

        this.listAndCheck(
            routingStorage,
            StoragePath.parse("/mount1/dir1/"),
            0,
            3,
            CONTEXT,
            storageValueInfo("/mount1/dir1/file1"),
            storageValueInfo("/mount1/dir1/file2")
        );
    }

    @Override
    public RoutingStorage<StorageContext> createStorage() {
        return this.createStorage(
            Storages.treeMapStore(),// /mount1
            Storages.treeMapStore() // /mount2
        );
    }

    private RoutingStorage<StorageContext> createStorage(final Storage<StorageContext>... stores) {
        return this.createStorage(
            Lists.of(stores)
        );
    }

    private RoutingStorage<StorageContext> createStorage(final List<Storage<StorageContext>> stores) {
        final RoutingStorageBuilder<StorageContext> b = RoutingStorageBuilder.empty();

        int i = 1;
        for (Storage<StorageContext> store : stores) {
            b.startsWith(
                StoragePath.parse("/mount" + i),
                store
            );

            i++;
        }

        return (RoutingStorage<StorageContext>) b.build();
    }

    @Override
    public StorageContext createContext() {
        return CONTEXT;
    }

    private static StorageValue storageValue(final String path,
                                             final String value) {
        return StorageValue.with(
            StoragePath.parse(path)
        ).setValue(
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
    public Class<RoutingStorage<StorageContext>> type() {
        return Cast.to(RoutingStorage.class);
    }
}
