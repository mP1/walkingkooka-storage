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
import walkingkooka.Binary;
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.HasCharsetTesting;
import walkingkooka.environment.AuditInfo;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.storage.StorageShared2WrapperExplodedZipFileTest.TestStorageContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageShared2WrapperExplodedZipFileTest extends StorageShared2TestCase<StorageShared2WrapperExplodedZipFile<TestStorageContext>, TestStorageContext>
    implements HasCharsetTesting {

    private final static StoragePath ARCHIVE_STORAGE_PATH = StoragePath.parse("/file1.zip");

    private final static StoragePath FILE_STORAGE_PATH = StoragePath.parse("/inside-zip/hello.txt");
    private final static Binary FILE_CONTENT = Binary.with(
        "HelloWorld123".getBytes(CHARSET)
    );

    private final static StoragePath DIFFERENT_STORAGE_PATH = StoragePath.parse("/unknown-file-404.txt");

    private final static LocalDateTime CREATION_TIME = LocalDateTime.of(
        2001,
        1,
        1,
        12,
        58,
        59
    );

    private final static LocalDateTime LAST_MODIFIED_TIME = LocalDateTime.of(
        2002,
        2,
        2,
        12,
        58,
        59
    );

    // with.............................................................................................................

    @Test
    public void testWithNullStoragePathFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageShared2WrapperExplodedZipFile.with(
                null,
                Storages.fake()
            )
        );
    }

    // Storage..........................................................................................................

    @Test
    public void testCanReadFileInsideArchive() {
        this.canReadAndCheck(
            this.createStorage(),
            FILE_STORAGE_PATH,
            this.createContext(),
            true
        );
    }

    @Test
    public void testCanReadFileUnknownFile() {
        this.canReadAndCheck(
            this.createStorage(),
            DIFFERENT_STORAGE_PATH,
            this.createContext(),
            false
        );
    }

    @Test
    public void testCanWrite() {
        this.canWriteAndCheck(
            this.createStorage(),
            DIFFERENT_STORAGE_PATH,
            this.createContext(),
            false
        );
    }

    @Test
    public void testLoadFileInsideArchive() {
        this.loadAndCheck(
            this.createStorage(),
            FILE_STORAGE_PATH,
            new TestStorageContext(),
            StorageValue.with(FILE_STORAGE_PATH)
                .setValue(
                    Optional.of(FILE_CONTENT)
                )
        );
    }

    @Test
    public void testLoadFileWithUnknown() {
        this.loadAndCheck(
            this.createStorage(),
            DIFFERENT_STORAGE_PATH,
            new TestStorageContext()
        );
    }

    @Test
    public void testSaveFails() {
        assertThrows(
            InvalidStoragePathException.class,
            () -> this.createStorage()
                .save(
                    StorageValue.with(DIFFERENT_STORAGE_PATH)
                        .setValue(
                            Optional.of(FILE_CONTENT)
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
                    FILE_STORAGE_PATH,
                    this.createContext()
                )
        );
    }

    @Test
    public void testDeleteUnknownFileFails() {
        assertThrows(
            InvalidStoragePathException.class,
            () -> this.createStorage()
                .delete(
                    DIFFERENT_STORAGE_PATH,
                    this.createContext()
                )
        );
    }

    @Test
    public void testListRootPath() {
        this.listAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            0,
            4,
            new TestStorageContext(),
            StorageValueInfo.with(
                FILE_STORAGE_PATH.parent()
                    .get(),
                AUDIT_INFO
            )
        );
    }

    @Test
    public void testListSubdirectory() {
        this.listAndCheck(
            this.createStorage(),
            FILE_STORAGE_PATH.parent()
                .get(),
            0,
            4,
            new TestStorageContext(),
            StorageValueInfo.with(
                FILE_STORAGE_PATH,
                AuditInfo.with(
                    USER,
                    CREATION_TIME,
                    USER,
                    LAST_MODIFIED_TIME
                )
            )
        );
    }

    @Test
    public void testSetAuditInfo() {
        assertThrows(
            InvalidStoragePathException.class,
            () -> this.createStorage()
                .setAuditInfo(
                    StorageValueInfo.with(
                        FILE_STORAGE_PATH,
                        AUDIT_INFO
                    ),
                    this.createContext()
                )
        );
    }

    @Override
    public StorageShared2WrapperExplodedZipFile<TestStorageContext> createStorage() {
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();

            final ZipOutputStream zipOutputStream = new ZipOutputStream(baos);
            final ZipEntry zipEntry = new ZipEntry(FILE_STORAGE_PATH.value());
            zipEntry.setCreationTime(
                FileTime.from(
                    CREATION_TIME.toInstant(ZoneOffset.UTC)
                )
            );
            zipEntry.setLastModifiedTime(
                FileTime.from(
                    LAST_MODIFIED_TIME.toInstant(ZoneOffset.UTC)
                )
            );
            zipOutputStream.putNextEntry(zipEntry);

            zipOutputStream.write(
                FILE_CONTENT.value()
            );
            zipOutputStream.closeEntry();
            zipOutputStream.flush();
            zipOutputStream.close();

            final Storage<TestStorageContext> storage = Storages.treeMapStore();
            storage.save(
                StorageValue.with(ARCHIVE_STORAGE_PATH)
                    .setValue(
                        Optional.of(
                            Binary.with(
                                baos.toByteArray()
                            )
                        )
                    ),
                this.createContext()
            );

            return StorageShared2WrapperExplodedZipFile.with(
                ARCHIVE_STORAGE_PATH,
                storage
            );
        } catch (final IOException rethrow) {
            throw new RuntimeException(rethrow);
        }
    }

    @Override
    public TestStorageContext createContext() {
        return new TestStorageContext();
    }

    final static class TestStorageContext extends FakeStorageContext implements StorageContext {

        TestStorageContext() {
            super();
        }

        @Override
        public <T> Either<T, String> convert(final Object value,
                                             final Class<T> target) {
            return this.successfulConversion(
                target.cast(
                    Optional.class.cast(value)
                        .get()
                ),
                target
            );
        }

        @Override
        public Optional<EmailAddress> user() {
            return Optional.ofNullable(StorageShared2WrapperExplodedZipFileTest.USER);
        }

        @Override
        public LocalDateTime now() {
            return StorageShared2WrapperExplodedZipFileTest.NOW;
        }
    }

    // TreePrintable....................................................................................................

    @Test
    public void testPrintTreeWhenNotExploded() {
        this.treePrintAndCheck(
            this.createStorage(),
            "StorageShared2WrapperExplodedZipFile\n" +
                "  \"/file1.zip\"\n"
        );
    }

    @Test
    public void testPrintTreeWhenExploded() {
        final StorageShared2WrapperExplodedZipFile<TestStorageContext> storage = this.createStorage();
        final TestStorageContext context = this.createContext();

        this.loadAndCheck(
            storage,
            StoragePath.parse("/doesnt-matter"),
            context
        );

        this.treePrintAndCheck(
            storage,
            "StorageShared2WrapperExplodedZipFile\n" +
                "  \"/file1.zip\"\n" +
                "    ReadOnlyStorage\n" +
                "      StorageShared2TreeMapStore\n" +
                "        TreeMapStore\n" +
                "          /\n" +
                "          /inside-zip\n" +
                "          /inside-zip/hello.txt\n" +
                "            48 65 6c 6c 6f 57 6f 72 6c 64 31 32 33                      HelloWorld123       \n" +
                "             (walkingkooka.Binary)\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageShared2WrapperExplodedZipFile<TestStorageContext>> type() {
        return Cast.to(StorageShared2WrapperExplodedZipFile.class);
    }
}
