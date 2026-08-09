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

import walkingkooka.text.CharSequences;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Objects;

/**
 * A single mount within a {@link RoutingStorage}.
 */
public final class StorageMountPoint<C extends StorageContext> implements Comparable<StorageMountPoint<C>>,
    TreePrintable {

    static <C extends StorageContext> StorageMountPoint<C> with(final StoragePath path,
                                                                final Storage<C> storage) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(storage, "storage");

        return new StorageMountPoint<>(
            path,
            storage
        );
    }

    private StorageMountPoint(final StoragePath path,
                              final Storage<C> storage) {
        super();
        this.path = path;
        this.storage = storage;

        this.storagePathSlash = path.isRoot() ?
            path.value() :
            path.value()
                .concat(StoragePath.SEPARATOR.string());
    }

    // /mount111 vs /mount111 -> true
    // /mount111 vs /mount111/under222 -> true
    // /mount222 vs /mount333/under333 -> false
    // / vs /under444 -> true
    boolean isMatch(final StoragePath path) {
        return this.path.equals(path) ||
            StoragePath.CASE_SENSITIVITY.startsWith(
                path.value(),
                this.storagePathSlash
            );
    }

    private final String storagePathSlash;

    // /mount1/path2/path3
    //
    // /path/path2
    StoragePath remove(final StoragePath path) {
        final StoragePath thisStoragePath = this.path;

        return thisStoragePath.isRoot() ?
            path :
            thisStoragePath.equals(path) ?
                StoragePath.ROOT :
                StoragePath.parse(
                    path.value()
                        .substring(
                            thisStoragePath.value().length()
                        )
                );
    }

    StoragePath add(final StoragePath path) {
        return this.path.append(path);
    }

    Runnable addWatcher(final StorageWatcher watcher,
                        final C context) {
        return this.storage.addWatcher(
            watcher.setPathPrefix(this.path),
            context
        );
    }

    Runnable addWatcherOnce(final StorageWatcher watcher,
                            final C context) {
        return this.storage.addWatcherOnce(
            watcher.setPathPrefix(this.path),
            context
        );
    }

    // path.............................................................................................................

    public StoragePath path() {
        return this.path;
    }

    final StoragePath path;

    // storage..........................................................................................................

    public Storage<C> storage() {
        return this.storage;
    }


    final Storage<C> storage;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.path,
            this.storage
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof StorageMountPoint && this.equals0((StorageMountPoint<?>) other);
    }

    private boolean equals0(final StorageMountPoint<?> other) {
        return this.path.equals(other.path) &&
            this.storage.equals(other.storage);
    }

    @Override
    public String toString() {
        return this.path.quotedAppendedWithStar() + " " + this.storage;
    }

    // Comparable.......................................................................................................

    /**
     * Compares paths reversed, so parents will appear before children.
     */
    @Override
    public int compareTo(final StorageMountPoint<C> other) {
        return -this.path.compareTo(other.path);
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());

        printer.indent();
        {
            printer.println(
                CharSequences.quoteAndEscape(this.path.value())
            );

            printer.indent();
            {
                TreePrintable.printTreeOrToString(
                    this.storage,
                    printer
                );
            }
            printer.outdent();
        }
        printer.outdent();
    }
}
