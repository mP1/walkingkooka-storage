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
import walkingkooka.net.email.EmailAddress;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageShared2ValueTest extends StorageShared2TestCase<StorageShared2Value<StorageContext>, StorageContext> {

    private final static StorageValue VALUE = StorageValue.with(
        StoragePath.ROOT
    ).setValue(
        Optional.of("999")
    );

    private final static Function<StorageContext, StorageValue> VALUE_FUNCTION = (StorageContext c) -> VALUE;

    @Test
    public void testCanReadRoot() {
        this.canReadAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            this.createContext(),
            true
        );
    }

    @Test
    public void testCanReadNonRoot() {
        this.canReadAndCheck(
            this.createStorage(),
            StoragePath.parse("/unknown.txt"),
            this.createContext(),
            false
        );
    }

    @Test
    public void testCanWriteRoot() {
        this.canWriteAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            this.createContext(),
            false
        );
    }

    @Test
    public void testCanWriteNonRoot() {
        this.canWriteAndCheck(
            this.createStorage(),
            StoragePath.parse("/file.txt"),
            this.createContext(),
            false
        );
    }

    @Test
    @Override
    public void testLoadRoot() {
        this.loadAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            this.createContext(),
            VALUE
        );
    }

    @Test
    public void testLoadWithNonRoot() {
        this.loadAndCheck(
            this.createStorage(),
            StoragePath.parse("/non-root"),
            this.createContext()
        );
    }

    @Test
    public void testSaveFails() {
        assertThrows(
            InvalidStoragePathException.class,
            () -> this.createStorage()
                .save(
                    VALUE,
                    this.createContext()
                )
        );
    }

    @Test
    public void testDeleteFails() {
        assertThrows(
            InvalidStoragePathException.class,
            () -> this.createStorage()
                .delete(
                    StoragePath.ROOT,
                    this.createContext()
                )
        );
    }

    @Test
    public void testList() {
        this.listAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            0,
            999,
            this.createContext(),
            StorageValueInfo.with(
                StoragePath.ROOT,
                AUDIT_INFO
            )
        );
    }

    @Test
    public void testSetAuditInfoFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createStorage()
                .setAuditInfo(
                    StorageValueInfo.with(
                        StoragePath.ROOT,
                        AUDIT_INFO
                    ),
                    this.createContext()
                )
        );
    }

    @Test
    public void testAddWatcher() {
        this.createStorage()
            .addWatcher(
                new StorageWatcher() {
                    @Override
                    public void onValueChange(Optional<StorageValue> optional, Optional<StorageValue> optional1) {

                    }
                },
                this.createContext()
            );
    }

    @Test
    public void testAddWatcherOnce() {
        this.createStorage()
            .addWatcherOnce(
                new StorageWatcher() {
                    @Override
                    public void onValueChange(Optional<StorageValue> optional, Optional<StorageValue> optional1) {

                    }
                },
                this.createContext()
            );
    }

    @Test
    public void testMounted() {
        final Storage<StorageContext> storage = Storages.mount(
            Storages.treeMapStore()
        );

        final StorageContext context = this.createContext();

        final StoragePath path = StoragePath.parse("/mount1");

        storage.mount(
            StorageMountPoint.with(
                path,
                this.createStorage()
            ),
            context
        );

        this.loadAndCheck(
            storage,
            path,
            context,
            VALUE.setPath(path)
        );
    }

    @Override
    public StorageShared2Value<StorageContext> createStorage() {
        return StorageShared2Value.with(VALUE_FUNCTION);
    }

    @Override
    public StorageContext createContext() {
        return new FakeStorageContext() {
            @Override
            public Optional<EmailAddress> user() {
                return OPTIONAL_USER;
            }

            @Override
            public LocalDateTime now() {
                return StorageShared2ValueTest.NOW;
            }
        };
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createStorage(),
            VALUE_FUNCTION.toString()
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageShared2Value<StorageContext>> type() {
        return Cast.to(StorageShared2Value.class);
    }
}
