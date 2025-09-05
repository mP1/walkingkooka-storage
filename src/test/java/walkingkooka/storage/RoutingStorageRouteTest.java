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
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

public final class RoutingStorageRouteTest implements ClassTesting2<RoutingStorageRoute> {

    // add...........................................................................................................

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
            this.createRoute(route),
            path,
            expected
        );
    }

    private void addAndCheck(final RoutingStorageRoute route,
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
            this.createRoute(route),
            path,
            expected
        );
    }

    private void removeAndCheck(final RoutingStorageRoute route,
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
            this.createRoute(route),
            path,
            expected
        );
    }

    private void isMatchAndCheck(final RoutingStorageRoute route,
                                 final StoragePath path,
                                 final boolean expected) {
        this.checkEquals(
            expected,
            route.isMatch(path),
            "isMatch " + path
        );
    }

    // helpers..........................................................................................................

    private RoutingStorageRoute createRoute(final StoragePath path) {
        return RoutingStorageRoute.with(
            path,
            Storages.fake()
        );
    }

    // class............................................................................................................

    @Override
    public Class<RoutingStorageRoute> type() {
        return RoutingStorageRoute.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
