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
import walkingkooka.environment.MissingEnvironmentValueException;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.ThrowableTesting;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HasHomeDirectoryTest implements HasHomeDirectoryTesting,
    ClassTesting2<HasHomeDirectory>,
    StorageContextTesting,
    ThrowableTesting {

    // homeDirectoryOrFail..............................................................................................

    @Test
    public void testHomeDirectory() {
        this.checkEquals(
            STORAGE_CONTEXT.homeDirectoryOrFail(),
            HOME_DIRECTORY
        );
    }

    @Test
    public void testHomeDirectoryMissingFails() {
        final MissingEnvironmentValueException thrown = assertThrows(
            MissingEnvironmentValueException.class,
            () -> new HasHomeDirectory() {
                @Override
                public Optional<StoragePath> homeDirectory() {
                    return NO_HOME_DIRECTORY;
                }
            }.homeDirectoryOrFail()
        );
        this.getMessageAndCheck(
            thrown,
            "Missing environment value \"homeDirectory\""
        );
    }

    // class............................................................................................................

    @Override
    public Class<HasHomeDirectory> type() {
        return HasHomeDirectory.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
