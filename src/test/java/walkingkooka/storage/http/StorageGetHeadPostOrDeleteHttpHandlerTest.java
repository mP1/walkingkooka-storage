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
package walkingkooka.storage.http;

import org.junit.jupiter.api.Test;
import walkingkooka.Binary;
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.currency.CurrencyLocaleContextTesting;
import walkingkooka.datetime.DateTimeContextTesting;
import walkingkooka.math.DecimalNumberContextTesting;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.header.ETag;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.header.MediaTypeBoundary;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.net.http.server.GetHeadPostOrDeleteHttpHandlerTesting;
import walkingkooka.net.http.server.HttpRequests;
import walkingkooka.net.http.server.HttpResponses;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.storage.FakeStorageContext;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.storage.Storages;
import walkingkooka.storage.convert.StorageConverterContext;
import walkingkooka.storage.convert.StorageConverterContexts;
import walkingkooka.storage.convert.StorageConverters;
import walkingkooka.storage.http.StorageGetHeadPostOrDeleteHttpHandlerTest.TestStorageHttpHandlerContext;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.convert.ExpressionNumberBinaryNumberConverterFunctions;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverters;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContextTesting;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public final class StorageGetHeadPostOrDeleteHttpHandlerTest implements GetHeadPostOrDeleteHttpHandlerTesting<StorageGetHeadPostOrDeleteHttpHandler<TestStorageHttpHandlerContext>, TestStorageHttpHandlerContext>,
    StorageHttpHandlerContextTesting,
    CurrencyLocaleContextTesting,
    DateTimeContextTesting,
    DecimalNumberContextTesting,
    JsonNodeMarshallUnmarshallContextTesting {

    @Test
    public void testHandleGetMissingAccept() {
        final TestStorageHttpHandlerContext context = this.createContext();

        this.handleAndCheck(
            HttpRequests.parse(
                HttpTransport.UNSECURED,
                "GET /api/storage/ HTTP/1.0\r\n" +
                    "\r\n"
            ),
            context,
            HttpResponses.parse(
                "HTTP/1.0 400 Missing Accept\r\n" +
                    "\r\n"
            )
        );
    }

    @Test
    public void testHandleGetMissingAcceptIncompatibleContentType() {
        final TestStorageHttpHandlerContext context = this.createContext();

        this.handleAndCheck(
            HttpRequests.parse(
                HttpTransport.UNSECURED,
                "GET /api/storage/ HTTP/1.0\r\n" +
                    "Accept: text/plain\r\n" +
                    "\r\n"
            ),
            context,
            HttpResponses.parse(
                "HTTP/1.0 400 Accept: Got application/json require text/plain\r\n" +
                    "\r\n"
            )
        );
    }

    @Test
    public void testHandleGetParentEmptyListing() {
        final TestStorageHttpHandlerContext context = this.createContext();

        this.handleAndCheck(
            HttpRequests.parse(
                HttpTransport.UNSECURED,
                "GET /api/storage/ HTTP/1.0\r\n" +
                    "Accept: application/json\r\n" +
                    "\r\n"
            ),
            context,
            HttpResponses.parse(
                "HTTP/1.0 200 OK\r\n" +
                    "Content-Length: 2\r\n" +
                    "Content-Type: application/json; charset=UTF-8\r\n" +
                    "\r\n" +
                    "[]"
            )
        );
    }

    @Test
    public void testHandleGetParentNotEmptyListing() {
        final TestStorageHttpHandlerContext context = this.createContext();

        context.saveStorage(
            StorageValue.with(
                StoragePath.parse("/1st.txt")
            ).setValue(
                Optional.of("111")
            )
        );

        context.saveStorage(
            StorageValue.with(
                StoragePath.parse("/2st.txt")
            ).setValue(
                Optional.of("222")
            )
        );

        this.handleAndCheck(
            HttpRequests.parse(
                HttpTransport.UNSECURED,
                "GET /api/storage/ HTTP/1.0\r\n" +
                    "Accept: application/json\r\n" +
                    "\r\n"
            ),
            context,
            HttpResponses.parse(
                "HTTP/1.0 200 OK\r\n" +
                    "Content-Length: 484\r\n" +
                    "Content-Type: application/json; charset=UTF-8\r\n" +
                    "\r\n" +
                    "[\n" +
                    "  {\n" +
                    "    \"path\": \"/1st.txt\",\n" +
                    "    \"auditInfo\": {\n" +
                    "      \"createdBy\": \"user123@example.com\",\n" +
                    "      \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                    "      \"modifiedBy\": \"user123@example.com\",\n" +
                    "      \"modifiedTimestamp\": \"1999-12-31T12:58:59\"\n" +
                    "    }\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"path\": \"/2st.txt\",\n" +
                    "    \"auditInfo\": {\n" +
                    "      \"createdBy\": \"user123@example.com\",\n" +
                    "      \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                    "      \"modifiedBy\": \"user123@example.com\",\n" +
                    "      \"modifiedTimestamp\": \"1999-12-31T12:58:59\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "]"
            )
        );
    }

    @Test
    public void testHandleGetUnknownStorageValue() {
        final TestStorageHttpHandlerContext context = this.createContext();

        this.handleAndCheck(
            HttpRequests.parse(
                HttpTransport.UNSECURED,
                "GET /api/storage/storage-value-not-found.txt HTTP/1.0\r\n" +
                    "Accept: */*\r\n" +
                    "\r\n"
            ),
            context,
            HttpResponses.parse(
                "HTTP/1.0 404 Not found\r\n" +
                    "\r\n"
            )
        );
    }

    @Test
    public void testHandleGetStorageValueJson() {
        final TestStorageHttpHandlerContext context = this.createContext();

        context.saveStorage(
            StorageValue.with(
                StoragePath.parse("/file123.json")
            ).setValue(
                Optional.of(
                    JsonNode.object()
                        .set(
                            JsonPropertyName.with("hello"),
                            "World 123"
                        )
                )
            )
        );

        this.handleAndCheck(
            HttpRequests.parse(
                HttpTransport.UNSECURED,
                "GET /api/storage/file123.json HTTP/1.0\r\n" +
                    "Accept: */*\r\n" +
                    "\r\n"
            ),
            context,
            HttpResponses.parse(
                "HTTP/1.0 200 OK\r\n" +
                    "Content-Length: 26\r\n" +
                    "Content-Type: application/json; charset=UTF-8\r\n" +
                    "\r\n" +
                    "{\n" +
                    "  \"hello\": \"World 123\"\n" +
                    "}"
            )
        );
    }

    @Test
    public void testHandleGetStorageValueTxt() {
        final TestStorageHttpHandlerContext context = this.createContext();

        context.saveStorage(
            StorageValue.with(
                StoragePath.parse("/file123.txt")
            ).setValue(
                Optional.of("File Content 123")
            ).setContentType(
                Optional.of(MediaType.TEXT_PLAIN)
            )
        );

        this.handleAndCheck(
            HttpRequests.parse(
                HttpTransport.UNSECURED,
                "GET /api/storage/file123.txt HTTP/1.0\r\n" +
                    "Accept: */*\r\n" +
                    "\r\n"
            ),
            context,
            HttpResponses.parse(
                "HTTP/1.0 200 OK\r\n" +
                    "Content-Length: 16\r\n" +
                    "Content-Type: text/plain; charset=UTF-8\r\n" +
                    "\r\n" +
                    "File Content 123"
            )
        );
    }

    @Test
    public void testHandlePostTextFile() {
        final TestStorageHttpHandlerContext context = this.createContext();

        this.handleAndCheck(
            HttpRequests.parse(
                HttpTransport.UNSECURED,
                "POST /api/storage/uploaded-file.txt HTTP/1.0\r\n" +
                    "Content-Length: 16\r\n" +
                    "Content-Type: text/plain; charset=UTF-8\r\n" +
                    "\r\n" +
                    "Hello"
            ),
            context,
            HttpResponses.parse(
                "HTTP/1.0 200 OK\r\n" +
                    "\r\n"
            )
        );

        this.loadStorageAndCheck(
            context,
            StoragePath.parse("/uploaded-text-file.txt")

        );
    }

    @Test
    public void testHandlePostMultipartTextFile() {
        final TestStorageHttpHandlerContext context = this.createContext();

        final String boundary = "delimiter12345";

        final String content = "HelloWorld123";

        final HttpEntity multipart = HttpEntity.EMPTY.setContentType(
            MediaType.MULTIPART_FORM_DATA.setBoundary(MediaTypeBoundary.parse(boundary))
        ).setBodyText(
            "--delimiter12345\r\n" +
                "Content-Disposition: form-data; name=\"field2\"; filename=\"abc.txt\"\r\n" +
                "\r\n" +
                content +
                "\r\n" +
                "--" + boundary + "--"
        );

        this.handleAndCheck(
            HttpRequests.post(
                HttpTransport.UNSECURED,
                Url.parseRelative("/api/storage/uploaded-multipart-file.txt"),
                HttpProtocolVersion.VERSION_1_0,
                multipart
            ),
            context,
            HttpResponses.parse(
                "HTTP/1.0 200 OK\r\n" +
                    "\r\n"
            )
        );

        final StoragePath path = StoragePath.parse("/uploaded-multipart-file.txt");

        this.loadStorageAndCheck(
            context,
            path,
            StorageValue.with(path)
                .setValue(
                    Optional.of(content)
                ).setContentType(
                    Optional.of(MediaType.TEXT_PLAIN)
                )
        );
    }

    @Test
    public void testHandleDeleteNotFound() {
        this.handleAndCheck(
            HttpRequests.parse(
                HttpTransport.UNSECURED,
                "DELETE /api/storage/fileNotFound.txt HTTP/1.0\r\n" +
                    "\r\n"
            ),
            HttpResponses.parse(
                "HTTP/1.0 404 StorageValue not found\r\n" +
                    "\r\n"
            )
        );
    }

    @Test
    public void testHandleDeleteExisting() {
        final TestStorageHttpHandlerContext context = this.createContext();

        final StoragePath path = StoragePath.parse("/deleteMe.txt");

        context.saveStorage(
            StorageValue.with(
                path
            ).setValue(
                Optional.of("111")
            )
        );

        this.handleAndCheck(
            HttpRequests.parse(
                HttpTransport.UNSECURED,
                "DELETE /api/storage/deleteMe.txt HTTP/1.0\r\n" +
                    "\r\n"
            ),
            context,
            HttpResponses.parse(
                "HTTP/1.0 200 OK\r\n" +
                    "\r\n"
            )
        );

        this.loadStorageAndCheck(
            context,
            path
        );
    }

    @Override
    public StorageGetHeadPostOrDeleteHttpHandler createHttpHandler() {
        return StorageGetHeadPostOrDeleteHttpHandler.with(2);
    }

    @Override
    public TestStorageHttpHandlerContext createContext() {
        return new TestStorageHttpHandlerContext();
    }

    static final class TestStorageHttpHandlerContext extends FakeStorageContext implements StorageHttpHandlerContext {

        @Override
        public Optional<StorageValue> loadStorage(final StoragePath path) {
            return this.storage.load(
                path,
                this
            );
        }

        @Override
        public StorageValue saveStorage(final StorageValue value) {
            return this.storage.save(
                value,
                this
            );
        }

        @Override
        public void deleteStorage(final StoragePath path) {
            this.storage.delete(
                path,
                this
            );
        }

        @Override
        public List<StorageValueInfo> listStorage(final StoragePath parent,
                                                  final int offset,
                                                  final int count) {
            return this.storage.list(
                parent,
                offset,
                count,
                this
            );
        }

        private final Storage<TestStorageHttpHandlerContext> storage = Storages.treeMapStore();

        @Override
        public Optional<ETag> computeETag(final Binary binary) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> Either<T, String> convert(final Object value,
                                             final Class<T> target) {
            return StorageConverterContexts.basic(
                this.converter,
                HAS_USER_DIRECTORIES,
                MEDIA_TYPE_DETECTOR,
                JsonNodeConverterContexts.basic(
                    ExpressionNumberConverterContexts.basic(
                        Converters.fake(),
                        ExpressionNumberBinaryNumberConverterFunctions.multiply(), // multiplier
                        ConverterContexts.basic(
                            false, // canNumbersHaveGroupSeparator
                            Converters.JAVA_EPOCH_OFFSET, // dateOffset
                            ',', // valueSeparator
                            Converters.fake(),
                            BinaryNumberConverterFunctions.fake(), // multiplier
                            BINARY_TEXT_CONTEXT,
                            CURRENCY_LOCALE_CONTEXT,
                            DATE_TIME_CONTEXT,
                            DECIMAL_NUMBER_CONTEXT
                        ),
                        EXPRESSION_NUMBER_KIND
                    ),
                    JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT
                )
            ).convert(
                value,
                target
            );
        }

        private final Converter<StorageConverterContext> converter = Converters.collection(
            Lists.of(
                Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString(),
                Converters.binaryToString(),
                Converters.textToBinary(),
                Converters.toBinary(),
                JsonNodeConverters.toJsonNode(),
                JsonNodeConverters.textToJsonNode(),//,
                StorageConverters.storageBinaryToStorageValueJson(),
                StorageConverters.storageBinaryToStorageValueTxt(),
                StorageConverters.storageValueToStorageBinaryJson(),
                StorageConverters.storageValueToStorageBinaryTxt(),
                StorageConverters.storageValueInfoListToText()
            )
        );

        @Override
        public Charset charset() {
            return StorageGetHeadPostOrDeleteHttpHandlerTest.CHARSET;
        }

        @Override
        public Indentation indentation() {
            return StorageGetHeadPostOrDeleteHttpHandlerTest.INDENTATION;
        }

        @Override
        public LineEnding lineEnding() {
            return StorageGetHeadPostOrDeleteHttpHandlerTest.LINE_ENDING;
        }

        @Override
        public LocalDateTime now() {
            return StorageGetHeadPostOrDeleteHttpHandlerTest.NOW;
        }

        @Override
        public Optional<EmailAddress> user() {
            return OPTIONAL_USER;
        }
    }

    // class............................................................................................................

    @Override
    public Class<StorageGetHeadPostOrDeleteHttpHandler<TestStorageHttpHandlerContext>> type() {
        return Cast.to(StorageGetHeadPostOrDeleteHttpHandler.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
