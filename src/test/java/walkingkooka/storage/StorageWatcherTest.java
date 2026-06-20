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
import walkingkooka.predicate.Predicates;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageWatcherTest implements ToStringTesting<StorageWatcher>,
    ClassTesting<StorageWatcher> {

    final StoragePath STORAGE_PATH = StoragePath.parse("/path222/path333");

    final Optional<Object> OLD_VALUE = Optional.of("oldValue111");
    final Optional<Object> NEW_VALUE = Optional.of("newValue111");

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

        final StoragePath pathPrefix = StoragePath.parse("/prefix111");

        new StorageWatcher() {
            @Override
            public void onValueChange(final Optional<StorageValue> ov,
                                      final Optional<StorageValue> nv) {
                final StoragePath prefix = StoragePath.parse(
                    pathPrefix.value() + STORAGE_PATH.value()
                );

                checkEquals(
                    Optional.of(
                        StorageValue.with(prefix)
                            .setValue(OLD_VALUE)
                    ),
                    ov,
                    "oldValue"
                );
                checkEquals(
                    Optional.of(
                        StorageValue.with(prefix)
                            .setValue(NEW_VALUE)
                    ),
                    nv,
                    "newValue"
                );

                StorageWatcherTest.this.fired = true;
            }
        }.setPathPrefix(pathPrefix)
            .onValueChange(
                Optional.of(
                    StorageValue.with(STORAGE_PATH)
                        .setValue(OLD_VALUE)
                ),
                Optional.of(
                    StorageValue.with(STORAGE_PATH)
                        .setValue(NEW_VALUE)
                )
            );

        this.checkEquals(
            true,
            this.fired,
            "fired"
        );
    }

    // setFilter........................................................................................................

    @Test
    public void testSetFilterWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> new FakeStorageWatcher()
                .setFilter(null)
        );
    }

    @Test
    public void testSetFilterUnmatchedPath() {
        new FakeStorageWatcher()
            .setFilter(Predicates.never())
            .onValueChange(
                Optional.of(
                    StorageValue.with(StoragePath.parse("/ignored"))
                ),
                Optional.empty()
            );
    }

    @Test
    public void testSetFilterMatchedPath() {
        final Optional<StorageValue> oldValue = Optional.of(
            StorageValue.with(STORAGE_PATH)
                .setValue(OLD_VALUE)
        );

        final Optional<StorageValue> newValue = Optional.of(
            StorageValue.with(STORAGE_PATH)
                .setValue(NEW_VALUE)
        );

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

                StorageWatcherTest.this.fired = true;
            }
        }.setFilter(Predicates.is(STORAGE_PATH))
            .onValueChange(
                oldValue,
                newValue
            );

        this.checkEquals(
            true,
            this.fired,
            "fired"
        );
    }

    private boolean fired;

    // toString.........................................................................................................

    @Test
    public void testSetFilterAndToString() {
        final StorageWatcher watcher = new FakeStorageWatcher();

        final Predicate<StoragePath> filter = Predicates.fake();

        this.toStringAndCheck(
            watcher.setFilter(filter),
            "if " + filter + " " + watcher
        );
    }

    @Override
    public void testCheckToStringOverridden() {
        throw new UnsupportedOperationException();
    }

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
