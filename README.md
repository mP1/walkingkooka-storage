[![Build Status](https://github.com/mP1/walkingkooka-storage/actions/workflows/build.yaml/badge.svg)](https://github.com/mP1/walkingkooka-storage/actions/workflows/build.yaml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/mP1/walkingkooka-storage/badge.svg?branch=master)](https://coveralls.io/repos/github/mP1/walkingkooka-storage?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/mP1/walkingkooka-storage.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-storage/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/mP1/walkingkooka-storage.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-storage/alerts/)
![](https://tokei.rs/b1/github/mP1/walkingkooka-storage)
[![J2CL compatible](https://img.shields.io/badge/J2CL-compatible-brightgreen.svg)](https://github.com/mP1/j2cl-central)

# walkingkooka-storage
A [Storage](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/Storage.java) is an object-oriented heirarchical view with concepts similar to a file system.

An example of its utlity is the usage within [walkingkooka-spreadsheet](https://github.com/mP1/walkingkooka-spreadsheet), to provide various spreadsheet values including a spreadsheet itself as files at various mounts.

- /spreadsheet/1
- /spreadsheet/2/cell/A1
- /spreadsheet/3/label/Label222

Using the above paths, one can interact or perform CRUD operations from a shell to
- spreadsheet
- the cell A1 or its contents
- the [label](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/reference/SpreadsheetLabelName.java) (named range)

# Converters

- [storage-path-json-to-class](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/convert/StorageConverterStoragePathJsonToClass.java)
- [storage-value-info-list-to-text](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/convert/StorageConverterStorageValueInfoListToText.java)
- [text-to-storage-path](https://github.com/mP1/walkingkooka-storage/blob/master/src/main/java/walkingkooka/storage/convert/StorageConverterTextToStoragePath.java)