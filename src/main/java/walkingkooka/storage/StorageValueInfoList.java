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

import walkingkooka.collect.list.ImmutableListDefaults;
import walkingkooka.collect.list.Lists;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * An immutable list of {@link StorageValueInfo}.
 */
public final class StorageValueInfoList extends AbstractList<StorageValueInfo>
    implements ImmutableListDefaults<StorageValueInfoList, StorageValueInfo> {

    /**
     * An empty {@link StorageValueInfoList}.
     */
    public final static StorageValueInfoList EMPTY = new StorageValueInfoList(
        Lists.empty()
    );

    /**
     * Factory that creates a {@link StorageValueInfoList} from the list of {@link StorageValueInfo infos}.
     */
    public static StorageValueInfoList with(final Collection<StorageValueInfo> infos) {
        Objects.requireNonNull(infos, "infos");

        StorageValueInfoList StorageValueInfoList;

        if (infos instanceof StorageValueInfoList) {
            StorageValueInfoList = (StorageValueInfoList) infos;
        } else {
            final List<StorageValueInfo> copy = Lists.array();
            for (final StorageValueInfo name : infos) {
                copy.add(
                    Objects.requireNonNull(name, "includes null " + StorageValueInfo.class.getSimpleName())
                );
            }

            switch (infos.size()) {
                case 0:
                    StorageValueInfoList = EMPTY;
                    break;
                default:
                    StorageValueInfoList = new StorageValueInfoList(copy);
                    break;
            }
        }

        return StorageValueInfoList;
    }

    private StorageValueInfoList(final List<StorageValueInfo> infos) {
        this.infos = infos;
    }

    @Override
    public StorageValueInfo get(int index) {
        return this.infos.get(index);
    }

    @Override
    public int size() {
        return this.infos.size();
    }

    private final List<StorageValueInfo> infos;

    @Override
    public void elementCheck(final StorageValueInfo infos) {
        Objects.requireNonNull(infos, "infos");
    }

    @Override
    public StorageValueInfoList setElements(final Collection<StorageValueInfo> infos) {
        final StorageValueInfoList copy = with(infos);
        return this.equals(copy) ?
            this :
            copy;
    }

    // json.............................................................................................................

    static StorageValueInfoList unmarshall(final JsonNode node,
                                           final JsonNodeUnmarshallContext context) {
        return with(
            context.unmarshallList(
                node,
                StorageValueInfo.class
            )
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallCollection(this.infos);
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(StorageValueInfoList.class),
            StorageValueInfoList::unmarshall,
            StorageValueInfoList::marshall,
            StorageValueInfoList.class
        );
    }
}
