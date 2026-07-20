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

import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.collect.list.TsvStringList;
import walkingkooka.io.FileExtension;
import walkingkooka.net.header.MediaType;
import walkingkooka.storage.StorageBinary;


/**
 * Converts {@link FileExtension#TSV} files in several steps,
 * <ul>
 * <li>Convert {@link StorageBinary} to a {@link String}, which is expected to hold TSV and then convert that to a {@link TsvStringList}</li>
 * </ul>
 */
final class StorageConverterStorageBinaryToStorageValueSharedTsv<C extends StorageConverterContext> extends StorageConverterStorageBinaryToStorageValueShared<C> {

    /**
     * Type safe getter.
     */
    static <C extends StorageConverterContext> StorageConverterStorageBinaryToStorageValueSharedTsv<C> instance() {
        return Cast.to(INSTANCE);
    }

    private final static StorageConverterStorageBinaryToStorageValueSharedTsv INSTANCE = new StorageConverterStorageBinaryToStorageValueSharedTsv<>();

    private StorageConverterStorageBinaryToStorageValueSharedTsv() {
        super();
    }

    @Override
    FileExtension fileExtension() {
        return FileExtension.TSV;
    }

    @Override
    MediaType contentType() {
        return MediaType.TEXT_TAB_SEPARATED_VALUES;
    }

    @Override
    <T> Either<T, String> storageBinaryToStorageValue(final StorageBinary storageBinary,
                                                      final Class<T> type,
                                                      final C context) {
        final Either<T, String> result;

        // convert Binary to String
        final Either<String, String> text = context.convert(
            storageBinary,
            String.class
        );
        if (text.isRight()) {
            result = Cast.to(text);
        } else {
            final Either<TsvStringList, String> tsv = context.convert(
                storageBinary,
                TsvStringList.class
            );

            if (tsv.isRight()) {
                result = Cast.to(tsv);
            } else {
                result = this.successfulConversion(
                    storageBinary.path(),
                    type,
                    tsv.leftValue(),
                    storageBinary.contentType()
                );
            }
        }

        return result;
    }
}
