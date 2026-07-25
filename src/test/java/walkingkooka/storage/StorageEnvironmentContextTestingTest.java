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
import walkingkooka.environment.ReadOnlyEnvironmentValueException;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageEnvironmentContextTestingTest implements StorageEnvironmentContextTesting,
    ClassTesting2<StorageEnvironmentContextTesting> {

    @Test
    public void testStorageEnvironmentContextConstantReadOnly() {
        final StorageEnvironmentContext context = STORAGE_ENVIRONMENT_CONTEXT;

        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> context.setCharset(DIFFERENT_CHARSET)
        );

        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> context.setCurrency(DIFFERENT_CURRENCY)
        );

        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> context.setCurrentWorkingDirectory(
                Optional.of(DIFFERENT_CURRENT_WORKING_DIRECTORY)
            )
        );

        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> context.setHomeDirectory(
                Optional.of(DIFFERENT_HOME_DIRECTORY)
            )
        );

        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> context.setIndentation(DIFFERENT_INDENTATION)
        );

        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> context.setLocale(DIFFERENT_LOCALE)
        );

        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> context.setUser(
                Optional.of(DIFFERENT_USER)
            )
        );
    }

    @Test
    public void testStorageEnvironmentContextConstantCloneEnvironment() {
        final StorageEnvironmentContext context = STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment();

        this.setCharsetAndCheck(
            context,
            DIFFERENT_CHARSET
        );

        this.setCurrentWorkingDirectoryAndCheck(
            context,
            DIFFERENT_CURRENT_WORKING_DIRECTORY
        );

        this.setHomeDirectoryAndCheck(
            context,
            DIFFERENT_HOME_DIRECTORY
        );

        this.setIndentationAndCheck(
            context,
            DIFFERENT_INDENTATION
        );

        this.setLineEndingAndCheck(
            context,
            DIFFERENT_LINE_ENDING
        );

        this.setLocaleAndCheck(
            context,
            DIFFERENT_LOCALE
        );

        this.setUserAndCheck(
            context,
            DIFFERENT_USER
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageEnvironmentContextTesting> type() {
        return StorageEnvironmentContextTesting.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
