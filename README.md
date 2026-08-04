# ODCS Java SDK

Java SDK for the [Open Data Contract Standard (ODCS)](https://bitol-io.github.io/open-data-contract-standard/) — an open specification for machine-readable agreements between data producers and consumers.

This library helps you **parse**, **write**, **validate**, and **programmatically build** ODCS contracts (YAML or JSON) on the JVM.

- **Group ID:** `io.github.data-spec-labs`
- **Latest release:** `0.1.0` (Maven Central)
- **Java:** 17+
- **License:** [Apache License 2.0](LICENSE)

## Modules

| Artifact | Description |
|----------|-------------|
| [`odcs-core`](#installation) | Core library — models, parser, writer, JSON Schema validator, fluent builder |
| `odcs-java-sdk` | Parent / BOM-style aggregator (multi-module build) |

Most applications depend on **`odcs-core`** only.

## Installation

### Maven

```xml
<dependency>
  <groupId>io.github.data-spec-labs</groupId>
  <artifactId>odcs-core</artifactId>
  <version>0.1.0</version>
</dependency>
```

### Gradle

```kotlin
implementation("io.github.data-spec-labs:odcs-core:0.1.0")
```

## Features

| Capability | API | Notes |
|------------|-----|--------|
| **Typed models** | `model.v3.*` | Immutable Jackson-annotated records for the ODCS **v3.x** family |
| **Parse** | `ODCSParser` | YAML or JSON → `DataContract` |
| **Write** | `ODCSWriter` | `DataContract` → YAML / JSON (string or file) |
| **Validate** | `ODCSSpecValidator` | Raw document vs official Bitol JSON Schemas → `SpecValidationReport` |
| **Build** | `DataContractBuilder` | Fluent construction of contracts in Java |

## ODCS version support

| Concern | Support |
|---------|---------|
| Typed parse / write / builder | **v3.x** (`apiVersion` starting with `v3.`) — one model family covering v3.0.0–v3.1.0 |
| JSON Schema validation | **v3.1.0**, **v3.0.x**, and legacy **v2.2.x** (schema-only; no `model.v2` types yet) |
| Default for new contracts | `apiVersion: v3.1.0` |

`ODCSParser` rejects missing or non-v3 `apiVersion`. Spec validation can still check v2 documents against the bundled v2 JSON Schema without mapping them to Java domain types.

Official standard docs: [ODCS v3.1.0](https://bitol-io.github.io/open-data-contract-standard/v3.1.0/)

---

## Quick start

### 1. Parse a contract

```java
import io.github.dataspeclabs.odcs.core.ODCSParser;
import io.github.dataspeclabs.odcs.core.model.v3.DataContract;

DataContract contract = ODCSParser.parse("""
    apiVersion: v3.1.0
    kind: DataContract
    id: 53581432-6c55-4ba2-a65f-72344a91553a
    version: 1.0.0
    status: active
    name: seller_payments_v1
    """);

System.out.println(contract.id());       // 53581432-...
System.out.println(contract.apiVersion()); // v3.1.0
```

Also supported:

```java
ODCSParser.parse(path);                          // .yaml / .yml / .json by extension
ODCSParser.parse(inputStream, OdcsFormat.YAML);  // format required for streams
ODCSParser.parse(content, OdcsFormat.JSON);      // explicit format
```

### 2. Write a contract

```java
import io.github.dataspeclabs.odcs.core.ODCSWriter;
import io.github.dataspeclabs.odcs.core.OdcsFormat;

String yaml = ODCSWriter.toYaml(contract);
String json = ODCSWriter.toJson(contract);  // pretty-printed

ODCSWriter.write(contract, Path.of("payments.odcs.yaml"), OdcsFormat.YAML);
```

### 3. Validate against Bitol JSON Schema

Validates the **raw document** (not the typed model), so unknown fields and strict v3.1.0 rules are checked correctly.

```java
import io.github.dataspeclabs.odcs.core.ODCSSpecValidator;
import io.github.dataspeclabs.odcs.core.SpecValidationReport;

SpecValidationReport report = ODCSSpecValidator.validate(yaml);

if (!report.valid()) {
    report.errors().forEach(err ->
        System.out.println(err.path() + ": " + err.message() + " [" + err.keyword() + "]"));
}

System.out.println(report.resolvedVersion()); // e.g. v3.1.0
// report.versionWarning() is set when apiVersion was missing/unknown and a fallback schema was used
```

Bundled schemas (resolved from `apiVersion`):

| `apiVersion` | Schema |
|--------------|--------|
| `v3.1.0` | v3.1.0 (strict) |
| `v3.0.2`, `v3.0.1` | v3.0.2 |
| `v3.0.0` | v3.0.0 |
| `v2.2.2`, `v2.2.1`, `v2.2.0` | v2.2.2 |
| missing / unknown | falls back to **v3.1.0** + warning |

### 4. Build a contract in code (Fluent API)

```java
import io.github.dataspeclabs.odcs.core.builder.DataContractBuilder;
import io.github.dataspeclabs.odcs.core.model.v3.LogicalType;
import io.github.dataspeclabs.odcs.core.model.v3.ServerType;

DataContract contract = DataContractBuilder.create()
    .id("53581432-6c55-4ba2-a65f-72344a91553a")
    .version("1.0.0")
    .status("active")
    .name("seller_payments_v1")
    .domain("seller")
    .schemaObject(obj -> obj
        .name("payments")
        .physicalType("table")
        .property(p -> p
            .name("payment_id")
            .logicalType(LogicalType.STRING)
            .primaryKey(true)
            .required(true)))
    .server(s -> s
        .server("my-postgres")
        .type(ServerType.POSTGRES)
        .property("host", "localhost")
        .property("port", 5432))
    .team(t -> t
        .name("payments-team")
        .member(m -> m.username("alice").role("Owner")))
    .build();
```

Defaults on `create()`: `apiVersion = "v3.1.0"`, `kind = "DataContract"`.

`build()` requires non-blank `apiVersion`, `kind`, `id`, `version`, and `status`. For full Bitol schema checks, pass the written YAML/JSON through `ODCSSpecValidator`.

Edit an existing contract:

```java
DataContract updated = DataContractBuilder.from(contract)
    .status("deprecated")
    .build();
```