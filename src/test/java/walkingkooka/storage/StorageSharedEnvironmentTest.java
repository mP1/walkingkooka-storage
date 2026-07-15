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
import walkingkooka.InvalidCharacterException;
import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.currency.FakeCurrencyContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.environment.AuditInfo;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextTesting;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.header.MediaTypeDetectors;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.text.TextPrinting;

import java.math.MathContext;
import java.nio.charset.StandardCharsets;
import java.text.DateFormatSymbols;
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
    public void testCanReadRoot() {
        this.canReadAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            this.createContext(),
            false
        );
    }

    @Test
    public void testCanReadMissingEnvironmentValueName() {
        this.canReadAndCheck(
            this.createStorage(),
            StoragePath.parse("/!Invalid"),
            this.createContext(),
            false
        );
    }

    @Test
    public void testCanReadEnvironmentValueName() {
        this.canReadAndCheck(
            this.createStorage(),
            StoragePath.parse("/" + MAGIC_ENVIRONMENT_VALUE_NAME),
            this.createContext(),
            true
        );
    }

    @Test
    public void testCanWriteRootFails() {
        assertThrows(
            InvalidStoragePathException.class,
            () -> this.createStorage()
                .canWrite(
                    StoragePath.ROOT,
                    this.createContext()
                )
        );
    }

    @Test
    public void testCanWriteInvalidEnvironmentValueName() {
        assertThrows(
            InvalidCharacterException.class,
            () -> this.createStorage()
                .canWrite(
                    StoragePath.parse("/!Invalid"),
                    this.createContext()
                )
        );
    }

    @Test
    public void testCanWriteEnvironmentValueName() {
        this.canWriteAndCheck(
            this.createStorage(),
            StoragePath.parse("/" + MAGIC_ENVIRONMENT_VALUE_NAME),
            this.createContext(),
            true
        );
    }

    @Test
    public void testCanWriteNewEnvironmentValueName() {
        this.canWriteAndCheck(
            this.createStorage(),
            StoragePath.parse("/hello"),
            this.createContext(),
            true
        );
    }

    @Test
    public void testLoadMissing() {
        this.loadAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            this.createContext()
        );
    }

    @Test
    public void testLoadDoublePath() {
        this.loadAndCheck(
            this.createStorage(),
            StoragePath.parse("/path1/path2"),
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
            StorageValue.with(path)
                    .setValue(
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
                    StorageValue.with(StoragePath.ROOT),
                    this.createContext()
                )
        );
    }

    @Test
    public void testSaveInvalidPathFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> StorageSharedEnvironment.instance()
                .save(
                    StorageValue.with(
                        StoragePath.parse("/path1/" + MAGIC_ENVIRONMENT_VALUE_NAME)
                    ).setValue(
                        Optional.of(MAGIC_ENVIRONMENT_VALUE)
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
                        StoragePath.parse("/!!-invalid-environment-value-name")
                    ),
                    this.createContext()
                )
        );
    }

    @Test
    public void testSave() {
        final Integer value = 999;

        final StorageValue storageValue = StorageValue.with(
            StoragePath.parse("/magic-integer")
        ).setValue(
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
            StoragePath.parse("/missing123"),
            this.createContext()
        );
    }

    @Test
    public void testDeleteInvalidPathFails() {
        assertThrows(
            InvalidStoragePathException.class,
            () -> this.createStorage()
                .delete(
                    StoragePath.parse("/path1/" + MAGIC_ENVIRONMENT_VALUE_NAME),
                    this.createContext()
                )
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

        final StorageValue value = StorageValue.with(path)
            .setValue(
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
            EnvironmentValueName.CHARSET,
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
            2,
            2,
            this.createContext(),
            //EnvironmentValueName.CHARSET,
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

    // addWatcher.......................................................................................................

    private final static StoragePath LINE_ENDING_STORAGE_PATH = StoragePath.parse("/lineEnding");

    @Test
    public void testAddWatcher() {
        final StorageSharedEnvironment<StorageContext> storage = this.createStorage();
        final StorageContext context = this.createContext();

        this.fired = false;

        final LineEnding lineEnding = LineEnding.CRNL;

        this.checkNotEquals(
            LINE_ENDING,
            lineEnding
        );

        storage.addWatcher(
            new StorageWatcher() {
                @Override
                public void onValueChange(final Optional<StorageValue> oldValue,
                                          final Optional<StorageValue> newValue) {
                    checkEquals(
                        Optional.of(
                            StorageValue.with(LINE_ENDING_STORAGE_PATH)
                                .setValue(
                                    Optional.of(LINE_ENDING)
                                )
                        ),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(
                            StorageValue.with(LINE_ENDING_STORAGE_PATH)
                                .setValue(
                                    Optional.of(lineEnding)
                                )
                        ),
                        newValue,
                        "newValue"
                    );

                    fired = true;
                }
            },
            context
        );

        context.setLineEnding(lineEnding);

        this.checkEquals(
            true,
            this.fired
        );
    }

    @Test
    public void testAddWatcherOnce() {
        final StorageSharedEnvironment<StorageContext> storage = this.createStorage();
        final StorageContext context = this.createContext();

        this.fired = false;

        final LineEnding lineEnding = LineEnding.CRNL;

        this.checkNotEquals(
            LINE_ENDING,
            lineEnding
        );

        storage.addWatcherOnce(
            new StorageWatcher() {
                @Override
                public void onValueChange(final Optional<StorageValue> oldValue,
                                          final Optional<StorageValue> newValue) {
                    checkEquals(
                        Optional.of(
                            StorageValue.with(LINE_ENDING_STORAGE_PATH)
                                .setValue(
                                    Optional.of(LINE_ENDING)
                                )
                        ),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(
                            StorageValue.with(LINE_ENDING_STORAGE_PATH)
                                .setValue(
                                    Optional.of(lineEnding)
                                )
                        ),
                        newValue,
                        "newValue"
                    );

                    fired = true;
                }
            },
            context
        );

        context.setLineEnding(lineEnding);
        context.setLineEnding(LINE_ENDING);

        this.checkEquals(
            true,
            this.fired
        );
    }

    private boolean fired;

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
        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                StandardCharsets.UTF_8,
                Currency.getInstance(LOCALE),
                Indentation.SPACES4,
                LINE_ENDING,
                LOCALE,
                () -> NOW,
                Optional.ofNullable(USER)
            )
        );

        environmentContext.setEnvironmentValue(
            MAGIC_ENVIRONMENT_VALUE_NAME,
            MAGIC_ENVIRONMENT_VALUE
        );

        return StorageContexts.basic(
            ConverterContexts.basic(
                false, // canNumbersHaveGroupSeparator
                Converters.EXCEL_1904_DATE_SYSTEM_OFFSET,
                ',', // valueSeparator
                Converters.fake(),
                BinaryNumberConverterFunctions.fake(), // multiplier
                TextPrinting.with(
                    Indentation.SPACES2,
                    LINE_ENDING
                ).setCharset(StandardCharsets.UTF_8),
                new FakeCurrencyContext() {
                    @Override
                    public Optional<Currency> currencyForLocale(final Locale locale) {
                        return Optional.of(
                            Currency.getInstance(locale)
                        );
                    }
                }.setLocaleContext(
                    LocaleContexts.jre(LOCALE)
                ),
                DateTimeContexts.basic(
                    DateTimeSymbols.fromDateFormatSymbols(
                        new DateFormatSymbols(LOCALE)
                    ),
                    LOCALE,
                    1920,
                    20,
                    LocalDateTime::now
                ),
                DecimalNumberContexts.american(MathContext.DECIMAL32)
            ),
            MediaTypeDetectors.fake(),
            environmentContext
        );
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
