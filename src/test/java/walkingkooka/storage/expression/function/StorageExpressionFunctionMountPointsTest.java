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

package walkingkooka.storage.expression.function;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.storage.StorageMountPoint;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageTesting;
import walkingkooka.storage.Storages;
import walkingkooka.storage.expression.function.StorageExpressionFunctionTestCase.TestStorageExpressionEvaluationContext;

import java.util.List;

public final class StorageExpressionFunctionMountPointsTest extends StorageExpressionFunctionTestCase<StorageExpressionFunctionMountPoints<TestStorageExpressionEvaluationContext>, List<StorageMountPoint<TestStorageExpressionEvaluationContext>>>
    implements StorageTesting {

    private final static List<StorageMountPoint<TestStorageExpressionEvaluationContext>> STORAGE_MOUNT_POINTS = Lists.of(
        StorageMountPoint.with(
            StoragePath.ROOT,
            Storages.treeMapStore()
        ),
        StorageMountPoint.with(
            StoragePath.parse("/mount1"),
            Storages.treeMapStore()
        )
    );

    @Test
    public void testApply() {
        final TestStorageExpressionEvaluationContext context = this.createContext();

        this.applyAndCheck(
            StorageExpressionFunctionMountPoints.instance(),
            Lists.empty(),
            context,
            STORAGE_MOUNT_POINTS
        );
    }

    @Override
    public StorageExpressionFunctionMountPoints<TestStorageExpressionEvaluationContext> createBiFunction() {
        return StorageExpressionFunctionMountPoints.instance();
    }

    @Override
    public TestStorageExpressionEvaluationContext createContext() {
        return new TestStorageExpressionEvaluationContext(Storages.fake()) {

            @Override
            public List<StorageMountPoint<?>> storageMountPoints() {
                return Cast.to(STORAGE_MOUNT_POINTS);
            }
        };
    }

    @Override
    public int minimumParameterCount() {
        return 0;
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            StorageExpressionFunctionMountPoints.instance(),
            "mountPoints"
        );
    }

    // class............................................................................................................

    @Override
    public Class<StorageExpressionFunctionMountPoints<TestStorageExpressionEvaluationContext>> type() {
        return Cast.to(StorageExpressionFunctionMountPoints.class);
    }
}
