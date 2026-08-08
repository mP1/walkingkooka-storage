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

/**
 * Wraps another {@link Storage} resolving any paths that begin with {@link StoragePath#HOME_DIRECTORY_PREFIX}
 * replacing that with the actual {@link StorageContext#homeDirectory()}.
 */
final class StorageShared2WrapperExpandedHomeDirectory<C extends StorageContext> extends StorageShared2WrapperExpanded<C> {

    static <C extends StorageContext> StorageShared2WrapperExpandedHomeDirectory<C> with(final Storage<C> storage) {
        return storage instanceof StorageShared2WrapperExpandedHomeDirectory ?
            (StorageShared2WrapperExpandedHomeDirectory) storage :
            new StorageShared2WrapperExpandedHomeDirectory(storage);
    }

    private StorageShared2WrapperExpandedHomeDirectory(final Storage<C> storage) {
        super(storage);
    }

    // StorageShared2WrapperExpanded.....................................................................................

    @Override//
    StoragePath expand(final StoragePath path,
                       final C context) {
        return path.replaceHomeDirectory(context);
    }

    @Override//
    StoragePath unexpand(final StoragePath path,
                         final C context) {
        return path.restoreHomeDirectory(context);
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return StoragePath.HOME_DIRECTORY_PREFIX + " " + this.storage.toString();
    }
}
