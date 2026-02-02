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
import walkingkooka.convert.ShortCircuitingConverter;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.storage.StorageValueInfoList;
import walkingkooka.text.LineEnding;

import java.util.stream.Collectors;

/**
 * A {@link walkingkooka.convert.Converter} that converts a {@link StorageValueInfoList} into lines of text, with one
 * per item.
 */
final class StorageConverterStorageValueInfoListToText<C extends StorageConverterContext> extends StorageConverter<C>
    implements ShortCircuitingConverter<C> {

    /**
     * Type safe getter.
     */
    static <C extends StorageConverterContext> StorageConverterStorageValueInfoListToText<C> instance() {
        return Cast.to(INSTANCE);
    }

    private final static StorageConverterStorageValueInfoListToText<?> INSTANCE = new StorageConverterStorageValueInfoListToText<>();


    private StorageConverterStorageValueInfoListToText() {
        super();
    }

    // ShortCircuitingConverter.........................................................................................

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final C context) {
        return value instanceof StorageValueInfoList &&
            context.canConvert(
                "",
                CharSequence.class
            );
    }

    @Override
    public <T> Either<T, String> doConvert(final Object value,
                                           final Class<T> type,
                                           final C context) {
        final LineEnding lineEnding = context.lineEnding();

        return this.successfulConversion(
            ((StorageValueInfoList)value).stream()
            .map(StorageValueInfo::text)
            .collect(
                Collectors.joining(
                    lineEnding, // between
                    "", // prefix
                    lineEnding // suffix
                )
            ),
            type
        );
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "StorageValueInfoList -> Text";
    }
}
