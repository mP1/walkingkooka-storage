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

import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;
import walkingkooka.UsesToStringBuilder;
import walkingkooka.collect.list.CsvStringList;
import walkingkooka.collect.list.HasCsvStringList;
import walkingkooka.environment.HasUser;
import walkingkooka.logging.HasLoggingLevel;
import walkingkooka.logging.LoggerPath;
import walkingkooka.logging.LoggingLevel;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasText;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.Printer;
import walkingkooka.text.printer.Printers;
import walkingkooka.text.printer.TreePrintable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public final class LoggingMessage implements HasLoggingLevel,
    HasCsvStringList,
    HasText,
    HasUser,
    TreePrintable,
    UsesToStringBuilder {

    public static LoggingMessage with(final Optional<LoggerPath> logger,
                                      final LoggingLevel loggingLevel,
                                      final LocalDateTime timestamp,
                                      final String message,
                                      final Optional<Throwable> throwable,
                                      final Optional<EmailAddress> user) {
        Objects.requireNonNull(logger, "logger");
        Objects.requireNonNull(loggingLevel, "loggingLevel");
        Objects.requireNonNull(timestamp, "timestamp");
        Objects.requireNonNull(throwable, "throwable");
        Objects.requireNonNull(user, "user");

        return new LoggingMessage(
            logger,
            loggingLevel,
            timestamp,
            message,
            throwable,
            user
        );
    }

    private LoggingMessage(final Optional<LoggerPath> logger,
                           final LoggingLevel loggingLevel,
                           final LocalDateTime timestamp,
                           final String message,
                           final Optional<Throwable> throwable,
                           final Optional<EmailAddress> user) {
        super();

        this.logger = logger;
        this.loggingLevel = loggingLevel;
        this.timestamp = timestamp;
        this.message = message;
        this.throwable = throwable;
        this.user = user;
    }

    public Optional<LoggerPath> logger() {
        return this.logger;
    }

    private final Optional<LoggerPath> logger;

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
            this.logger,
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
        return this.logger.equals(other.logger) &&
            this.loggingLevel.equals(other.loggingLevel) &&
            this.timestamp.equals(other.timestamp) &&
            Objects.equals(this.message, other.message) &&
            this.throwable.equals(other.throwable) &&
            this.user.equals(other.user);
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    // UsesToStringBuilder..............................................................................................

    @Override
    public void buildToString(final ToStringBuilder b) {
        b.enable(ToStringBuilderOption.QUOTE)
            .value(this.logger)
            .value(this.loggingLevel)
            .value(this.timestamp)
            .value(this.message)
            .value(this.throwable)
            .value(this.user);
    }

    // HasCsvStringList.................................................................................................

    /**
     * The {@link Throwable} dump will be used as the {@link String} representation.
     */
    @Override
    public CsvStringList csvStringList() {
        return CsvStringList.EMPTY.concat(
            this.logger.map(HasText::text).orElse("")
        ).concat(
            this.loggingLevel.name()
        ).concat(
            this.timestampToIsoDateTimeString()
        ).concat(
            CharSequences.nullToEmpty(this.message)
                .toString()
        ).concat(
            this.throwableToString()
        ).concat(
            this.user.map(HasText::text).orElse("")
        );
    }

    private String throwableToString() {
        String string = "";

        final Throwable throwable = this.throwable.orElse(null);
        if (null != throwable) {
            final StringBuilder builder = new StringBuilder();

            // which LineEnding ???
            try (final Printer printer = Printers.stringBuilder(builder, LineEnding.NL)) {
                printer.printThrowable(throwable);
            }

            string = builder.toString();
        }

        return string;
    }

    // HasText..........................................................................................................

    @Override
    public String text() {
        return this.csvStringList()
            .text();
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());

        printer.indent();
        {
            this.logger.ifPresent(
                (LoggerPath loggerPath) -> {
                    printer.print(loggerPath.toString());
                    printer.print(" ");
                }
            );

            printer.print(this.loggingLevel.toString());
            printer.print(" ");
            printer.println(this.timestampToIsoDateTimeString());

            printer.indent();
            {
                final String message = this.message;
                if (CharSequences.isNotNullOrEmpty(message)) {
                    printer.println(message);
                }

                final Throwable throwable = this.throwable.orElse(null);
                if(null != throwable) {
                    printer.printThrowable(throwable);
                }
            }
            printer.outdent();

            this.user.ifPresent(
                (EmailAddress user) -> printer.println(user.toString())
            );
        }
        printer.outdent();
    }

    private String timestampToIsoDateTimeString() {
        return DateTimeFormatter.ISO_DATE_TIME.format(this.timestamp);
    }
}
