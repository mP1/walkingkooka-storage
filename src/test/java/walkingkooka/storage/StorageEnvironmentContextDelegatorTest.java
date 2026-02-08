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
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.storage.StorageEnvironmentContextDelegatorTest.TestStorageEnvironmentContextDelegator;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public final class StorageEnvironmentContextDelegatorTest implements StorageEnvironmentContextTesting2<TestStorageEnvironmentContextDelegator> {

    @Test
    public void testSetCurrentWorkingDirectory() {
        this.setCurrentWorkingDirectoryAndCheck(
            new TestStorageEnvironmentContextDelegator(),
            StoragePath.parse("/path1/path2")
        );
    }

    @Override
    public void testSetEnvironmentContextWithEqualEnvironmentContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestStorageEnvironmentContextDelegator createContext() {
        return new TestStorageEnvironmentContextDelegator();
    }

    @Override
    public Class<TestStorageEnvironmentContextDelegator> type() {
        return TestStorageEnvironmentContextDelegator.class;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    final static class TestStorageEnvironmentContextDelegator implements StorageEnvironmentContextDelegator {

        @Override
        public StorageEnvironmentContext storageEnvironmentContext() {
            return this.storageEnvironmentContext;
        }

        private final StorageEnvironmentContext storageEnvironmentContext = new TestStorageEnvironmentContext();

        @Override
        public StorageEnvironmentContext cloneEnvironment() {
            throw new UnsupportedOperationException();
        }

        @Override
        public StorageEnvironmentContext setEnvironmentContext(final EnvironmentContext environmentContext) {
            Objects.requireNonNull(environmentContext, "environmentContext");
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return this.storageEnvironmentContext.toString();
        }
    }

    final static class TestStorageEnvironmentContext implements StorageEnvironmentContext, EnvironmentContextDelegator {

        @Override
        public Optional<StoragePath> currentWorkingDirectory() {
            return this.environmentValue(CURRENT_WORKING_DIRECTORY);
        }

        @Override
        public void setCurrentWorkingDirectory(final Optional<StoragePath> currentWorkingDirectory) {
            this.setOrRemoveEnvironmentValue(
                CURRENT_WORKING_DIRECTORY,
                currentWorkingDirectory
            );
        }

        @Override
        public EnvironmentContext environmentContext() {
            return this.environmentContext;
        }

        private final EnvironmentContext environmentContext = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                Indentation.SPACES4,
                LineEnding.NL,
                Locale.ENGLISH,
                () -> LocalDateTime.MIN,
                ANONYMOUS
            )
        );

        @Override
        public StorageEnvironmentContext cloneEnvironment() {
            throw new UnsupportedOperationException();
        }

        @Override
        public StorageEnvironmentContext setEnvironmentContext(final EnvironmentContext environmentContext) {
            throw new UnsupportedOperationException();
        }
    }
}
