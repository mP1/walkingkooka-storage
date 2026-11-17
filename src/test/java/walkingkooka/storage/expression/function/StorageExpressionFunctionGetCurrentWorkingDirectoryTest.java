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

public final class StorageExpressionFunctionGetCurrentWorkingDirectoryTest extends StorageExpressionFunctionTestCase<StorageExpressionFunctionGetCurrentWorkingDirectory<TestStorageExpressionEvaluationContext>, StoragePath> {

    private final static StoragePath CWD = StoragePath.parse("/dir1/file2.txt");

    @Test
    public void testApplyMissingParameter() {
        final TestStorageExpressionEvaluationContext context = this.createContext(
            Optional.of(CWD)
        );

        this.applyAndCheck(
            StorageExpressionFunctionGetCurrentWorkingDirectory.instance(),
            Lists.empty(),
            context,
            CWD
        );
    }

    @Test
    public void testApplyMissingParameterAndContextCurrentWorkingDirectoryEmpty() {
        final TestStorageExpressionEvaluationContext context = this.createContext(
            Optional.empty()
        );

        this.applyAndCheck(
            StorageExpressionFunctionGetCurrentWorkingDirectory.instance(),
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
            StorageExpressionFunctionGetCurrentWorkingDirectory.instance(),
            Lists.of(CWD),
            context,
            CWD
        );
    }

    @Override
    public StorageExpressionFunctionGetCurrentWorkingDirectory<TestStorageExpressionEvaluationContext> createBiFunction() {
        return StorageExpressionFunctionGetCurrentWorkingDirectory.instance();
    }

    @Override
    public TestStorageExpressionEvaluationContext createContext() {
        return this.createContext(
            Optional.of(CWD)
        );
    }

    private TestStorageExpressionEvaluationContext createContext(final Optional<StoragePath> path) {
        return new TestStorageExpressionEvaluationContext(Storages.fake()) {

            @Override
            public Optional<StoragePath> currentWorkingDirectory() {
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
            StorageExpressionFunctionGetCurrentWorkingDirectory.instance(),
            "getCurrentWorkingDirectory"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageExpressionFunctionGetCurrentWorkingDirectory<TestStorageExpressionEvaluationContext>> type() {
        return Cast.to(StorageExpressionFunctionGetCurrentWorkingDirectory.class);
    }
}
