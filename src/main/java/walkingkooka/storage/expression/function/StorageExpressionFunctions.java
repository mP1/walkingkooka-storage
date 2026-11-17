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

import javaemul.internal.annotations.GwtIncompatible;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageMountPoint;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValueInfoList;
import walkingkooka.storage.expression.StorageExpressionEvaluationContext;
import walkingkooka.tree.expression.function.ExpressionFunction;

import java.util.List;

public final class StorageExpressionFunctions implements PublicStaticHelper {

    /**
     * {@see StorageExpressionFunctionDelete}
     */
    public static <C extends StorageExpressionEvaluationContext> ExpressionFunction<Void, C> deleteStorage() {
        return StorageExpressionFunctionDelete.instance();
    }

    /**
     * {@see StorageExpressionFunctionNativeFileStorage}
     */
    @GwtIncompatible
    public static <C extends StorageExpressionEvaluationContext> ExpressionFunction<Storage<C>, C> fileStorage() {
        return StorageExpressionFunctionNativeFileStorage.instance();
    }

    /**
     * {@see StorageExpressionFunctionGetCurrentWorkingDirectory}
     */
    public static <C extends StorageExpressionEvaluationContext> ExpressionFunction<StoragePath, C> getCurrentWorkingDirectory() {
        return StorageExpressionFunctionGetCurrentWorkingDirectory.instance();
    }

    /**
     * {@see StorageExpressionFunctionGetHomeDirectory}
     */
    public static <C extends StorageExpressionEvaluationContext> ExpressionFunction<StoragePath, C> getHomeDirectory() {
        return StorageExpressionFunctionGetHomeDirectory.instance();
    }

    /**
     * {@see StorageExpressionFunctionList}
     */
    public static <C extends StorageExpressionEvaluationContext> ExpressionFunction<StorageValueInfoList, C> listStorage() {
        return StorageExpressionFunctionList.instance();
    }

    /**
     * {@see StorageExpressionFunctionMount}
     */
    public static <C extends StorageExpressionEvaluationContext> ExpressionFunction<Void, C> mountStorage() {
        return StorageExpressionFunctionMount.instance();
    }

    /**
     * {@see StorageExpressionFunctionMountPoints}
     */
    public static <C extends StorageExpressionEvaluationContext> ExpressionFunction<List<StorageMountPoint<C>>, C> mountPoints() {
        return StorageExpressionFunctionMountPoints.instance();
    }

    /**
     * {@see StorageExpressionFunctionRead}
     */
    public static <C extends StorageExpressionEvaluationContext> ExpressionFunction<Object, C> readStorage() {
        return StorageExpressionFunctionRead.instance();
    }

    /**
     * {@see StorageExpressionFunctionReadText}
     */
    public static <C extends StorageExpressionEvaluationContext> ExpressionFunction<String, C> readStorageText() {
        return StorageExpressionFunctionReadText.instance();
    }

    /**
     * {@see StorageExpressionFunctionScript}
     */
    public static <C extends StorageExpressionEvaluationContext> ExpressionFunction<Object, C> script() {
        return StorageExpressionFunctionScript.instance();
    }

    /**
     * {@see StorageExpressionFunctionSetCurrentWorkingDirectory}
     */
    public static <C extends StorageExpressionEvaluationContext> ExpressionFunction<Void, C> setCurrentWorkingDirectory() {
        return StorageExpressionFunctionSetCurrentWorkingDirectory.instance();
    }

    /**
     * {@see StorageExpressionFunctionSetHomeDirectory}
     */
    public static <C extends StorageExpressionEvaluationContext> ExpressionFunction<Void, C> setHomeDirectory() {
        return StorageExpressionFunctionSetHomeDirectory.instance();
    }

    /**
     * {@see StorageExpressionFunctionTreeMapStorage}
     */
    public static <C extends StorageExpressionEvaluationContext> ExpressionFunction<Storage<C>, C> treeMapStorage() {
        return StorageExpressionFunctionTreeMapStorage.instance();
    }

    /**
     * {@see StorageExpressionFunctionUnmount}
     */
    public static <C extends StorageExpressionEvaluationContext> ExpressionFunction<Void, C> unmountStorage() {
        return StorageExpressionFunctionUnmount.instance();
    }

    /**
     * {@see StorageExpressionFunctionWrite}
     */
    public static <C extends StorageExpressionEvaluationContext> ExpressionFunction<Void, C> writeStorage() {
        return StorageExpressionFunctionWrite.instance();
    }

    /**
     * {@see StorageExpressionFunctionWriteText}
     */
    public static <C extends StorageExpressionEvaluationContext> ExpressionFunction<Void, C> writeStorageText() {
        return StorageExpressionFunctionWriteText.instance();
    }

    /**
     * Stop creation
     */
    private StorageExpressionFunctions() {
        throw new UnsupportedOperationException();
    }
}
