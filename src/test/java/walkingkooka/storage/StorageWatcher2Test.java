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
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageWatcher2Test implements ClassTesting2<StorageWatcher2> {

    private final static StoragePath PATH = StoragePath.parse("/file123.txt");

    // onStorageValue...................................................................................................

    @Test
    public void testOnStorageValueWithNullNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> new FakeStorageWatcher2()
                .onStorageValueChange(
                    null,
                    Optional.empty(),
                    Optional.empty()
                )
        );
    }

    @Test
    public void testOnStorageValueWithNullOldValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> new FakeStorageWatcher2()
                .onStorageValueChange(
                    PATH,
                    null,
                    Optional.empty()
                )
        );
    }

    @Test
    public void testOnStorageValueWithNullNewValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> new FakeStorageWatcher2()
                .onStorageValueChange(
                    PATH,
                    Optional.empty(),
                    null
                )
        );
    }

    // onStorageValueAdd................................................................................................

    @Test
    public void testOnStorageValueAdd() {
        this.fired = false;

        final Locale value = Locale.ENGLISH;

        new FakeStorageWatcher2() {
            @Override
            public void onStorageValueAdd(final StoragePath p,
                                          final Object nv) {
                checkEquals(PATH, p, "path");
                checkEquals(value, nv, "newValue");

                StorageWatcher2Test.this.fired = true;
            }
        }.onStorageValueChange(
            PATH,
            Optional.empty(),
            Optional.of(value)
        );

        this.checkEquals(
            true,
            this.fired
        );
    }

    // onStorageValueRemove.............................................................................................

    @Test
    public void testOnStorageValueRemove() {
        this.fired = false;

        final Locale value = Locale.ENGLISH;

        new FakeStorageWatcher2() {

            @Override
            public void onStorageValueRemove(final StoragePath p,
                                             final Object ov) {
                checkEquals(PATH, p, "path");
                checkEquals(value, ov, "oldValue");

                StorageWatcher2Test.this.fired = true;
            }

        }.onStorageValueChange(
            PATH,
            Optional.of(value),
            Optional.empty()
        );

        this.checkEquals(
            true,
            this.fired
        );
    }

    // onStorageUpdate.........................................................................................

    @Test
    public void testOnStorageValueUpdate() {
        this.fired = false;

        final Locale oldValue = Locale.ENGLISH;
        final Locale newValue = Locale.FRENCH;

        new FakeStorageWatcher2() {

            @Override
            public void onStorageValueUpdate(final StoragePath p,
                                             final Object ov,
                                             final Object nv) {
                checkEquals(PATH, p, "path");
                checkEquals(oldValue, ov, "oldValue");
                checkEquals(newValue, nv, "newValue");

                StorageWatcher2Test.this.fired = true;
            }
        }.onStorageValueChange(
            PATH,
            Optional.of(oldValue),
            Optional.of(newValue)
        );

        this.checkEquals(
            true,
            this.fired
        );
    }

    private boolean fired;

    static class FakeStorageWatcher2 implements StorageWatcher2 {

        @Override
        public void onStorageValueAdd(final StoragePath p,
                                      final Object nv) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void onStorageValueRemove(final StoragePath p,
                                         final Object ov) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void onStorageValueUpdate(final StoragePath p,
                                         final Object ov,
                                         final Object nv) {
            throw new UnsupportedOperationException();
        }
    }

    // class............................................................................................................

    @Override
    public Class<StorageWatcher2> type() {
        return StorageWatcher2.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
