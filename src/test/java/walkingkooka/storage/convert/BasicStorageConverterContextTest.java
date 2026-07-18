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

package walkingkooka.storage.convert;

import org.junit.jupiter.api.Test;
import walkingkooka.Binary;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.header.MediaType;
import walkingkooka.storage.FakeHasUserDirectories;
import walkingkooka.storage.StoragePath;
import walkingkooka.tree.expression.convert.ExpressionNumberBinaryNumberConverterFunctions;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContext;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContextTesting;

import java.math.MathContext;
import java.text.DateFormatSymbols;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicStorageConverterContextTest implements StorageConverterContextTesting<BasicStorageConverterContext>,
    DecimalNumberContextDelegator,
    JsonNodeMarshallUnmarshallContextTesting {

    private final static Converter<StorageConverterContext> CONVERTER = Converters.collection(
        Lists.of(
            Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString(),
            StorageConverters.textToStoragePath()
        )
    );

    private final static JsonNodeConverterContext CONVERTER_CONTEXT = JsonNodeConverterContexts.fake();

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = DecimalNumberContexts.american(MathContext.DECIMAL32);

    // with.............................................................................................................

    @Test
    public void testWithNullConverterFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicStorageConverterContext.with(
                null,
                HAS_USER_DIRECTORIES,
                MEDIA_TYPE_DETECTOR,
                CONVERTER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullCurrentWorkingDirectoryFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicStorageConverterContext.with(
                CONVERTER,
                null,
                MEDIA_TYPE_DETECTOR,
                CONVERTER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullMediaTypeDetectorFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicStorageConverterContext.with(
                CONVERTER,
                HAS_USER_DIRECTORIES,
                null,
                CONVERTER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullJsonNodeConverterContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicStorageConverterContext.with(
                CONVERTER,
                new FakeHasUserDirectories(),
                MEDIA_TYPE_DETECTOR,
                null
            )
        );
    }

    // converter........................................................................................................

    @Test
    public void testCanConvert() {
        this.canConvertAndCheck(
            this.createContext(),
            "/storage-path123",
            StoragePath.class,
            true
        );
    }

    @Test
    public void testConvert() {
        final String text = "/storage-path123";

        this.convertAndCheck(
            this.createContext(),
            text,
            StoragePath.class,
            StoragePath.parse(text)
        );
    }

    // currentWorkingDirectory..........................................................................................

    @Test
    public void testCurrentWorkingDirectory() {
        this.currentWorkingDirectoryAndCheck(
            this.createContext(),
            CURRENT_WORKING_DIRECTORY
        );
    }

    // detect...........................................................................................................

    @Test
    public void testDetect() {
        this.detectAndCheck(
            this.createContext(),
            "file.txt",
            Binary.EMPTY,
            MediaType.BINARY
        );
    }

    // parseStoragePath.................................................................................................

    @Test
    public void testParseStoragePath() {
        this.parseStoragePathAndCheck(
            this.createContext(),
            "after123",
            StoragePath.parse(CURRENT_WORKING_DIRECTORY + "/after123")
        );
    }

    @Override
    public BasicStorageConverterContext createContext() {
        return BasicStorageConverterContext.with(
            CONVERTER,
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
                        DateTimeContexts.basic(
                            DateTimeSymbols.fromDateFormatSymbols(
                                new DateFormatSymbols(LOCALE)
                            ),
                            LOCALE,
                            1920, // defaultYear
                            20, // twoDigitYear
                            HAS_NOW
                        ),
                        DecimalNumberContexts.american(MATH_CONTEXT)
                    ),
                    EXPRESSION_NUMBER_KIND
                ),
                JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT
            )
        );
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public int decimalNumberDigitCount() {
        return DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT;
    }

    @Override
    public MathContext mathContext() {
        return MathContext.DECIMAL32;
    }

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return DECIMAL_NUMBER_CONTEXT;
    }

    // class............................................................................................................

    @Override
    public Class<BasicStorageConverterContext> type() {
        return BasicStorageConverterContext.class;
    }
}
