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
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.environment.AuditInfo;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.storage.StorageValueInfoList;
import walkingkooka.text.LineEnding;

import java.time.LocalDateTime;

public class StorageConverterSharedStorageValueInfoListToTextTest extends StorageConverterSharedTestCase<StorageConverterSharedStorageValueInfoListToText<FakeStorageConverterContext>> {

    @Test
    public void testConvertNullFails() {
        this.convertFails(
            null,
            StoragePath.class
        );
    }

    @Test
    public void testConvertNonStorageValueInfoListFails() {
        this.convertFails(
            123,
            String.class
        );
    }

    @Test
    public void testConvertStorageValueInfoListToString() {
        this.convertAndCheck(
            StorageValueInfoList.EMPTY.concat(
                StorageValueInfo.with(
                    StoragePath.parse("/1st"),
                    AuditInfo.create(
                        EmailAddress.parse("user@example.com"),
                        LocalDateTime.MIN
                    )
                )
            ).concat(
                StorageValueInfo.with(
                    StoragePath.parse("/2nd"),
                    AuditInfo.create(
                        EmailAddress.parse("user@example.com"),
                        LocalDateTime.MIN
                    )
                )
            ),
            String.class,
            "/1st\n/2nd\n"
        );
    }

    @Override
    public StorageConverterSharedStorageValueInfoListToText<FakeStorageConverterContext> createConverter() {
        return StorageConverterSharedStorageValueInfoListToText.instance();
    }

    @Override
    public FakeStorageConverterContext createContext() {
        return new FakeStorageConverterContext() {

            @Override
            public LineEnding lineEnding() {
                return LineEnding.NL;
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

            private final Converter<ConverterContext> converter = Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString();
        };
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            StorageConverterSharedStorageValueInfoListToText.instance(),
            "StorageValueInfoList -> Text"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageConverterSharedStorageValueInfoListToText<FakeStorageConverterContext>> type() {
        return Cast.to(StorageConverterSharedStorageValueInfoListToText.class);
    }
}
