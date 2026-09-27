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
import walkingkooka.net.header.HasContentType;
import walkingkooka.net.header.MediaType;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;
import java.util.Optional;

/**
 * A value that contains a {@link StoragePath} and {@link Binary}. This exists mostly as an intermediate object
 * between objects and something like a native file system file.
 */
public final class StorageBinary implements HasPath<StoragePath>,
    HasBinary,
    HasContentType,
    TreePrintable {

    public static StorageBinary with(final StoragePath path,
                                     final Binary binary) {
        return new StorageBinary(
            Objects.requireNonNull(path, "path"),
            Objects.requireNonNull(binary, "binary"),
            NO_CONTENT_TYPE
        );
    }

    private StorageBinary(final StoragePath path,
                          final Binary binary,
                          final Optional<MediaType> contentType) {
        this.path = path;
        this.binary = binary;
        this.contentType = contentType;
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

    // HasContentType...................................................................................................

    @Override
    public Optional<MediaType> contentType() {
        return this.contentType;
    }

    private final Optional<MediaType> contentType;

    /**
     * Would be setter that returns a StorageValue with the given contentType creating a new instance if necessary.
     */
    public StorageBinary setContentType(final Optional<MediaType> contentType) {
        return this.contentType.equals(contentType) ?
            this :
            new StorageBinary(
                this.path,
                this.binary,
                Objects.requireNonNull(
                    contentType,
                    "contentType"
                )
            );
    }

    public StorageBinary clearContentType() {
        return this.setContentType(NO_CONTENT_TYPE);
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.path,
            this.binary,
            this.contentType
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof StorageBinary && this.equals0((StorageBinary) other);
    }

    private boolean equals0(final StorageBinary other) {
        return this.path.equals(other.path) &&
            this.binary.equals(other.binary) &&
            this.contentType.equals(other.contentType);
    }

    @Override
    public String toString() {
        return this.path + " " +
            this.contentType.map(Object::toString).orElse("") +
            this.binary;
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.path.toString());
        printer.indent();
        {
            final MediaType contentType = this.contentType.orElse(null);
            if(null != contentType) {
                printer.println(contentType.toString());
                printer.indent();
            }

            // Binary includes EOL
            printer.print(
                this.binary.toString()
            );

            if(null != contentType) {
                printer.outdent();
            }
        }
        printer.outdent();
    }

    // json.............................................................................................................

    static StorageBinary unmarshall(final JsonNode node,
                                    final JsonNodeUnmarshallContext context) {
        StoragePath storagePath = null;
        Binary binary = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case PATH_PROPERTY_STRING:
                    storagePath = context.unmarshall(
                        child,
                        StoragePath.class
                    );
                    break;
                case BINARY_PROPERTY_STRING:
                    binary = context.unmarshall(
                        child,
                        Binary.class
                    );
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(
                        name,
                        node
                    );
                    break;
            }
        }

        return with(
            storagePath,
            binary
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.object()
            .set(
                PATH_PROPERTY,
                context.marshall(
                    this.path
                )
            ).set(
                BINARY_PROPERTY,
                context.marshall(
                    this.binary
                )
            );
    }

    private final static String PATH_PROPERTY_STRING = "path";

    private final static String BINARY_PROPERTY_STRING = "binary";

    final static JsonPropertyName PATH_PROPERTY = JsonPropertyName.with(PATH_PROPERTY_STRING);

    final static JsonPropertyName BINARY_PROPERTY = JsonPropertyName.with(BINARY_PROPERTY_STRING);

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(StorageBinary.class),
            StorageBinary::unmarshall,
            StorageBinary::marshall,
            StorageBinary.class
        );
    }
}
