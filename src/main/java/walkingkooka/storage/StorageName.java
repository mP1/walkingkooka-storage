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
import walkingkooka.InvalidTextLengthException;
import walkingkooka.io.FileExtension;
import walkingkooka.naming.Name;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CaseSensitivity;

import java.util.Optional;

/**
 * The name portion of a {@link StoragePath}
 */
public final class StorageName implements Name,
        Comparable<StorageName> {

    /**
     * storage names are case-sensitive.
     */
    final static CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.SENSITIVE;

    public final static int MIN_LENGTH = 1;

    public final static int MAX_LENGTH = 255;

    final static StorageName ROOT = new StorageName(
        StoragePath.SEPARATOR.string()
    );

    private final static CharPredicate CHARACTERS = CharPredicates.printable().andNot(
        CharPredicates.is(
            StoragePath.SEPARATOR.character()
        )
    );

    /**
     * Factory that creates a new {@link StorageName}
     */
    public static StorageName with(final String name) {
        InvalidTextLengthException.throwIfFail(
            "name",
            name,
            MIN_LENGTH,
            MAX_LENGTH
        );

        return new StorageName(
            CharPredicates.failIfNullOrEmptyOrInitialAndPartFalse(
                name,
                "name",
                CHARACTERS, // initial
                CHARACTERS // part
            )
        );
    }

    private StorageName(final String name) {
        this.name = name;
    }

    @Override
    public String value() {
        return this.name;
    }

    private final String name;

    /**
     * Returns the file extension without the '.' if one was present.
     */
    public Optional<FileExtension> fileExtension() {
        if (null == this.fileExtension) {
            this.fileExtension = FileExtension.extract(this.name);
        }
        return this.fileExtension;
    }

    /**
     * A cached copy of the extracted {@link FileExtension}.
     */
    transient Optional<FileExtension> fileExtension;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return CASE_SENSITIVITY.hash(this.name);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof StorageName &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final StorageName other) {
        return CASE_SENSITIVITY.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return this.name;
    }

    // Comparable ......................................................................................................

    @Override
    public int compareTo(final StorageName other) {
        return CASE_SENSITIVITY.comparator()
                .compare(this.name, other.name);
    }

    // HasCaseSensitivity...............................................................................................

    @Override
    public CaseSensitivity caseSensitivity() {
        return CASE_SENSITIVITY;
    }
}
