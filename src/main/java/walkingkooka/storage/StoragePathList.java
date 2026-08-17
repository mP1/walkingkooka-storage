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
import walkingkooka.text.HasText;
import walkingkooka.text.HasTextWithLineBreaks;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * An immutable list of {@link StoragePath}.
 */
public final class StoragePathList extends AbstractList<StoragePath>
    implements ImmutableListDefaults<StoragePathList, StoragePath>,
    HasText,
    HasTextWithLineBreaks,
    TreePrintable {

    /**
     * An empty {@link StoragePathList}.
     */
    public final static StoragePathList EMPTY = new StoragePathList(
        Lists.empty()
    );

    /**
     * Factory that creates a {@link StoragePathList} from the list of {@link StoragePath paths}.
     */
    public static StoragePathList with(final Collection<StoragePath> paths) {
        Objects.requireNonNull(paths, "paths");

        StoragePathList storagePathList;

        if (paths instanceof StoragePathList) {
            storagePathList = (StoragePathList) paths;
        } else {
            final List<StoragePath> copy = Lists.array();
            for (final StoragePath mountPoint : paths) {
                copy.add(
                    Objects.requireNonNull(
                        mountPoint,
                        "includes null " + StoragePath.class.getSimpleName()
                    )
                );
            }

            switch (paths.size()) {
                case 0:
                    storagePathList = EMPTY;
                    break;
                default:
                    storagePathList = new StoragePathList(copy);
                    break;
            }
        }

        return storagePathList;
    }

    private StoragePathList(final List<StoragePath> paths) {
        this.paths = paths;
    }

    @Override
    public StoragePath get(int index) {
        return this.paths.get(index);
    }

    @Override
    public int size() {
        return this.paths.size();
    }

    private final List<StoragePath> paths;

    @Override
    public void elementCheck(final StoragePath paths) {
        Objects.requireNonNull(paths, "paths");
    }

    @Override
    public StoragePathList setElements(final Collection<StoragePath> paths) {
        final StoragePathList copy = with(paths);

        return this.equals(copy) ?
            this :
            copy;
    }

    // HasText..........................................................................................................

    @Override
    public String text() {
        return this.textWithLineBreaks(LineEnding.TERMINAL);
    }

    // HasTextWithLineBreaks............................................................................................

    @Override
    public String textWithLineBreaks(final LineEnding lineEnding) {
        Objects.requireNonNull(lineEnding, "lineEnding");

        return this.paths.stream()
            .map(HasText::text)
            .collect(
                Collectors.joining(
                    lineEnding, // delimiter
                    "", // prefix
                    lineEnding // suffix last mount point will have line break
                )
            );
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());

        printer.indent();
        {
            this.paths.forEach(p -> p.printTree(printer));
        }
        printer.outdent();
    }

    // json.............................................................................................................

    static StoragePathList unmarshall(final JsonNode node,
                                      final JsonNodeUnmarshallContext context) {
        return with(
            context.unmarshallList(
                node,
                StoragePath.class
            )
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallCollection(this.paths);
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(StoragePathList.class),
            StoragePathList::unmarshall,
            StoragePathList::marshall,
            StoragePathList.class
        );
    }
}
