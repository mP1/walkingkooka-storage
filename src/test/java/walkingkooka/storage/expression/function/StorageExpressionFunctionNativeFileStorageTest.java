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
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageTesting;
import walkingkooka.storage.Storages;
import walkingkooka.storage.expression.function.StorageExpressionFunctionTestCase.TestStorageExpressionEvaluationContext;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class StorageExpressionFunctionNativeFileStorageTest extends StorageExpressionFunctionTestCase<StorageExpressionFunctionNativeFileStorage<TestStorageExpressionEvaluationContext>, Storage<TestStorageExpressionEvaluationContext>>
    implements StorageTesting {

    @Test
    public void testApply() {
        final TestStorageExpressionEvaluationContext context = this.createContext();

        final Path path = Paths.get(".");

        this.applyAndCheck(
            StorageExpressionFunctionNativeFileStorage.instance(),
            Lists.of(
                path
            ),
            context,
            Storages.nativeStorage(
                path,
                context
            )
        );
    }

    @Override
    public StorageExpressionFunctionNativeFileStorage<TestStorageExpressionEvaluationContext> createBiFunction() {
        return StorageExpressionFunctionNativeFileStorage.instance();
    }

    @Override
    public TestStorageExpressionEvaluationContext createContext() {
        return new TestStorageExpressionEvaluationContext(Storages.fake());
    }

    @Override
    public int minimumParameterCount() {
        return 1;
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            StorageExpressionFunctionNativeFileStorage.instance(),
            "nativeFileStorage"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageExpressionFunctionNativeFileStorage<TestStorageExpressionEvaluationContext>> type() {
        return Cast.to(StorageExpressionFunctionNativeFileStorage.class);
    }
}
