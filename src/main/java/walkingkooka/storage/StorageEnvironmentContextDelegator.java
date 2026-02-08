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
import walkingkooka.environment.EnvironmentContextDelegator;

import java.util.Optional;

public interface StorageEnvironmentContextDelegator extends StorageEnvironmentContext,
    EnvironmentContextDelegator {

    // StorageEnvironmentContext........................................................................................

    @Override
    default Optional<StoragePath> currentWorkingDirectory() {
        return this.storageEnvironmentContext()
            .currentWorkingDirectory();
    }

    @Override
    default void setCurrentWorkingDirectory(final Optional<StoragePath> currentWorkingDirectory) {
        this.storageEnvironmentContext()
            .setCurrentWorkingDirectory(
                currentWorkingDirectory
            );
    }

    StorageEnvironmentContext storageEnvironmentContext();

    // EnvironmentContextDelegator......................................................................................

    @Override
    default EnvironmentContext environmentContext() {
        return this.storageEnvironmentContext();
    }
}
