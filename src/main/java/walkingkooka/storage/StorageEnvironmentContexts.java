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

import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.reflect.PublicStaticHelper;

import java.util.function.Predicate;

/**
 * A collection of {@link StorageEnvironmentContext}
 */
public final class StorageEnvironmentContexts implements PublicStaticHelper {

    /**
     * {@see StorageEnvironmentContextBasic}
     */
    public static StorageEnvironmentContext basic(final EnvironmentContext environmentContext) {
        return StorageEnvironmentContextBasic.with(environmentContext);
    }

    /**
     * {@see FakeStorageEnvironmentContext}
     */
    public static FakeStorageEnvironmentContext fake() {
        return new FakeStorageEnvironmentContext();
    }

    /**
     * {@see StorageEnvironmentContextReadOnly}
     */
    public static StorageEnvironmentContext readOnly(final Predicate<EnvironmentValueName<?>> readOnlyFilter,
                                                     final EnvironmentContext environmentContext) {
        return StorageEnvironmentContextReadOnly.with(
            readOnlyFilter,
            environmentContext
        );
    }

    /**
     * Stop creation
     */
    private StorageEnvironmentContexts() {
        throw new UnsupportedOperationException();
    }
}
