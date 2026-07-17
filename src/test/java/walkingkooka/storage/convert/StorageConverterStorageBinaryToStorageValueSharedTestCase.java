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

package walkingkooka.storage.convert;

import org.junit.jupiter.api.Test;
import walkingkooka.Binary;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StoragePath;

public abstract class StorageConverterStorageBinaryToStorageValueSharedTestCase<C extends StorageConverterStorageBinaryToStorageValueShared<FakeStorageConverterContext>> extends StorageConverterTestCase<C> {

    StorageConverterStorageBinaryToStorageValueSharedTestCase() {
        super();
    }

    @Test
    public final void testConvertNonStorageBinaryFails() {
        this.convertFails(
            StorageBinary.with(
                StoragePath.parse("/file.not.matched"),
                Binary.EMPTY
            ),
            Void.class
        );
    }
}
