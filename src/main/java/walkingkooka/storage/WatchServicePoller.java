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

import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Triggers a single polling of the {@link java.nio.file.WatchService} for {@link WatchKey} and events.
 */
@GwtIncompatible
public interface WatchServicePoller<C extends StorageContext> {

    /**
     * Captures the call back that can and should be called in a loop to handle polling and dispatching of {@link WatchService} events.
     */
    void beginPolling(final Consumer<WatchServicePoller<C>> poller);

    /**
     * Callback that handles polling or taking of the {@link WatchKey}.
     */
    Optional<WatchKey> pollOrTakeWatchKey(final WatchService watchService);

    /**
     * Getter that returns the active {@link StorageContext}.
     */
    C context();
}
