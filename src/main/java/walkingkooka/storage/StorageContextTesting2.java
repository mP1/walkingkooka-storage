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

import walkingkooka.convert.ConverterLikeTesting2;
import walkingkooka.net.header.MediaTypeDetectorTesting2;

public interface StorageContextTesting2<C extends StorageContext> extends StorageContextTesting,
    StorageEnvironmentContextTesting2<C>,
    ConverterLikeTesting2<C>,
    CanParseStoragePathTesting2<C>,
    MediaTypeDetectorTesting2<C> {

    // CanParseStoragePath..............................................................................................

    @Override
    default C createCanParseStoragePath() {
        return this.createContext();
    }

    // ConverterLike....................................................................................................

    @Override
    default C createConverterLike() {
        return this.createContext();
    }

    // MediaTypeDetectorTesting.........................................................................................

    @Override
    default C createMediaTypeDetector() {
        return this.createContext();
    }

    // class............................................................................................................

    @Override
    default String typeNameSuffix() {
        return StorageContext.class.getSimpleName();
    }
}
