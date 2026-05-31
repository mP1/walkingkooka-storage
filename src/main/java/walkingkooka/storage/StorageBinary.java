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
import walkingkooka.HasBinary;
import walkingkooka.naming.HasPath;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Objects;

/**
 * A value that contains a {@link StoragePath} and {@link Binary}. This exists mostly as an intermediate object
 * between objects and something like a native file system file.
 */
public final class StorageBinary implements HasPath<StoragePath>, HasBinary,
    TreePrintable {

    public static StorageBinary with(final StoragePath path,
                                     final Binary binary) {
        return new StorageBinary(
            Objects.requireNonNull(path, "path"),
            Objects.requireNonNull(binary, "binary")
        );
    }

    private StorageBinary(final StoragePath path,
                          final Binary binary) {
        this.path = path;
        this.binary = binary;
    }

    @Override
    public StoragePath path() {
        return this.path;
    }

    private final StoragePath path;

    @Override
    public Binary binary() {
        return this.binary;
    }

    private final Binary binary;

// Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.path,
            this.binary
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof StorageBinary && this.equals0((StorageBinary) other);
    }

    private boolean equals0(final StorageBinary other) {
        return this.path.equals(other.path) &&
            this.binary.equals(other.binary);
    }

    @Override
    public String toString() {
        return this.path + " " + this.binary;
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.path.toString());
        printer.indent();
        {
            // Binary includes EOL
            printer.print(
                this.binary.toString()
            );
        }
        printer.outdent();
    }
}
