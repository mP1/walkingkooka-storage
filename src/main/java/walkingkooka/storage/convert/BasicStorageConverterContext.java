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
import walkingkooka.storage.StoragePath;

import java.util.Objects;
import java.util.Optional;

final class BasicStorageConverterContext implements StorageConverterContext,
    ConverterContextDelegator {

    static BasicStorageConverterContext with(final Optional<StoragePath> currentWorkingDirectory,
                                             final ConverterContext context) {
        Objects.requireNonNull(currentWorkingDirectory, "currentWorkingDirectory");
        Objects.requireNonNull(context, "context");

        return new BasicStorageConverterContext(currentWorkingDirectory, context);
    }

    private BasicStorageConverterContext(final Optional<StoragePath> currentWorkingDirectory,
                                         final ConverterContext context) {
        this.currentWorkingDirectory = currentWorkingDirectory;
        this.context = context;
    }

    // StorageConverterContext..........................................................................................

    @Override
    public Optional<StoragePath> currentWorkingDirectory() {
        return this.currentWorkingDirectory;
    }

    private final Optional<StoragePath> currentWorkingDirectory;

    // ConverterContextDelegator........................................................................................

    @Override
    public ConverterContext converterContext() {
        return this.context;
    }

    private final ConverterContext context;

    // toString.........................................................................................................

    @Override
    public String toString() {
        return "currentWorkingDirectory: " + this.currentWorkingDirectory + ", context: " + this.context;
    }
}
