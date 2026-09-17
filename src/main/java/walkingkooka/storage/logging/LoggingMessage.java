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

import walkingkooka.environment.HasUser;
import walkingkooka.logging.HasLoggingLevel;
import walkingkooka.logging.LoggingLevel;
import walkingkooka.net.email.EmailAddress;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public final class LoggingMessage implements HasLoggingLevel,
    HasUser {

    public static LoggingMessage with(final LoggingLevel loggingLevel,
                                      final LocalDateTime timestamp,
                                      final String message,
                                      final Optional<Throwable> throwable,
                                      final Optional<EmailAddress> user) {
        Objects.requireNonNull(loggingLevel, "loggingLevel");
        Objects.requireNonNull(timestamp, "timestamp");
        Objects.requireNonNull(message, "message");
        Objects.requireNonNull(throwable, "throwable");
        Objects.requireNonNull(user, "user");

        return new LoggingMessage(
            loggingLevel,
            timestamp,
            message,
            throwable,
            user
        );
    }

    private LoggingMessage(final LoggingLevel loggingLevel,
                           final LocalDateTime timestamp,
                           final String message,
                           final Optional<Throwable> throwable,
                           final Optional<EmailAddress> user) {
        super();
        this.loggingLevel = loggingLevel;
        this.timestamp = timestamp;
        this.message = message;
        this.throwable = throwable;
        this.user = user;
    }

    @Override
    public LoggingLevel loggingLevel() {
        return this.loggingLevel;
    }

    private final LoggingLevel loggingLevel;

    public LocalDateTime timestamp() {
        return this.timestamp;
    }

    private final LocalDateTime timestamp;

    public String message() {
        return this.message;
    }

    private final String message;

    public Optional<Throwable> throwable() {
        return this.throwable;
    }

    private final Optional<Throwable> throwable;

    @Override
    public Optional<EmailAddress> user() {
        return this.user;
    }

    private final Optional<EmailAddress> user;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.loggingLevel,
            this.timestamp,
            this.message,
            this.throwable,
            this.user
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof LoggingMessage &&
                this.equals0((LoggingMessage) other));
    }

    private boolean equals0(final LoggingMessage other) {
        return this.loggingLevel.equals(other.loggingLevel) &&
            this.timestamp.equals(other.timestamp) &&
            this.message.equals(other.message) &&
            this.throwable.equals(other.throwable) &&
            this.user.equals(other.user);
    }

    @Override
    public String toString() {
        return this.timestamp + " " + this.loggingLevel + " " + this.message + " " + this.throwable + " " + this.user;
    }
}
