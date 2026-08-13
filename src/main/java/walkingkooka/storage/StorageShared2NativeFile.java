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
import walkingkooka.Cast;
import walkingkooka.collect.list.ImmutableList;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.environment.AuditInfo;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.text.CharSequences;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.util.OpenChecker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A {@link Storage} for a {@link Path}, converting the binary content of a file to an object using the file name,
 * and converting the object to binary when saving.
 */
@GwtIncompatible
final class StorageShared2NativeFile<C extends StorageContext> extends StorageShared2<C>
    implements TreePrintable {

    static <C extends StorageContext> StorageShared2NativeFile<C> with(final Path root,
                                                                       final WatchServicePoller<C> poller) {
        return new StorageShared2NativeFile<>(
            Objects.requireNonNull(root, "root"),
            Objects.requireNonNull(poller, "poller")
        );
    }

    private StorageShared2NativeFile(final Path root,
                                     final WatchServicePoller<C> poller) {
        this.root = root;

        try {
            this.watcher = root.getFileSystem()
                .newWatchService();;
        } catch (final IOException rethrow) {
            throw new IllegalArgumentException("Unable to open watch service", rethrow);
        }

        this.watchKeyToPath = Maps.concurrent();
        this.registerTree(root);

        poller.beginPolling(this::pollEvents);
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
        StorageValue storageValue = null;

        if(false == storagePath.isParent()) {
            // map StoragePath to file system path
            final Path fileSystemPath = this.toPath(storagePath);

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

        final List<StorageValueInfo> listing;

        if(parent.isParent()) {
            try {
                try (Stream<Path> stream = Files.list(fileSystemPath)) {
                    listing = stream.skip(
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
        } else {
            // get metadata for file
            listing = 0 == offset &
                count > 0 &&
                Files.exists(fileSystemPath) ?
                Lists.of(
                    this.toStorageValueInfo(
                        fileSystemPath,
                        parent,
                        context
                    )
                ) :
                Lists.empty();
        }

        return listing;
    }

    // setAuditInfo.....................................................................................................

    @Override
    void setAuditInfo0(final StorageValueInfo value,
                       final C context) {
        final StoragePath storagePath = value.path();

        final Path fileSystemPath = this.toPath(storagePath);

        final AuditInfo auditInfo = value.auditInfo();

        try {
            BasicFileAttributeView attributes = Files.getFileAttributeView(
                fileSystemPath,
                BasicFileAttributeView.class
            );
            attributes.setTimes(
                toFileTime(
                    auditInfo.lastModified()
                ),
                attributes.readAttributes()
                    .lastAccessTime(),
                toFileTime(
                    auditInfo.createdTimestamp()
                )
            );
        } catch (final IOException cause) {
            throw storagePath.invalidStoragePathException(
                "Unable to set creation & last modified",
                cause
            );
        }
    }

    // addWatcher.......................................................................................................

    private void registerTree(final Path start) {
        try {
            Files.walkFileTree(
                start,
                new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult preVisitDirectory(final Path dir,
                                                             final BasicFileAttributes attributes) throws IOException {
                        StorageShared2NativeFile.this.register(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
        } catch (final IOException cause) {
            throw new IllegalArgumentException("Unable to register watchers for tree", cause);
        }
    }

    private void register(final Path dir) throws IOException {
        this.watchKeyToPath.put(
            dir.register(
                this.watcher,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY
            ),
            dir
        );
    }

    /**
     * Note the poll events below handles registering new watchers for created sub-directories. Events from directories
     * such as create/modify/delete are filtered from the given {@link StorageWatcher}.
     */
    private void pollEvents(final WatchServicePoller<C> poller) {
        for (; ; ) {
            final WatchKey watchKey = poller.pollOrTakeWatchKey(this.watcher)
                .orElse(null);
            if (null == watchKey) {
                break;
            }
            final Path dir = this.watchKeyToPath.get(watchKey);

            if (dir != null) {
                for (final WatchEvent<?> event : watchKey.pollEvents()) {
                    final WatchEvent.Kind<?> kind = event.kind();

                    if (StandardWatchEventKinds.OVERFLOW != kind) {
                        // Context for directory entry event is the file name of entry
                        final Path path = dir.resolve(
                            Cast.<WatchEvent<Path>>to(event)
                                .context()
                        );

                        Path oldPath = null;
                        Path newPath = null;

                        // if directory create register parent and sub-directories
                        if (StandardWatchEventKinds.ENTRY_CREATE == kind) {
                            if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
                                this.registerTree(path);
                            }

                            newPath = path;
                        } else {
                            if (StandardWatchEventKinds.ENTRY_DELETE == kind) {
                                oldPath = path;
                            } else {
                                if (StandardWatchEventKinds.ENTRY_MODIFY == kind) {
                                    oldPath = path;
                                    newPath = path;
                                }
                            }
                        }

                        if (null != oldPath || null != newPath) {
                            if (Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
                                this.watchers.onValueChange(
                                    Optional.ofNullable(oldPath)
                                        .map(
                                            (final Path p) -> StorageValue.with(
                                                this.toStoragePath(p)
                                            )
                                        ),
                                    Optional.ofNullable(newPath)
                                        .flatMap(
                                            (final Path p) -> this.load(
                                                this.toStoragePath(p),
                                                poller.context()
                                            )
                                        )
                                );
                            }
                        }
                    }
                }
            }

            if (false == watchKey.reset()) {
                this.watchKeyToPath.remove(watchKey);
            }
        }
    }

    private final WatchService watcher;

    private final Map<WatchKey, Path> watchKeyToPath;

    @Override
    Runnable addWatcher0(final StorageWatcher watcher,
                         final C context) {
        return this.watchers.add(watcher);
    }

    @Override
    Runnable addWatcherOnce0(final StorageWatcher watcher,
                             final C context) {
        return this.watchers.addOnce(watcher);
    }

    private final StorageWatchers watchers = StorageWatchers.empty();

    // helpers..........................................................................................................

    /**
     * Helper that converts a {@link StoragePath} to the equivalent file system {@link Path}.
     */
    private Path toPath(final StoragePath storagePath) {
        this.stopped.check();

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
                    ).replace(
                        File.separatorChar,
                        StoragePath.SEPARATOR.character()
                    )
            )
        );
    }

    private static FileTime toFileTime(final LocalDateTime dateTime) {
        return FileTime.from(
            dateTime.toInstant(ZONE_OFFSET)
        );
    }

    private LocalDateTime toLocalDateTime(final FileTime fileTime) {
        return LocalDateTime.ofInstant(
            fileTime.toInstant(),
            ZONE_OFFSET // TODO read from Context
        );
    }

    // @VisibleForTesting
    final static ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;

    /**
     * The root directory containing all files and directories.
     */
    private final Path root;

    // Stoppable........................................................................................................

    @Override
    public void stop() {
        if(false == this.stopped.close()) {
            // kill background thread
        };
    }

    private final OpenChecker<IllegalStateException> stopped = OpenChecker.with(
        "NativeFile Storage stopped",
        (String message) -> new IllegalStateException(message)
    );

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.root.toString();
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
        printer.indent();
        {
            printer.println(
                CharSequences.quoteAndEscape(
                    this.root.toString()
                )
            );
        }
        printer.outdent();
    }
}
