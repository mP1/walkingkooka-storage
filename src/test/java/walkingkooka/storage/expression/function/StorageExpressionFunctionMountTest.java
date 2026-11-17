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
import walkingkooka.storage.StorageMountPoint;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageTesting;
import walkingkooka.storage.Storages;
import walkingkooka.storage.expression.function.StorageExpressionFunctionTestCase.TestStorageExpressionEvaluationContext;

public final class StorageExpressionFunctionMountTest extends StorageExpressionFunctionTestCase<StorageExpressionFunctionMount<TestStorageExpressionEvaluationContext>, Void>
    implements StorageTesting {

    private final static StoragePath PATH = StoragePath.parse("/mount1/");

    @Test
    public void testApply() {
        final Storage<TestStorageExpressionEvaluationContext> root = Storages.treeMapStore();
        final Storage<TestStorageExpressionEvaluationContext> storage = Storages.mount(root);

        final TestStorageExpressionEvaluationContext context = this.createContext(storage);

        final Storage<TestStorageExpressionEvaluationContext> mounted = Storages.treeMapStore();

        this.applyAndCheck(
            StorageExpressionFunctionMount.instance(),
            Lists.of(
                PATH,
                mounted
            ),
            context,
            null
        );

        this.mountPointsAndCheck(
            storage,
            StorageMountPoint.with(
                StoragePath.ROOT,
                root
            ),
            StorageMountPoint.with(
                PATH,
                mounted
            )
        );
    }

    @Override
    public StorageExpressionFunctionMount<TestStorageExpressionEvaluationContext> createBiFunction() {
        return StorageExpressionFunctionMount.instance();
    }

    @Override
    public TestStorageExpressionEvaluationContext createContext() {
        return this.createContext(Storages.empty());
    }

    @Override
    public int minimumParameterCount() {
        return 2;
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            StorageExpressionFunctionMount.instance(),
            "mountStorage"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageExpressionFunctionMount<TestStorageExpressionEvaluationContext>> type() {
        return Cast.to(StorageExpressionFunctionMount.class);
    }
}
