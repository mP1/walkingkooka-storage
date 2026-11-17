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

package walkingkooka.storage.expression.function;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.reflect.ThrowableTesting;
import walkingkooka.storage.FakeStorage;
import walkingkooka.storage.InvalidStoragePathException;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.expression.function.StorageExpressionFunctionTestCase.TestStorageExpressionEvaluationContext;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonString;
import walkingkooka.tree.json.convert.JsonNodeConverters;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageExpressionFunctionReadTextTest extends StorageExpressionFunctionTestCase<StorageExpressionFunctionReadText<TestStorageExpressionEvaluationContext>, String>
    implements ThrowableTesting {

    private final static StoragePath PATH = StoragePath.parse("/dir1/file2.json");

    private final static JsonNode JSON = JsonNode.parse("{ \"Hello\": \"World\" }");

    @Test
    public void testApplyWithJsonObject() {
        this.applyAndCheck(
            Lists.of(PATH),
            "{\n" +
                "  \"Hello\": \"World\"\n" +
                "}"
        );
    }

    @Test
    public void testApplyStorageEntryMissing() {
        final InvalidStoragePathException thrown = assertThrows(
            InvalidStoragePathException.class,
            () -> this.apply2(
                StoragePath.parse("/dir1/missing.json")
            )
        );

        this.getMessageAndCheck(
            thrown,
            "Unable to read text \"/dir1/missing.json\""
        );
    }

    @Test
    public void testApplyStorageEntryPresentAndMissingPath() {
        final InvalidStoragePathException thrown = assertThrows(
            InvalidStoragePathException.class,
            () -> this.apply2()
        );

        this.getMessageAndCheck(
            thrown,
            "Unable to read text \"/current1/working2/directory3\""
        );
    }

    @Override
    public void testSetParametersSame() {
        throw new UnsupportedOperationException();
    }

    @Override
    public StorageExpressionFunctionReadText<TestStorageExpressionEvaluationContext> createBiFunction() {
        return StorageExpressionFunctionReadText.instance();
    }

    @Override
    public TestStorageExpressionEvaluationContext createContext() {
        return new TestStorageExpressionEvaluationContext(
            new FakeStorage<>() {
                @Override
                public Optional<StorageValue> load(final StoragePath path,
                                                   final TestStorageExpressionEvaluationContext context) {
                    return Optional.ofNullable(
                        path.equals(PATH) ?
                            StorageValue.with(path)
                                .setValue(
                                    Optional.of(JSON)
                                ) :
                            null
                    );
                }
            }
        ) {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> type) {
                return this.converters.canConvert(
                    value,
                    type,
                    this
                );
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return this.converters.convert(
                        value,
                        target,
                        this
                    );
            }

            private final Converter<TestStorageExpressionEvaluationContext> converters = Converters.collection(
                Lists.of(
                    Converters.simple(),
                    JsonNodeConverters.toJsonNode(),
                    Converters.toMultiLineText(),
                    Converters.toText()
                )
            );

            @Override
            public Optional<StoragePath> currentWorkingDirectory() {
                return OPTIONAL_CURRENT_WORKING_DIRECTORY;
            }

            @Override
            public JsonNode marshall(final Object value) {
                return JSON_NODE_MARSHALL_CONTEXT.marshall(value);
            }

            @Override
            public Optional<JsonString> typeName(final Class<?> type) {
                return JSON_NODE_MARSHALL_CONTEXT.typeName(type);
            }
        };
    }

    @Override
    public int minimumParameterCount() {
        return 0;
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            StorageExpressionFunctionReadText.instance(),
            "readStorageText"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageExpressionFunctionReadText<TestStorageExpressionEvaluationContext>> type() {
        return Cast.to(StorageExpressionFunctionReadText.class);
    }
}
