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
import walkingkooka.storage.StorageMountPoint;
import walkingkooka.storage.expression.StorageExpressionEvaluationContext;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;

import java.util.List;

/**
 * A function that returns all available {@link StorageMountPoint}.
 */
final class StorageExpressionFunctionMountPoints<C extends StorageExpressionEvaluationContext> extends StorageExpressionFunction<C, List<StorageMountPoint<C>>> {

    /**
     * Type-safe getter.
     */
    static <C extends StorageExpressionEvaluationContext> StorageExpressionFunctionMountPoints<C> instance() {
        return Cast.to(INSTANCE);
    }

    /**
     * Singleton
     */
    private final static StorageExpressionFunctionMountPoints<?> INSTANCE = new StorageExpressionFunctionMountPoints<>();

    private StorageExpressionFunctionMountPoints() {
        super("mountPoints");
    }

    private final Class<List<StorageMountPoint<?>>> LIST_STORAGE_MOUNT_POINTS = Cast.to(Lists.class);

    @Override
    public Class<List<StorageMountPoint<C>>> returnType() {
        return Cast.to(LIST_STORAGE_MOUNT_POINTS);
    }

    @Override
    public List<StorageMountPoint<C>> apply(final List<Object> values,
                                            final C context) {
        return Cast.to(
            context.storageMountPoints()
        );
    }

    @Override
    public List<ExpressionFunctionParameter<?>> parameters(final int count) {
        return NO_PARAMETERS;
    }
}
