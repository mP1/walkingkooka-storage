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
import walkingkooka.HasBinary;
import walkingkooka.HasCharsetTesting;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;

import java.nio.charset.Charset;
import java.util.Optional;

public final class StorageConverterStorageValueToStorageBinaryBinaryTest extends StorageConverterTestCase<StorageConverterStorageValueToStorageBinaryBinary<FakeStorageConverterContext>>
    implements HasCharsetTesting,
    ToStringTesting<StorageConverterStorageValueToStorageBinaryBinary<FakeStorageConverterContext>> {

    @Test
    public void testConvertStorageValueWithBinary() {
        final StoragePath path = StoragePath.parse("/file123.bin");

        final Binary binary = Binary.with(
            "Hello".getBytes(CHARSET)
        );

        this.convertAndCheck(
            StorageValue.with(path)
                .setValue(
                    Optional.of(
                        new HasBinary() {
                            @Override
                            public Binary binary() {
                                return binary;
                            }
                        }
                    )
                ),
            StorageBinary.class,
            StorageBinary.with(
                path,
                binary
            )
        );
    }

    @Override
    public StorageConverterStorageValueToStorageBinaryBinary<FakeStorageConverterContext> createConverter() {
        return StorageConverterStorageValueToStorageBinaryBinary.instance();
    }

    @Override
    public FakeStorageConverterContext createContext() {
        return new FakeStorageConverterContext() {

            @Override
            public Charset charset() {
                return StorageConverterStorageValueToStorageBinaryBinaryTest.CHARSET;
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
                    Converters.binaryToString(),
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
            "StorageValue(Binary) to StorageBinary"
        );
    }

    @Override
    public Class<StorageConverterStorageValueToStorageBinaryBinary<FakeStorageConverterContext>> type() {
        return Cast.to(StorageConverterStorageValueToStorageBinaryBinary.class);
    }
}
