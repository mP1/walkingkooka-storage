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
import walkingkooka.reflect.PublicClassTesting;
import walkingkooka.text.HasMultiLineTextTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallerTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class StoragePathListTest implements ListTesting2<StoragePathList, StoragePath>,
    HasTextTesting,
    HasMultiLineTextTesting,
    ImmutableListTesting<StoragePathList, StoragePath>,
    JsonNodeMarshallerTesting<StoragePathList>,
    PublicClassTesting<StoragePathList>,
    TreePrintableTesting {

    private final static StoragePath STORAGE_PATH1 = StoragePath.parse("/value111");

    private final static StoragePath STORAGE_PATH2 = StoragePath.parse("/value222");

    private final static StoragePath STORAGE_PATH3 = StoragePath.parse("/value333");

    private final static StoragePath STORAGE_PATH4 = StoragePath.parse("/value444");

    private final static StoragePath STORAGE_PATH5 = StoragePath.parse("/value555");

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
            "Includes null StoragePath",
            thrown.getMessage()
        );
    }

    @Test
    public void testSetElementsWithStoragePathList() {
        final StoragePathList expected = new StoragePathList(
            Lists.of(
                STORAGE_PATH1
            )
        );

        this.setElementsAndCheck(
            this.createList(),
            expected,
            expected
        );
    }

    // subList..........................................................................................................

    private final static List<StoragePath> ELEMENTS = Lists.of(
        STORAGE_PATH1,
        STORAGE_PATH2,
        STORAGE_PATH3,
        STORAGE_PATH4,
        STORAGE_PATH5
    );

    @Test
    public void testSubList() {
        this.subListAndCheck(
            new StoragePathList(ELEMENTS),
            0,
            2,
            new StoragePathList(
                Lists.of(
                    STORAGE_PATH1,
                    STORAGE_PATH2
                )
            )
        );
    }

    @Test
    public void testSubList2() {
        this.subListAndCheck(
            new StoragePathList(ELEMENTS),
            2,
            4,
            new StoragePathList(
                Lists.of(
                    STORAGE_PATH3,
                    STORAGE_PATH4
                )
            )
        );
    }

    @Test
    public void testSubListSubList() {
        this.subListAndCheck(
            new StoragePathList(ELEMENTS)
                .subList(
                    1,
                    4
                ), // 2, 3, 4
            1,
            3,
            new StoragePathList(
                Lists.of(
                    STORAGE_PATH3,
                    STORAGE_PATH4
                )
            )
        );
    }

    @Override
    public StoragePathList createList() {
        return new StoragePathList(
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

    // HasMultiLineText...........................................................................................

    @Test
    public void testMultiLineTextWithCrlf() {
        this.multiLineTextAndCheck(
            this.createList(),
            LineEnding.CRNL,
            "/value111\r\n" +
                "/value222\r\n"
        );
    }

    @Test
    public void testMultiLineTextWithNl() {
        this.multiLineTextAndCheck(
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
}
