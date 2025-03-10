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
import walkingkooka.naming.Name;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CaseSensitivity;

/**
 * A case-sensitive key to a value in a store.
 */
public final class StorageKey implements Name,
    Comparable<StorageKey> {

    // constants

    static final CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.SENSITIVE;

    /**
     * {@link CharPredicate} that may be used to match the first valid character of a {@link StorageKey}.
     */
    public final static CharPredicate INITIAL = CharPredicates.range('A', 'Z')
        .or(
            CharPredicates.range('a', 'z')
        );

    /**
     * {@link CharPredicate} that may be used to match the non first valid character of a {@link StorageKey}.
     */
    public final static CharPredicate PART = INITIAL.or(
        CharPredicates.range('0', '9')
            .or(
                CharPredicates.is('.')
            )
    );

    /**
     * The minimum valid length
     */
    public final static int MIN_LENGTH = 1;

    /**
     * The maximum valid length
     */
    public final static int MAX_LENGTH = 255;

    /**
     * Factory that creates a {@link StorageKey}
     */
    public static StorageKey with(final String name) {
        CharPredicates.failIfNullOrEmptyOrInitialAndPartFalse(name, StorageKey.class.getSimpleName(), INITIAL, PART);

        InvalidTextLengthException.throwIfFail(
            "storageKey",
            name,
            MIN_LENGTH,
            MAX_LENGTH
        );

        return new StorageKey(name);
    }

    /**
     * Private constructor
     */
    private StorageKey(final String name) {
        super();
        this.name = name;
    }

    // Name.............................................................................................................

    @Override
    public String value() {
        return this.name;
    }

    private final String name;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return CASE_SENSITIVITY.hash(this.name);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof StorageKey &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final StorageKey other) {
        return this.compareTo(other) == 0;
    }

    @Override
    public String toString() {
        return this.name;
    }

    // Comparable ......................................................................................................

    @Override
    public int compareTo(final StorageKey other) {
        return CASE_SENSITIVITY.comparator()
            .compare(
                this.name,
                other.name
            );
    }

    // HasCaseSensitivity...............................................................................................

    @Override
    public CaseSensitivity caseSensitivity() {
        return CASE_SENSITIVITY;
    }
}
