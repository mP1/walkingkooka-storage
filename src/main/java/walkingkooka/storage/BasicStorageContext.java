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
import walkingkooka.net.email.EmailAddress;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

final class BasicStorageContext implements StorageContext, EnvironmentContextDelegator {

    static BasicStorageContext with(final EnvironmentContext environmentContext) {
        return new BasicStorageContext(
            Objects.requireNonNull(environmentContext, "environmentContext")
        );
    }

    private BasicStorageContext(final EnvironmentContext environmentContext) {
        this.environmentContext = environmentContext;
    }

    @Override
    public StorageContext setLocale(final Locale locale) {
        return this.setEnvironmentValue(
            EnvironmentValueName.LOCALE,
            locale
        );
    }

    @Override
    public StorageContext setUser(final Optional<EmailAddress> user) {
        return user.isPresent() ?
            this.setEnvironmentValue(
                EnvironmentValueName.USER,
                user.orElse(null)
            ) :
            this.removeEnvironmentValue(EnvironmentValueName.USER);
    }

    @Override
    public StorageContext cloneEnvironment() {
        final EnvironmentContext environmentContext = this.environmentContext;
        final EnvironmentContext cloned = environmentContext.cloneEnvironment();

        // only re-create if different instance
        return environmentContext == cloned ?
            this :
            with(cloned);
    }

    @Override
    public StorageContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        // only re-create if different instance
        return this.environmentContext == environmentContext ?
            this :
            with(environmentContext);
    }

    @Override
    public <T> StorageContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                  final T value) {
        this.environmentContext.setEnvironmentValue(name, value);
        return this;
    }

    @Override
    public StorageContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
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
    public int hashCode() {
        return this.environmentContext.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof BasicStorageContext &&
                this.equals0((BasicStorageContext) other));
    }

    private boolean equals0(final BasicStorageContext other) {
        return this.environmentContext.equals(other.environmentContext);
    }

    @Override
    public String toString() {
        return this.environmentContext.toString();
    }
}
