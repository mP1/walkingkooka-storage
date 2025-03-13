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

/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
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
import walkingkooka.text.CharSequences;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link Path} that identifies a storage entry.
 */
final public class StoragePath
    implements Path<StoragePath, StorageName>,
    Comparable<StoragePath> {

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
     * Parses the {@link String} into a {@link StoragePath}
     */
    public static StoragePath parse(final String path) {
        SEPARATOR.checkBeginning(path);

        final StoragePath storagePath;

        switch (path) {
            case "/":
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
            StoragePath result = ROOT;

            if (path.length() > 1) {
                for (String component : path.substring(1).split(SEPARATOR.string())) {
                    result = result.append(StorageName.with(component));
                }
            }
            return result;
        } catch (final IllegalArgumentException cause) {
            throw new IllegalArgumentException(
                "Failed to parse " +
                    CharSequences.quote(path) +
                    ", message: " + cause.getMessage(),
                cause
            );
        }
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

    // value............................................................................................................

    @Override
    public String value() {
        return this.path;
    }

    private final String path;

    // append...........................................................................................................

    @Override
    public StoragePath append(final StorageName name) {
        Objects.requireNonNull(name, "name");

        StoragePath appended;

        switch (name.value()) {
            case "/":
                appended = this;
                break;
            case ".":
                appended = this; // ignore
                break;
            case "..":
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
        path.append(name.value());

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

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final StoragePath other) {
        Objects.requireNonNull(other, "other");

        return StorageName.CASE_SENSITIVITY.comparator()
            .compare(
                this.path,
                other.path
            );
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.path.hashCode();
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
}
