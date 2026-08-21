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

import org.junit.jupiter.api.Test;
import walkingkooka.environment.EnvironmentContextTesting2;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface StorageEnvironmentContextTesting2<C extends StorageEnvironmentContext> extends StorageEnvironmentContextTesting,
    EnvironmentContextTesting2<C> {

    @Test
    default void testParseEnvironmentValueNameWithCurrentWorkingDirectory() {
        this.parseEnvironmentValueNameAndCheck(StorageEnvironmentContext.CURRENT_WORKING_DIRECTORY);
    }

    @Test
    default void testParseEnvironmentValueNameWithHomeDirectory() {
        this.parseEnvironmentValueNameAndCheck(StorageEnvironmentContext.HOME_DIRECTORY);
    }

    // currentWorkingDirectory..........................................................................................

    @Override
    default void currentWorkingDirectoryAndCheck(final HasCurrentWorkingDirectory has,
                                                 final Optional<StoragePath> expected) {
        StorageEnvironmentContextTesting.super.currentWorkingDirectoryAndCheck(
            has,
            expected
        );

        if (has instanceof StorageEnvironmentContext) {
            this.environmentValueAndCheck(
                (StorageEnvironmentContext) has,
                StorageEnvironmentContext.CURRENT_WORKING_DIRECTORY,
                expected
            );
        }
    }

    // setCurrentWorkingDirectory.......................................................................................

    @Test
    default void testSetCurrentWorkingDirectoryWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .setCurrentWorkingDirectory(null)
        );
    }

    // homeDirectory....................................................................................................

    @Override
    default void homeDirectoryAndCheck(final HasHomeDirectory has,
                                       final Optional<StoragePath> expected) {
        StorageEnvironmentContextTesting.super.homeDirectoryAndCheck(
            has,
            expected
        );

        if (has instanceof StorageEnvironmentContext) {
            this.environmentValueAndCheck(
                (StorageEnvironmentContext) has,
                StorageEnvironmentContext.HOME_DIRECTORY,
                expected
            );
        }
    }

    // setHomeDirectory.................................................................................................

    @Test
    default void testSetHomeDirectoryWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .setHomeDirectory(null)
        );
    }
    
    // class............................................................................................................

    @Override
    default String typeNameSuffix() {
        return StorageEnvironmentContext.class.getSimpleName();
    }
}
