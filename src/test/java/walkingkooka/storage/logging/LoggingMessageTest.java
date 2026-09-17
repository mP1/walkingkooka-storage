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
import walkingkooka.reflect.PublicClassTesting;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class LoggingMessageTest implements PublicClassTesting<LoggingMessage>,
    HashCodeEqualsDefinedTesting2<LoggingMessage>,
    ToStringTesting<LoggingMessage>,
    HasNowTesting,
    HasUserTesting,
    HasLoggingLevelTesting {

    private final static String MESSAGE = "message123";

    private final static Optional<Throwable> THROWABLE = Optional.of(
        new RuntimeException("RuntimeExceptionMessage234")
    );

    // with.............................................................................................................

    @Test
    public void testWithNullLoggingLevelFails() {
        assertThrows(
            NullPointerException.class,
            () -> LoggingMessage.with(
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
                LOGGING_LEVEL,
                null,
                MESSAGE,
                THROWABLE,
                OPTIONAL_USER
            )
        );
    }

    @Test
    public void testWithNullMessageFails() {
        assertThrows(
            NullPointerException.class,
            () -> LoggingMessage.with(
                LOGGING_LEVEL,
                NOW,
                null,
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
                LOGGING_LEVEL,
                NOW,
                MESSAGE,
                THROWABLE,
                OPTIONAL_USER
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

    // hashEquals/equals................................................................................................

    @Test
    public void testEqualsDifferentLoggingLevel() {
        this.checkNotEquals(
            LoggingMessage.with(
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
                LOGGING_LEVEL,
                NOW,
                "Different " + MESSAGE,
                THROWABLE,
                OPTIONAL_USER
            )
        );
    }

    @Test
    public void testEqualsDifferentThrowable() {
        this.checkNotEquals(
            LoggingMessage.with(
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
            "NONE 1999-12-31T12:58:59 \"message123\" java.lang.RuntimeException: RuntimeExceptionMessage234 user123@example.com"
        );
    }

    @Test
    public void testToStringWithoutUser() {
        this.toStringAndCheck(
            LoggingMessage.with(
                LOGGING_LEVEL,
                NOW,
                MESSAGE,
                THROWABLE,
                Optional.empty()
            ),
            "NONE 1999-12-31T12:58:59 \"message123\" java.lang.RuntimeException: RuntimeExceptionMessage234"
        );
    }

    // class............................................................................................................

    @Override
    public Class<LoggingMessage> type() {
        return LoggingMessage.class;
    }
}
