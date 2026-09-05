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

package walkingkooka.storage.currency;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.currency.CurrencyCode;
import walkingkooka.currency.CurrencyExchange;
import walkingkooka.currency.CurrencyExchangeRaterTesting2;
import walkingkooka.currency.FakeCurrencyExchangeRaterContext;
import walkingkooka.net.header.MediaTypeDetectorTesting;
import walkingkooka.props.Properties;
import walkingkooka.storage.FakeStorageContext;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageContext;
import walkingkooka.storage.StorageContexts;
import walkingkooka.storage.StorageEnvironmentContextTesting;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageWatcher;
import walkingkooka.storage.Storages;

import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageCurrencyExchangeRaterStoragePathPropertiesTest implements CurrencyExchangeRaterTesting2<StorageCurrencyExchangeRaterStoragePathProperties<FakeCurrencyExchangeRaterContext>, FakeCurrencyExchangeRaterContext>,
    HashCodeEqualsDefinedTesting2<StorageCurrencyExchangeRaterStoragePathProperties>,
    MediaTypeDetectorTesting,
    StorageEnvironmentContextTesting {

    private final static StoragePath STORAGE_PATH = StoragePath.parse("/currency-exchange-rates.properties");

    private final static Function<String, Number> NUMBER_PARSER = Double::parseDouble;

    private final static StorageContext STORAGE_CONTEXT = new FakeStorageContext() {

        @Override
        public Optional<StorageValue> loadStorage(final StoragePath path) {
            return Optional.empty();
        }

        @Override
        public Runnable addStorageWatcher(final StorageWatcher watcher) {
            return StorageWatcher.NOTHING_REMOVER;
        }
    };

    private final static Properties PROPERTIES = Properties.parse(
        "AUD-NZD=0.9"
    );

    private final static CurrencyCode AUD = CurrencyCode.parse("AUD");

    private final static CurrencyCode NZD = CurrencyCode.parse("NZD");

    // with.............................................................................................................

    @Test
    public void testWithNullStoragePathFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageCurrencyExchangeRaterStoragePathProperties.with(
                null,
                NUMBER_PARSER,
                STORAGE_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullNumberParserFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageCurrencyExchangeRaterStoragePathProperties.with(
                STORAGE_PATH,
                null,
                STORAGE_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullStorageContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageCurrencyExchangeRaterStoragePathProperties.with(
                STORAGE_PATH,
                NUMBER_PARSER,
                null
            )
        );
    }

    @Test
    public void testCurrencyExchanges() {
        this.currencyExchangesAndCheck(
            this.createCurrencyExchangeRater(),
            this.createContext(),
            CurrencyExchange.with(
                AUD,
                NZD
            )
        );
    }

    @Test
    public void testCurrencyExchangeRate() {
        this.currencyExchangeRateAndCheck(
            this.createCurrencyExchangeRater(),
            CurrencyExchange.with(
                AUD,
                NZD
            ),
            this.createContext(),
            0.9
        );
    }

    @Test
    public void testCurrencyExchangeRateAfterPropertiesChange() {
        final StorageContext storageContext = this.createStorageContext();

        final StorageCurrencyExchangeRaterStoragePathProperties currencyExchangeRater = this.createCurrencyExchangeRater(
            storageContext
        );

        storageContext.saveStorage(
            StorageValue.with(STORAGE_PATH)
                .setValue(
                    Optional.of(
                        Properties.parse(
                            "AUD-NZD=0.8"
                        )
                    )
                )
        );

        this.currencyExchangeRateAndCheck(
            currencyExchangeRater,
            CurrencyExchange.with(
                AUD,
                NZD
            ),
            this.createContext(),
            0.8
        );
    }

    @Test
    public void testCurrencyExchangeRateAfterPropertiesDeleted() {
        final StorageContext storageContext = this.createStorageContext();

        final StorageCurrencyExchangeRaterStoragePathProperties currencyExchangeRater = this.createCurrencyExchangeRater(
            storageContext
        );

        storageContext.deleteStorage(STORAGE_PATH);

        this.currencyExchangeRateAndCheck(
            currencyExchangeRater,
            CurrencyExchange.with(
                AUD,
                NZD
            ),
            this.createContext()
        );
    }

    @Override
    public StorageCurrencyExchangeRaterStoragePathProperties createCurrencyExchangeRater() {
        return this.createCurrencyExchangeRater(
            this.createStorageContext()
        );
    }

    private StorageContext createStorageContext() {
        final Storage<StorageContext> storage = Storages.treeMapStore();

        final StorageContext context = StorageContexts.basic(
            ConverterContexts.fake(), // ConverterLike
            MEDIA_TYPE_DETECTOR,
            storage,
            STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment()
        );

        context.saveStorage(
            StorageValue.with(STORAGE_PATH)
                .setValue(
                    Optional.of(PROPERTIES)
                )
        );

        return context;
    }

    private StorageCurrencyExchangeRaterStoragePathProperties createCurrencyExchangeRater(final StorageContext context) {
        return StorageCurrencyExchangeRaterStoragePathProperties.with(
            STORAGE_PATH,
            NUMBER_PARSER,
            context
        );
    }

    @Override
    public FakeCurrencyExchangeRaterContext createContext() {
        return new FakeCurrencyExchangeRaterContext();
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsWithDifferentStoragePath() {
        this.checkNotEquals(
            StorageCurrencyExchangeRaterStoragePathProperties.with(
                StoragePath.parse("/different"),
                NUMBER_PARSER,
                STORAGE_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsWithDifferentNumberParser() {
        this.checkNotEquals(
            StorageCurrencyExchangeRaterStoragePathProperties.with(
                STORAGE_PATH,
                Double::parseDouble,
                STORAGE_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsWithDifferentStorageContext() {
        this.checkNotEquals(
            StorageCurrencyExchangeRaterStoragePathProperties.with(
                STORAGE_PATH,
                NUMBER_PARSER,
                new FakeStorageContext() {

                    @Override
                    public Optional<StorageValue> loadStorage(final StoragePath path) {
                        return Optional.empty();
                    }

                    @Override
                    public Runnable addStorageWatcher(final StorageWatcher watcher) {
                        return StorageWatcher.NOTHING_REMOVER;
                    }
                }
            )
        );
    }

    @Override
    public StorageCurrencyExchangeRaterStoragePathProperties createObject() {
        return StorageCurrencyExchangeRaterStoragePathProperties.with(
            STORAGE_PATH,
            NUMBER_PARSER,
            STORAGE_CONTEXT
        );
    }
}
