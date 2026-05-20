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

import walkingkooka.reflect.JavaVisibility;
import walkingkooka.storage.StorageDelegatorTest.TestStorageDelegator;

public final class StorageDelegatorTest implements StorageTesting<TestStorageDelegator, FakeStorageContext> {

    @Override
    public TestStorageDelegator createStorage() {
        return new TestStorageDelegator();
    }

    @Override
    public FakeStorageContext createContext() {
        return StorageContexts.fake();
    }

    // class............................................................................................................

    @Override
    public Class<TestStorageDelegator> type() {
        return TestStorageDelegator.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }

    final static class TestStorageDelegator implements StorageDelegator<FakeStorageContext> {

        @Override
        public Storage<FakeStorageContext> storage() {
            return Storages.treeMapStore();
        }
    }
}
