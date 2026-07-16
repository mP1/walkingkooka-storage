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
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.JsonString;
import walkingkooka.tree.json.convert.JsonNodeConverters;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormatSymbols;
import java.util.Locale;
import java.util.Optional;

public final class StorageConverterStorageValueToStorageBinarySharedJsonTest extends StorageConverterStorageValueToStorageBinarySharedTestCase<StorageConverterStorageValueToStorageBinarySharedJson<FakeStorageConverterContext>> {

    private final static Charset CHARSET = StandardCharsets.UTF_8;

    private final static JsonNodeMarshallContext MARSHALL_CONTEXT = JsonNodeMarshallContexts.basic();

    @Test
    public void testConvertEmptyStorageValueJsonToStorageBinaryFails() {
        this.convertFails(
            StorageValue.with(
                StoragePath.parse("/dir/DateTimeSymbols.json")
            ),
            StorageBinary.class
        );
    }

    @Test
    public void testConvertStorageValueFileExtensionJsonToStorageBinary() {
        final DateTimeSymbols dateTimeSymbols = DateTimeSymbols.fromDateFormatSymbols(
            new DateFormatSymbols(
                Locale.forLanguageTag("en-AU")
            )
        );

        final StoragePath storagePath = StoragePath.parse("/dir/DateTimeSymbols.json");

        this.convertAndCheck(
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(dateTimeSymbols)
                ),
            StorageBinary.with(
                storagePath,
                MARSHALL_CONTEXT.marshall(dateTimeSymbols)
                    .binary(CHARSET)
            )
        );
    }

    @Test
    public void testConvertStorageValueJsonWithoutFileExtensionToStorageBinary() {
        final JsonNode json = JsonNode.object()
            .set(
                JsonPropertyName.with("hello"),
                "World123"
            );

        final StoragePath storagePath = StoragePath.parse("/dir/json");

        this.convertAndCheck(
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(json)
                ),
            StorageBinary.with(
                storagePath,
                json.binary(CHARSET)
            )
        );
    }

    @Test
    public void testConvertStorageValueWithoutFileExtensionToStorageBinary() {
        final DateTimeSymbols dateTimeSymbols = DateTimeSymbols.fromDateFormatSymbols(
            new DateFormatSymbols(
                Locale.forLanguageTag("en-AU")
            )
        );

        final StoragePath storagePath = StoragePath.parse("/dir/DateTimeSymbols");

        this.convertFails(
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(dateTimeSymbols)
                ),
            StorageBinary.class
        );
    }

    @Override
    public StorageConverterStorageValueToStorageBinarySharedJson<FakeStorageConverterContext> createConverter() {
        return StorageConverterStorageValueToStorageBinarySharedJson.instance();
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

            private final Converter<StorageConverterContext> converter = Converters.collection(
                Lists.of(
                    Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString(),
                    JsonNodeConverters.toJsonNode(),
                    Converters.toText(),
                    Converters.textToBinary()
                )
            );

            @Override
            public Optional<JsonString> typeName(final Class<?> type) {
                return MARSHALL_CONTEXT.typeName(type);
            }

            @Override
            public JsonNode marshall(final Object value) {
                return MARSHALL_CONTEXT.marshall(value);
            }
        };
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createConverter(),
            "*.json to StorageBinary"
        );
    }

    @Override
    public Class<StorageConverterStorageValueToStorageBinarySharedJson<FakeStorageConverterContext>> type() {
        return Cast.to(StorageConverterStorageValueToStorageBinarySharedJson.class);
    }
}
