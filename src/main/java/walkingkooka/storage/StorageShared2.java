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

import walkingkooka.collect.list.Lists;

import java.util.List;

abstract class StorageShared2<C extends StorageContext> extends StorageShared<C> {

    StorageShared2() {
        super();
    }

    @Override //
    final void mount0(final StorageMountPoint<C> mountPoint,
                                final C context) {
        throw new UnsupportedOperationException();
    }

    @Override final void unmount0(final StoragePath path,
                                  final C context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final List<StorageMountPoint<C>> mountPoints() {
        return Lists.empty();
    }
}
