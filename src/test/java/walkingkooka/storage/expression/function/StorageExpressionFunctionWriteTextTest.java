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
import walkingkooka.storage.StorageContextTesting;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.Storages;
import walkingkooka.storage.expression.function.StorageExpressionFunctionTestCase.TestStorageExpressionEvaluationContext;

import java.util.Optional;

public final class StorageExpressionFunctionWriteTextTest extends StorageExpressionFunctionTestCase<StorageExpressionFunctionWriteText<TestStorageExpressionEvaluationContext>, Void>
    implements StorageContextTesting {

    private final static StoragePath PATH = StoragePath.parse("/dir1/file2.txt");

    private final static String TEXT = "Hello World";

    @Test
    public void testApplyStorageEntryPresent() {
        final Storage<TestStorageExpressionEvaluationContext> storage = Storages.treeMapStore();
        final TestStorageExpressionEvaluationContext context = this.createContext(storage);

        this.applyAndCheck(
            StorageExpressionFunctionWriteText.instance(),
            Lists.of(
                PATH,
                TEXT
            ),
            context,
            null
        );

        this.loadStorageAndCheck(
            context,
            PATH,
            StorageValue.with(PATH)
                .setValue(
                    Optional.of(TEXT)
                )
        );
    }

    @Override
    public StorageExpressionFunctionWriteText<TestStorageExpressionEvaluationContext> createBiFunction() {
        return StorageExpressionFunctionWriteText.instance();
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
            StorageExpressionFunctionWriteText.instance(),
            "writeStorageText"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageExpressionFunctionWriteText<TestStorageExpressionEvaluationContext>> type() {
        return Cast.to(StorageExpressionFunctionWriteText.class);
    }
}
