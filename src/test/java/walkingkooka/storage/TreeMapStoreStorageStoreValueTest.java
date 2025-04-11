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
import walkingkooka.ToStringTesting;
import walkingkooka.environment.AuditInfo;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class TreeMapStoreStorageStoreValueTest implements HashCodeEqualsDefinedTesting2<TreeMapStoreStorageStoreValue>,
    ClassTesting2<TreeMapStoreStorageStoreValue>,
    ToStringTesting<TreeMapStoreStorageStoreValue> {

    private final static StoragePath PATH = StoragePath.parse("/path123");

    private final static EmailAddress CREATED_BY = EmailAddress.parse("created-by@example.com");

    private final static LocalDateTime CREATED_TIMESTAMP = LocalDateTime.parse("1999-12-31T12:58:59");

    private final static EmailAddress MODIFIED_BY = EmailAddress.parse("modified-by@example.com");

    private final static LocalDateTime MODIFIED_TIMESTAMP = LocalDateTime.parse("2000-01-02T12:58:59");

    private final static AuditInfo AUDIT_INFO = AuditInfo.with(
        CREATED_BY,
        CREATED_TIMESTAMP,
        MODIFIED_BY,
        MODIFIED_TIMESTAMP
    );

    static {
        final String value = "Value999";

        VALUE = StorageValue.with(
            PATH,
            Optional.of(value)
        );

        STORAGE_VALUE_INFO = StorageValueInfo.with(
            PATH,
            AUDIT_INFO
        );
    }
    
    final static StorageValueInfo STORAGE_VALUE_INFO;
    final static StorageValue VALUE;

    // with.............................................................................................................

    @Test
    public void testWithNullInfoFails() {
        assertThrows(
            NullPointerException.class,
            () -> TreeMapStoreStorageStoreValue.with(
                null,
                VALUE
            )
        );
    }

    @Test
    public void testWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> TreeMapStoreStorageStoreValue.with(
                STORAGE_VALUE_INFO,
                null
            )
        );
    }

    @Test
    public void testWith() {
        final TreeMapStoreStorageStoreValue value = TreeMapStoreStorageStoreValue.with(
            STORAGE_VALUE_INFO,
                    VALUE
                );
        this.valueAndCheck(value);
        this.infoAndCheck(value);
    }

    // setInfo..........................................................................................................

    @Test
    public void testSetInfoWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .setInfo(null)
        );
    }

    @Test
    public void testSetInfoWithSame() {
        final TreeMapStoreStorageStoreValue value = this.createObject();
        assertSame(
            value,
            value.setInfo(STORAGE_VALUE_INFO)
        );
    }

    @Test
    public void testSetInfoWithDifferent() {
        final TreeMapStoreStorageStoreValue value = this.createObject();

        final StorageValueInfo differentInfo = STORAGE_VALUE_INFO.setAuditInfo(
            AUDIT_INFO.setModifiedBy(EmailAddress.parse("different@example.com"))
        );
        final TreeMapStoreStorageStoreValue different = value.setInfo(differentInfo);

        assertNotSame(
            value,
            different
        );

        this.valueAndCheck(different);
        this.valueAndCheck(value);

        this.infoAndCheck(different, differentInfo);
        this.infoAndCheck(value);
    }

    // setValue.........................................................................................................

    @Test
    public void testSetValueWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .setValue(null)
        );
    }

    @Test
    public void testSetValueWithSame() {
        final TreeMapStoreStorageStoreValue value = this.createObject();
        assertSame(
            value,
            value.setValue(VALUE)
        );
    }

    @Test
    public void testSetValueWithDifferent() {
        final TreeMapStoreStorageStoreValue value = this.createObject();

        final StorageValue differentValue = VALUE.setValue(
            Optional.of("different")
        );
        final TreeMapStoreStorageStoreValue different = value.setValue(differentValue);

        assertNotSame(
            value,
            different
        );

        this.infoAndCheck(different);
        this.infoAndCheck(value);

        this.valueAndCheck(different, differentValue);
        this.valueAndCheck(value);
    }
    
    // setPath...........................................................................................................

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
        final TreeMapStoreStorageStoreValue value = this.createObject();
        assertSame(
            value,
            value.setPath(PATH)
        );
    }

    @Test
    public void testSetPathWithDifferent() {
        final TreeMapStoreStorageStoreValue value = this.createObject();

        final StoragePath differentPath = StoragePath.parse("/different.path");
        final TreeMapStoreStorageStoreValue different = value.setPath(differentPath);

        assertNotSame(
            value,
            different
        );

        this.valueAndCheck(different, VALUE.setPath(differentPath));
        this.valueAndCheck(value);

        this.pathAndCheck(
            different,
            differentPath
        );
        this.pathAndCheck(value);
    }

    private void pathAndCheck(final TreeMapStoreStorageStoreValue value) {
        this.pathAndCheck(
            value,
            PATH
        );
    }

    private void pathAndCheck(final TreeMapStoreStorageStoreValue value,
                              final StoragePath expected) {
        this.checkEquals(
            expected,
            value.path()
        );
    }
    
    private void infoAndCheck(final TreeMapStoreStorageStoreValue value) {
        this.infoAndCheck(
            value,
            STORAGE_VALUE_INFO
        );
    }

    private void infoAndCheck(final TreeMapStoreStorageStoreValue value,
                              final StorageValueInfo expected) {
        this.checkEquals(
            expected,
            value.info
        );
    }

    private void valueAndCheck(final TreeMapStoreStorageStoreValue value) {
        this.valueAndCheck(
            value,
            VALUE
        );
    }

    private void valueAndCheck(final TreeMapStoreStorageStoreValue value,
                              final StorageValue expected) {
        this.checkEquals(
            expected,
            value.value
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentInfo() {
        this.checkNotEquals(
            TreeMapStoreStorageStoreValue.with(
                STORAGE_VALUE_INFO.setAuditInfo(
                    AUDIT_INFO.setModifiedTimestamp(
                        MODIFIED_TIMESTAMP.plusYears(1)
                    )
                ),
                VALUE
            )
        );
    }

    @Test
    public void testEqualsDifferentValue() {
        this.checkNotEquals(
            TreeMapStoreStorageStoreValue.with(
                STORAGE_VALUE_INFO,
                VALUE.setValue(
                    Optional.of("Different")
                )
            )
        );
    }

    @Override 
    public TreeMapStoreStorageStoreValue createObject() {
        return TreeMapStoreStorageStoreValue.with(
            STORAGE_VALUE_INFO,
            VALUE
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createObject(),
            "/path123 created-by@example.com 1999-12-31T12:58:59 modified-by@example.com 2000-01-02T12:58:59 /path123=\"Value999\" application/octet-stream"
        );
    }

    // class............................................................................................................

    @Override
    public Class<TreeMapStoreStorageStoreValue> type() {
        return TreeMapStoreStorageStoreValue.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
