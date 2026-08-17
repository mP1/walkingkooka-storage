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
import walkingkooka.collect.list.ImmutableListTesting;
import walkingkooka.collect.list.ListTesting2;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.HasTextWithLineBreaksTesting;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StoragePathListTest implements ListTesting2<StoragePathList, StoragePath>,
    ClassTesting<StoragePathList>,
    HasTextTesting,
    HasTextWithLineBreaksTesting,
    ImmutableListTesting<StoragePathList, StoragePath>,
    JsonNodeMarshallingTesting<StoragePathList>,
    TreePrintableTesting {

    private final static StoragePath STORAGE_PATH1 = StoragePath.parse("/value111");

    private final static StoragePath STORAGE_PATH2 = StoragePath.parse("/value222");

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> StoragePathList.with(null)
        );
    }

    @Test
    public void testWithDoesntDoubleWrap() {
        final StoragePathList list = this.createList();
        assertSame(
            list,
            StoragePathList.with(list)
        );
    }

    @Test
    public void testWithEmpty() {
        assertSame(
            StoragePathList.EMPTY,
            StoragePathList.with(
                Lists.empty()
            )
        );
    }

    // list.............................................................................................................

    @Test
    public void testGet() {
        this.getAndCheck(
            this.createList(),
            0, // index
            STORAGE_PATH1 // expected
        );
    }

    @Test
    public void testGet2() {
        this.getAndCheck(
            this.createList(),
            1, // index
            STORAGE_PATH2 // expected
        );
    }

    @Test
    public void testSetFails() {
        this.setFails(
            this.createList(),
            0, // index
            STORAGE_PATH1 // expected
        );
    }

    @Test
    public void testRemoveIndexFails() {
        final StoragePathList list = this.createList();

        this.removeIndexFails(
            list,
            0
        );
    }

    @Test
    public void testRemoveElementFails() {
        final StoragePathList list = this.createList();

        this.removeFails(
            list,
            list.get(0)
        );
    }

    @Test
    public void testSetElementsIncludesNullFails() {
        final NullPointerException thrown = assertThrows(
            NullPointerException.class,
            () -> this.createList()
                .setElements(
                    Lists.of(
                        STORAGE_PATH1,
                        null
                    )
                )
        );
        this.checkEquals(
            "includes null StoragePath",
            thrown.getMessage()
        );
    }

    @Override
    public StoragePathList createList() {
        return StoragePathList.with(
            Lists.of(
                STORAGE_PATH1,
                STORAGE_PATH2
            )
        );
    }

    // HasText..........................................................................................................

    @Test
    public void testText() {
        this.textAndCheck(
            this.createList(),
            "/value111\r\n" +
                "/value222\r\n"
        );
    }

    // HasTextWithLineBreaks............................................................................................

    @Test
    public void testTextWithLineBreaksWithCrlf() {
        this.textWithLineBreaksAndCheck(
            this.createList(),
            LineEnding.CRNL,
            "/value111\r\n" +
                "/value222\r\n"
        );
    }

    @Test
    public void testTextWithLineBreaksWithNl() {
        this.textWithLineBreaksAndCheck(
            this.createList(),
            LineEnding.NL,
            "/value111\n" +
                "/value222\n"
        );
    }

    // Json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            this.createList(),
            "[\n" +
                "  \"/value111\",\n" +
                "  \"/value222\"\n" +
                "]"
        );
    }

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(
            "[\n" +
                "  \"/value111\",\n" +
                "  \"/value222\"\n" +
                "]",
            this.createList()
        );
    }

    @Override
    public StoragePathList unmarshall(final JsonNode json,
                                           final JsonNodeUnmarshallContext context) {
        return StoragePathList.unmarshall(
            json,
            context
        );
    }

    @Override
    public StoragePathList createJsonNodeMarshallingValue() {
        return this.createList();
    }

    // TreePrintable....................................................................................................

    @Test
    public void testPrintTree() {
        this.treePrintAndCheck(
            this.createList(),
            "StoragePathList\n" +
                "  /value111\n" +
                "  /value222\n"
        );
    }

    // firstOrEmpty.....................................................................................................

    @Test
    public void testFirstOrEmptyWhenEmpty() {
        this.firstOrEmptyAndCheck(
            StoragePathList.EMPTY
        );
    }

    @Test
    public void testFirstOrEmptyWhenNotEmpty() {
        this.firstOrEmptyAndCheck(
            StoragePathList.EMPTY.concat(STORAGE_PATH1)
                .concat(STORAGE_PATH2),
            STORAGE_PATH1
        );
    }

    // class............................................................................................................

    @Override
    public Class<StoragePathList> type() {
        return StoragePathList.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
