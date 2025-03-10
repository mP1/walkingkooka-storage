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

    private final static StorageKey KEY = StorageKey.with("key123");

    private final static EmailAddress CREATED_BY = EmailAddress.parse("created-by@example.com");

    private final static LocalDateTime CREATE_TIMESTAMP = LocalDateTime.parse("1999-12-31T12:58:59");

    private final static EmailAddress MODIFIED_BY = EmailAddress.parse("modified-by@example.com");

    private final static LocalDateTime MODIFIED_TIMESTAMP = LocalDateTime.parse("2000-01-02T12:58:59");

    // with.............................................................................................................

    @Test
    public void testWithNullKeyFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageValueInfo.with(
                null,
                CREATED_BY,
                CREATE_TIMESTAMP,
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
                KEY,
                null,
                CREATE_TIMESTAMP,
                MODIFIED_BY,
                MODIFIED_TIMESTAMP
            )
        );
    }

    @Test
    public void testWithNullCreateTimestampFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageValueInfo.with(
                KEY,
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
                KEY,
                CREATED_BY,
                CREATE_TIMESTAMP,
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
                KEY,
                CREATED_BY,
                CREATE_TIMESTAMP,
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
                KEY,
                CREATED_BY,
                CREATE_TIMESTAMP,
                MODIFIED_BY,
                CREATE_TIMESTAMP.minusDays(1)
            )
        );

        this.checkEquals(
            "ModifiedTimestamp 1999-12-30T12:58:59 < createTimestamp 1999-12-31T12:58:59",
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testWith() {
        final StorageValueInfo info = StorageValueInfo.with(
            KEY,
            CREATED_BY,
            CREATE_TIMESTAMP,
            MODIFIED_BY,
            MODIFIED_TIMESTAMP
        );

        this.keyAndCheck(info);
        this.createdByAndCheck(info);
        this.createTimestampAndCheck(info);
        this.modifiedByAndCheck(info);
        this.modifiedTimestampAndCheck(info);
    }

    // setKey...........................................................................................................

    @Test
    public void testSetKeyWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .setKey(null)
        );
    }

    @Test
    public void testSetKeyWithSame() {
        final StorageValueInfo info = this.createObject();

        assertSame(
            info,
            info.setKey(KEY)
        );
    }

    @Test
    public void testSetKeyWithDifferent() {
        final StorageValueInfo info = this.createObject();

        final StorageKey differentKey = StorageKey.with("different.key");
        final StorageValueInfo different = info.setKey(differentKey);

        assertNotSame(
            info,
            different
        );

        this.keyAndCheck(info);
        this.keyAndCheck(different, differentKey);

        this.createdByAndCheck(info);
        this.createdByAndCheck(different);

        this.createTimestampAndCheck(info);
        this.createTimestampAndCheck(different);

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

        this.keyAndCheck(info);
        this.keyAndCheck(different);

        this.createdByAndCheck(info);
        this.createdByAndCheck(different, differentCreatedBy);

        this.createTimestampAndCheck(info);
        this.createTimestampAndCheck(different);

        this.modifiedByAndCheck(info);
        this.modifiedByAndCheck(different);

        this.modifiedTimestampAndCheck(info);
        this.modifiedTimestampAndCheck(different);
    }

    // setCreateTimestamp...............................................................................................

    @Test
    public void testSetCreateTimestampWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .setCreateTimestamp(null)
        );
    }

    @Test
    public void testSetCreateTimestampWithSame() {
        final StorageValueInfo info = this.createObject();

        assertSame(
            info,
            info.setCreateTimestamp(CREATE_TIMESTAMP)
        );
    }

    @Test
    public void testSetCreateTimestampWithDifferent() {
        final StorageValueInfo info = this.createObject();

        final LocalDateTime differentCreateTimestamp = LocalDateTime.MIN;
        final StorageValueInfo different = info.setCreateTimestamp(differentCreateTimestamp);

        assertNotSame(
            info,
            different
        );

        this.keyAndCheck(info);
        this.keyAndCheck(different);

        this.createdByAndCheck(info);
        this.createdByAndCheck(different);

        this.createTimestampAndCheck(info);
        this.createTimestampAndCheck(different, differentCreateTimestamp);

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

        this.keyAndCheck(info);
        this.keyAndCheck(different);

        this.createdByAndCheck(info);
        this.createdByAndCheck(different);

        this.createTimestampAndCheck(info);
        this.createTimestampAndCheck(different);

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

        this.keyAndCheck(info);
        this.keyAndCheck(different);

        this.createdByAndCheck(info);
        this.createdByAndCheck(different);

        this.createTimestampAndCheck(info);
        this.createTimestampAndCheck(different);

        this.modifiedByAndCheck(info);
        this.modifiedByAndCheck(different);

        this.modifiedTimestampAndCheck(info);
        this.modifiedTimestampAndCheck(different, differentModifiedTimestamp);
    }
    
    // helper...........................................................................................................

    private void keyAndCheck(final StorageValueInfo info) {
        this.keyAndCheck(
            info,
            KEY
        );
    }

    private void keyAndCheck(final StorageValueInfo info,
                             final StorageKey expected) {
        this.checkEquals(
            expected,
            info.key()
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

    private void createTimestampAndCheck(final StorageValueInfo info) {
        this.createTimestampAndCheck(
            info,
            CREATE_TIMESTAMP
        );
    }

    private void createTimestampAndCheck(final StorageValueInfo info,
                                         final LocalDateTime expected) {
        this.checkEquals(
            expected,
            info.createTimestamp()
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
    public void testEqualsDifferentKey() {
        this.checkNotEquals(
            StorageValueInfo.with(
                StorageKey.with("different"),
                CREATED_BY,
                CREATE_TIMESTAMP,
                MODIFIED_BY,
                MODIFIED_TIMESTAMP
            )
        );
    }

    @Test
    public void testEqualsDifferentCreatedBy() {
        this.checkNotEquals(
            StorageValueInfo.with(
                KEY,
                EmailAddress.parse("different@example.com"),
                CREATE_TIMESTAMP,
                MODIFIED_BY,
                MODIFIED_TIMESTAMP
            )
        );
    }

    @Test
    public void testEqualsDifferentCreateTimestamp() {
        this.checkNotEquals(
            StorageValueInfo.with(
                KEY,
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
                KEY,
                CREATED_BY,
                CREATE_TIMESTAMP,
                EmailAddress.parse("different@example.com"),
                MODIFIED_TIMESTAMP
            )
        );
    }

    @Test
    public void testEqualsDifferentLastTimestamp() {
        this.checkNotEquals(
            StorageValueInfo.with(
                KEY,
                CREATED_BY,
                CREATE_TIMESTAMP,
                MODIFIED_BY,
                LocalDateTime.MAX
            )
        );
    }

    @Override
    public StorageValueInfo createObject() {
        return StorageValueInfo.with(
            KEY,
            CREATED_BY,
            CREATE_TIMESTAMP,
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
