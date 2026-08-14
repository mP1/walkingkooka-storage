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
 * Replaces the {@link StoragePath#ROOT} with the current {@link StorageEnvironmentContext#homeDirectory()}.
 */
final class StorageShared2ExpandedHomeDirectory<C extends StorageContext> extends StorageShared2Expanded<C> {

    static <C extends StorageContext> StorageShared2ExpandedHomeDirectory<C> instance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private final static StorageShared2ExpandedHomeDirectory INSTANCE = new StorageShared2ExpandedHomeDirectory<>();

    private StorageShared2ExpandedHomeDirectory() {
        super();
    }

    // StorageShared2Expanded...........................................................................................

    @Override//
    Optional<StoragePath> expand(final StoragePath path,
                                 final C context) {
        return this.replacePrefixWithEnvironment(
            path,
            context.homeDirectory()
        );
    }

    @Override//
    Optional<StoragePath> unexpand(final StoragePath path,
                                   final C context) {
        return this.replaceEnvironment(
            path,
            context.homeDirectory()
        );
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return StoragePath.HOME_DIRECTORY_PREFIX.toString();
    }
}
