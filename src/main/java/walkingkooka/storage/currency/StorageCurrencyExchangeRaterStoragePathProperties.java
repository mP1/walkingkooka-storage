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

import walkingkooka.currency.CurrencyExchange;
import walkingkooka.currency.CurrencyExchangeRater;
import walkingkooka.currency.CurrencyExchangeRaterContext;
import walkingkooka.currency.CurrencyExchangeRaters;
import walkingkooka.props.Properties;
import walkingkooka.storage.StorageContext;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageWatcher;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

final class StorageCurrencyExchangeRaterStoragePathProperties<C extends CurrencyExchangeRaterContext> implements CurrencyExchangeRater<C> {

    static <C extends CurrencyExchangeRaterContext> StorageCurrencyExchangeRaterStoragePathProperties<C> with(final StoragePath storagePath,
                                                                                                              final Function<String, Number> numberParser,
                                                                                                              final StorageContext storageContext) {
        return new StorageCurrencyExchangeRaterStoragePathProperties<>(
            Objects.requireNonNull(storagePath, "storagePath"),
            Objects.requireNonNull(numberParser, "numberParser"),
            Objects.requireNonNull(storageContext, "storageContext")
        );
    }

    private StorageCurrencyExchangeRaterStoragePathProperties(final StoragePath storagePath,
                                                              final Function<String, Number> numberParser,
                                                              final StorageContext storageContext) {
        super();

        this.storagePath = storagePath;
        this.numberParser = numberParser;

        storageContext.addStorageWatcher(
            new StorageWatcher() {
                @Override
                public void onValueChange(final Optional<StorageValue> oldValue,
                                          final Optional<StorageValue> newValue) {
                    StorageCurrencyExchangeRaterStoragePathProperties.this.setProperties(newValue);
                }

                @Override
                public String toString() {
                    return StorageCurrencyExchangeRaterStoragePathProperties.this.toString();
                }
            }
        );

        // pre-load
        this.setProperties(
            storageContext.loadStorage(storagePath)
        );
    }

    // CurrencyExchangeRater............................................................................................

    @Override
    public Set<CurrencyExchange> currencyExchanges(final C context) {
        return this.properties.currencyExchanges(context);
    }

    @Override
    public Optional<Number> currencyExchangeRate(final CurrencyExchange currencyExchange,
                                                 final Optional<LocalDateTime> dateTime,
                                                 final C context) {
        return this.properties.currencyExchangeRate(
            currencyExchange,
            dateTime,
            context
        );
    }

    private final StoragePath storagePath;

    // StorageWatcher...................................................................................................

    private void setProperties(final Optional<StorageValue> value) {
        Objects.requireNonNull(value, "value");

        this.properties = CurrencyExchangeRaters.properties(
            value.map(
                (StorageValue storageValue) -> (Properties) storageValue.value()
                    .orElse(Properties.EMPTY)
            ).orElse(
                Properties.EMPTY
            ),
            this.numberParser
        );
    }

    private final Function<String, Number> numberParser;

    private CurrencyExchangeRater<C> properties;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.storagePath + " " + this.properties;
    }
}
