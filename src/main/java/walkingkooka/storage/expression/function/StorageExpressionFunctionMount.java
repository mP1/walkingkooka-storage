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
import walkingkooka.storage.StorageMountPoint;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.expression.StorageExpressionEvaluationContext;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterName;

import java.util.List;

/**
 * Mounts the given {@link Storage} at the given {@link StoragePath}.
 */
final class StorageExpressionFunctionMount<C extends StorageExpressionEvaluationContext> extends StorageExpressionFunction<C, Void> {

    /**
     * Type safe getter.
     */
    static <C extends StorageExpressionEvaluationContext> StorageExpressionFunctionMount<C> instance() {
        return Cast.to(INSTANCE);
    }

    final static StorageExpressionFunctionMount<?> INSTANCE = new StorageExpressionFunctionMount<>();

    private StorageExpressionFunctionMount() {
        super("mountStorage");
    }

    private final static Class<Storage<?>> STORAGE_WILDCARD = Cast.to(Storage.class);

    final static ExpressionFunctionParameter<Storage<?>> STORAGE_REQUIRED = ExpressionFunctionParameterName.with("storage")
        .required(STORAGE_WILDCARD)
        .setKinds(ExpressionFunctionParameterKind.EVALUATE_RESOLVE_REFERENCES);

    final static List<ExpressionFunctionParameter<?>> PARAMETERS = ExpressionFunctionParameter.list(
        PATH_REQUIRED,
        STORAGE_REQUIRED
    );

    @Override
    public List<ExpressionFunctionParameter<?>> parameters(final int count) {
        return PARAMETERS;
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

        final Storage<?> storage = STORAGE_REQUIRED.getOrFail(
            parameters,
            1
        );

        context.mountStorage(
            StorageMountPoint.with(
                path,
                storage
            )
        );

        return null;
    }
}
