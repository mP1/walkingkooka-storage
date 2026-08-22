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
import walkingkooka.environment.CanParseEnvironmentValueNameTesting;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.ThrowableTesting;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageEnvironmentContextTest implements CanParseEnvironmentValueNameTesting,
    ClassTesting<StorageEnvironmentContext>,
    ThrowableTesting {

    // STORAGE_ENVIRONMENT_CONTEXT_PARSE................................................................................

    @Test
    public void testEnvironmentContextParseWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageEnvironmentContext.STORAGE_ENVIRONMENT_CONTEXT_PARSE.parseEnvironmentValueName(null)
        );
    }

    @Test
    public void testEnvironmentContextParseWithUnknownFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> StorageEnvironmentContext.STORAGE_ENVIRONMENT_CONTEXT_PARSE.parseEnvironmentValueName("unknown")
        );

        this.getMessageAndCheck(
            thrown,
            "Unknown environment value name \"unknown\""
        );
    }

    @Test
    public void testEnvironmentContextParseWithCharset() {
        this.parseEnvironmentValueNameAndCheck(
            StorageEnvironmentContext.STORAGE_ENVIRONMENT_CONTEXT_PARSE,
            EnvironmentValueName.CHARSET
        );
    }

    @Test
    public void testEnvironmentContextParseWithHomeDirectory() {
        this.parseEnvironmentValueNameAndCheck(
            StorageEnvironmentContext.STORAGE_ENVIRONMENT_CONTEXT_PARSE,
            StorageEnvironmentContext.HOME_DIRECTORY
        );
    }

    @Test
    public void testEnvironmentContextParseWithEnvironmentConstants() throws Exception {
        int i = 0;

        for(final Field field : EnvironmentContext.class.getDeclaredFields()) {
            if(field.getType() == EnvironmentValueName.class) {
                this.parseEnvironmentValueNameAndCheck(
                    StorageEnvironmentContext.STORAGE_ENVIRONMENT_CONTEXT_PARSE,
                    (EnvironmentValueName<?>) field.get(null)
                );
                i++;
            }
        }

        this.checkNotEquals(
            0,
            i
        );
    }
    
    // class............................................................................................................
    
    @Override
    public Class<StorageEnvironmentContext> type() {
        return StorageEnvironmentContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
