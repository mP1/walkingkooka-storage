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
import walkingkooka.environment.AuditInfo;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.storage.TreeMapStoreStorageTest.TestStorageContext;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class TreeMapStoreStorageTest implements StorageTesting<TreeMapStoreStorage<TestStorageContext>, TestStorageContext> {

    private final static StoragePath PATH = StoragePath.parse("/path123");

    private final static String VALUE = "value456";

    private final static EmailAddress USER = EmailAddress.parse("user123@example.com");

    private final static LocalDateTime TIMESTAMP = LocalDateTime.parse("1999-12-31T12:58:59");

    private final static AuditInfo AUDIT_INFO = AuditInfo.create(
        USER,
        TIMESTAMP
    );

    private final static StorageValue STORAGE_VALUE = StorageValue.with(
        PATH,
        Optional.of(VALUE)
    );

    @Test
    public void testSaveAndLoad() {
        final TreeMapStoreStorage<TestStorageContext> store = this.createStorage();
        final TestStorageContext context = new TestStorageContext();

        final StorageValue value = STORAGE_VALUE;

        store.save(
            value,
            context
        );

        this.loadAndCheck(
            store,
            value.path(),
            context,
            value
        );
    }

    @Test
    public void testBuildPathSaveAndLoad() {
        final TreeMapStoreStorage<TestStorageContext> store = this.createStorage();
        final TestStorageContext context = new TestStorageContext();

        final StoragePath base = StoragePath.parse("/base");

        final StorageValue value1 = StorageValue.with(
            base.append(StorageName.with("file1.txt")),
            Optional.of("value1")
        );

        store.save(
            value1,
            context
        );

        this.loadAndCheck(
            store,
            StoragePath.parse("/base/file1.txt"),
            context,
            value1
        );

        final StorageValue value2 = StorageValue.with(
            base.append(StorageName.with("file2.txt")),
            Optional.of("value2")
        );

        store.save(
            value2,
            context
        );

        this.loadAndCheck(
            store,
            StoragePath.parse("/base/file2.txt"),
            context,
            value2
        );
    }

    @Test
    public void testSaveAndList() {
        final TreeMapStoreStorage<TestStorageContext> store = this.createStorage();
        final TestStorageContext context = new TestStorageContext();

        final StorageValue value = STORAGE_VALUE;

        store.save(
            value,
            context
        );

        this.listAndCheck(
            store,
            PATH.parent()
                .get(),
            0,
            2,
            context,
            StorageValueInfo.with(
                PATH,
                AUDIT_INFO
            )
        );
    }

    @Test
    public void testSaveAndListMixedParents() {
        final TreeMapStoreStorage<TestStorageContext> store = this.createStorage();
        final TestStorageContext context = new TestStorageContext();

        final StoragePath base = StoragePath.parse("/base");

        final StorageValue value1 = StorageValue.with(
            base.append(
                StorageName.with("file1.txt")
            ),
            Optional.of("value1")
        );

        store.save(
            value1,
            context
        );

        final StorageValue value2 = StorageValue.with(
            base.append(
                StorageName.with("file2.txt")
            ),
            Optional.of("value2")
        );

        store.save(
            value2,
            context
        );

        final StorageValue value3 = StorageValue.with(
            base.append(
                StorageName.with("file3.txt")
            ),
            Optional.of("value3")
        );

        store.save(
            value3,
            context
        );

        final StorageValue value4 = StorageValue.with(
            base.append(
                StorageName.with("file4.txt")
            ),
            Optional.of("value4")
        );

        store.save(
            value4,
            context
        );

        final StorageValue subsub = StorageValue.with(
            base.append(
                StoragePath.parse("/sub")
            ).append(
                StorageName.with("sub.txt")
            ),
            Optional.of("subsub")
        );

        store.save(
            subsub,
            context
        );

        final StorageValue rootfile = StorageValue.with(
            StoragePath.parse("/root.txt"),
            Optional.of("root")
        );

        store.save(
            rootfile,
            context
        );

        this.listAndCheck(
            store,
            base,
            1,
            2,
            context,
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
    public void testSaveUpdateAndList() {
        final TreeMapStoreStorage store = this.createStorage();
        final TestStorageContext context = new TestStorageContext();

        final StorageValue value = STORAGE_VALUE;
        store.save(
            value,
            context
        );

        final LocalDateTime modifiedTimestamp = TIMESTAMP.plusYears(10);
        context.now = modifiedTimestamp;

        store.save(
            StorageValue.with(
                PATH,
                Optional.of("different-value-6666")
            ),
            context
        );

        this.listAndCheck(
            store,
            PATH.parent()
                .get(),
            0,
            2,
            context,
            StorageValueInfo.with(
                PATH,
                AUDIT_INFO.setModifiedTimestamp(modifiedTimestamp)
            )
        );
    }

    @Test
    public void testListRootPath() {
        final TreeMapStoreStorage<TestStorageContext> store = this.createStorage();
        final TestStorageContext context = new TestStorageContext();

        final StoragePath file1 = StoragePath.parse("/file1.txt");
        store.save(
            StorageValue.with(
                file1,
                Optional.of("file1-value")
            ),
            context
        );

        final StoragePath file2 = StoragePath.parse("/dir2/file2.txt");
        store.save(
            StorageValue.with(
                file2,
                Optional.of("file2-value")
            ),
            context
        );

        final StoragePath file5 = StoragePath.parse("/dir2/dir3/file3.txt");
        store.save(
            StorageValue.with(
                file5,
                Optional.of("file3-value")
            ),
            context
        );

        // listing alpha sorted
        this.listAndCheck(
            store,
            StoragePath.ROOT,
            0,
            4,
            context,
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
    public void testListSubdirectory() {
        final TreeMapStoreStorage<TestStorageContext> store = this.createStorage();
        final TestStorageContext context = new TestStorageContext();

        final StoragePath file1 = StoragePath.parse("/file1.txt");
        store.save(
            StorageValue.with(
                file1,
                Optional.of("file1-value")
            ),
            context
        );

        final StoragePath file2 = StoragePath.parse("/dir2/file2.txt");
        store.save(
            StorageValue.with(
                file2,
                Optional.of("file2-value")
            ),
            context
        );

        final StoragePath file3 = StoragePath.parse("/dir2/file3.txt");
        store.save(
            StorageValue.with(
                file3,
                Optional.of("file3-value")
            ),
            context
        );

        final StoragePath file4 = StoragePath.parse("/dir4/file4.txt");
        store.save(
            StorageValue.with(
                file3,
                Optional.of("file4-value")
            ),
            context
        );

        this.listAndCheck(
            store,
            StoragePath.parse("/dir2/"),
            0,
            10,
            context,
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
    public TreeMapStoreStorage<TestStorageContext> createStorage() {
        return TreeMapStoreStorage.empty();
    }

    @Override
    public TestStorageContext createContext() {
        return new TestStorageContext();
    }

    final static class TestStorageContext extends FakeStorageContext implements StorageContext {

        TestStorageContext() {
            this.now = TIMESTAMP;
        }

        @Override
        public StoragePath parseStoragePath(final String text) {
            Objects.requireNonNull(text, "text");
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<EmailAddress> user() {
            return Optional.ofNullable(TreeMapStoreStorageTest.USER);
        }

        @Override
        public void setUser(final Optional<EmailAddress> user) {
            Objects.requireNonNull(user, "user");
            throw new UnsupportedOperationException();
        }

        @Override
        public LocalDateTime now() {
            return this.now;
        }

        LocalDateTime now;
    }

    // class............................................................................................................

    @Override
    public Class<TreeMapStoreStorage<TestStorageContext>> type() {
        return Cast.to(TreeMapStoreStorage.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
