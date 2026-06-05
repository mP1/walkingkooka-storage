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

import javaemul.internal.annotations.GwtIncompatible;
import walkingkooka.Binary;
import walkingkooka.collect.list.ImmutableList;
import walkingkooka.environment.AuditInfo;
import walkingkooka.net.email.EmailAddress;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A {@link Storage} for a {@link Path}, converting the binary content of a file to an object using the file name,
 * and converting the object to binary when saving.
 */
@GwtIncompatible
final class StorageSharedNativeFile<C extends StorageContext> extends StorageShared<C> {

    static <C extends StorageContext> StorageSharedNativeFile<C> with(final Path root) {
        return new StorageSharedNativeFile<>(
            Objects.requireNonNull(root, "root")
        );
    }

    private StorageSharedNativeFile(final Path root) {
        this.root = root;
    }

    @Override
    boolean canRead0(final StoragePath storagePath,
                     final C context) {
        return Files.isReadable(
            this.toPath(storagePath)
        );
    }

    @Override
    boolean canWrite0(final StoragePath storagePath,
                      final C context) {
        return Files.isWritable(
            this.toPath(storagePath)
        );
    }

    @Override
    Optional<StorageValue> load0(final StoragePath storagePath,
                                 final C context) {
        // map StoragePath to file system path
        final Path fileSystemPath = this.toPath(storagePath);

        StorageValue storageValue;

        try {
            // load file binary into Binary
            final Binary binary = Binary.with(
                Files.readAllBytes(fileSystemPath)
            );

            final StorageBinary storageBinary = StorageBinary.with(
                storagePath,
                binary
            );

            // convert StorageBinary into StorageValue
            storageValue = context.convert(
                storageBinary,
                StorageValue.class
            ).orElseLeft(
                StorageValue.with(storagePath)
            );
        } catch (final FileNotFoundException | NoSuchFileException cause) {
            storageValue = null;
        } catch (final IOException cause) {
            throw storagePath.invalidStoragePathException(
                "Unable to read",
                cause
            );
        }

        return Optional.ofNullable(storageValue);
    }

    @Override
    StorageValue save0(final StorageValue storageValue,
                       final C context) {
        final StoragePath storagePath = storageValue.path();

        // map StoragePath to file system path
        final Path fileSystemPath = this.toPath(storagePath);

        try {
            // create parent directories
            Files.createDirectories(
                fileSystemPath.getParent()
            );

            // convert StorageValue.value to Binary
            final StorageBinary storageBinary = context.convertOrFail(
                storageValue,
                StorageBinary.class
            );

            Files.write(
                fileSystemPath,
                storageBinary.binary()
                    .value()
            );

        } catch (final IOException cause) {
            throw storagePath.invalidStoragePathException(
                "Unable to write",
                cause
            );
        }

        return storageValue;
    }

    @Override
    void delete0(final StoragePath storagePath,
                 final C context) {
        // map StoragePath to file system path
        final Path fileSystemPath = this.toPath(storagePath);

        try {
            Files.delete(fileSystemPath);
        } catch (final NoSuchFileException cause) {
            throw storagePath.invalidStoragePathException(
                "Unable to delete",
                cause
            );
        } catch (final DirectoryNotEmptyException cause) {
            throw storagePath.invalidStoragePathException(
                "Directory not empty",
                cause
            );
        } catch (final IOException cause) {
            throw storagePath.invalidStoragePathException(
                "Unable to delete",
                cause
            );
        }
    }

    @Override
    List<StorageValueInfo> list0(final StoragePath parent,
                                 final int offset,
                                 final int count,
                                 final C context) {
        // map StoragePath to file system path
        final Path fileSystemPath = this.toPath(parent);

        try {
            try (Stream<Path> stream = Files.list(fileSystemPath)) {
                return stream.skip(
                        offset
                    ).limit(count)
                    .map(
                        (Path fileSystemPath2) -> this.toStorageValueInfo(
                            fileSystemPath2,
                            parent,
                            context
                        ))
                    .collect(
                        ImmutableList.collector()
                    );
            }
        } catch (final NoSuchFileException cause) {
            throw parent.invalidStoragePathException(
                "Invalid path",
                cause
            );
        } catch (final IOException cause) {
            throw parent.invalidStoragePathException(
                "Unable to list",
                cause
            );
        }
    }


    /**
     * Helper that converts a {@link StoragePath} to the equivalent file system {@link Path}.
     */
    private Path toPath(final StoragePath storagePath) {
        final Path root = this.root;

        return root.getFileSystem()
            .getPath(
                root.toString(),
                storagePath.value()
            );
    }

    private StorageValueInfo toStorageValueInfo(final Path fileSystemPath,
                                                final StoragePath parent,
                                                final C context) {
        try {
            final EmailAddress user = context.userOrFail();

            final BasicFileAttributes basicFileAttributes = Files.readAttributes(
                fileSystemPath,
                BasicFileAttributes.class
            );

            return StorageValueInfo.with(
                this.toStoragePath(fileSystemPath), // path
                AuditInfo.with(
                    user,
                    toLocalDateTime(
                        basicFileAttributes.creationTime()
                    ),
                    user,
                    toLocalDateTime(
                        basicFileAttributes.lastModifiedTime()
                    )
                )
            );
        } catch (final IOException rethrow) {
            throw parent.append(
                StorageName.with(
                    fileSystemPath.getFileName()
                        .toString()
                )
            ).invalidStoragePathException(
                "Unable to read file attributes",
                rethrow
            );
        }
    }

    private StoragePath toStoragePath(final Path path) {
        return StoragePath.ROOT.append(
            StoragePath.parse(
                path.toAbsolutePath()
                    .toString()
                    .substring(
                        this.root.toAbsolutePath()
                            .toString()
                            .length()
                    )
            )
        );
    }

    private LocalDateTime toLocalDateTime(final FileTime fileTime) {
        return LocalDateTime.ofInstant(
            fileTime.toInstant(),
            UTC // TODO read from Context
        );
    }

    // @VisibleForTesting
    final static ZoneOffset UTC = ZoneOffset.UTC;

    /**
     * The root directory containing all files and directories.
     */
    private final Path root;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.root.toString();
    }
}
