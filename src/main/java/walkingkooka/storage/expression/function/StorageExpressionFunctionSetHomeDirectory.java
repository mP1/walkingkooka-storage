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
import walkingkooka.storage.StorageEnvironmentContext;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.expression.StorageExpressionEvaluationContext;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterName;

import java.util.List;
import java.util.Optional;

/**
 * A function that updates the {@link StorageEnvironmentContext#HOME_DIRECTORY} with a new {@link StoragePath}.
 */
final class StorageExpressionFunctionSetHomeDirectory<C extends StorageExpressionEvaluationContext> extends StorageExpressionFunction<C, Void> {

    /**
     * Type-safe getter.
     */
    static <C extends StorageExpressionEvaluationContext> StorageExpressionFunctionSetHomeDirectory<C> instance() {
        return Cast.to(INSTANCE);
    }

    /**
     * Singleton
     */
    private final static StorageExpressionFunctionSetHomeDirectory<?> INSTANCE = new StorageExpressionFunctionSetHomeDirectory<>();

    private StorageExpressionFunctionSetHomeDirectory() {
        super("setHomeDirectory");
    }

    @Override
    public Class<Void> returnType() {
        return Void.class;
    }

    @Override
    public Void apply(final List<Object> values,
                      final C context) {
        context.setHomeDirectory(
            Optional.of(
                HOME_DIRECTORY.getOrFail(
                    values,
                    0
                )
            )
        );
        return null;
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
                throw new IllegalArgumentException("Missing homeDirectory");
        }

        return parameters;
    }

    private final ExpressionFunctionParameter<StoragePath> HOME_DIRECTORY = ExpressionFunctionParameterName.with("homeDirectory")
        .required(StoragePath.class)
        .setKinds(ExpressionFunctionParameterKind.CONVERT_EVALUATE);

    private final List<ExpressionFunctionParameter<?>> PARAMETERS = Lists.of(HOME_DIRECTORY);
}
