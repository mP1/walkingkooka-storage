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
import walkingkooka.collect.list.Lists;
import walkingkooka.environment.AuditInfo;
import walkingkooka.net.email.EmailAddress;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public final class RoutingStorageStoreTest implements StorageStoreTesting<RoutingStorageStore>,
    ToStringTesting<RoutingStorageStore> {

    private final static LocalDateTime NOW = LocalDateTime.of(
        1999,
        12,
        31,
        12,
        58
    );

    private final static EmailAddress USER = EmailAddress.parse("user@example.com");

    private final static String VALUE1 = "value1";

    @Test
    public void testSave() {
        final StorageStore store1 = this.treeStore();

        final RoutingStorageStore routingStorageStore = this.createStore(
            store1,
            StorageStores.fake()
        );

        routingStorageStore.save(
            this.storageValue(
                "/mount1/file1",
                VALUE1
            )
        );

        this.allAndCheck(
            store1,
            this.storageValue(
                "/file1",
                VALUE1
            )
        );
    }

    @Test
    public void testSaveExtraLongPath() {
        final StorageStore store1 = this.treeStore();

        final RoutingStorageStore routingStorageStore = this.createStore(
            store1,
            StorageStores.fake()
        );

        routingStorageStore.save(
            this.storageValue(
                "/mount1/dir1/dir11/dir111/file1",
                VALUE1
            )
        );

        this.allAndCheck(
            store1,
            this.storageValue(
                "/dir1/dir11/dir111/file1",
                VALUE1
            )
        );
    }

    @Override
    public void testAddSaveWatcherAndSave() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testAddSaveWatcherAndSaveFirstMount() {
        final StorageStore store1 = this.treeStore();
        final StorageStore store2 = this.treeStore();

        final RoutingStorageStore routingStorageStore = this.createStore(
            store1,
            store2
        );

        final List<StorageValue> saved = Lists.array();
        routingStorageStore.addSaveWatcher(saved::add);

        routingStorageStore.save(
            this.storageValue(
                "/mount1/file1",
                VALUE1
            )
        );

        this.checkEquals(
            Lists.of(
                this.storageValue(
                    "/file1",
                    VALUE1
                )
            ),
            saved
        );

        this.allAndCheck(
            store1,
            this.storageValue(
                "/file1",
                VALUE1
            )
        );

        this.allAndCheck(
            store2
        );
    }

    @Test
    public void testSave2ndMount() {
        final StorageStore store1 = this.treeStore();

        final RoutingStorageStore routingStorageStore = this.createStore(
            StorageStores.fake(),
            store1
        );

        routingStorageStore.save(
            this.storageValue(
                "/mount2/file1",
                VALUE1
            )
        );

        this.allAndCheck(
            store1,
            this.storageValue(
                "/file1",
                VALUE1
            )
        );
    }

    @Test
    public void testSaveAndLoad() {
        final StorageStore store1 = this.treeStore();

        final RoutingStorageStore routingStorageStore = this.createStore(
            store1,
            StorageStores.fake()
        );

        final StorageValue value =this.storageValue(
            "/mount1/file1",
            VALUE1
        );

        routingStorageStore.save(value);

        this.loadAndCheck(
            routingStorageStore,
            value.path(),
            value
        );
    }

    @Test
    public void testSaveAndLoadExtraLongPath() {
        final StorageStore store1 = this.treeStore();

        final RoutingStorageStore routingStorageStore = this.createStore(
            store1,
            StorageStores.fake()
        );

        final StorageValue value =this.storageValue(
            "/mount1/dir1/dir11/file1",
            VALUE1
        );

        routingStorageStore.save(value);

        this.loadAndCheck(
            routingStorageStore,
            value.path(),
            value
        );
    }

    @Test
    public void testSaveAndDelete() {
        final StorageStore store1 = this.treeStore();

        final RoutingStorageStore routingStorageStore = this.createStore(
            store1,
            StorageStores.fake()
        );

        routingStorageStore.save(
            this.storageValue(
                "/mount1/file1",
                VALUE1
            )
        );

        this.allAndCheck(
            store1,
            this.storageValue(
                "/file1",
                VALUE1
            )
        );

        routingStorageStore.delete(
            StoragePath.parse("/mount1/file1")
        );

        this.allAndCheck(
            store1
        );
    }

    @Override
    public void testAddDeleteWatcherAndDelete() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testAddDeleteSaveAndDelete() {
        final StorageStore store1 = this.treeStore();
        final StorageStore store2 = this.treeStore();

        final RoutingStorageStore routingStorageStore = this.createStore(
            store1,
            store2
        );

        routingStorageStore.save(
            this.storageValue(
                "/mount1/file1",
                VALUE1
            )
        );

        this.allAndCheck(
            store1,
            this.storageValue(
                "/file1",
                VALUE1
            )
        );

        final List<StoragePath> deleted = Lists.array();
        routingStorageStore.addDeleteWatcher(deleted::add);

        routingStorageStore.delete(
            StoragePath.parse("/mount1/file1")
        );

        this.allAndCheck(
            store1
        );

        this.checkEquals(
            Lists.of(
                StoragePath.parse("/file1")
            ),
            deleted
        );

        this.allAndCheck(
            store2
        );
    }

    @Test
    public void testIdsAll() {
        final StorageStore store1 = this.treeStore();
        final StorageStore store2 = this.treeStore();

        final RoutingStorageStore routingStorageStore = this.createStore(
            store1,
            store2
        );

        final StorageValue value1 = this.storageValue(
            "/mount1/file1",
            "value1"
        );

        final StorageValue value2 = this.storageValue(
            "/mount1/file2",
            "value2"
        );

        routingStorageStore.save(
            value1
        );

        routingStorageStore.save(
            value2
        );

        this.idsAndCheck(
            routingStorageStore,
            0,
            3,
            value1.path(),
            value2.path()
        );
    }

    @Test
    public void testIdsWithOffset() {
        final StorageStore store1 = this.treeStore();
        final StorageStore store2 = this.treeStore();

        final RoutingStorageStore routingStorageStore = this.createStore(
            store1,
            store2
        );

        final StorageValue value1 = this.storageValue(
            "/mount1/file1",
            "value1"
        );

        routingStorageStore.save(
            value1
        );

        final StorageValue value2 = this.storageValue(
            "/mount1/file2",
            "value2"
        );

        routingStorageStore.save(
            value2
        );

        final StorageValue value3 = this.storageValue(
            "/mount2/file3",
            "value3"
        );

        routingStorageStore.save(
            value3
        );

        final StorageValue value4 = this.storageValue(
            "/mount2/file4",
            "value4"
        );

        routingStorageStore.save(
            value4
        );

        this.idsAndCheck(
            routingStorageStore,
            1,
            4,
            value2.path(),
            value3.path(),
            value4.path()
        );
    }

    @Test
    public void testIdsWithOffsetAndCount() {
        final StorageStore store1 = this.treeStore();
        final StorageStore store2 = this.treeStore();

        final RoutingStorageStore routingStorageStore = this.createStore(
            store1,
            store2
        );

        final StorageValue value1 = this.storageValue(
            "/mount1/file1",
            "value1"
        );

        routingStorageStore.save(
            value1
        );

        final StorageValue value2 = this.storageValue(
            "/mount1/file2",
            "value2"
        );

        routingStorageStore.save(
            value2
        );

        final StorageValue value3 = this.storageValue(
            "/mount2/file3",
            "value3"
        );

        routingStorageStore.save(
            value3
        );

        final StorageValue value4 = this.storageValue(
            "/mount2/file4",
            "value4"
        );

        routingStorageStore.save(
            value4
        );

        this.idsAndCheck(
            routingStorageStore,
            1,
            2,
            value2.path(),
            value3.path()
        );
    }

    @Test
    public void testValuesAll() {
        final StorageStore store1 = this.treeStore();
        final StorageStore store2 = this.treeStore();

        final RoutingStorageStore routingStorageStore = this.createStore(
            store1,
            store2
        );

        final StorageValue value1 = this.storageValue(
            "/mount1/file1",
            "value1"
        );

        final StorageValue value2 = this.storageValue(
            "/mount1/file2",
            "value2"
        );

        routingStorageStore.save(
            value1
        );

        routingStorageStore.save(
            value2
        );

        this.valuesAndCheck(
            routingStorageStore,
            0,
            3,
            value1,
            value2
        );
    }

    @Test
    public void testValuesWithOffset() {
        final StorageStore store1 = this.treeStore();
        final StorageStore store2 = this.treeStore();

        final RoutingStorageStore routingStorageStore = this.createStore(
            store1,
            store2
        );

        final StorageValue value1 = this.storageValue(
            "/mount1/file1",
            "value1"
        );

        routingStorageStore.save(
            value1
        );

        final StorageValue value2 = this.storageValue(
            "/mount1/file2",
            "value2"
        );

        routingStorageStore.save(
            value2
        );

        final StorageValue value3 = this.storageValue(
            "/mount2/file3",
            "value3"
        );

        routingStorageStore.save(
            value3
        );

        final StorageValue value4 = this.storageValue(
            "/mount2/file4",
            "value4"
        );

        routingStorageStore.save(
            value4
        );

        this.valuesAndCheck(
            routingStorageStore,
            1,
            4,
            value2,
            value3,
            value4
        );
    }

    @Test
    public void testValuesWithOffsetAndCount() {
        final StorageStore store1 = this.treeStore();
        final StorageStore store2 = this.treeStore();

        final RoutingStorageStore routingStorageStore = this.createStore(
            store1,
            store2
        );

        final StorageValue value1 = this.storageValue(
            "/mount1/file1",
            "value1"
        );

        routingStorageStore.save(
            value1
        );

        final StorageValue value2 = this.storageValue(
            "/mount1/file2",
            "value2"
        );

        routingStorageStore.save(
            value2
        );

        final StorageValue value3 = this.storageValue(
            "/mount2/file3",
            "value3"
        );

        routingStorageStore.save(
            value3
        );

        final StorageValue value4 = this.storageValue(
            "/mount2/file4",
            "value4"
        );

        routingStorageStore.save(
            value4
        );

        this.valuesAndCheck(
            routingStorageStore,
            1,
            2,
            value2,
            value3
        );
    }

    @Test
    public void testBetween() {
        final StorageStore store1 = this.treeStore();
        final StorageStore store2 = this.treeStore();

        final RoutingStorageStore routingStorageStore = this.createStore(
            store1,
            store2
        );

        final StorageValue value1 = this.storageValue(
            "/mount1/file1",
            "value1"
        );

        routingStorageStore.save(
            value1
        );

        final StorageValue value2 = this.storageValue(
            "/mount1/file2",
            "value2"
        );

        routingStorageStore.save(
            value2
        );

        final StorageValue value3 = this.storageValue(
            "/mount1/file3",
            "value3"
        );

        routingStorageStore.save(
            value3
        );

        final StorageValue value4 = this.storageValue(
            "/mount2/file4",
            "value4"
        );

        routingStorageStore.save(
            value4
        );

        final StorageValue value5 = this.storageValue(
            "/mount2/file5",
            "value5"
        );

        routingStorageStore.save(
            value5
        );

        this.betweenAndCheck(
            routingStorageStore,
            value2.path(),
            value4.path(),
            value2,
            value3,
            value4
        );
    }

    @Test
    public void testBetween2() {
        final StorageStore store1 = this.treeStore();
        final StorageStore store2 = this.treeStore();

        final RoutingStorageStore routingStorageStore = this.createStore(
            store1,
            store2
        );

        final StorageValue value1 = this.storageValue(
            "/mount1/file10",
            "value1"
        );

        routingStorageStore.save(
            value1
        );

        final StorageValue value2 = this.storageValue(
            "/mount1/file20",
            "value2"
        );

        routingStorageStore.save(
            value2
        );

        final StorageValue value3 = this.storageValue(
            "/mount1/file30",
            "value3"
        );

        routingStorageStore.save(
            value3
        );

        final StorageValue value4 = this.storageValue(
            "/mount1/file40",
            "value4"
        );

        routingStorageStore.save(
            value4
        );

        final StorageValue value5 = this.storageValue(
            "/mount1/file50",
            "value5"
        );

        routingStorageStore.save(
            value5
        );

        this.betweenAndCheck(
            routingStorageStore,
            StoragePath.parse("/mount1/file11"),
            value4.path(),
            value2,
            value3,
            value4
        );
    }

    @Test
    public void testStorageValueInfos() {
        final StorageStore store1 = this.treeStore();
        final StorageStore store2 = this.treeStore();

        final RoutingStorageStore routingStorageStore = this.createStore(
            store1,
            store2
        );

        final StorageValue value1 = this.storageValue(
            "/mount1/file1",
            "value1"
        );

        routingStorageStore.save(
            value1
        );

        final StorageValue value2 = this.storageValue(
            "/mount1/file2",
            "value2"
        );

        routingStorageStore.save(
            value2
        );

        final StorageValue value3 = this.storageValue(
            "/mount2/file3",
            "value3"
        );

        routingStorageStore.save(
            value3
        );

        final StorageValue value4 = this.storageValue(
            "/mount2/file4",
            "value4"
        );

        routingStorageStore.save(
            value4
        );

        this.storageValueInfosAndCheck(
            routingStorageStore,
            StoragePath.parse("/mount1"),
            0,
            3,
            storageValueInfo("/mount1/file1"),
            storageValueInfo("/mount1/file2")
        );

        this.storageValueInfosAndCheck(
            routingStorageStore,
            StoragePath.parse("/mount2"),
            0,
            3,
            storageValueInfo("/mount2/file3"),
            storageValueInfo("/mount2/file4")
        );
    }

    @Test
    public void testStorageValueInfosSubDirectoryOfMount() {
        final StorageStore store1 = this.treeStore();
        final StorageStore store2 = this.treeStore();

        final RoutingStorageStore routingStorageStore = this.createStore(
            store1,
            store2
        );

        final StorageValue value1 = this.storageValue(
            "/mount1/dir1/file1",
            "value1"
        );

        routingStorageStore.save(
            value1
        );

        final StorageValue value2 = this.storageValue(
            "/mount1/dir1/file2",
            "value2"
        );

        routingStorageStore.save(
            value2
        );

        final StorageValue value3 = this.storageValue(
            "/mount1/dir2/file3",
            "value3"
        );

        routingStorageStore.save(
            value3
        );

        final StorageValue value4 = this.storageValue(
            "/mount2/file4",
            "value4"
        );

        routingStorageStore.save(
            value4
        );

        this.storageValueInfosAndCheck(
            routingStorageStore,
            StoragePath.parse("/mount1/dir1/"),
            0,
            3,
            storageValueInfo("/mount1/dir1/file1"),
            storageValueInfo("/mount1/dir1/file2")
        );
    }

    @Override
    public RoutingStorageStore createStore() {
        return this.createStore(
            treeStore(),// /mount1
            treeStore() // /mount2
        );
    }

    private RoutingStorageStore createStore(final StorageStore...stores) {
        return this.createStore(
            Lists.of(stores)
        );
    }

    private RoutingStorageStore createStore(final List<StorageStore> stores) {
        final RoutingStorageStoreBuilder b = RoutingStorageStoreBuilder.empty();

        int i = 1;
        for(StorageStore store : stores) {
            b.startsWith(
                StoragePath.parse("/mount" + i),
                store
            );

            i++;
        }

        return (RoutingStorageStore)b.build();
    }

    private StorageStore treeStore() {
        return StorageStores.tree(
            new FakeStorageStoreContext() {
                @Override
                public LocalDateTime now() {
                    return NOW;
                }

                @Override
                public Optional<EmailAddress> user() {
                    return Optional.of(USER);
                }
            }
        );
    }

    private StorageValue storageValue(final String path,
                                      final String value) {
        return StorageValue.with(
            StoragePath.parse(path),
            Optional.of(value)
        );
    }

    private StorageValueInfo storageValueInfo(final String path) {
        return StorageValueInfo.with(
            StoragePath.parse(path),
            AuditInfo.with(
                USER,
                NOW,
                USER,
                NOW
            )
        );
    }

    @Override
    public StoragePath id() {
        return StoragePath.parse("/mount1/file1");
    }

    @Override
    public StorageValue value() {
        return StorageValue.with(
            this.id(),
            Optional.of("value111")
        );
    }

    @Override
    public void testAddSaveWatcherAndSaveTwiceFiresOnce() {
        throw new UnsupportedOperationException();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {

    }

    // class............................................................................................................

    @Override
    public Class<RoutingStorageStore> type() {
        return RoutingStorageStore.class;
    }
}
