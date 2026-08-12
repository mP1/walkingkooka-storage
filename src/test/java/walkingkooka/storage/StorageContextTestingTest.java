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

package walkingkooka.storage;

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

public final class StorageContextTestingTest implements StorageContextTesting,
    ClassTesting2<StorageContextTesting> {

    @Test
    public void testStorageContextConstant() {
        this.checkEquals(
            "application/octet-stream  {charset=UTF-8, currency=AUD, currentWorkingDirectory=/current1/working2/directory3, homeDirectory=/users/user123@example.com, indentation=\"  \", lineEnding=\"\\n\", locale=en_AU, timeOffset=Z, user=user123@example.com}",
            StorageContextTesting.STORAGE_CONTEXT.toString()
        );
    }

    @Test
    public void testDifferentStorageContextConstant() {
        this.checkEquals(
            "application/octet-stream  {charset=ISO-8859-1, currency=NZD, currentWorkingDirectory=/different/current1/working2/directory3, homeDirectory=/users/different-user, indentation=\"    \", lineEnding=\"\\r\\n\", locale=en_NZ, timeOffset=Z, user=different-user-456@example.com}",
            StorageContextTesting.DIFFERENT_STORAGE_CONTEXT.toString()
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageContextTesting> type() {
        return StorageContextTesting.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
