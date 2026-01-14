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
import walkingkooka.environment.FakeEnvironmentContext;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.text.LineEnding;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class FakeStorageContext extends FakeEnvironmentContext implements StorageContext {

    @Override
    public StorageContext cloneEnvironment() {
        throw new UnsupportedOperationException();
    }

    @Override
    public StorageContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> StorageContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                  final T value) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(value, "value");
        throw new UnsupportedOperationException();
    }

    @Override
    public StorageContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        Objects.requireNonNull(name, "name");
        throw new UnsupportedOperationException();
    }

    @Override
    public StorageContext setLineEnding(final LineEnding lineEnding) {
        Objects.requireNonNull(lineEnding, "lineEnding");
        throw new UnsupportedOperationException();
    }

    @Override
    public StorageContext setLocale(final Locale locale) {
        Objects.requireNonNull(locale, "locale");
        throw new UnsupportedOperationException();
    }

    @Override
    public StorageContext setUser(final Optional<EmailAddress> user) {
        Objects.requireNonNull(user, "user");
        throw new UnsupportedOperationException();
    }
}
