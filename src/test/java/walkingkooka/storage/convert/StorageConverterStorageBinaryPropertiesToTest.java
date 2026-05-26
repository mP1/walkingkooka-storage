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
import walkingkooka.Binary;
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.props.Properties;
import walkingkooka.props.PropertiesPath;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormatSymbols;
import java.util.Locale;

public final class StorageConverterStorageBinaryPropertiesToTest extends StorageConverterStorageBinaryTestCase<StorageConverterStorageBinaryPropertiesTo<FakeStorageConverterContext>> {

    private final static Charset CHARSET = StandardCharsets.UTF_8;

    @Test
    public void testConvertStorageBinaryTxtToDateTimeSymbols() {
        final DateTimeSymbols dateTimeSymbols = DateTimeSymbols.fromDateFormatSymbols(
            new DateFormatSymbols(
                Locale.ENGLISH
            )
        );

        this.convertAndCheck(
            StorageBinary.with(
                StoragePath.parse("/dateTimeSymbols.properties"),
                Binary.with(
                    dateTimeSymbols.properties()
                        .text()
                        .getBytes(CHARSET)
                )
            ),
            dateTimeSymbols
        );
    }

    @Test
    public void testConvertStorageBinaryTxtToProperties() {
        final Properties properties = Properties.EMPTY.set(
            PropertiesPath.parse("hello.world.123"),
            "Hello World"
        ).set(
            PropertiesPath.parse("country"),
            "Australia"
        );

        this.convertAndCheck(
            StorageBinary.with(
                StoragePath.parse("/file.properties"),
                Binary.with(
                    properties.text()
                        .getBytes(CHARSET)
                )
            ),
            properties
        );
    }

    @Override
    public StorageConverterStorageBinaryPropertiesTo<FakeStorageConverterContext> createConverter() {
        return StorageConverterStorageBinaryPropertiesTo.instance();
    }

    @Override
    public FakeStorageConverterContext createContext() {
        return new FakeStorageConverterContext() {

            @Override
            public Charset charset() {
                return CHARSET;
            }

            @Override
            public char valueSeparator() {
                return ',';
            }

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

            private final Converter<ConverterContext> converter = Converters.collection(
                Lists.of(
                    Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString(),
                    Converters.hasBinaryToString(),
                    Converters.textToProperties(),
                    Converters.propertiesToDateTimeSymbols()
                )
            );
        };
    }

    @Override
    public Class<StorageConverterStorageBinaryPropertiesTo<FakeStorageConverterContext>> type() {
        return Cast.to(StorageConverterStorageBinaryPropertiesTo.class);
    }
}
