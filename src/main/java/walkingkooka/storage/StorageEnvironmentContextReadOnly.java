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

import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Wraps another {@link EnvironmentContext} using a {@link Predicate} to match and fail read only {@link EnvironmentValueName}.
 */
final class StorageEnvironmentContextReadOnly implements StorageEnvironmentContext,
    EnvironmentContextDelegator,
    TreePrintable {

    static StorageEnvironmentContextReadOnly with(final Predicate<EnvironmentValueName<?>> readOnlyFilter,
                                                  final EnvironmentContext environmentContext) {
        Objects.requireNonNull(readOnlyFilter, "readOnlyFilter");
        Objects.requireNonNull(environmentContext, "environmentContext");

        StorageEnvironmentContextReadOnly storageEnvironmentContextReadOnly = null;

        if (environmentContext instanceof StorageEnvironmentContextReadOnly) {
            storageEnvironmentContextReadOnly = (StorageEnvironmentContextReadOnly) environmentContext;

            if (false == readOnlyFilter.equals(storageEnvironmentContextReadOnly.readOnlyFilter)) {
                storageEnvironmentContextReadOnly = null;
            }
        }

        return null == storageEnvironmentContextReadOnly ?
            new StorageEnvironmentContextReadOnly(
                readOnlyFilter,
                environmentContext
            ) :
            storageEnvironmentContextReadOnly;
    }

    private StorageEnvironmentContextReadOnly(final Predicate<EnvironmentValueName<?>> readOnlyFilter,
                                              final EnvironmentContext context) {
        super();

        this.readOnlyFilter = readOnlyFilter;
        this.context = context;
    }

    // StorageEnvironmentContext........................................................................................

    @Override
    public Optional<StoragePath> currentWorkingDirectory() {
        return this.environmentValue(CURRENT_WORKING_DIRECTORY);
    }

    @Override
    public void setCurrentWorkingDirectory(final Optional<StoragePath> currentWorkingDirectory) {
        CURRENT_WORKING_DIRECTORY.setOrRemoveEnvironmentValue(
            currentWorkingDirectory,
            this
        );
    }

    @Override
    public Optional<StoragePath> homeDirectory() {
        return HOME_DIRECTORY.getEnvironmentValue(this);
    }

    @Override
    public void setHomeDirectory(final Optional<StoragePath> homeDirectory) {
        HOME_DIRECTORY.setOrRemoveEnvironmentValue(
            homeDirectory,
            this
        );
    }

    // EnvironmentContextDelegator......................................................................................

    @Override
    public StorageEnvironmentContext cloneEnvironment() {
        final EnvironmentContext environmentContext = this.context.cloneEnvironment();
        return environmentContext instanceof StorageEnvironmentContext ?
            (StorageEnvironmentContext) environmentContext :
            StorageEnvironmentContexts.basic(environmentContext);
    }

    @Override
    public StorageEnvironmentContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        return this.context == environmentContext ?
            this :
            StorageEnvironmentContextReadOnly.with(
                this.readOnlyFilter,
                environmentContext
            );
    }

    @Override
    public <T> void setEnvironmentValue(final EnvironmentValueName<T> name,
                                        final T value) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(value, "value");

        if (this.readOnlyFilter.test(name) && false == value.equals(this.environmentValue(name).orElse(null))) {
            throw name.readOnlyEnvironmentValueException();
        }

        this.context.setEnvironmentValue(
            name,
            value
        );
    }

    @Override
    public void removeEnvironmentValue(final EnvironmentValueName<?> name) {
        Objects.requireNonNull(name, "name");

        if (this.readOnlyFilter.test(name) && this.environmentValue(name).isPresent()) {
            throw name.readOnlyEnvironmentValueException();
        }

        this.context.removeEnvironmentValue(name);
    }

    @Override
    public EnvironmentContext environmentContext() {
        return this.context;
    }

    private final Predicate<EnvironmentValueName<?>> readOnlyFilter;

    private final EnvironmentContext context;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.readOnlyFilter,
            this.context
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof StorageEnvironmentContextReadOnly &&
                this.equals0((StorageEnvironmentContextReadOnly) other));
    }

    private boolean equals0(final StorageEnvironmentContextReadOnly other) {
        return this.readOnlyFilter.equals(other.readOnlyFilter) &&
            this.context.equals(other.context);
    }

    @Override
    public String toString() {
        return this.context.toString();
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
        printer.indent();
        {
            printer.println("readOnlyFilter");
            printer.indent();
            {
                TreePrintable.printTreeOrToString(
                    this.readOnlyFilter,
                    printer
                );
            }
            printer.outdent();

            printer.println("context");
            printer.indent();
            {
                TreePrintable.printTreeOrToString(
                    this.context,
                    printer
                );
            }
            printer.outdent();
        }
        printer.outdent();
    }
}
