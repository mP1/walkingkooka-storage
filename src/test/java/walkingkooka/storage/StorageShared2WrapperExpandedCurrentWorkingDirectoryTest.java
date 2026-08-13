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

public final class StorageShared2WrapperExpandedCurrentWorkingDirectoryTest extends StorageShared2WrapperExpandedTestCase<StorageShared2WrapperExpandedCurrentWorkingDirectory<FakeStorageContext>, FakeStorageContext>
    implements HasCurrentWorkingDirectoryTesting,
    HasNowTesting {

    private final static StoragePath CURRENT_WORKING_PREFIX_PATH = StoragePath.parse(
        StoragePath.CURRENT_WORKING_DIRECTORY_PREFIX + "/111"
    );

    private final static StorageValue CURRENT_WORKING_PREFIX_VALUE = StorageValue.with(CURRENT_WORKING_PREFIX_PATH)
        .setValue(
            Optional.of(999)
        );

    private final static StoragePath CURRENT_WORKING_DIRECTORY_PATH = StoragePath.parse(
        CURRENT_WORKING_DIRECTORY + "/111"
    );

    private final static StorageValue CURRENT_WORKING_DIRECTORY_VALUE = StorageValue.with(CURRENT_WORKING_DIRECTORY_PATH)
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
    public void testLoadCurrentWorkingDirectoryPath() {
        final StorageShared2WrapperExpandedCurrentWorkingDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        storage.storage.save(
            CURRENT_WORKING_DIRECTORY_VALUE,
            context
        );

        this.loadAndCheck(
            storage,
            CURRENT_WORKING_DIRECTORY_PATH,
            context,
            CURRENT_WORKING_DIRECTORY_VALUE
        );
    }

    @Test
    public void testLoadCurrentWorkingDirectoryPrefixPath() {
        final StorageShared2WrapperExpandedCurrentWorkingDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        storage.storage.save(
            CURRENT_WORKING_DIRECTORY_VALUE,
            context
        );

        this.loadAndCheck(
            storage,
            CURRENT_WORKING_PREFIX_PATH,
            context,
            CURRENT_WORKING_PREFIX_VALUE
        );
    }

    @Test
    public void testLoadCurrentWorkingDirectoryAndEnvRoot() {
        final StorageShared2WrapperExpandedCurrentWorkingDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext(
            Optional.of(StoragePath.ROOT)
        );

        final StoragePath path = StoragePath.parse("/value111");
        final StorageValue value = StorageValue.with(path);

        storage.storage.save(
            value,
            context
        );

        this.loadAndCheck(
            storage,
            path,
            context,
            value
        );
    }

    // save.............................................................................................................

    @Test
    public void testSaveCurrentWorkingDirectoryPath() {
        final StorageShared2WrapperExpandedCurrentWorkingDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        this.saveAndCheck(
            storage,
            CURRENT_WORKING_DIRECTORY_VALUE,
            context,
            CURRENT_WORKING_DIRECTORY_VALUE
        );

        this.loadAndCheck(
            storage.storage,
            CURRENT_WORKING_DIRECTORY_PATH,
            context,
            CURRENT_WORKING_DIRECTORY_VALUE
        );
    }

    @Test
    public void testSaveCurrentWorkingDirectoryPrefixPath() {
        final StorageShared2WrapperExpandedCurrentWorkingDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        this.saveAndCheck(
            storage,
            CURRENT_WORKING_PREFIX_VALUE,
            context,
            CURRENT_WORKING_PREFIX_VALUE
        );

        this.loadAndCheck(
            storage.storage,
            CURRENT_WORKING_DIRECTORY_PATH,
            context,
            CURRENT_WORKING_DIRECTORY_VALUE
        );
    }

    // delete...........................................................................................................

    @Test
    public void testDeleteHomePrefixDirectoryPath() {
        final StorageShared2WrapperExpandedCurrentWorkingDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        storage.delete(
            CURRENT_WORKING_PREFIX_PATH,
            context
        );

        this.loadAndCheck(
            storage.storage,
            CURRENT_WORKING_DIRECTORY_PATH,
            context
        );
    }

    @Test
    public void testDeleteCurrentWorkingDirectoryPath() {
        final StorageShared2WrapperExpandedCurrentWorkingDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        storage.delete(
            CURRENT_WORKING_DIRECTORY_PATH,
            context
        );

        this.loadAndCheck(
            storage.storage,
            CURRENT_WORKING_DIRECTORY_PATH,
            context
        );
    }

    // list.............................................................................................................

    @Test
    public void testListCurrentWorkingDirectoryPrefix() {
        final StorageShared2WrapperExpandedCurrentWorkingDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        final StoragePath path1 = StoragePath.parse(CURRENT_WORKING_DIRECTORY + "/value111");
        final StorageValue value1 = StorageValue.with(path1)
            .setValue(
                Optional.of(111)
            );

        storage.save(
            value1,
            context
        );

        final StoragePath path2 = StoragePath.parse(CURRENT_WORKING_DIRECTORY + "/value222");
        final StorageValue value2 = StorageValue.with(path2)
            .setValue(
                Optional.of(222)
            );

        storage.save(
            value2,
            context
        );

        final StoragePath path3 = StoragePath.parse(CURRENT_WORKING_DIRECTORY + "/value333");
        final StorageValue value3 = StorageValue.with(path3)
            .setValue(
                Optional.of(333)
            );

        storage.save(
            value3,
            context
        );

        final StoragePath path4 = StoragePath.parse(CURRENT_WORKING_DIRECTORY + "/value444");
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
            StoragePath.CURRENT_WORKING_DIRECTORY_PREFIX,
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
    public void testListCurrentWorkingDirectory() {
        final StorageShared2WrapperExpandedCurrentWorkingDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        final StoragePath path1 = StoragePath.parse(CURRENT_WORKING_DIRECTORY + "/value111");
        final StorageValue value1 = StorageValue.with(path1)
            .setValue(
                Optional.of(111)
            );

        storage.save(
            value1,
            context
        );

        final StoragePath path2 = StoragePath.parse(CURRENT_WORKING_DIRECTORY + "/value222");
        final StorageValue value2 = StorageValue.with(path2)
            .setValue(
                Optional.of(222)
            );

        storage.save(
            value2,
            context
        );

        final StoragePath path3 = StoragePath.parse(CURRENT_WORKING_DIRECTORY + "/value333");
        final StorageValue value3 = StorageValue.with(path3)
            .setValue(
                Optional.of(333)
            );

        storage.save(
            value3,
            context
        );

        final StoragePath path4 = StoragePath.parse(CURRENT_WORKING_DIRECTORY + "/value444");
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
            CURRENT_WORKING_DIRECTORY,
            1, // offset
            2, // count
            context,
            StorageValueInfo.with(
                StoragePath.parse(CURRENT_WORKING_DIRECTORY + "/value222"),
                context.createdAuditInfo()
            ),
            StorageValueInfo.with(
                StoragePath.parse(CURRENT_WORKING_DIRECTORY + "/value333"),
                context.createdAuditInfo()
            )
        );
    }

    @Test
    public void testListCurrentWorkingDirectoryWithEnvRoot() {
        final StorageShared2WrapperExpandedCurrentWorkingDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext(
            Optional.of(StoragePath.ROOT)
        );

        final StoragePath path1 = StoragePath.parse("/value111");
        final StorageValue value1 = StorageValue.with(path1)
            .setValue(
                Optional.of(111)
            );

        storage.save(
            value1,
            context
        );

        final StoragePath path2 = StoragePath.parse("/value222");
        final StorageValue value2 = StorageValue.with(path2)
            .setValue(
                Optional.of(222)
            );

        storage.save(
            value2,
            context
        );

        final StoragePath path3 = StoragePath.parse("/value333");
        final StorageValue value3 = StorageValue.with(path3)
            .setValue(
                Optional.of(333)
            );

        storage.save(
            value3,
            context
        );

        final StoragePath path4 = StoragePath.parse("/value444");
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
            StoragePath.ROOT,
            1, // offset
            2, // count
            context,
            StorageValueInfo.with(
                StoragePath.parse("/value222"),
                context.createdAuditInfo()
            ),
            StorageValueInfo.with(
                StoragePath.parse("/value333"),
                context.createdAuditInfo()
            )
        );
    }

    // setAuditInfo.....................................................................................................

    @Test
    public void testSetAuditInfoWithCurrentWorkingDirectoryPath() {
        final StorageShared2WrapperExpandedCurrentWorkingDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        this.saveAndCheck(
            storage,
            CURRENT_WORKING_DIRECTORY_VALUE,
            context,
            CURRENT_WORKING_DIRECTORY_VALUE
        );

        final StorageValueInfo storageValueInfo = StorageValueInfo.with(
            CURRENT_WORKING_DIRECTORY_PATH,
            DIFFERENT_AUDIT_INFO
        );

        storage.setAuditInfo(
            storageValueInfo,
            context
        );

        this.listAndCheck(
            storage.storage,
            CURRENT_WORKING_DIRECTORY_PATH,
            0, // offset
            2, // count
            context,
            storageValueInfo
        );
    }

    @Test
    public void testSetAuditInfoWithCurrentWorkingDirectoryPrefixPath() {
        final StorageShared2WrapperExpandedCurrentWorkingDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        this.saveAndCheck(
            storage,
            CURRENT_WORKING_PREFIX_VALUE,
            context,
            CURRENT_WORKING_PREFIX_VALUE
        );

        final StorageValueInfo storageValueInfo = StorageValueInfo.with(
            CURRENT_WORKING_PREFIX_PATH,
            DIFFERENT_AUDIT_INFO
        );

        storage.setAuditInfo(
            storageValueInfo,
            context
        );

        this.listAndCheck(
            storage.storage,
            CURRENT_WORKING_DIRECTORY_PATH,
            0, // offset
            2, // count
            context,
            storageValueInfo.setPath(CURRENT_WORKING_DIRECTORY_PATH)
        );
    }

    // addWatcher.......................................................................................................

    @Test
    public void testAddWatcherAndSaveReplace() {
        final StorageShared2WrapperExpandedCurrentWorkingDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        final StoragePath path = StoragePath.parse(CURRENT_WORKING_DIRECTORY + "/saveValue");

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

                    StorageShared2WrapperExpandedCurrentWorkingDirectoryTest.this.fired = true;
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
        final StorageShared2WrapperExpandedCurrentWorkingDirectory<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        final StoragePath path = StoragePath.parse(CURRENT_WORKING_DIRECTORY + "/saveValue");

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

                    StorageShared2WrapperExpandedCurrentWorkingDirectoryTest.this.fired = true;
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
    public StorageShared2WrapperExpandedCurrentWorkingDirectory<FakeStorageContext> createStorage(final Storage<FakeStorageContext> storage) {
        return Cast.to(
            StorageShared2WrapperExpandedCurrentWorkingDirectory.with(storage)
        );
    }

    @Override
    Storage<FakeStorageContext> createWrappedStorage() {
        return Storages.treeMapStore();
    }

    @Override
    public FakeStorageContext createContext() {
        return this.createContext(OPTIONAL_CURRENT_WORKING_DIRECTORY);
    }

    private FakeStorageContext createContext(final Optional<StoragePath> currentWorkingDirectory) {
        return new FakeStorageContext() {

            @Override
            public Optional<StoragePath> currentWorkingDirectory() {
                return currentWorkingDirectory;
            }

            @Override
            public LocalDateTime now() {
                return StorageShared2WrapperExpandedCurrentWorkingDirectoryTest.NOW;
            }

            @Override
            public Optional<EmailAddress> user() {
                return OPTIONAL_USER;
            }
        };
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createStorage(),
            "/cwd {}"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageShared2WrapperExpandedCurrentWorkingDirectory<FakeStorageContext>> type() {
        return Cast.to(StorageShared2WrapperExpandedCurrentWorkingDirectory.class);
    }
}
