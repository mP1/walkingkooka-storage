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
import walkingkooka.net.header.MediaType;
import walkingkooka.storage.StorageBinary;
import walkingkooka.tree.expression.Expression;


/**
 * Converts {@link FileExtension#EXPRESSION} files to {@link Expression} by converting the {@link StorageBinary} to
 * {@link String} then to {@link walkingkooka.tree.expression.Expression}.
 */
final class StorageConverterStorageBinaryToStorageValueSharedExpression<C extends StorageConverterContext> extends StorageConverterStorageBinaryToStorageValueShared<C> {

    /**
     * Type safe getter.
     */
    static <C extends StorageConverterContext> StorageConverterStorageBinaryToStorageValueSharedExpression<C> instance() {
        return Cast.to(INSTANCE);
    }

    /**
     * Singleton
     */
    private final static StorageConverterStorageBinaryToStorageValueSharedExpression INSTANCE = new StorageConverterStorageBinaryToStorageValueSharedExpression<>();

    private StorageConverterStorageBinaryToStorageValueSharedExpression() {
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
    <T> Either<T, String> storageBinaryToStorageValue(final StorageBinary storageBinary,
                                                      final Class<T> type,
                                                      final C context) {
        final Either<T, String> storageValue;

        // convert Binary to String
        final Either<String, String> text = context.convert(
            storageBinary,
            String.class
        );
        if (text.isRight()) {
            storageValue = Cast.to(text);
        } else {
            // parse String holding expression into an Expression etc
            final Either<Expression, String> expression = context.convert(
                text.leftValue(),
                Expression.class
            );
            if (expression.isRight()) {
                storageValue = Cast.to(text);
            } else {
                storageValue = this.successfulConversion(
                    storageBinary.path(),
                    type,
                    expression.leftValue()
                );
            }
        }

        return storageValue;
    }
}
