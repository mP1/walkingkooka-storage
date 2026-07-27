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

import walkingkooka.convert.ConverterContexts;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.storage.StorageContextDelegatorTest.TestStorageContextDelegator;

import java.util.Objects;

public final class StorageContextDelegatorTest implements StorageContextTesting2<TestStorageContextDelegator> {

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
    public void testSetCurrencyWithDifferentAndWatcher() {
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
                ConverterContexts.fake(), // ConverterLike
                MEDIA_TYPE_DETECTOR,
                ENVIRONMENT_CONTEXT.cloneEnvironment()
            );
        }

        // Object.......................................................................................................

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
