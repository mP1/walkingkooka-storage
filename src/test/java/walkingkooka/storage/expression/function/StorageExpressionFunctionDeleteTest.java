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
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.Storages;
import walkingkooka.storage.expression.function.StorageExpressionFunctionTestCase.TestStorageExpressionEvaluationContext;

import java.util.Optional;

public final class StorageExpressionFunctionDeleteTest extends StorageExpressionFunctionTestCase<StorageExpressionFunctionDelete<TestStorageExpressionEvaluationContext>, Void> {

    private final static StoragePath PATH = StoragePath.parse("/dir1/file2.txt");

    @Test
    public void testApplyStorageDeleted() {
        final Storage<TestStorageExpressionEvaluationContext> storage = Storages.treeMapStore();
        final TestStorageExpressionEvaluationContext context = this.createContext(storage);

        storage.save(
            StorageValue.with(PATH)
                .setValue(
                    Optional.of("value456")
                ),
            context
        );

        this.applyAndCheck(
            StorageExpressionFunctionDelete.instance(),
            Lists.of(PATH),
            context,
            null
        );

        this.checkEquals(
            Optional.empty(),
            storage.load(
                PATH,
                context
            )
        );
    }

    @Test
    public void testApplyStorageMissing() {
        final Storage<TestStorageExpressionEvaluationContext> storage = Storages.treeMapStore();
        final TestStorageExpressionEvaluationContext context = this.createContext(storage);

        this.applyAndCheck(
            StorageExpressionFunctionDelete.instance(),
            Lists.of(PATH),
            context,
            null
        );
    }

    @Override
    public StorageExpressionFunctionDelete<TestStorageExpressionEvaluationContext> createBiFunction() {
        return StorageExpressionFunctionDelete.instance();
    }

    @Override
    public TestStorageExpressionEvaluationContext createContext() {
        return this.createContext(Storages.empty());
    }

    @Override
    public int minimumParameterCount() {
        return 1;
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            StorageExpressionFunctionDelete.instance(),
            "deleteStorage"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageExpressionFunctionDelete<TestStorageExpressionEvaluationContext>> type() {
        return Cast.to(StorageExpressionFunctionDelete.class);
    }
}
