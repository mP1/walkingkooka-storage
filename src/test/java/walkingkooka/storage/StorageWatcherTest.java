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
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageWatcherTest implements ClassTesting<StorageWatcher> {
    // setPathPrefix....................................................................................................

    @Test
    public void testSetPathPrefixWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> new FakeStorageWatcher()
                .setPathPrefix(null)
        );
    }

    @Test
    public void testSetPathPrefixWithRoot() {
        final StorageWatcher watcher = new FakeStorageWatcher();

        assertSame(
            watcher,
            watcher.setPathPrefix(StoragePath.ROOT)
        );
    }

    @Test
    public void testSetPathPrefixAndOnValueChange() {
        this.fired = false;

        final StoragePath storagePath = StoragePath.parse("/path222/path333");

        final StoragePath pathPrefix = StoragePath.parse("/prefix111");

        final Optional<Object> oldValue = Optional.of("oldValue111");
        final Optional<Object> newValue = Optional.of("newValue111");

        new StorageWatcher() {
            @Override
            public void onValueChange(final Optional<StorageValue> ov,
                                      final Optional<StorageValue> nv) {
                final StoragePath prefix = StoragePath.parse(
                    pathPrefix.value() + storagePath.value()
                );

                checkEquals(
                    Optional.of(
                        StorageValue.with(prefix)
                            .setValue(oldValue)
                    ),
                    ov,
                    "oldValue"
                );
                checkEquals(
                    Optional.of(
                        StorageValue.with(prefix)
                            .setValue(newValue)
                    ),
                    nv,
                    "newValue"
                );

                StorageWatcherTest.this.fired = true;
            }
        }.setPathPrefix(pathPrefix)
            .onValueChange(
                Optional.of(
                    StorageValue.with(storagePath)
                        .setValue(oldValue)
                ),
                Optional.of(
                    StorageValue.with(storagePath)
                        .setValue(newValue)
                )
            );

        this.checkEquals(
            true,
            this.fired,
            "fired"
        );
    }

    private boolean fired;

    // class............................................................................................................

    @Override
    public Class<StorageWatcher> type() {
        return StorageWatcher.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
