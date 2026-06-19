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

import walkingkooka.Binary;
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.io.FileExtension;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StorageValue;
import walkingkooka.tree.expression.Expression;

/**
 * Converts a {@link StorageValue} into {@link StorageBinary} if the file extension is {@link FileExtension#EXPRESSION} and the value can be
 * converted into a {@link Expression} and then text and then into {@link Binary} and then {@link StorageBinary}.
 */
final class StorageConverterToStorageBinaryExpression<C extends StorageConverterContext> extends StorageConverterToStorageBinary<C> {

    /**
     * Type safe getter.
     */
    static <C extends StorageConverterContext> StorageConverterToStorageBinaryExpression<C> instance() {
        return Cast.to(INSTANCE);
    }

    private final static StorageConverterToStorageBinaryExpression INSTANCE = new StorageConverterToStorageBinaryExpression<>();

    private StorageConverterToStorageBinaryExpression() {
        super();
    }

    @Override
    boolean testStorageValue(final StorageValue storageValue,
                             final C context) {
        // file extension is *.txt and value can be converted to text
        return FileExtension.EXPRESSION.equals(
            storageValue.path()
                .fileExtension()
                .orElse(null)
        ) &&
            storageValue.value()
                .map((Object value) -> context.canConvert(value, Expression.class) && context.canConvert(EXPRESSION, String.class) && context.canConvert("", Binary.class))
                .orElse(false);
    }

    private final static Expression EXPRESSION = Expression.value("Hello");

    @Override
    Either<Binary, String> toBinary(final StorageValue storageValue,
                                    final C context) {
        Either<Binary, String> binary;

        final Either<Expression, String> expression = context.convert(
            storageValue.value()
                .orElse(null),
            Expression.class
        );

        if (expression.isRight()) {
            binary = Cast.to(expression);
        } else {
            final Either<String, String> text = context.convert(
                expression.leftValue(),
                String.class
            );

            if (text.isRight()) {
                binary = Cast.to(text);
            } else {
                binary = context.convert(
                    text.leftValue(),
                    Binary.class
                );
            }
        }

        return binary;
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "*." + FileExtension.EXPRESSION + " to " + StorageBinary.class.getSimpleName();
    }
}
