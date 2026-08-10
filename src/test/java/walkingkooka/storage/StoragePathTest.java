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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import walkingkooka.InvalidTextLengthException;
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.PathSeparator;
import walkingkooka.naming.PathTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

final public class StoragePathTest implements PathTesting<StoragePath, StorageName>,
    ClassTesting2<StoragePath>,
HasCurrentWorkingDirectoryTesting,
    HasHomeDirectoryTesting,
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
    public void testParseTooLongFails() {
        final char[] path = new char[256 + 1];
        Arrays.fill(path, 'a');
        path[0] = '/';
        final String stringPath = String.valueOf(path);

        this.parseStringFails(
            stringPath,
            new InvalidTextLengthException(
                "path",
                stringPath,
                1,
                255
            )
        );
    }

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
        this.valueAndCheck(
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
    public void testParseTrailSlash() {
        final String value = "/path1/";

        final StoragePath path = StoragePath.parse(value);
        this.valueAndCheck(
            path,
            value
        );
        this.nameCheck(
            path,
            StorageName.with("path1")
        );
        this.parentCheck(path);
    }

    @Test
    public void testParseSlashHome() {
        final String value = "/home";

        final StoragePath path = StoragePath.parse(value);
        this.valueAndCheck(
            path,
            value
        );
        this.nameCheck(
            path,
            StorageName.with("home")
        );
        this.parentCheck(
            path,
            StoragePath.ROOT
        );
    }

    @Test
    public void testParseFlat() {
        final String value = "/path to";

        final StoragePath path = StoragePath.parse(value);
        this.valueAndCheck(path, value);
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
        this.valueAndCheck(path, "/path1/path2"); // normalized
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
    public void testParseEmptyComponentNormalizedSlash() {
        final String value = "/path1//path2/";

        final StoragePath path = StoragePath.parse(value);
        this.valueAndCheck(path, "/path1/path2/"); // normalized
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
    public void testParseTwoComponents() {
        final String value = "/path/to";
        final StoragePath path = StoragePath.parse(value);
        this.valueAndCheck(path, value);
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
    public void testParseTwoComponentsSlash() {
        final String value = "/path/to/";
        final StoragePath path = StoragePath.parse(value);
        this.valueAndCheck(path, value);
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
    public void testParseThreeComponents() {
        final String value = "/path/to/xyz";

        final StoragePath path = StoragePath.parse(value);
        this.valueAndCheck(path, value);
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

        this.valueAndCheck(
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
    public void testParseThreeComponentsSlash() {
        final String value = "/path/to/xyz/";

        final StoragePath path = StoragePath.parse(value);
        this.valueAndCheck(path, value);
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

        this.valueAndCheck(
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

    @Override
    public void testPathWithFourComponents() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseIncludesDot() {
        final StoragePath path = StoragePath.parse("/path1/./path2/./path3");
        this.valueAndCheck(
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
        this.valueAndCheck(
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
        this.valueAndCheck(
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
    public void testParseIncludesDoubleDotSlash() {
        final StoragePath path = StoragePath.parse("/path1/./path2/../path3/");
        this.valueAndCheck(
            path,
            "/path1/path3/"
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
        this.valueAndCheck(
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
    public void testParseSpecialWithUserHomeMissingFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> StoragePath.parseSpecial(
                "~/path123",
                HasUserDirectorieses.empty()
            )
        );

        this.checkEquals(
            "Missing home directory",
            thrown.getMessage()
        );
    }

    @Test
    public void testParseSpecialWithUserHomePath() {
        final String text = "after4.txt";

        this.parseSpecialAndCheck(
            "~/" + text,
            HOME + "/" + text
        );
    }

    @Test
    public void testParseSpecialWithUserHomePath2() {
        final String text = "after4.txt";

        this.parseSpecialAndCheck(
            "~//" + text,
            HOME + "/" + text
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

    private final static String CWD = "/current1/working2/directory31/working2/directory3/";

    private final static String HOME = "/home/user123";

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

                @Override
                public Optional<StoragePath> homeDirectory() {
                    return Optional.of(
                        StoragePath.parse(HOME)
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
        this.valueAndCheck(path, "/");
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
        this.valueAndCheck(path, "/name1");
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
        this.valueAndCheck(path, "/parent1/name2");
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
        this.valueAndCheck(
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
        this.valueAndCheck(
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

    @Test
    public void testAppendPathToNonRootTwiceSlash() {
        final StoragePath parent = StoragePath.parse("/parent1");
        final StoragePath path2 = StoragePath.parse("/path2");
        final StoragePath path34 = StoragePath.parse("/path3/path4/");

        final StoragePath path = parent.append(path2)
            .append(path34);
        this.rootNotCheck(path);
        this.valueAndCheck(
            path,
            "/parent1/path2/path3/path4/"
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
            "/path3",
            "/path1/path2/",
            "/path1/path2/path3"
        );
    }

    @Test
    public void testPrependPath3() {
        this.prependPathAndCheck(
            "/path3/path4",
            "/path1/path2",
            "/path1/path2/path3/path4"
        );
    }

    @Test
    public void testPrependPath4() {
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
            InvalidStoragePathException.class,
            () -> StoragePath.parse("/path123")
                .removePrefix(
                    StoragePath.parse("/diff")
                )
        );

        this.checkEquals(
            "Prefix \"/diff\" missing from path \"/path123\"",
            thrown.getMessage()
        );
    }

    @Test
    public void testRemovePrefixDifferentFails2() {
        final IllegalArgumentException thrown = assertThrows(
            InvalidStoragePathException.class,
            () -> StoragePath.parse("/path123")
                .removePrefix(
                    StoragePath.parse("/path")
                )
        );

        this.checkEquals(
            "Prefix \"/path\" missing from path \"/path123\"",
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

    // isValue..........................................................................................................

    @Test
    public void testIsValueWithRoot() {
        this.isValueAndCheck(
            StoragePath.ROOT,
            false
        );
    }

    @Test
    public void testIsValueWithFolder() {
        this.isValueAndCheck(
            "/folder1/",
            false
        );
    }

    @Test
    public void testIsValueWithValue() {
        this.isValueAndCheck(
            "/value1",
            true
        );
    }

    @Test
    public void testIsValueWithValue2() {
        this.isValueAndCheck(
            "/folder1/value2",
            true
        );
    }

    private void isValueAndCheck(final String path,
                                 final boolean expected) {

        this.isValueAndCheck(
            this.parsePath(path),
            expected
        );
    }

    private void isValueAndCheck(final StoragePath path,
                                 final boolean expected) {
        this.checkEquals(
            expected,
            path.isValue(),
            path::toString
        );
    }

    // isParent.........................................................................................................

    @Test
    public void testIsParentWithRoot() {
        this.isParentAndCheck(
            StoragePath.ROOT,
            true
        );
    }

    @Test
    public void testIsParentWithFile() {
        this.isParentAndCheck(
            "/file1.txt",
            false
        );
    }

    @Test
    public void testIsParentWithParent() {
        this.isParentAndCheck(
            "/dir1/",
            true
        );
    }

    @Test
    public void testIsParentWithFile2() {
        this.isParentAndCheck(
            "/dir1/file1.txt",
            false
        );
    }

    private void isParentAndCheck(final String path,
                                  final boolean expected) {
        this.isParentAndCheck(
            StoragePath.parse(path),
            expected
        );
    }

    private void isParentAndCheck(final StoragePath path,
                                  final boolean expected) {
        this.checkEquals(
            expected,
            path.isParent(),
            path::toString
        );
    }

    // replaceHomeDirectory.............................................................................................

    @Test
    public void testReplaceHomeDirectoryWithRoot() {
        this.replaceHomeDirectoryAndCheck(
            StoragePath.ROOT
        );
    }

    @Test
    public void testReplaceHomeDirectoryWithNot() {
        this.replaceHomeDirectoryAndCheck(
            "/hello/world/123"
        );
    }

    @Test
    public void testReplaceHomeDirectoryWithUserDirectory() {
        this.replaceHomeDirectoryAndCheck(
            "/home",
            "/users/user123@example.com"
        );
    }

    @Test
    public void testReplaceHomeDirectoryWithUserDirectory2() {
        this.replaceHomeDirectoryAndCheck(
            "/home/",
            "/users/user123@example.com/"
        );
    }

    @Test
    public void testReplaceHomeDirectoryWithUserDirectory3() {
        this.replaceHomeDirectoryAndCheck(
            "/home/123",
            "/users/user123@example.com/123"
        );
    }

    private void replaceHomeDirectoryAndCheck(final String path) {
        this.replaceHomeDirectoryAndCheck(
            StoragePath.parse(path)
        );
    }

    private void replaceHomeDirectoryAndCheck(final StoragePath path) {
        this.replaceHomeDirectoryAndCheck(
            path,
            path
        );
    }

    private void replaceHomeDirectoryAndCheck(final String path,
                                              final String expected) {
        this.replaceHomeDirectoryAndCheck(
            StoragePath.parse(path),
            StoragePath.parse(expected)
        );
    }

    private void replaceHomeDirectoryAndCheck(final StoragePath path,
                                              final StoragePath expected) {
        this.checkEquals(
            expected,
            path.replaceHomeDirectory(
                new HasHomeDirectory() {
                    @Override
                    public Optional<StoragePath> homeDirectory() {
                        return OPTIONAL_HOME_DIRECTORY;
                    }
                }
            ),
            path::toString
        );
    }

    // replaceCurrentWorkingDirectory...................................................................................

    @Test
    public void testReplaceCurrentWorkingDirectoryWithRoot() {
        this.replaceCurrentWorkingDirectoryAndCheck(
            StoragePath.ROOT
        );
    }

    @Test
    public void testReplaceCurrentWorkingDirectoryWithNot() {
        this.replaceCurrentWorkingDirectoryAndCheck(
            "/hello/world/123"
        );
    }

    @Test
    public void testReplaceCurrentWorkingDirectoryWithCwd() {
        this.replaceCurrentWorkingDirectoryAndCheck(
            "/cwd",
            "/current1/working2/directory3/"
        );
    }

    @Test
    public void testReplaceCurrentWorkingDirectoryWithCwd2() {
        this.replaceCurrentWorkingDirectoryAndCheck(
            "/cwd/",
            "/current1/working2/directory3/"
        );
    }

    @Test
    public void testReplaceCurrentWorkingDirectoryWithCwd3() {
        this.replaceCurrentWorkingDirectoryAndCheck(
            "/cwd/123",
            "/current1/working2/directory3/123"
        );
    }

    private void replaceCurrentWorkingDirectoryAndCheck(final String path) {
        this.replaceCurrentWorkingDirectoryAndCheck(
            StoragePath.parse(path)
        );
    }

    private void replaceCurrentWorkingDirectoryAndCheck(final StoragePath path) {
        this.replaceCurrentWorkingDirectoryAndCheck(
            path,
            path
        );
    }

    private void replaceCurrentWorkingDirectoryAndCheck(final String path,
                                                        final String expected) {
        this.replaceCurrentWorkingDirectoryAndCheck(
            StoragePath.parse(path),
            StoragePath.parse(expected)
        );
    }

    private void replaceCurrentWorkingDirectoryAndCheck(final StoragePath path,
                                                        final StoragePath expected) {
        this.checkEquals(
            expected,
            path.replaceCurrentWorkingDirectory(
                new HasCurrentWorkingDirectory() {
                    @Override
                    public Optional<StoragePath> currentWorkingDirectory() {
                        return OPTIONAL_CURRENT_WORKING_DIRECTORY;
                    }
                }
            ),
            path::toString
        );
    }
    
    // restoreHomeDirectory.............................................................................................

    @Test
    public void testRestoreHomeDirectoryWithRoot() {
        this.restoreHomeDirectoryAndCheck(
            StoragePath.ROOT
        );
    }

    @Test
    public void testRestoreHomeDirectoryWithNot() {
        this.restoreHomeDirectoryAndCheck(
            "/hello/world/123"
        );
    }

    @Test
    public void testRestoreHomeDirectoryWithHomeDirectory() {
        this.restoreHomeDirectoryAndCheck(
            "/users/user123@example.com",
            "/home"
        );
    }

    @Test
    public void testRestoreHomeDirectoryWithHomeDirectory2() {
        this.restoreHomeDirectoryAndCheck(
            "/users/user123@example.com/",
            "/home/"
        );
    }

    @Test
    public void testRestoreHomeDirectoryWithHomeDirectory3() {
        this.restoreHomeDirectoryAndCheck(
            "/users/user123@example.com/123",
            "/home/123"
        );
    }

    private void restoreHomeDirectoryAndCheck(final String path) {
        this.restoreHomeDirectoryAndCheck(
            StoragePath.parse(path)
        );
    }

    private void restoreHomeDirectoryAndCheck(final StoragePath path) {
        this.restoreHomeDirectoryAndCheck(
            path,
            path
        );
    }

    private void restoreHomeDirectoryAndCheck(final String path,
                                              final String expected) {
        this.restoreHomeDirectoryAndCheck(
            StoragePath.parse(path),
            StoragePath.parse(expected)
        );
    }

    private void restoreHomeDirectoryAndCheck(final StoragePath path,
                                              final StoragePath expected) {
        this.checkEquals(
            expected,
            path.restoreHomeDirectory(
                new HasHomeDirectory() {
                    @Override
                    public Optional<StoragePath> homeDirectory() {
                        return OPTIONAL_HOME_DIRECTORY;
                    }
                }
            ),
            path::toString
        );
    }

    // restoreCurrentWorkingDirectory...................................................................................

    @Test
    public void testRestoreCurrentWorkingDirectoryWithRoot() {
        this.restoreCurrentWorkingDirectoryAndCheck(
            StoragePath.ROOT
        );
    }

    @Test
    public void testRestoreCurrentWorkingDirectoryWithNot() {
        this.restoreCurrentWorkingDirectoryAndCheck(
            "/hello/world/123"
        );
    }

    @Test
    public void testRestoreCurrentWorkingDirectoryWithCurrentWorkingDirectory() {
        this.restoreCurrentWorkingDirectoryAndCheck(
            "/current1/working2/directory3",
            "/cwd"
        );
    }

    @Test
    public void testRestoreCurrentWorkingDirectoryWithCurrentWorkingDirectory2() {
        this.restoreCurrentWorkingDirectoryAndCheck(
            "/current1/working2/directory3/",
            "/cwd/"
        );
    }

    @Test
    public void testRestoreCurrentWorkingDirectoryWithCurrentWorkingDirectory3() {
        this.restoreCurrentWorkingDirectoryAndCheck(
            "/current1/working2/directory3/123",
            "/cwd/123"
        );
    }

    private void restoreCurrentWorkingDirectoryAndCheck(final String path) {
        this.restoreCurrentWorkingDirectoryAndCheck(
            StoragePath.parse(path)
        );
    }

    private void restoreCurrentWorkingDirectoryAndCheck(final StoragePath path) {
        this.restoreCurrentWorkingDirectoryAndCheck(
            path,
            path
        );
    }

    private void restoreCurrentWorkingDirectoryAndCheck(final String path,
                                                        final String expected) {
        this.restoreCurrentWorkingDirectoryAndCheck(
            StoragePath.parse(path),
            StoragePath.parse(expected)
        );
    }

    private void restoreCurrentWorkingDirectoryAndCheck(final StoragePath path,
                                                        final StoragePath expected) {
        this.checkEquals(
            expected,
            path.restoreCurrentWorkingDirectory(
                new HasCurrentWorkingDirectory() {
                    @Override
                    public Optional<StoragePath> currentWorkingDirectory() {
                        return OPTIONAL_CURRENT_WORKING_DIRECTORY;
                    }
                }
            ),
            path::toString
        );
    }

    // replacePrefix....................................................................................................

    @Test
    public void testReplacePrefixWithNullPrefixFails() {
        assertThrows(
            NullPointerException.class,
            () -> StoragePath.ROOT.replacePrefix(
                null,
                StoragePath.ROOT
            )
        );
    }

    @Test
    public void testReplacePrefixWithNullReplaceWithFails() {
        assertThrows(
            NullPointerException.class,
            () -> StoragePath.ROOT.replacePrefix(
                StoragePath.ROOT,
                null
            )
        );
    }

    @Test
    public void testReplacePrefixWithPrefixMissing() {
        this.replacePrefixAndCheck(
            "/hello",
            "/prefix1",
            "/prefix2",
            "/hello"
        );
    }

    @Test
    public void testReplacePrefixWithRootRootRoot() {
        this.replacePrefixAndCheck(
            StoragePath.ROOT,
            StoragePath.ROOT,
            StoragePath.ROOT,
            StoragePath.ROOT
        );
    }

    @Test
    public void testReplacePrefixWithPrefixWithPrefixSlash() {
        this.replacePrefixAndCheck(
            "/prefix1/hello",
            "/prefix1/",
            "/prefix2/",
            "/prefix2/hello"
        );
    }

    @Test
    public void testReplacePrefixWithPrefixMissingSlash() {
        this.replacePrefixAndCheck(
            "/prefix1/hello",
            "/prefix1",
            "/prefix2",
            "/prefix2/hello"
        );
    }

    @Test
    public void testReplacePrefixWithPrefixMissingSlash2() {
        this.replacePrefixAndCheck(
            "/prefix1",
            "/prefix1",
            "/prefix2",
            "/prefix2"
        );
    }

    private void replacePrefixAndCheck(final String path,
                                       final String prefix,
                                       final String replaceWith,
                                       final String expected) {
        this.replacePrefixAndCheck(
            parsePath(path),
            parsePath(prefix),
            parsePath(replaceWith),
            parsePath(expected)
        );
    }

    private void replacePrefixAndCheck(final StoragePath path,
                                       final StoragePath prefix,
                                       final StoragePath replaceWith,
                                       final StoragePath expected) {
        this.checkEquals(
            expected,
            path.replacePrefix(
                prefix,
                replaceWith
            ),
            () -> path + " replacePrefix " + prefix + " " + replaceWith
        );
    }

    // StoragePathTesting...............................................................................................

    @Override
    public void parentCheck(final StoragePath path,
                            final String value) {
        if (value.endsWith(StoragePath.SEPARATOR_STRING)) {
            Assertions.fail("Path " + CharSequences.quote(value) + " must NOT end with " + CharSequences.quote(StoragePath.SEPARATOR_STRING));
        }

        PathTesting.super.parentCheck(
            path,
            value
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<StoragePath> type() {
        return StoragePath.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // ConstantTesting .................................................................................................

    @Override
    public Set<StoragePath> intentionalDuplicateConstants() {
        return Sets.empty();
    }
}
