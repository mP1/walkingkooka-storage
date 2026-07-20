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
import walkingkooka.Binary;
import walkingkooka.HasBinaryTesting;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.naming.HasPathTesting;
import walkingkooka.net.header.HasContentTypeTesting;
import walkingkooka.net.header.MediaType;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.printer.TreePrintableTesting;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageBinaryTest implements HasPathTesting,
    HasBinaryTesting,
    HasContentTypeTesting,
    TreePrintableTesting,
    HashCodeEqualsDefinedTesting2<StorageBinary>,
    ClassTesting<StorageBinary>,
    ToStringTesting<StorageBinary> {

    private final static StoragePath PATH = StoragePath.parse("/file.txt");

    private final static Binary BINARY = Binary.EMPTY;

    // with.............................................................................................................

    @Test
    public void testWithNullPathFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageBinary.with(
                null,
                BINARY
            )
        );
    }

    @Test
    public void testWithNullBinaryFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageBinary.with(
                PATH,
                null
            )
        );
    }

    @Test
    public void testWith() {
        final StorageBinary storageBinary = StorageBinary.with(PATH, BINARY);
        this.pathAndCheck(
            storageBinary,
            PATH
        );
        this.binaryAndCheck(
            storageBinary,
            BINARY
        );
    }

    // setContentType...................................................................................................

    @Test
    public void testSetContentTypeWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .setContentType(null)
        );
    }

    @Test
    public void testSetContentTypeWithSame() {
        final StorageBinary storageBinary = this.createObject();

        assertSame(
            storageBinary,
            storageBinary.setContentType(StorageBinary.NO_CONTENT_TYPE)
        );
    }

    @Test
    public void testSetContentTypeWithSame2() {
        final Optional<MediaType> contentType = Optional.of(MediaType.TEXT_PLAIN);

        final StorageBinary storageBinary = this.createObject()
                .setContentType(contentType);

        assertSame(
            storageBinary,
            storageBinary.setContentType(contentType)
        );
    }

    @Test
    public void testSetContentTypeWithDifferent() {
        final Optional<MediaType> contentType = Optional.of(MediaType.TEXT_PLAIN);

        final StorageBinary storageBinary = this.createObject()
            .setContentType(contentType);

        final Optional<MediaType> differentContentType = Optional.of(
            MediaType.parse("text/different")
        );

        final StorageBinary different = storageBinary.setContentType(differentContentType);
        assertNotSame(
            storageBinary,
            different
        );

        this.contentTypeAndCheck(
            different,
            differentContentType
        );
    }

    // clearContentType.................................................................................................

    @Test
    public void testClearContentTypeWithDifferent() {
        final Optional<MediaType> contentType = Optional.of(MediaType.TEXT_PLAIN);

        final StorageBinary storageBinary = this.createObject()
            .setContentType(contentType);

        final StorageBinary different = storageBinary.clearContentType();
        assertNotSame(
            storageBinary,
            different
        );

        this.contentTypeAndCheck(different);
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentPath() {
        this.checkNotEquals(
            StorageBinary.with(
                StoragePath.parse("/different.txt"),
                BINARY
            )
        );
    }

    @Test
    public void testEqualsDifferentBinary() {
        this.checkNotEquals(
            StorageBinary.with(
                PATH,
                Binary.with(
                    "different".getBytes(Charset.defaultCharset())
                )
            )
        );
    }

    @Test
    public void testEqualsDifferentContentType() {
        this.checkNotEquals(
            this.createObject()
                .setContentType(
                    Optional.of(MediaType.TEXT_PLAIN)
                )
        );
    }

    @Override
    public StorageBinary createObject() {
        return StorageBinary.with(
            PATH,
            BINARY
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            StorageBinary.with(PATH, BINARY),
            "/file.txt "
        );
    }

    @Test
    public void testToStringWithContentType() {
        this.toStringAndCheck(
            StorageBinary.with(PATH, BINARY)
                .setContentType(
                    Optional.of(MediaType.TEXT_PLAIN)
                ),
            "/file.txt text/plain"
        );
    }

    @Test
    public void testToStringWithBinaryAndContentType() {
        this.toStringAndCheck(
            StorageBinary.with(PATH, Binary.with("Hello".getBytes(StandardCharsets.UTF_8)))
                .setContentType(
                    Optional.of(MediaType.TEXT_PLAIN)
                ),
            "/file.txt text/plain48 65 6c 6c 6f                                              Hello               \n"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintable() {
        this.treePrintAndCheck(
            StorageBinary.with(
                PATH,
                Binary.with(
                    "The quick brown fox jumps over the lazy dog".getBytes(Charset.defaultCharset())
                )
            ),
            "/file.txt\n" +
                "  54 68 65 20 71 75 69 63 6b 20 62 72 6f 77 6e 20 66 6f 78 20 The quick brown fox \n" +
                "  6a 75 6d 70 73 20 6f 76 65 72 20 74 68 65 20 6c 61 7a 79 20 jumps over the lazy \n" +
                "  64 6f 67                                                    dog                 \n"
        );
    }

    @Test
    public void testTreePrintableWithContentType() {
        this.treePrintAndCheck(
            StorageBinary.with(
                PATH,
                Binary.with(
                    "The quick brown fox jumps over the lazy dog".getBytes(Charset.defaultCharset())
                )
            ).setContentType(
                Optional.of(MediaType.TEXT_PLAIN)
            ),
            "/file.txt\n" +
                "  text/plain\n" +
                "    54 68 65 20 71 75 69 63 6b 20 62 72 6f 77 6e 20 66 6f 78 20 The quick brown fox \n" +
                "    6a 75 6d 70 73 20 6f 76 65 72 20 74 68 65 20 6c 61 7a 79 20 jumps over the lazy \n" +
                "    64 6f 67                                                    dog                 \n"
        );
    }

    // with.............................................................................................................

    @Override
    public Class<StorageBinary> type() {
        return StorageBinary.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
