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
import walkingkooka.net.header.MediaType;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StorageValue;
import walkingkooka.tree.expression.Expression;

/**
 * Converts a {@link StorageValue} into {@link StorageBinary} if the file extension is {@link FileExtension#EXPRESSION} and the value can be
 * converted into a {@link Expression} and then text and then into {@link Binary} and then {@link StorageBinary}.
 */
final class StorageConverterStorageValueToStorageBinarySharedExpression<C extends StorageConverterContext> extends StorageConverterStorageValueToStorageBinaryShared<C> {

    /**
     * Type safe getter.
     */
    static <C extends StorageConverterContext> StorageConverterStorageValueToStorageBinarySharedExpression<C> instance() {
        return Cast.to(INSTANCE);
    }

    private final static StorageConverterStorageValueToStorageBinarySharedExpression INSTANCE = new StorageConverterStorageValueToStorageBinarySharedExpression<>();

    private StorageConverterStorageValueToStorageBinarySharedExpression() {
        super();
    }

    @Override
    FileExtension fileExtension() {
        return FileExtension.EXPRESSION;
    }

    @Override
    MediaType contentType() {
        return Expression.MEDIA_TYPE;
    }

    @Override
    boolean testValue(final Object value,
                      final C context) {
        return context.canConvert(value, Expression.class) &&
            context.canConvert(EXPRESSION, String.class);
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
}
