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

import walkingkooka.naming.HasPath;
import walkingkooka.text.CharSequences;

import java.util.Objects;

/**
 * An {@link IllegalArgumentException} used to report a problem with a path.
 */
public final class InvalidStoragePathException extends IllegalArgumentException
    implements HasPath<StoragePath> {

    private static final long serialVersionUID = 1L;

    InvalidStoragePathException(final String message,
                                final StoragePath path) {
        super(
            CharSequences.failIfNullOrEmpty(message, "message")
        );
        this.message = message;
        this.path = path;
    }

    private InvalidStoragePathException(final String message,
                                        final StoragePath path,
                                        final Throwable cause) {
        super(message, cause);

        this.message = message;
        this.path = path;
    }

    @Override
    public String getMessage() {
        return this.message + " " + this.path;
    }

    private final String message;

    @Override
    public StoragePath path() {
        return this.path;
    }

    public InvalidStoragePathException setPath(final StoragePath path) {
        Objects.requireNonNull(path, "path");

        return this.path.equals(path) ?
            this :
            new InvalidStoragePathException(
                this.message,
                path,
                this
            );
    }

    private StoragePath path;
}
