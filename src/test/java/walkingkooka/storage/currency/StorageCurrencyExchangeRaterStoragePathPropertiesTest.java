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

import walkingkooka.currency.CurrencyExchangeRaterTesting2;
import walkingkooka.currency.FakeCurrencyExchangeRaterContext;
import walkingkooka.storage.StoragePath;

import java.util.function.Function;

public final class StorageCurrencyExchangeRaterStoragePathPropertiesTest implements CurrencyExchangeRaterTesting2<StorageCurrencyExchangeRaterStoragePathProperties<FakeCurrencyExchangeRaterContext>, FakeCurrencyExchangeRaterContext> {

    private final static StoragePath STORAGE_PATH = StoragePath.parse("/currency-exchange-rates.properties");

    private final static Function<String, Number> NUMBER_PARSER = Double::parseDouble;

    @Override
    public StorageCurrencyExchangeRaterStoragePathProperties createCurrencyExchangeRater() {
        return this.createCurrencyExchangeRater(
            this.createContext()
        );
    }

    private StorageCurrencyExchangeRaterStoragePathProperties createCurrencyExchangeRater(final FakeCurrencyExchangeRaterContext context) {
        return StorageCurrencyExchangeRaterStoragePathProperties.with(
            STORAGE_PATH,
            NUMBER_PARSER,
            context
        );
    }

    @Override
    public FakeCurrencyExchangeRaterContext createContext() {
        return new FakeCurrencyExchangeRaterContext() {

        };
    }
}
