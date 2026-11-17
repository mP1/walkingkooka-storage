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
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.Storages;
import walkingkooka.storage.expression.function.StorageExpressionFunctionTestCase.TestStorageExpressionEvaluationContext;

import java.util.Optional;

public final class StorageExpressionFunctionGetHomeDirectoryTest extends StorageExpressionFunctionTestCase<StorageExpressionFunctionGetHomeDirectory<TestStorageExpressionEvaluationContext>, StoragePath> {

    private final static StoragePath HOME = StoragePath.parse("/user/home123");

    @Test
    public void testApplyMissingParameter() {
        final TestStorageExpressionEvaluationContext context = this.createContext(
            Optional.of(HOME)
        );

        this.applyAndCheck(
            StorageExpressionFunctionGetHomeDirectory.instance(),
            Lists.empty(),
            context,
            HOME
        );
    }

    @Test
    public void testApplyMissingParameterAndContextHomeDirectoryEmpty() {
        final TestStorageExpressionEvaluationContext context = this.createContext(
            Optional.empty()
        );

        this.applyAndCheck(
            StorageExpressionFunctionGetHomeDirectory.instance(),
            Lists.empty(),
            context,
            null
        );
    }

    @Test
    public void testApplyWithStoragePath() {
        final TestStorageExpressionEvaluationContext context = this.createContext(
            Optional.empty()
        );

        this.applyAndCheck(
            StorageExpressionFunctionGetHomeDirectory.instance(),
            Lists.of(HOME),
            context,
            HOME
        );
    }

    @Override
    public StorageExpressionFunctionGetHomeDirectory<TestStorageExpressionEvaluationContext> createBiFunction() {
        return StorageExpressionFunctionGetHomeDirectory.instance();
    }

    @Override
    public TestStorageExpressionEvaluationContext createContext() {
        return this.createContext(
            Optional.of(HOME)
        );
    }

    private TestStorageExpressionEvaluationContext createContext(final Optional<StoragePath> path) {
        return new TestStorageExpressionEvaluationContext(Storages.fake()) {

            @Override
            public Optional<StoragePath> homeDirectory() {
                return path;
            }
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
            StorageExpressionFunctionGetHomeDirectory.instance(),
            "getHomeDirectory"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageExpressionFunctionGetHomeDirectory<TestStorageExpressionEvaluationContext>> type() {
        return Cast.to(StorageExpressionFunctionGetHomeDirectory.class);
    }
}
