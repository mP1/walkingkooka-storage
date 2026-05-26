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

import walkingkooka.storage.HasUserDirectories;
import walkingkooka.storage.HasUserDirectoriesDelegator;
import walkingkooka.storage.StoragePath;
import walkingkooka.tree.json.convert.JsonNodeConverterContext;
import walkingkooka.tree.json.convert.JsonNodeConverterContextDelegator;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.util.Objects;

final class BasicStorageConverterContext implements StorageConverterContext,
    JsonNodeConverterContextDelegator,
    HasUserDirectoriesDelegator {

    static BasicStorageConverterContext with(final HasUserDirectories hasUserDirectories,
                                             final JsonNodeConverterContext context) {
        Objects.requireNonNull(hasUserDirectories, "hasUserDirectories");
        Objects.requireNonNull(context, "context");

        return new BasicStorageConverterContext(hasUserDirectories, context);
    }

    private BasicStorageConverterContext(final HasUserDirectories hasUserDirectories,
                                         final JsonNodeConverterContext context) {
        this.hasUserDirectories = hasUserDirectories;
        this.context = context;
    }


    @Override
    public StoragePath parseStoragePath(final String text) {
        return StoragePath.parseSpecial(
            text,
            this
        );
    }

    // StorageConverterContext..........................................................................................

    @Override
    public HasUserDirectories hasUserDirectories() {
        return this.hasUserDirectories;
    }

    private final HasUserDirectories hasUserDirectories;

    // JsonNodeConverterContextDelegator................................................................................


    @Override
    public StorageConverterContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor jsonNodeMarshallContextObjectPostProcessor) {
        final JsonNodeConverterContext before = this.context;
        final JsonNodeConverterContext after = before.setObjectPostProcessor(jsonNodeMarshallContextObjectPostProcessor);

        return before != after ?
            new BasicStorageConverterContext(
                this.hasUserDirectories,
                after
            ) :
            this;
    }

    @Override
    public StorageConverterContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor jsonNodeUnmarshallContextPreProcessor) {
        final JsonNodeConverterContext before = this.context;
        final JsonNodeConverterContext after = before.setPreProcessor(jsonNodeUnmarshallContextPreProcessor);

        return before != after ?
            new BasicStorageConverterContext(
                this.hasUserDirectories,
                after
            ) :
            this;
    }

    @Override
    public JsonNodeConverterContext jsonNodeConverterContext() {
        return this.context;
    }

    private final JsonNodeConverterContext context;

    // toString.........................................................................................................

    @Override
    public String toString() {
        return "hasUserDirectories: " + this.hasUserDirectories + ", context: " + this.context;
    }
}
