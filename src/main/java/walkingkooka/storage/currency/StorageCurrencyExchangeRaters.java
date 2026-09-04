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

import walkingkooka.currency.CurrencyExchangeRater;
import walkingkooka.currency.CurrencyExchangeRaterContext;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.storage.StorageContext;
import walkingkooka.storage.StoragePath;

import java.util.function.Function;

public final class StorageCurrencyExchangeRaters implements PublicStaticHelper {

    /**
     * {@link StorageCurrencyExchangeRaterStoragePathProperties}
     */
    public static <C extends CurrencyExchangeRaterContext> CurrencyExchangeRater<C> storagePathProperties(final StoragePath storagePath,
                                                                                                          final Function<String, Number> numberParser,
                                                                                                          final StorageContext storageContext) {
        return StorageCurrencyExchangeRaterStoragePathProperties.with(
            storagePath,
            numberParser,
            storageContext
        );
    }

    /**
     * Stop creation
     */
    private StorageCurrencyExchangeRaters() {
        throw new UnsupportedOperationException();
    }
}
