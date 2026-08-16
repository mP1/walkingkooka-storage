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

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.FileTimeSource;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.HasCharsetTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.convert.ShortCircuitingConverter;
import walkingkooka.currency.CurrencyLocaleContextTesting;
import walkingkooka.datetime.DateTimeContextTesting;
import walkingkooka.datetime.HasNowTesting;
import walkingkooka.environment.AuditInfo;
import walkingkooka.environment.HasUserTesting;
import walkingkooka.math.DecimalNumberContextTesting;
import walkingkooka.math.MathTesting;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.header.MediaTypeDetectors;
import walkingkooka.props.Properties;
import walkingkooka.props.PropertiesPath;
import walkingkooka.reflect.ThrowableTesting;
import walkingkooka.storage.convert.StorageConverterContext;
import walkingkooka.storage.convert.StorageConverterContexts;
import walkingkooka.storage.convert.StorageConverters;
import walkingkooka.text.BinaryTextContextTesting;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.HasExpressionNumberKindTesting;
import walkingkooka.tree.expression.convert.ExpressionNumberBinaryNumberConverterFunctions;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverters;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContextTesting;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageShared2NativeFileTest extends StorageShared2TestCase<StorageShared2NativeFile<FakeStorageContext>, FakeStorageContext>
    implements BinaryTextContextTesting,
    CurrencyLocaleContextTesting,
    DateTimeContextTesting,
    DecimalNumberContextTesting,
    HasCharsetTesting,
    HasExpressionNumberKindTesting,
    HasNowTesting,
    HasUserTesting,
    JsonNodeMarshallUnmarshallContextTesting,
    MathTesting,
    ThrowableTesting {

    private final static Expression EXPRESSION = Expression.add(
        Expression.value(111),
        Expression.value(222)
    );

    private final static String EXPRESSION_FILE_PATH = "ExpressionFile111.expression.txt";

    private final static String EXPRESSION_CONTENT = "111+222";

    private final static String JSON_FILE_PATH = "JsonFile111.json";

    private final static JsonNode JSON_CONTENT = JsonNode.parse("{ \"hello\": \"world\" }");

    private final static Properties PROPERTIES = Properties.EMPTY.set(
        PropertiesPath.parse("key1.key11"),
        "value111"
    );

    private final static String SUB_STORAGE = "ZSubStorage";

    private final static String PROPERTIES_FILE_PATH = "PropertiesFile111.properties";

    private final static String TEXT_FILE_PATH = "TextFile111.txt";

    private final static String TEXT_CONTENT = "HelloWorldText123";

    private final static FileTime FILE_TIME_NOW = FileTime.from(
        NOW.toInstant(
            StorageShared2NativeFile.ZONE_OFFSET
        )
    );

    // with.............................................................................................................

    @Test
    public void testWithNullPathFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageShared2NativeFile.with(
                null,
                this.createContext()
            )
        );
    }

    @Test
    public void testWithInvalidPathFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> StorageShared2NativeFile.with(
                Path.of("/temp/123456789012345678901234567890"),
                this.createContext()
            )
        );
        this.getMessageAndCheck(
            thrown,
            "Unable to register watchers for \"/temp/123456789012345678901234567890\""
        );
    }

    @Test
    public void testWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageShared2NativeFile.with(
                Path.of("/temp"),
                null
            )
        );
    }

    // load.............................................................................................................

    @Test
    public void testLoadUnknown() {
        this.loadAndCheck(
            this.createStorage(),
            StoragePath.parse("/unknown.txt"),
            this.createContext()
        );
    }

    @Test
    public void testLoadExpressionFile() {
        final StoragePath storagePath = StoragePath.parse("/" + EXPRESSION_FILE_PATH);

        this.loadAndCheck(
            this.createStorage(),
            storagePath,
            this.createContext(),
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(EXPRESSION)
                )
        );
    }

    @Test
    public void testLoadJsonFile() {
        final StoragePath storagePath = StoragePath.parse("/" + JSON_FILE_PATH);

        this.loadAndCheck(
            this.createStorage(),
            storagePath,
            this.createContext(),
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(JSON_CONTENT)
                )
        );
    }

    @Test
    public void testLoadPropertiesFile() {
        final StoragePath storagePath = StoragePath.parse("/" + PROPERTIES_FILE_PATH);

        this.loadAndCheck(
            this.createStorage(),
            storagePath,
            this.createContext(),
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(PROPERTIES)
                ).setContentType(
                    Optional.of(MediaType.TEXT_PROPERTIES)
                )
        );
    }

    @Test
    public void testLoadTextFile() {
        final StoragePath storagePath = StoragePath.parse("/" + TEXT_FILE_PATH);

        this.loadAndCheck(
            this.createStorage(),
            storagePath,
            this.createContext(),
            StorageValue.with(storagePath)
                .setValue(
                    Optional.of(TEXT_CONTENT)
                ).setContentType(
                    Optional.of(MediaType.TEXT_PLAIN)
                )
        );
    }

    // save.............................................................................................................

    @Test
    public void testSaveExpressionFile() {
        final StorageShared2NativeFile<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        final StoragePath storagePath = StoragePath.parse("/different.expression.txt");

        final StorageValue storageValue = StorageValue.with(storagePath)
            .setValue(
                Optional.of(EXPRESSION)
            );

        this.saveAndCheck(
            storage,
            storageValue,
            context
        );

        this.loadAndCheck(
            storage,
            storagePath,
            context,
            storageValue
        );
    }

    @Test
    public void testSaveJsonFile() {
        final StorageShared2NativeFile<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        final StoragePath storagePath = StoragePath.parse("/different.json");

        final JsonNode jsonNode = JsonNode.parse("[\"different-string-222\"]");

        final StorageValue storageValue = StorageValue.with(storagePath)
            .setValue(
                Optional.of(jsonNode)
            );

        this.saveAndCheck(
            storage,
            storageValue,
            context
        );

        this.loadAndCheck(
            storage,
            storagePath,
            context,
            storageValue
        );
    }

    @Test
    public void testSavePropertiesFile() {
        final StorageShared2NativeFile<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        final StoragePath storagePath = StoragePath.parse("/different.properties");

        final Properties properties = Properties.EMPTY.set(
            PropertiesPath.parse("different.key222"),
            "value222"
        );

        final StorageValue storageValue = StorageValue.with(storagePath)
            .setValue(
                Optional.of(properties)
            );

        this.saveAndCheck(
            storage,
            storageValue,
            context
        );

        this.loadAndCheck(
            storage,
            storagePath,
            context,
            storageValue.setContentType(
                Optional.of(MediaType.TEXT_PROPERTIES)
            )
        );
    }

    @Test
    public void testSaveTextFile() {
        final StorageShared2NativeFile<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        final StoragePath storagePath = StoragePath.parse("/different.txt");

        final StorageValue storageValue = StorageValue.with(storagePath)
            .setValue(
                Optional.of("different text 222")
            );

        this.saveAndCheck(
            storage,
            storageValue,
            context
        );

        this.loadAndCheck(
            storage,
            storagePath,
            context,
            storageValue.setContentType(
                Optional.of(MediaType.TEXT_PLAIN)
            )
        );
    }

    // delete...........................................................................................................

    @Test
    public void testDeleteUnknown() {
        final InvalidStoragePathException thrown = assertThrows(
            InvalidStoragePathException.class,
            () -> this.createStorage()
                .delete(
                    StoragePath.parse("/unknown.txt"),
                    this.createContext()
                )
        );

        this.getMessageAndCheck(
            thrown,
            "Unable to delete \"/unknown.txt\""
        );
    }

    @Test
    public void testDelete() {
        final StorageShared2NativeFile<FakeStorageContext> storage = this.createStorage();
        final StoragePath storagePath = StoragePath.parse("/" + TEXT_FILE_PATH);
        final FakeStorageContext context = this.createContext();

        this.deleteAndCheck(
            storage,
            storagePath,
            context
        );

        this.loadAndCheck(
            storage,
            storagePath,
            context
        );
    }

    // list.............................................................................................................

    @Test
    public void testListUnknownParent() {
        final InvalidStoragePathException thrown = assertThrows(
            InvalidStoragePathException.class,
            () -> this.createStorage()
                .list0(
                    StoragePath.parse("/unknown-parent/"),
                    0,
                    1,
                    this.createContext()
                )
        );

        this.getMessageAndCheck(
            thrown,
            "Invalid path \"/unknown-parent/\""
        );
    }

    @Test
    public void testListParent() {
        this.listAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            0, // offset
            10, // count
            this.createContext(),
            this.storageValueInfo(
                StoragePath.parse("/" + EXPRESSION_FILE_PATH)
            ),
            this.storageValueInfo(
                StoragePath.parse("/" + JSON_FILE_PATH)
            ),
            this.storageValueInfo(
                StoragePath.parse("/" + PROPERTIES_FILE_PATH)
            ),
            this.storageValueInfo(
                StoragePath.parse("/" + TEXT_FILE_PATH)
            ),
            this.storageValueInfo(
                StoragePath.parse("/" + SUB_STORAGE)
            )
        );
    }

    @Test
    public void testListParentWithOffset() {
        this.listAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            1, // offset
            10, // count
            this.createContext(),
            this.storageValueInfo(
                StoragePath.parse("/" + JSON_FILE_PATH)
            ),
            this.storageValueInfo(
                StoragePath.parse("/" + PROPERTIES_FILE_PATH)
            ),
            this.storageValueInfo(
                StoragePath.parse("/" + TEXT_FILE_PATH)
            ),
            this.storageValueInfo(
                StoragePath.parse("/" + SUB_STORAGE)
            )
        );
    }

    @Test
    public void testListParentWithCount() {
        this.listAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            0, // offset
            2, // count
            this.createContext(),
            this.storageValueInfo(
                StoragePath.parse("/" + EXPRESSION_FILE_PATH)
            ),
            this.storageValueInfo(
                StoragePath.parse("/" + JSON_FILE_PATH)
            )
        );
    }

    @Test
    public void testListParentOffsetAndCount() {
        this.listAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            1, // offset
            1, // count
            this.createContext(),
            this.storageValueInfo(
                StoragePath.parse("/" + JSON_FILE_PATH)
            )
        );
    }

    @Test
    public void testListStorageValue() {
        final StoragePath storagePath = StoragePath.parse("/" + EXPRESSION_FILE_PATH);

        this.listAndCheck(
            this.createStorage(),
            storagePath,
            0, // offset
            10, // count
            this.createContext(),
            this.storageValueInfo(
                storagePath
            )
        );
    }

    @Test
    public void testListStorageValue2() {
        final StoragePath storagePath = StoragePath.parse("/" + JSON_FILE_PATH);

        this.listAndCheck(
            this.createStorage(),
            storagePath,
            0, // offset
            10, // count
            this.createContext(),
            this.storageValueInfo(
                storagePath
            )
        );
    }

    // setAuditInfo.....................................................................................................

    @Test
    public void testSetAuditInfo() {
        final StoragePath storagePath = StoragePath.parse("/" + EXPRESSION_FILE_PATH);

        final StorageShared2NativeFile<FakeStorageContext> storage = this.createStorage();
        final FakeStorageContext context = this.createContext();

        // cant use DIFFERENT_AUDIT_INFO because it contains a different user and StorageShared2NativeFile always uses Context.user()
        final StorageValueInfo value = this.storageValueInfo(storagePath)
            .setAuditInfo(
                AuditInfo.with(
                    AUDIT_INFO.createdBy(),
                    DIFFERENT_AUDIT_INFO.createdTimestamp(),
                    AUDIT_INFO.modifiedBy(),
                    DIFFERENT_AUDIT_INFO.modifiedTimestamp()
                )
            );

        storage.setAuditInfo(
            value,
            context
        );

        this.listAndCheck(
            storage,
            storagePath,
            0, // offset
            2, // count
            context,
            value
        );
    }

    private StorageValueInfo storageValueInfo(final StoragePath storagePath) {
        return StorageValueInfo.with(
            storagePath,
            AUDIT_INFO
        );
    }

    // addWatcherXXX....................................................................................................

    private final static int TIMEOUT = 15 * 1000;

    @Test
    public void testAddWatcherAndSave() {
        final FakeStorageContext context = this.createContext();

        final long end = TIMEOUT + System.currentTimeMillis();

        final StorageShared2NativeFile<FakeStorageContext> storage = this.createStorage();

        final StorageValue storageValue = StorageValue.with(
            StoragePath.parse("/different.txt")
        ).setValue(
            Optional.of("different " + TEXT_CONTENT)
        ).setContentType(
            Optional.of(MediaType.TEXT_PLAIN)
        );

        storage.addWatcher(
            new StorageWatcher() {
                @Override
                public void onValueChange(final Optional<StorageValue> oldValue,
                                          final Optional<StorageValue> newValue) {
                    System.out.println("onValueChange " + oldValue + " " + newValue);
                    checkEquals(
                        StorageValue.NO_VALUE,
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(storageValue),
                        newValue,
                        "newValue"
                    );
                    StorageShared2NativeFileTest.this.fired = true;
                }
            },
            context
        );

        this.fired = false;

        storage.save(
            storageValue,
            context
        );

        while (false == this.fired && System.currentTimeMillis() < end) {
            System.out.print('.');
            try {
                Thread.sleep(100);
            } catch (final InterruptedException e) {
                // ignore
            }
        }

        this.checkEquals(
            true,
            this.fired,
            "fired"
        );
    }

    private boolean fired;

    // Storage..........................................................................................................

    @Override
    public StorageShared2NativeFile<FakeStorageContext> createStorage() {
        try {
            final FileSystem fileSystem = Jimfs.newFileSystem(
                Configuration.unix()
                    .toBuilder()
                    .setFileTimeSource(
                        new FileTimeSource() {
                            @Override
                            public FileTime now() {
                                return FILE_TIME_NOW;
                            }
                        }
                    ).build()
            );

            final String rootPathString = "/111/222/";

            final Path root = fileSystem.getPath(rootPathString);
            Files.createDirectories(root);

            Files.write(
                fileSystem.getPath(
                    rootPathString,
                    EXPRESSION_FILE_PATH
                ),
                EXPRESSION_CONTENT.getBytes(CHARSET)
            );

            Files.write(
                fileSystem.getPath(
                    rootPathString,
                    JSON_FILE_PATH
                ),
                JSON_CONTENT.toString()
                    .getBytes(CHARSET)
            );

            Files.write(
                fileSystem.getPath(
                    rootPathString,
                    PROPERTIES_FILE_PATH
                ),
                PROPERTIES.text()
                    .getBytes(CHARSET)
            );

            Files.write(
                fileSystem.getPath(
                    rootPathString,
                    TEXT_FILE_PATH
                ),
                TEXT_CONTENT.getBytes(CHARSET)
            );

            Files.createDirectories(
                fileSystem.getPath(
                    rootPathString + SUB_STORAGE
                )
            );

            return StorageShared2NativeFile.with(
                root,
                this.createContext()
            );
        } catch (final IOException cause) {
            throw new Error(cause.getMessage(), cause);
        }
    }

    @Override
    public FakeStorageContext createContext() {
        return new FakeStorageContext() {
            @Override
            public Charset charset() {
                return StorageShared2NativeFileTest.CHARSET;
            }

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> type) {
                return this.converter.canConvert(
                    value,
                    type,
                    this.storageConverterContext
                );
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return this.converter.convert(
                    value,
                    target,
                    this.storageConverterContext
                );
            }

            private final Converter<StorageConverterContext> converter = Converters.collection(
                Lists.of(
                    Converters.simple(),
                    Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString(),
                    Converters.toText(),
                    Converters.binaryToString(),
                    Converters.textToBinary(),
                    new ShortCircuitingConverter<>() {

                        @Override
                        public boolean canConvert(final Object value,
                                                  final Class<?> type,
                                                  final StorageConverterContext context) {
                            return (
                                "".equals(value) ||
                                    EXPRESSION.text().equals(value)
                            ) &&
                                Expression.class == type;
                        }

                        @Override
                        public <T> Either<T, String> doConvert(final Object value,
                                                               final Class<T> type,
                                                               final StorageConverterContext context) {
                            return this.successfulConversion(
                                EXPRESSION,
                                type
                            );
                        }
                    },
                    Converters.textToProperties(),
                    JsonNodeConverters.toJsonNode(),
                    StorageConverters.storageBinaryToStorageValueExpression(),
                    StorageConverters.storageBinaryToStorageValueTxt(),
                    StorageConverters.storageBinaryToStorageValueProperties(),
                    StorageConverters.storageBinaryToStorageValueJson(),
                    StorageConverters.storageValueToStorageBinaryExpression(),
                    StorageConverters.storageValueToStorageBinaryJson(),
                    StorageConverters.storageValueToStorageBinaryProperties(),
                    StorageConverters.storageValueToStorageBinaryTxt()
                )
            );

            private final StorageConverterContext storageConverterContext = StorageConverterContexts.basic(
                this.converter,
                new FakeHasUserDirectories(),
                MediaTypeDetectors.fake(),
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
            );

            @Override
            public Optional<EmailAddress> user() {
                return OPTIONAL_USER;
            }
        };
    }

    // TreePrintable....................................................................................................

    @Test
    public void testPrintTree() {
        this.treePrintAndCheck(
            this.createStorage(),
            "StorageShared2NativeFile\n" +
                "  \"/111/222\"\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageShared2NativeFile<FakeStorageContext>> type() {
        return Cast.to(StorageShared2NativeFile.class);
    }
}
