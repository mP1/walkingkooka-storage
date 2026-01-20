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
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageValueTest implements HashCodeEqualsDefinedTesting2<StorageValue>,
    ToStringTesting<StorageValue>,
    TreePrintableTesting,
    JsonNodeMarshallingTesting<StorageValue> {

    {
        StorageStartup.init();
    }

    private final static StoragePath PATH = StoragePath.parse("/path123");

    private final static Optional<Object> VALUE = Optional.of("Hello");

    private final static MediaType CONTENT_TYPE = MediaType.TEXT_PLAIN;

    @Test
    public void testWithWithNullPathFails() {
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
                PATH,
                null
            )
        );
    }

    @Test
    public void testWith() {
        final StorageValue storageValue = StorageValue.with(
            PATH,
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

    @Test
    public void testWithRootAndNotValue() {
        assertSame(
            StorageValue.ROOT,
            StorageValue.with(
                StoragePath.ROOT,
                StorageValue.NO_VALUE
            )
        );
    }

    // setPath...........................................................................................................

    @Test
    public void testSetPathWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject().setPath(null)
        );
    }

    @Test
    public void testSetPathWithSame() {
        final StorageValue storageValue = StorageValue.with(
            PATH,
            VALUE
        );

        assertSame(
            storageValue,
            storageValue.setPath(PATH)
        );
    }

    @Test
    public void testSetPathWithDifferent() {
        final StorageValue storageValue = StorageValue.with(
            PATH,
            VALUE
        );

        final StoragePath differentPath = StoragePath.parse("/Different");

        final StorageValue different = storageValue.setPath(differentPath);
        assertNotSame(
            storageValue,
            different
        );

        this.pathAndCheck(
            different,
            differentPath
        );
    }

    private void pathAndCheck(final StorageValue value,
                              final StoragePath expected) {
        this.checkEquals(
            expected,
            value.path()
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
            PATH,
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
            PATH,
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
            PATH,
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
            PATH,
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
    public void testEqualsDifferentPath() {
        this.checkNotEquals(
            StorageValue.with(
                StoragePath.parse("/different123"),
                VALUE
            )
        );
    }

    @Test
    public void testEqualsDifferentValue() {
        this.checkNotEquals(
            StorageValue.with(
                PATH,
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
                PATH,
                Optional.of(
                    VALUE
                )
            ).setContentType(CONTENT_TYPE)
        );
    }

    @Override
    public StorageValue createObject() {
        return StorageValue.with(
            PATH,
            VALUE
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createObject()
                .setContentType(CONTENT_TYPE),
            "/path123=\"Hello\" text/plain"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintableWithoutValue() {
        this.treePrintAndCheck(
            StorageValue.with(
                PATH,
                Optional.empty()
            ),
            "/path123\n"
        );
    }

    @Test
    public void testTreePrintableWithValue() {
        this.treePrintAndCheck(
            this.createObject(),
            "/path123\n" +
                "  \"Hello\"\n"
        );
    }

    @Test
    public void testTreePrintableContentTypeNotBinary() {
        this.treePrintAndCheck(
            this.createObject()
                .setContentType(MediaType.TEXT_PLAIN),
            "/path123\n" +
                "  contentType: text/plain\n" +
                "    \"Hello\"\n"
        );
    }

    // json.............................................................................................................

    @Test
    public void testMarshallWithEmptyValue() {
        this.marshallAndCheck(
            StorageValue.with(
                PATH,
                Optional.empty()
            ),
            "{\n" +
                "  \"path\": \"/path123\",\n" +
                "  \"value\": null\n" +
                "}"
        );
    }

    @Test
    public void testMarshallWithContentType() {
        this.marshallAndCheck(
            StorageValue.with(
                PATH,
                VALUE
            ).setContentType(
                MediaType.APPLICATION_JSON
            ),
            "{\n" +
                "  \"path\": \"/path123\",\n" +
                "  \"value\": \"Hello\",\n" +
                "  \"contentType\": \"application/json\"\n" +
                "}"
        );
    }

    @Test
    public void testMarshallWithDefaultContentType() {
        this.marshallAndCheck(
            StorageValue.with(
                PATH,
                VALUE
            ),
            "{\n" +
                "  \"path\": \"/path123\",\n" +
                "  \"value\": \"Hello\"\n" +
                "}"
        );
    }

    @Test
    public void testUnmarshallWithEmptyValue() {
        this.unmarshallAndCheck(
            "{\n" +
                "  \"path\": \"/path123\",\n" +
                "  \"value\": null\n" +
                "}",
            StorageValue.with(
                PATH,
                Optional.empty()
            )
        );
    }

    @Override
    public StorageValue unmarshall(final JsonNode json,
                                   final JsonNodeUnmarshallContext context) {
        return StorageValue.unmarshall(
            json,
            context
        );
    }

    @Override
    public StorageValue createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    // class............................................................................................................

    @Override
    public Class<StorageValue> type() {
        return StorageValue.class;
    }
}
