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
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.reflect.MethodAttributes;
import walkingkooka.reflect.PublicStaticHelperTesting;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.text.printer.TreePrintableTesting;

import java.lang.reflect.Method;
import java.util.List;

public final class StorageConvertersTest implements PublicStaticHelperTesting<StorageConverters>,
    TreePrintableTesting {

    @Test
    public void testConverterCollectionWithAllConvertersPrintTree() throws Exception {
        final List<Converter<ConverterContext>> converters = Lists.array();

        for (final Method method : StorageConverters.class.getMethods()) {
            if (false == MethodAttributes.STATIC.is(method)) {
                continue;
            }

            if (false == method.getReturnType().equals(Converter.class)) {
                continue;
            }

            if (method.getParameterCount() != 0) {
                continue;
            }

            if (method.getName().equals("fake")) {
                continue;
            }

            converters.add(
                (Converter<ConverterContext>) method.invoke(null)
            );
        }

        converters.sort(
            (Converter<?> left, Converter<?> right) -> left.toString().compareTo(right.toString())
        );

        this.treePrintAndCheck(
            (TreePrintable) Converters.collection(converters),
            "ConverterCollection\n" +
                "  * to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueBinary)\n" +
                "  *.csv to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedCsv)\n" +
                "  *.env to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedEnvironment)\n" +
                "  *.expression.txt to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedExpression)\n" +
                "  *.json to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedJson)\n" +
                "  *.properties to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedProperties)\n" +
                "  *.tsv to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedTsv)\n" +
                "  *.txt to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinarySharedTxt)\n" +
                "  StorageBinary *.csv | text/csv to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedCsv)\n" +
                "  StorageBinary *.env | text/x-env to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedEnvironment)\n" +
                "  StorageBinary *.expression.txt | text/expression to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedExpression)\n" +
                "  StorageBinary *.json | application/json to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedJson)\n" +
                "  StorageBinary *.properties | text/x-java-properties to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedProperties)\n" +
                "  StorageBinary *.tsv | text/tab-separated-values to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedTsv)\n" +
                "  StorageBinary *.txt | text/plain to StorageValue (walkingkooka.storage.convert.StorageConverterStorageBinaryToStorageValueSharedTxt)\n" +
                "  StorageValue(Binary) to StorageBinary (walkingkooka.storage.convert.StorageConverterStorageValueToStorageBinaryBinary)\n" +
                "  StorageValueInfoList -> Text (walkingkooka.storage.convert.StorageConverterStorageValueInfoListToText)\n" +
                "  TEXT to StoragePath (walkingkooka.storage.convert.StorageConverterTextToStoragePath)\n"
        );
    }

    @Override
    public Class<StorageConverters> type() {
        return StorageConverters.class;
    }

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }
}
