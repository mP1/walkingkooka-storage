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
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.expression.StorageExpressionEvaluationContext;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;

import java.util.List;

/**
 * Deletes a storage entry.
 */
final class StorageExpressionFunctionDelete<C extends StorageExpressionEvaluationContext> extends StorageExpressionFunction<C, Void> {

    /**
     * Type safe getter.
     */
    static <C extends StorageExpressionEvaluationContext> StorageExpressionFunctionDelete<C> instance() {
        return Cast.to(INSTANCE);
    }

    final static StorageExpressionFunctionDelete<?> INSTANCE = new StorageExpressionFunctionDelete<>();

    private StorageExpressionFunctionDelete() {
        super("deleteStorage");
    }

    @Override
    public List<ExpressionFunctionParameter<?>> parameters(final int count) {
        return PARAMETERS_PATH_REQUIRED;
    }

    @Override
    public Class<Void> returnType() {
        return Void.class;
    }

    @Override
    public Void apply(final List<Object> parameters,
                      final C context) {
        final StoragePath path = PATH_REQUIRED.getOrFail(
            parameters,
            0
        );

        context.deleteStorage(path);

        return null;
    }
}
