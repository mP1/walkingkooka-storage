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

import walkingkooka.Binary;
import walkingkooka.net.header.MediaType;
import walkingkooka.storage.StoragePath;
import walkingkooka.tree.json.convert.FakeJsonNodeConverterContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.util.Optional;

public class FakeStorageConverterContext extends FakeJsonNodeConverterContext implements StorageConverterContext {

    public FakeStorageConverterContext() {
        super();
    }

    @Override
    public MediaType detect(final String filename,
                            final Binary content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<StoragePath> currentWorkingDirectory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<StoragePath> homeDirectory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public StoragePath parseStoragePath(final String text) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StorageConverterContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor jsonNodeMarshallContextObjectPostProcessor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StorageConverterContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor jsonNodeUnmarshallContextPreProcessor) {
        throw new UnsupportedOperationException();
    }
}
