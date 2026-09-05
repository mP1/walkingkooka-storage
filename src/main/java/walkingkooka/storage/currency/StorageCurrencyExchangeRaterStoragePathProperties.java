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

import walkingkooka.Cast;
import walkingkooka.currency.CurrencyExchange;
import walkingkooka.currency.CurrencyExchangeRater;
import walkingkooka.currency.CurrencyExchangeRaterContext;
import walkingkooka.currency.CurrencyExchangeRaters;
import walkingkooka.props.Properties;
import walkingkooka.storage.StorageContext;
import walkingkooka.storage.StoragePath;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * A {@link CurrencyExchangeRater} that watches the given path that should contain a {@link Properties}.
 */
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

        this.storageContext = storageContext;

        storageContext.statefulStorageValueChangeWatcher(
            storagePath,
            (Optional<Properties> oldValue, Optional<Properties> newValue) -> StorageCurrencyExchangeRaterStoragePathProperties.this.setProperties(newValue)
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

    private void setProperties(final Optional<Properties> value) {
        Objects.requireNonNull(value, "value");

        this.properties = CurrencyExchangeRaters.properties(
            value.orElse(
                Properties.EMPTY
            ),
            this.numberParser
        );
    }

    private final Function<String, Number> numberParser;

    private CurrencyExchangeRater<C> properties;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.storagePath,
            this.numberParser,
            this.properties,
            this.storageContext
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof StorageCurrencyExchangeRaterStoragePathProperties &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final StorageCurrencyExchangeRaterStoragePathProperties other) {
        return this.storagePath.equals(other.storagePath) &&
            this.numberParser.equals(other.numberParser) &&
            this.properties.equals(other.properties) &&
            this.storageContext.equals(other.storageContext);
    }

    private final StorageContext storageContext;

    @Override
    public String toString() {
        return this.storagePath + " " + this.properties;
    }
}
