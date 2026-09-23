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

package walkingkooka.storage.logging;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.datetime.HasNowTesting;
import walkingkooka.environment.HasUserTesting;
import walkingkooka.logging.HasLoggingLevelTesting;
import walkingkooka.logging.LoggerPath;
import walkingkooka.reflect.PublicClassTesting;
import walkingkooka.text.HasTextTesting;

import java.io.PrintWriter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class LoggingMessageTest implements PublicClassTesting<LoggingMessage>,
    HashCodeEqualsDefinedTesting2<LoggingMessage>,
    ToStringTesting<LoggingMessage>,
    HasNowTesting,
    HasTextTesting,
    HasUserTesting,
    HasLoggingLevelTesting {

    private final static Optional<LoggerPath> LOGGER = Optional.of(
        LoggerPath.parse("Logger123")
    );

    private final static String MESSAGE = "message123";

    private final static Optional<Throwable> THROWABLE = Optional.of(
        new RuntimeException("RuntimeExceptionMessage234") {
            @Override
            public void printStackTrace(final PrintWriter printWriter) {
                printWriter.println(this.getMessage());
                printWriter.println("  stack trace...");
                printWriter.flush();
            }
        }
    );

    // with.............................................................................................................

    @Test
    public void testWithNullLoggerFails() {
        assertThrows(
            NullPointerException.class,
            () -> LoggingMessage.with(
                null,
                LOGGING_LEVEL,
                NOW,
                MESSAGE,
                THROWABLE,
                OPTIONAL_USER
            )
        );
    }

    @Test
    public void testWithNullLoggingLevelFails() {
        assertThrows(
            NullPointerException.class,
            () -> LoggingMessage.with(
                LOGGER,
                null,
                NOW,
                MESSAGE,
                THROWABLE,
                OPTIONAL_USER
            )
        );
    }

    @Test
    public void testWithNullTimestampFails() {
        assertThrows(
            NullPointerException.class,
            () -> LoggingMessage.with(
                LOGGER,
                LOGGING_LEVEL,
                null,
                MESSAGE,
                THROWABLE,
                OPTIONAL_USER
            )
        );
    }

    @Test
    public void testWithNullThrowableFails() {
        assertThrows(
            NullPointerException.class,
            () -> LoggingMessage.with(
                LOGGER,
                LOGGING_LEVEL,
                NOW,
                MESSAGE,
                null,
                OPTIONAL_USER
            )
        );
    }

    @Test
    public void testWithNullUserFails() {
        assertThrows(
            NullPointerException.class,
            () -> LoggingMessage.with(
                LOGGER,
                LOGGING_LEVEL,
                NOW,
                MESSAGE,
                THROWABLE,
                null
            )
        );
    }

    @Test
    public void testWith() {
        final LoggingMessage loggingMessage = LoggingMessage.with(
            LOGGER,
            LOGGING_LEVEL,
            NOW,
            MESSAGE,
            THROWABLE,
            OPTIONAL_USER
        );

        this.checkEquals(
            LOGGER,
            loggingMessage.logger(),
            "logger"
        );

        this.loggingLevelAndCheck(
            loggingMessage,
            LOGGING_LEVEL
        );

        this.checkEquals(
            NOW,
            loggingMessage.timestamp(),
            "timestamp"
        );

        this.checkEquals(
            MESSAGE,
            loggingMessage.message(),
            "message"
        );

        this.checkEquals(
            THROWABLE,
            loggingMessage.throwable(),
            "throwable"
        );

        this.userAndCheck(
            loggingMessage,
            USER
        );
    }

    @Test
    public void testWithNullMessage() {
        final LoggingMessage loggingMessage = LoggingMessage.with(
            LOGGER,
            LOGGING_LEVEL,
            NOW,
            null,
            THROWABLE,
            OPTIONAL_USER
        );

        this.checkEquals(
            LOGGER,
            loggingMessage.logger(),
            "logger"
        );

        this.loggingLevelAndCheck(
            loggingMessage,
            LOGGING_LEVEL
        );

        this.checkEquals(
            NOW,
            loggingMessage.timestamp(),
            "timestamp"
        );

        this.checkEquals(
            null,
            loggingMessage.message(),
            "message"
        );

        this.checkEquals(
            THROWABLE,
            loggingMessage.throwable(),
            "throwable"
        );

        this.userAndCheck(
            loggingMessage,
            USER
        );
    }

    // hashEquals/equals................................................................................................

    @Test
    public void testEqualsDifferentLogger() {
        this.checkNotEquals(
            LoggingMessage.with(
                Optional.empty(),
                LOGGING_LEVEL,
                NOW,
                MESSAGE,
                THROWABLE,
                OPTIONAL_USER
            )
        );
    }

    @Test
    public void testEqualsDifferentLoggingLevel() {
        this.checkNotEquals(
            LoggingMessage.with(
                LOGGER,
                DIFFERENT_LOGGING_LEVEL,
                NOW,
                MESSAGE,
                THROWABLE,
                OPTIONAL_USER
            )
        );
    }

    @Test
    public void testEqualsDifferentTimestamp() {
        this.checkNotEquals(
            LoggingMessage.with(
                LOGGER,
                LOGGING_LEVEL,
                DIFFERENT_NOW,
                MESSAGE,
                THROWABLE,
                OPTIONAL_USER
            )
        );
    }

    @Test
    public void testEqualsDifferentMessage() {
        this.checkNotEquals(
            LoggingMessage.with(
                LOGGER,
                LOGGING_LEVEL,
                NOW,
                "Different " + MESSAGE,
                THROWABLE,
                OPTIONAL_USER
            )
        );
    }

    @Test
    public void testEqualsDifferentNullMessage() {
        this.checkNotEquals(
            LoggingMessage.with(
                LOGGER,
                LOGGING_LEVEL,
                NOW,
                null, // message
                THROWABLE,
                OPTIONAL_USER
            )
        );
    }

    @Test
    public void testEqualsDifferentThrowable() {
        this.checkNotEquals(
            LoggingMessage.with(
                LOGGER,
                LOGGING_LEVEL,
                NOW,
                MESSAGE,
                Optional.of(
                    new RuntimeException("Different")
                ),
                OPTIONAL_USER
            )
        );
    }

    @Override
    public LoggingMessage createObject() {
        return LoggingMessage.with(
            LOGGER,
            LOGGING_LEVEL,
            NOW,
            MESSAGE,
            THROWABLE,
            OPTIONAL_USER
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createObject(),
            "Logger123 NONE 1999-12-31T12:58:59 \"message123\" walkingkooka.storage.logging.LoggingMessageTest$1: RuntimeExceptionMessage234 user123@example.com"
        );
    }

    @Test
    public void testToStringWithoutLogger() {
        this.toStringAndCheck(
            LoggingMessage.with(
                Optional.empty(),
                LOGGING_LEVEL,
                NOW,
                MESSAGE,
                THROWABLE,
                Optional.empty()
            ),
            "NONE 1999-12-31T12:58:59 \"message123\" walkingkooka.storage.logging.LoggingMessageTest$1: RuntimeExceptionMessage234"
        );
    }

    @Test
    public void testToStringWithoutUser() {
        this.toStringAndCheck(
            LoggingMessage.with(
                LOGGER,
                LOGGING_LEVEL,
                NOW,
                MESSAGE,
                THROWABLE,
                Optional.empty()
            ),
            "Logger123 NONE 1999-12-31T12:58:59 \"message123\" walkingkooka.storage.logging.LoggingMessageTest$1: RuntimeExceptionMessage234"
        );
    }

    // HasText..........................................................................................................

    @Test
    public void testText() {
        this.textAndCheck(
            this.createObject(),
            "Logger123 NONE 1999-12-31T12:58:59 \"message123\" walkingkooka.storage.logging.LoggingMessageTest$1: RuntimeExceptionMessage234 user123@example.com"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testPrintTree() {
        this.treePrintAndCheck(
            this.createObject(),
            "LoggingMessage\n" +
                "  Logger123 NONE 1999-12-31T12:58:59\n" +
                "    message123\n" +
                "    RuntimeExceptionMessage234\n" +
                "      stack trace...\n" +
                "  user123@example.com\n"
        );
    }

    @Test
    public void testPrintTreeEmptyMessage() {
        this.treePrintAndCheck(
            LoggingMessage.with(
                LOGGER,
                LOGGING_LEVEL,
                NOW,
                "",
                THROWABLE,
                OPTIONAL_USER
            ),
            "LoggingMessage\n" +
                "  Logger123 NONE 1999-12-31T12:58:59\n" +
                "    RuntimeExceptionMessage234\n" +
                "      stack trace...\n" +
                "  user123@example.com\n"
        );
    }

    @Test
    public void testPrintTreeWithoutThrowable() {
        this.treePrintAndCheck(
            LoggingMessage.with(
                LOGGER,
                LOGGING_LEVEL,
                NOW,
                MESSAGE,
                Optional.empty(),
                OPTIONAL_USER
            ),
            "LoggingMessage\n" +
                "  Logger123 NONE 1999-12-31T12:58:59\n" +
                "    message123\n" +
                "  user123@example.com\n"
        );
    }

    @Test
    public void testPrintTreeWithoutLogger() {
        this.treePrintAndCheck(
            LoggingMessage.with(
                Optional.empty(),
                LOGGING_LEVEL,
                NOW,
                MESSAGE,
                THROWABLE,
                OPTIONAL_USER
            ),
            "LoggingMessage\n" +
                "  NONE 1999-12-31T12:58:59\n" +
                "    message123\n" +
                "    RuntimeExceptionMessage234\n" +
                "      stack trace...\n" +
                "  user123@example.com\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<LoggingMessage> type() {
        return LoggingMessage.class;
    }
}
