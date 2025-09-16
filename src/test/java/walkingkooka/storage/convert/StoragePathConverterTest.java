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

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.ToStringTesting;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterTesting2;
import walkingkooka.convert.Converters;
import walkingkooka.convert.FakeConverterContext;
import walkingkooka.storage.StoragePath;

public final class StoragePathConverterTest implements ConverterTesting2<StoragePathConverter<FakeConverterContext>, FakeConverterContext>,
    ToStringTesting<StoragePathConverter<FakeConverterContext>> {

    @Test
    public void testConvertNullFails() {
        this.convertFails(
            null,
            StoragePath.class
        );
    }

    @Test
    public void testConvertNonStringFails() {
        this.convertFails(
            123,
            StoragePath.class
        );
    }

    @Test
    public void testConvertString() {
        final String text = "/path123/file456.txt";

        this.convertAndCheck(
            text,
            StoragePath.class,
            StoragePath.parse(text)
        );
    }

    @Test
    public void testConvertCharSequence() {
        final String text = "/path123/file456.txt";

        this.convertAndCheck(
            new StringBuilder(text),
            StoragePath.class,
            StoragePath.parse(text)
        );
    }

    @Override
    public StoragePathConverter<FakeConverterContext> createConverter() {
        return StoragePathConverter.instance();
    }

    @Override
    public FakeConverterContext createContext() {
        return new FakeConverterContext() {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> type) {
                return this.converter.canConvert(
                    value,
                    type,
                    this
                );
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return this.converter.convert(
                    value,
                    target,
                    this
                );
            }

            private final Converter<ConverterContext> converter = Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString();
        };
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            StoragePathConverter.instance(),
            "String -> StoragePath"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StoragePathConverter<FakeConverterContext>> type() {
        return Cast.to(StoragePathConverter.class);
    }
}
