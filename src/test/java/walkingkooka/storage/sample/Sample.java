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

package walkingkooka.storage.expression.function.sample;

import org.junit.jupiter.api.Test;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.expression.FakeStorageExpressionEvaluationContext;
import walkingkooka.storage.expression.StorageExpressionEvaluationContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class Sample {

    public static void main(final String[] args) {
        final Sample sample = new Sample();
        sample.testLoadStorage();
    }

    @Test
    public void testLoadStorage() {
        final StoragePath path = StoragePath.parse("/hello");

        final Optional<StorageValue> storageValue = Optional.of(
            StorageValue.with(path)
                .setValue(
                    Optional.of(1)
                )
        );

        final StorageExpressionEvaluationContext context = new FakeStorageExpressionEvaluationContext() {

            @Override
            public Optional<StorageValue> loadStorage(final StoragePath path) {
                return storageValue;
            }
        };

        checkEquals(
            storageValue,
            context.loadStorage(
                path
            )
        );
    }

    private void checkEquals(final Object expected,
                             final Object actual) {
        assertEquals(
            expected,
            actual
        );
    }
}

