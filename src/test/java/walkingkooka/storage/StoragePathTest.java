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
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.PathSeparator;
import walkingkooka.naming.PathTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;

import java.util.Set;

final public class StoragePathTest implements PathTesting<StoragePath, StorageName>,
    ClassTesting2<StoragePath>,
    ParseStringTesting<StoragePath> {

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
    public void testParseEmptyComponentFails() {
        this.parseStringFails(
            "/before//after",
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
            StoragePath.ROOT_NAME
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

    // path.............................................................................................................

    @Test
    public void testRoot() {
        final StoragePath path = StoragePath.ROOT;
        this.rootCheck(path);
        this.valueCheck(path, "/");
        this.nameSameCheck(path, StoragePath.ROOT_NAME);
        this.parentAbsentCheck(path);
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
