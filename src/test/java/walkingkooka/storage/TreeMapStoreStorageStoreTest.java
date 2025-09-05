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
import walkingkooka.environment.AuditInfo;
import walkingkooka.net.email.EmailAddress;

import java.time.LocalDateTime;
import java.util.Optional;

public class TreeMapStoreStorageStoreTest implements StorageStoreTesting<TreeMapStoreStorageStore> {

    private final static StoragePath PATH = StoragePath.parse("/path123");

    private final static String VALUE = "value456";

    private final static EmailAddress USER = EmailAddress.parse("user123@example.com");

    private final static LocalDateTime TIMESTAMP = LocalDateTime.parse("1999-12-31T12:58:59");

    private final static AuditInfo AUDIT_INFO = AuditInfo.with(
        USER,
        TIMESTAMP,
        USER,
        TIMESTAMP
    );

    @Test
    public void testSaveAndLoad() {
        final TestStorageContext context = new TestStorageContext();
        final TreeMapStoreStorageStore store = this.createStore(context);

        final StorageValue value = this.value();

        store.save(value);

        this.loadAndCheck(
            store,
            value.path(),
            value
        );
    }

    @Test
    public void testBuildPathSaveAndLoad() {
        final TestStorageContext context = new TestStorageContext();
        final TreeMapStoreStorageStore store = this.createStore(context);

        final StoragePath base = StoragePath.parse("/base");

        final StorageValue value1 = StorageValue.with(
            base.append(StorageName.with("file1.txt")),
            Optional.of("value1")
        );

        store.save(value1);

        this.loadAndCheck(
            store,
            StoragePath.parse("/base/file1.txt"),
            value1
        );

        final StorageValue value2 = StorageValue.with(
            base.append(StorageName.with("file2.txt")),
            Optional.of("value2")
        );

        store.save(value2);

        this.loadAndCheck(
            store,
            StoragePath.parse("/base/file2.txt"),
            value2
        );
    }

    @Test
    public void testSaveAndStorageValueInfos() {
        final TestStorageContext context = new TestStorageContext();
        final TreeMapStoreStorageStore store = this.createStore(context);

        final StorageValue value = this.value();

        store.save(value);

        this.storageValueInfosAndCheck(
            store,
            PATH.parent()
                .get(),
            0,
            2,
            StorageValueInfo.with(
                PATH,
                AUDIT_INFO
            )
        );
    }

    @Test
    public void testSaveAndStorageValueInfosMixedParents() {
        final TestStorageContext context = new TestStorageContext();
        final TreeMapStoreStorageStore store = this.createStore(context);

        final StoragePath base = StoragePath.parse("/base");

        final StorageValue value1 = StorageValue.with(
            base.append(
                StorageName.with("file1.txt")
            ),
            Optional.of("value1")
        );

        store.save(value1);

        final StorageValue value2 = StorageValue.with(
            base.append(
                StorageName.with("file2.txt")
            ),
            Optional.of("value2")
        );

        store.save(value2);

        final StorageValue value3 = StorageValue.with(
            base.append(
                StorageName.with("file3.txt")
            ),
            Optional.of("value3")
        );

        store.save(value3);

        final StorageValue value4 = StorageValue.with(
            base.append(
                StorageName.with("file4.txt")
            ),
            Optional.of("value4")
        );

        store.save(value4);

        final StorageValue subsub = StorageValue.with(
            base.append(
                StoragePath.parse("/sub")
            ).append(
                StorageName.with("sub.txt")
            ),
            Optional.of("subsub")
        );

        store.save(subsub);

        final StorageValue rootfile = StorageValue.with(
            StoragePath.parse("/root.txt"),
            Optional.of("root")
        );

        store.save(rootfile);

        this.storageValueInfosAndCheck(
            store,
            base,
            1,
            2,
            StorageValueInfo.with(
                value2.path(),
                AUDIT_INFO
            ),
            StorageValueInfo.with(
                value3.path(),
                AUDIT_INFO
            )
        );
    }

    @Test
    public void testSaveUpdateAndStorageValueInfos() {
        final TestStorageContext context = new TestStorageContext();
        final TreeMapStoreStorageStore store = this.createStore(context);

        final StorageValue value = this.value();
        store.save(value);

        final LocalDateTime modifiedTimestamp = TIMESTAMP.plusYears(10);
        context.now = modifiedTimestamp;

        store.save(
            StorageValue.with(
                PATH,
                Optional.of("different-value-6666")
            )
        );

        this.storageValueInfosAndCheck(
            store,
            PATH.parent()
                .get(),
            0,
            2,
            StorageValueInfo.with(
                PATH,
                AUDIT_INFO.setModifiedTimestamp(modifiedTimestamp)
            )
        );
    }

    @Test
    public void testStorageValueInfosRootPath() {
        final TestStorageContext context = new TestStorageContext();
        final TreeMapStoreStorageStore store = this.createStore(context);

        final StoragePath file1 = StoragePath.parse("/file1.txt");
        store.save(
            StorageValue.with(
                file1,
                Optional.of("file1-value")
            )
        );

        final StoragePath file2 = StoragePath.parse("/dir2/file2.txt");
        store.save(
            StorageValue.with(
                file2,
                Optional.of("file2-value")
            )
        );

        final StoragePath file5 = StoragePath.parse("/dir2/dir3/file3.txt");
        store.save(
            StorageValue.with(
                file5,
                Optional.of("file3-value")
            )
        );

        // listing alpha sorted
        this.storageValueInfosAndCheck(
            store,
            StoragePath.ROOT,
            0,
            4,
            StorageValueInfo.with(
                StoragePath.parse("/dir2/"),
                AUDIT_INFO
            ),
            StorageValueInfo.with(
                file1,
                AUDIT_INFO
            )
        );
    }

    @Test
    public void testStorageValueInfosSubdirectory() {
        final TestStorageContext context = new TestStorageContext();
        final TreeMapStoreStorageStore store = this.createStore(context);

        final StoragePath file1 = StoragePath.parse("/file1.txt");
        store.save(
            StorageValue.with(
                file1,
                Optional.of("file1-value")
            )
        );

        final StoragePath file2 = StoragePath.parse("/dir2/file2.txt");
        store.save(
            StorageValue.with(
                file2,
                Optional.of("file2-value")
            )
        );

        final StoragePath file3 = StoragePath.parse("/dir2/file3.txt");
        store.save(
            StorageValue.with(
                file3,
                Optional.of("file3-value")
            )
        );

        final StoragePath file4 = StoragePath.parse("/dir4/file4.txt");
        store.save(
            StorageValue.with(
                file3,
                Optional.of("file4-value")
            )
        );

        this.storageValueInfosAndCheck(
            store,
            StoragePath.parse("/dir2/"),
            0,
            10,
            StorageValueInfo.with(
                file2,
                AUDIT_INFO
            ),
            StorageValueInfo.with(
                file3,
                AUDIT_INFO
            )
        );
    }

    @Override
    public TreeMapStoreStorageStore createStore() {
        return this.createStore(
            new FakeStorageContext() {

                @Override
                public LocalDateTime now() {
                    return TIMESTAMP;
                }

                @Override
                public Optional<EmailAddress> user() {
                    return Optional.of(USER);
                }
            }
        );
    }

    private TreeMapStoreStorageStore createStore(final StorageContext context) {
        return TreeMapStoreStorageStore.with(context);
    }

    final static class TestStorageContext extends FakeStorageContext implements StorageContext {

        TestStorageContext() {
            this.now = TIMESTAMP;
        }

        @Override
        public Optional<EmailAddress> user() {
            return Optional.ofNullable(USER);
        }

        @Override
        public LocalDateTime now() {
            return this.now;
        }

        LocalDateTime now;
    }

    @Override
    public StoragePath id() {
        return PATH;
    }

    @Override
    public StorageValue value() {
        return StorageValue.with(
            this.id(),
            Optional.of(VALUE)
        );
    }

    // class............................................................................................................

    @Override
    public Class<TreeMapStoreStorageStore> type() {
        return TreeMapStoreStorageStore.class;
    }
}
