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

package walkingkooka.storage.expression;

import org.junit.jupiter.api.Test;
import walkingkooka.environment.expression.EnvironmentExpressionEvaluationContextTesting2;
import walkingkooka.reflect.ThrowableTesting;
import walkingkooka.storage.StorageContextTesting2;
import walkingkooka.storage.StorageEnvironmentContextTesting2;
import walkingkooka.storage.convert.StorageConverterContextTesting2;
import walkingkooka.tree.expression.ExpressionEvaluationContextTesting2;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface StorageExpressionEvaluationContextTesting2<C extends StorageExpressionEvaluationContext> extends ExpressionEvaluationContextTesting2<C>,
    EnvironmentExpressionEvaluationContextTesting2<C>,
    StorageContextTesting2<C>,
    StorageExpressionEvaluationContextTesting,
    StorageEnvironmentContextTesting2<C>,
    StorageConverterContextTesting2<C>,
    ThrowableTesting {

    @Test
    @Override
    default void testSetLocaleWithNullFails() {
        EnvironmentExpressionEvaluationContextTesting2.super.testSetLocaleWithNullFails();
    }

    // loadStorage......................................................................................................

    @Test
    default void testLoadStorageWithNullPathFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .loadStorage(null)
        );
    }

    // saveStorage......................................................................................................

    @Test
    default void testSaveStorageWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .saveStorage(null)
        );
    }

    // deleteStorage....................................................................................................

    @Test
    default void testDeleteStorageWithNullPathFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .deleteStorage(null)
        );
    }

    @Test
    default void testMountStorageWithNullStorageMountStorageFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .mountStorage(null)
        );
    }

    @Test
    default void testUnmountStorageWithNullStoragePathFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .unmountStorage(null)
        );
    }

    @Override
    default C createCanDateTimeSymbolsForLocale() {
        return this.createContext();
    }

    @Override
    default C createCanDecimalNumberSymbolsForLocale() {
        return this.createContext();
    }

    @Override
    default C createCanLocaleForLanguageTag() {
        return this.createContext();
    }

    @Override
    default C createCanParseEnvironmentValueName() {
        return this.createContext();
    }

    @Override
    default C createCanParseStoragePath() {
        return this.createContext();
    }

    @Override
    default C createConverterLike() {
        return this.createContext();
    }

    @Override
    default C createMediaTypeDetector() {
        return this.createContext();
    }

    // class............................................................................................................

    @Override
    default String typeNameSuffix() {
        return StorageExpressionEvaluationContext.class.getSimpleName();
    }
}
