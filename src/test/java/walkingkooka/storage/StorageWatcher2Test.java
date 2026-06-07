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

    // onValueChange....................................................................................................

    @Test
    public void testOnValueChangeWithNullOldValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> new FakeStorageWatcher2()
                .onValueChange(
                    null,
                    Optional.empty()
                )
        );
    }

    @Test
    public void testOnValueChangeWithNullNewValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> new FakeStorageWatcher2()
                .onValueChange(
                    Optional.empty(),
                    null
                )
        );
    }

    // onValueChangeAdd.................................................................................................

    @Test
    public void testOnValueChangeAdd() {
        this.fired = false;

        final StorageValue value = StorageValue.with(PATH)
            .setValue(
                Optional.of(Locale.ENGLISH)
            );

        new FakeStorageWatcher2() {
            @Override
            public void onValueChangeAdd(final StorageValue nv) {
                checkEquals(value, nv, "newValue");

                StorageWatcher2Test.this.fired = true;
            }
        }.onValueChange(
            Optional.empty(),
            Optional.of(value)
        );

        this.checkEquals(
            true,
            this.fired
        );
    }

    // onValueChangeRemove..............................................................................................

    @Test
    public void testOnValueChangeRemove() {
        this.fired = false;

        final StorageValue value = StorageValue.with(PATH)
            .setValue(
                Optional.of(Locale.ENGLISH)
            );

        new FakeStorageWatcher2() {

            @Override
            public void onValueChangeRemove(final StorageValue ov) {
                checkEquals(value, ov, "oldValue");

                StorageWatcher2Test.this.fired = true;
            }

        }.onValueChange(
            Optional.of(value),
            Optional.empty()
        );

        this.checkEquals(
            true,
            this.fired
        );
    }

    // onStorageReplace.................................................................................................

    @Test
    public void testOnValueChangeReplace() {
        this.fired = false;

        final StorageValue oldValue = StorageValue.with(PATH)
            .setValue(
                Optional.of(Locale.ENGLISH)
            );
        final StorageValue newValue = StorageValue.with(PATH)
            .setValue(
                Optional.of(Locale.FRENCH)
            );

        new FakeStorageWatcher2() {

            @Override
            public void onValueChangeReplace(final StorageValue ov,
                                             final StorageValue nv) {
                checkEquals(oldValue, ov, "oldValue");
                checkEquals(newValue, nv, "newValue");

                StorageWatcher2Test.this.fired = true;
            }
        }.onValueChange(
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
        public void onValueChangeAdd(final StorageValue nv) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void onValueChangeRemove(final StorageValue ov) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void onValueChangeReplace(final StorageValue ov,
                                         final StorageValue nv) {
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
