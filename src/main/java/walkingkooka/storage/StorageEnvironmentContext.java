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

import walkingkooka.environment.CanParseEnvironmentValueName;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;

import java.util.Objects;
import java.util.Optional;

/**
 * An {@link EnvironmentContext} with additional values, {@link #CURRENT_WORKING_DIRECTORY} and {@link #HOME_DIRECTORY}.
 */
public interface StorageEnvironmentContext extends EnvironmentContext,
    HasUserDirectories {

    /**
     * Constant that should be used to hold the current working directory or PWD.
     */
    EnvironmentValueName<StoragePath> CURRENT_WORKING_DIRECTORY = EnvironmentValueName.registerConstant(
        "currentWorkingDirectory",
        StoragePath.class
    );

    /**
     * Sets or replaces the current working directory.
     */
    void setCurrentWorkingDirectory(final Optional<StoragePath> currentWorkingDirectory);

    /**
     * Constant that should be used to hold the home directory.
     */
    EnvironmentValueName<StoragePath> HOME_DIRECTORY = EnvironmentValueName.registerConstant(
        "homeDirectory",
        StoragePath.class
    );

    /**
     * Sets or replaces the home directory.
     */
    void setHomeDirectory(final Optional<StoragePath> homeDirectory);

    /**
     * A {@link CanParseEnvironmentValueName} that only works for {@link StorageEnvironmentContext}.
     */
    CanParseEnvironmentValueName STORAGE_ENVIRONMENT_CONTEXT_PARSE = (final String name) -> {
        Objects.requireNonNull(name, "name");

        final EnvironmentValueName<?> environmentValueName;

        // assumes Case insensitive
        switch (name.toLowerCase()) {
            case "currentworkingdirectory":
                environmentValueName = CURRENT_WORKING_DIRECTORY;
                break;
            case "homedirectory":
                environmentValueName = HOME_DIRECTORY;
                break;
            default:
                environmentValueName = EnvironmentContext.ENVIRONMENT_CONTEXT_PARSE.parseEnvironmentValueName(name);
        }

        return environmentValueName;
    };

    // EnvironmentContext...............................................................................................

    @Override
    StorageEnvironmentContext cloneEnvironment();

    @Override
    StorageEnvironmentContext setEnvironmentContext(final EnvironmentContext environmentContext);
}
