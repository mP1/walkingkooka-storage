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
import walkingkooka.ToStringTesting;
import walkingkooka.naming.HasPathTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StoragePathStorageWatcherTest implements HasPathTesting,
    ClassTesting<StoragePathStorageWatcher>,
    ToStringTesting<StoragePathStorageWatcher> {

    private final static StoragePath PATH = StoragePath.ROOT;

    private final static StorageWatcher WATCHER = new FakeStorageWatcher();

    // with.............................................................................................................

    @Test
    public void testWithNullStorageWatcherFails() {
        assertThrows(
            NullPointerException.class,
            () -> StoragePathStorageWatcher.with(
                null,
                PATH
            )
        );
    }

    @Test
    public void testWithNullStoragePathFails() {
        assertThrows(
            NullPointerException.class,
            () -> StoragePathStorageWatcher.with(
                WATCHER,
                null
            )
        );
    }

    @Test
    public void testWith() {
        final StoragePathStorageWatcher watcher = StoragePathStorageWatcher.with(
            WATCHER,
            PATH
        );

        this.pathAndCheck(
            watcher,
            PATH
        );

        this.checkEquals(
            WATCHER,
            watcher.watcher
        );
    }

    // onValueChange....................................................................................................

    private final static StoragePath DIFFERENT_PATH = StoragePath.parse("/different");

    @Test
    public void testOnValueChangeWithOldValueDifferentPath() {
        StoragePathStorageWatcher.with(
            WATCHER,
            PATH
        ).onValueChange(
            Optional.of(
                StorageValue.with(
                    DIFFERENT_PATH
                )
            ),
            Optional.empty()
        );
    }

    @Test
    public void testOnValueChangeWithOldValueDifferentPath2() {
        StoragePathStorageWatcher.with(
            WATCHER,
            PATH
        ).onValueChange(
            Optional.of(
                StorageValue.with(
                    DIFFERENT_PATH
                )
            ),
            Optional.of(
                StorageValue.with(
                    PATH
                )
            )
        );
    }

    @Test
    public void testOnValueChangeWithNewValueDifferentPath() {
        StoragePathStorageWatcher.with(
            WATCHER,
            PATH
        ).onValueChange(
            Optional.empty(),
            Optional.of(
                StorageValue.with(
                    DIFFERENT_PATH
                )
            )
        );
    }

    @Test
    public void testOnValueChangeWithNewValueDifferentPath2() {
        StoragePathStorageWatcher.with(
            WATCHER,
            PATH
        ).onValueChange(
            Optional.of(
                StorageValue.with(
                    PATH
                )
            ),
            Optional.of(
                StorageValue.with(
                    DIFFERENT_PATH
                )
            )
        );
    }

    @Test
    public void testOnValueChangeWithValueChange() {
        this.onValueChangeAndCheck(
            Optional.of(
                StorageValue.with(PATH)
                    .setValue(
                        Optional.of("oldValue")
                    )
            ),
            Optional.of(
                StorageValue.with(PATH)
                    .setValue(
                        Optional.of("newValue")
                    )
            )
        );
    }

    @Test
    public void testOnValueChangeWithAddValue() {
        this.onValueChangeAndCheck(
            Optional.empty(),
            Optional.of(
                StorageValue.with(PATH)
                    .setValue(
                        Optional.of("newValue")
                    )
            )
        );
    }

    @Test
    public void testOnValueChangeWithRemoveValue() {
        this.onValueChangeAndCheck(
            Optional.empty(),
            Optional.of(
                StorageValue.with(PATH)
                    .setValue(
                        Optional.of("newValue")
                    )
            )
        );
    }

    private void onValueChangeAndCheck(final Optional<StorageValue> oldValue,
                                       final Optional<StorageValue> newValue) {
        this.fired = false;

        StoragePathStorageWatcher.with(
            new StorageWatcher() {
                @Override
                public void onValueChange(final Optional<StorageValue> ov,
                                          final Optional<StorageValue> nv) {
                    checkEquals(
                        oldValue,
                        ov,
                        "oldValue"
                    );
                    checkEquals(
                        newValue,
                        nv,
                        "newValue"
                    );

                    StoragePathStorageWatcherTest.this.fired = true;
                }
            },
            PATH
        ).onValueChange(
            oldValue,
            newValue
        );

        this.checkEquals(
            true,
            this.fired
        );
    }

    private boolean fired;

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            StoragePathStorageWatcher.with(
                WATCHER,
                PATH
            ),
            PATH + " " + WATCHER
        );
    }

    // class............................................................................................................

    @Override
    public Class<StoragePathStorageWatcher> type() {
        return StoragePathStorageWatcher.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
