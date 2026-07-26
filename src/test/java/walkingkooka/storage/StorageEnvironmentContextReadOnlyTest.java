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
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.ReadOnlyEnvironmentValueException;
import walkingkooka.predicate.Predicates;

import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageEnvironmentContextReadOnlyTest implements StorageEnvironmentContextTesting2<StorageEnvironmentContextReadOnly> {

    private final static Predicate<EnvironmentValueName<?>> READ_ONLY_FILTER = new Predicate<>() {

        @Override
        public boolean test(final EnvironmentValueName<?> name) {
            return StorageEnvironmentContext.HOME_DIRECTORY.equals(name);
        }

        @Override
        public String toString() {
            return "READ_ONLY_FILTER";
        }
    };

    @Test
    public void testWithNullReadOnlyFilterFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageEnvironmentContextReadOnly.with(
                null,
                StorageEnvironmentContexts.fake()
            )
        );
    }

    @Test
    public void testWithNullStorageEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageEnvironmentContextReadOnly.with(
                READ_ONLY_FILTER,
                null
            )
        );
    }

    @Test
    public void testWithStorageEnvironmentContextReadOnlyAndDifferentReadOnlyFilter() {
        final StorageEnvironmentContextReadOnly context = this.createContext();

        assertNotSame(
            context,
            StorageEnvironmentContextReadOnly.with(
                Predicates.never(),
                context
            )
        );
    }

    @Test
    public void testWithStorageEnvironmentContextReadOnlyAndSameReadOnlyFilter() {
        final StorageEnvironmentContextReadOnly context = this.createContext();

        assertSame(
            context,
            StorageEnvironmentContextReadOnly.with(
                READ_ONLY_FILTER,
                context
            )
        );
    }

    @Test
    public void testCurrentWorkingDirectory() {
        this.currentWorkingDirectoryAndCheck(
            this.createContext(),
            CURRENT_WORKING_DIRECTORY
        );
    }

    @Test
    public void testSetCurrentWorkingDirectoryWithDifferent() {
        this.setCurrentWorkingDirectoryAndCheck(
            this.createContext(),
            DIFFERENT_CURRENT_WORKING_DIRECTORY
        );
    }

    @Test
    public void testHomeDirectory() {
        this.homeDirectoryAndCheck(
            this.createContext(),
            HOME_DIRECTORY
        );
    }

    @Test
    public void testSetHomeDirectoryWithDifferent() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> this.createContext()
                .setHomeDirectory(
                    Optional.of(DIFFERENT_HOME_DIRECTORY)
                )
        );
    }

    @Test
    public void testCloneEnvironmentContext() {
        final StorageEnvironmentContextReadOnly context = this.createContext();
        assertNotSame(
            context.cloneEnvironment(),
            context
        );
    }

    @Test
    public void testCloneEnvironmentContextWithReadOnlyEnvironmentContext() {
        final EnvironmentContext notReadOnly = ENVIRONMENT_CONTEXT.cloneEnvironment();

        final StorageEnvironmentContextReadOnly readOnly = StorageEnvironmentContextReadOnly.with(
            READ_ONLY_FILTER,
            notReadOnly
        );
        assertNotSame(
            readOnly.cloneEnvironment(),
            notReadOnly
        );

        this.setCharsetAndCheck(
            notReadOnly,
            DIFFERENT_CHARSET
        );
    }

    @Test
    public void testSetEnvironmentContext() {
        final StorageEnvironmentContextReadOnly context = this.createContext();

        final StorageEnvironmentContext different = context.setEnvironmentContext(DIFFERENT_ENVIRONMENT_CONTEXT);

        assertNotSame(
            different,
            DIFFERENT_ENVIRONMENT_CONTEXT
        );

        this.charsetAndCheck(
            different,
            DIFFERENT_CHARSET
        );

        this.currencyAndCheck(
            different,
            DIFFERENT_CURRENCY
        );

        this.indentationAndCheck(
            different,
            DIFFERENT_INDENTATION
        );

        this.lineEndingAndCheck(
            different,
            DIFFERENT_LINE_ENDING
        );

        this.localeAndCheck(
            different,
            DIFFERENT_LOCALE
        );

        this.userAndCheck(
            different,
            DIFFERENT_USER
        );
    }

    @Override
    public StorageEnvironmentContextReadOnly createContext() {
        final EnvironmentContext context = ENVIRONMENT_CONTEXT.cloneEnvironment();

        StorageEnvironmentContext.CURRENT_WORKING_DIRECTORY.setEnvironmentValue(
            CURRENT_WORKING_DIRECTORY,
            context
        );

        StorageEnvironmentContext.HOME_DIRECTORY.setEnvironmentValue(
            HOME_DIRECTORY,
            context
        );

        return StorageEnvironmentContextReadOnly.with(
            READ_ONLY_FILTER,
            context
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            "{charset=UTF-8, currency=AUD, currentWorkingDirectory=/current1/working2/directory3, homeDirectory=/home/user, indentation=\"  \", lineEnding=\"\\n\", locale=en_AU, timeOffset=Z, user=user123@example.com}"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testPrintTree() {
        this.treePrintAndCheck(
            this.createContext(),
            "StorageEnvironmentContextReadOnly\n" +
                "  readOnlyFilter\n" +
                "    READ_ONLY_FILTER (walkingkooka.storage.StorageEnvironmentContextReadOnlyTest$1)\n" +
                "  context\n" +
                "    EnvironmentContextSharedMap\n" +
                "      charset\n" +
                "        UTF-8 (sun.nio.cs.UTF_8)\n" +
                "      currency\n" +
                "        AUD (java.util.Currency)\n" +
                "      currentWorkingDirectory\n" +
                "        /current1/working2/directory3\n" +
                "      homeDirectory\n" +
                "        /home/user\n" +
                "      indentation\n" +
                "        \"  \" (walkingkooka.text.Indentation)\n" +
                "      lineEnding\n" +
                "        \"\\n\"\n" +
                "      locale\n" +
                "        en_AU (java.util.Locale)\n" +
                "      now\n" +
                "        1999-12-31T12:58:59 (java.time.LocalDateTime)\n" +
                "      timeOffset\n" +
                "        Z (java.time.ZoneOffset)\n" +
                "      user\n" +
                "        user123@example.com (walkingkooka.net.email.EmailAddress)\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageEnvironmentContextReadOnly> type() {
        return StorageEnvironmentContextReadOnly.class;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}
