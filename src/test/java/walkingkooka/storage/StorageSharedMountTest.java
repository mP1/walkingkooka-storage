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
import walkingkooka.environment.HasUserTesting;
import walkingkooka.net.email.EmailAddress;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageSharedMountTest extends StorageSharedTestCase<StorageSharedMount<StorageContext>, StorageContext>
    implements HasNowTesting,
    HasUserTesting {

    private final static StorageContext CONTEXT = new FakeStorageContext() {
        @Override
        public LocalDateTime now() {
            return StorageSharedMountTest.NOW;
        }

        @Override
        public Optional<EmailAddress> user() {
            return OPTIONAL_USER;
        }
    };

    private final static StoragePath ROOT_VALUE_PATH = StoragePath.parse("/value999");

    private final static StoragePath MOUNT1_PATH = StoragePath.parse("/mount1");

    private final static StoragePath VALUE1_PATH = StoragePath.parse("/value1");

    private final static StoragePath MOUNT1_VALUE_PATH = StoragePath.parse(
        "" + MOUNT1_PATH + VALUE1_PATH
    );

    private final static StoragePath MOUNT2_PATH = StoragePath.parse("/mount1/mount2");

    private final static StoragePath VALUE2_PATH = StoragePath.parse("/value2");

    private final static StoragePath MOUNT2_VALUE_PATH = StoragePath.parse(
        "" + MOUNT2_PATH + VALUE2_PATH
    );

    private final static Storage<StorageContext> READ_ONLY = Storages.readOnly(
        Storages.treeMapStore()
    );

    private final static StorageValue ROOT_VALUE = StorageValue.with(ROOT_VALUE_PATH)
        .setValue(
            Optional.of("root")
        );

    private final static StorageValue MOUNT1_VALUE = StorageValue.with(MOUNT1_VALUE_PATH)
        .setValue(
            Optional.of("value1")
        );

    private final static StorageValue MOUNT2_VALUE = StorageValue.with(MOUNT2_VALUE_PATH)
        .setValue(
            Optional.of("value2")
        );

    // mount............................................................................................................

    @Test
    public void testMountDuplicateFails() {
        final Storage<StorageContext> root = Storages.fake();

        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(root);
        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                Storages.fake()
            ),
            CONTEXT
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> storage.mount(
                StorageMountPoint.with(
                    MOUNT1_PATH,
                    Storages.fake()
                ),
                CONTEXT
            )
        );
    }

    @Test
    public void testMountDuplicateFails2() {
        final Storage<StorageContext> root = Storages.fake();

        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(root);

        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                Storages.fake()
            ),
            CONTEXT
        );

        storage.mount(
            StorageMountPoint.with(
                MOUNT2_PATH,
                Storages.fake()
            ),
            CONTEXT
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> storage.mount(
                StorageMountPoint.with(
                    MOUNT2_PATH,
                    Storages.fake()
                ),
                CONTEXT
            )
        );
    }

    // mountPoints......................................................................................................

    @Test
    public void testMountPoints() {
        final Storage<StorageContext> root = Storages.fake();

        final StorageMountPoint<StorageContext> mountPoint1 = StorageMountPoint.with(
            MOUNT1_PATH,
            Storages.fake()
        );

        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(root);
        storage.mount(
            mountPoint1,
            CONTEXT
        );

        this.mountPointsAndCheck(
            storage,
            mountPoint1
        );
    }

    // firstMount.......................................................................................................

    @Test
    public void testFirstMountWhenOnlyMount() {
        final Storage<StorageContext> root = Storages.fake();

        this.firstMountAndCheck(
            StorageSharedMount.with(root),
            StoragePath.ROOT,
            StorageMountPoint.with(
                StoragePath.ROOT,
                root
            )
        );
    }

    @Test
    public void testFirstMountWhenOnlyMount1() {
        final Storage<StorageContext> root = Storages.fake();

        this.firstMountAndCheck(
            StorageSharedMount.with(root),
            StoragePath.parse("/path1/value1"),
            StorageMountPoint.with(
                StoragePath.ROOT,
                root
            )
        );
    }

    @Test
    public void testFirstMountWithMount1() {
        final Storage<StorageContext> root = Storages.fake();

        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(root);

        final Storage<StorageContext> mount1 = Storages.fake();
        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                mount1
            ),
            CONTEXT
        );

        this.firstMountAndCheck(
            storage,
            MOUNT1_PATH,
            StorageMountPoint.with(
                MOUNT1_PATH,
                mount1
            )
        );
    }

    @Test
    public void testFirstMountWithMount12() {
        final Storage<StorageContext> root = Storages.fake();

        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(root);

        final Storage<StorageContext> mount1 = Storages.fake();
        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                mount1
            ),
            CONTEXT
        );

        this.firstMountAndCheck(
            storage,
            MOUNT1_VALUE_PATH,
            StorageMountPoint.with(
                MOUNT1_PATH,
                mount1
            )
        );
    }

    @Test
    public void testFirstMountWithMount2() {
        final Storage<StorageContext> root = Storages.fake();

        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(root);

        final Storage<StorageContext> mount1 = Storages.fake();
        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                mount1
            ),
            CONTEXT
        );

        final Storage<StorageContext> mount2 = Storages.fake();
        storage.mount(
            StorageMountPoint.with(
                MOUNT2_PATH,
                mount2
            ),
            CONTEXT
        );

        this.firstMountAndCheck(
            storage,
            MOUNT2_VALUE_PATH,
            StorageMountPoint.with(
                MOUNT2_PATH,
                mount2
            )
        );
    }

    private void firstMountAndCheck(final StorageSharedMount<StorageContext> storage,
                                    final StoragePath path,
                                    final StorageMountPoint<StorageContext> expected) {
        this.checkEquals(
            expected,
            storage.firstMount(path),
            storage::toString
        );
    }

    // canRead.........................................................................................................

    @Test
    public void testCanReadWithoutMounts() {
        final Storage<StorageContext> root = Storages.treeMapStore();

        root.save(
            ROOT_VALUE,
            CONTEXT
        );

        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(root);

        this.canReadAndCheck(
            storage,
            ROOT_VALUE_PATH,
            CONTEXT,
            true
        );
    }

    @Test
    public void testCanReadWithoutMounts2() {
        final Storage<StorageContext> root = Storages.treeMapStore();

        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(root);

        this.canReadAndCheck(
            storage,
            ROOT_VALUE_PATH,
            CONTEXT,
            false
        );
    }

    @Test
    public void testCanReadWithMountAndMountPath() {
        final Storage<StorageContext> mount1 = Storages.treeMapStore();

        mount1.save(
            MOUNT1_VALUE.setPath(VALUE1_PATH),
            CONTEXT
        );

        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(Storages.fake());

        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                mount1
            ),
            CONTEXT
        );

        this.canReadAndCheck(
            storage,
            MOUNT1_VALUE_PATH,
            CONTEXT,
            true
        );
    }

    @Test
    public void testCanReadWithMountAndRootPath() {
        final Storage<StorageContext> root = Storages.treeMapStore();

        root.save(
            ROOT_VALUE,
            CONTEXT
        );

        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(root);

        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                Storages.fake()
            ),
            CONTEXT
        );

        this.canReadAndCheck(
            storage,
            ROOT_VALUE_PATH,
            CONTEXT,
            true
        );
    }

    // canWrite.........................................................................................................

    @Test
    public void testCanWriteWithoutMounts() {
        final Storage<StorageContext> root = Storages.treeMapStore();

        root.save(
            ROOT_VALUE,
            CONTEXT
        );

        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(root);

        this.canWriteAndCheck(
            storage,
            ROOT_VALUE_PATH,
            CONTEXT,
            true
        );
    }

    @Test
    public void testCanWriteWithoutMounts2() {
        final Storage<StorageContext> root = Storages.treeMapStore();

        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(root);

        this.canWriteAndCheck(
            storage,
            ROOT_VALUE_PATH,
            CONTEXT,
            true
        );
    }

    @Test
    public void testCanWriteWithMountAndMountPath() {
        final Storage<StorageContext> mount1 = Storages.treeMapStore();

        mount1.save(
            MOUNT1_VALUE.setPath(VALUE1_PATH),
            CONTEXT
        );

        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(Storages.fake());

        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                mount1
            ),
            CONTEXT
        );

        this.canWriteAndCheck(
            storage,
            MOUNT1_VALUE_PATH,
            CONTEXT,
            true
        );
    }

    @Test
    public void testCanWriteWithMountAndRootPath() {
        final Storage<StorageContext> root = Storages.treeMapStore();

        root.save(
            ROOT_VALUE,
            CONTEXT
        );

        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(root);

        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                Storages.fake()
            ),
            CONTEXT
        );

        this.canWriteAndCheck(
            storage,
            ROOT_VALUE_PATH,
            CONTEXT,
            true
        );
    }

    // load.............................................................................................................

    @Test
    public void testLoadWithOnlyRoot() {
        final Storage<StorageContext> root = Storages.treeMapStore();
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(root);

        storage.save(
            ROOT_VALUE,
            CONTEXT
        );

        this.loadAndCheck(
            storage,
            ROOT_VALUE_PATH,
            CONTEXT,
            ROOT_VALUE
        );
    }

    @Test
    public void testLoadWithMount1RootValue() {
        final Storage<StorageContext> root = Storages.treeMapStore();
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(root);

        storage.save(
            ROOT_VALUE,
            CONTEXT
        );

        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                Storages.fake()
            ),
            CONTEXT
        );

        this.loadAndCheck(
            storage,
            ROOT_VALUE_PATH,
            CONTEXT,
            ROOT_VALUE
        );
    }

    @Test
    public void testLoadWithMount1Mount1Value() {
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(Storages.fake());

        final Storage<StorageContext> mount1 = Storages.treeMapStore();
        mount1.save(
            MOUNT1_VALUE.setPath(VALUE1_PATH),
            CONTEXT
        );

        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                mount1
            ),
            CONTEXT
        );

        this.loadAndCheck(
            storage,
            MOUNT1_VALUE_PATH,
            CONTEXT,
            MOUNT1_VALUE
        );
    }

    @Test
    public void testLoadWithMount1Mount2Mount1Value() {
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(Storages.fake());

        final Storage<StorageContext> mount1 = Storages.treeMapStore();
        mount1.save(
            MOUNT1_VALUE.setPath(VALUE1_PATH),
            CONTEXT
        );

        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                mount1
            ),
            CONTEXT
        );

        storage.mount(
            StorageMountPoint.with(
                MOUNT2_PATH,
                Storages.fake()
            ),
            CONTEXT
        );

        this.loadAndCheck(
            storage,
            MOUNT1_VALUE_PATH,
            CONTEXT,
            MOUNT1_VALUE
        );
    }

    @Test
    public void testLoadWithMount1Mount2Mount2Value() {
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(Storages.fake());

        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                Storages.fake()
            ),
            CONTEXT
        );

        final Storage<StorageContext> mount2 = Storages.treeMapStore();
        mount2.save(
            MOUNT2_VALUE.setPath(VALUE2_PATH),
            CONTEXT
        );

        storage.mount(
            StorageMountPoint.with(
                MOUNT2_PATH,
                mount2
            ),
            CONTEXT
        );

        this.loadAndCheck(
            storage,
            MOUNT2_VALUE_PATH,
            CONTEXT,
            MOUNT2_VALUE
        );
    }

    // save.............................................................................................................

    @Test
    public void testSaveWithOnlyRoot() {
        final Storage<StorageContext> root = Storages.treeMapStore();
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(root);

        this.saveAndCheck(
            storage,
            ROOT_VALUE,
            CONTEXT,
            ROOT_VALUE
        );
    }

    @Test
    public void testSaveWithMount1RootValue() {
        final Storage<StorageContext> root = Storages.treeMapStore();
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(root);

        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                Storages.fake()
            ),
            CONTEXT
        );

        this.saveAndCheck(
            storage,
            ROOT_VALUE,
            CONTEXT,
            ROOT_VALUE
        );

        this.loadAndCheck(
            root,
            ROOT_VALUE_PATH,
            CONTEXT,
            ROOT_VALUE
        );
    }

    @Test
    public void testSaveWithMount1AndSaveMount1Value() {
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(Storages.fake());

        final Storage<StorageContext> mount1 = Storages.treeMapStore();

        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                mount1
            ),
            CONTEXT
        );

        this.saveAndCheck(
            storage,
            MOUNT1_VALUE,
            CONTEXT,
            MOUNT1_VALUE
        );

        this.loadAndCheck(
            mount1,
            VALUE1_PATH,
            CONTEXT,
            MOUNT1_VALUE.setPath(VALUE1_PATH)
        );
    }

    @Test
    public void testSaveWithMount1Mount2AndSaveMount1Value() {
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(Storages.fake());

        final Storage<StorageContext> mount1 = Storages.treeMapStore();

        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                mount1
            ),
            CONTEXT
        );

        storage.mount(
            StorageMountPoint.with(
                MOUNT2_PATH,
                Storages.fake()
            ),
            CONTEXT
        );

        this.saveAndCheck(
            storage,
            MOUNT1_VALUE,
            CONTEXT,
            MOUNT1_VALUE
        );

        this.loadAndCheck(
            mount1,
            VALUE1_PATH,
            CONTEXT,
            MOUNT1_VALUE.setPath(VALUE1_PATH)
        );
    }

    @Test
    public void testSaveWithMount1Mount2AndSaveMount2Value() {
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(Storages.fake());

        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                Storages.fake()
            ),
            CONTEXT
        );

        final Storage<StorageContext> mount2 = Storages.treeMapStore();

        storage.mount(
            StorageMountPoint.with(
                MOUNT2_PATH,
                mount2
            ),
            CONTEXT
        );

        this.saveAndCheck(
            storage,
            MOUNT2_VALUE,
            CONTEXT,
            MOUNT2_VALUE
        );

        this.loadAndCheck(
            mount2,
            VALUE2_PATH,
            CONTEXT,
            MOUNT2_VALUE.setPath(VALUE2_PATH)
        );
    }

    // delete...........................................................................................................

    @Test
    public void testDeleteWithOnlyRoot() {
        final Storage<StorageContext> root = Storages.treeMapStore();
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(root);

        this.saveAndCheck(
            storage,
            ROOT_VALUE,
            CONTEXT,
            ROOT_VALUE
        );

        storage.delete(
            ROOT_VALUE_PATH,
            CONTEXT
        );

        this.loadAndCheck(
            storage,
            ROOT_VALUE_PATH,
            CONTEXT
        );
    }

    @Test
    public void testDeleteWithMount1RootValue() {
        final Storage<StorageContext> root = Storages.treeMapStore();
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(root);

        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                Storages.fake()
            ),
            CONTEXT
        );

        this.saveAndCheck(
            storage,
            ROOT_VALUE,
            CONTEXT,
            ROOT_VALUE
        );

        storage.delete(
            ROOT_VALUE_PATH,
            CONTEXT
        );

        this.loadAndCheck(
            root,
            ROOT_VALUE_PATH,
            CONTEXT
        );
    }

    @Test
    public void testDeleteWithMount1AndDeleteMount1Value() {
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(Storages.fake());

        final Storage<StorageContext> mount1 = Storages.treeMapStore();

        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                mount1
            ),
            CONTEXT
        );

        this.saveAndCheck(
            storage,
            MOUNT1_VALUE,
            CONTEXT,
            MOUNT1_VALUE
        );

        storage.delete(
            MOUNT1_VALUE_PATH,
            CONTEXT
        );

        this.loadAndCheck(
            mount1,
            VALUE1_PATH,
            CONTEXT
        );
    }

    @Test
    public void testDeleteWithMount1Mount2AndDeleteMount1Value() {
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(Storages.fake());

        final Storage<StorageContext> mount1 = Storages.treeMapStore();

        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                mount1
            ),
            CONTEXT
        );

        storage.mount(
            StorageMountPoint.with(
                MOUNT2_PATH,
                Storages.fake()
            ),
            CONTEXT
        );

        this.saveAndCheck(
            storage,
            MOUNT1_VALUE,
            CONTEXT,
            MOUNT1_VALUE
        );

        storage.delete(
            MOUNT1_VALUE_PATH,
            CONTEXT
        );

        this.loadAndCheck(
            mount1,
            VALUE1_PATH,
            CONTEXT
        );
    }

    @Test
    public void testDeleteWithMount1Mount2AndDeleteMount2Value() {
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(Storages.fake());

        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                Storages.fake()
            ),
            CONTEXT
        );

        final Storage<StorageContext> mount2 = Storages.treeMapStore();

        storage.mount(
            StorageMountPoint.with(
                MOUNT2_PATH,
                mount2
            ),
            CONTEXT
        );

        this.saveAndCheck(
            storage,
            MOUNT2_VALUE,
            CONTEXT,
            MOUNT2_VALUE
        );

        storage.delete(
            MOUNT2_VALUE_PATH,
            CONTEXT
        );

        this.loadAndCheck(
            mount2,
            VALUE2_PATH,
            CONTEXT
        );
    }

    // list.............................................................................................................

    @Test
    public void testListWithOnlyRoot() {
        final Storage<StorageContext> root = Storages.treeMapStore();
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(root);

        this.saveAndCheck(
            storage,
            ROOT_VALUE,
            CONTEXT,
            ROOT_VALUE
        );

        this.listAndCheck(
            storage,
            StoragePath.ROOT,
            0,
            2,
            CONTEXT,
            storageValueInfo(ROOT_VALUE_PATH)
        );
    }

    @Test
    public void testListWithOnlyRootAndSubStorage() {
        final Storage<StorageContext> root = Storages.treeMapStore();
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(root);

        this.saveAndCheck(
            storage,
            ROOT_VALUE,
            CONTEXT,
            ROOT_VALUE
        );

        this.saveAndCheck(
            storage,
            MOUNT1_VALUE,
            CONTEXT,
            MOUNT1_VALUE
        );

        this.listAndCheck(
            storage,
            StoragePath.ROOT,
            0,
            3,
            CONTEXT,
            storageValueInfo(MOUNT1_PATH),
            storageValueInfo(ROOT_VALUE_PATH)
        );
    }

    @Test
    public void testListWithSubStoragePath() {
        final Storage<StorageContext> root = Storages.treeMapStore();
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(root);

        this.saveAndCheck(
            storage,
            ROOT_VALUE,
            CONTEXT,
            ROOT_VALUE
        );

        this.saveAndCheck(
            storage,
            MOUNT1_VALUE,
            CONTEXT,
            MOUNT1_VALUE
        );

        this.listAndCheck(
            storage,
            MOUNT1_PATH,
            0,
            2,
            CONTEXT,
            storageValueInfo(MOUNT1_VALUE_PATH)
        );
    }

    // unmount..........................................................................................................

    @Test
    public void testMountUnmount() {
        final Storage<StorageContext> root = Storages.treeMapStore();
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(root);

        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                Storages.fake()
            ),
            CONTEXT
        );

        storage.unmount(
            MOUNT1_PATH,
            CONTEXT
        );

        this.saveAndCheck(
            storage,
            MOUNT1_VALUE,
            CONTEXT,
            MOUNT1_VALUE
        );

        this.loadAndCheck(
            root,
            MOUNT1_VALUE_PATH,
            CONTEXT,
            MOUNT1_VALUE
        );

        this.listAndCheck(
            root,
            MOUNT1_VALUE_PATH,
            0,
            2,
            CONTEXT,
            storageValueInfo(MOUNT1_VALUE_PATH)
        );
    }

    // addWatcher.......................................................................................................

    @Test
    public void testAddWatcherAndSave() {
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(Storages.treeMapStore());

        this.fired = false;

        storage.addWatcher(
            new StorageWatcher() {
                @Override
                public void onValueChange(final Optional<StorageValue> oldValue,
                                          final Optional<StorageValue> newValue) {
                    checkEquals(
                        Optional.empty(),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(ROOT_VALUE),
                        newValue,
                        "newValue"
                    );

                    StorageSharedMountTest.this.fired = true;
                }
            },
            CONTEXT
        );

        storage.save(
            ROOT_VALUE,
            CONTEXT
        );

        this.checkEquals(
            true,
            this.fired,
            "fired"
        );
    }

    @Test
    public void testAddWatcherMount1AndSaveMount1() {
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(Storages.treeMapStore());

        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                Storages.treeMapStore()
            ),
            CONTEXT
        );

        this.fired = false;

        storage.addWatcher(
            new StorageWatcher() {
                @Override
                public void onValueChange(final Optional<StorageValue> oldValue,
                                          final Optional<StorageValue> newValue) {
                    checkEquals(
                        Optional.empty(),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(MOUNT1_VALUE),
                        newValue,
                        "newValue"
                    );

                    StorageSharedMountTest.this.fired = true;
                }
            },
            CONTEXT
        );

        this.saveAndCheck(
            storage,
            MOUNT1_VALUE,
            CONTEXT,
            MOUNT1_VALUE
        );

        this.checkEquals(
            true,
            this.fired,
            "fired"
        );
    }

    @Test
    public void testAddWatcherMount1Mount2AndSaveMount1() {
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(Storages.treeMapStore());

        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                Storages.treeMapStore()
            ),
            CONTEXT
        );

        this.fired = false;

        storage.addWatcher(
            new StorageWatcher() {
                @Override
                public void onValueChange(final Optional<StorageValue> oldValue,
                                          final Optional<StorageValue> newValue) {
                    checkEquals(
                        Optional.empty(),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(MOUNT1_VALUE),
                        newValue,
                        "newValue"
                    );

                    StorageSharedMountTest.this.fired = true;
                }
            },
            CONTEXT
        );

        this.saveAndCheck(
            storage,
            MOUNT1_VALUE,
            CONTEXT,
            MOUNT1_VALUE
        );

        this.checkEquals(
            true,
            this.fired,
            "fired"
        );
    }

    @Test
    public void testAddWatcherMount1Mount2AndSaveMount2() {
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(Storages.treeMapStore());

        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                Storages.treeMapStore()
            ),
            CONTEXT
        );

        storage.mount(
            StorageMountPoint.with(
                MOUNT2_PATH,
                Storages.treeMapStore()
            ),
            CONTEXT
        );

        this.fired = false;

        storage.addWatcher(
            new StorageWatcher() {
                @Override
                public void onValueChange(final Optional<StorageValue> oldValue,
                                          final Optional<StorageValue> newValue) {
                    checkEquals(
                        Optional.empty(),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(MOUNT2_VALUE),
                        newValue,
                        "newValue"
                    );

                    StorageSharedMountTest.this.fired = true;
                }
            },
            CONTEXT
        );

        this.saveAndCheck(
            storage,
            MOUNT2_VALUE,
            CONTEXT,
            MOUNT2_VALUE
        );

        this.checkEquals(
            true,
            this.fired,
            "fired"
        );
    }


    // addWatcherOnce...................................................................................................

    @Test
    public void testAddWatcherOnceAndSave() {
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(Storages.treeMapStore());

        final StorageValue previous = ROOT_VALUE.setValue(
            Optional.of("Different")
        );

        storage.save(
            previous,
            CONTEXT
        );

        this.fired = false;

        storage.addWatcherOnce(
            new StorageWatcher() {
                @Override
                public void onValueChange(final Optional<StorageValue> oldValue,
                                          final Optional<StorageValue> newValue) {
                    checkEquals(
                        Optional.of(previous),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(ROOT_VALUE),
                        newValue,
                        "newValue"
                    );

                    StorageSharedMountTest.this.fired = true;
                }
            },
            CONTEXT
        );

        storage.save(
            ROOT_VALUE,
            CONTEXT
        );

        this.checkEquals(
            true,
            this.fired,
            "fired"
        );

        storage.save(
            ROOT_VALUE.setValue(
                Optional.of("DifferentUnseenByWatcher")
            ),
            CONTEXT
        );
    }

    private boolean fired;

    @Override
    public StorageSharedMount<StorageContext> createStorage() {
        return StorageSharedMount.with(
            Storages.treeMapStore()
        );
    }

    @Override
    public StorageContext createContext() {
        return CONTEXT;
    }

    private static StorageValueInfo storageValueInfo(final StoragePath path) {
        return StorageValueInfo.with(
            path,
            AUDIT_INFO
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createStorage(),
            "[\"/*\" []]"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testPrintTree() {
        final StorageSharedMount<StorageContext> storage = StorageSharedMount.with(Storages.treeMapStore());

        storage.mount(
            StorageMountPoint.with(
                MOUNT1_PATH,
                Storages.treeMapStore()
            ),
            CONTEXT
        );

        storage.mount(
            StorageMountPoint.with(
                MOUNT2_PATH,
                Storages.treeMapStore()
            ),
            CONTEXT
        );

        this.treePrintAndCheck(
            storage,
            "StorageSharedMount\n" +
                "  StorageMountPoint\n" +
                "    \"/mount1/mount2\"\n" +
                "      StorageShared2TreeMapStore\n" +
                "        TreeMapStore\n" +
                "  StorageMountPoint\n" +
                "    \"/mount1\"\n" +
                "      StorageShared2TreeMapStore\n" +
                "        TreeMapStore\n" +
                "  StorageMountPoint\n" +
                "    \"/\"\n" +
                "      StorageShared2TreeMapStore\n" +
                "        TreeMapStore\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageSharedMount<StorageContext>> type() {
        return Cast.to(StorageSharedMount.class);
    }
}
