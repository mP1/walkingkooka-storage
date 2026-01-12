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
import walkingkooka.ToStringTesting;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.JavaVisibility;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class PrefixedStorageTest implements StorageTesting<PrefixedStorage, FakeStorageContext>,
    ToStringTesting<PrefixedStorage> {

    private final static String PREFIX = "/prefix111";

    private final static LocalDateTime NOW = LocalDateTime.of(
        1999,
        12,
        31,
        12,
        58,
        59
    );

    // with.............................................................................................................

    @Test
    public void testWithNullPrefixFails() {
        assertThrows(
            NullPointerException.class,
            () -> PrefixedStorage.with(
                null,
                Storages.fake()
            )
        );
    }

    @Test
    public void testWithNullStorageFails() {
        assertThrows(
            NullPointerException.class,
            () -> PrefixedStorage.with(
                StoragePath.ROOT,
                null
            )
        );
    }

    @Test
    public void testWithRoot() {
        final Storage storage = Storages.fake();

        assertSame(
            storage,
            PrefixedStorage.with(
                StoragePath.ROOT,
                storage
            )
        );
    }

    @Test
    public void testWithPrefixedStorage() {
        final PrefixedStorage storage = this.createStorage();

        final StoragePath prefix2 = StoragePath.parse("/prefix222");

        final PrefixedStorage prefixedStorage = Cast.to(
            PrefixedStorage.with(
                prefix2,
                storage
            )
        );

        this.checkEquals(
            StoragePath.parse("/prefix222/prefix111"),
            prefixedStorage.prefix,
            "prefix"
        );

        assertSame(
            storage.storage,
            prefixedStorage.storage,
            "storage"
        );
    }

    // load.............................................................................................................

    @Test
    public void testLoadUnknown() {
        this.loadAndCheck(
            this.createStorage(),
            StoragePath.parse(PREFIX + "/unknown"),
            this.createContext()
        );
    }

    @Test
    public void testLoad() {
        final PrefixedStorage storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        final StorageValue value = StorageValue.with(
            StoragePath.parse("/value111"),
            Optional.of(999)
        );

        storage.storage.save(
            value,
            context
        );

        final StoragePath path = StoragePath.parse(PREFIX + "/value111");

        this.loadAndCheck(
            storage,
            path,
            context,
            value.setPath(path)
        );
    }

    // save.............................................................................................................

    @Test
    public void testSave() {
        final PrefixedStorage storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        final StoragePath path = StoragePath.parse(PREFIX + "/value111");

        final StorageValue value = StorageValue.with(
            path,
            Optional.of(999)
        );

        this.saveAndCheck(
            storage,
            value,
            context,
            value
        );

        final StoragePath without = StoragePath.parse("/value111");

        this.loadAndCheck(
            storage.storage,
            without,
            context,
            value.setPath(without)
        );
    }

    // delete...........................................................................................................

    @Test
    public void testDeleteWithUnknownPath() {
        final PrefixedStorage storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        final StoragePath path = StoragePath.parse("/value111");

        final StorageValue value = StorageValue.with(
            path,
            Optional.of(999)
        );

        storage.storage.save(
            value,
            context
        );

        storage.delete(
            StoragePath.parse(PREFIX + "/unknown404"),
            context
        );

        this.loadAndCheck(
            storage.storage,
            path,
            context,
            value
        );
    }

    @Test
    public void testDelete() {
        final PrefixedStorage storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        final StoragePath path = StoragePath.parse("/value111");

        final StorageValue value = StorageValue.with(
            path,
            Optional.of(999)
        );

        storage.storage.save(
            value,
            context
        );

        storage.delete(
            StoragePath.parse(PREFIX + "/value111"),
            context
        );

        this.loadAndCheck(
            storage.storage,
            path,
            context
        );
    }

    // list.............................................................................................................

    @Test
    public void testList() {
        final PrefixedStorage storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        final StoragePath path1 = StoragePath.parse(PREFIX + "/value111");
        final StorageValue value1 = StorageValue.with(
            path1,
            Optional.of(111)
        );

        storage.save(
            value1,
            context
        );

        final StoragePath path2 = StoragePath.parse(PREFIX + "/value222");
        final StorageValue value2 = StorageValue.with(
            path2,
            Optional.of(222)
        );

        storage.save(
            value2,
            context
        );

        final StoragePath path3 = StoragePath.parse(PREFIX + "/value333");
        final StorageValue value3 = StorageValue.with(
            path3,
            Optional.of(333)
        );

        storage.save(
            value3,
            context
        );

        final StoragePath path4 = StoragePath.parse(PREFIX + "/value444");
        final StorageValue value4 = StorageValue.with(
            path4,
            Optional.of(444)
        );

        storage.save(
            value4,
            context
        );

        this.listAndCheck(
            storage,
            StoragePath.parse(PREFIX),
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

    @Override
    public PrefixedStorage createStorage() {
        return Cast.to(
            PrefixedStorage.with(
                StoragePath.parse(PREFIX),
                Storages.tree()
            )
        );
    }

    @Override
    public FakeStorageContext createContext() {
        return new FakeStorageContext() {
            @Override
            public LocalDateTime now() {
                return PrefixedStorageTest.NOW;
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
            "/prefix111 []"
        );
    }

    // class............................................................................................................

    @Override
    public Class<PrefixedStorage> type() {
        return Cast.to(PrefixedStorage.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
