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
import walkingkooka.math.DecimalNumberContextTesting;
import walkingkooka.net.header.MediaTypeDetectors;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageContextBasicTest implements StorageContextTesting2<StorageContextBasic>,
    CurrencyLocaleContextTesting,
    DateTimeContextTesting,
    DecimalNumberContextTesting,
    HashCodeEqualsDefinedTesting2<StorageContextBasic> {

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

    private final static Storage<StorageContext> STORAGE = Storages.fake();

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
    public void testWithNullConverterLikeFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageContextBasic.with(
                null,
                MEDIA_TYPE_DETECTOR,
                STORAGE,
                STORAGE_ENVIRONMENT_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullMediaTypeDetectorFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageContextBasic.with(
                CONVERTER_LIKE,
                null,
                STORAGE,
                STORAGE_ENVIRONMENT_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullStorageFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageContextBasic.with(
                CONVERTER_LIKE,
                MEDIA_TYPE_DETECTOR,
                null,
                STORAGE_ENVIRONMENT_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullStorageEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageContextBasic.with(
                CONVERTER_LIKE,
                MEDIA_TYPE_DETECTOR,
                STORAGE,
                null
            )
        );
    }

    // canReadStorage...................................................................................................

    @Test
    public void testCanReadStorage() {
        this.canReadStorageAndCheck(
            this.createContext(),
            STORAGE_PATH,
            true
        );
    }

    // canWriteStorage...................................................................................................

    @Test
    public void testCanWriteStorage() {
        this.canWriteStorageAndCheck(
            this.createContext(),
            STORAGE_PATH,
            true
        );
    }

    @Test
    public void testCanWriteStorage2() {
        this.canWriteStorageAndCheck(
            StorageContextBasic.with(
                CONVERTER_LIKE,
                MEDIA_TYPE_DETECTOR,
                new FakeStorage<>() {
                    @Override
                    public boolean canWrite(final StoragePath path,
                                            final StorageContext context) {
                        checkEquals(
                            STORAGE_PATH,
                            path
                        );
                        return false;
                    }
                },
                STORAGE_ENVIRONMENT_CONTEXT
            ),
            STORAGE_PATH,
            false
        );
    }

    // loadStorage......................................................................................................

    @Test
    public void testLoadStorage() {
        this.loadStorageAndCheck(
            this.createContext(),
            STORAGE_PATH,
            STORAGE_VALUE
        );
    }

    // setAuditInfoStorage..............................................................................................

    @Test
    public void testSetAuditInfoStorage() {
        final StorageContextBasic context = this.createContext();

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
    }

    // addStorageWatcherXXX.............................................................................................

    @Test
    public void testAddStorageWatcher() {
        final StorageContextBasic context = this.createContext();

        context.addStorageWatcher(
            new StorageWatcher() {
                @Override
                public void onValueChange(final Optional<StorageValue> oldValue,
                                          final Optional<StorageValue> newValue) {
                    checkEquals(
                        Optional.empty(),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(DIFFERENT_STORAGE_VALUE),
                        newValue,
                        "newValue"
                    );

                    fired = true;
                }
            }
        );

        this.fired = false;

        context.saveStorage(DIFFERENT_STORAGE_VALUE);

        this.checkEquals(
            true,
            this.fired
        );
    }

    @Test
    public void testAddStorageWatcher2() {
        final StorageContextBasic context = this.createContext();

        final StorageValue lost = DIFFERENT_STORAGE_VALUE.setValue(
            Optional.of("lost")
        );

        context.saveStorage(lost);

        context.addStorageWatcher(
            new StorageWatcher() {
                @Override
                public void onValueChange(final Optional<StorageValue> oldValue,
                                          final Optional<StorageValue> newValue) {
                    checkEquals(
                        Optional.of(lost),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(DIFFERENT_STORAGE_VALUE),
                        newValue,
                        "newValue"
                    );

                    fired = true;
                }
            }
        );

        this.fired = false;

        context.saveStorage(
            DIFFERENT_STORAGE_VALUE
        );

        this.checkEquals(
            true,
            this.fired
        );
    }

    @Test
    public void testAddStorageWatcherOnce() {
        final StorageContextBasic context = this.createContext();

        final StorageValue lost = DIFFERENT_STORAGE_VALUE.setValue(
            Optional.of("lost")
        );

        context.saveStorage(lost);

        context.addStorageWatcherOnce(
            new StorageWatcher() {
                @Override
                public void onValueChange(final Optional<StorageValue> oldValue,
                                          final Optional<StorageValue> newValue) {
                    checkEquals(
                        Optional.of(lost),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(DIFFERENT_STORAGE_VALUE),
                        newValue,
                        "newValue"
                    );

                    fired = true;
                }
            }
        );

        this.fired = false;

        context.saveStorage(
            DIFFERENT_STORAGE_VALUE
        );

        this.checkEquals(
            true,
            this.fired
        );

        // if fired watcher checks will fail.
        context.deleteStorage(
            DIFFERENT_STORAGE_PATH
        );
    }

    private boolean fired;

    // cloneEnvironmentContext..........................................................................................

    @Test
    public void testCloneEnvironmentContext() {
        final StorageEnvironmentContext storageEnvironmentContext = STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final StorageContextBasic basicStorageContext = StorageContextBasic.with(
            CONVERTER_LIKE,
            MEDIA_TYPE_DETECTOR,
            STORAGE,
            storageEnvironmentContext
        );

        final StorageContext cloned = basicStorageContext.cloneEnvironment();

        this.setHomeDirectoryAndCheck(
            cloned,
            DIFFERENT_HOME_DIRECTORY
        );
    }

    // setEnvironmentContext............................................................................................

    @Test
    public void testSetEnvironmentContextWithSameEnvironmentContext() {
        final StorageContextBasic context = this.createContext();

        assertSame(
            context,
            context.setEnvironmentContext(
                context.environmentContext()
            )
        );
    }

    @Test
    public void testSetEnvironmentContextWithDifferentStorageEnvironmentContext() {
        final StorageContextBasic context = this.createContext();

        final StorageContext after = context.setEnvironmentContext(DIFFERENT_STORAGE_ENVIRONMENT_CONTEXT);
        assertNotSame(
            context,
            after
        );

        this.checkEquals(
            StorageContextBasic.with(
                CONVERTER_LIKE,
                MEDIA_TYPE_DETECTOR,
                context.storage,
                DIFFERENT_STORAGE_ENVIRONMENT_CONTEXT
            ),
            after
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

    // homeDirectory....................................................................................................

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
    public StorageContextBasic createContext() {
        final StorageEnvironmentContext environmentContext = STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final Storage<StorageContext> storage = Storages.treeMapStore();

        final StorageContextBasic context = StorageContextBasic.with(
            CONVERTER_LIKE,
            MEDIA_TYPE_DETECTOR,
            storage,
            environmentContext
        );

        storage.save(
            STORAGE_VALUE,
            context
        );

        return context;
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentConverterLike() {
        this.checkNotEquals(
            StorageContextBasic.with(
                ConverterContexts.fake(),
                MEDIA_TYPE_DETECTOR,
                STORAGE,
                STORAGE_ENVIRONMENT_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentMediaTypeDetector() {
        this.checkNotEquals(
            StorageContextBasic.with(
                CONVERTER_LIKE,
                MediaTypeDetectors.fake(),
                STORAGE,
                STORAGE_ENVIRONMENT_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentStorage() {
        this.checkNotEquals(
            StorageContextBasic.with(
                CONVERTER_LIKE,
                MEDIA_TYPE_DETECTOR,
                Storages.fake(),
                STORAGE_ENVIRONMENT_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentEnvironmentContext() {
        this.checkNotEquals(
            StorageContextBasic.with(
                CONVERTER_LIKE,
                MEDIA_TYPE_DETECTOR,
                STORAGE,
                DIFFERENT_STORAGE_ENVIRONMENT_CONTEXT
            )
        );
    }

    @Override
    public StorageContextBasic createObject() {
        return this.createContext();
    }

    // HasEnvironmentContext............................................................................................

    @Test
    @Override
    public void testEnvironmentContext() {
        final StorageContextBasic context = this.createContext();

        this.environmentContextAndCheck(
            context,
            context.storageEnvironmentContext
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageContextBasic> type() {
        return StorageContextBasic.class;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}
