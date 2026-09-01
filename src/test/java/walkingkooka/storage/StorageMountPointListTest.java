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
import walkingkooka.text.HasTextWithTextContextTesting;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.TreePrintableTesting;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StorageMountPointListTest implements ListTesting2<StorageMountPointList, StorageMountPoint<?>>,
    ClassTesting<StorageMountPointList>,
    HasTextTesting,
    HasTextWithTextContextTesting,
    ImmutableListTesting<StorageMountPointList, StorageMountPoint<?>>,
    TreePrintableTesting {

    private final static StorageMountPoint<?> MOUNT_POINT1 = StorageMountPoint.with(
        StoragePath.parse("/mount1"),
        Storages.empty()
    );

    private final static StorageMountPoint<?> MOUNT_POINT2 = StorageMountPoint.with(
        StoragePath.parse("/mount2"),
        Storages.empty()
    );

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageMountPointList.with(null)
        );
    }

    @Test
    public void testWithDoesntDoubleWrap() {
        final StorageMountPointList list = this.createList();
        assertSame(
            list,
            StorageMountPointList.with(list)
        );
    }

    @Test
    public void testWithEmpty() {
        assertSame(
            StorageMountPointList.EMPTY,
            StorageMountPointList.with(
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
            MOUNT_POINT1 // expected
        );
    }

    @Test
    public void testGet2() {
        this.getAndCheck(
            this.createList(),
            1, // index
            MOUNT_POINT2 // expected
        );
    }

    @Test
    public void testSetFails() {
        this.setFails(
            this.createList(),
            0, // index
            MOUNT_POINT1 // expected
        );
    }

    @Test
    public void testRemoveIndexFails() {
        final StorageMountPointList list = this.createList();

        this.removeIndexFails(
            list,
            0
        );
    }

    @Test
    public void testRemoveElementFails() {
        final StorageMountPointList list = this.createList();

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
                        MOUNT_POINT1,
                        null
                    )
                )
        );
        this.checkEquals(
            "includes null StorageMountPoint",
            thrown.getMessage()
        );
    }

    @Override
    public StorageMountPointList createList() {
        return StorageMountPointList.with(
            Lists.of(
                MOUNT_POINT1,
                MOUNT_POINT2
            )
        );
    }

    // HasText..........................................................................................................

    @Test
    public void testText() {
        this.textAndCheck(
            this.createList(),
            "/mount1={}\r\n" +
                "/mount2={}\r\n"
        );
    }

    // HasTextWithTextContext...........................................................................................

    @Test
    public void testTextWithTextContextWithCrlf() {
        this.textWithTextContextAndCheck(
            this.createList(),
            LineEnding.CRNL,
            "/mount1={}\r\n" +
                "/mount2={}\r\n"
        );
    }

    @Test
    public void testTextWithTextContextWithNl() {
        this.textWithTextContextAndCheck(
            this.createList(),
            LineEnding.NL,
            "/mount1={}\n" +
                "/mount2={}\n"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testPrintTree() {
        this.treePrintAndCheck(
            this.createList(),
            "StorageMountPointList\n" +
                "  StorageMountPoint\n" +
                "    \"/mount1\"\n" +
                "      {} (walkingkooka.storage.StorageShared2Empty)\n" +
                "  StorageMountPoint\n" +
                "    \"/mount2\"\n" +
                "      {} (walkingkooka.storage.StorageShared2Empty)\n"
        );
    }

    // firstOrEmpty.....................................................................................................

    @Test
    public void testFirstOrEmptyWhenEmpty() {
        this.firstOrEmptyAndCheck(
            StorageMountPointList.EMPTY
        );
    }

    @Test
    public void testFirstOrEmptyWhenNotEmpty() {
        this.firstOrEmptyAndCheck(
            StorageMountPointList.EMPTY.concat(MOUNT_POINT1)
                .concat(MOUNT_POINT2),
            MOUNT_POINT1
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageMountPointList> type() {
        return StorageMountPointList.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
