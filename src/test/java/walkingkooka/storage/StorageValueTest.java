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
import walkingkooka.HasValueTesting;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.net.header.HasContentTypeTesting;
import walkingkooka.net.header.MediaType;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageValueTest implements HasContentTypeTesting,
    HashCodeEqualsDefinedTesting2<StorageValue>,
    ToStringTesting<StorageValue>,
    TreePrintableTesting,
    JsonNodeMarshallingTesting<StorageValue>,
    HasValueTesting {

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
            () -> StorageValue.with(null)
        );
    }

    @Test
    public void testWith() {
        final StorageValue storageValue = StorageValue.with(PATH);
        this.valueAndCheck(
            storageValue,
            StorageValue.NO_VALUE
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
    public void testSetPathWithRoot() {
        final StorageValue storageValue = StorageValue.with(PATH);

        assertSame(
            StorageValue.ROOT,
            storageValue.setPath(StoragePath.ROOT)
        );
    }

    @Test
    public void testSetPathWithSame() {
        final StorageValue storageValue = StorageValue.with(PATH);

        assertSame(
            storageValue,
            storageValue.setPath(PATH)
        );
    }

    @Test
    public void testSetPathWithDifferent() {
        final StorageValue storageValue = StorageValue.with(PATH);

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
        final StorageValue storageValue = StorageValue.with(PATH)
            .setValue(VALUE);

        assertSame(
            storageValue,
            storageValue.setValue(VALUE)
        );
    }

    @Test
    public void testSetValueWithDifferent() {
        final StorageValue storageValue = StorageValue.with(PATH)
            .setValue(VALUE);

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
        final StorageValue storageValue = StorageValue.with(PATH)
            .setValue(VALUE);

        assertSame(
            storageValue,
            storageValue.setContentType(StorageValue.NO_CONTENT_TYPE)
        );
    }

    @Test
    public void testSetContentTypeWithDifferent() {
        final StorageValue storageValue = StorageValue.with(PATH)
            .setValue(VALUE);

        final Optional<MediaType> differentContentType = Optional.of(CONTENT_TYPE);

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

    // Object...........................................................................................................

    @Test
    public void testEqualsDifferentPath() {
        this.checkNotEquals(
            StorageValue.with(
                StoragePath.parse("/different123")
            ).setValue(VALUE)
        );
    }

    @Test
    public void testEqualsDifferentValue() {
        this.checkNotEquals(
            StorageValue.with(PATH)
                .setValue(
                    Optional.of(
                        "different " + VALUE
                    )
                )
        );
    }

    @Test
    public void testEqualsDifferentContentType() {
        this.checkNotEquals(
            StorageValue.with(PATH)
                .setValue(
                    Optional.of(
                        VALUE
                    )
                ).setContentType(
                    Optional.of(CONTENT_TYPE)
                )
        );
    }

    @Override
    public StorageValue createObject() {
        return StorageValue.with(PATH)
            .setValue(VALUE);
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createObject()
                .setContentType(
                    Optional.of(CONTENT_TYPE)
                ),
            "/path123=\"Hello\" text/plain"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintableWithoutValue() {
        this.treePrintAndCheck(
            StorageValue.with(PATH),
            "StorageValue\n" +
                "  /path123\n"
        );
    }

    @Test
    public void testTreePrintableWithValue() {
        this.treePrintAndCheck(
            this.createObject(),
            "StorageValue\n" +
                "  /path123\n" +
                "    \"Hello\"\n"
        );
    }

    @Test
    public void testTreePrintableContentType() {
        this.treePrintAndCheck(
            this.createObject()
                .setContentType(
                    Optional.of(MediaType.TEXT_PLAIN)
                ),
            "StorageValue\n" +
                "  /path123\n" +
                "    contentType: text/plain\n" +
                "      \"Hello\"\n"
        );
    }

    // json.............................................................................................................

    @Test
    public void testMarshallWithEmptyValue() {
        this.marshallAndCheck(
            StorageValue.with(PATH),
            "{\n" +
                "  \"path\": \"/path123\",\n" +
                "  \"value\": null\n" +
                "}"
        );
    }

    @Test
    public void testMarshallWithContentType() {
        this.marshallAndCheck(
            StorageValue.with(PATH)
                .setValue(VALUE)
                .setContentType(
                    Optional.of(
                        MediaType.APPLICATION_JSON
                    )
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
            StorageValue.with(PATH)
                .setValue(VALUE),
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
            StorageValue.with(PATH)
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
