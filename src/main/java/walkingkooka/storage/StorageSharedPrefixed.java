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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adds a prefix to all paths for the given {@link Storage} and removes the prefix from returned {@link StorageValue} and
 * {@link StorageValueInfo}.
 * This is particularly useful for a {@link Storage} that unites one or more {@link Storage} at different mount points.
 */
final class StorageSharedPrefixed<C extends StorageContext> extends StorageShared<C> {

    static <C extends StorageContext> Storage<C> with(final StoragePath prefix,
                                                      final Storage<C> storage) {
        Objects.requireNonNull(prefix, "prefix");
        Objects.requireNonNull(storage, "storage");

        final Storage<C> result;

        if (prefix.equals(StoragePath.ROOT)) {
            result = storage;
        } else {
            StoragePath wrapPrefix = prefix;
            Storage<C> wrapStoraage = storage;

            if (storage instanceof StorageSharedPrefixed) {
                final StorageSharedPrefixed<C> prefixedStorage = (StorageSharedPrefixed<C>) storage;
                wrapPrefix = prefix.append(prefixedStorage.prefix);
                wrapStoraage = prefixedStorage.storage;
            }

            result = new StorageSharedPrefixed<C>(
                wrapPrefix,
                wrapStoraage
            );
        }

        return result;
    }

    private StorageSharedPrefixed(final StoragePath prefix,
                                  final Storage<C> storage) {
        this.prefix = prefix;
        this.storage = storage;
    }

    @Override
    Optional<StorageValue> load0(final StoragePath path,
                                 final C context) {
        return this.storage.load(
            path.removePrefix(this.prefix),
            context
        ).map(
            v -> v.prependPath(this.prefix)
        );
    }

    @Override
    StorageValue save0(final StorageValue value,
                       final C context) {
        return this.storage.save(
            value.removePrefixPath(this.prefix),
            context
        ).prependPath(this.prefix);
    }

    @Override
    void delete0(final StoragePath path,
                 final C context) {
        this.storage.delete(
            path.removePrefix(this.prefix),
            context
        );
    }

    @Override
    List<StorageValueInfo> list0(final StoragePath parent,
                                 final int offset,
                                 final int count,
                                 final C context) {
        return this.storage.list(
                parent.removePrefix(this.prefix),
                offset,
                count,
                context
            ).stream()
            .map(i -> i.prependPath(this.prefix))
            .collect(Collectors.toList());
    }

    // @VisibleForTesting
    final StoragePath prefix;

    // @VisibleForTesting
    final Storage<C> storage;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.prefix + " " + this.storage;
    }
}
