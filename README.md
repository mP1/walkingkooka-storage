[![Build Status](https://github.com/mP1/walkingkooka-storage/actions/workflows/build.yaml/badge.svg)](https://github.com/mP1/walkingkooka-storage/actions/workflows/build.yaml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/mP1/walkingkooka-storage/badge.svg)](https://coveralls.io/github/mP1/walkingkooka-storage)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/mP1/walkingkooka-storage.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-storage/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/mP1/walkingkooka-storage.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-storage/alerts/)
![](https://tokei.rs/b1/github/mP1/walkingkooka-storage)
[![J2CL compatible](https://img.shields.io/badge/J2CL-compatible-brightgreen.svg)](https://github.com/mP1/j2cl-central)

# walkingkooka-storage
A [Storage](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/Storage.java) is an object-oriented hierarchical view with concepts similar to a file system.

An example of its utlity is the usage within [walkingkooka-spreadsheet](https://github.com/mP1/walkingkooka-spreadsheet), to provide various spreadsheet values including a spreadsheet itself as files at various mounts.

- /spreadsheet/1
- /spreadsheet/2/cell/A1.json
- /spreadsheet/3/label/Label222.json

Using the above paths, one can interact or perform CRUD operations from a shell to
- spreadsheet
- the cell A1 or its contents, the file extension *.json requests the value as JSON.
- the [label](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/reference/SpreadsheetLabelName.java) (named range)

# [Converters](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/convert/StorageConverters.java)

- [storage-binary-to-storage-value-csv](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/convert/StorageConverterStorageBinaryToStorageValueSharedCsv.java)
- [storage-binary-to-storage-value-expression](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/convert/StorageConverterStorageBinaryToStorageValueSharedExpression.java)
- [storage-binary-to-storage-value-json](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/convert/StorageConverterStorageBinaryToStorageValueSharedJson.java)
- [storage-binary-to-storage-value-properties](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/convert/StorageConverterStorageBinaryToStorageValueSharedProperties.java)
- [storage-binary-to-storage-value-tsv](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/convert/StorageConverterStorageBinaryToStorageValueSharedTsv.java)
- [storage-binary-to-storage-value-txt](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/convert/StorageConverterStorageBinaryToStorageValueTxt.java)
- [storage-value-info-list-to-text](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/convert/StorageConverterStorageValueInfoListToText.java)
- [storage-value-to-storage-binary-binary](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/convert/StorageConverterStorageValueToStorageBinaryBinary.java)
- [storage-value-to-storage-binary-csv](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/convert/StorageConverterStorageValueToStorageBinarySharedCsv.java)
- [storage-value-to-storage-binary-expression](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/convert/StorageConverterStorageValueToStorageBinarySharedExpression.java)
- [storage-value-to-storage-binary-json](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/convert/StorageConverterStorageValueToStorageBinarySharedJson.java)
- [storage-value-to-storage-binary-properties](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/convert/StorageConverterStorageValueToStorageBinarySharedProperties.java)
- [storage-value-to-storage-binary-tsv](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/convert/StorageConverterStorageValueToStorageBinarySharedTsv.java)
- [storage-value-to-storage-binary-txt](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/convert/StorageConverterStorageValueToStorageBinarySharedTxt.java)
- [text-to-storage-path](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/convert/StorageConverterTextToStoragePath.java)

# [Storages](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/Storages.java)

- [empty](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/StorageSharedEmpty.java)
- [native](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/StorageSharedNativeFile.java)
- [prefixed](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/StorageSharedPrefixed.java)
- [readOnly](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/ReadOnlyStorage.java)
- [treeMapStore](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/StorageSharedTreeMapStore.java)