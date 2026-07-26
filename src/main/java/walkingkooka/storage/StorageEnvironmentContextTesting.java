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
import walkingkooka.environment.EnvironmentContextTesting;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.predicate.Predicates;

import java.util.Optional;

public interface StorageEnvironmentContextTesting extends EnvironmentContextTesting,
    HasCurrentWorkingDirectoryTesting,
    HasUserDirectoriesTesting {

    StorageEnvironmentContext STORAGE_ENVIRONMENT_CONTEXT = storageEnvironmentContext();

    private static StorageEnvironmentContext storageEnvironmentContext() {
        final EnvironmentContext context = ENVIRONMENT_CONTEXT.cloneEnvironment();

        StorageEnvironmentContext.CURRENT_WORKING_DIRECTORY.setEnvironmentValue(
            CURRENT_WORKING_DIRECTORY,
            context
        );

        StorageEnvironmentContext.HOME_DIRECTORY.setEnvironmentValue(
            HOME_DIRECTORY,
            context
        );

        return StorageEnvironmentContexts.basic(
            EnvironmentContexts.readOnly(
                Predicates.always(),
                context
            )
        );
    }

    StorageEnvironmentContext DIFFERENT_STORAGE_ENVIRONMENT_CONTEXT = differentStorageEnvironmentContext();

    private static StorageEnvironmentContext differentStorageEnvironmentContext() {
        final EnvironmentContext context = DIFFERENT_ENVIRONMENT_CONTEXT.cloneEnvironment();

        StorageEnvironmentContext.CURRENT_WORKING_DIRECTORY.setEnvironmentValue(
            DIFFERENT_CURRENT_WORKING_DIRECTORY,
            context
        );

        StorageEnvironmentContext.HOME_DIRECTORY.setEnvironmentValue(
            DIFFERENT_HOME_DIRECTORY,
            context
        );

        return StorageEnvironmentContexts.basic(
            EnvironmentContexts.readOnly(
                Predicates.always(),
                context
            )
        );
    }

    // setCurrentWorkingDirectory.......................................................................................

    default void setCurrentWorkingDirectoryAndCheck(final StorageEnvironmentContext context) {
        this.setCurrentWorkingDirectoryAndCheck(
            context,
            Optional.empty()
        );
    }

    default void setCurrentWorkingDirectoryAndCheck(final StorageEnvironmentContext context,
                                                    final StoragePath currentWorkingDirectory) {
        this.setCurrentWorkingDirectoryAndCheck(
            context,
            Optional.of(currentWorkingDirectory)
        );
    }

    default void setCurrentWorkingDirectoryAndCheck(final StorageEnvironmentContext context,
                                                    final Optional<StoragePath> currentWorkingDirectory) {
        context.setCurrentWorkingDirectory(currentWorkingDirectory);

        this.currentWorkingDirectoryAndCheck(
            context,
            currentWorkingDirectory
        );
    }

    // setHomeDirectory.................................................................................................

    default void setHomeDirectoryAndCheck(final StorageEnvironmentContext context) {
        this.setHomeDirectoryAndCheck(
            context,
            Optional.empty()
        );
    }

    default void setHomeDirectoryAndCheck(final StorageEnvironmentContext context,
                                          final StoragePath homeDirectory) {
        this.setHomeDirectoryAndCheck(
            context,
            Optional.of(homeDirectory)
        );
    }

    default void setHomeDirectoryAndCheck(final StorageEnvironmentContext context,
                                          final Optional<StoragePath> homeDirectory) {
        context.setHomeDirectory(homeDirectory);

        this.homeDirectoryAndCheck(
            context,
            homeDirectory
        );
    }
}
