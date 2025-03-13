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

/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
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
import walkingkooka.naming.NameTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageNameTest implements ClassTesting2<StorageName>,
        NameTesting<StorageName, StorageName> {

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

    // NameTesting......................................................................................................

    @Override
    public StorageName createName(final String name) {
        return StorageName.with(name);
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.fileSystem();
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

    // MIN_LENGTH.......................................................................................................

    @Test
    public void testWithMinLengthFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> StorageName.with("")
        );

        this.checkEquals(
            "Empty \"name\"",
            thrown.getMessage()
        );
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
            "Length 256 of \"filename\" not between 1..255 = \"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"",
            thrown.getMessage()
        );
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
