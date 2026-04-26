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
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.ThrowableTesting2;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class InvalidStoragePathExceptionTest implements ThrowableTesting2<InvalidStoragePathException> {

    @Override
    public void testAllConstructorsVisibility() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testIfClassIsFinalIfAllConstructorsArePrivate() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testNew() {
        final String message = "Message 123";
        final StoragePath path = StoragePath.parse("/path1/file2.txt");

        final InvalidStoragePathException thrown = new InvalidStoragePathException(message, path);

        this.getMessageAndCheck(
            thrown,
            message + " " + '"' + path + '"'
        );
        this.checkEquals(
            path,
            thrown.path()
        );
    }

    @Test
    public void testSetPathWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> new InvalidStoragePathException("Message123", StoragePath.ROOT)
                .setPath(null)
        );
    }

    @Test
    public void testSetPath() {
        final String message = "Message 123";
        final StoragePath path = StoragePath.parse("/path1/file2.txt");

        final InvalidStoragePathException thrown = new InvalidStoragePathException(message, StoragePath.ROOT)
            .setPath(path);

        this.getMessageAndCheck(
            thrown,
            message + " " + '"' + path + '"'
        );
        this.checkEquals(
            path,
            thrown.path()
        );
    }

    // class............................................................................................................

    @Override
    public Class<InvalidStoragePathException> type() {
        return InvalidStoragePathException.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
