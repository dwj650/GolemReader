---
id: DECISION-LOG
tier: ledger
status: active
updated: 2026-07-01
if-incomplete: "The locked register is foundation/design-charter.md; full text in foundation/design-spec.md."
---
# Decision log (all decisions, over time)

- **D1–D76 — LOCKED.** Canonical register: `foundation/design-charter.md`. Full clause
  text: `foundation/design-spec.md` (Parts I–II). Consolidated from Design Spec v0.5.0 +
  Grooming Decision Log v1.0.0/v1.1.0/v1.2.0; the **D30 reconciliation gate passed**.
- **Foundation gate G0 — prototype lite-waived (operator-delegated, 2026-06-28).**
  The G0 visual-prototype requirement was waived for Golem Reader because: (a) the design
  is exceptionally complete (76 locked decisions, 70+ requirements, gate passed);
  (b) the dominant risk is the audio listen-loop, already feasibility-tested on-device
  (T1–T5), not screen layout; (c) the walking skeleton is itself the minimal runnable
  proof a prototype would stand in for. Operator approved 2026-06-28.

> New build-time decisions append below as **D77+**, each via `templates/decision.md`.

# Decision D77 — Bootstrap stack
- Date: 2026-06-30  ·  Status: locked  ·  Maps to phase: P1
- Operator-delegated? yes — AI chose, reasoning below

## Context
S1 needs a minimal native Android project that proves the build, install, launch, JVM test,
and guard workflow before feature work begins.
## Decision
Use Jetpack Compose + Material3 for UI, package and namespace `com.golemreader`, application
ID `com.golemreader`, `minSdk = 29`, `compileSdk`/`targetSdk = 36`, Kotlin + Android Gradle
Plugin versions available from the installed stable Android Studio toolchain, and Gradle
Kotlin DSL.
## Reasoning
Compose is the current-standard Android UI toolkit and fits the synchronized-highlight
rendering planned later. `minSdk = 29` covers the S23 Ultra while retaining modern Android
media/service APIs. The package name is simple to fix during bootstrap and costly to change
later.
## Alternatives considered
XML views were rejected because the planned reading surface is custom and state-driven.
Raising `minSdk` was unnecessary for the target device. A different package namespace was
unnecessary churn.
## Consequences
Future P1 steps build on a standard Compose Android module with a stable application ID and
Gradle Kotlin DSL wiring.

# Decision D78 — Gate-check control-flow fix
- Date: 2026-06-30  ·  Status: locked  ·  Maps to phase: P1
- Operator-delegated? yes — AI accepted corrected guard behavior, reasoning below

## Context
S1 could not commit after successful build/test verification because `guards/gate-check.sh`
reported both PASS and FAIL rows for checks that had passed, and treated an intentionally
deferred secret scanner as a hard G3 failure.
## Decision
Use the corrected `guards/gate-check.sh` that makes `row()` return success, changes
PASS/FAIL checks to explicit if/else branches, and reports an unconfigured secret scan as
SKIP to match the pre-commit hook and guard README setup policy.
## Reasoning
The guard should block real failures, not control-flow artifacts. The corrected behavior
keeps the working-branch and docs-with-code checks enforceable while allowing S1 to proceed
with the secret scanner explicitly deferred by `guards.config`.
## Alternatives considered
Configuring a scanner during S1 was rejected because S1 only scoped the Android bootstrap
test/build guard values. Bypassing G3 was rejected because S1 requires a shown gate-check
PASS report before commit.
## Consequences
S1 can complete with G3 evidence. The upstream Armature template defect is recorded in
`ledgers/known-issues.md` for later template cleanup.

# Decision D79 — S2 test-runtime posture
- Date: 2026-06-30  ·  Status: locked  ·  Maps to phase: P1
- Operator-delegated? no

## Context
S2 needs storage invariants and Room migrations to sit in the commit gate where possible,
while device-only behaviors still need the S23.
## Decision
Run pure-logic tests as JVM unit tests under `./gradlew testDebugUnitTest`. Run Room
build/init and the migration harness via Robolectric in the JVM source set. Run restart,
process-death, and real Settings cache-clear checks on the S23.
## Reasoning
The highest-risk storage rules are deterministic and should be gate-enforced. Android OS
cache behavior and process lifetime need the real target device.
## Alternatives considered
Putting every Room test in `androidTest` was rejected because it would remove migration
coverage from the commit gate unless Robolectric proved incompatible.
## Consequences
S2 has fast JVM coverage for substrate invariants and separate recorded S23 verification
for OS/user behavior.

# Decision D80 — F-057 substrate table + demonstration migration
- Date: 2026-06-30  ·  Status: locked  ·  Maps to phase: P1
- Operator-delegated? no

## Context
F-057 must stand up the precious database before the first feature-owned precious table
arrives in S3.
## Decision
Create an F-057-owned `db_meta` table and a demonstration v1 to v2 migration on that
table. Commit exported Room schemas under `app/schemas/`.
## Reasoning
`db_meta` makes the database valid and openable now without stealing schema ownership from
later features. The demonstration migration proves the D31 harness before real precious
feature tables depend on it.
## Alternatives considered
Waiting until F-020 for the first migration was rejected because S2 explicitly owns the
storage substrate and D31 harness. Adding feature tables in S2 was rejected as out of scope.
## Consequences
Future precious tables inherit a working Room schema-export and migration-test pattern.

# Decision D81 — KSP under AGP built-in Kotlin
- Date: 2026-06-30  ·  Status: locked  ·  Maps to phase: P1
- Operator-delegated? no

## Context
The first S2 attempt used a Kotlin-matched KSP line that failed under AGP 9.2.1 built-in
Kotlin with a `kotlin.sourceSets` error.
## Decision
Use KSP 2.3.5 from the latest stable 2.3.x line, declared in the top-level build file, while
keeping Kotlin/KGP at 2.2.10. Do not use the temporary `android.disallowKotlinSourceSets=false`
fallback unless the latest stable 2.3.x still fails.
## Reasoning
KSP 2.3.x is decoupled from the Kotlin compiler version and supports the AGP built-in
Kotlin source registration path. KSP 2.3.5 resolved the toolchain conflict without a Kotlin
stack bump and without the deprecated Gradle property.
## Alternatives considered
Setting `android.disallowKotlinSourceSets=false` was retained only as a bounded fallback and
was not used. Bumping Kotlin/Compose to the 2.3 line was deferred as a separate stack
decision.
## Consequences
S2 can use Room/KSP on AGP 9.2.1 with Kotlin/KGP 2.2.10. No TD-001 was created because the
fallback was not used.

# Decision D82 — P1 ladder renumbering
- Date: 2026-06-30  ·  Status: locked  ·  Maps to phase: P1
- Operator-delegated? no

## Context
S2 delivered storage only, leaving book identity as the next dependency in the walking
skeleton. The phase ladder needed to reflect the actual build order without mixing identity
and thin text extraction in one step.
## Decision
Renumber the P1 ladder so S3 is F-020 Book identity, and S4 is Text → sentences
(F-018 thin + F-027 + F-028).
## Reasoning
Book identity is the stable key for later text, state, and catalog records. Keeping it as its
own step makes the first feature-owned precious table and D31 migration exercise small and
verifiable.
## Alternatives considered
Combining identity with text extraction was rejected because it would blur the D84 permanent
fingerprint recipe with the separate parser/text pipeline.
## Consequences
State and phase records name S3 as Book identity and S4 as Text → sentences.

# Decision D83 — S3 identity-only boundary
- Date: 2026-06-30  ·  Status: locked  ·  Maps to phase: P1
- Operator-delegated? no

## Context
F-020 needs EPUB structure to find spine documents, but F-018 owns content extraction and the
plain-text parser that will feed sentence segmentation.
## Decision
S3 performs structural EPUB reading only: container → OPF → manifest/spine → decompressed
spine entry streams. It does not parse chapter XHTML into text, create sentence records, or
link multiple source files to one book.
## Reasoning
The identity hash depends on raw decompressed spine bytes before parsing. Delaying text
extraction avoids coupling the permanent identity recipe to parser behavior.
## Alternatives considered
Parsing one chapter during S3 was rejected as premature scope overlap with S4.
## Consequences
S4 owns thin text extraction and sentence segmentation on top of the identity substrate.

# Decision D84 — Permanent book identity fingerprint recipe
- Date: 2026-06-30  ·  Status: locked  ·  Maps to phase: P1
- Operator-delegated? no

## Context
Golem Reader needs a permanent content fingerprint that recognizes the same EPUB across
renames and ZIP repackaging while not confusing distinct books.
## Decision
Compute identity from every OPF spine itemref in document order, including `linear="no"`.
For each referenced content-document entry, feed an 8-byte big-endian decompressed byte
length followed by that entry's decompressed bytes into SHA-256. Exclude container/package
metadata and all non-spine manifest items such as covers, images, CSS, and fonts. Store
records with `algorithm = "SHA-256"` and `recipe_version = 1`.
## Reasoning
Raw decompressed content bytes make ZIP packaging irrelevant while preserving content,
order, and document-boundary sensitivity. The length prefix avoids separator ambiguity.
Algorithm and recipe version make any future app-wide recipe change explicit.
## Alternatives considered
Hashing ZIP bytes was rejected because repackaging would change identity. Hashing rendered
or parsed text was rejected because parser behavior can change. Ignoring `linear="no"` was
rejected by D35 because those spine entries can carry book content.
## Consequences
The recipe is permanent for app identity v1. Any ambiguity in the recipe is a Design Zone
halt condition, not an implementation choice.

# Decision D85 — No machine-specific paths in committed config
- Date: 2026-07-01  ·  Status: locked  ·  Maps to phase: P1
- Operator-delegated? no

## Context
S2 was built on the T14; Android Studio wrote that machine's absolute Java home and Android
SDK dir into the committed gradle.properties. On p1 those paths do not exist, so Gradle
failed before compiling.
## Decision
The committed gradle.properties carries no machine-specific absolute paths. Each machine
supplies its Android SDK via local.properties (gitignored) and its Java home via user-level
~/.gradle/gradle.properties. Neither is committed.
## Reasoning
Absolute machine paths in shared config break every other machine; per-machine files are the
standard Gradle/Android mechanism and keep the repo portable.
## Alternatives considered
Committing per-machine profile pairs was rejected as fragile and non-standard.
## Consequences
The repo builds on p1, the T14, and future machines without editing shared config.

# Decision D86 — Build/test JDK is OpenJDK 21
- Date: 2026-07-01  ·  Status: locked  ·  Maps to phase: P1
- Operator-delegated? no

## Context
Robolectric sandboxing for Android SDK 36 requires Java 21. p1 had only Java 17 (starts
Gradle, cannot run the Room/migration tests). The T14 had been passing only via Android
Studio's bundled JBR 21 — an implicit dependency exposed once D85 removed it.
## Decision
The project's build/test JDK is OpenJDK 21, provisioned per machine and selected via the
per-machine ~/.gradle/gradle.properties java home. p1 installed openjdk-21-jdk.
## Reasoning
The unit/Robolectric suite must simulate the app's actual target (SDK 36), which mandates
Java 21. Making it explicit prevents silent reliance on a bundled JBR.
## Alternatives considered
Pinning tests to an older SDK (34) to run under Java 17 was rejected: lower test fidelity
than the app's real target.
## Consequences
Every build machine needs Java 21; the T14 needs its java-home pointed at a JDK 21 when next
used. No app runtime behavior changes.

# Decision D87 — S4 single-chapter text pipeline boundary
- Date: 2026-07-01  ·  Status: locked  ·  Maps to phase: P1
- Operator-delegated? yes — AI recorded the already-approved S4 boundary, reasoning below

## Context
S4 needs enough F-018/F-027/F-028 behavior to turn one real EPUB chapter into indexed
display/spoken sentence records, while later walking-skeleton steps still own playback,
highlighting, normalization rule content, and multi-chapter flow.
## Decision
Limit S4 to a single-chapter text pipeline: reuse `EpubStructuralReader` for spine order,
extract XHTML text into typed structural tokens, pre-clean, segment, fork display/spoken
renderings, assign the composite sentence index, and route the output as rebuildable cache
data. Explicitly defer image extraction under F-018 R10 until F-074 has a built surface.
## Reasoning
The walking skeleton needs a real sentence stream before synthesis, but does not yet need
continuous chapter traversal or image handling. Keeping image extraction out avoids building
a storage/UI surface before there is a consumer.
## Alternatives considered
Combining multi-chapter looping with S4 was rejected because S6 owns continuous playback.
Adding image extraction now was rejected because F-074 has no current presentation surface.
Adding F-029 normalization content or F-043 rule resolution was rejected because S4 only
needs observable empty rule slots.
## Consequences
S5 can consume the first synthesized sentence from a proven text pipeline. Later steps must
connect continuous playback, richer normalization/rules, and any image extraction without
changing the S4 parser boundary.
