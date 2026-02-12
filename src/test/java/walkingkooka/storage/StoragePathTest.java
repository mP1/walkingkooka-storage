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
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.PathSeparator;
import walkingkooka.naming.PathTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

final public class StoragePathTest implements PathTesting<StoragePath, StorageName>,
    ClassTesting2<StoragePath>,
    ParseStringTesting<StoragePath>,
    JsonNodeMarshallingTesting<StoragePath>,
    TreePrintableTesting {

    @Override
    public void testAllConstructorsVisibility() {
    }

    @Override
    public void testIfClassIsFinalIfAllConstructorsArePrivate() {
    }

    // parse............................................................................................................

    @Test
    public void testParseMissingRequiredLeadingSlashFails() {
        this.parseStringFails(
            "without-leading-slash",
            IllegalArgumentException.class
        );
    }

    @Test
    public void testParseSlash() {
        final String value = "/";

        final StoragePath path = StoragePath.parse(value);
        this.valueCheck(
            path,
            value
        );
        this.rootCheck(path);
        this.nameCheck(
            path,
            StorageName.ROOT
        );
        this.parentAbsentCheck(path);
    }

    @Test
    public void testParseFlat() {
        final String value = "/path to";

        final StoragePath path = StoragePath.parse(value);
        this.valueCheck(path, value);
        this.rootNotCheck(path);
        this.nameCheck(
            path,
            StorageName.with("path to")
        );
        this.parentSame(
            path,
            StoragePath.ROOT
        );
    }

    @Test
    public void testParseEmptyComponentNormalized() {
        final String value = "/path1//path2";

        final StoragePath path = StoragePath.parse(value);
        this.valueCheck(path, "/path1/path2"); // normalized
        this.rootNotCheck(path);
        this.nameCheck(
            path,
            StorageName.with("path2")
        );
        this.parentCheck(
            path,
            "/path1"
        );
    }

    @Test
    public void testParseHierarchical() {
        final String value = "/path/to";
        final StoragePath path = StoragePath.parse(value);
        this.valueCheck(path, value);
        this.rootNotCheck(path);
        this.nameCheck(
            path,
            StorageName.with("to")
        );
        this.parentCheck(
            path,
            "/path"
        );
    }

    @Test
    public void testParseHierarchical2() {
        final String value = "/path/to/xyz";

        final StoragePath path = StoragePath.parse(value);
        this.valueCheck(path, value);
        this.rootNotCheck(path);
        this.nameCheck(
            path,
            StorageName.with("xyz")
        );

        this.parentCheck(
            path,
            "/path/to"
        );

        final StoragePath parent = path.parent()
            .get();

        this.valueCheck(
            parent,
            "/path/to"
        );
        this.rootNotCheck(parent);
        this.nameCheck(
            parent,
            StorageName.with("to")
        );
        this.parentCheck(
            parent,
            "/path"
        );
    }

    @Test
    public void testParseIncludesDot() {
        final StoragePath path = StoragePath.parse("/path1/./path2/./path3");
        this.valueCheck(
            path,
            "/path1/path2/path3"
        );
        this.rootNotCheck(path);
        this.nameCheck(
            path,
            StorageName.with("path3")
        );

        this.parentCheck(
            path,
            "/path1/path2"
        );
    }

    @Test
    public void testParseIncludesTrailingDot() {
        final StoragePath path = StoragePath.parse("/path1/path2/path3/.");
        this.valueCheck(
            path,
            "/path1/path2/path3"
        );
        this.rootNotCheck(path);
        this.nameCheck(
            path,
            StorageName.with("path3")
        );

        this.parentCheck(
            path,
            "/path1/path2"
        );
    }

    @Test
    public void testParseIncludesDoubleDot() {
        final StoragePath path = StoragePath.parse("/path1/./path2/../path3");
        this.valueCheck(
            path,
            "/path1/path3"
        );
        this.rootNotCheck(path);
        this.nameCheck(
            path,
            StorageName.with("path3")
        );

        this.parentCheck(
            path,
            "/path1"
        );
    }

    @Test
    public void testParseIncludesTrailingDoubleDot() {
        final StoragePath path = StoragePath.parse("/path1/path2/path3/..");
        this.valueCheck(
            path,
            "/path1/path2"
        );
        this.rootNotCheck(path);
        this.nameCheck(
            path,
            StorageName.with("path2")
        );

        this.parentCheck(
            path,
            "/path1"
        );
    }

    // ParseStringTesting ..............................................................................................

    @Override
    public StoragePath parseString(final String text) {
        return StoragePath.parse(text);
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return expected;
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> expected) {
        return expected;
    }

    // parseMaybeRelative ..............................................................................................

    @Test
    public void testParseSpecialStringWithAbsolutePath() {
        final String text = "/path123/file456.txt";

        this.parseSpecialAndCheck(
            text
        );
    }

    @Test
    public void testParseSpecialPath() {
        final String text = "after4.txt";

        this.parseSpecialAndCheck(
            text,
            CWD + "/" + text
        );
    }

    @Test
    public void testParseSpecialPathAndCurrentWorkingDirectoryWithEndingSlash() {
        final String text = "after4.txt";

        this.parseSpecialAndCheck(
            text,
            new FakeHasUserDirectories() {
                @Override
                public Optional<StoragePath> currentWorkingDirectory() {
                    return Optional.of(
                        StoragePath.parse(CWD + "/")
                    );
                }
            },
            CWD + "/" + text
        );
    }

    private final static String CWD = "/current1/working2/directory3";

    private void parseSpecialAndCheck(final String text) {
        this.parseSpecialAndCheck(
            text,
            text
        );
    }

    private void parseSpecialAndCheck(final String text,
                                      final String expected) {
        this.parseSpecialAndCheck(
            text,
            new FakeHasUserDirectories() {
                @Override
                public Optional<StoragePath> currentWorkingDirectory() {
                    return Optional.of(
                        StoragePath.parse(CWD)
                    );
                }
            },
            expected
        );
    }

    private void parseSpecialAndCheck(final String text,
                                      final HasUserDirectories has,
                                      final String expected) {
        this.checkEquals(
            StoragePath.parse(expected),
            StoragePath.parseSpecial(
                text,
                has
            )
        );
    }

    // path.............................................................................................................

    @Test
    public void testRoot() {
        final StoragePath path = StoragePath.ROOT;
        this.rootCheck(path);
        this.valueCheck(path, "/");
        this.nameSameCheck(
            path,
            StorageName.ROOT
        );
        this.parentAbsentCheck(path);
    }

    // appendName.......................................................................................................

    @Test
    public void testAppendNameWithRoot() {
        final StorageName name = StorageName.with("name1");

        final StoragePath path = StoragePath.ROOT.append(name);
        this.rootNotCheck(path);
        this.valueCheck(path, "/name1");
        this.nameCheck(
            path,
            name
        );
    }

    @Test
    public void testAppendNameToNonRoot() {
        final StoragePath parent = StoragePath.parse("/parent1");
        final StorageName name = StorageName.with("name2");

        final StoragePath path = parent.append(name);
        this.rootNotCheck(path);
        this.valueCheck(path, "/parent1/name2");
        this.nameCheck(
            path,
            name
        );
    }

    // appendPath.......................................................................................................

    @Test
    public void testAppendPathWithRoot() {
        final StoragePath path = StoragePath.parse("/path1");
        assertSame(
            path,
            path.append(StoragePath.ROOT)
        );
    }

    @Test
    public void testAppendPathWithRoot2() {
        final StoragePath path = StoragePath.parse("/path1/path2");
        assertSame(
            path,
            path.append(StoragePath.ROOT)
        );
    }

    @Test
    public void testAppendPathToNonRoot() {
        final StoragePath parent = StoragePath.parse("/parent1");
        final StoragePath path2 = StoragePath.parse("/path2");

        final StoragePath path = parent.append(path2);
        this.rootNotCheck(path);
        this.valueCheck(
            path,
            "/parent1/path2"
        );
        this.nameCheck(
            path,
            StorageName.with("path2")
        );
        this.parentCheck(
            path,
            "/parent1"
        );
    }

    @Test
    public void testAppendPathToNonRootTwice() {
        final StoragePath parent = StoragePath.parse("/parent1");
        final StoragePath path2 = StoragePath.parse("/path2");
        final StoragePath path34 = StoragePath.parse("/path3/path4");

        final StoragePath path = parent.append(path2)
            .append(path34);
        this.rootNotCheck(path);
        this.valueCheck(
            path,
            "/parent1/path2/path3/path4"
        );
        this.nameCheck(
            path,
            StorageName.with("path4")
        );
        this.parentCheck(
            path,
            "/parent1/path2/path3"
        );
    }

    // prepend(StorageName).............................................................................................

    @Test
    public void testPrependNameWithNullNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> StoragePath.ROOT.prepend(
                (StorageName) null
            )
        );
    }

    @Test
    public void testPrependNameWithRoot() {
        this.prependNameAndCheck(
            StoragePath.parse("/path1"),
            StorageName.ROOT
        );
    }

    @Test
    public void testPrependNameWithCurrent() {
        this.prependNameAndCheck(
            "/path1",
            ".",
            "/path1"
        );
    }

    @Test
    public void testPrependNameWithParent() {
        this.prependNameAndCheck(
            "/path1",
            "..",
            "/"
        );
    }

    @Test
    public void testPrependName() {
        this.prependNameAndCheck(
            "/path2",
            "path1",
            "/path1/path2"
        );
    }

    private void prependNameAndCheck(final StoragePath path,
                                     final StorageName name) {
        assertSame(
            path,
            path.prepend(name),
            () -> path + " prepend " + name
        );
    }

    private void prependNameAndCheck(final String path,
                                     final String name,
                                     final String expected) {
        this.prependNameAndCheck(
            this.parsePath(path),
            StorageName.with(name),
            this.parsePath(expected)
        );
    }

    private void prependNameAndCheck(final StoragePath path,
                                     final StorageName name,
                                     final StoragePath expected) {
        this.checkEquals(
            expected,
            path.prepend(name),
            () -> path + " prepend " + name
        );
    }

    // prepend(StoragePath).............................................................................................

    @Test
    public void testPrependPathWithNullPathFails() {
        assertThrows(
            NullPointerException.class,
            () -> StoragePath.ROOT.prepend(
                (StoragePath) null
            )
        );
    }

    @Test
    public void testPrependPathWithRootPath() {
        this.prependPathAndCheck(
            StoragePath.parse("/path1"),
            StoragePath.ROOT
        );
    }

    @Test
    public void testPrependPathWithCurrent() {
        final StoragePath parent = StoragePath.parse("/path1");

        assertSame(
            parent,
            parent.prepend(
                this.parsePath("/.")
            )
        );
    }

    @Test
    public void testPrependPath() {
        this.prependPathAndCheck(
            "/path1",
            "/path2",
            "/path2/path1"
        );
    }

    @Test
    public void testPrependPath2() {
        this.prependPathAndCheck(
            "/path3/path4",
            "/path1/path2",
            "/path1/path2/path3/path4"
        );
    }

    @Test
    public void testPrependPath3() {
        this.prependPathAndCheck(
            "/path3/path4",
            "/path1/./lost/../path2",
            "/path1/path2/path3/path4"
        );
    }

    private void prependPathAndCheck(final String path,
                                     final String prepend) {
        this.prependPathAndCheck(
            this.parsePath(path),
            this.parsePath(prepend)
        );
    }

    private void prependPathAndCheck(final StoragePath path,
                                     final StoragePath prepend) {
        assertSame(
            path,
            path.prepend(prepend),
            () -> path + " prepend " + prepend
        );
    }

    private void prependPathAndCheck(final String path,
                                     final String prepend,
                                     final String expected) {
        this.prependPathAndCheck(
            this.parsePath(path),
            this.parsePath(prepend),
            this.parsePath(expected)
        );
    }

    private void prependPathAndCheck(final StoragePath path,
                                     final StoragePath prepend,
                                     final StoragePath expected) {
        this.checkEquals(
            expected,
            path.prepend(prepend),
            () -> path + " prepend " + prepend
        );
    }

    // removePrefix.....................................................................................................

    @Test
    public void testRemovePrefixWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> StoragePath.parse("/path123")
                .removePrefix(null)
        );
    }

    @Test
    public void testRemovePrefixWithRoot() {
        final StoragePath path = StoragePath.parse("/path123");

        assertSame(
            path,
            path.removePrefix(
                StoragePath.ROOT
            )
        );
    }

    @Test
    public void testRemovePrefixWithSelf() {
        final StoragePath path = StoragePath.parse("/path123");

        assertSame(
            StoragePath.ROOT,
            path.removePrefix(path)
        );
    }

    @Test
    public void testRemovePrefixDifferentFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> StoragePath.parse("/path123")
                .removePrefix(
                    StoragePath.parse("/diff")
                )
        );

        this.checkEquals(
            "Path missing prefix \"/diff\"",
            thrown.getMessage()
        );
    }

    @Test
    public void testRemovePrefixDifferentFails2() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> StoragePath.parse("/path123")
                .removePrefix(
                    StoragePath.parse("/path")
                )
        );

        this.checkEquals(
            "Path missing prefix \"/path\"",
            thrown.getMessage()
        );
    }

    @Test
    public void testRemovePrefix() {
        this.removePrefixAndCheck(
            "/path123/path456",
            "/path123",
            "/path456"
        );
    }

    @Test
    public void testRemovePrefix2() {
        this.removePrefixAndCheck(
            "/path123/path456/path789",
            "/path123",
            "/path456/path789"
        );
    }

    @Test
    public void testRemovePrefix3() {
        this.removePrefixAndCheck(
            "/path123/path456/path789",
            "/path123/path456",
            "/path789"
        );
    }

    private void removePrefixAndCheck(final String path,
                                      final String prefix,
                                      final String expected) {
        this.removePrefixAndCheck(
            parsePath(path),
            parsePath(prefix),
            parsePath(expected)
        );
    }

    private void removePrefixAndCheck(final StoragePath path,
                                      final StoragePath prefix,
                                      final StoragePath expected) {
        this.checkEquals(
            expected,
            path.removePrefix(prefix),
            () -> path + " removePrefix " + prefix
        );
    }

    // equals/Compare...................................................................................................

    @Test
    public void testEqualsDifferentPath() {
        this.checkNotEquals(
            StoragePath.parse("/different")
        );
    }

    @Test
    public void testCompareLess() {
        this.compareToAndCheckLess(
            StoragePath.parse("/zebra")
        );
    }

    @Test
    public void testCompareMore() {
        this.compareToAndCheckMore(
            StoragePath.parse("/before")
        );
    }

    @Override
    public StoragePath root() {
        return StoragePath.ROOT;
    }

    @Override
    public StoragePath createPath() {
        return StoragePath.parse("/path");
    }

    @Override
    public StoragePath parsePath(final String path) {
        return StoragePath.parse(path);
    }

    @Override
    public StorageName createName(final int n) {
        return StorageName.with("string-name-" + n);
    }

    @Override
    public PathSeparator separator() {
        return StoragePath.SEPARATOR;
    }

    // ComparableTesting................................................................................................

    @Override
    public StoragePath createComparable() {
        return StoragePath.parse("/path");
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            this.createComparable(),
            "/path\n"
        );
    }

    // json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            this.createJsonNodeMarshallingValue(),
            JsonNode.string("/path1/path2/file3")
        );
    }

    @Override
    public StoragePath unmarshall(final JsonNode json,
                                  final JsonNodeUnmarshallContext context) {
        return StoragePath.unmarshall(
            json,
            context
        );
    }

    @Override
    public StoragePath createJsonNodeMarshallingValue() {
        return StoragePath.parse("/path1/path2/file3");
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<StoragePath> type() {
        return StoragePath.class;
    }

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // ConstantTesting .................................................................................................

    @Override
    public Set<StoragePath> intentionalDuplicateConstants() {
        return Sets.empty();
    }
}
