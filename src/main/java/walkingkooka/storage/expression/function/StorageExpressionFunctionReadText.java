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
import walkingkooka.text.MultiLineText;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;

import java.util.List;

/**
 * Reads a storage entry and converts it to {@link String}, using the {@link StoragePath} to pick a {@link walkingkooka.convert.Converter}.
 */
final class StorageExpressionFunctionReadText<C extends StorageExpressionEvaluationContext> extends StorageExpressionFunction<C, String> {

    /**
     * Type safe getter.
     */
    static <C extends StorageExpressionEvaluationContext> StorageExpressionFunctionReadText<C> instance() {
        return Cast.to(INSTANCE);
    }

    final static StorageExpressionFunctionReadText<?> INSTANCE = new StorageExpressionFunctionReadText<>();

    private StorageExpressionFunctionReadText() {
        super("readStorageText");
    }

    @Override
    public List<ExpressionFunctionParameter<?>> parameters(final int count) {
        return parametersWithOnlyPath(count);
    }

    @Override
    public Class<String> returnType() {
        return String.class;
    }

    @Override
    public String apply(final List<Object> parameters,
                        final C context) {
        final StoragePath path = PATH_OPTIONAL.get(
            parameters,
            0
        ).orElse(
            context.currentWorkingDirectory()
                .orElse(StoragePath.ROOT)
        );

        final StorageValue storageValue = context.loadStorage(
            path
        ).orElseThrow(() -> path.invalidStoragePathException("Unable to read text"));

        Object value = null != storageValue ?
            storageValue.value()
                .orElse(null) :
            null;

        // Convert StoragePath to Class
        // eg:  *.json -> JsonNode
        // then value
        return context.convert(
            value,
            MultiLineText.class
        ).orElseLeftThrow(() -> path.invalidStoragePathException("Reading text failed"))
            .text();
    }
}
