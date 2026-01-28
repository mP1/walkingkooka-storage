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
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.ToStringTesting;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterTesting2;
import walkingkooka.convert.Converters;
import walkingkooka.storage.StoragePath;

import java.util.Optional;

public final class StorageConverterTextToStoragePathTest implements ConverterTesting2<StorageConverterTextToStoragePath<FakeStorageConverterContext>, FakeStorageConverterContext>,
    ToStringTesting<StorageConverterTextToStoragePath<FakeStorageConverterContext>> {

    private final static String CWD = "/current1/working2/directory3";

    @Test
    public void testConvertNullFails() {
        this.convertFails(
            null,
            StoragePath.class
        );
    }

    @Test
    public void testConvertNonStringFails() {
        this.convertFails(
            123,
            StoragePath.class
        );
    }

    @Test
    public void testConvertEmptyString() {
        this.convertAndCheck(
            "",
            StoragePath.class,
            StoragePath.parse(CWD)
        );
    }

    @Test
    public void testConvertStringWithAbsolutePath() {
        final String text = "/path123/file456.txt";

        this.convertAndCheck(
            text,
            StoragePath.class,
            StoragePath.parse(text)
        );
    }

    @Test
    public void testConvertCharSequenceWithAbsolutePath() {
        final String text = "/path123/file456.txt";

        this.convertAndCheck(
            new StringBuilder(text),
            StoragePath.class,
            StoragePath.parse(text)
        );
    }

    @Test
    public void testConvertRelativePath() {
        final String text = "after4.txt";

        this.convertAndCheck(
            text,
            StoragePath.class,
            StoragePath.parse(CWD + "/" + text)
        );
    }

    @Test
    public void testConvertRelativePathAndCurrentWorkingDirectoryWithEndingSlash() {
        final String text = "after4.txt";

        this.convertAndCheck(
            this.createConverter(),
            text,
            StoragePath.class,
            this.createContext(
                Optional.of(
                    StoragePath.parse(CWD + "/")
                )
            ),
            StoragePath.parse(CWD + "/" + text)
        );
    }

    @Override
    public StorageConverterTextToStoragePath<FakeStorageConverterContext> createConverter() {
        return StorageConverterTextToStoragePath.instance();
    }

    @Override
    public FakeStorageConverterContext createContext() {
        return this.createContext(
            Optional.of(
                StoragePath.parse(CWD)
            )
        );
    }

    public FakeStorageConverterContext createContext(final Optional<StoragePath> currentWorkingDirectory) {
        return new FakeStorageConverterContext() {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> type) {
                return this.converter.canConvert(
                    value,
                    type,
                    this
                );
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return this.converter.convert(
                    value,
                    target,
                    this
                );
            }

            private final Converter<ConverterContext> converter = Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString();

            @Override
            public Optional<StoragePath> currentWorkingDirectory() {
                return currentWorkingDirectory;
            }
        };
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            StorageConverterTextToStoragePath.instance(),
            "String -> StoragePath"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageConverterTextToStoragePath<FakeStorageConverterContext>> type() {
        return Cast.to(StorageConverterTextToStoragePath.class);
    }

    @Override
    public String typeNamePrefix() {
        return "StorageConverter";
    }

    @Override
    public String typeNameSuffix() {
        return "";
    }
}
