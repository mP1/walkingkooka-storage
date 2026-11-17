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
import walkingkooka.tree.json.convert.JsonNodeConverters;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageExpressionFunctionScriptTest extends StorageExpressionFunctionTestCase<StorageExpressionFunctionScript<TestStorageExpressionEvaluationContext>, Object>
    implements ThrowableTesting {

    private final static StoragePath SCRIPT = StoragePath.parse("/dir1/script.sh");

    private final static String RESPONSE = "Hello World";

    @Test
    public void testApplyScriptEvaluated() {
        this.applyAndCheck(
            Lists.of(SCRIPT),
            RESPONSE
        );
    }

    @Test
    public void testApplyScriptMissing() {
        final InvalidStoragePathException thrown = assertThrows(
            InvalidStoragePathException.class,
            () -> this.createBiFunction()
                .apply(
                    Lists.of(
                        StoragePath.parse("/dir1/missing-script.sh")
                    ),
                    this.createContext()
                )
        );

        this.getMessageAndCheck(
            thrown,
            "Missing script \"/dir1/missing-script.sh\""
        );
    }

    @Override
    public void testSetParametersSame() {
        throw new UnsupportedOperationException();
    }

    @Override
    public StorageExpressionFunctionScript<TestStorageExpressionEvaluationContext> createBiFunction() {
        return StorageExpressionFunctionScript.instance();
    }

    @Override
    public TestStorageExpressionEvaluationContext createContext() {
        return new TestStorageExpressionEvaluationContext(
            new FakeStorage<>() {
                @Override
                public Optional<StorageValue> load(final StoragePath path,
                                                   final TestStorageExpressionEvaluationContext context) {
                    return Optional.ofNullable(
                        path.equals(SCRIPT) ?
                            StorageValue.with(path)
                                .setValue(
                                    Optional.of(RESPONSE)
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
                    Converters.toText()
                )
            );

            @Override
            public Object evaluate(final String expression) {
                return RESPONSE;
            }

            @Override
            public Optional<StoragePath> currentWorkingDirectory() {
                return OPTIONAL_CURRENT_WORKING_DIRECTORY;
            }
        };
    }

    @Override
    public int minimumParameterCount() {
        return 1;
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            StorageExpressionFunctionScript.instance(),
            "script"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageExpressionFunctionScript<TestStorageExpressionEvaluationContext>> type() {
        return Cast.to(StorageExpressionFunctionScript.class);
    }
}
