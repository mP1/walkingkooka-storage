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
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.math.DecimalNumberContextTesting;
import walkingkooka.net.header.MediaTypeDetectors;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicStorageContextTest implements StorageContextTesting2<BasicStorageContext>,
    CurrencyLocaleContextTesting,
    DateTimeContextTesting,
    DecimalNumberContextTesting,
    HashCodeEqualsDefinedTesting2<BasicStorageContext> {

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

    @Test
    public void testWithNullConverterLikeFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicStorageContext.with(
                null,
                MEDIA_TYPE_DETECTOR,
                STORAGE,
                ENVIRONMENT_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullMediaTypeDetectorFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicStorageContext.with(
                CONVERTER_LIKE,
                null,
                STORAGE,
                ENVIRONMENT_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullStoragetFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicStorageContext.with(
                CONVERTER_LIKE,
                MEDIA_TYPE_DETECTOR,
                null,
                ENVIRONMENT_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicStorageContext.with(
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

    // loadStorage......................................................................................................

    @Test
    public void testLoadStorage() {
        this.loadStorageAndCheck(
            this.createContext(),
            STORAGE_PATH,
            STORAGE_VALUE
        );
    }

    // setEnvironmentContext............................................................................................

    @Test
    public void testSetEnvironmentContextWithSameBasicStorageContext() {
        final BasicStorageContext context = this.createContext();

        assertSame(
            context,
            context.setEnvironmentContext(context)
        );
    }

    @Test
    public void testSetEnvironmentContextWithSameEnvironmentContext() {
        final BasicStorageContext context = this.createContext();

        assertSame(
            context,
            context.setEnvironmentContext(
                context.environmentContext()
            )
        );
    }

    @Test
    public void testSetEnvironmentContextWithDifferentEnvironmentContext() {
        final BasicStorageContext context = this.createContext();

        final StorageContext after = context.setEnvironmentContext(DIFFERENT_ENVIRONMENT_CONTEXT);
        assertNotSame(
            context,
            after
        );

        this.checkEquals(
            BasicStorageContext.with(
                CONVERTER_LIKE,
                MEDIA_TYPE_DETECTOR,
                context.storage,
                DIFFERENT_ENVIRONMENT_CONTEXT
            ),
            after
        );
    }

    @Test
    public void testSetEnvironmentContextWithBasicStorageContextWithDifferentEnvironmentContext() {
        final BasicStorageContext context = this.createContext();

        final StorageContext after = context.setEnvironmentContext(
            BasicStorageContext.with(
                CONVERTER_LIKE,
                MEDIA_TYPE_DETECTOR,
                STORAGE,
                DIFFERENT_ENVIRONMENT_CONTEXT
            )
        );
        assertNotSame(
            context,
            after
        );

        this.checkEquals(
            BasicStorageContext.with(
                CONVERTER_LIKE,
                MEDIA_TYPE_DETECTOR,
                context.storage,
                DIFFERENT_ENVIRONMENT_CONTEXT
            ),
            after
        );
    }

    @Test
    public void testSetEnvironmentContextWithBasicStorageContextWithDifferentEnvironmentContext2() {
        final BasicStorageContext context = this.createContext();

        final StorageContext after = context.setEnvironmentContext(
            BasicStorageContext.with(
                CONVERTER_LIKE,
                MediaTypeDetectors.fake(),
                STORAGE,
                DIFFERENT_ENVIRONMENT_CONTEXT
            )
        );
        assertNotSame(
            context,
            after
        );

        this.checkEquals(
            BasicStorageContext.with(
                CONVERTER_LIKE,
                MEDIA_TYPE_DETECTOR,
                context.storage,
                DIFFERENT_ENVIRONMENT_CONTEXT
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
    public BasicStorageContext createContext() {
        final EnvironmentContext environmentContext = ENVIRONMENT_CONTEXT.cloneEnvironment();

        StorageContext.CURRENT_WORKING_DIRECTORY.setEnvironmentValue(
            CURRENT_WORKING_DIRECTORY,
            environmentContext
        );

        StorageContext.HOME_DIRECTORY.setEnvironmentValue(
            HOME_DIRECTORY,
            environmentContext
        );

        final Storage<StorageContext> storage = Storages.treeMapStore();

        final BasicStorageContext context = BasicStorageContext.with(
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
            BasicStorageContext.with(
                ConverterContexts.fake(),
                MEDIA_TYPE_DETECTOR,
                STORAGE,
                ENVIRONMENT_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentMediaTypeDetector() {
        this.checkNotEquals(
            BasicStorageContext.with(
                CONVERTER_LIKE,
                MediaTypeDetectors.fake(),
                STORAGE,
                ENVIRONMENT_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentStorage() {
        this.checkNotEquals(
            BasicStorageContext.with(
                CONVERTER_LIKE,
                MEDIA_TYPE_DETECTOR,
                Storages.fake(),
                ENVIRONMENT_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentEnvironmentContext() {
        this.checkNotEquals(
            BasicStorageContext.with(
                CONVERTER_LIKE,
                MEDIA_TYPE_DETECTOR,
                STORAGE,
                EnvironmentContexts.fake()
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
