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

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageWatchersTest implements ClassTesting<StorageWatchers> {

    private final static StoragePath PATH = StoragePath.parse("/file123.txt");

    @Test
    public void testAddWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageWatchers.empty()
                .add(null)
        );
    }

    @Test
    public void testAddOnceWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageWatchers.empty()
                .addOnce(null)
        );
    }

    @Test
    public void testAddThenFire() {
        this.fired = false;

        final Optional<Locale> oldValue = Optional.of(
            Locale.FRANCE
        );
        final Optional<Locale> newValue = Optional.of(
            Locale.GERMANY
        );

        final StorageWatchers watchers = StorageWatchers.empty();
        watchers.add(
            new StorageWatcher() {
                @Override
                public void onStorageValueChange(final StoragePath p,
                                                 final Optional<?> ov,
                                                 final Optional<?> nv) {
                    checkEquals(PATH, p);
                    checkEquals(oldValue, ov);
                    checkEquals(newValue, nv);

                    fired = true;
                }
            });
        watchers.onStorageValueChange(
            PATH,
            oldValue,
            newValue
        );

        this.checkEquals(
            true,
            this.fired
        );
    }

    @Test
    public void testAddThenFireEqualEmptyValues() {
        this.fired = false;

        final StorageWatchers watchers = StorageWatchers.empty();
        watchers.add(
            new StorageWatcher() {
                @Override
                public void onStorageValueChange(final StoragePath n,
                                                 final Optional<?> ov,
                                                 final Optional<?> nv) {
                    throw new UnsupportedOperationException();
                }
            });
        watchers.onStorageValueChange(
            StoragePath.ROOT,
            Optional.empty(),
            Optional.empty()
        );

        this.checkEquals(
            false,
            this.fired
        );
    }

    @Test
    public void testAddThenFireEqualValues() {
        this.fired = false;

        final StorageWatchers watchers = StorageWatchers.empty();
        watchers.add(
            new StorageWatcher() {
                @Override
                public void onStorageValueChange(final StoragePath n,
                                                 final Optional<?> ov,
                                                 final Optional<?> nv) {
                    throw new UnsupportedOperationException();
                }
            });

        final Locale locale = Locale.FRANCE;

        watchers.onStorageValueChange(
            PATH,
            Optional.of(locale),
            Optional.of(locale)
        );

        this.checkEquals(
            false,
            this.fired
        );
    }

    @Test
    public void testAddOnceThenFire() {
        this.fired = false;

        final Optional<Locale> oldValue = Optional.of(
            Locale.FRANCE
        );
        final Optional<Locale> newValue = Optional.of(
            Locale.GERMANY
        );

        final StorageWatchers watchers = StorageWatchers.empty();
        watchers.addOnce(
            new StorageWatcher() {
                @Override
                public void onStorageValueChange(final StoragePath p,
                                                 final Optional<?> ov,
                                                 final Optional<?> nv) {
                    checkEquals(
                        false,
                        fired,
                        "event should only have been fired once!"
                    );

                    checkEquals(PATH, p);
                    checkEquals(oldValue, ov);
                    checkEquals(newValue, nv);

                    fired = true;
                }
            });
        watchers.onStorageValueChange(
            PATH,
            oldValue,
            newValue
        );

        this.checkEquals(
            true,
            this.fired
        );
    }

    private boolean fired = false;

    // Class............................................................................................................

    @Override
    public Class<StorageWatchers> type() {
        return StorageWatchers.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
