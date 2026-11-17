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

import javaemul.internal.annotations.GwtIncompatible;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageContext;
import walkingkooka.storage.Storages;
import walkingkooka.storage.expression.StorageExpressionEvaluationContext;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterName;

import java.nio.file.Path;
import java.util.List;

/**
 * A function that maps a {@link Path} as a {@link Storages#nativeStorage(Path, StorageContext)}.
 */
@GwtIncompatible
final class StorageExpressionFunctionNativeFileStorage<C extends StorageExpressionEvaluationContext> extends StorageExpressionFunction<C, Storage<C>> {

    /**
     * Type-safe getter.
     */
    static <C extends StorageExpressionEvaluationContext> StorageExpressionFunctionNativeFileStorage<C> instance() {
        return Cast.to(INSTANCE);
    }

    /**
     * Singleton
     */
    private final static StorageExpressionFunctionNativeFileStorage<?> INSTANCE = new StorageExpressionFunctionNativeFileStorage<>();

    private StorageExpressionFunctionNativeFileStorage() {
        super("nativeFileStorage");
    }

    @Override
    public Class<Storage<C>> returnType() {
        return Cast.to(Storage.class);
    }

    final static ExpressionFunctionParameter<Path> NIO_PATH = ExpressionFunctionParameterName.with("path")
        .required(Path.class)
        .setKinds(ExpressionFunctionParameterKind.CONVERT_EVALUATE);

    private final static List<ExpressionFunctionParameter<?>> PARAMETERS = Lists.of(NIO_PATH);

    @Override
    public List<ExpressionFunctionParameter<?>> parameters(final int count) {
        return PARAMETERS;
    }

    @Override
    public Storage<C> apply(final List<Object> parameters,
                            final C context) {
        final Path path = NIO_PATH.getOrFail(
            parameters,
            0
        );

        return Storages.nativeStorage(
            path,
            context
        );
    }
}
