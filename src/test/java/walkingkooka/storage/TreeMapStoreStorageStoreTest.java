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
import walkingkooka.net.email.EmailAddress;

import java.time.LocalDateTime;
import java.util.Optional;

public class TreeMapStoreStorageStoreTest implements StorageStoreTesting<TreeMapStoreStorageStore> {

    private final static StoragePath PATH = StoragePath.parse("/path123");

    private final static String VALUE = "value456";

    private final static EmailAddress USER = EmailAddress.parse("user123@example.com");

    private final static LocalDateTime TIMESTAMP = LocalDateTime.parse("1999-12-31T12:58:59");

    @Test
    public void testSaveAndLoad() {
        final TestStorageStoreContext context = new TestStorageStoreContext();
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
        final TestStorageStoreContext context = new TestStorageStoreContext();
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
        final TestStorageStoreContext context = new TestStorageStoreContext();
        final TreeMapStoreStorageStore store = this.createStore(context);

        final StorageValue value = this.value();

        store.save(value);

        this.storageValueInfosAndCheck(
            store,
            0,
            2,
            StorageValueInfo.with(
                PATH,
                USER,
                TIMESTAMP,
                USER,
                TIMESTAMP
            )
        );
    }

    @Test
    public void testSaveUpdateAndStorageValueInfos() {
        final TestStorageStoreContext context = new TestStorageStoreContext();
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
            0,
            2,
            StorageValueInfo.with(
                PATH,
                USER,
                TIMESTAMP,
                USER,
                modifiedTimestamp
            )
        );
    }

    @Override
    public TreeMapStoreStorageStore createStore() {
        return this.createStore(
            new FakeStorageStoreContext() {

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

    private TreeMapStoreStorageStore createStore(final StorageStoreContext context) {
        return TreeMapStoreStorageStore.with(context);
    }

    final static class TestStorageStoreContext extends FakeStorageStoreContext implements StorageStoreContext {

        TestStorageStoreContext() {
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
