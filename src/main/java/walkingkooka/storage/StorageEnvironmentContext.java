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

import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;

import java.util.Optional;

public interface StorageEnvironmentContext extends EnvironmentContext, HasCurrentWorkingDirectory {

    /**
     * Constant that should be used to hold the current working directory or PWD.
     */
    EnvironmentValueName<StoragePath> CURRENT_WORKING_DIRECTORY = EnvironmentValueName.with(
        "currentWorkingDirectory",
        StoragePath.class
    );


    /**
     * Sets or replaces the current working directory.
     */
    void setCurrentWorkingDirectory(final Optional<StoragePath> currentWorkingDirectory);

    // EnvironmentContext...............................................................................................

    @Override
    StorageEnvironmentContext cloneEnvironment();

    @Override
    StorageEnvironmentContext setEnvironmentContext(final EnvironmentContext environmentContext);
}
