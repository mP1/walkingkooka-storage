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

package walkingkooka.storage.http;

import walkingkooka.net.http.server.HttpHandlerContext;
import walkingkooka.storage.StorageContext;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;

import java.util.List;
import java.util.Optional;

public interface StorageHttpHandlerContext extends HttpHandlerContext,
    StorageContext {

    Optional<StorageValue> loadStorage(final StoragePath path);

    StorageValue saveStorage(final StorageValue value);

    void deleteStorage(final StoragePath path);

    List<StorageValueInfo> listStorage(final StoragePath parent,
                                       final int offset,
                                       final int count);
}
