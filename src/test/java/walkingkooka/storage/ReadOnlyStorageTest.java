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
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ReadOnlyStorageTest implements StorageTesting<ReadOnlyStorage<FakeStorageContext>, FakeStorageContext>,
    ToStringTesting<ReadOnlyStorage<FakeStorageContext>>,
    ClassTesting<ReadOnlyStorage<FakeStorageContext>> {

    // with.............................................................................................................

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> ReadOnlyStorage.with(null)
        );
    }

    @Test
    public void testWithReadOnlyStorage() {
        final ReadOnlyStorage<FakeStorageContext> readOnlyStorage = this.createStorage();

        assertSame(
            readOnlyStorage,
            ReadOnlyStorage.with(readOnlyStorage)
        );
    }

    @Test
    public void testSaveFails() {
        final StoragePath path = StoragePath.parse("/file.txt");
        final StorageValue storageValue = StorageValue.with(path)
            .setValue(
                Optional.of("Hello111")
            );

        final FakeStorageContext context = this.createContext();

        final Storage<FakeStorageContext> treeMapStorage = Storages.treeMapStore();
        treeMapStorage.save(
            storageValue,
            context
        );

        final ReadOnlyStorage<FakeStorageContext> readOnlyStorage = ReadOnlyStorage.with(treeMapStorage);

        assertThrows(
            InvalidStoragePathException.class,
            () -> readOnlyStorage.save(
                StorageValue.with(path)
                    .setValue(
                        Optional.of("Replaced222")
                    ),
                this.createContext()
            )
        );

        this.loadAndCheck(
            readOnlyStorage,
            path,
            context,
            storageValue
        );
    }

    @Test
    public void testDeleteFails() {
        final StoragePath path = StoragePath.parse("/file.txt");
        final StorageValue storageValue = StorageValue.with(path)
            .setValue(
                Optional.of("Hello111")
            );

        final FakeStorageContext context = this.createContext();

        final Storage<FakeStorageContext> treeMapStorage = Storages.treeMapStore();
        treeMapStorage.save(
            storageValue,
            context
        );

        final ReadOnlyStorage<FakeStorageContext> readOnlyStorage = ReadOnlyStorage.with(treeMapStorage);

        assertThrows(
            InvalidStoragePathException.class,
            () -> readOnlyStorage.delete(
                path,
                this.createContext()
            )
        );

        this.loadAndCheck(
            readOnlyStorage,
            path,
            context,
            storageValue
        );
    }

    @Override
    public ReadOnlyStorage<FakeStorageContext> createStorage() {
        return ReadOnlyStorage.with(
            Storages.treeMapStore()
        );
    }

    @Override
    public FakeStorageContext createContext() {
        return new FakeStorageContext() {
            @Override
            public LocalDateTime now() {
                return LocalDateTime.now();
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
        final Storage<FakeStorageContext> storage = Storages.fake();

        this.toStringAndCheck(
            ReadOnlyStorage.with(storage),
            "ReadOnly " + storage
        );
    }

    // class............................................................................................................

    @Override
    public Class<ReadOnlyStorage<FakeStorageContext>> type() {
        return Cast.to(ReadOnlyStorage.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
