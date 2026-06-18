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

package walkingkooka.storage.convert;

import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.io.FileExtension;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;
import walkingkooka.tree.expression.Expression;


/**
 * Converts *.expression.txt files after converting the {@link StorageBinary#binary()} to {@link String} and then converting to a
 * {@link Expression} and then to the requested target type.
 */
final class StorageConverterStorageBinaryToExpression<C extends StorageConverterContext> extends StorageConverterStorageBinaryTo<C> {

    /**
     * Type safe getter.
     */
    static <C extends StorageConverterContext> StorageConverterStorageBinaryToExpression<C> instance() {
        return Cast.to(INSTANCE);
    }

    private final static StorageConverterStorageBinaryToExpression INSTANCE = new StorageConverterStorageBinaryToExpression<>();

    private StorageConverterStorageBinaryToExpression() {
        super();
    }

    @Override
    boolean isPathAndType(final StoragePath path,
                          final Class<?> type,
                          final C context) {
        return FileExtension.EXPRESSION.equals(
            path.fileExtension()
                .orElse(null)
        ) && context.canConvert(
            "",
            Expression.class
        ) && context.canConvert(
            EXPRESSION,
            type
        );
    }

    private final static Expression EXPRESSION = Expression.value(1);

    @Override
    <T> Either<T, String> storageBinaryTo(final StorageBinary storageBinary,
                                          final Class<T> type,
                                          final C context) {
        final Either<T, String> result;

        // convert Binary to String
        final Either<String, String> text = context.convert(
            storageBinary,
            String.class
        );
        if (text.isRight()) {
            result = Cast.to(text);
        } else {
            final Either<Expression, String> expression = context.convert(
                text.leftValue(),
                Expression.class
            );
            if (expression.isRight()) {
                result = Cast.to(expression);
            } else {
                // convert String to $type aka Expression
                result = context.convert(
                    text.leftValue(),
                    type
                );
            }
        }

        return result;
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "*." + FileExtension.EXPRESSION + " AND " + StorageBinary.class.getSimpleName() + " to";
    }
}
