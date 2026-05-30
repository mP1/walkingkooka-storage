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

import walkingkooka.compare.Comparators;
import walkingkooka.naming.Path;
import walkingkooka.naming.PathSeparator;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.HasCaseSensitivity;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link Path} that identifies a storage entry.
 */
final public class StoragePath
    implements Path<StoragePath, StorageName>,
    Comparable<StoragePath>,
    HasCaseSensitivity,
    TreePrintable {

    final static String SEPARATOR_STRING = "/";

    /**
     * {@link PathSeparator} instance
     */
    public final static PathSeparator SEPARATOR = PathSeparator.requiredAtStart('/');

    /**
     * Convenient constant holding the root.
     */
    public final static StoragePath ROOT = new StoragePath(
        StoragePath.SEPARATOR.string(),
        StorageName.ROOT,
        Optional.empty()
    );

    /**
     * Parses the {@link String} into a {@link StoragePath}. Not path navigation components such as EMPTY, DOT and
     * DOUBLE DOT are processed and the path normalized.
     */
    public static StoragePath parse(final String path) {
        SEPARATOR.checkBeginning(path);

        final StoragePath storagePath;

        switch (path) {
            case SEPARATOR_STRING:
                storagePath = ROOT;
                break;
            default:
                storagePath = parseNonRoot(path);
                break;
        }

        return storagePath;
    }

    private static StoragePath parseNonRoot(final String path) {
        try {
            StoragePath storagePath = ROOT;

            final int length = path.length();
            int nameStart = 1; // must start with slash

            final StringBuilder b = new StringBuilder()
                .append(SEPARATOR);

            while (nameStart < length) {
                final int next = path.indexOf(
                    SEPARATOR.character(),
                    nameStart
                );

                final int nameEnd = -1 != next ?
                    next :
                    length -
                        (
                            path.charAt(
                                length - 1
                            ) == SEPARATOR.character() ?
                                1 :
                                0
                        );

                // skip empty components
                if (nameStart != nameEnd) {
                    final String name = path.substring(
                        nameStart,
                        nameEnd
                    );

                    switch (name) {
                        case CURRENT:
                            break;
                        case PARENT:
                            storagePath = storagePath.parent()
                                .orElse(ROOT);

                            b.setLength(
                                storagePath.path.length()
                            );
                            break;
                        default:
                            b.append(name);

                            if (-1 != next) {
                                b.append(SEPARATOR_STRING);
                            }

                            storagePath = new StoragePath(
                                b.toString(),
                                StorageName.with(name),
                                Optional.ofNullable(storagePath)
                            );
                            break;
                    }
                }

                if (next == -1) {
                    break;
                }

                nameStart = next + 1;
            }

            return storagePath;
        } catch (final IllegalArgumentException cause) {
            // Failed to parse "/path111/path222", message
            throw new IllegalArgumentException(
                "Failed to parse " +
                    CharSequences.quote(path) +
                    ", message: " + cause.getMessage(),
                cause
            );
        }
    }

    /**
     * Replaced by the {@link HasUserDirectories#homeDirectory()} by {@link #parseSpecial(String, HasUserDirectories)}.
     */
    public final static CharacterConstant USER_HOME = CharacterConstant.with('~');

    /**
     * Parses the given text as an absolute path or relative using the given current for the later.
     */
    public static StoragePath parseSpecial(final String text,
                                           final HasUserDirectories hasUserDirectories) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(hasUserDirectories, "hasUserDirectories");

        return text.isEmpty() ?
            currentWorkingDirectoryOrRoot(hasUserDirectories) :
            StoragePath.parse(
                text.startsWith(USER_HOME.string()) ?
                    hasUserDirectories.homeDirectory()
                        .orElseThrow(() -> new IllegalArgumentException("Missing home directory"))
                        .value()
                        .concat(
                            text.substring(
                                1 // skip ~
                            )
                        ) :
                    text.startsWith(
                        SEPARATOR.string()
                    ) ?
                        text :
                        currentWorkingDirectoryOrRoot(hasUserDirectories)
                            .value() +
                            SEPARATOR.character() +
                            text
            );
    }

    private static StoragePath currentWorkingDirectoryOrRoot(final HasUserDirectories has) {
        return has.currentWorkingDirectory()
            .orElse(StoragePath.ROOT);
    }

    /**
     * Private constructor
     */
    private StoragePath(final String path,
                        final StorageName name,
                        final Optional<StoragePath> parent) {
        super();
        this.path = path;
        this.name = name;
        this.parent = parent;
    }

    @Override
    public StoragePath append(final StoragePath path) {
        Objects.requireNonNull(path, "path");

        StoragePath appended = this;

        if (false == path.isRoot()) {
            if (false == this.isRoot() && false == this.isEmpty()) {
                for (StorageName component : path) {
                    appended = appended.append(component);
                }

                final String pathValue = path.value();
                if (pathValue.endsWith(SEPARATOR_STRING)) {
                    appended = new StoragePath(
                        appended.path.concat(SEPARATOR_STRING), // replace #path with path ending with slash
                        appended.name,
                        appended.parent
                    );
                }
            } else {
                appended = path;
            }
        }

        return appended;
    }

    // value............................................................................................................

    @Override
    public String value() {
        return this.path;
    }

    private final String path;

    // removePrefix.....................................................................................................

    /**
     * Removes the required given prefix, throwing an {@link IllegalArgumentException} if the prefix is missing.
     */
    public StoragePath removePrefix(final StoragePath prefix) {
        StoragePath removed;

        if (ROOT.equals(prefix)) {
            removed = this;
        } else {
            final String path = this.path;
            final String prefixString = prefix.toString();

            if (CASE_SENSITIVITY.equals(path, prefixString)) {
                removed = ROOT;
            } else {
                if (false == CASE_SENSITIVITY.startsWith(path, prefixString.concat(SEPARATOR_STRING))) {
                    // Prefix "/prefix123" missing from path "/path111/path222"
                    throw this.invalidStoragePathException(
                        "Prefix " +
                            CharSequences.quoteAndEscape(prefixString) +
                            " missing from path"
                    );
                }

                removed = parse(
                    path.substring(
                        prefixString.length()
                    )
                );
            }
        }

        return removed;
    }

    // prepend..........................................................................................................

    /**
     * Returns a {@link StoragePath} with the given {@link StorageName} prefixed.
     */
    public StoragePath prepend(final StorageName name) {
        Objects.requireNonNull(name, "name");

        final StoragePath prepended;

        switch (name.value()) {
            case StorageName.ROOT_NAME:
                prepended = this;
                break;
            case CURRENT:
                prepended = this; // ignore
                break;
            case PARENT:
                prepended = this.parent.orElse(ROOT);
                break;
            default:
                prepended = this.prepend(
                    ROOT.append(name)
                );
                break;
        }

        return prepended;
    }

    /**
     * Returns a {@link StoragePath} with the given {@link StoragePath} prefixed.
     */
    public StoragePath prepend(final StoragePath path) {
        Objects.requireNonNull(path, "path");

        return path.append(this);
    }

    // append...........................................................................................................

    @Override
    public StoragePath append(final StorageName name) {
        Objects.requireNonNull(name, "name");

        StoragePath appended;

        switch (name.value()) {
            case StorageName.ROOT_NAME:
                appended = this;
                break;
            case CURRENT:
                appended = this; // ignore
                break;
            case PARENT:
                appended = this.parent.orElse(ROOT);
                break;
            default:
                appended = this.appendNonRootName(name);
                break;
        }

        return appended;
    }

    private StoragePath appendNonRootName(final StorageName name) {
        final StringBuilder path = new StringBuilder();
        path.append(this.path);
        if (false == this.isRoot()) {
            path.append(SEPARATOR);
        }
        path.append(
            name.value()
        );

        return new StoragePath(
            path.toString(), // path
            name,
            Optional.of(this) // parent
        );
    }

    private final Optional<StoragePath> parent;

    /**
     * Returns the parent {@link StoragePath}.
     */
    @Override
    public Optional<StoragePath> parent() {
        return this.parent;
    }

    private final StorageName name;

    @Override
    public StorageName name() {
        return this.name;
    }

    /**
     * {@link PathSeparator} getter.
     */
    @Override
    public PathSeparator separator() {
        return StoragePath.SEPARATOR;
    }

    /**
     * Only returns true if this {@link StoragePath} is the {@link #ROOT}.
     */
    @Override
    public boolean isRoot() {
        return this == StoragePath.ROOT;
    }

    /**
     * Some paths represent a value others a parent holding values (a directory/folder).
     */
    public boolean isValue() {
        return false == this.isParent();
    }

    /**
     * Returns true if this path is a parent or directory and not a file.
     * Parent paths will end with a slash or {@link #SEPARATOR}.
     */
    public boolean isParent() {
        return this.path.endsWith(SEPARATOR_STRING);
    }

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final StoragePath other) {
        Objects.requireNonNull(other, "other");

        return CASE_SENSITIVITY.comparator()
            .compare(
                this.path,
                other.path
            );
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return CASE_SENSITIVITY.hash(this.path);
    }

    @Override
    public boolean equals(final Object other) {
        return (this == other) ||
            ((other instanceof StoragePath) && this.equals0((StoragePath) other));
    }

    private boolean equals0(final StoragePath other) {
        return this.compareTo(other) == Comparators.EQUAL;
    }

    @Override
    public String toString() {
        return this.path;
    }

    CharSequence quotedAppendedWithStar() {
        return CharSequences.quoteAndEscape(
            this.isRoot() ?
                "/*" :
                this.path + "/*"
        );
    }

    // TreePrintable....................................................................................................

    /**
     * Implemented so that test failures comparing a Collection of {@link StoragePath} will print vertically instead of
     * a long line. The former is easier to see differences in Intellij.
     */
    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.path);
    }

    // json.............................................................................................................

    static StoragePath unmarshall(final JsonNode node,
                                  final JsonNodeUnmarshallContext context) {
        return parse(
            node.stringOrFail()
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.path);
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(StoragePath.class),
            StoragePath::unmarshall,
            StoragePath::marshall,
            StoragePath.class
        );
    }

    // InvalidStoragePathException......................................................................................

    public InvalidStoragePathException invalidStoragePathException(final String message) {
        return new InvalidStoragePathException(
            message,
            this
        );
    }

    public InvalidStoragePathException invalidStoragePathException(final String message,
                                                                   final Throwable cause) {
        return new InvalidStoragePathException(
            message,
            this,
            cause
        );
    }

    // HasCaseSensitivity...............................................................................................

    public final static CaseSensitivity CASE_SENSITIVITY = StorageName.CASE_SENSITIVITY;

    @Override
    public CaseSensitivity caseSensitivity() {
        return CASE_SENSITIVITY;
    }
}
