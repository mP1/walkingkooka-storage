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

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageEnvironmentContextBasicTest implements StorageEnvironmentContextTesting2<StorageEnvironmentContextBasic> {

    @Test
    public void testWithNullEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageEnvironmentContextBasic.with(null)
        );
    }

    @Test
    public void testWithStorageEnvironmentContextBasic() {
        final StorageEnvironmentContextBasic context = this.createContext();

        assertSame(
            context,
            StorageEnvironmentContextBasic.with(context)
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
        this.setHomeDirectoryAndCheck(
            this.createContext(),
            DIFFERENT_HOME_DIRECTORY
        );
    }

    @Test
    public void testCloneEnvironmentContext() {
        final StorageEnvironmentContextBasic context = this.createContext();
        assertNotSame(
            context.cloneEnvironment(),
            context
        );
    }

    @Test
    public void testSetEnvironmentContext() {
        final StorageEnvironmentContextBasic context = this.createContext();

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
    public StorageEnvironmentContextBasic createContext() {
        final EnvironmentContext context = ENVIRONMENT_CONTEXT.cloneEnvironment();

        StorageEnvironmentContext.CURRENT_WORKING_DIRECTORY.setEnvironmentValue(
            CURRENT_WORKING_DIRECTORY,
            context
        );

        StorageEnvironmentContext.HOME_DIRECTORY.setEnvironmentValue(
            HOME_DIRECTORY,
            context
        );

        return StorageEnvironmentContextBasic.with(
            context
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            "{charset=UTF-8, currency=AUD, currentWorkingDirectory=/current1/working2/directory3, homeDirectory=/users/user123@example.com, indentation=\"  \", lineEnding=\"\\n\", locale=en_AU, timeOffset=Z, user=user123@example.com}"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testPrintTree() {
        this.treePrintAndCheck(
            this.createContext(),
            "StorageEnvironmentContextBasic\n" +
                "  EnvironmentContextSharedMap\n" +
                "    charset\n" +
                "      UTF-8 (sun.nio.cs.UTF_8)\n" +
                "    currency\n" +
                "      AUD (java.util.Currency)\n" +
                "    currentWorkingDirectory\n" +
                "      /current1/working2/directory3\n" +
                "    homeDirectory\n" +
                "      /users/user123@example.com\n" +
                "    indentation\n" +
                "      \"  \" (walkingkooka.text.Indentation)\n" +
                "    lineEnding\n" +
                "      \"\\n\"\n" +
                "    locale\n" +
                "      en_AU (java.util.Locale)\n" +
                "    now\n" +
                "      1999-12-31T12:58:59 (java.time.LocalDateTime)\n" +
                "    timeOffset\n" +
                "      Z (java.time.ZoneOffset)\n" +
                "    user\n" +
                "      user123@example.com (walkingkooka.net.email.EmailAddress)\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageEnvironmentContextBasic> type() {
        return StorageEnvironmentContextBasic.class;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}
