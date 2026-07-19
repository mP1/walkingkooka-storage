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
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.text.BinaryTextContextTesting;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.JsonString;
import walkingkooka.tree.json.convert.JsonNodeConverters;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContextTesting;
import walkingkooka.util.HasLocaleTesting;

import java.nio.charset.Charset;
import java.text.DateFormatSymbols;
import java.util.Optional;

public final class StorageConverterStorageBinaryToStorageValueSharedJsonTest extends StorageConverterStorageBinaryToStorageValueSharedTestCase<StorageConverterStorageBinaryToStorageValueSharedJson<FakeStorageConverterContext>>
    implements BinaryTextContextTesting,
    HasLocaleTesting,
    JsonNodeMarshallUnmarshallContextTesting {

    @Test
    public void testConvertStorageBinaryDateTimeSymbolsJsonToStorageValue() {
        final FakeStorageConverterContext context = this.createContext();

        final JsonNode dateTimeSymbols = context.marshall(
            DateTimeSymbols.fromDateFormatSymbols(
                new DateFormatSymbols(LOCALE)
            )
        );

        final StoragePath storagePath = StoragePath.parse("/dateTimeSymbols.json");

        this.convertAndCheck(
            StorageBinary.with(
                storagePath,
                Binary.with(
                    dateTimeSymbols.toJsonText(context)
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
    public void testConvertStorageBinaryJsonToJson() {
        final JsonNode json = JsonNode.object()
            .set(
                JsonPropertyName.with("key111"),
                "string111"
            ).set(
                JsonPropertyName.with("country"),
                "Australia"
            );

        final StoragePath storagePath = StoragePath.parse("/jsonObject.json");

        this.convertAndCheck(
            StorageBinary.with(
                storagePath,
                Binary.with(
                    json.toString()
                        .getBytes(CHARSET)
                )
            ),
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(json)
                )
        );
    }

    @Override
    public StorageConverterStorageBinaryToStorageValueSharedJson<FakeStorageConverterContext> createConverter() {
        return StorageConverterStorageBinaryToStorageValueSharedJson.instance();
    }

    @Override
    public FakeStorageConverterContext createContext() {
        return new FakeStorageConverterContext() {

            @Override
            public Charset charset() {
                return CHARSET;
            }

            @Override
            public Indentation indentation() {
                return INDENTATION;
            }

            @Override
            public LineEnding lineEnding() {
                return LINE_ENDING;
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
                    Converters.binaryToString(),
                    JsonNodeConverters.jsonNodeTo()
                )
            ).cast(StorageConverterContext.class);

            @Override
            public Optional<JsonString> typeName(final Class<?> type) {
                return JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT.typeName(type);
            }

            @Override
            public JsonNode marshall(final Object value) {
                return JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT.marshall(value);
            }

            @Override
            public <T> T unmarshall(final JsonNode json,
                                    final Class<T> type) {
                return JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT.unmarshall(
                    json,
                    type
                );
            }
        };
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createConverter(),
            "StorageBinary *.json | application/json to StorageValue"
        );
    }

    @Override
    public Class<StorageConverterStorageBinaryToStorageValueSharedJson<FakeStorageConverterContext>> type() {
        return Cast.to(StorageConverterStorageBinaryToStorageValueSharedJson.class);
    }
}
