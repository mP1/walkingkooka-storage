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
import walkingkooka.collect.list.CsvStringList;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class StorageConverterStorageBinaryToStorageValueCsvTest extends StorageConverterStorageBinaryToStorageValueTestCase<StorageConverterStorageBinaryToStorageValueCsv<FakeStorageConverterContext>> {

    private final static Charset CHARSET = StandardCharsets.UTF_8;

    @Test
    public void testConvertStorageBinaryJsonToCsvStringList() {
        final CsvStringList list = CsvStringList.EMPTY.concat(
            "abc"
        ).concat("def")
            .concat("ghi");

        final StoragePath storagePath = StoragePath.parse("/letters.csv");

        this.convertAndCheck(
            StorageBinary.with(
                storagePath,
                Binary.with(
                    list.text()
                        .getBytes(CHARSET)
                )
            ),
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(list)
                )
        );
    }

    @Override
    public StorageConverterStorageBinaryToStorageValueCsv<FakeStorageConverterContext> createConverter() {
        return StorageConverterStorageBinaryToStorageValueCsv.instance();
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

            private final Converter<StorageConverterContext> converter = Converters.collection(
                Lists.of(
                    Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString(),
                    Converters.hasBinaryToString(),
                    Converters.textToCsvStringList()
                )
            ).cast(StorageConverterContext.class);
        };
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createConverter(),
            "*.csv to StorageValue"
        );
    }

    @Override
    public Class<StorageConverterStorageBinaryToStorageValueCsv<FakeStorageConverterContext>> type() {
        return Cast.to(StorageConverterStorageBinaryToStorageValueCsv.class);
    }
}
