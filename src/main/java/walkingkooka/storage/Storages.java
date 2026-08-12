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

import javaemul.internal.annotations.GwtIncompatible;
import walkingkooka.reflect.PublicStaticHelper;

import java.nio.file.Path;
import java.util.function.Function;

/**
 * A collection of {@link Storage}.
 */
public final class Storages implements PublicStaticHelper {

    /**
     * {@see StorageShared2WrapperExpandedCurrentWorkingDirectory}
     */
    public static <C extends StorageContext> Storage<C> currentWorkingDirectory(final Storage<C> storage) {
        return StorageShared2WrapperExpandedCurrentWorkingDirectory.with(storage);
    }

    /**
     * {@see StorageShared2Empty}
     */
    public static <C extends StorageContext> Storage<C> empty() {
        return StorageShared2Empty.instance();
    }

    /**
     * {@see StorageShared2Environment}
     */
    public static <C extends StorageContext> Storage<C> environment() {
        return StorageShared2Environment.instance();
    }

    /**
     * {@see StorageShared2WrapperExplodedZipFile}
     */
    @GwtIncompatible
    public static <C extends StorageContext> Storage<C> explodedZipFile(final StoragePath archive,
                                                                        final Storage<C> storage) {
        return StorageShared2WrapperExplodedZipFile.with(
            archive,
            storage
        );
    }

    /**
     * {@see FakeStorage}
     */
    public static <C extends StorageContext> Storage<C> fake() {
        return new FakeStorage<>();
    }

    /**
     * {@see StorageShared2WrapperExpandedHomeDirectory}
     */
    public static <C extends StorageContext> Storage<C> homeDirectory(final Storage<C> storage) {
        return StorageShared2WrapperExpandedHomeDirectory.with(storage);
    }

    /**
     * {@see StorageSharedMount}
     */
    public static <C extends StorageContext> Storage<C> mount(final Storage<C> storage) {
        return StorageSharedMount.with(storage);
    }

    /**
     * {@see StorageShared2NativeFile}
     */
    @GwtIncompatible
    public static <C extends StorageContext> Storage<C> nativeStorage(final Path root,
                                                                      final WatchServicePoller<C> poller) {
        return StorageShared2NativeFile.with(
            root,
            poller
        );
    }

    /**
     * {@see StorageShared2WrapperPrefixed}
     */
    public static <C extends StorageContext> Storage<C> prefixed(final StoragePath prefix,
                                                                 final Storage<C> storage) {
        return StorageShared2WrapperPrefixed.with(
            prefix,
            storage
        );
    }

    /**
     * {@see ReadOnlyStorage}
     */
    public static <C extends StorageContext> Storage<C> readOnly(final Storage<C> storage) {
        return ReadOnlyStorage.with(storage);
    }

    /**
     * {@see StorageShared2TreeMapStore}
     */
    public static <C extends StorageContext> Storage<C> treeMapStore() {
        return StorageShared2TreeMapStore.empty();
    }

    /**
     * {@see StorageShared2Value}
     */
    public static <C extends StorageContext> Storage<C> value(final Function<C, StorageValue> value) {
        return StorageShared2Value.with(value);
    }

    /**
     * Stop creation
     */
    private Storages() {
        throw new UnsupportedOperationException();
    }
}
