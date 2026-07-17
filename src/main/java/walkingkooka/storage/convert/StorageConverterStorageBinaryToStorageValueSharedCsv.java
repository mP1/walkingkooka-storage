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
import walkingkooka.collect.list.CsvStringList;
import walkingkooka.io.FileExtension;
import walkingkooka.net.header.MediaType;
import walkingkooka.storage.StorageBinary;


/**
 * Converts {@link FileExtension#CSV} files in several steps,
 * <ul>
 * <li>Convert {@link StorageBinary} to a {@link String}, which is expected to hold CSV and then convert that to a {@link CsvStringList}</li>
 * </ul>
 */
final class StorageConverterStorageBinaryToStorageValueSharedCsv<C extends StorageConverterContext> extends StorageConverterStorageBinaryToStorageValueShared<C> {

    /**
     * Type safe getter.
     */
    static <C extends StorageConverterContext> StorageConverterStorageBinaryToStorageValueSharedCsv<C> instance() {
        return Cast.to(INSTANCE);
    }

    private final static StorageConverterStorageBinaryToStorageValueSharedCsv INSTANCE = new StorageConverterStorageBinaryToStorageValueSharedCsv<>();

    private StorageConverterStorageBinaryToStorageValueSharedCsv() {
        super();
    }

    @Override
    FileExtension fileExtension() {
        return FileExtension.CSV;
    }

    @Override
    MediaType contentType() {
        return MediaType.TEXT_CSV;
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
            final Either<CsvStringList, String> csv = context.convert(
                storageBinary,
                CsvStringList.class
            );

            if (csv.isRight()) {
                result = Cast.to(csv);
            } else {
                result = this.successfulConversion(
                    storageBinary.path(),
                    type,
                    csv.leftValue()
                );
            }
        }

        return result;
    }
}
