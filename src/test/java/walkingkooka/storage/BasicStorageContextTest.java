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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.datetime.HasNow;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.text.LineEnding;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicStorageContextTest implements StorageContextTesting<BasicStorageContext>,
    HashCodeEqualsDefinedTesting2<BasicStorageContext> {

    private final static HasNow HAS_NOW = LocalDateTime::now;

    @Test
    public void testWithNullEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicStorageContext.with(null)
        );
    }

    @Test
    public void testSetEnvironmentContext() {
        final BasicStorageContext context = this.createContext();

        final EnvironmentContext environmentContext = EnvironmentContexts.empty(
            LineEnding.CRNL,
            Locale.GERMAN,
            HAS_NOW,
            Optional.of(
                EmailAddress.parse("different@example.com")
            )
        );

        final StorageContext after = context.setEnvironmentContext(environmentContext);
        assertNotSame(
            context,
            after
        );

        this.checkEquals(
            BasicStorageContext.with(environmentContext),
            after
        );
    }

    @Override
    public BasicStorageContext createContext() {
        return BasicStorageContext.with(
            EnvironmentContexts.map(
                EnvironmentContexts.empty(
                    LineEnding.NL,
                    Locale.FRANCE,
                    HAS_NOW,
                    Optional.of(
                        EmailAddress.parse("user@example.com")
                    )
                )
            )
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentEnvironmentContext() {
        this.checkNotEquals(
            BasicStorageContext.with(
                EnvironmentContexts.empty(
                    LineEnding.CR,
                    Locale.GERMAN,
                    LocalDateTime::now,
                    Optional.of(
                        EmailAddress.parse("user@example.com")
                    )
                )
            )
        );
    }

    @Override
    public BasicStorageContext createObject() {
        return this.createContext();
    }

    // class............................................................................................................

    @Override
    public Class<BasicStorageContext> type() {
        return BasicStorageContext.class;
    }
}
