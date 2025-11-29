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
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.text.LineEnding;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicStorageContextTest implements StorageContextTesting<BasicStorageContext> {

    @Test
    public void testWithNullEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicStorageContext.with(null)
        );
    }

    @Override
    public BasicStorageContext createContext() {
        return BasicStorageContext.with(
            EnvironmentContexts.empty(
                LineEnding.NL,
                Locale.FRANCE,
                LocalDateTime::now,
                Optional.of(
                    EmailAddress.parse("user@example.com")
                )
            )
        );
    }

    @Override
    public Class<BasicStorageContext> type() {
        return BasicStorageContext.class;
    }
}
