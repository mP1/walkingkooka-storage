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

import walkingkooka.Binary;
import walkingkooka.environment.AuditInfo;
import walkingkooka.net.email.EmailAddress;

import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Uses a {@link StoragePath} to an archive and presents a read-only view of its contents.
 */
final class StorageSharedWrapperExplodedZipFile<C extends StorageContext> extends StorageSharedWrapper<C> {

    static <C extends StorageContext> StorageSharedWrapperExplodedZipFile<C> with(final StoragePath archive,
                                                                                  final Storage<C> storage) {
        return new StorageSharedWrapperExplodedZipFile<>(
            Objects.requireNonNull(archive, "archive"),
            storage
        );
    }

    private StorageSharedWrapperExplodedZipFile(final StoragePath archive,
                                                final Storage<C> storage) {
        super(storage);
        this.archive = archive;
    }

    @Override
    boolean canRead0(final StoragePath path,
                     final C context) {
        return this.exploded(context)
            .canRead(
                path,
                context
            );
    }

    @Override
    boolean canWrite0(final StoragePath path,
                      final C context) {
        return false;
    }

    @Override
    Optional<StorageValue> load0(final StoragePath path,
                                 final C context) {
        return this.exploded(context)
            .load(
                path,
                context
            );
    }

    @Override
    StorageValue save0(final StorageValue value,
                       final C context) {
        throw value.path()
            .invalidStoragePathException("Read only");
    }

    @Override
    void delete0(final StoragePath path,
                 final C context) {
        throw path.invalidStoragePathException("Read only");
    }

    @Override
    List<StorageValueInfo> list0(final StoragePath parent,
                                 final int offset,
                                 final int count,
                                 final C context) {
        return this.exploded(context)
            .list(
                parent,
                offset,
                count,
                context
            );
    }

    @Override
    void setAuditInfo0(final StorageValueInfo value,
                       final C context) {
        throw value.path()
            .invalidStoragePathException("Read only");
    }

    /**
     * The {@link StoragePath} for the archive in the wrapped {@link Storage}.
     */
    private final StoragePath archive;

    private Storage<C> exploded(final C context) {
        if (null == this.exploded) {
            final StorageValue storageValue = this.storage.load(
                this.archive,
                context
            ).orElse(null);

            try {
                if (null != storageValue) {
                    final Binary binary = context.convert(
                        storageValue.value(),
                        Binary.class
                    ).orElseLeft(null);

                    if (null != binary) {
                        final Storage<C> exploded = Storages.treeMapStore();

                        try (final ZipInputStream zipInputStream = new ZipInputStream(binary.inputStream())) {
                            for (; ; ) {
                                final ZipEntry zipEntry = zipInputStream.getNextEntry();
                                if (null == zipEntry) {
                                    break;
                                }

                                if (zipEntry.isDirectory()) {
                                    continue;
                                }

                                final StoragePath zipEntryStoragePath = StoragePath.parse(
                                    zipEntry.getName()
                                );

                                exploded.save(
                                    StorageValue.with(
                                        zipEntryStoragePath
                                    ).setValue(
                                        Optional.of(
                                            Binary.with(
                                                zipInputStream.readAllBytes()
                                            )
                                        )
                                    ),
                                    context
                                );

                                final EmailAddress user = context.userOrFail();

                                // FIX the create and last modified timestamps
                                exploded.setAuditInfo(
                                    StorageValueInfo.with(
                                        zipEntryStoragePath,
                                        AuditInfo.with(
                                            user,
                                            toLocalDateTime(
                                                zipEntry.getCreationTime()
                                            ),
                                            user,
                                            toLocalDateTime(
                                                zipEntry.getLastModifiedTime()
                                            )
                                        )
                                    ),
                                    context
                                );
                            }

                        } catch (final IOException cause) {
                            throw new IllegalStateException("Unable to explode directory, " + cause.getMessage(), cause);
                        }

                        this.exploded = Storages.readOnly(exploded);
                    }
                }
            } finally {
                if (null == this.exploded) {
                    this.exploded = Storages.empty();
                }
            }
        }

        return this.exploded;
    }

    private static LocalDateTime toLocalDateTime(final FileTime fileTime) {
        return LocalDateTime.ofInstant(
            fileTime.toInstant(),
            ZoneOffset.UTC
        );
    }

    /**
     * The {@link Storages#treeMapStore()} containing the archive in exploded form.
     */
    private Storage<C> exploded;

    // addWatcher.......................................................................................................

    @Override
    Runnable addWatcher0(final StorageWatcher watcher,
                         final C context) {
        throw new UnsupportedOperationException();
    }

    @Override
    Runnable addWatcherOnce0(final StorageWatcher watcher,
                             final C context) {
        throw new UnsupportedOperationException();
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.storage.toString();
    }
}
