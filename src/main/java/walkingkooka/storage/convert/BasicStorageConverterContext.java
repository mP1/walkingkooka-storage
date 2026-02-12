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

package walkingkooka.storage.convert;

import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterContextDelegator;
import walkingkooka.storage.HasUserDirectories;
import walkingkooka.storage.StoragePath;

import java.util.Objects;
import java.util.Optional;

final class BasicStorageConverterContext implements StorageConverterContext,
    ConverterContextDelegator {

    static BasicStorageConverterContext with(final HasUserDirectories hasUserDirectories,
                                             final ConverterContext context) {
        Objects.requireNonNull(hasUserDirectories, "hasUserDirectories");
        Objects.requireNonNull(context, "context");

        return new BasicStorageConverterContext(hasUserDirectories, context);
    }

    private BasicStorageConverterContext(final HasUserDirectories hasUserDirectories,
                                         final ConverterContext context) {
        this.hasUserDirectories = hasUserDirectories;
        this.context = context;
    }


    @Override
    public StoragePath parseStoragePath(final String text) {
        return StoragePath.parseMaybeRelative(
            text,
            this
        );
    }

    // StorageConverterContext..........................................................................................

    @Override
    public Optional<StoragePath> currentWorkingDirectory() {
        return this.hasUserDirectories.currentWorkingDirectory();
    }

    private final HasUserDirectories hasUserDirectories;

    // ConverterContextDelegator........................................................................................

    @Override
    public ConverterContext converterContext() {
        return this.context;
    }

    private final ConverterContext context;

    // toString.........................................................................................................

    @Override
    public String toString() {
        return "hasUserDirectories: " + this.hasUserDirectories + ", context: " + this.context;
    }
}
