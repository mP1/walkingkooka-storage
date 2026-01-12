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

import walkingkooka.build.Builder;
import walkingkooka.build.BuilderException;
import walkingkooka.collect.list.Lists;

import java.util.List;

/**
 * A {@link Builder} that may be used to add one more mounted {@link Storage}.
 */
public final class RoutingStorageBuilder<C extends StorageContext> implements Builder<Storage<C>> {

    public static <C extends StorageContext> RoutingStorageBuilder<C> empty() {
        return new RoutingStorageBuilder();
    }

    private RoutingStorageBuilder() {
        this.routes = Lists.array();
    }

    /**
     * Adds a mount at the given path. If this mount is shadowed by previous addition a {@link IllegalArgumentException}
     * will be thrown.
     */
    public RoutingStorageBuilder<C> startsWith(final StoragePath path,
                                               final Storage store) {
        final RoutingStorageRoute<C> newRoute = RoutingStorageRoute.with(
            path,
            store
        );

        for (final RoutingStorageRoute<C> route : this.routes) {
            if (route.isMatch(path)) {
                throw new IllegalArgumentException("Invalid path " + path.quotedAppendedWithStar() + " would be shadowed by " + route);
            }
        }

        this.routes.add(newRoute);
        return this;
    }

    @Override
    public Storage<C> build() throws BuilderException {
        final List<RoutingStorageRoute<C>> copy = Lists.array();
        copy.addAll(this.routes);

        switch (copy.size()) {
            case 0:
                throw new BuilderException("Empty builder");
            default:
                return RoutingStorage.with(copy);
        }
    }

    private final List<RoutingStorageRoute<C>> routes;

    @Override
    public String toString() {
        return this.routes.toString();
    }
}
