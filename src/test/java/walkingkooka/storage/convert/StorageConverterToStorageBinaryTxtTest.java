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
import walkingkooka.net.header.MediaType;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormatSymbols;
import java.util.Locale;
import java.util.Optional;

public final class StorageConverterToStorageBinaryTxtTest extends StorageConverterToStorageBinaryTestCase<StorageConverterToStorageBinaryTxt<FakeStorageConverterContext>> {

    private final static Charset CHARSET = StandardCharsets.UTF_8;

    @Test
    public void testConvertEmptyStorageValueTxtToStorageBinaryFails() {
        this.convertFails(
            StorageValue.with(
                StoragePath.parse("/dir/DateTimeSymbols.txt")
            ).setValue(
                Optional.empty()
            ),
            StorageBinary.class
        );
    }

    @Test
    public void testConvertStorageValueWithFileExtensionTxtToStorageBinary() {
        final DateTimeSymbols dateTimeSymbols = DateTimeSymbols.fromDateFormatSymbols(
            new DateFormatSymbols(
                Locale.forLanguageTag("en-AU")
            )
        );

        final StoragePath storagePath = StoragePath.parse("/dir/DateTimeSymbols.txt");

        this.convertAndCheck(
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(dateTimeSymbols)
                ),
            StorageBinary.with(
                storagePath,
                Binary.with(
                    dateTimeSymbols.text()
                        .getBytes(CHARSET)
                )
            )
        );
    }

    @Test
    public void testConvertStorageValueTxtAndStringToStorageBinary() {
        final String value = "Hello world";
        final StoragePath storagePath = StoragePath.parse("/dir/text-file.txt");

        this.convertAndCheck(
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(value)
                ),
            StorageBinary.with(
                storagePath,
                Binary.with(
                    value.getBytes(CHARSET)
                )
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
                    Optional.of(MediaType.TEXT_PLAIN)
                ),
            StorageBinary.with(
                storagePath,
                Binary.with(
                    value.getBytes(CHARSET)
                )
            )
        );
    }

    @Override
    public StorageConverterToStorageBinaryTxt<FakeStorageConverterContext> createConverter() {
        return StorageConverterToStorageBinaryTxt.instance();
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
                    Converters.hasText(),
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
    public Class<StorageConverterToStorageBinaryTxt<FakeStorageConverterContext>> type() {
        return Cast.to(StorageConverterToStorageBinaryTxt.class);
    }
}
