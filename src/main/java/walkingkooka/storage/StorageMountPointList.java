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
import walkingkooka.text.HasMultiLineText;
import walkingkooka.text.HasText;
import walkingkooka.text.LineEnding;
import walkingkooka.text.MultiLineText;
import walkingkooka.text.TextContext;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * An immutable list of {@link StorageMountPoint}. Note marshalling/unmarshalling to/from JSON is not supported.
 */
public final class StorageMountPointList extends AbstractList<StorageMountPoint<?>>
    implements ImmutableListDefaults<StorageMountPointList, StorageMountPoint<?>>,
    HasText,
    HasMultiLineText,
    TreePrintable {

    /**
     * An empty {@link StorageMountPointList}.
     */
    public final static StorageMountPointList EMPTY = new StorageMountPointList(
        Lists.empty()
    );

    // @VisibleForTesting
    StorageMountPointList(final List<StorageMountPoint<?>> storageMountPoints) {
        this.storageMountPoints = storageMountPoints;
    }

    @Override
    public StorageMountPoint<?> get(int index) {
        return this.storageMountPoints.get(index);
    }

    @Override
    public int size() {
        return this.storageMountPoints.size();
    }

    private final List<StorageMountPoint<?>> storageMountPoints;

    @Override
    public void elementCheck(final StorageMountPoint storageMountPoint) {
        Objects.requireNonNull(storageMountPoint, "Includes null StorageMountPoint");
    }

    @Override
    public StorageMountPointList setElements(final Collection<StorageMountPoint<?>> elements) {
        return (StorageMountPointList)
            this.setElementsMaybeCreate(
                elements,
                (final Collection<StorageMountPoint<?>> collection) -> collection instanceof StorageMountPointList,
                this::elementCheck,
                this::replaceStorageMountPoints
            );
    }

    @Override
    public StorageMountPointList subList(final int from,
                                         final int to) {
        return 0 == from && this.size() == to ?
            this :
            this.replaceStorageMountPoints(
                this.storageMountPoints.subList(
                    from,
                    to
                )
            );
    }

    private StorageMountPointList replaceStorageMountPoints(final List<StorageMountPoint<?>> storageMountPoints) {
        return storageMountPoints.isEmpty() ?
            EMPTY :
            new StorageMountPointList(storageMountPoints);
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
            this.storageMountPoints.stream()
                .map(HasText::text)
                .collect(
                    Collectors.joining(
                        lineEnding, // delimiter
                        "", // prefix
                        lineEnding // suffix last mount point will have line break
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
            this.storageMountPoints.forEach(i -> i.printTree(printer));
        }
        printer.outdent();
    }
}
