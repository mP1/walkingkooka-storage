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

import java.util.List;
import java.util.Optional;

/**
 * A {@link Storage} that supports storing values including support for tree or directory structure.
 */
public interface Storage<C extends StorageContext> {

    Optional<StorageValue> load(final StoragePath path,
                                final C context);

    StorageValue save(final StorageValue value,
                      final C context);

    void delete(final StoragePath path,
                final C context);

    /**
     * Gets the {@link StorageValueInfo} for the given range.<br>
     * Conceptually equivalent to getting a directory listing.
     */
    List<StorageValueInfo> list(final StoragePath parent,
                                final int offset,
                                final int count,
                                final C context);

    /**
     * Returns a {@link Storage} with an additional prefix to all its {@link StoragePath}.
     */
    default Storage<C> setPrefix(final StoragePath prefix) {
        return StorageSharedPrefixed.with(
            prefix,
            this
        );
    }
}
