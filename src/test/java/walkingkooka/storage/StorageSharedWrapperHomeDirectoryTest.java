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
import walkingkooka.datetime.HasNowTesting;
import walkingkooka.net.email.EmailAddress;

import java.time.LocalDateTime;
import java.util.Optional;

public final class StorageSharedWrapperHomeDirectoryTest extends StorageSharedWrapperTestCase<StorageSharedWrapperHomeDirectory<FakeStorageContext>, FakeStorageContext>
    implements HasHomeDirectoryTesting,
    HasNowTesting {

    private final static StoragePath HOME_PREFIX_PATH = StoragePath.parse(
        StoragePath.HOME_DIRECTORY_PREFIX + "/111"
    );

    private final static StorageValue HOME_PREFIX_VALUE = StorageValue.with(HOME_PREFIX_PATH)
        .setValue(
            Optional.of(999)
        );

    private final static StoragePath HOME_DIRECTORY_PATH = StoragePath.parse(
        HOME_DIRECTORY + "/111"
    );

    private final static StorageValue HOME_DIRECTORY_VALUE = StorageValue.with(HOME_DIRECTORY_PATH)
        .setValue(
            Optional.of(999)
        );

    // load.............................................................................................................

    @Test
    public void testLoadUnknown() {
        this.loadAndCheck(
            this.createStorage(),
            StoragePath.parse("/unknown"),
            this.createContext()
        );
    }

    @Test
    public void testLoadHomeDirectoryPath() {
        final StorageSharedWrapperHomeDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        storage.storage.save(
            HOME_DIRECTORY_VALUE,
            context
        );

        this.loadAndCheck(
            storage,
            HOME_DIRECTORY_PATH,
            context,
            HOME_DIRECTORY_VALUE
        );
    }

    @Test
    public void testLoadHomeDirectoryPrefixPath() {
        final StorageSharedWrapperHomeDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        storage.storage.save(
            HOME_DIRECTORY_VALUE,
            context
        );

        this.loadAndCheck(
            storage,
            HOME_PREFIX_PATH,
            context,
            HOME_PREFIX_VALUE
        );
    }

    // save.............................................................................................................

    @Test
    public void testSaveHomeDirectoryPath() {
        final StorageSharedWrapperHomeDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        this.saveAndCheck(
            storage,
            HOME_DIRECTORY_VALUE,
            context,
            HOME_DIRECTORY_VALUE
        );

        this.loadAndCheck(
            storage.storage,
            HOME_DIRECTORY_PATH,
            context,
            HOME_DIRECTORY_VALUE
        );
    }

    @Test
    public void testSaveHomeDirectoryPrefixPath() {
        final StorageSharedWrapperHomeDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        this.saveAndCheck(
            storage,
            HOME_PREFIX_VALUE,
            context,
            HOME_PREFIX_VALUE
        );

        this.loadAndCheck(
            storage.storage,
            HOME_DIRECTORY_PATH,
            context,
            HOME_DIRECTORY_VALUE
        );
    }

    // delete...........................................................................................................

    @Test
    public void testDeleteHomePrefixDirectoryPath() {
        final StorageSharedWrapperHomeDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        storage.delete(
            HOME_PREFIX_PATH,
            context
        );

        this.loadAndCheck(
            storage.storage,
            HOME_DIRECTORY_PATH,
            context
        );
    }

    @Test
    public void testDeleteHomeDirectoryPath() {
        final StorageSharedWrapperHomeDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        storage.delete(
            HOME_DIRECTORY_PATH,
            context
        );

        this.loadAndCheck(
            storage.storage,
            HOME_DIRECTORY_PATH,
            context
        );
    }

    // list.............................................................................................................

    @Test
    public void testListHomeDirectoryPrefix() {
        final StorageSharedWrapperHomeDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        final StoragePath path1 = StoragePath.parse(HOME_DIRECTORY + "/value111");
        final StorageValue value1 = StorageValue.with(path1)
            .setValue(
                Optional.of(111)
            );

        storage.save(
            value1,
            context
        );

        final StoragePath path2 = StoragePath.parse(HOME_DIRECTORY + "/value222");
        final StorageValue value2 = StorageValue.with(path2)
            .setValue(
                Optional.of(222)
            );

        storage.save(
            value2,
            context
        );

        final StoragePath path3 = StoragePath.parse(HOME_DIRECTORY + "/value333");
        final StorageValue value3 = StorageValue.with(path3)
            .setValue(
                Optional.of(333)
            );

        storage.save(
            value3,
            context
        );

        final StoragePath path4 = StoragePath.parse(HOME_DIRECTORY + "/value444");
        final StorageValue value4 = StorageValue.with(path4)
            .setValue(
                Optional.of(444)
            );

        storage.save(
            value4,
            context
        );

        this.listAndCheck(
            storage,
            StoragePath.HOME_DIRECTORY_PREFIX,
            1, // offset
            2, // count
            context,
            StorageValueInfo.with(
                path2,
                context.createdAuditInfo()
            ),
            StorageValueInfo.with(
                path3,
                context.createdAuditInfo()
            )
        );
    }

    @Test
    public void testListHomeDirectory() {
        final StorageSharedWrapperHomeDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        final StoragePath path1 = StoragePath.parse(HOME_DIRECTORY + "/value111");
        final StorageValue value1 = StorageValue.with(path1)
            .setValue(
                Optional.of(111)
            );

        storage.save(
            value1,
            context
        );

        final StoragePath path2 = StoragePath.parse(HOME_DIRECTORY + "/value222");
        final StorageValue value2 = StorageValue.with(path2)
            .setValue(
                Optional.of(222)
            );

        storage.save(
            value2,
            context
        );

        final StoragePath path3 = StoragePath.parse(HOME_DIRECTORY + "/value333");
        final StorageValue value3 = StorageValue.with(path3)
            .setValue(
                Optional.of(333)
            );

        storage.save(
            value3,
            context
        );

        final StoragePath path4 = StoragePath.parse(HOME_DIRECTORY + "/value444");
        final StorageValue value4 = StorageValue.with(path4)
            .setValue(
                Optional.of(444)
            );

        storage.save(
            value4,
            context
        );

        this.listAndCheck(
            storage,
            HOME_DIRECTORY,
            1, // offset
            2, // count
            context,
            StorageValueInfo.with(
                StoragePath.parse(StoragePath.HOME_DIRECTORY_PREFIX + "/value222"),
                context.createdAuditInfo()
            ),
            StorageValueInfo.with(
                StoragePath.parse(StoragePath.HOME_DIRECTORY_PREFIX + "/value333"),
                context.createdAuditInfo()
            )
        );
    }

    // setAuditInfo.....................................................................................................

    @Test
    public void testSetAuditInfoWithHomeDirectoryPath() {
        final StorageSharedWrapperHomeDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        this.saveAndCheck(
            storage,
            HOME_DIRECTORY_VALUE,
            context,
            HOME_DIRECTORY_VALUE
        );

        final StorageValueInfo storageValueInfo = StorageValueInfo.with(
            HOME_DIRECTORY_PATH,
            DIFFERENT_AUDIT_INFO
        );

        storage.setAuditInfo(
            storageValueInfo,
            context
        );

        this.listAndCheck(
            storage.storage,
            HOME_DIRECTORY_PATH,
            0, // offset
            2, // count
            context,
            storageValueInfo
        );
    }

    @Test
    public void testSetAuditInfoWithHomeDirectoryPrefixPath() {
        final StorageSharedWrapperHomeDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        this.saveAndCheck(
            storage,
            HOME_PREFIX_VALUE,
            context,
            HOME_PREFIX_VALUE
        );

        final StorageValueInfo storageValueInfo = StorageValueInfo.with(
            HOME_PREFIX_PATH,
            DIFFERENT_AUDIT_INFO
        );

        storage.setAuditInfo(
            storageValueInfo,
            context
        );

        this.listAndCheck(
            storage.storage,
            HOME_DIRECTORY_PATH,
            0, // offset
            2, // count
            context,
            storageValueInfo.setPath(HOME_DIRECTORY_PATH)
        );
    }

    // addWatcher.......................................................................................................

    @Test
    public void testAddWatcherAndSaveReplace() {
        final StorageSharedWrapperHomeDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        final StoragePath path = StoragePath.parse(HOME_DIRECTORY + "/saveValue");

        final StorageValue value1 = StorageValue.with(path)
            .setValue(
                Optional.of(1)
            );

        this.saveAndCheck(
            storage,
            value1,
            context,
            value1
        );

        final StorageValue value2 = StorageValue.with(path)
            .setValue(
                Optional.of(222)
            );

        this.fired = false;

        storage.addWatcher(
            new StorageWatcher() {
                @Override
                public void onValueChange(final Optional<StorageValue> oldValue,
                                          final Optional<StorageValue> newValue) {
                    checkEquals(
                        Optional.of(value1),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(value2),
                        newValue,
                        "newValue"
                    );

                    StorageSharedWrapperHomeDirectoryTest.this.fired = true;
                }
            },
            context
        );

        this.saveAndCheck(
            storage,
            value2,
            context,
            value2
        );

        this.checkEquals(
            true,
            this.fired,
            "fired"
        );
    }

    // addWatcherOnce...................................................................................................

    @Test
    public void testAddWatcherOnceAndSaveReplace() {
        final StorageSharedWrapperHomeDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        final StoragePath path = StoragePath.parse(HOME_DIRECTORY + "/saveValue");

        final StorageValue value1 = StorageValue.with(path)
            .setValue(
                Optional.of(1)
            );

        this.saveAndCheck(
            storage,
            value1,
            context,
            value1
        );

        final StorageValue value2 = StorageValue.with(path)
            .setValue(
                Optional.of(222)
            );

        this.fired = false;

        storage.addWatcherOnce(
            new StorageWatcher() {
                @Override
                public void onValueChange(final Optional<StorageValue> oldValue,
                                          final Optional<StorageValue> newValue) {
                    checkEquals(
                        Optional.of(value1),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(value2),
                        newValue,
                        "newValue"
                    );

                    StorageSharedWrapperHomeDirectoryTest.this.fired = true;
                }
            },
            context
        );

        this.saveAndCheck(
            storage,
            value2,
            context,
            value2
        );

        this.checkEquals(
            true,
            this.fired,
            "fired"
        );
    }

    private boolean fired;

    @Override
    public StorageSharedWrapperHomeDirectory<FakeStorageContext> createStorage(final Storage<FakeStorageContext> storage) {
        return Cast.to(
            StorageSharedWrapperHomeDirectory.with(storage)
        );
    }

    @Override
    Storage<FakeStorageContext> createWrappedStorage() {
        return Storages.treeMapStore();
    }

    @Override
    public FakeStorageContext createContext() {
        return new FakeStorageContext() {

            @Override
            public Optional<StoragePath> homeDirectory() {
                return OPTIONAL_HOME_DIRECTORY;
            }

            @Override
            public LocalDateTime now() {
                return StorageSharedWrapperHomeDirectoryTest.NOW;
            }

            @Override
            public Optional<EmailAddress> user() {
                return Optional.of(
                    EmailAddress.parse("user@example.com")
                );
            }
        };
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createStorage(),
            "[]"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageSharedWrapperHomeDirectory<FakeStorageContext>> type() {
        return Cast.to(StorageSharedWrapperHomeDirectory.class);
    }
}
