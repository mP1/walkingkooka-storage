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
import walkingkooka.datetime.DateTimeContextTesting;
import walkingkooka.net.header.MediaType;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;

import java.nio.charset.Charset;
import java.util.Optional;

public final class StorageConverterStorageValueToStorageBinarySharedPropertiesTest extends StorageConverterStorageValueToStorageBinarySharedTestCase<StorageConverterStorageValueToStorageBinarySharedProperties<FakeStorageConverterContext>>
    implements DateTimeContextTesting {

    @Test
    public void testConvertEmptyStorageValuePropertiesToStorageBinaryFails() {
        this.convertFails(
            StorageValue.with(
                    StoragePath.parse("/dir/DateTimeSymbols.properties")
                ),
            StorageBinary.class
        );
    }

    @Test
    public void testConvertStorageValuePropertiesWithFileExtensionWithoutContentTypeToStorageBinary() {
        final StoragePath storagePath = StoragePath.parse("/dir/DateTimeSymbols.properties");

        this.convertAndCheck(
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(DATE_TIME_SYMBOLS)
                ).clearContentType(),
            StorageBinary.with(
                storagePath,
                Binary.with(
                    DATE_TIME_SYMBOLS.properties()
                        .text()
                        .getBytes(CHARSET)
                )
            ).clearContentType()
        );
    }

    @Test
    public void testConvertStorageValuePropertiesWithFileExtensionWithTextPropertiesContentTypeToStorageBinary() {
        final StoragePath storagePath = StoragePath.parse("/dir/DateTimeSymbols.properties");

        this.convertAndCheck(
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(DATE_TIME_SYMBOLS)
                ).setContentType(
                    Optional.of(MediaType.TEXT_PROPERTIES)
                ),
            StorageBinary.with(
                storagePath,
                Binary.with(
                    DATE_TIME_SYMBOLS.properties()
                        .text()
                        .getBytes(CHARSET)
                )
            ).setContentType(
                Optional.of(MediaType.TEXT_PROPERTIES)
            )
        );
    }

    @Test
    public void testConvertStorageValuePropertiesWithOnlyContentTypeToStorageBinary() {
        final StoragePath storagePath = StoragePath.parse("/dir/DateTimeSymbols");

        this.convertAndCheck(
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(DATE_TIME_SYMBOLS)
                ).setContentType(
                    Optional.of(MediaType.TEXT_PROPERTIES)
                ),
            StorageBinary.with(
                storagePath,
                Binary.with(
                    DATE_TIME_SYMBOLS.properties()
                        .text()
                        .getBytes(CHARSET)
                )
            ).setContentType(
                Optional.of(MediaType.TEXT_PROPERTIES)
            )
        );
    }

    @Override
    public StorageConverterStorageValueToStorageBinarySharedProperties<FakeStorageConverterContext> createConverter() {
        return StorageConverterStorageValueToStorageBinarySharedProperties.instance();
    }

    @Override
    public FakeStorageConverterContext createContext() {
        return new FakeStorageConverterContext() {

            @Override
            public Charset charset() {
                return CHARSET;
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
                    Converters.toProperties(),
                    Converters.toText(),
                    Converters.textToBinary()
                )
            );
        };
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createConverter(),
            "*.properties to StorageBinary"
        );
    }

    @Override
    public Class<StorageConverterStorageValueToStorageBinarySharedProperties<FakeStorageConverterContext>> type() {
        return Cast.to(StorageConverterStorageValueToStorageBinarySharedProperties.class);
    }
}
