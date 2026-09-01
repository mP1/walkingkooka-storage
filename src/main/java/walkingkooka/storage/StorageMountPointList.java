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

    /**
     * Factory that creates a {@link StorageMountPointList} from the list of {@link StorageMountPoint infos}.
     */
    public static StorageMountPointList with(final Collection<StorageMountPoint<?>> mountPoints) {
        Objects.requireNonNull(mountPoints, "mountPoints");

        StorageMountPointList StorageMountPointList;

        if (mountPoints instanceof StorageMountPointList) {
            StorageMountPointList = (StorageMountPointList) mountPoints;
        } else {
            final List<StorageMountPoint<?>> copy = Lists.array();
            for (final StorageMountPoint<?> mountPoint : mountPoints) {
                copy.add(
                    Objects.requireNonNull(
                        mountPoint,
                        "includes null " + StorageMountPoint.class.getSimpleName()
                    )
                );
            }

            switch (mountPoints.size()) {
                case 0:
                    StorageMountPointList = EMPTY;
                    break;
                default:
                    StorageMountPointList = new StorageMountPointList(copy);
                    break;
            }
        }

        return StorageMountPointList;
    }

    private StorageMountPointList(final List<StorageMountPoint<?>> storageMountPoints) {
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
    public void elementCheck(final StorageMountPoint infos) {
        Objects.requireNonNull(infos, "infos");
    }

    @Override
    public StorageMountPointList setElements(final Collection<StorageMountPoint<?>> storageMountPoints) {
        final StorageMountPointList copy = with(storageMountPoints);

        return this.equals(copy) ?
            this :
            copy;
    }

    // HasText..........................................................................................................

    @Override
    public String text() {
        return this.textWithLineBreaks(LineEnding.TERMINAL);
    }

    // HasMultiLineText.................................................................................................

    @Override
    public String multiLineText(final TextContext context) {
        Objects.requireNonNull(context, "context");

        return this.textWithLineBreaks(
            context.lineEnding()
        );
    }

    private String textWithLineBreaks(final LineEnding lineEnding) {
        return this.storageMountPoints.stream()
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
            this.storageMountPoints.forEach(i -> i.printTree(printer));
        }
        printer.outdent();
    }
}
