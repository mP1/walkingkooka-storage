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
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.util.Optional;

public final class StorageExpressionFunctionWriteTest extends StorageExpressionFunctionTestCase<StorageExpressionFunctionWrite<TestStorageExpressionEvaluationContext>, Void> {

    private final static StoragePath PATH = StoragePath.parse("/dir1/file2.doesnt.matter");

    private final static ExpressionNumber VALUE = ExpressionNumberKind.DEFAULT.create(123.5);

    @Test
    public void testApply() {
        final Storage<TestStorageExpressionEvaluationContext> storage = Storages.treeMapStore();

        final TestStorageExpressionEvaluationContext context = new TestStorageExpressionEvaluationContext(storage);

        this.applyAndCheck(
            StorageExpressionFunctionWrite.instance(),
            Lists.of(
                PATH,
                VALUE
            ),
            context,
            null
        );

        this.checkEquals(
            Optional.of(VALUE),
            storage.load(
                    PATH,
                    context
                ).map(StorageValue::value)
                .orElse(null)
        );
    }

    @Override
    public StorageExpressionFunctionWrite<TestStorageExpressionEvaluationContext> createBiFunction() {
        return StorageExpressionFunctionWrite.instance();
    }

    @Override
    public TestStorageExpressionEvaluationContext createContext() {
        return new TestStorageExpressionEvaluationContext(Storages.treeMapStore());
    }

    @Override
    public int minimumParameterCount() {
        return 1;
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            StorageExpressionFunctionWrite.instance(),
            "writeStorage"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageExpressionFunctionWrite<TestStorageExpressionEvaluationContext>> type() {
        return Cast.to(StorageExpressionFunctionWrite.class);
    }
}
