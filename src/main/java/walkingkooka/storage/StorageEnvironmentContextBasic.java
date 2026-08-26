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

final class StorageEnvironmentContextBasic implements StorageEnvironmentContext,
    EnvironmentContextDelegator,
    TreePrintable {

    static StorageEnvironmentContextBasic with(final EnvironmentContext environmentContext) {
        Objects.requireNonNull(environmentContext, "environmentContext");

        return environmentContext instanceof StorageEnvironmentContextBasic ?
            (StorageEnvironmentContextBasic) environmentContext :
            new StorageEnvironmentContextBasic(environmentContext);
    }

    private StorageEnvironmentContextBasic(final EnvironmentContext context) {
        super();

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
        return this.setEnvironmentContext(
            this.context.cloneEnvironment()
        );
    }

    @Override
    public StorageEnvironmentContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        return this.context == environmentContext ?
            this :
            StorageEnvironmentContextBasic.with(
                environmentContext
            );
    }

    @Override
    public <T> void setEnvironmentValue(final EnvironmentValueName<T> name,
                                        final T value) {
        this.context.setEnvironmentValue(
            name,
            value
        );
    }

    @Override
    public void removeEnvironmentValue(final EnvironmentValueName<?> name) {
        this.context.removeEnvironmentValue(name);
    }

    @Override
    public EnvironmentContext environmentContext() {
        return this.context;
    }

    private final EnvironmentContext context;

    // CanParseEnvironmentValueName.....................................................................................

    @Override
    public EnvironmentValueName<?> parseEnvironmentValueName(final String name) {
        Objects.requireNonNull(name, "name");

        // required because wrapped EnvironmentContext may be missing currentWorkingDirectory * homeDirectory
        return EnvironmentValueName.CASE_SENSITIVITY.equals(
            CURRENT_WORKING_DIRECTORY.value(),
            name
        ) ?
            CURRENT_WORKING_DIRECTORY :
            EnvironmentValueName.CASE_SENSITIVITY.equals(
                HOME_DIRECTORY.value(),
                name
            ) ?
                HOME_DIRECTORY :
                this.context.parseEnvironmentValueName(name);
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.context.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof StorageEnvironmentContextBasic &&
                this.equals0((StorageEnvironmentContextBasic) other));
    }

    private boolean equals0(final StorageEnvironmentContextBasic other) {
        return this.context.equals(other.context);
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
            TreePrintable.printTreeOrToString(
                this.context,
                printer
            );
        }
        printer.outdent();
    }
}
