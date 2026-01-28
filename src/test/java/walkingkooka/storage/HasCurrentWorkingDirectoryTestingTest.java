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

import java.util.Optional;

public final class HasCurrentWorkingDirectoryTestingTest implements HasCurrentWorkingDirectoryTesting {

    private final static String CWD = "/current/working/directory/";

    @Test
    public void testCurrentWorkingDirectoryWhenEmpty() {
        this.currentWorkingDirectoryAndCheck(
            () -> Optional.empty()
        );
    }

    @Test
    public void testCurrentWorkingDirectoryWhenNotEmpty() {
        final StoragePath path = StoragePath.parse(CWD);

        this.currentWorkingDirectoryAndCheck(
            () -> Optional.of(path),
            path
        );
    }

    @Test
    public void testCurrentWorkingDirectoryFails() {
        final StoragePath path = StoragePath.parse(CWD);

        boolean failed = false;

        try {
            this.currentWorkingDirectoryAndCheck(
                () -> Optional.of(
                    StoragePath.parse(CWD + "different")
                ),
                path
            );
        } catch (final AssertionError e) {
            failed = true;
        }

        this.checkEquals(
            true,
            failed
        );
    }
}
