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
import walkingkooka.collect.list.TsvStringList;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.net.header.MediaType;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;

import java.nio.charset.Charset;
import java.util.Optional;

public final class StorageConverterStorageValueToStorageBinarySharedTsvTest extends StorageConverterStorageValueToStorageBinarySharedTestCase<StorageConverterStorageValueToStorageBinarySharedTsv<FakeStorageConverterContext>> {

    @Test
    public void testConvertStorageValueTsvFileExtensionWithoutContentTypeToStorageBinary() {
        final TsvStringList list = TsvStringList.EMPTY.concat("abc")
            .concat("def")
            .concat("g h i");

        final StoragePath storagePath = StoragePath.parse("/dir/letters.tsv");

        this.convertAndCheck(
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(list)
                ).clearContentType(),
            StorageBinary.with(
                storagePath,
                Binary.with(
                    list.text()
                        .getBytes(CHARSET)
                )
            ).clearContentType()
        );
    }

    @Test
    public void testConvertStorageValueTsvFileExtensionWithTextTsvContentTypeToStorageBinary() {
        final TsvStringList list = TsvStringList.EMPTY.concat("abc")
            .concat("def")
            .concat("g h i");

        final StoragePath storagePath = StoragePath.parse("/dir/letters.tsv");

        this.convertAndCheck(
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(list)
                ).setContentType(
                    Optional.of(MediaType.TEXT_TAB_SEPARATED_VALUES)
                ),
            StorageBinary.with(
                storagePath,
                Binary.with(
                    list.text()
                        .getBytes(CHARSET)
                )
            ).setContentType(
                Optional.of(MediaType.TEXT_TAB_SEPARATED_VALUES)
            )
        );
    }

    @Test
    public void testConvertStorageValueWithOnlyContentTypeToStorageBinary() {
        final TsvStringList list = TsvStringList.EMPTY.concat("abc")
            .concat("def")
            .concat("g h i");

        final StoragePath storagePath = StoragePath.parse("/dir/letters");

        this.convertAndCheck(
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(list)
                ).setContentType(
                    Optional.of(MediaType.TEXT_TAB_SEPARATED_VALUES)
                ),
            StorageBinary.with(
                storagePath,
                Binary.with(
                    list.text()
                        .getBytes(CHARSET)
                )
            ).setContentType(
                Optional.of(MediaType.TEXT_TAB_SEPARATED_VALUES)
            )
        );
    }

    @Override
    public StorageConverterStorageValueToStorageBinarySharedTsv<FakeStorageConverterContext> createConverter() {
        return StorageConverterStorageValueToStorageBinarySharedTsv.instance();
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
                    Converters.toText(),
                    Converters.simple(),
                    Converters.textToBinary()
                )
            );
        };
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createConverter(),
            "*.tsv to StorageBinary"
        );
    }

    @Override
    public Class<StorageConverterStorageValueToStorageBinarySharedTsv<FakeStorageConverterContext>> type() {
        return Cast.to(StorageConverterStorageValueToStorageBinarySharedTsv.class);
    }
}
