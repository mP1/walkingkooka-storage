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
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageTesting;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.Storages;
import walkingkooka.storage.expression.StorageExpressionEvaluationContext;
import walkingkooka.storage.expression.function.StorageExpressionFunctionTestCase.TestStorageExpressionEvaluationContext;

import java.util.Optional;

public final class StorageExpressionFunctionTreeMapStorageTest extends StorageExpressionFunctionTestCase<StorageExpressionFunctionTreeMapStorage<TestStorageExpressionEvaluationContext>, Storage<TestStorageExpressionEvaluationContext>>
    implements StorageTesting {

    @Test
    public void testApply() {
        final TestStorageExpressionEvaluationContext context = this.createContext();

        this.applyAndCheck(
            StorageExpressionFunctionTreeMapStorage.instance(),
            Lists.empty(),
            context,
            Storages.treeMapStore()
        );
    }

    @Test
    public void testApplyStorageSave() {
        final TestStorageExpressionEvaluationContext context = this.createContext();

        final Storage<StorageExpressionEvaluationContext> storage = StorageExpressionFunctionTreeMapStorage.instance()
            .apply(
                Lists.empty(),
                context
            );

        StorageValue storageValue = StorageValue.with(
            StoragePath.parse("/value123")
        ).setValue(
            Optional.of("111")
        );

        this.saveAndCheck(
            storage,
            storageValue,
            context,
            storageValue
        );
    }

    @Override
    public StorageExpressionFunctionTreeMapStorage<TestStorageExpressionEvaluationContext> createBiFunction() {
        return StorageExpressionFunctionTreeMapStorage.instance();
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
            StorageExpressionFunctionTreeMapStorage.instance(),
            "treeMapStorage"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageExpressionFunctionTreeMapStorage<TestStorageExpressionEvaluationContext>> type() {
        return Cast.to(StorageExpressionFunctionTreeMapStorage.class);
    }
}
