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

import walkingkooka.storage.StoragePath;
import walkingkooka.storage.expression.StorageExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionPurityContext;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterCardinality;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterName;

import java.util.List;
import java.util.Optional;

abstract class StorageExpressionFunction<C extends StorageExpressionEvaluationContext, T> implements ExpressionFunction<T, C> {

    StorageExpressionFunction(final String name) {
        super();

        this.name = Optional.of(
            ExpressionFunctionName.with(name)
        );
    }

    @Override
    public final Optional<ExpressionFunctionName> name() {
        return this.name;
    }

    private final Optional<ExpressionFunctionName> name;

    @Override
    public final boolean isPure(final ExpressionPurityContext expressionPurityContext) {
        return false; // storage functions are NEVER pure
    }

    static List<ExpressionFunctionParameter<?>> parametersWithOnlyPath(final int count) {
        return 0 == count ?
            PARAMETERS_PATH_OPTIONAL :
            PARAMETERS_PATH_REQUIRED;
    }

    final static ExpressionFunctionParameter<StoragePath> PATH_REQUIRED = ExpressionFunctionParameterName.with("path")
        .required(StoragePath.class)
        .setKinds(ExpressionFunctionParameterKind.CONVERT_EVALUATE);

    final static ExpressionFunctionParameter<StoragePath> PATH_OPTIONAL = PATH_REQUIRED.setCardinality(ExpressionFunctionParameterCardinality.OPTIONAL);

    final static List<ExpressionFunctionParameter<?>> PARAMETERS_PATH_REQUIRED = ExpressionFunctionParameter.list(
        PATH_REQUIRED
    );

    final static List<ExpressionFunctionParameter<?>> PARAMETERS_PATH_OPTIONAL = ExpressionFunctionParameter.list(
        PATH_OPTIONAL
    );

    // Object...........................................................................................................

    @Override
    public final String toString() {
        return this.name.get()
            .toString();
    }
}
