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
import walkingkooka.datetime.HasOptionalLastModified;
import walkingkooka.text.HasMultiLineText;
import walkingkooka.text.HasText;
import walkingkooka.text.LineEnding;
import walkingkooka.text.MultiLineText;
import walkingkooka.text.TextContext;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.time.LocalDateTime;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An immutable list of {@link StorageValueInfo}.
 */
public final class StorageValueInfoList extends AbstractList<StorageValueInfo>
    implements ImmutableListDefaults<StorageValueInfoList, StorageValueInfo>,
    HasOptionalLastModified,
    HasText,
    HasMultiLineText,
    TreePrintable {

    /**
     * An empty {@link StorageValueInfoList}.
     */
    public final static StorageValueInfoList EMPTY = new StorageValueInfoList(
        Lists.empty()
    );

    // @VisibleForTesting
    StorageValueInfoList(final List<StorageValueInfo> infos) {
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
    public void elementCheck(final StorageValueInfo storageValueInfo) {
        Objects.requireNonNull(storageValueInfo, "Includes null StorageValueInfo");
    }

    @Override
    public StorageValueInfoList setElements(final Collection<StorageValueInfo> elements) {
        return (StorageValueInfoList)
            this.setElementsMaybeCreate(
                elements,
                (final Collection<StorageValueInfo> collection) -> collection instanceof StorageValueInfoList,
                this::elementCheck,
                this::replaceStorageValueInfos
            );
    }

    @Override
    public StorageValueInfoList subList(final int from,
                                        final int to) {
        return 0 == from && this.size() == to ?
            this :
            this.replaceStorageValueInfos(
                this.infos.subList(
                    from,
                    to
                )
            );
    }

    private StorageValueInfoList replaceStorageValueInfos(final List<StorageValueInfo> storageValueInfos) {
        return storageValueInfos.isEmpty() ?
            EMPTY :
            new StorageValueInfoList(storageValueInfos);
    }

    // json.............................................................................................................

    static StorageValueInfoList unmarshall(final JsonNode node,
                                           final JsonNodeUnmarshallContext context) {
        return EMPTY.setElements(
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

    // HasText..........................................................................................................

    @Override
    public String text() {
        return this.textWithLineBreaks(LineEnding.TERMINAL)
            .text();
    }

    // HasMultiLineText.................................................................................................

    @Override
    public MultiLineText multiLineText(final TextContext context) {
        Objects.requireNonNull(context, "context");

        return this.textWithLineBreaks(
            context.lineEnding()
        );
    }

    private MultiLineText textWithLineBreaks(final LineEnding lineEnding) {
        return MultiLineText.EMPTY.setText(
            this.infos.stream()
                .map(HasText::text)
                .collect(
                    Collectors.joining(
                        lineEnding, // delimiter
                        "", // prefix
                        lineEnding // suffix
                    )
                )
        );
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());

        printer.indent();
        {
            this.infos.forEach(i -> i.printTree(printer));
        }
        printer.outdent();
    }

    // HasOptionalLastModified...................................................................................

    /**
     * Returns the most recent {@link LocalDateTime} from the zero or many {@link StorageValueInfo}.
     */
    @Override
    public Optional<LocalDateTime> lastModified() {
        return this.infos.stream()
            .map((StorageValueInfo storageValueInfo) -> storageValueInfo.lastModified())
            .filter(Objects::nonNull)
            .sorted(Comparator.reverseOrder())
            .findFirst();
    }
}
