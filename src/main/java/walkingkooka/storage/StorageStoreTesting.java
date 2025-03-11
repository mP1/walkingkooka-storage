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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.store.StoreTesting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface StorageStoreTesting<S extends StorageStore> extends StoreTesting<S, StorageKey, StorageValue> {

    @Test
    default void testStorageValueInfosWithNegativeOffsetFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createStore()
                .storageValueInfos(
                    -1,
                    1
                )
        );

        this.checkEquals(
            "Invalid offset -1 < 0",
            thrown.getMessage()
        );
    }

    @Test
    default void testStorageValueInfosWithNegativeCountFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createStore()
                .storageValueInfos(
                    0,
                    -1
                )
        );

        this.checkEquals(
            "Invalid count -1 < 0",
            thrown.getMessage()
        );
    }

    default void storageValueInfosAndCheck(final StorageStore store,
                                           final int offset,
                                           final int count,
                                           final StorageValueInfo... expected) {
        this.storageValueInfosAndCheck(
            store,
            offset,
            count,
            Lists.of(expected)
        );
    }

    default void storageValueInfosAndCheck(final StorageStore store,
                                           final int offset,
                                           final int count,
                                           final List<StorageValueInfo> expected) {
        this.checkEquals(
            expected,
            store.storageValueInfos(
                offset,
                count
            )
        );
    }
}
