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

import walkingkooka.store.Store;

import java.util.List;

/**
 * A {@link Store} that supports storing values including support for tree or directory structure.
 */
public interface StorageStore extends Store<StoragePath, StorageValue> {

    /**
     * Gets the {@link StorageValueInfo} for the given range.<br>
     * Conceptually equivalent to getting a directory listing.
     */
    List<StorageValueInfo> storageValueInfos(final StoragePath parent,
                                             final int offset,
                                             final int count);
}
