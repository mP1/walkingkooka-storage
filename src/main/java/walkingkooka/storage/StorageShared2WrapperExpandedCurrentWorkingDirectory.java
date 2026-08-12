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

import java.util.Optional;

/**
 * Wraps another {@link Storage} resolving any paths that begin with {@link StoragePath#CURRENT_WORKING_DIRECTORY_PREFIX}
 * replacing that with the actual {@link StorageContext#currentWorkingDirectory()} ()}.
 */
final class StorageShared2WrapperExpandedCurrentWorkingDirectory<C extends StorageContext> extends StorageShared2WrapperExpanded<C> {

    static <C extends StorageContext> StorageShared2WrapperExpandedCurrentWorkingDirectory<C> with(final Storage<C> storage) {
        return storage instanceof StorageShared2WrapperExpandedCurrentWorkingDirectory ?
            (StorageShared2WrapperExpandedCurrentWorkingDirectory) storage :
            new StorageShared2WrapperExpandedCurrentWorkingDirectory(storage);
    }

    private StorageShared2WrapperExpandedCurrentWorkingDirectory(final Storage<C> storage) {
        super(storage);
    }

    // StorageShared2WrapperExpanded.....................................................................................

    @Override//
    Optional<StoragePath> expand(final StoragePath path,
                                 final C context) {
        return path.replaceCurrentWorkingDirectory(context);
    }

    @Override//
    Optional<StoragePath> unexpand(final StoragePath path,
                                   final C context) {
        return path.restoreCurrentWorkingDirectory(context);
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return StoragePath.CURRENT_WORKING_DIRECTORY_PREFIX + " " + this.storage.toString();
    }
}
