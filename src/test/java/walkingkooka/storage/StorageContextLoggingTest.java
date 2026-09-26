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
import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.ConverterLike;
import walkingkooka.convert.Converters;
import walkingkooka.currency.CurrencyLocaleContextTesting;
import walkingkooka.datetime.DateTimeContextTesting;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.logging.CanLogs;
import walkingkooka.logging.LoggingLevel;
import walkingkooka.math.DecimalNumberContextTesting;
import walkingkooka.text.printer.Printers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageContextLoggingTest implements StorageContextTesting2<StorageContextLogging>,
    CurrencyLocaleContextTesting,
    DateTimeContextTesting,
    DecimalNumberContextTesting,
    HashCodeEqualsDefinedTesting2<StorageContextLogging> {

    private final static LoggingLevel LOGGING_LEVEL = LoggingLevel.DEBUG;

    private final static ConverterLike CONVERTER_LIKE = ConverterContexts.basic(
        false, // canNumbersHaveGroupSeparator
        Converters.EXCEL_1904_DATE_SYSTEM_OFFSET,
        ',', // valueSeparator
        Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString(),
        BinaryNumberConverterFunctions.fake(), // multiplier
        BINARY_TEXT_CONTEXT,
        CURRENCY_LOCALE_CONTEXT,
        DATE_TIME_CONTEXT,
        DECIMAL_NUMBER_CONTEXT
    );

    private final static StoragePath STORAGE_PATH = StoragePath.parse("/value111");

    private final static StorageValue STORAGE_VALUE = StorageValue.with(STORAGE_PATH)
        .setValue(
            Optional.of(111)
        );

    private final static StoragePath DIFFERENT_STORAGE_PATH = StoragePath.parse("/value222");

    private final static StorageValue DIFFERENT_STORAGE_VALUE = StorageValue.with(DIFFERENT_STORAGE_PATH)
        .setValue(
            Optional.of(222)
        );

    @Test
    public void testWithNullStorageContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageContextLogging.with(
                null
            )
        );
    }

    // canReadStorage...................................................................................................

    @Test
    public void testCanReadStorage() {
        final StringBuilder logs = new StringBuilder();

        this.canReadStorageAndCheck(
            this.createContext(logs),
            STORAGE_PATH,
            true
        );

        this.checkEquals(
            "DEBUG fire value change null to currentWorkingDirectory=/current1/working2/directory3\n" +
                "DEBUG fire value change null to homeDirectory=/users/user123@example.com\n" +
                "storage INFO canRead /value111\n" +
                "storage INFO canRead /value111=true\n",
            logs.toString()
        );
    }

    // canWriteStorage...................................................................................................

    @Test
    public void testCanWriteStorage() {
        final StringBuilder logs = new StringBuilder();

        this.canWriteStorageAndCheck(
            this.createContext(logs),
            STORAGE_PATH,
            true
        );

        this.checkEquals(
            "DEBUG fire value change null to currentWorkingDirectory=/current1/working2/directory3\n" +
                "DEBUG fire value change null to homeDirectory=/users/user123@example.com\n" +
                "storage INFO canWrite /value111\n" +
                "storage INFO canWrite /value111=true\n",
            logs.toString()
        );
    }

    // loadStorage......................................................................................................

    @Test
    public void testLoadStorage() {
        final StringBuilder logs = new StringBuilder();

        this.loadStorageAndCheck(
            this.createContext(logs),
            STORAGE_PATH,
            STORAGE_VALUE
        );

        this.checkEquals(
            "DEBUG fire value change null to currentWorkingDirectory=/current1/working2/directory3\n" +
                "DEBUG fire value change null to homeDirectory=/users/user123@example.com\n" +
                "storage INFO load /value111\n" +
                "storage INFO load /value111=Optional[/value111=111]\n",
            logs.toString()
        );
    }

    // saveStorage......................................................................................................

    @Test
    public void testSaveStorage() {
        final StringBuilder logs = new StringBuilder();

        this.saveStorageAndCheck(
            this.createContext(logs),
            STORAGE_VALUE,
            STORAGE_VALUE
        );

        this.checkEquals(
            "DEBUG fire value change null to currentWorkingDirectory=/current1/working2/directory3\n" +
                "DEBUG fire value change null to homeDirectory=/users/user123@example.com\n" +
                "storage INFO save /value111=111\n",
            logs.toString()
        );
    }


    // deleteStorage....................................................................................................

    @Test
    public void testDeleteStorage() {
        final StringBuilder logs = new StringBuilder();

        this.createContext(logs)
            .deleteStorage(STORAGE_PATH);

        this.checkEquals(
            "DEBUG fire value change null to currentWorkingDirectory=/current1/working2/directory3\n" +
                "DEBUG fire value change null to homeDirectory=/users/user123@example.com\n" +
                "storage INFO delete /value111\n",
            logs.toString()
        );
    }

    // setAuditInfoStorage..............................................................................................

    @Test
    public void testSetAuditInfoStorage() {
        final StringBuilder logs = new StringBuilder();
        final StorageContextLogging context = this.createContext(logs);

        final StorageValueInfo info = StorageValueInfo.with(
            STORAGE_PATH,
            DIFFERENT_AUDIT_INFO
        );

        context.setAuditInfoStorage(info);

        this.listStorageAndCheck(
            context,
            StoragePath.ROOT,
            0,
            2,
            info
        );

        this.checkEquals(
            "DEBUG fire value change null to currentWorkingDirectory=/current1/working2/directory3\n" +
                "DEBUG fire value change null to homeDirectory=/users/user123@example.com\n" +
                "storage INFO setAuditInfo /value111 different-user-456@example.com 2000-01-31T12:58:59 different-user-456@example.com 2000-01-31T12:58:59\n" +
                "storage INFO list / 0 2\n" +
                "storage INFO list / 0 2 [/value111 different-user-456@example.com 2000-01-31T12:58:59 different-user-456@example.com 2000-01-31T12:58:59]\n",
            logs.toString()
        );
    }

    // addStorageWatcherXXX.............................................................................................

    @Test
    public void testAddStorageWatcher() {
        final StringBuilder logs = new StringBuilder();
        final StorageContextLogging context = this.createContext(logs);

        context.addStorageWatcher(
            new StorageWatcher() {
                @Override
                public void onValueChange(final Optional<StorageValue> oldValue,
                                          final Optional<StorageValue> newValue) {
                    // nop
                }

                @Override
                public String toString() {
                    return "WATCHER123";
                }
            }
        );

        context.saveStorage(DIFFERENT_STORAGE_VALUE);

        this.checkEquals(
            "DEBUG fire value change null to currentWorkingDirectory=/current1/working2/directory3\n" +
                "DEBUG fire value change null to homeDirectory=/users/user123@example.com\n" +
                "storage INFO addWatcher WATCHER123\n" +
                "storage INFO save /value222=222\n",
            logs.toString()
        );
    }

    @Test
    public void testAddStorageWatcherOnce() {
        final StringBuilder logs = new StringBuilder();
        final StorageContextLogging context = this.createContext(logs);

        context.addStorageWatcherOnce(
            new StorageWatcher() {
                @Override
                public void onValueChange(final Optional<StorageValue> oldValue,
                                          final Optional<StorageValue> newValue) {
                    // nop
                }

                @Override
                public String toString() {
                    return "WATCHER123";
                }
            }
        );

        context.saveStorage(DIFFERENT_STORAGE_VALUE);

        this.checkEquals(
            "DEBUG fire value change null to currentWorkingDirectory=/current1/working2/directory3\n" +
                "DEBUG fire value change null to homeDirectory=/users/user123@example.com\n" +
                "storage INFO addWatcherOnce WATCHER123\n" +
                "storage INFO save /value222=222\n",
            logs.toString()
        );
    }

    // cloneEnvironmentContext..........................................................................................

    @Test
    public void testCloneEnvironmentContext() {
        final StorageContext storageContext = STORAGE_CONTEXT.cloneEnvironment();

        final StorageContextLogging storageContextLogging = StorageContextLogging.with(storageContext);

        final StorageContext cloned = storageContextLogging.cloneEnvironment();

        this.setHomeDirectoryAndCheck(
            cloned,
            DIFFERENT_HOME_DIRECTORY
        );

        this.homeDirectoryAndCheck(
            storageContextLogging,
            HOME_DIRECTORY
        );
    }

    // setEnvironmentContext............................................................................................

    @Test
    public void testSetEnvironmentContextWithSameEnvironmentContext() {
        final StorageContextLogging context = this.createContext();

        this.setEnvironmentContextAndCheck(
            context,
            context
        );
    }

    // currentWorkingDirectory..........................................................................................

    @Test
    public void testCurrentWorkingDirectory() {
        this.currentWorkingDirectoryAndCheck(
            this.createContext(),
            CURRENT_WORKING_DIRECTORY
        );
    }

    @Test
    public void testSetCurrentWorkingDirectoryWithSame() {
        this.setCurrentWorkingDirectoryAndCheck(
            this.createContext(),
            CURRENT_WORKING_DIRECTORY
        );
    }

    @Test
    public void testSetCurrentWorkingDirectoryWithDifferent() {
        this.setCurrentWorkingDirectoryAndCheck(
            this.createContext(),
            DIFFERENT_CURRENT_WORKING_DIRECTORY
        );
    }

    // homeDirectory..................................................................................................

    @Test
    public void testHomeDirectory() {
        this.homeDirectoryAndCheck(
            this.createContext(),
            HOME_DIRECTORY
        );
    }

    @Test
    public void testSetHomeDirectoryWithSame() {
        this.setHomeDirectoryAndCheck(
            this.createContext(),
            HOME_DIRECTORY
        );
    }

    @Test
    public void testSetHomeDirectoryWithDifferent() {
        this.setHomeDirectoryAndCheck(
            this.createContext(),
            DIFFERENT_HOME_DIRECTORY
        );
    }

    @Test
    @Override
    public void testLoggingLevel() {
        this.loggingLevelAndCheck(
            this.createContext(),
            LOGGING_LEVEL
        );
    }

    @Override
    public void testSetLoggingLevelWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    // ConverterLike....................................................................................................

    @Test
    public void testConvert() {
        this.convertAndCheck(
            this.createContext(),
            "A",
            Character.class,
            'A'
        );
    }

    @Override
    public StorageContextLogging createContext() {
        return this.createContext(
            new StringBuilder()
        );
    }

    private StorageContextLogging createContext(final StringBuilder logger) {
        final StorageEnvironmentContext storageEnvironmentContext = StorageEnvironmentContexts.basic(
            EnvironmentContexts.map(
                CanLogs.printer(
                    Printers.stringBuilder(
                        logger,
                        LINE_ENDING
                    )
                ),
                CHARSET,
                CURRENCY,
                INDENTATION,
                LINE_ENDING,
                LOCALE,
                LOGGING_LEVEL,
                HAS_NOW,
                OPTIONAL_USER
            )
        );
        storageEnvironmentContext.setCurrentWorkingDirectory(OPTIONAL_CURRENT_WORKING_DIRECTORY);
        storageEnvironmentContext.setHomeDirectory(OPTIONAL_HOME_DIRECTORY);

        final Storage<StorageContext> storage = Storages.treeMapStore();

        final StorageContextLogging context = StorageContextLogging.with(
            StorageContexts.basic(
                CONVERTER_LIKE,
                MEDIA_TYPE_DETECTOR,
                storage,
                storageEnvironmentContext
            )
        );

        storage.save(
            STORAGE_VALUE,
            context
        );

        return context;
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentStorageEnvironmentContext() {
        this.checkNotEquals(
            StorageContextLogging.with(
                StorageContexts.fake()
            )
        );
    }

    @Override
    public StorageContextLogging createObject() {
        return StorageContextLogging.with(
            STORAGE_CONTEXT
        );
    }

    // HasEnvironmentContext............................................................................................

    @Test
    @Override
    public void testEnvironmentContext() {
        final StorageContextLogging context = this.createContext();

        this.environmentContextAndCheck(
            context,
            context.storageContext()
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageContextLogging> type() {
        return StorageContextLogging.class;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}
