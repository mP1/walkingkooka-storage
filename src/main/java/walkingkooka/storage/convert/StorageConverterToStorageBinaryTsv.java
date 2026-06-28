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

import walkingkooka.Binary;
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.collect.list.TsvStringList;
import walkingkooka.io.FileExtension;
import walkingkooka.storage.StorageBinary;
import walkingkooka.storage.StorageValue;

/**
 * Converts a {@link StorageValue} into {@link StorageBinary} if the file extension is {@link FileExtension#TSV}
 * and the {@link StorageValue#value()} can be converted into a {@link TsvStringList} and then {@link String} and then
 * {@link StorageBinary}.
 */
final class StorageConverterToStorageBinaryTsv<C extends StorageConverterContext> extends StorageConverterToStorageBinary<C> {

    /**
     * Type safe getter.
     */
    static <C extends StorageConverterContext> StorageConverterToStorageBinaryTsv<C> instance() {
        return Cast.to(INSTANCE);
    }

    private final static StorageConverterToStorageBinaryTsv INSTANCE = new StorageConverterToStorageBinaryTsv<>();

    private StorageConverterToStorageBinaryTsv() {
        super();
    }

    @Override
    boolean testStorageValue(final StorageValue storageValue,
                             final C context) {
        return FileExtension.TSV.equals(
            storageValue.path()
                .fileExtension()
                .orElse(null)
        ) &&
            storageValue.value()
                .map((Object value) -> context.canConvert(value, TsvStringList.class) && context.canConvert(TsvStringList.EMPTY, Binary.class))
                .orElse(false);
    }

    @Override
    Either<Binary, String> toBinary(final StorageValue storageValue,
                                    final C context) {
        Either<TsvStringList, String> tsvStringList = context.convert(
            storageValue.value()
                .orElse(null),
            TsvStringList.class
        );

        return tsvStringList.isLeft() ?
            context.convert(
                tsvStringList.leftValue(),
                Binary.class
            ) :
            Cast.to(tsvStringList);
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "*." + FileExtension.TSV + " to " + StorageBinary.class.getSimpleName();
    }
}
