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

import java.util.Optional;

/**
 * A getter that may be called to fetch the users home directory.
 */
public interface HasHomeDirectory {

    /**
     * Nice constant holding no home directory.
     */
    Optional<StoragePath> NO_HOME_DIRECTORY = Optional.empty();

    /**
     * Returns the home directory.
     */
    Optional<StoragePath> homeDirectory();
}
