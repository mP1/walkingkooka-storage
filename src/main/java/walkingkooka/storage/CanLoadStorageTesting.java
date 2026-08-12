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

import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Optional;

public interface CanLoadStorageTesting extends TreePrintableTesting {

    // loadStorage......................................................................................................

    default void loadStorageAndCheck(final CanLoadStorage can,
                                     final StoragePath path) {
        this.loadStorageAndCheck(
            can,
            path,
            Optional.empty()
        );
    }

    default void loadStorageAndCheck(final CanLoadStorage can,
                                     final StoragePath path,
                                     final StorageValue expected) {
        this.loadStorageAndCheck(
            can,
            path,
            Optional.of(expected)
        );
    }

    default void loadStorageAndCheck(final CanLoadStorage can,
                                     final StoragePath path,
                                     final Optional<StorageValue> expected) {
        this.checkEquals(
            expected,
            can.loadStorage(path),
            () -> " loadStorage " + path
        );
    }
}
