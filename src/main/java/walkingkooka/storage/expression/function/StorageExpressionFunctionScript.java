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
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.expression.StorageExpressionEvaluationContext;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;

import java.util.List;

/**
 * Reads a file containing a script from {@link walkingkooka.storage.Storage} and executes it.
 */
final class StorageExpressionFunctionScript<C extends StorageExpressionEvaluationContext> extends StorageExpressionFunction<C, Object> {

    /**
     * Type safe getter.
     */
    static <C extends StorageExpressionEvaluationContext> StorageExpressionFunctionScript<C> instance() {
        return Cast.to(INSTANCE);
    }

    final static StorageExpressionFunctionScript<?> INSTANCE = new StorageExpressionFunctionScript<>();

    private StorageExpressionFunctionScript() {
        super("script");
    }

    @Override
    public List<ExpressionFunctionParameter<?>> parameters(final int count) {
        return parametersWithOnlyPath(count);
    }

    @Override
    public Class<Object> returnType() {
        return Object.class;
    }

    @Override
    public Object apply(final List<Object> parameters,
                        final C context) {
        final StoragePath path = PATH_REQUIRED.getOrFail(
            parameters,
            0
        );

        final StorageValue storageValue = context.loadStorage(
            path
        ).orElseThrow(
            () -> path.invalidStoragePathException("Missing script")
        );

        final Object value = storageValue.value()
            .orElseThrow(() -> new IllegalArgumentException("Missing script " + path));

        return context.evaluate(
            context.convertOrFail(
                value,
                String.class
            )
        );
    }
}
