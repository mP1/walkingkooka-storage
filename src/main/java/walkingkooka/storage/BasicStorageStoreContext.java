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
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.environment.EnvironmentValueName;

import java.util.Objects;

final class BasicStorageStoreContext implements StorageStoreContext, EnvironmentContextDelegator {

    static BasicStorageStoreContext with(final EnvironmentContext environmentContext) {
        return new BasicStorageStoreContext(
            Objects.requireNonNull(environmentContext, "environmentContext")
        );
    }

    private BasicStorageStoreContext(final EnvironmentContext environmentContext) {
        this.environmentContext = environmentContext;
    }

    @Override
    public <T> StorageStoreContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                       final T value) {
        this.environmentContext.setEnvironmentValue(name, value);
        return this;
    }

    @Override
    public StorageStoreContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        this.environmentContext.removeEnvironmentValue(name);
        return this;
    }

    @Override
    public EnvironmentContext environmentContext() {
        return environmentContext;
    }

    private final EnvironmentContext environmentContext;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.environmentContext.toString();
    }
}
