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

package walkingkooka.storage.expression.function;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.storage.HasHomeDirectoryTesting;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.Storages;
import walkingkooka.storage.expression.function.StorageExpressionFunctionTestCase.TestStorageExpressionEvaluationContext;

import java.util.Optional;

public final class StorageExpressionFunctionSetHomeDirectoryTest extends StorageExpressionFunctionTestCase<StorageExpressionFunctionSetHomeDirectory<TestStorageExpressionEvaluationContext>, Void>
    implements HasHomeDirectoryTesting {

    private final static StoragePath HOME = StoragePath.parse("/dir1/file2.txt");

    @Test
    public void testApply() {
        final TestStorageExpressionEvaluationContext context = this.createContext();

        this.applyAndCheck(
            StorageExpressionFunctionSetHomeDirectory.instance(),
            Lists.of(HOME),
            context,
            null
        );

        this.homeDirectoryAndCheck(
            context,
            HOME
        );
    }

    @Override
    public StorageExpressionFunctionSetHomeDirectory<TestStorageExpressionEvaluationContext> createBiFunction() {
        return StorageExpressionFunctionSetHomeDirectory.instance();
    }

    @Override
    public TestStorageExpressionEvaluationContext createContext() {
        return new TestStorageExpressionEvaluationContext(Storages.fake()) {

            @Override
            public Optional<StoragePath> homeDirectory() {
                return path;
            }

            @Override
            public void setHomeDirectory(final Optional<StoragePath> path) {
                this.path = path;
            }

            private Optional<StoragePath> path = Optional.empty();
        };
    }

    @Override
    public int minimumParameterCount() {
        return 1;
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            StorageExpressionFunctionSetHomeDirectory.instance(),
            "setHomeDirectory"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageExpressionFunctionSetHomeDirectory<TestStorageExpressionEvaluationContext>> type() {
        return Cast.to(StorageExpressionFunctionSetHomeDirectory.class);
    }
}
