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
import walkingkooka.environment.AuditInfo;
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

    private final static AuditInfo AUDIT_INFO = AuditInfo.with(
        CREATED_BY,
        CREATED_TIMESTAMP,
        MODIFIED_BY,
        MODIFIED_TIMESTAMP
    );

    private final static AuditInfo DIFFERENT_AUDIT_INFO = AuditInfo.with(
        CREATED_BY,
        CREATED_TIMESTAMP,
        EmailAddress.parse("different-modified-by@example.com"),
        MODIFIED_TIMESTAMP
    );

    // with.............................................................................................................

    @Test
    public void testWithNullPathFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageValueInfo.with(
                null,
                AUDIT_INFO
            )
        );
    }

    @Test
    public void testWithNullAuditInfoFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageValueInfo.with(
                PATH,
                null
            )
        );
    }

    @Test
    public void testWith() {
        final StorageValueInfo info = StorageValueInfo.with(
            PATH,
            AUDIT_INFO
        );

        this.pathAndCheck(info);
        this.auditInfoAndCheck(info);
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

        this.auditInfoAndCheck(info);
        this.auditInfoAndCheck(different);
    }

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

    // setAuditInfo.....................................................................................................

    @Test
    public void testSetAuditInfoWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .setAuditInfo(null)
        );
    }

    @Test
    public void testSetAuditInfoWithSame() {
        final StorageValueInfo info = this.createObject();

        assertSame(
            info,
            info.setAuditInfo(AUDIT_INFO)
        );
    }

    @Test
    public void testSetAuditInfoWithDifferent() {
        final StorageValueInfo info = this.createObject();

        final StorageValueInfo different = info.setAuditInfo(DIFFERENT_AUDIT_INFO);

        assertNotSame(
            info,
            different
        );

        this.pathAndCheck(info);
        this.pathAndCheck(different);

        this.auditInfoAndCheck(info);
        this.auditInfoAndCheck(different, DIFFERENT_AUDIT_INFO);
    }

    private void auditInfoAndCheck(final StorageValueInfo info) {
        this.auditInfoAndCheck(
            info,
            AUDIT_INFO
        );
    }

    private void auditInfoAndCheck(final StorageValueInfo info,
                                   final AuditInfo expected) {
        this.checkEquals(
            expected,
            info.auditInfo()
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentPath() {
        this.checkNotEquals(
            StorageValueInfo.with(
                StoragePath.parse("/different"),
                AUDIT_INFO
            )
        );
    }

    @Test
    public void testEqualsDifferentCreatedBy() {
        this.checkNotEquals(
            StorageValueInfo.with(
                PATH,
                DIFFERENT_AUDIT_INFO
            )
        );
    }

    @Override
    public StorageValueInfo createObject() {
        return StorageValueInfo.with(
            PATH,
            AUDIT_INFO
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
