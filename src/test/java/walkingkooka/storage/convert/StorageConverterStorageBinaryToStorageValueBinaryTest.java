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
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.net.header.MediaType;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class StorageConverterStorageBinaryToStorageValueBinaryTest extends StorageConverterStorageBinaryToStorageValueTestCase<StorageConverterStorageBinaryToStorageValueBinary<FakeStorageConverterContext>> {

    private final static Charset CHARSET = StandardCharsets.UTF_8;

    private final static Binary BINARY = Binary.with(
        "Hello".getBytes(CHARSET)
    );

    private final static MediaType DETECTED_CONTENT_TYPE = MediaType.parse("text/custom123");

    private final static String PATH = "/image.bin";

    @Test
    public void testConvertStorageBinaryToStorageValue() {
        final StoragePath storagePath = StoragePath.parse(PATH);

        this.convertAndCheck(
            StorageBinary.with(
                storagePath,
                BINARY
            ),
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(BINARY)
                ).setContentType(
                    Optional.of(DETECTED_CONTENT_TYPE)
                )
        );
    }

    @Override
    public StorageConverterStorageBinaryToStorageValueBinary<FakeStorageConverterContext> createConverter() {
        return StorageConverterStorageBinaryToStorageValueBinary.instance();
    }

    @Override
    public FakeStorageConverterContext createContext() {
        return new FakeStorageConverterContext() {

            @Override
            public MediaType detect(final String filename,
                                    final Binary content) {
                checkEquals(PATH, filename);
                checkEquals(BINARY, content);
                return DETECTED_CONTENT_TYPE;
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

            private final Converter<StorageConverterContext> converter = Converters.binaryToString();
        };
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createConverter(),
            "* to " + StorageValue.class.getSimpleName()
        );
    }

    @Override
    public Class<StorageConverterStorageBinaryToStorageValueBinary<FakeStorageConverterContext>> type() {
        return Cast.to(StorageConverterStorageBinaryToStorageValueBinary.class);
    }
}
