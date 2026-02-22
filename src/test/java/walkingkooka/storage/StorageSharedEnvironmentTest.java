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
import walkingkooka.Cast;
import walkingkooka.environment.AuditInfo;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextTesting;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageSharedEnvironmentTest extends StorageSharedTestCase<StorageSharedEnvironment<StorageContext>, StorageContext>
    implements EnvironmentContextTesting {

    private final static LocalDateTime NOW = LocalDateTime.of(
        1999,
        12,
        31,
        12,
        58,
        59
    );

    private final static EmailAddress USER = EmailAddress.parse("user@example.com");

    private final static EnvironmentValueName<Integer> MAGIC_ENVIRONMENT_VALUE_NAME = EnvironmentValueName.with(
        "magic-integer",
        Integer.class
    );

    private final static Integer MAGIC_ENVIRONMENT_VALUE = 123;

    @Test
    public void testLoadMissing() {
        this.loadAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            this.createContext()
        );
    }

    @Test
    public void testLoadUnknownEnvironmentValue() {
        this.loadAndCheck(
            this.createStorage(),
            StoragePath.parse("/unknown-environment-value-name"),
            this.createContext()
        );
    }

    @Test
    public void testLoadExisting() {
        final StoragePath path = StoragePath.parse("/magic-integer");

        this.loadAndCheck(
            this.createStorage(),
            path,
            this.createContext(),
            StorageValue.with(
                path,
                Optional.of(MAGIC_ENVIRONMENT_VALUE)
            )
        );
    }

    @Test
    public void testSaveFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> StorageSharedEnvironment.instance()
                .save(
                    StorageValue.with(
                        StoragePath.ROOT,
                        StorageValue.NO_VALUE
                    ),
                    this.createContext()
                )
        );
    }

    @Test
    public void testSaveInvalidEnvironmentValueNameFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> EnvironmentValueName.with(
                "!!-invalid-environment-value-name",
                Object.class
            )
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> StorageSharedEnvironment.instance()
                .save(
                    StorageValue.with(
                        StoragePath.parse("/!!-invalid-environment-value-name"),
                        StorageValue.NO_VALUE
                    ),
                    this.createContext()
                )
        );
    }

    @Test
    public void testSave() {
        final Integer value = 999;

        final StorageValue storageValue = StorageValue.with(
            StoragePath.parse("/magic-integer"),
            Optional.of(value)
        );

        final StorageContext context = this.createContext();

        this.saveAndCheck(
            this.createStorage(),
            storageValue,
            context,
            storageValue
        );

        this.environmentValueAndCheck(
            context,
            MAGIC_ENVIRONMENT_VALUE_NAME,
            value
        );
    }

    @Test
    public void testDeleteMissing() {
        this.deleteAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            this.createContext()
        );
    }

    @Test
    public void testDeleteUnknownEnvironmentValue() {
        this.deleteAndCheck(
            this.createStorage(),
            StoragePath.parse("/unknown-environment-value-name"),
            this.createContext()
        );
    }

    @Test
    public void testDeleteExisting() {
        final StoragePath path = StoragePath.parse("/magic-integer");

        final StorageSharedEnvironment<StorageContext> storage = this.createStorage();
        final StorageContext context = this.createContext();

        final StorageValue value = StorageValue.with(
            path,
            Optional.of(MAGIC_ENVIRONMENT_VALUE)
        );

        this.saveAndCheck(
            storage,
            value,
            context,
            value
        );

        this.deleteAndCheck(
            this.createStorage(),
            path,
            this.createContext()
        );
    }

    // /currency
    //  AuditInfo
    //    created
    //      user@example.com -999999999-01-01T00:00
    //    modified
    //      user@example.com -999999999-01-01T00:00
    //
    ///indentation
    //  AuditInfo
    //    created
    //      user@example.com -999999999-01-01T00:00
    //    modified
    //      user@example.com -999999999-01-01T00:00
    //
    ///lineEnding
    //  AuditInfo
    //    created
    //      user@example.com -999999999-01-01T00:00
    //    modified
    //      user@example.com -999999999-01-01T00:00
    //
    ///locale
    //  AuditInfo
    //    created
    //      user@example.com -999999999-01-01T00:00
    //    modified
    //      user@example.com -999999999-01-01T00:00
    //
    ///magic-integer
    //  AuditInfo
    //    created
    //      user@example.com -999999999-01-01T00:00
    //    modified
    //      user@example.com -999999999-01-01T00:00
    //
    ///now
    //  AuditInfo
    //    created
    //      user@example.com -999999999-01-01T00:00
    //    modified
    //      user@example.com -999999999-01-01T00:00
    //
    ///timeOffset
    //  AuditInfo
    //    created
    //      user@example.com -999999999-01-01T00:00
    //    modified
    //      user@example.com -999999999-01-01T00:00
    //

    /// user
    //  AuditInfo
    //    created
    //      user@example.com -999999999-01-01T00:00
    //    modified
    //      user@example.com -999999999-01-01T00:00
    @Test
    public void testListAll() {
        this.listAndCheck(
            StorageSharedEnvironment.instance(),
            StoragePath.ROOT,
            0,
            999,
            this.createContext(),
            EnvironmentValueName.CURRENCY,
            EnvironmentValueName.INDENTATION,
            EnvironmentValueName.LINE_ENDING,
            EnvironmentValueName.LOCALE,
            MAGIC_ENVIRONMENT_VALUE_NAME,
            EnvironmentValueName.NOW,
            EnvironmentValueName.TIME_OFFSET,
            EnvironmentValueName.USER
        );
    }

    @Test
    public void testListWithOffsetAndCount() {
        this.listAndCheck(
            StorageSharedEnvironment.instance(),
            StoragePath.ROOT,
            1,
            2,
            this.createContext(),
            //EnvironmentValueName.CURRENCY,
            EnvironmentValueName.INDENTATION,
            EnvironmentValueName.LINE_ENDING
            //EnvironmentValueName.LOCALE,
            //MAGIC_ENVIRONMENT_VALUE_NAME,
            //EnvironmentValueName.NOW,
            //EnvironmentValueName.TIME_OFFSET,
            //EnvironmentValueName.USER
        );
    }

    @Test
    public void testListWithPrefix() {
        this.listAndCheck(
            StorageSharedEnvironment.instance(),
            StoragePath.parse("/cur"),
            0,
            999,
            this.createContext(),
            EnvironmentValueName.CURRENCY
        );
    }

    @Test
    public void testListWithPrefix2() {
        this.listAndCheck(
            StorageSharedEnvironment.instance(),
            StoragePath.parse("/l"),
            0,
            999,
            this.createContext(),
            EnvironmentValueName.LINE_ENDING,
            EnvironmentValueName.LOCALE
        );
    }

    private void listAndCheck(final StorageSharedEnvironment<StorageContext> storage,
                              final StoragePath path,
                              final int offset,
                              final int count,
                              final StorageContext context,
                              final EnvironmentValueName<?>... environmentValueNames) {
        this.listAndCheck(
            storage,
            path,
            offset,
            count,
            context,
            Arrays.stream(environmentValueNames)
                .map(StorageSharedEnvironmentTest::storageValueInfo)
                .collect(Collectors.toList())
        );
    }

    private static StorageValueInfo storageValueInfo(final EnvironmentValueName<?> name) {
        return StorageValueInfo.with(
            StoragePath.ROOT.append(
                StorageName.with(
                    name.value()
                )
            ),
            AuditInfo.create(
                USER,
                NOW
            )
        );
    }

    @Override
    public StorageSharedEnvironment<StorageContext> createStorage() {
        return StorageSharedEnvironment.instance();
    }

    @Override
    public StorageContext createContext() {
        final Locale locale = Locale.forLanguageTag("en-AU");

        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                Currency.getInstance(locale),
                Indentation.SPACES4,
                LineEnding.NL,
                locale,
                () -> NOW,
                Optional.ofNullable(USER)
            )
        );

        environmentContext.setEnvironmentValue(
            MAGIC_ENVIRONMENT_VALUE_NAME,
            MAGIC_ENVIRONMENT_VALUE
        );

        return StorageContexts.basic(environmentContext);
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            StorageSharedEnvironment.instance(),
            StorageSharedEnvironment.class.getSimpleName()
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageSharedEnvironment<StorageContext>> type() {
        return Cast.to(StorageSharedEnvironment.class);
    }
}
