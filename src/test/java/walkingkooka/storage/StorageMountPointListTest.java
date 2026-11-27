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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class StorageMountPointListTest implements ListTesting2<StorageMountPointList, StorageMountPoint<?>>,
    HasTextTesting,
    HasMultiLineTextTesting,
    ImmutableListTesting<StorageMountPointList, StorageMountPoint<?>>,
    PublicClassTesting<StorageMountPointList>,
    TreePrintableTesting {

    private final static StorageMountPoint<?> STORAGE_MOUNT_POINT1 = StorageMountPoint.with(
        StoragePath.parse("/mount1"),
        Storages.empty()
    );

    private final static StorageMountPoint<?> STORAGE_MOUNT_POINT2 = StorageMountPoint.with(
        StoragePath.parse("/mount2"),
        Storages.empty()
    );

    private final static StorageMountPoint<?> STORAGE_MOUNT_POINT3 = StorageMountPoint.with(
        StoragePath.parse("/mount3"),
        Storages.empty()
    );

    private final static StorageMountPoint<?> STORAGE_MOUNT_POINT4 = StorageMountPoint.with(
        StoragePath.parse("/mount4"),
        Storages.empty()
    );

    private final static StorageMountPoint<?> STORAGE_MOUNT_POINT5 = StorageMountPoint.with(
        StoragePath.parse("/mount5"),
        Storages.empty()
    );

    // list.............................................................................................................

    @Test
    public void testGet() {
        this.getAndCheck(
            this.createList(),
            0, // index
            STORAGE_MOUNT_POINT1 // expected
        );
    }

    @Test
    public void testGet2() {
        this.getAndCheck(
            this.createList(),
            1, // index
            STORAGE_MOUNT_POINT2 // expected
        );
    }

    @Test
    public void testSetFails() {
        this.setFails(
            this.createList(),
            0, // index
            STORAGE_MOUNT_POINT1 // expected
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
                        STORAGE_MOUNT_POINT1,
                        null
                    )
                )
        );
        this.checkEquals(
            "Includes null StorageMountPoint",
            thrown.getMessage()
        );
    }

    @Test
    public void testSetElementsWithEmpty() {
        final StorageMountPointList list = this.createList();

        this.setElementsAndCheck(
            list,
            StorageMountPointList.EMPTY,
            StorageMountPointList.EMPTY
        );
    }

    // subList..........................................................................................................

    private final static List<StorageMountPoint<?>> ELEMENTS = Lists.of(
        STORAGE_MOUNT_POINT1,
        STORAGE_MOUNT_POINT2,
        STORAGE_MOUNT_POINT3,
        STORAGE_MOUNT_POINT4,
        STORAGE_MOUNT_POINT5
    );

    @Test
    public void testSubList() {
        this.subListAndCheck(
            new StorageMountPointList(ELEMENTS),
            0,
            2,
            new StorageMountPointList(
                Lists.of(
                    STORAGE_MOUNT_POINT1,
                    STORAGE_MOUNT_POINT2
                )
            )
        );
    }

    @Test
    public void testSubList2() {
        this.subListAndCheck(
            new StorageMountPointList(ELEMENTS),
            2,
            4,
            new StorageMountPointList(
                Lists.of(
                    STORAGE_MOUNT_POINT3,
                    STORAGE_MOUNT_POINT4
                )
            )
        );
    }

    @Test
    public void testSubListSubList() {
        this.subListAndCheck(
            new StorageMountPointList(ELEMENTS)
                .subList(
                    1,
                    4
                ), // 2, 3, 4
            1,
            3,
            new StorageMountPointList(
                Lists.of(
                    STORAGE_MOUNT_POINT3,
                    STORAGE_MOUNT_POINT4
                )
            )
        );
    }

    @Override
    public StorageMountPointList createList() {
        return new StorageMountPointList(
            Lists.of(
                STORAGE_MOUNT_POINT1,
                STORAGE_MOUNT_POINT2
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

    // HasMultiLineText...........................................................................................

    @Test
    public void testMultiLineTextWithCrlf() {
        this.multiLineTextAndCheck(
            this.createList(),
            LineEnding.CRNL,
            "/mount1={}\r\n" +
                "/mount2={}\r\n"
        );
    }

    @Test
    public void testMultiLineTextWithNl() {
        this.multiLineTextAndCheck(
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
            StorageMountPointList.EMPTY.concat(STORAGE_MOUNT_POINT1)
                .concat(STORAGE_MOUNT_POINT2),
            STORAGE_MOUNT_POINT1
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageMountPointList> type() {
        return StorageMountPointList.class;
    }
}
