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
import walkingkooka.HasCharsetTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.HasDateTimeSymbolsTesting;
import walkingkooka.net.header.MediaType;
import walkingkooka.props.Properties;
import walkingkooka.props.PropertiesPath;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;

import java.nio.charset.Charset;
import java.util.Optional;

public final class StorageConverterStorageBinaryToStorageValueSharedPropertiesTest extends StorageConverterStorageBinaryToStorageValueSharedTestCase<StorageConverterStorageBinaryToStorageValueSharedProperties<FakeStorageConverterContext>>
    implements HasCharsetTesting,
    HasDateTimeSymbolsTesting {

    @Test
    public void testConvertStorageBinaryDateTimeSymbolsPropertiesToStorageValue() {
        final Properties dateTimeSymbols = DATE_TIME_SYMBOLS.properties();

        final StoragePath storagePath = StoragePath.parse("/dateTimeSymbols.properties");

        this.convertAndCheck(
            StorageBinary.with(
                storagePath,
                Binary.with(
                    dateTimeSymbols.text()
                        .getBytes(CHARSET)
                )
            ),
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(dateTimeSymbols)
                )
        );
    }

    @Test
    public void testConvertStorageBinaryPropertiesWithoutContentTypeToProperties() {
        final Properties properties = Properties.EMPTY.set(
            PropertiesPath.parse("hello.world.123"),
            "Hello World"
        ).set(
            PropertiesPath.parse("country"),
            "Australia"
        );

        final StoragePath storagePath = StoragePath.parse("/file.properties");

        this.convertAndCheck(
            StorageBinary.with(
                storagePath,
                Binary.with(
                    properties.text()
                        .getBytes(CHARSET)
                )
            ).clearContentType(),
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(properties)
                ).clearContentType()
        );
    }

    @Test
    public void testConvertStorageBinaryPropertiesWithPropertiesContentTypeToProperties() {
        final Properties properties = Properties.EMPTY.set(
            PropertiesPath.parse("hello.world.123"),
            "Hello World"
        ).set(
            PropertiesPath.parse("country"),
            "Australia"
        );

        final StoragePath storagePath = StoragePath.parse("/file.properties");

        this.convertAndCheck(
            StorageBinary.with(
                storagePath,
                Binary.with(
                    properties.text()
                        .getBytes(CHARSET)
                )
            ).setContentType(
                Optional.of(
                    MediaType.TEXT_PROPERTIES
                )
            ),
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(properties)
                ).setContentType(
                    Optional.of(
                        MediaType.TEXT_PROPERTIES
                    )
                )
        );
    }

    @Override
    public StorageConverterStorageBinaryToStorageValueSharedProperties<FakeStorageConverterContext> createConverter() {
        return StorageConverterStorageBinaryToStorageValueSharedProperties.instance();
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
                    Converters.binaryToString(),
                    Converters.textToProperties(),
                    Converters.propertiesToDateTimeSymbols()
                )
            );
        };
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createConverter(),
            "StorageBinary *.properties | text/x-java-properties to StorageValue"
        );
    }

    @Override
    public Class<StorageConverterStorageBinaryToStorageValueSharedProperties<FakeStorageConverterContext>> type() {
        return Cast.to(StorageConverterStorageBinaryToStorageValueSharedProperties.class);
    }
}
