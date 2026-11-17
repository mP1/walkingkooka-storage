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

package walkingkooka.storage.expression;

import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.expression.EnvironmentExpressionEvaluationContext;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageContext;
import walkingkooka.storage.StorageEnvironmentContext;
import walkingkooka.storage.convert.StorageConverterContext;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

/**
 * A {@link ExpressionEvaluationContext} that adds methods that should delegate to an internal {@link Storage}.
 */
public interface StorageExpressionEvaluationContext extends EnvironmentExpressionEvaluationContext,
    StorageContext,
    StorageConverterContext,
    StorageEnvironmentContext {

    // EnvironmentContext...............................................................................................

    @Override
    StorageExpressionEvaluationContext cloneEnvironment();

    @Override
    StorageExpressionEvaluationContext setEnvironmentContext(final EnvironmentContext environmentContext);

    // JsonNodeConverterContext.........................................................................................

    @Override
    StorageExpressionEvaluationContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor jsonNodeMarshallContextObjectPostProcessor);

    @Override
    StorageExpressionEvaluationContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor jsonNodeUnmarshallContextPreProcessor);
}
