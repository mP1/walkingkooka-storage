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

import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.storage.StorageContextDelegatorTest.TestStorageContextDelegator;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public final class StorageContextDelegatorTest implements StorageContextTesting<TestStorageContextDelegator> {

    @Override
    public TestStorageContextDelegator createContext() {
        return new TestStorageContextDelegator();
    }

    @Override
    public Class<TestStorageContextDelegator> type() {
        return TestStorageContextDelegator.class;
    }

    @Override
    public void testSetEnvironmentContextWithEqualEnvironmentContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetIndentationWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLineEndingWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLocaleWithDifferent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLocaleWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetTimeOffsetWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetUserWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    final static class TestStorageContextDelegator implements StorageContextDelegator {

        @Override
        public Optional<StoragePath> currentWorkingDirectory() {
            throw new UnsupportedOperationException();
        }

        @Override
        public TestStorageContextDelegator cloneEnvironment() {
            throw new UnsupportedOperationException();
        }

        @Override
        public TestStorageContextDelegator setEnvironmentContext(final EnvironmentContext context) {
            Objects.requireNonNull(context, "context");
            throw new UnsupportedOperationException();
        }

        @Override
        public StorageContext storageContext() {
            return StorageContexts.basic(
                EnvironmentContexts.empty(
                    Indentation.SPACES4,
                    LineEnding.NL,
                    Locale.ENGLISH,
                    () -> LocalDateTime.MIN,
                    EnvironmentContext.ANONYMOUS
                )
            );
        }

        // Object.......................................................................................................

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
