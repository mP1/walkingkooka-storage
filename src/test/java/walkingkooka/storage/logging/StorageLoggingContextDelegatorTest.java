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

import walkingkooka.logging.LoggerPath;
import walkingkooka.logging.LoggingLevel;
import walkingkooka.storage.logging.StorageLoggingContextDelegatorTest.TestStorageLoggingContextDelegator;

import java.util.Objects;

public final class StorageLoggingContextDelegatorTest implements StorageLoggingContextTesting2<TestStorageLoggingContextDelegator> {

    @Override
    public TestStorageLoggingContextDelegator createContext() {
        return new TestStorageLoggingContextDelegator();
    }

    @Override
    public Class<TestStorageLoggingContextDelegator> type() {
        return TestStorageLoggingContextDelegator.class;
    }

    @Override
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    final static class TestStorageLoggingContextDelegator implements StorageLoggingContextDelegator {

        @Override
        public void logEnter(final LoggerPath logger) {
            Objects.requireNonNull(logger, "logger");
        }

        @Override
        public void logExit() {
            // nop
        }

        @Override
        public void log(final LoggingLevel loggingLevel,
                        final String message,
                        final Throwable throwable) {
            Objects.requireNonNull(loggingLevel, "loggingLevel");
        }

        @Override
        public boolean isLoggingEnabled(LoggingLevel loggingLevel) {
            Objects.requireNonNull(loggingLevel, "loggingLevel");

            return false;
        }

        @Override
        public StorageLoggingContext storageLoggingContext() {
            return new FakeStorageLoggingContext();
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
