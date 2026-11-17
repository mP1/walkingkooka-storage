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
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterName;

import java.util.List;
import java.util.Optional;

/**
 * Writes some text to {@link walkingkooka.storage.Storage}.
 */
final class StorageExpressionFunctionWriteText<C extends StorageExpressionEvaluationContext> extends StorageExpressionFunction<C, Void> {

    /**
     * Type safe getter.
     */
    static <C extends StorageExpressionEvaluationContext> StorageExpressionFunctionWriteText<C> instance() {
        return Cast.to(INSTANCE);
    }

    final static StorageExpressionFunctionWriteText<?> INSTANCE = new StorageExpressionFunctionWriteText<>();

    private StorageExpressionFunctionWriteText() {
        super("writeStorageText");
    }

    @Override
    public List<ExpressionFunctionParameter<?>> parameters(final int count) {
        return PARAMETERS;
    }

    private final static ExpressionFunctionParameter<String> TEXT = ExpressionFunctionParameterName.with("text")
        .required(String.class)
        .setKinds(ExpressionFunctionParameterKind.CONVERT_EVALUATE_RESOLVE_REFERENCES);

    final static List<ExpressionFunctionParameter<?>> PARAMETERS = ExpressionFunctionParameter.list(
        PATH_REQUIRED,
        TEXT
    );

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

        final String text = TEXT.getOrFail(
            parameters,
            1
        );

        context.saveStorage(
            StorageValue.with(path)
                .setValue(
                    Optional.of(text)
                )
        );

        return null;
    }
}
