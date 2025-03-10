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
import walkingkooka.net.header.MediaType;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageValueTest implements HashCodeEqualsDefinedTesting2<StorageValue>,
    ToStringTesting<StorageValue> {

    private final static StorageKey KEY = StorageKey.with("key123");

    private final static Optional<Object> VALUE = Optional.of("Hello");

    private final static MediaType CONTENT_TYPE = MediaType.TEXT_PLAIN;

    @Test
    public void testWithWithNullKeyFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageValue.with(
                null,
                VALUE
            )
        );
    }

    @Test
    public void testWithWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageValue.with(
                KEY,
                null
            )
        );
    }

    @Test
    public void testWith() {
        final StorageValue storageValue = StorageValue.with(
            KEY,
            VALUE
        );
        this.valueAndCheck(
            storageValue,
            VALUE
        );
    }

    private void valueAndCheck(final StorageValue value,
                               final Optional<Object> expected) {
        this.checkEquals(
            expected,
            value.value()
        );
    }

    // setKey...........................................................................................................

    @Test
    public void testSetKeyWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject().setKey(null)
        );
    }

    @Test
    public void testSetKeyWithSame() {
        final StorageValue storageValue = StorageValue.with(
            KEY,
            VALUE
        );

        assertSame(
            storageValue,
            storageValue.setKey(KEY)
        );
    }

    @Test
    public void testSetKeyWithDifferent() {
        final StorageValue storageValue = StorageValue.with(
            KEY,
            VALUE
        );

        final StorageKey differentKey = StorageKey.with("Different");

        final StorageValue different = storageValue.setKey(differentKey);
        assertNotSame(
            storageValue,
            different
        );

        this.keyAndCheck(
            different,
            differentKey
        );
    }

    private void keyAndCheck(final StorageValue value,
                             final StorageKey expected) {
        this.checkEquals(
            expected,
            value.key()
        );
    }

    // setValue.........................................................................................................

    @Test
    public void testSetValueWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject().setValue(null)
        );
    }

    @Test
    public void testSetValueWithSame() {
        final StorageValue storageValue = StorageValue.with(
            KEY,
            VALUE
        );

        assertSame(
            storageValue,
            storageValue.setValue(VALUE)
        );
    }

    @Test
    public void testSetValueWithDifferent() {
        final StorageValue storageValue = StorageValue.with(
            KEY,
            VALUE
        );

        final Optional<Object> differentValue = Optional.of("Different");

        final StorageValue different = storageValue.setValue(differentValue);
        assertNotSame(
            storageValue,
            different
        );

        this.valueAndCheck(
            different,
            differentValue
        );
    }

    // setContentType...................................................................................................

    @Test
    public void testSetContentTypeWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject().setContentType(null)
        );
    }

    @Test
    public void testSetContentTypeWithSame() {
        final StorageValue storageValue = StorageValue.with(
            KEY,
            VALUE
        );

        assertSame(
            storageValue,
            storageValue.setContentType(StorageValue.DEFAULT_CONTENT_TYPE)
        );
    }

    @Test
    public void testSetContentTypeWithDifferent() {
        final StorageValue storageValue = StorageValue.with(
            KEY,
            VALUE
        );

        final MediaType differentContentType = CONTENT_TYPE;

        final StorageValue different = storageValue.setContentType(differentContentType);
        assertNotSame(
            storageValue,
            different
        );

        this.contentTypeAndCheck(
            different,
            differentContentType
        );
    }

    private void contentTypeAndCheck(final StorageValue value,
                                     final MediaType expected) {
        this.checkEquals(
            expected,
            value.contentType()
        );
    }

    // Object...........................................................................................................

    @Test
    public void testEqualsDifferentKey() {
        this.checkNotEquals(
            StorageValue.with(
                StorageKey.with("different123"),
                VALUE
            )
        );
    }

    @Test
    public void testEqualsDifferentValue() {
        this.checkNotEquals(
            StorageValue.with(
                KEY,
                Optional.of(
                    "different " + VALUE
                )
            )
        );
    }

    @Test
    public void testEqualsDifferentContentType() {
        this.checkNotEquals(
            StorageValue.with(
                KEY,
                Optional.of(
                    VALUE
                )
            ).setContentType(CONTENT_TYPE)
        );
    }

    @Override
    public StorageValue createObject() {
        return StorageValue.with(
            KEY,
            VALUE
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createObject()
                .setContentType(CONTENT_TYPE),
            "key123=\"Hello\" text/plain"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageValue> type() {
        return StorageValue.class;
    }
}
