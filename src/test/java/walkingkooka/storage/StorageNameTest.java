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
import walkingkooka.io.FileExtension;
import walkingkooka.naming.NameTesting2;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageNameTest implements ClassTesting2<StorageName>,
    NameTesting2<StorageName, StorageName> {

    // with.............................................................................................................

    @Override
    public void testEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testWithRoot() {
        assertSame(
            StorageName.ROOT,
            StorageName.with("")
        );
    }

    // fileExtension........................................................................................................

    @Test
    public void testFileExtensionMissing() {
        this.fileExtensionAndCheck(
            StorageName.with("xyz")
        );
    }

    @Test
    public void testFileExtensionEmpty() {
        this.fileExtensionAndCheck(
            StorageName.with("xyz."),
            FileExtension.with("")
        );
    }

    @Test
    public void testFileExtensionPresent() {
        this.fileExtensionAndCheck(
            StorageName.with("xyz.txt"),
            FileExtension.with("txt")
        );
    }

    @Test
    public void testFileExtensionPresent2() {
        this.fileExtensionAndCheck(
            StorageName.with("xyz.EXE"),
            FileExtension.with("EXE")
        );
    }

    private void fileExtensionAndCheck(final StorageName name) {
        this.fileExtensionAndCheck(
            name,
            Optional.empty()
        );
    }

    private void fileExtensionAndCheck(final StorageName name,
                                       final FileExtension extension) {
        this.fileExtensionAndCheck(
            name,
            Optional.of(extension)
        );
    }

    private void fileExtensionAndCheck(final StorageName name,
                                       final Optional<FileExtension> extension) {
        this.checkEquals(
            extension,
            name.fileExtension(),
            "file extension within " + name
        );
    }

    // withoutFileExtension.............................................................................................

    @Test
    public void testWithoutFileExtensionMissingFileExtension() {
        this.withoutFileExtensionAndCheck(
            StorageName.with("xyz")
        );
    }

    @Test
    public void testWithoutFileExtensionEmpty() {
        this.withoutFileExtensionAndCheck(
            StorageName.with("xyz."),
            "xyz"
        );
    }

    @Test
    public void testWithoutFileExtensionPresent() {
        this.withoutFileExtensionAndCheck(
            StorageName.with("xyz.txt"),
            "xyz"
        );
    }

    @Test
    public void testWithoutFileExtensionPresent2() {
        this.withoutFileExtensionAndCheck(
            StorageName.with("xyz.EXE"),
            "xyz"
        );
    }

    @Test
    public void testWithoutFileExtensionPresent3() {
        this.withoutFileExtensionAndCheck(
            StorageName.with("xyz.111.222"),
            "xyz.111"
        );
    }

    private void withoutFileExtensionAndCheck(final StorageName name) {
        this.withoutFileExtensionAndCheck(
            name,
            name.value()
        );
    }

    private void withoutFileExtensionAndCheck(final StorageName name,
                                              final String expected) {
        this.checkEquals(
            expected,
            name.withoutFileExtension(),
            "name without file extension " + CharSequences.quoteAndEscape(name.value())
        );
    }

    // NameTesting......................................................................................................

    @Override
    public StorageName createName(final String name) {
        return StorageName.with(name);
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.SENSITIVE;
    }

    @Override
    public String nameText() {
        return "file123.txt";
    }

    @Override
    public String differentNameText() {
        return "different.txt";
    }

    @Override
    public String nameTextLess() {
        return "abc.txt";
    }

    @Override
    public String possibleValidChars(final int i) {
        return ASCII_LETTERS_DIGITS;
    }

    @Override
    public String possibleInvalidChars(final int i) {
        return CONTROL;
    }

    // MAX_LENGTH.......................................................................................................

    @Test
    public void testWithMaxLengthFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> StorageName.with(
                CharSequences.repeating('A', StorageName.MAX_LENGTH + 1)
                    .toString()
            )
        );

        this.checkEquals(
            "Length 256 of \"name\" not between 1..255 = \"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"",
            thrown.getMessage()
        );
    }

    @Override
    public int minLength() {
        return StorageName.MIN_LENGTH;
    }

    @Override
    public int maxLength() {
        return StorageName.MAX_LENGTH;
    }

    // class............................................................................................................

    @Override
    public Class<StorageName> type() {
        return StorageName.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
