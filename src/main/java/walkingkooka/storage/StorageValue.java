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

import walkingkooka.Cast;
import walkingkooka.HasId;
import walkingkooka.ToStringBuilder;
import walkingkooka.Value;
import walkingkooka.net.header.MediaType;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Objects;
import java.util.Optional;

/**
 * A value type that holds the storage value and some extra meta data.
 * Instances are not meant be marshalled to JSON or serializable.
 */
public final class StorageValue implements Value<Optional<Object>>,
    HasId<Optional<StoragePath>>,
    Comparable<StorageValue>,
    TreePrintable {

    public final static MediaType DEFAULT_CONTENT_TYPE = MediaType.BINARY;

    public final static Optional<Object> NO_VALUE = Optional.empty();

    public final static StorageValue ROOT = new StorageValue(
        StoragePath.ROOT,
        NO_VALUE,
        MediaType.BINARY
    );

    public static StorageValue with(final StoragePath path,
                                    final Optional<Object> value) {
        return StoragePath.ROOT.equals(path) &&
            NO_VALUE.equals(value) ?
            ROOT :
            new StorageValue(
                Objects.requireNonNull(path, "path"),
                Objects.requireNonNull(value, "value"),
                MediaType.BINARY
            );
    }

    private StorageValue(final StoragePath path,
                         final Optional<Object> value,
                         final MediaType contentType) {
        this.path = path;
        this.value = value;
        this.contentType = contentType;
    }

    // Value............................................................................................................

    @Override
    public Optional<Object> value() {
        return this.value;
    }

    private final Optional<Object> value;

    /**
     * Would be setter that returns a StorageValue with the given value creating a new instance if necessary.
     */
    public StorageValue setValue(final Optional<Object> value) {
        return this.value.equals(value) ?
            this :
            new StorageValue(
                this.path,
                Objects.requireNonNull(value, "value"),
                this.contentType
            );
    }

    // HasId............................................................................................................

    @Override
    public Optional<StoragePath> id() {
        return Optional.of(
            this.path()
        );
    }

    public StoragePath path() {
        return this.path;
    }

    public StorageValue setPath(final StoragePath path) {
        return this.path.equals(path) ?
            this :
            StoragePath.ROOT.equals(path) &&
                NO_VALUE.equals(this.value) &&
                MediaType.BINARY.equals(this.contentType) ?
                ROOT :
                new StorageValue(
                    Objects.requireNonNull(path, "path"),
                    this.value,
                    this.contentType
                );
    }

    private final StoragePath path;

    // ContentType......................................................................................................

    public MediaType contentType() {
        return this.contentType;
    }

    private final MediaType contentType;

    /**
     * Would be setter that returns a StorageValue with the given contentType creating a new instance if necessary.
     */
    public StorageValue setContentType(final MediaType contentType) {
        return this.contentType.equals(contentType) ?
            this :
            new StorageValue(
                this.path,
                this.value,
                Objects.requireNonNull(
                    contentType,
                    "contentType"
                )
            );
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.path,
            this.value,
            this.contentType
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof StorageValue &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final StorageValue other) {
        return this.path.equals(other.path) &&
            this.value.equals(other.value) &&
            this.contentType.equals(other.contentType);
    }

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .label(this.path.toString())
            .separator("=")
            .value(this.value)
            .separator(" ")
            .value(this.contentType)
            .build();
    }

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final StorageValue other) {
        return this.path.compareTo(other.path());
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.path.toString());

        printer.indent();
        {
            boolean indentValue = false;

            final MediaType contentType = this.contentType;
            if (false == contentType.equals(MediaType.BINARY)) {
                printer.print("contentType: ");
                printer.println(contentType.toString());

                indentValue = true;
            }

            final Optional<Object> value = this.value;
            if (value.isPresent()) {
                if (indentValue) {
                    printer.indent();
                }

                TreePrintable.printTreeOrToString(
                    value.get(),
                    printer
                );

                if (indentValue) {
                    printer.outdent();
                }
            }

        }
        printer.outdent();
    }
}
