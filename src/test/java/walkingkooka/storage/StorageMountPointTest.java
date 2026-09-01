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
import walkingkooka.Cast;
import walkingkooka.naming.HasPathTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.HasTextWithTextContextTesting;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.TreePrintableTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageMountPointTest implements TreePrintableTesting,
    HasPathTesting,
    HasTextTesting,
    HasTextWithTextContextTesting,
    ClassTesting2<StorageMountPoint<StorageContext>> {

    private final static Storage<StorageContext> STORAGE = Storages.fake();

    // with.............................................................................................................

    @Test
    public void testWithNullPathFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageMountPoint.with(
                null,
                STORAGE
            )
        );
    }

    @Test
    public void testWithNullStorageFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageMountPoint.with(
                StoragePath.ROOT,
                null
            )
        );
    }

    @Test
    public void testWithRootPath() {
        final StorageMountPoint<StorageContext> storageMountPoint = StorageMountPoint.with(
            StoragePath.ROOT,
            STORAGE
        );

        this.pathAndCheck(
            storageMountPoint,
            StoragePath.ROOT
        );

        this.storageAndCheck(
            storageMountPoint,
            STORAGE
        );
    }

    @Test
    public void testWith() {
        final StoragePath storagePath = StoragePath.parse("/value111");

        final StorageMountPoint<StorageContext> storageMountPoint = StorageMountPoint.with(
            storagePath,
            STORAGE
        );

        this.pathAndCheck(
            storageMountPoint,
            storagePath
        );

        this.storageAndCheck(
            storageMountPoint,
            STORAGE
        );
    }

    @Test
    public void testWithPathTrailingSlash() {
        final StorageMountPoint<StorageContext> storageMountPoint = StorageMountPoint.with(
            StoragePath.parse("/path1/"),
            STORAGE
        );

        this.pathAndCheck(
            storageMountPoint,
            StoragePath.parse("/path1")
        );

        this.storageAndCheck(
            storageMountPoint,
            STORAGE
        );
    }

    private void storageAndCheck(final StorageMountPoint<StorageContext> storageMountPoint,
                                 final Storage<StorageContext> expected) {
        this.checkEquals(
            expected,
            storageMountPoint.storage(),
            storageMountPoint::toString
        );
    }

    // add..............................................................................................................

    @Test
    public void testAddRootWithRoot() {
        this.addAndCheck(
            "/",
            "/",
            "/"
        );
    }

    @Test
    public void testAddRootWithNonRoot() {
        this.addAndCheck(
            "/",
            "/file123",
            "/file123"
        );
    }

    @Test
    public void testAddNonRoot() {
        this.addAndCheck(
            "/dir123",
            "/file456",
            "/dir123/file456"
        );
    }

    @Test
    public void testAddNonRoot2() {
        this.addAndCheck(
            "/dir123/dir456",
            "/file456",
            "/dir123/dir456/file456"
        );
    }

    private void addAndCheck(final String route,
                             final String path,
                             final String expected) {
        this.addAndCheck(
            StoragePath.parse(route),
            StoragePath.parse(path),
            StoragePath.parse(expected)
        );
    }

    private void addAndCheck(final StoragePath route,
                             final StoragePath path,
                             final StoragePath expected) {
        this.addAndCheck(
            this.createMountPoint(route),
            path,
            expected
        );
    }

    private void addAndCheck(final StorageMountPoint<StorageContext> route,
                             final StoragePath path,
                             final StoragePath expected) {
        this.checkEquals(
            expected,
            route.add(path),
            route + " add " + path
        );
    }

    // remove...........................................................................................................

    @Test
    public void testRemoveRootWithRoot() {
        this.removeAndCheck(
            "/",
            "/",
            "/"
        );
    }

    @Test
    public void testRemoveRootWithNonRoot() {
        this.removeAndCheck(
            "/",
            "/file123",
            "/file123"
        );
    }

    @Test
    public void testRemoveNonRoot() {
        this.removeAndCheck(
            "/dir123",
            "/dir123/file456",
            "/file456"
        );
    }

    @Test
    public void testRemoveNonRoot2() {
        this.removeAndCheck(
            "/dir123/dir456",
            "/dir123/dir456/file456",
            "/file456"
        );
    }

    private void removeAndCheck(final String route,
                                final String path,
                                final String expected) {
        this.removeAndCheck(
            StoragePath.parse(route),
            StoragePath.parse(path),
            StoragePath.parse(expected)
        );
    }

    private void removeAndCheck(final StoragePath route,
                                final StoragePath path,
                                final StoragePath expected) {
        this.removeAndCheck(
            this.createMountPoint(route),
            path,
            expected
        );
    }

    private void removeAndCheck(final StorageMountPoint<StorageContext> route,
                                final StoragePath path,
                                final StoragePath expected) {
        this.checkEquals(
            expected,
            route.remove(path),
            route + " remove " + path
        );
    }

    // isMatch..........................................................................................................

    @Test
    public void testIsMatchRootWithRoot() {
        this.isMatchAndCheck(
            "/",
            "/",
            true
        );
    }

    @Test
    public void testIsMatchRootWithNonRoot() {
        this.isMatchAndCheck(
            "/",
            "/file-under",
            true
        );
    }

    @Test
    public void testIsMatchDirWithPathUnder() {
        this.isMatchAndCheck(
            "/path123",
            "/path123/file-under",
            true
        );
    }

    @Test
    public void testIsMatchDirWithPathUnder2() {
        this.isMatchAndCheck(
            "/path111/path222",
            "/path111/path222/file-under",
            true
        );
    }

    @Test
    public void testIsMatchDirWithPathUnder3() {
        this.isMatchAndCheck(
            "/path111/path222",
            "/path111/path222/path333/file-under",
            true
        );
    }

    @Test
    public void testIsMatchDirWithPathNotUnder() {
        this.isMatchAndCheck(
            "/path123",
            "/path456/file-under",
            false
        );
    }

    @Test
    public void testIsMatchDirWithPathNotUnder2() {
        this.isMatchAndCheck(
            "/path111/path222/mount",
            "/path111/path222/under",
            false
        );
    }

    @Test
    public void testIsMatchDirWithPathNotUnder3() {
        this.isMatchAndCheck(
            "/path123",
            "/file-under",
            false
        );
    }

    private void isMatchAndCheck(final String route,
                                 final String path,
                                 final boolean expected) {
        this.isMatchAndCheck(
            StoragePath.parse(route),
            StoragePath.parse(path),
            expected
        );
    }

    private void isMatchAndCheck(final StoragePath route,
                                 final StoragePath path,
                                 final boolean expected) {
        this.isMatchAndCheck(
            this.createMountPoint(route),
            path,
            expected
        );
    }

    private void isMatchAndCheck(final StorageMountPoint<?> route,
                                 final StoragePath path,
                                 final boolean expected) {
        this.checkEquals(
            expected,
            route.isMatch(path),
            "isMatch " + path
        );
    }

    // helpers..........................................................................................................

    private StorageMountPoint<StorageContext> createMountPoint(final StoragePath path) {
        return StorageMountPoint.with(
            path,
            Storages.fake()
        );
    }

    // HasText..........................................................................................................

    @Test
    public void testHasText() {
        this.textAndCheck(
            StorageMountPoint.with(
                StoragePath.parse("/path1/path2"),
                Storages.treeMapStore()
            ),
            "/path1/path2={}"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testPrintTree() {
        this.treePrintAndCheck(
            StorageMountPoint.with(
                StoragePath.parse("/path1/path2"),
                Storages.treeMapStore()
            ),
            "StorageMountPoint\n" +
                "  \"/path1/path2\"\n" +
                "    StorageShared2TreeMapStore\n" +
                "      TreeMapStore\n"
        );
    }

    // HasTextWithTextContext...........................................................................................

    @Test
    public void testTextWithTextContextCr() {
        this.textWithTextContextAndCheck(
            StorageMountPoint.with(
                StoragePath.parse("/path1/path2"),
                Storages.treeMapStore()
            ),
            LineEnding.CR,
            "/path1/path2={}\r"
        );
    }

    @Test
    public void testTextWithTextContextNl() {
        this.textWithTextContextAndCheck(
            StorageMountPoint.with(
                StoragePath.parse("/path1/path2"),
                Storages.treeMapStore()
            ),
            LineEnding.NL,
            "/path1/path2={}\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageMountPoint<StorageContext>> type() {
        return Cast.to(StorageMountPoint.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
