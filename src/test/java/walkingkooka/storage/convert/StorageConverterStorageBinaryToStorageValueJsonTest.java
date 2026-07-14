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
import walkingkooka.currency.CurrencyLocaleContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.JsonString;
import walkingkooka.tree.json.convert.JsonNodeConverters;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.MathContext;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormatSymbols;
import java.util.Locale;
import java.util.Optional;

public final class StorageConverterStorageBinaryToStorageValueJsonTest extends StorageConverterStorageBinaryToStorageValueTestCase<StorageConverterStorageBinaryToStorageValueJson<FakeStorageConverterContext>> {

    private final static Charset CHARSET = StandardCharsets.UTF_8;

    private final JsonNodeMarshallUnmarshallContext MARSHALL_UNMARSHALL_CONTEXT =  JsonNodeMarshallUnmarshallContexts.basic(
        JsonNodeMarshallContexts.basic(),
        JsonNodeUnmarshallContexts.basic(
            ExpressionNumberKind.DEFAULT,
            CurrencyLocaleContexts.fake(), // CurrencyCodeLanguageTagContext
            MathContext.DECIMAL32
        )
    );

    @Test
    public void testConvertStorageBinaryJsonToDateTimeSymbols() {
        final DateTimeSymbols dateTimeSymbols = DateTimeSymbols.fromDateFormatSymbols(
            new DateFormatSymbols(
                Locale.ENGLISH
            )
        );

        final FakeStorageConverterContext context = this.createContext();

        final StoragePath storagePath = StoragePath.parse("/dateTimeSymbols.json");

        this.convertAndCheck(
            StorageBinary.with(
                storagePath,
                Binary.with(
                    context.marshall(dateTimeSymbols)
                        .toString()
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
    public StorageConverterStorageBinaryToStorageValueJson<FakeStorageConverterContext> createConverter() {
        return StorageConverterStorageBinaryToStorageValueJson.instance();
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
                    JsonNodeConverters.jsonNodeTo()
                )
            ).cast(StorageConverterContext.class);

            @Override
            public Optional<JsonString> typeName(final Class<?> type) {
                return MARSHALL_UNMARSHALL_CONTEXT.typeName(type);
            }

            @Override
            public JsonNode marshall(final Object value) {
                return MARSHALL_UNMARSHALL_CONTEXT.marshall(value);
            }

            @Override
            public <T> T unmarshall(final JsonNode json,
                                    final Class<T> type) {
                return MARSHALL_UNMARSHALL_CONTEXT.unmarshall(
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
            "*.json to StorageValue"
        );
    }

    @Override
    public Class<StorageConverterStorageBinaryToStorageValueJson<FakeStorageConverterContext>> type() {
        return Cast.to(StorageConverterStorageBinaryToStorageValueJson.class);
    }
}
