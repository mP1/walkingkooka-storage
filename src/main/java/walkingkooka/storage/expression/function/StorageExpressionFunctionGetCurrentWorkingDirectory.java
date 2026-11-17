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
import walkingkooka.collect.list.Lists;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.expression.StorageExpressionEvaluationContext;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterName;

import java.util.List;

/**
 * A function that returns the current working directory {@link StoragePath}.
 */
final class StorageExpressionFunctionGetCurrentWorkingDirectory<C extends StorageExpressionEvaluationContext> extends StorageExpressionFunction<C, StoragePath> {

    /**
     * Type-safe getter.
     */
    static <C extends StorageExpressionEvaluationContext> StorageExpressionFunctionGetCurrentWorkingDirectory<C> instance() {
        return Cast.to(INSTANCE);
    }

    /**
     * Singleton
     */
    private final static StorageExpressionFunctionGetCurrentWorkingDirectory<?> INSTANCE = new StorageExpressionFunctionGetCurrentWorkingDirectory<>();

    private StorageExpressionFunctionGetCurrentWorkingDirectory() {
        super("getCurrentWorkingDirectory");
    }

    @Override
    public Class<StoragePath> returnType() {
        return StoragePath.class;
    }

    @Override
    public StoragePath apply(final List<Object> values,
                             final C context) {
        return CURRENT_WORKING_DIRECTORY.get(
            values,
            0
        ).orElseGet(
            () -> context.currentWorkingDirectory().orElse(null)
        );
    }

    /**
     * Given the count assembles the parameters with the correct parameter names and types.
     */
    @Override
    public List<ExpressionFunctionParameter<?>> parameters(final int count) {
        List<ExpressionFunctionParameter<?>> parameters;

        switch (count) {
            case 0:
            case 1:
                parameters = PARAMETERS;
                break;
            default:
                throw new IllegalArgumentException("Too many parameters");
        }

        return parameters;
    }

    private final ExpressionFunctionParameter<StoragePath> CURRENT_WORKING_DIRECTORY = ExpressionFunctionParameterName.with("currentWorkingDirectory")
        .optional(StoragePath.class)
        .setKinds(ExpressionFunctionParameterKind.CONVERT_EVALUATE);

    private final List<ExpressionFunctionParameter<?>> PARAMETERS = Lists.of(CURRENT_WORKING_DIRECTORY);
}
