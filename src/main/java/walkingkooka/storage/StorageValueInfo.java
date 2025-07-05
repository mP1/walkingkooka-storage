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

import walkingkooka.environment.AuditInfo;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.text.Normalizer.Form;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Holds the metadata for a single {@link StoragePath}.
 */
public final class StorageValueInfo implements Comparable<StorageValueInfo>,
    TreePrintable {

    public static StorageValueInfo with(final StoragePath path,
                                        final AuditInfo auditInfo) {
        return new StorageValueInfo(
            Objects.requireNonNull(path, "path"),
            Objects.requireNonNull(auditInfo, "auditInfo")
        );
    }

    private StorageValueInfo(final StoragePath path,
                             final AuditInfo auditInfo) {
        this.path = path;
        this.auditInfo = auditInfo;
    }

    // path..............................................................................................................

    public StoragePath path() {
        return this.path;
    }

    public StorageValueInfo setPath(final StoragePath path) {
        return this.path.equals(path) ?
            this :
            new StorageValueInfo(
                Objects.requireNonNull(path, "path"),
                this.auditInfo
            );
    }

    private final StoragePath path;

    // auditInfo........................................................................................................

    public AuditInfo auditInfo() {
        return this.auditInfo;
    }

    private final AuditInfo auditInfo;

    public StorageValueInfo setAuditInfo(final AuditInfo auditInfo) {
        return this.auditInfo.equals(auditInfo) ?
            this :
            new StorageValueInfo(
                this.path,
                Objects.requireNonNull(auditInfo, "auditInfo")
            );
    }

    // Comparable.......................................................................................................

    /**
     * Only compares the path ignoring the audit component.
     */
    @Override
    public int compareTo(final StorageValueInfo other) {
        return this.path.compareTo(other.path);
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.path,
            this.auditInfo
        );
    }

    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof StorageValueInfo && this.equals0((StorageValueInfo) other));
    }

    private boolean equals0(final StorageValueInfo other) {
        return
            this.path.equals(other.path) &&
                this.auditInfo.equals(other.auditInfo);
    }

    @Override
    public String toString() {
        return this.path +
            " " +
            this.auditInfo;
    }

    // Json.............................................................................................................

    private final static String PATH_PROPERTY_STRING = "path";

    final static JsonPropertyName PATH_PROPERTY = JsonPropertyName.with(PATH_PROPERTY_STRING);

    private final static String AUDIT_INFO_PROPERTY_STRING = "auditInfo";

    final static JsonPropertyName AUDIT_INFO_PROPERTY = JsonPropertyName.with(AUDIT_INFO_PROPERTY_STRING);

    static StorageValueInfo unmarshall(final JsonNode node,
                                       final JsonNodeUnmarshallContext context) {
        StoragePath path = null;
        AuditInfo auditInfo = null;

        for (JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case PATH_PROPERTY_STRING:
                    path = context.unmarshall(
                        child,
                        StoragePath.class
                    );
                    break;
                case AUDIT_INFO_PROPERTY_STRING:
                    auditInfo = context.unmarshall(
                        child,
                        AuditInfo.class
                    );
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
        if (null == auditInfo) {
            JsonNodeUnmarshallContext.missingProperty(
                AUDIT_INFO_PROPERTY,
                node
            );
        }

        return with(
            path,
            auditInfo
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.object()
            .set(
                PATH_PROPERTY,
                context.marshall(this.path)
            ).set(
                AUDIT_INFO_PROPERTY,
                context.marshall(this.auditInfo)
            );
    }

    static {
        StoragePath.ROOT.isRoot();
        AuditInfo.with(
            EmailAddress.parse("user@example.com"),
            LocalDateTime.MIN,
            EmailAddress.parse("user@example.com"),
            LocalDateTime.MIN
        );

        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(Form.class),
            StorageValueInfo::unmarshall,
            StorageValueInfo::marshall,
            StorageValueInfo.class
        );
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.path.toString());

        printer.indent();
        {
            this.auditInfo.printTree(printer);
        }
        printer.outdent();
    }
}
