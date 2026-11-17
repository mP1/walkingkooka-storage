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

import walkingkooka.Cast;
import walkingkooka.storage.Storage;
import walkingkooka.storage.Storages;
import walkingkooka.storage.expression.StorageExpressionEvaluationContext;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;

import java.util.List;

/**
 * A function that returns a new empty {@link Storages#treeMapStore()}.
 */
final class StorageExpressionFunctionTreeMapStorage<C extends StorageExpressionEvaluationContext> extends StorageExpressionFunction<C, Storage<C>> {

    /**
     * Type-safe getter.
     */
    static <C extends StorageExpressionEvaluationContext> StorageExpressionFunctionTreeMapStorage<C> instance() {
        return Cast.to(INSTANCE);
    }

    /**
     * Singleton
     */
    private final static StorageExpressionFunctionTreeMapStorage<?> INSTANCE = new StorageExpressionFunctionTreeMapStorage<>();

    private StorageExpressionFunctionTreeMapStorage() {
        super("treeMapStorage");
    }

    @Override
    public Class<Storage<C>> returnType() {
        return Cast.to(Storage.class);
    }

    @Override
    public Storage<C> apply(final List<Object> values,
                            final C context) {
        return Storages.treeMapStore();
    }

    @Override
    public List<ExpressionFunctionParameter<?>> parameters(final int count) {
        return NO_PARAMETERS;
    }
}
