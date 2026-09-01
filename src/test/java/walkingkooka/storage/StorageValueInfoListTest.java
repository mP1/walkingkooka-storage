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
import walkingkooka.datetime.HasOptionalLastModifiedTesting;
import walkingkooka.environment.HasAuditInfoTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.HasTextWithTextContextTesting;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallerTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StorageValueInfoListTest implements ListTesting2<StorageValueInfoList, StorageValueInfo>,
    ClassTesting<StorageValueInfoList>,
    HasAuditInfoTesting,
    HasOptionalLastModifiedTesting,
    HasTextTesting,
    HasTextWithTextContextTesting,
    ImmutableListTesting<StorageValueInfoList, StorageValueInfo>,
    JsonNodeMarshallerTesting<StorageValueInfoList>,
    TreePrintableTesting {

    private final static StorageValueInfo FILE1 = StorageValueInfo.with(
        StoragePath.parse("/file1"),
        AUDIT_INFO
    );

    private final static StorageValueInfo FILE2 = StorageValueInfo.with(
        StoragePath.parse("/file2"),
        AUDIT_INFO
    );

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageValueInfoList.with(null)
        );
    }

    @Test
    public void testWithDoesntDoubleWrap() {
        final StorageValueInfoList list = this.createList();
        assertSame(
            list,
            StorageValueInfoList.with(list)
        );
    }

    @Test
    public void testWithEmpty() {
        assertSame(
            StorageValueInfoList.EMPTY,
            StorageValueInfoList.with(
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
            FILE1 // expected
        );
    }

    @Test
    public void testGet2() {
        this.getAndCheck(
            this.createList(),
            1, // index
            FILE2 // expected
        );
    }

    @Test
    public void testSetFails() {
        this.setFails(
            this.createList(),
            0, // index
            FILE1 // expected
        );
    }

    @Test
    public void testRemoveIndexFails() {
        final StorageValueInfoList list = this.createList();

        this.removeIndexFails(
            list,
            0
        );
    }

    @Test
    public void testRemoveElementFails() {
        final StorageValueInfoList list = this.createList();

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
                        FILE1,
                        null
                    )
                )
        );
        this.checkEquals(
            "includes null StorageValueInfo",
            thrown.getMessage()
        );
    }

    @Override
    public StorageValueInfoList createList() {
        return StorageValueInfoList.with(
            Lists.of(
                FILE1,
                FILE2
            )
        );
    }

    // HasText..........................................................................................................

    @Test
    public void testText() {
        this.textAndCheck(
            this.createList(),
            "/file1\r\n" +
                "/file2\r\n"
        );
    }

    // HasTextWithTextContext...........................................................................................

    @Test
    public void testTextWithTextContextWithCrlf() {
        this.textWithTextContextAndCheck(
            this.createList(),
            LineEnding.CRNL,
            "/file1\r\n" +
                "/file2\r\n"
        );
    }

    @Test
    public void testTextWithTextContextWithNl() {
        this.textWithTextContextAndCheck(
            this.createList(),
            LineEnding.NL,
            "/file1\n" +
                "/file2\n"
        );
    }

    // Json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            this.createList(),
            "[\n" +
                "  {\n" +
                "    \"path\": \"/file1\",\n" +
                "    \"auditInfo\": {\n" +
                "      \"createdBy\": \"user123@example.com\",\n" +
                "      \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "      \"modifiedBy\": \"user123@example.com\",\n" +
                "      \"modifiedTimestamp\": \"1999-12-31T12:58:59\"\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"path\": \"/file2\",\n" +
                "    \"auditInfo\": {\n" +
                "      \"createdBy\": \"user123@example.com\",\n" +
                "      \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "      \"modifiedBy\": \"user123@example.com\",\n" +
                "      \"modifiedTimestamp\": \"1999-12-31T12:58:59\"\n" +
                "    }\n" +
                "  }\n" +
                "]"
        );
    }

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(
            "[\n" +
                "  {\n" +
                "    \"path\": \"/file1\",\n" +
                "    \"auditInfo\": {\n" +
                "      \"createdBy\": \"user123@example.com\",\n" +
                "      \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "      \"modifiedBy\": \"user123@example.com\",\n" +
                "      \"modifiedTimestamp\": \"1999-12-31T12:58:59\"\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"path\": \"/file2\",\n" +
                "    \"auditInfo\": {\n" +
                "      \"createdBy\": \"user123@example.com\",\n" +
                "      \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "      \"modifiedBy\": \"user123@example.com\",\n" +
                "      \"modifiedTimestamp\": \"1999-12-31T12:58:59\"\n" +
                "    }\n" +
                "  }\n" +
                "]",
            this.createList()
        );
    }

    @Override
    public StorageValueInfoList unmarshall(final JsonNode json,
                                           final JsonNodeUnmarshallContext context) {
        return StorageValueInfoList.unmarshall(
            json,
            context
        );
    }

    @Override
    public StorageValueInfoList createJsonNodeMarshallingValue() {
        return this.createList();
    }

    // TreePrintable....................................................................................................

    @Test
    public void testPrintTree() {
        this.treePrintAndCheck(
            this.createList(),
            "StorageValueInfoList\n" +
                "  /file1\n" +
                "    AuditInfo\n" +
                "      created\n" +
                "        user123@example.com 1999-12-31T12:58:59\n" +
                "      modified\n" +
                "        user123@example.com 1999-12-31T12:58:59\n" +
                "  /file2\n" +
                "    AuditInfo\n" +
                "      created\n" +
                "        user123@example.com 1999-12-31T12:58:59\n" +
                "      modified\n" +
                "        user123@example.com 1999-12-31T12:58:59\n"
        );
    }

    // firstOrEmpty.....................................................................................................

    @Test
    public void testFirstOrEmptyWhenEmpty() {
        this.firstOrEmptyAndCheck(
            StorageValueInfoList.EMPTY
        );
    }

    @Test
    public void testFirstOrEmptyWhenNotEmpty() {
        this.firstOrEmptyAndCheck(
            StorageValueInfoList.EMPTY.concat(FILE1)
                .concat(FILE2),
            FILE1
        );
    }

    // HasOptionalLastModified...................................................................................

    @Test
    public void testLastModifiedWhenEmpty() {
        this.lastModifiedAndCheck(
            StorageValueInfoList.EMPTY
        );
    }

    @Test
    public void testLastModifiedWhenOneStorageValueInfo() {
        this.lastModifiedAndCheck(
            StorageValueInfoList.EMPTY.concat(FILE1),
            FILE1.lastModified()
        );
    }

    @Test
    public void testLastModifiedWhenNotEmpty2() {
        this.lastModifiedAndCheck(
            StorageValueInfoList.EMPTY.concat(FILE1)
                .concat(
                    FILE2.setAuditInfo(
                        FILE2.auditInfo()
                            .setModifiedTimestamp(DIFFERENT_LAST_MODIFIED)
                    )
                ),
            DIFFERENT_LAST_MODIFIED
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageValueInfoList> type() {
        return StorageValueInfoList.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
