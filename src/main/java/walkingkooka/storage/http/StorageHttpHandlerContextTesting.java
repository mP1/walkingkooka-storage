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

import walkingkooka.collect.list.Lists;
import walkingkooka.net.http.server.HttpHandlerContextTesting;
import walkingkooka.storage.StorageContextTesting;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;

import java.util.List;
import java.util.Optional;

public interface StorageHttpHandlerContextTesting extends HttpHandlerContextTesting,
    StorageContextTesting {

    // Storage..........................................................................................................

    default void loadStorageAndCheck(final StorageHttpHandlerContext context,
                                     final StoragePath path) {
        this.loadStorageAndCheck(
            context,
            path,
            Optional.empty()
        );
    }

    default void loadStorageAndCheck(final StorageHttpHandlerContext context,
                                     final StoragePath path,
                                     final StorageValue expected) {
        this.loadStorageAndCheck(
            context,
            path,
            Optional.of(expected)
        );
    }

    default void loadStorageAndCheck(final StorageHttpHandlerContext context,
                                     final StoragePath path,
                                     final Optional<StorageValue> expected) {
        this.checkEquals(
            expected,
            context.loadStorage(path),
            () -> "loadStorage " + path
        );
    }

    default void saveStorageAndCheck(final StorageHttpHandlerContext context,
                                     final StorageValue value,
                                     final StorageValue expected) {
        this.checkEquals(
            expected,
            context.saveStorage(value),
            () -> "saveStorage " + value
        );
    }

    default void listStorageAndCheck(final StorageHttpHandlerContext context,
                                     final StoragePath parent,
                                     final int offset,
                                     final int count,
                                     final StorageValueInfo... expected) {
        this.listStorageAndCheck(
            context,
            parent,
            offset,
            count,
            Lists.of(expected)
        );
    }

    default void listStorageAndCheck(final StorageHttpHandlerContext context,
                                     final StoragePath parent,
                                     final int offset,
                                     final int count,
                                     final List<StorageValueInfo> expected) {
        this.checkEquals(
            expected,
            context.listStorage(
                parent,
                offset,
                count
            ),
            () -> "listStorage " + parent + " offset=" + offset + " count=" + count + " " + expected
        );
    }
}
