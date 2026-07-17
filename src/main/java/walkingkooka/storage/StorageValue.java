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
import walkingkooka.HasValue;
import walkingkooka.ToStringBuilder;
import walkingkooka.naming.HasPath;
import walkingkooka.net.header.HasContentType;
import walkingkooka.net.header.MediaType;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;
import java.util.Optional;

/**
 * A value type that holds the storage value and some extra meta data.
 * Instances are not meant be marshalled to JSON or serializable.
 */
public final class StorageValue implements HasContentType,
    HasValue<Optional<Object>>,
    HasId<Optional<StoragePath>>,
    HasPath<StoragePath>,
    TreePrintable {

    public final static Optional<Object> NO_VALUE = Optional.empty();

    /**
     * A {@link StorageValue} constant that is returned representing the {@link StoragePath} within a listing etc.
     */
    public final static StorageValue ROOT = new StorageValue(
        StoragePath.ROOT,
        NO_VALUE,
        NO_CONTENT_TYPE
    );

    public static StorageValue with(final StoragePath path) {
        return StoragePath.ROOT.equals(path) ?
            ROOT :
            new StorageValue(
                Objects.requireNonNull(path, "path"),
                NO_VALUE,
                HasContentType.NO_CONTENT_TYPE
            );
    }

    private StorageValue(final StoragePath path,
                         final Optional<Object> value,
                         final Optional<MediaType> contentType) {
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
     * If {@link #contentType()} is empty and the new value is present and implements {@link HasContentType},
     * the content type will be replaced.
     */
    public StorageValue setValue(final Optional<Object> value) {
        return this.value.equals(value) ?
            this :
            new StorageValue(
                this.path,
                Objects.requireNonNull(value, "value"),
                this.contentType.isEmpty() ?
                    value.filter(v -> v instanceof HasContentType)
                        .flatMap(
                            (Object hasContentType) -> ((HasContentType) hasContentType)
                                .contentType()
                        ) :
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

    // path.............................................................................................................

    @Override
    public StoragePath path() {
        return this.path;
    }

    public StorageValue setPath(final StoragePath path) {
        return this.path.equals(path) ?
            this :
            StoragePath.ROOT.equals(path) &&
                NO_VALUE.equals(this.value) && this.contentType.isEmpty() ?
                ROOT :
                new StorageValue(
                    Objects.requireNonNull(path, "path"),
                    this.value,
                    this.contentType
                );
    }

    // StorageSharedPrefixed...................................................................................................

    StorageValue prependPath(final StoragePath prefix) {
        return this.setPath(
            this.path()
                .prepend(prefix)
        );
    }

    StorageValue removePrefixPath(final StoragePath prefix) {
        return this.setPath(
            this.path()
                .removePrefix(prefix)
        );
    }

    private final StoragePath path;

    // ContentType......................................................................................................

    @Override
    public Optional<MediaType> contentType() {
        return this.contentType;
    }

    private final Optional<MediaType> contentType;

    /**
     * Would be setter that returns a StorageValue with the given contentType creating a new instance if necessary.
     */
    public StorageValue setContentType(final Optional<MediaType> contentType) {
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

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
        printer.indent();
        {
            printer.println(this.path.toString());

            printer.indent();
            {
                boolean indentValue = false;

                final MediaType contentType = this.contentType.orElse(null);
                if (null != contentType) {
                    printer.print("contentType: ");
                    printer.println(
                        contentType.toString()
                    );

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
        printer.outdent();
    }

    // Json.............................................................................................................

    private final static String PATH_PROPERTY_STRING = "path";

    final static JsonPropertyName PATH_PROPERTY = JsonPropertyName.with(PATH_PROPERTY_STRING);

    private final static String CONTENT_TYPE_PROPERTY_STRING = "contentType";

    final static JsonPropertyName CONTENT_TYPE_PROPERTY = JsonPropertyName.with(CONTENT_TYPE_PROPERTY_STRING);

    private final static String VALUE_PROPERTY_STRING = "value";

    final static JsonPropertyName VALUE_PROPERTY = JsonPropertyName.with(VALUE_PROPERTY_STRING);

    static StorageValue unmarshall(final JsonNode node,
                                   final JsonNodeUnmarshallContext context) {
        StoragePath path = null;
        Optional<MediaType> contentType = HasContentType.NO_CONTENT_TYPE;
        Optional<Object> value = null;

        for (JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case PATH_PROPERTY_STRING:
                    path = context.unmarshall(
                        child,
                        StoragePath.class
                    );
                    break;
                case CONTENT_TYPE_PROPERTY_STRING:
                    contentType = context.unmarshallOptional(
                        child,
                        MediaType.class
                    );
                    break;
                case VALUE_PROPERTY_STRING:
                    value = context.unmarshallOptionalWithType(child);
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        if (null == path) {
            JsonNodeUnmarshallContext.missingProperty(
                PATH_PROPERTY,
                node
            );
        }
        if (null == value) {
            JsonNodeUnmarshallContext.missingProperty(
                VALUE_PROPERTY,
                node
            );
        }

        return with(path)
            .setValue(value)
            .setContentType(contentType);
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        JsonObject json = JsonNode.object()
            .set(
                PATH_PROPERTY,
                context.marshall(this.path)
            ).set(
                VALUE_PROPERTY,
                context.marshallOptionalWithType(this.value)
            );

        final Optional<MediaType> contentType = this.contentType;
        if (contentType.isPresent()) {
            json = json.set(
                CONTENT_TYPE_PROPERTY,
                context.marshallOptional(contentType)
            );
        }
        return json;
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(StorageValue.class),
            StorageValue::unmarshall,
            StorageValue::marshall,
            StorageValue.class
        );
    }
}
