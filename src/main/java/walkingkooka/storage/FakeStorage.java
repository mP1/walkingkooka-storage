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

import walkingkooka.store.FakeStore;

import java.util.List;
import java.util.Optional;

public class FakeStorage<C extends StorageContext> extends FakeStore<StoragePath, StorageValue> implements Storage<C> {

    public FakeStorage() {
        super();
    }

    @Override
    public Optional<StorageValue> load(final StoragePath path,
                                       final C context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StorageValue save(final StorageValue value,
                             final C context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(final StoragePath path,
                       final C context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<StorageValueInfo> list(final StoragePath parent,
                                       final int offset,
                                       final int count,
                                       final C context) {
        throw new UnsupportedOperationException();
    }
}
