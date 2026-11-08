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
import walkingkooka.datetime.HasDateTimeSymbolsTesting;
import walkingkooka.net.header.MediaType;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;

import java.nio.charset.Charset;
import java.text.DateFormatSymbols;
import java.util.Locale;
import java.util.Optional;

public final class StorageConverterStorageValueToStorageBinarySharedTxtTest extends StorageConverterStorageValueToStorageBinarySharedTestCase<StorageConverterStorageValueToStorageBinarySharedTxt<FakeStorageConverterContext>>
    implements HasDateTimeSymbolsTesting {

    private final static StoragePath STORAGE_PATH = StoragePath.parse("/dir/DateTimeSymbols.txt");

    private final static Optional<MediaType> CONTENT_TYPE = Optional.of(MediaType.TEXT_PLAIN);

    @Test
    public void testConvertEmptyStorageValueTxtToStorageBinaryFails() {
        this.convertFails(
            StorageValue.with(STORAGE_PATH)
                .setValue(
                    Optional.empty()
                ),
            StorageBinary.class
        );
    }

    @Test
    public void testConvertStorageValueWithFileExtensionTxtWithoutContentTypeToStringFails() {
        this.convertFails(
            StorageValue.with(STORAGE_PATH)
                .setValue(
                    Optional.of(DATE_TIME_SYMBOLS)
                ).clearContentType(),
            String.class
        );
    }

    @Test
    public void testConvertStorageValueWithFileExtensionTxtWithoutContentTypeToStorageBinary() {
        this.convertAndCheck(
            StorageValue.with(STORAGE_PATH)
                .setValue(
                    Optional.of(DATE_TIME_SYMBOLS)
                ).clearContentType(),
            StorageBinary.with(
                STORAGE_PATH,
                Binary.with(
                    DATE_TIME_SYMBOLS.text()
                        .getBytes(CHARSET)
                )
            ).setContentType(CONTENT_TYPE)
        );
    }

    @Test
    public void testConvertStorageValueWithFileExtensionTxtWithTextPlainContentTypeToStorageBinary() {
        final DateTimeSymbols dateTimeSymbols = DateTimeSymbols.fromDateFormatSymbols(
            new DateFormatSymbols(
                Locale.forLanguageTag("en-AU")
            )
        );

        this.convertAndCheck(
            StorageValue.with(STORAGE_PATH)
                .setValue(
                    Optional.of(dateTimeSymbols)
                ).setContentType(CONTENT_TYPE),
            StorageBinary.with(
                STORAGE_PATH,
                Binary.with(
                    dateTimeSymbols.text()
                        .getBytes(CHARSET)
                )
            ).setContentType(CONTENT_TYPE)
        );
    }

    @Test
    public void testConvertStorageValueTxtWithTextPlainContentTypeAndStringToStorageBinary() {
        final String value = "Hello world";

        this.convertAndCheck(
            StorageValue.with(STORAGE_PATH)
                .setValue(
                    Optional.of(value)
                ).setContentType(
                    CONTENT_TYPE
                ),
            StorageBinary.with(
                STORAGE_PATH,
                Binary.with(
                    value.getBytes(CHARSET)
                )
            ).setContentType(
                CONTENT_TYPE
            )
        );
    }

    @Test
    public void testConvertStorageValueStringAndContentTypeToStorageBinary() {
        final String value = "Hello world";
        final StoragePath storagePath = StoragePath.parse("/dir/text-file");

        this.convertAndCheck(
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(value)
                ).setContentType(
                    CONTENT_TYPE
                ),
            StorageBinary.with(
                storagePath,
                Binary.with(
                    value.getBytes(CHARSET)
                )
            ).setContentType(
                CONTENT_TYPE
            )
        );
    }

    @Override
    public StorageConverterStorageValueToStorageBinarySharedTxt<FakeStorageConverterContext> createConverter() {
        return StorageConverterStorageValueToStorageBinarySharedTxt.instance();
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
                    Converters.toText(),
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
            "*.txt to StorageBinary"
        );
    }

    @Override
    public Class<StorageConverterStorageValueToStorageBinarySharedTxt<FakeStorageConverterContext>> type() {
        return Cast.to(StorageConverterStorageValueToStorageBinarySharedTxt.class);
    }
}
