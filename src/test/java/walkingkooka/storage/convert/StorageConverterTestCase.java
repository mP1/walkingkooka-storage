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

import walkingkooka.ToStringTesting;
import walkingkooka.convert.ConverterTesting2;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CharSequences;

public abstract class StorageConverterTestCase<C extends StorageConverter<FakeStorageConverterContext>> implements ConverterTesting2<C, FakeStorageConverterContext>,
    ToStringTesting<C>,
    ClassTesting<C> {

    StorageConverterTestCase() {
        super();
    }

    // class............................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public final String typeNamePrefix() {
        try {
            return Class.forName(
                CharSequences.subSequence(
                    this.getClass()
                        .getSuperclass()
                        .getName(),
                    0,
                    -"TestCase".length()
                ).toString()
            ).getSimpleName();
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final String typeNameSuffix() {
        return "";
    }
}
