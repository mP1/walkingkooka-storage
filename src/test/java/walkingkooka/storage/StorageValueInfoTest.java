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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.printer.TreePrintableTesting;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageValueInfoTest implements HashCodeEqualsDefinedTesting2<StorageValueInfo>,
    ClassTesting2<StorageValueInfo>,
    TreePrintableTesting {

    private final static StoragePath PATH = StoragePath.parse("/path123");

    private final static EmailAddress CREATED_BY = EmailAddress.parse("created-by@example.com");

    private final static LocalDateTime CREATED_TIMESTAMP = LocalDateTime.parse("1999-12-31T12:58:59");

    private final static EmailAddress MODIFIED_BY = EmailAddress.parse("modified-by@example.com");

    private final static LocalDateTime MODIFIED_TIMESTAMP = LocalDateTime.parse("2000-01-02T12:58:59");

    // with.............................................................................................................

    @Test
    public void testWithNullPathFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageValueInfo.with(
                null,
                CREATED_BY,
                CREATED_TIMESTAMP,
                MODIFIED_BY,
                MODIFIED_TIMESTAMP
            )
        );
    }

    @Test
    public void testWithNullCreatedByFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageValueInfo.with(
                PATH,
                null,
                CREATED_TIMESTAMP,
                MODIFIED_BY,
                MODIFIED_TIMESTAMP
            )
        );
    }

    @Test
    public void testWithNullCreatedTimestampFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageValueInfo.with(
                PATH,
                CREATED_BY,
                null,
                MODIFIED_BY,
                MODIFIED_TIMESTAMP
            )
        );
    }

    @Test
    public void testWithNullModifiedByFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageValueInfo.with(
                PATH,
                CREATED_BY,
                CREATED_TIMESTAMP,
                null,
                MODIFIED_TIMESTAMP
            )
        );
    }

    @Test
    public void testWithNullModifiedTimestampFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageValueInfo.with(
                PATH,
                CREATED_BY,
                CREATED_TIMESTAMP,
                MODIFIED_BY,
                null
            )
        );
    }

    @Test
    public void testWithModifiedTimestampBeforeCreatedByFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> StorageValueInfo.with(
                PATH,
                CREATED_BY,
                CREATED_TIMESTAMP,
                MODIFIED_BY,
                CREATED_TIMESTAMP.minusDays(1)
            )
        );

        this.checkEquals(
            "ModifiedTimestamp 1999-12-30T12:58:59 < createdTimestamp 1999-12-31T12:58:59",
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testWith() {
        final StorageValueInfo info = StorageValueInfo.with(
            PATH,
            CREATED_BY,
            CREATED_TIMESTAMP,
            MODIFIED_BY,
            MODIFIED_TIMESTAMP
        );

        this.pathAndCheck(info);
        this.createdByAndCheck(info);
        this.createdTimestampAndCheck(info);
        this.modifiedByAndCheck(info);
        this.modifiedTimestampAndCheck(info);
    }

    // setPath..........................................................................................................

    @Test
    public void testSetPathWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .setPath(null)
        );
    }

    @Test
    public void testSetPathWithSame() {
        final StorageValueInfo info = this.createObject();

        assertSame(
            info,
            info.setPath(PATH)
        );
    }

    @Test
    public void testSetPathWithDifferent() {
        final StorageValueInfo info = this.createObject();

        final StoragePath differentPath = StoragePath.parse("/different.path");
        final StorageValueInfo different = info.setPath(differentPath);

        assertNotSame(
            info,
            different
        );

        this.pathAndCheck(info);
        this.pathAndCheck(different, differentPath);

        this.createdByAndCheck(info);
        this.createdByAndCheck(different);

        this.createdTimestampAndCheck(info);
        this.createdTimestampAndCheck(different);

        this.modifiedByAndCheck(info);
        this.modifiedByAndCheck(different);

        this.modifiedTimestampAndCheck(info);
        this.modifiedTimestampAndCheck(different);
    }

    // setCreatedBy...........................................................................................................

    @Test
    public void testSetCreatedByWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .setCreatedBy(null)
        );
    }

    @Test
    public void testSetCreatedByWithSame() {
        final StorageValueInfo info = this.createObject();

        assertSame(
            info,
            info.setCreatedBy(CREATED_BY)
        );
    }

    @Test
    public void testSetCreatedByWithDifferent() {
        final StorageValueInfo info = this.createObject();

        final EmailAddress differentCreatedBy = EmailAddress.parse("different@example.com");
        final StorageValueInfo different = info.setCreatedBy(differentCreatedBy);

        assertNotSame(
            info,
            different
        );

        this.pathAndCheck(info);
        this.pathAndCheck(different);

        this.createdByAndCheck(info);
        this.createdByAndCheck(different, differentCreatedBy);

        this.createdTimestampAndCheck(info);
        this.createdTimestampAndCheck(different);

        this.modifiedByAndCheck(info);
        this.modifiedByAndCheck(different);

        this.modifiedTimestampAndCheck(info);
        this.modifiedTimestampAndCheck(different);
    }

    // setCreatedTimestamp...............................................................................................

    @Test
    public void testSetCreatedTimestampWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .setCreatedTimestamp(null)
        );
    }

    @Test
    public void testSetCreatedTimestampWithSame() {
        final StorageValueInfo info = this.createObject();

        assertSame(
            info,
            info.setCreatedTimestamp(CREATED_TIMESTAMP)
        );
    }

    @Test
    public void testSetCreatedTimestampWithDifferent() {
        final StorageValueInfo info = this.createObject();

        final LocalDateTime differentCreatedTimestamp = LocalDateTime.MIN;
        final StorageValueInfo different = info.setCreatedTimestamp(differentCreatedTimestamp);

        assertNotSame(
            info,
            different
        );

        this.pathAndCheck(info);
        this.pathAndCheck(different);

        this.createdByAndCheck(info);
        this.createdByAndCheck(different);

        this.createdTimestampAndCheck(info);
        this.createdTimestampAndCheck(different, differentCreatedTimestamp);

        this.modifiedByAndCheck(info);
        this.modifiedByAndCheck(different);

        this.modifiedTimestampAndCheck(info);
        this.modifiedTimestampAndCheck(different);
    }

    // setModifiedBy...............................................................................................

    @Test
    public void testSetModifiedByWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .setModifiedBy(null)
        );
    }

    @Test
    public void testSetModifiedByWithSame() {
        final StorageValueInfo info = this.createObject();

        assertSame(
            info,
            info.setModifiedBy(MODIFIED_BY)
        );
    }

    @Test
    public void testSetModifiedByWithDifferent() {
        final StorageValueInfo info = this.createObject();

        final EmailAddress differentModifiedBy = EmailAddress.parse("different@example.com");
        final StorageValueInfo different = info.setModifiedBy(differentModifiedBy);

        assertNotSame(
            info,
            different
        );

        this.pathAndCheck(info);
        this.pathAndCheck(different);

        this.createdByAndCheck(info);
        this.createdByAndCheck(different);

        this.createdTimestampAndCheck(info);
        this.createdTimestampAndCheck(different);

        this.modifiedByAndCheck(info);
        this.modifiedByAndCheck(different, differentModifiedBy);

        this.modifiedTimestampAndCheck(info);
        this.modifiedTimestampAndCheck(different);
    }

    // setModifiedTimestamp.............................................................................................

    @Test
    public void testSetModifiedTimestampWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .setModifiedTimestamp(null)
        );
    }

    @Test
    public void testSetModifiedTimestampWithSame() {
        final StorageValueInfo info = this.createObject();

        assertSame(
            info,
            info.setModifiedTimestamp(MODIFIED_TIMESTAMP)
        );
    }

    @Test
    public void testSetModifiedTimestampWithDifferent() {
        final StorageValueInfo info = this.createObject();

        final LocalDateTime differentModifiedTimestamp = LocalDateTime.MAX;
        final StorageValueInfo different = info.setModifiedTimestamp(differentModifiedTimestamp);

        assertNotSame(
            info,
            different
        );

        this.pathAndCheck(info);
        this.pathAndCheck(different);

        this.createdByAndCheck(info);
        this.createdByAndCheck(different);

        this.createdTimestampAndCheck(info);
        this.createdTimestampAndCheck(different);

        this.modifiedByAndCheck(info);
        this.modifiedByAndCheck(different);

        this.modifiedTimestampAndCheck(info);
        this.modifiedTimestampAndCheck(different, differentModifiedTimestamp);
    }
    
    // helper...........................................................................................................

    private void pathAndCheck(final StorageValueInfo info) {
        this.pathAndCheck(
            info,
            PATH
        );
    }

    private void pathAndCheck(final StorageValueInfo info,
                             final StoragePath expected) {
        this.checkEquals(
            expected,
            info.path()
        );
    }

    private void createdByAndCheck(final StorageValueInfo info) {
        this.createdByAndCheck(
            info,
            CREATED_BY
        );
    }

    private void createdByAndCheck(final StorageValueInfo info,
                                   final EmailAddress expected) {
        this.checkEquals(
            expected,
            info.createdBy()
        );
    }

    private void createdTimestampAndCheck(final StorageValueInfo info) {
        this.createdTimestampAndCheck(
            info,
            CREATED_TIMESTAMP
        );
    }

    private void createdTimestampAndCheck(final StorageValueInfo info,
                                         final LocalDateTime expected) {
        this.checkEquals(
            expected,
            info.createdTimestamp()
        );
    }

    private void modifiedByAndCheck(final StorageValueInfo info) {
        this.modifiedByAndCheck(
            info,
            MODIFIED_BY
        );
    }

    private void modifiedByAndCheck(final StorageValueInfo info,
                                    final EmailAddress expected) {
        this.checkEquals(
            expected,
            info.modifiedBy()
        );
    }

    private void modifiedTimestampAndCheck(final StorageValueInfo info) {
        this.modifiedTimestampAndCheck(
            info,
            MODIFIED_TIMESTAMP
        );
    }

    private void modifiedTimestampAndCheck(final StorageValueInfo info,
                                         final LocalDateTime expected) {
        this.checkEquals(
            expected,
            info.modifiedTimestamp()
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentPath() {
        this.checkNotEquals(
            StorageValueInfo.with(
                StoragePath.parse("/different"),
                CREATED_BY,
                CREATED_TIMESTAMP,
                MODIFIED_BY,
                MODIFIED_TIMESTAMP
            )
        );
    }

    @Test
    public void testEqualsDifferentCreatedBy() {
        this.checkNotEquals(
            StorageValueInfo.with(
                PATH,
                EmailAddress.parse("different@example.com"),
                CREATED_TIMESTAMP,
                MODIFIED_BY,
                MODIFIED_TIMESTAMP
            )
        );
    }

    @Test
    public void testEqualsDifferentCreatedTimestamp() {
        this.checkNotEquals(
            StorageValueInfo.with(
                PATH,
                CREATED_BY,
                LocalDateTime.MIN,
                MODIFIED_BY,
                MODIFIED_TIMESTAMP
            )
        );
    }

    @Test
    public void testEqualsDifferentModifiedBy() {
        this.checkNotEquals(
            StorageValueInfo.with(
                PATH,
                CREATED_BY,
                CREATED_TIMESTAMP,
                EmailAddress.parse("different@example.com"),
                MODIFIED_TIMESTAMP
            )
        );
    }

    @Test
    public void testEqualsDifferentLastTimestamp() {
        this.checkNotEquals(
            StorageValueInfo.with(
                PATH,
                CREATED_BY,
                CREATED_TIMESTAMP,
                MODIFIED_BY,
                LocalDateTime.MAX
            )
        );
    }

    @Override
    public StorageValueInfo createObject() {
        return StorageValueInfo.with(
            PATH,
            CREATED_BY,
            CREATED_TIMESTAMP,
            MODIFIED_BY,
            MODIFIED_TIMESTAMP
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageValueInfo> type() {
        return StorageValueInfo.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
