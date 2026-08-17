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
import walkingkooka.collect.list.Lists;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageMountPointPathsTest implements StorageTesting2<StorageMountPointPaths<FakeStorageContext>, FakeStorageContext>,
    ToStringTesting<StorageMountPointPaths<FakeStorageContext>>,
    ClassTesting<StorageMountPointPaths<FakeStorageContext>> {

    @Test
    public void testCanReadExisting() {
        this.canReadAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            this.createContext(),
            true
        );
    }

    @Test
    public void testCanReadUnknown() {
        this.canReadAndCheck(
            this.createStorage(),
            StoragePath.parse("/unknown-file-404.txt"),
            this.createContext(),
            false
        );
    }

    @Test
    public void testCanWrite() {
        this.canWriteAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
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
            StorageValue.with(
                StoragePath.ROOT
            ).setValue(
                Optional.of(
                    StoragePathList.EMPTY.setElements(
                        Lists.of(
                            StoragePath.ROOT,
                            StoragePath.parse("/mount1"),
                            StoragePath.parse("/mount2")
                        )
                    )
                )
            )
        );
    }

    @Test
    public void testSaveFails() {
        assertThrows(
            InvalidStoragePathException.class,
            () -> this.createStorage()
                .save(
                    StorageValue.with(StoragePath.ROOT)
                        .setValue(
                            Optional.of("Replaced222")
                        ),
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

    @Override
    public StorageMountPointPaths<FakeStorageContext> createStorage() {
        return StorageMountPointPaths.instance();
    }

    @Override
    public FakeStorageContext createContext() {
        return new FakeStorageContext() {
            @Override
            public LocalDateTime now() {
                return StorageMountPointPathsTest.NOW;
            }

            @Override
            public Optional<EmailAddress> user() {
                return OPTIONAL_USER;
            }

            @Override
            public List<StorageMountPoint<?>> storageMountPoints() {
                return Lists.of(
                    StorageMountPoint.with(
                        StoragePath.ROOT,
                        Storages.fake()
                    ),
                    StorageMountPoint.with(
                        StoragePath.parse("/mount1"),
                        Storages.fake()
                    ),
                    StorageMountPoint.with(
                        StoragePath.parse("/mount2"),
                        Storages.fake()
                    )
                );
            }
        };
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createStorage(),
            "StoragePathList"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageMountPointPaths<FakeStorageContext>> type() {
        return Cast.to(StorageMountPointPaths.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
