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

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.HasValueTesting;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.naming.HasPathTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.watch.FakeValueChangeWatcher;
import walkingkooka.watch.ValueChangeWatcher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StatefulStorageWatcherTest implements StorageContextTesting,
    HasPathTesting,
    HasValueTesting,
    ClassTesting<StatefulStorageWatcher<JsonNode>> {

    private final static StoragePath STORAGE_PATH = StoragePath.parse("/watch/file.properties");

    private final static ValueChangeWatcher<JsonNode> VALUE_CHANGE_WATCHER = new FakeValueChangeWatcher<>();

    private final static StorageContext STORAGE_CONTEXT = StorageContexts.fake();

    private final static JsonNode JSON = JsonNode.parse("{\"Hello\":\"World123\"}");

    // with.............................................................................................................

    @Test
    public void testWithNullStoragePathFails() {
        assertThrows(
            NullPointerException.class,
            () -> StatefulStorageWatcher.with(
                null,
                VALUE_CHANGE_WATCHER,
                STORAGE_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullStorageValueChangeFails() {
        assertThrows(
            NullPointerException.class,
            () -> StatefulStorageWatcher.with(
                STORAGE_PATH,
                null,
                STORAGE_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullStorageContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> StatefulStorageWatcher.with(
                STORAGE_PATH,
                VALUE_CHANGE_WATCHER,
                null
            )
        );
    }

    @Test
    public void testWith() {
        final StorageContext storageContext = this.storageContext();

        final StatefulStorageWatcher<JsonNode> statefulStorageWatcher = StatefulStorageWatcher.with(
            STORAGE_PATH,
            new FakeValueChangeWatcher<>() {
                @Override
                public void onValueChange(final Optional<JsonNode> oldValue,
                                          final Optional<JsonNode> newValue) {
                }
            },
            storageContext
        );

        this.pathAndCheck(
            statefulStorageWatcher,
            STORAGE_PATH
        );

        this.valueAndCheck(
            statefulStorageWatcher,
            Optional.empty()
        );
    }

    @Test
    public void testWithValuePresent() {
        final StorageContext storageContext = this.storageContext();

        storageContext.saveStorage(
            StorageValue.with(STORAGE_PATH)
                .setValue(
                    Optional.of(JSON)
                )
        );

        this.fired = false;

        final StatefulStorageWatcher<JsonNode> statefulStorageWatcher = StatefulStorageWatcher.with(
            STORAGE_PATH,
            new ValueChangeWatcher<JsonNode>() {
                @Override
                public void onValueChange(final Optional<JsonNode> oldValue,
                                          final Optional<JsonNode> newValue) {

                    checkEquals(
                        Optional.of(JSON),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(JSON),
                        newValue,
                        "newValue"
                    );

                    StatefulStorageWatcherTest.this.fired = true;
                }
            },
            storageContext
        );

        this.pathAndCheck(
            statefulStorageWatcher,
            STORAGE_PATH
        );

        this.valueAndCheck(
            statefulStorageWatcher,
            Optional.of(JSON)
        );

        this.checkEquals(
            true,
            this.fired
        );
    }

    @Test
    public void testStorageValueChange() {
        final StorageContext storageContext = this.storageContext();

        storageContext.saveStorage(
            StorageValue.with(STORAGE_PATH)
                .setValue(
                    Optional.of(JsonNode.string("1st"))
                )
        );

        this.fired = false;

        final StatefulStorageWatcher<JsonNode> statefulStorageWatcher = StatefulStorageWatcher.with(
            STORAGE_PATH,
            new ValueChangeWatcher<JsonNode>() {
                @Override
                public void onValueChange(final Optional<JsonNode> oldValue,
                                          final Optional<JsonNode> newValue) {
                    StatefulStorageWatcherTest.this.lastValue = newValue;
                    StatefulStorageWatcherTest.this.fired = true;
                }
            },
            storageContext
        );

        storageContext.saveStorage(
            StorageValue.with(STORAGE_PATH)
                .setValue(
                    Optional.of(JSON)
                )
        );

        this.valueAndCheck(
            statefulStorageWatcher,
            Optional.of(JSON)
        );
    }

    @Test
    public void testLoad() {
        final StorageContext storageContext = this.storageContext();

        storageContext.saveStorage(
            StorageValue.with(STORAGE_PATH)
                .setValue(
                    Optional.of(JsonNode.string("1st"))
                )
        );

        this.fired = false;

        final StatefulStorageWatcher<JsonNode> statefulStorageWatcher = StatefulStorageWatcher.with(
            STORAGE_PATH,
            new ValueChangeWatcher<JsonNode>() {
                @Override
                public void onValueChange(final Optional<JsonNode> oldValue,
                                          final Optional<JsonNode> newValue) {
                    StatefulStorageWatcherTest.this.lastValue = newValue;
                    StatefulStorageWatcherTest.this.fired = true;
                }
            },
            storageContext
        );

        storageContext.saveStorage(
            StorageValue.with(STORAGE_PATH)
                .setValue(
                    Optional.of(JSON)
                )
        );

        statefulStorageWatcher.value = null;
        statefulStorageWatcher.load();

        this.valueAndCheck(
            statefulStorageWatcher,
            Optional.of(JSON)
        );
    }


    private boolean fired;

    private Optional<JsonNode> lastValue;

    private StorageContext storageContext() {
        return StorageContexts.basic(
            ConverterContexts.fake(), // ConverterLike
            MEDIA_TYPE_DETECTOR,
            Storages.treeMapStore(),
            STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment()
        );
    }

    // class............................................................................................................

    @Override
    public Class<StatefulStorageWatcher<JsonNode>> type() {
        return Cast.to(StatefulStorageWatcher.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
