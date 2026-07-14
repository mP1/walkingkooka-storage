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
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.convert.ShortCircuitingConverter;
import walkingkooka.currency.CurrencyContexts;
import walkingkooka.currency.CurrencyLocaleContext;
import walkingkooka.currency.CurrencyLocaleContexts;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.environment.AuditInfo;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.header.MediaTypeDetectors;
import walkingkooka.props.Properties;
import walkingkooka.props.PropertiesPath;
import walkingkooka.reflect.ThrowableTesting;
import walkingkooka.storage.convert.StorageConverterContext;
import walkingkooka.storage.convert.StorageConverterContexts;
import walkingkooka.storage.convert.StorageConverters;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.text.TextPrinting;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberBinaryNumberConverterFunctions;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverters;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.io.IOException;
import java.math.MathContext;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.FileTime;
import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StorageSharedNativeFileTest extends StorageSharedTestCase<StorageSharedNativeFile<FakeStorageContext>, FakeStorageContext>
    implements ThrowableTesting {

    private final static Charset CHARSET = StandardCharsets.UTF_8;

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = DecimalNumberContexts.american(
        MathContext.DECIMAL32
    );

    private final static Expression EXPRESSION = Expression.add(
        Expression.value(111),
        Expression.value(222)
    );

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;

    private final static Locale LOCALE = Locale.forLanguageTag("en-AU");

    private final static CurrencyLocaleContext CURRENCY_LOCALE_CONTEXT = CurrencyLocaleContexts.basic(
        CurrencyContexts.fake(),
        LocaleContexts.jre(LOCALE)
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

    private final static LocalDateTime NOW = LocalDateTime.of(
        1999,
        12,
        31,
        12,
        58,
        59
    );

    private final static FileTime FILE_TIME_NOW = FileTime.from(
        NOW.toInstant(
            walkingkooka.storage.StorageSharedNativeFile.UTC
        )
    );

    private final static EmailAddress USER = EmailAddress.parse("user@example.com");

    private final static WatchServicePoller<FakeStorageContext> POLLER = new WatchServicePoller<>() {
        @Override
        public void beginPolling(final Consumer<WatchServicePoller<FakeStorageContext>> poller) {
            // NOP
        }

        @Override
        public Optional<WatchKey> pollOrTakeWatchKey(final WatchService watchService) {
            throw new UnsupportedOperationException();
        }

        @Override
        public FakeStorageContext context() {
            throw new UnsupportedOperationException();
        }
    };

    // with.............................................................................................................

    @Test
    public void testWithNullPathFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageSharedNativeFile.with(
                null,
                POLLER
            )
        );
    }

    @Test
    public void testWithNullConsumerFails() {
        assertThrows(
            NullPointerException.class,
            () -> StorageSharedNativeFile.with(
                null,
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
                )
        );
    }

    // save.............................................................................................................

    @Test
    public void testSaveExpressionFile() {
        final StorageSharedNativeFile<FakeStorageContext> storage = this.createStorage();
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
        final StorageSharedNativeFile<FakeStorageContext> storage = this.createStorage();
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
        final StorageSharedNativeFile<FakeStorageContext> storage = this.createStorage();
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
            storageValue
        );
    }

    @Test
    public void testSaveTextFile() {
        final StorageSharedNativeFile<FakeStorageContext> storage = this.createStorage();
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
            storageValue
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
        final StorageSharedNativeFile<FakeStorageContext> storage = this.createStorage();
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
                    StoragePath.parse("/unknown-parent"),
                    0,
                    1,
                    this.createContext()
                )
        );

        this.getMessageAndCheck(
            thrown,
            "Invalid path \"/unknown-parent\""
        );
    }

    @Test
    public void testList() {
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
    public void testListOffset() {
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
    public void testListCount() {
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
    public void testListOffsetAndCount() {
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

    private StorageValueInfo storageValueInfo(final StoragePath storagePath) {
        return StorageValueInfo.with(
            storagePath,
            AuditInfo.create(
                USER,
                NOW
            )
        );
    }

    // addWatcherXXX....................................................................................................

    private final static int TIMEOUT = 15 * 1000;

    @Test
    public void testAddWatcherAndSave() {
        final FakeStorageContext context = this.createContext();

        this.polling = true;

        final long end = TIMEOUT + System.currentTimeMillis();

        final WatchServicePoller<FakeStorageContext> poller = new WatchServicePoller<>() {
            @Override
            public void beginPolling(final Consumer<WatchServicePoller<FakeStorageContext>> poller) {
                new Thread(() -> {
                    while (false == StorageSharedNativeFileTest.this.fired && System.currentTimeMillis() < end) {
                        poller.accept(this);
                    }

                    StorageSharedNativeFileTest.this.polling = false;
                }).start();
            }

            @Override
            public Optional<WatchKey> pollOrTakeWatchKey(final WatchService watchService) {
                return Optional.ofNullable(
                    watchService.poll()
                );
            }

            @Override
            public FakeStorageContext context() {
                return context;
            }
        };

        final StorageSharedNativeFile<FakeStorageContext> storage = this.createStorage(poller);

        final StorageValue storageValue = StorageValue.with(
            StoragePath.parse("/different.txt")
        ).setValue(
            Optional.of("different " + TEXT_CONTENT)
        );

        storage.addWatcher(
            new StorageWatcher() {
                @Override
                public void onValueChange(final Optional<StorageValue> oldValue,
                                          final Optional<StorageValue> newValue) {
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
                    StorageSharedNativeFileTest.this.fired = true;
                }
            },
            context
        );

        this.fired = false;

        storage.save(
            storageValue,
            context
        );

        while (this.polling && System.currentTimeMillis() < end) {
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

    private boolean polling;
    private boolean fired;

    // Storage..........................................................................................................

    @Override
    public StorageSharedNativeFile<FakeStorageContext> createStorage() {
        return this.createStorage(POLLER);
    }

    private StorageSharedNativeFile<FakeStorageContext> createStorage(final WatchServicePoller<FakeStorageContext> poller) {
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

            return StorageSharedNativeFile.with(
                root,
                poller
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
                return StorageSharedNativeFileTest.CHARSET;
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
                    Converters.hasText(),
                    Converters.hasBinaryToString(),
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
                            TextPrinting.with(
                                Indentation.SPACES2,
                                LineEnding.NL
                            ).setCharset(StandardCharsets.UTF_8),
                            CURRENCY_LOCALE_CONTEXT,
                            DateTimeContexts.basic(
                                DateTimeSymbols.fromDateFormatSymbols(
                                    new DateFormatSymbols(StorageSharedNativeFileTest.LOCALE)
                                ),
                                StorageSharedNativeFileTest.LOCALE,
                                1920, // defaultYear
                                20, // twoDigitYear
                                LocalDateTime::now
                            ),
                            DECIMAL_NUMBER_CONTEXT
                        ),
                        EXPRESSION_NUMBER_KIND
                    ),
                    JsonNodeMarshallUnmarshallContexts.basic(
                        JsonNodeMarshallContexts.basic(),
                        JsonNodeUnmarshallContexts.basic(
                            EXPRESSION_NUMBER_KIND,
                            CURRENCY_LOCALE_CONTEXT, // CurrencyCodeLanguageTagContext
                            DECIMAL_NUMBER_CONTEXT.mathContext()
                        )
                    )
                )
            );

            @Override
            public Optional<EmailAddress> user() {
                return Optional.of(
                    StorageSharedNativeFileTest.USER
                );
            }
        };
    }

    @Override
    public Class<StorageSharedNativeFile<FakeStorageContext>> type() {
        return Cast.to(StorageSharedNativeFile.class);
    }
}
