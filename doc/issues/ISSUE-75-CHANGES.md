# Fix: Strict YAML 1.2 Compliance for 0x7F (DEL) Character

## Problem

YAML 1.2 spec has context-dependent rules:

| Production | Range | Contexts |
|------------|-------|----------|
| `c-printable` | `0x20-0x7E` (excludes 0x7F) | Plain scalars, comments, block scalars, anchors |
| `nb-json` | `0x20-0x10FFFF` (includes 0x7F) | Double-quoted and single-quoted strings |

## Changes Made

### 1. `ScannerImpl.java`

Added 0x7F validation in three methods:

- **`scanComment()`** (line ~1177): Rejects DEL in comments
- **`scanPlain()`** (line ~1918): Rejects DEL in plain scalars
- **`scanBlockScalar()`** (line ~1556): Rejects DEL in literal/folded block scalars

Each validation throws a `ScannerException` with a descriptive message when 0x7F is encountered.

### 2. `EscapeCharInDoubleQuoteTest.java`

Added test cases covering:

- DEL allowed in double-quoted strings
- DEL allowed in single-quoted strings
- DEL rejected in plain scalars (value)
- DEL rejected in plain scalars (key)
- DEL rejected in comments
- DEL rejected in literal block scalars
- DEL rejected in folded block scalars

## Behavior Summary

| Context | 0x7F (DEL) | Per YAML 1.2 Spec |
|---------|-----------|-------------------|
| Double-quoted strings | ALLOWED | `nb-json` production |
| Single-quoted strings | ALLOWED | `nb-json` production |
| Plain scalars | REJECTED | `c-printable` production |
| Comments | REJECTED | `c-printable` production |
| Block scalars | REJECTED | `c-printable` production |

