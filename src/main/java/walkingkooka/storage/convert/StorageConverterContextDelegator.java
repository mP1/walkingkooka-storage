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

package walkingkooka.storage.convert;

import walkingkooka.storage.HasUserDirectories;
import walkingkooka.storage.HasUserDirectoriesDelegator;
import walkingkooka.storage.StoragePath;
import walkingkooka.tree.json.convert.JsonNodeConverterContext;
import walkingkooka.tree.json.convert.JsonNodeConverterContextDelegator;

public interface StorageConverterContextDelegator extends JsonNodeConverterContextDelegator,
    StorageConverterContext,
    HasUserDirectoriesDelegator {

    @Override
    default StoragePath parseStoragePath(final String text) {
        return this.storageConverterContext()
            .parseStoragePath(
                text
            );
    }

    // JsonNodeConverterContextDelegator................................................................................

    @Override
    default JsonNodeConverterContext jsonNodeConverterContext() {
        return this.storageConverterContext();
    }

    // HasUserDirectoriesDelegator......................................................................................

    @Override
    default HasUserDirectories hasUserDirectories() {
        return this.storageConverterContext();
    }

    // StorageConverterContextDelegator.................................................................................

    StorageConverterContext storageConverterContext();
}
