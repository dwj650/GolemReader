---
id: DECISION-LOG
tier: ledger
status: active
updated: 2026-07-13
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

# Decision D88 — S5 test-asset handling
- Date: 2026-07-01  ·  Status: locked  ·  Maps to phase: P1
- Operator-delegated? no

## Context
S5 needs sherpa-onnx and two local neural voice packages to prove first audio on the S23,
but those binaries are large machine/device test assets rather than source code.
## Decision
S5 test-asset handling: local-only `.aar`, adb-push models, nothing large committed to git.
## Reasoning
The repository should record how to reproduce the S5 device proof without committing large
binary libraries or model files. The app code depends on the local `.aar`; the model folders
are pushed to the S23 for agent-run verification.
## Alternatives considered
Committing the `.aar` or voice model folders was rejected because these are large local test
assets. Downloading them during the build was rejected because S5 verification is explicit
local setup, not network-dependent build behavior.
## Consequences
`app/libs/*.aar` is gitignored, and S5 records the exact device-push commands used for
verification.

# Decision D89 — S5 voice boundary
- Date: 2026-07-01  ·  Status: locked  ·  Maps to phase: P1
- Operator-delegated? no

## Context
S5 must prove that the `VoiceEngine` abstraction can drive two real engines now, while full
F-048 voice selection and management features need later UI, persistence, and playback
infrastructure.
## Decision
S5 boundary: two-engine synthesis harness proven now; voice identity, hot-swap,
persistence, and the Voice Manager UI deferred to a later step.
## Reasoning
First audible output is the dependency needed before S6 streaming playback. Building voice
identity, hot-swap, persistence, or a manager UI now would expand beyond the walking
skeleton slice and create surfaces with no current host screen.
## Alternatives considered
Implementing full F-048 in S5 was rejected because identity, hot-swap, persistence, and UI
all depend on later playback and surface work. Proving only one engine was rejected because
it would not validate the shared interface boundary.
## Consequences
S5 claims only the two-engine synthesis substrate. Later F-048 work must add identity,
active-voice state, hot-swap, persistence, and the Voice Manager UI without redefining the
S5 interface foundation.

# Decision D90 — S6 scope boundary
- Date: 2026-07-01  ·  Status: locked  ·  Maps to phase: P1
- Operator-delegated? no — operator directly approved the proposed scope

## Context
S6 is specified on the phase-index ladder as F-001 only, but F-001 explicitly does not
own chapter-to-chapter continuity — that behavior is a separate thin feature, F-004,
which rides entirely on F-001's buffer. Separately, D87 deliberately limited S4 to a
single chapter, deferring multi-chapter text-ahead processing to S6; F-001's own delta
flags this as OB-D48, an explicit dependency that the producer's cross-chapter
pre-render is only valid because the text pipeline has the next chapter ready in time.

## Decision
S6 scope: build F-001's core producer/consumer/buffer/intent-loop spine, F-004's four
cross-chapter continuity rules, and the text-pipeline extension that runs chapter N+1
ahead of the playhead before the producer reaches the boundary (closing OB-D48).
Explicitly deferred: aggregation/batching + slice-verification (C-001-6, stays off per
OB-001-1's locked default), the final integrated battery/thermal budget thresholds
(T-001-C1's real numbers, which land at end of T3), and position persistence across app
kill (F-058).

## Reasoning
Building F-001 without F-004's rules would leave S6's own stated deliverable —
continuous chapter playback — unbuilt at the seam. Building F-001/F-004 without the
text-ahead wiring would pass isolated tests but glitch or stall the first time a real
multi-chapter book is tested end-to-end, since the cross-chapter pre-render's legality
depends on that wiring existing (D48). Deferred items each have a locked default or a
later, more appropriate convergence point already recorded elsewhere in the ledger.

## Alternatives considered
Scoping S6 to F-001 only (as phase-index literally lists) was rejected: it would produce
a step that "passes" in isolation but cannot demonstrate its own promise on a real book.
Building the aggregation/batching path now was rejected: the locked default is
one-call-per-segment until a build-phase ear A/B decides otherwise (OB-001-1); building
unused fallback machinery early adds scope with no current consumer.

## Consequences
S6's acceptance test is a real multi-chapter on-device listen-through, not a single-
chapter proof. The text pipeline (F-027/TextPipeline.kt) gains ahead-of-playhead
scheduling it didn't have after S4. Later steps (S7 highlight, S8 transport) build on a
genuinely continuous stream rather than a single-chapter stub.


# Decision D91 — S7 test-posture boundary
- Date: 2026-07-02  ·  Status: locked  ·  Maps to phase: P1
- Operator-delegated? no — operator directly approved the proposed scope

## Context
F-016 (R7) explicitly emits a highlight-position signal; it does not render pixels.
Rendering is F-014's job, and F-014 is scoped to S9, not S7. The spec's own on-device
result test (T-016-R1) is phrased as a visual check ("the highlight visibly tracks the
voice"), which has no surface to run against until F-014 exists.

## Decision
S7's on-device confirmation is **log-based**: the highlight signal's timestamped index
changes are recorded and cross-checked against the real audio's per-sentence sample
boundaries (the same data F-001 already exposes), rather than an eyes-on-screen check.
The full visual "watch it glow in sync" confirmation (T-016-R1 as literally written) is
deferred to S9, once Reading View exists to render onto.

## Reasoning
The rigor is unchanged — log-based comparison against real sample-length boundaries is
at least as precise as an eye check, arguably more so. Deferring only the *medium* of
confirmation (logs now, pixels later) avoids either building throwaway UI in S7 or
leaving F-016 unverified until S9.

## Alternatives considered
Building a minimal throw-away rendering harness in S7 just to satisfy T-016-R1 literally
was rejected: it would duplicate work S9 does properly with the real Reading View, for a
confirmation that log-based checking already provides with equal or better precision.

## Consequences
T-016-R1 stays open on paper until S9; S7's closeout will note it as "confirmed by proxy
(log-based), full visual confirmation carried to S9" rather than closed outright.

# Decision D92 — S8 scope boundary
- Date: 2026-07-02  ·  Status: locked  ·  Maps to phase: P1
- Operator-delegated? no — operator directly approved the proposed scope

## Context
"Transport controls" was originally one feature (F-002 v1.0.0). D62's ownership map
later split it into five: F-002 (the four commands), F-009 (the MediaSession hub),
F-012 (audio-focus/becoming-noisy policy), F-010 (background foreground-service
lifecycle), F-011 (lock-screen/notification surface), and F-013 (resume-after-kill).
F-002 is tagged build-tier T3 ("listen-loop spine," the tier S1-S7 have built);
F-009/F-010/F-011/F-012/F-013 are tagged T5 ("system integration") — a later,
distinct tier. Phase-index names only F-002 and "F-009 (thin)" for S8.

## Decision
S8 scope: F-002's four commands (play/pause/resume/stop, writing desired-play-state)
plus a thin F-009 — an in-app command hub and the orchestrator that keeps S6's
IntentLoop/PlaybackProducer/PlaybackConsumer/AbortController/StarvationState running
as a live, continuously-active session, rather than the manual/synchronous wiring S6's
own device test used. No real Android `MediaSessionService`, no lock screen, no audio-
focus handling, no background survival past the app being open, no resume-after-kill.
F-010, F-011, F-012, F-013, and full Media3 integration for F-009 are deferred to a
dedicated later step, after G4.

## Reasoning
F-002/F-009-thin is what phase-index already named for S8, and the T3/T5 build-tier
split is itself evidence the original design intended a walking-skeleton-appropriate
narrowing here, not accidental scope creep. Building the full T5 system-integration set
now would roughly triple S8's size for behaviors (lock screen, focus courtesy, kill-
survival) that matter for a polished app but not for proving the listen-loop spine
converges to real command input.

## Consequences
G4's "plays one book end-to-end" promise, once reached, will be demonstrated with the
app open and on-screen — not backgrounded, not through a screen lock, not surviving an
app kill, not pausing politely for a phone call. That is a genuine, deliberate
narrowing of what phase-acceptance proves, not a hidden gap. F-010/F-011/F-012/F-013
remain fully specified and ready to build as their own step once P1 closes.

# Decision D93 — S9 scope boundary
- Date: 2026-07-02  ·  Status: locked  ·  Maps to phase: P1
- Operator-delegated? no — operator directly approved the proposed scope

## Context
F-015's v1.0.1 delta (D51) specs Now Playing as a "hub" hosting two content slots
(F-073 embedded sync-preview, F-075 action-row) it does not own the content of — the
spec's own test (T-015-P3) already scopes slot *content* out. F-014's tap-to-inspect
host point (R5) invokes F-043, which has not been built in any step to date. Both
views are also part of a three-surface "gesture grammar" topology (D52, with a third
surface, Image Viewer/F-074, not in P1 scope at all).

## Decision
S9 scope: Reading View (display text + highlight rendering + scroll-follow) and Now
Playing (transport buttons + buffering indicator + position), both minimal per
phase-index's own wording. F-073/F-075 slots are reserved empty, no content built.
F-014's tap-to-inspect touch point is skipped entirely (not stubbed) since F-043
doesn't exist to invoke. Navigation between the two screens uses the simplest
mechanism that reaches both (in-app screen-switch state), not the full D52 gesture
grammar. Image Viewer (F-074) is out of scope entirely.

## Reasoning
Building F-073/F-075's actual content would mean building two more full features
inside a step named for two others. A tap-to-inspect host point with nothing behind it
is dead UI, not a real seam — worse than not building it, since it would look
functional and not be. Full gesture-grammar polish is real, separate design work; a
plain screen-switch reaches both surfaces with much less scope, matching "minimal" as
phase-index itself describes S9.

## Consequences
Now Playing's F-073/F-075 slots will visually be empty space until those features are
built in later steps. Reading View has no tap-to-inspect affordance yet — not a
regression, since it never existed before S9. Swipe gestures between screens are not
part of G4's phase-acceptance demonstration; a simpler switch is.

# Decision D94 — S9 Compose UI test scope expansion
- Date: 2026-07-02  ·  Status: locked  ·  Maps to phase: P1
- Operator-delegated? no — operator directly approved the scope expansion

## Context
S9's Step SOW required an on-device read-along proof that the highlighted Reading View
sentence is visibly on-screen and advances with the voice. The first S9 device proof
verified the live playback state feeding Reading View helpers, Now Playing controls, and
buffering state, but did not assert against rendered Compose UI. The project did not yet
include Compose UI test dependencies, and adding them required touching
`app/build.gradle.kts`, which was outside D93's original file scope.

## Decision
S9 scope expands to include `app/build.gradle.kts` for test-only Compose UI support:
`androidTestImplementation("androidx.compose.ui:ui-test-junit4")`, the existing Compose
BOM on androidTest, and `debugImplementation("androidx.compose.ui:ui-test-manifest")`.
No production dependency changes are allowed under this expansion.

## Reasoning
The visible-on-screen requirement is a real S9 acceptance criterion, not a later polish
item. A Compose semantics-tree assertion is the narrowest way to prove the actual rendered
Reading View highlights and advances on device without expanding product behavior or
introducing a production dependency.

## Consequences
S9's device test now uses Compose UI testing to assert that the `reading-highlight`
semantics node is displayed for the current Tom Sawyer sentence, then advances to a later
displayed sentence after the highlight emitter changes. The build gains test-only Compose
testing artifacts, but runtime app dependencies and app behavior remain unchanged.

# Decision D95 — G4 accepted with PR-7 gap recorded
- Date: 2026-07-02  ·  Status: locked  ·  Maps to phase: P1
- Operator-delegated? no — operator directly decided

## Context
G4 principle-conformance review found six of seven declared principles (PR-1 through
PR-6) in solid or deliberately-tracked partial standing. PR-7 (accessibility by
default — Methodology Core Principle 3.8, "included from the first iteration") has no
built coverage: F-066 through F-070 have been deferred across every step to date, with
no explicit prior decision scoping accessibility out of P1.

## Decision
G4 is accepted with the PR-7 gap explicitly recorded, not hidden. Accessibility
(F-066-070) is prioritized as early work in the next phase, rather than being treated
as later polish.

## Reasoning
The walking skeleton's purpose is proving the architecture, not shipping to real
users — V1 has not been released and G5 (version release) is the actual gate where
Core Principle 3.8 must hold without exception. Recording the gap honestly, rather
than silently passing it or blocking G4 over it, keeps the principle enforced (it's on
the record, with an owner and a "next" commitment) without halting a phase that has
otherwise met its acceptance bar.

## Consequences
The Phase P1 acceptance document records PR-7 as open, with accessibility named as
priority work for the phase that follows. Any future phase's G1 entry must show
accessibility work scoped in, or the gap becomes a repeated, unaddressed finding.
# Decision D96 — S10 inserted: app bootstrap, before G4 resumes
- Date: 2026-07-02  ·  Status: locked  ·  Maps to phase: P1
- Operator-delegated? no — operator directly approved

## Context
Codex halted on the G4 completion SOW: `MainActivity`/`GolemReaderApp` launch with
empty defaults (`sentences = emptyList()`, an unattached `TransportCommands`, a fresh
unwired `HighlightStateEmitter`). Every prior step (S6-S9) proved its piece correctly,
but always by wiring `PlaybackSession`/`TransportHub`/the two screens together inside
a test class. Nothing has ever connected the real app's launch path to a real book.
G4's SOW explicitly requires the demonstration to use the real app, not a test
harness, so this gap blocks G4 rather than being an oversight in G4's own scope.

## Decision
Insert **S10 — App bootstrap** before G4 resumes: on launch, load one hardcoded
fixture book through the existing pipeline (identity → text → a real
`PlaybackSession` wired to `TransportHub`), and pass live state into `GolemReaderApp`
instead of empty defaults. No book picker, no library browsing (F-019, separate,
later). The app does not auto-play on launch — a person or agent presses Play through
the real Now Playing controls, so S8's transport work is also demonstrated for the
first time outside a test.

## Reasoning
Codex correctly identified this as new production wiring and a real design decision
(where does the book come from, how does the app decide to load it) — outside G4's
declared demonstration/archive-only scope, and correctly halted rather than guessing.
The fix is small and self-contained: every piece it needs (the `asDriver()` adapters,
`TransportHub.attach()`, the two screens' existing constructor parameters) already
exists; the bootstrap only has to call them in the right order, once, in one place.

## Consequences
G4's completion SOW is unblocked once S10 lands — the same demonstration task can
proceed using the real app instead of a test harness. S10 becomes the tenth and final
build step before G4's on-device work resumes.

# Decision D97 — S11 inserted: end-of-book clean stop defect
- Date: 2026-07-02  ·  Status: locked  ·  Maps to phase: P1
- Operator-delegated? no — operator-approved via direct instruction to fix and proceed

## Context
G4's real, unattended end-to-end run (a short complete public-domain book, per the
G4 SOW's recommendation) reached the true final sentence and then repeated it
indefinitely instead of stopping. Diagnosis: `PlaybackSession.runOneIteration()`
treats a null result from `nextAfter(cursor)` as "no change" (stays on the same
cursor) rather than "this was the last sentence, stop." Because the render loop
re-invokes `producer.renderLookAheadFrom(renderCursor)` every tick regardless, and
that call re-enqueues the target sentence's audio every time it's called, a stuck
cursor produces an unbounded stream of duplicate final-sentence audio. F-004 R4
("end-of-book stops cleanly, no phantom render") was tested in S6 against
`ChapterContinuity.isEndOfBook()` directly, in isolation — but neither S8's
`PlaybackSession` nor S10's `BookBootstrap` ever wired that detector into the live
loop. `BookBootstrap` additionally builds its own ad-hoc next-sentence map via
`zipWithNext` rather than reusing `ChapterContinuity`.

## Decision
Insert **S11 — End-of-book clean stop** before G4 resumes again. Fix
`PlaybackSession` so reaching the true last sentence (no next sentence available)
transitions the session to a stopped state — no further render/enqueue calls, sink
flushed cleanly, loop thread exits. Reuse `ChapterContinuity` in `BookBootstrap`
rather than the ad-hoc `zipWithNext` map, so the same purpose-built detector S6 wrote
is what's actually driving the live app, not a duplicate implementation.

## Reasoning
This is a defect, not a scope question — the required behavior was already specified
(F-004 R4) and already had a tested detector; the gap is a wiring gap between two
steps built afterward. Fixing it in `PlaybackSession` fixes it for every caller
(bootstrap now, and any future caller), rather than patching around it only in
`BookBootstrap`.

## Consequences
G4's demonstration is blocked a second time, briefly, for a real fix rather than a
scope negotiation. Once S11 lands, G4 resumes with the same SOW, unchanged. The
`zipWithNext`-based `nextAfter` in `BookBootstrap` is replaced with
`ChapterContinuity`-backed logic as part of this fix, closing the duplicate-
implementation gap at the same time.



# Decision D98 — Visual prototype v0.2.0 is the frozen visual contract (ledger repair)
- Date: 2026-07-02  ·  Status: locked  ·  Maps to phase: P1 closeout / P2
- Operator-delegated? no — operator directly approved
- **Repair note:** commit 8f7d6f7 added `foundation/prototype/golem-reader-prototype-v0_2_0.jsx`
  and its commit message recorded D98, but this ledger entry never landed. Found and
  repaired at P2's G1 (2026-07-02). The decision itself was made and approved on
  2026-07-02, before that commit.

## Context
P1's retro (IMP-002) recorded that no visual-only G0 prototype ever existed, leaving
"build matches prototype" with no literal artifact to check at future gates. A
visual-only prototype (v0.2.0, dummy data, no backend) was iterated and approved,
correcting a real D51/D52 topology error found in v0.1.0 (Reading View wrongly had
its own nav tab) and tagging every not-yet-built control as roadmap.

## Decision
Prototype v0.2.0 (`foundation/prototype/golem-reader-prototype-v0_2_0.jsx`) is the
project's frozen visual contract. Future gates check built surfaces against it.
IMP-002 is closed.

## Reasoning
The Armature checklist's "build matches prototype" line needs a literal artifact;
v0.2.0 provides it while honestly distinguishing target vision (D51/D52 gesture
grammar) from the current build's simpler placeholder (D93).

## Alternatives considered
Continuing with the Design Spec as sole visual authority (the IMP-002 interim
posture) was superseded once a real prototype existed and was approved.

## Consequences
G4 gates from P2 onward archive screenshots against the prototype. The dark theme's
token values (S12 / F-065) derive from the prototype's palette.

# Decision D99 — Phase P2 opens with F-065 (design-token foundation)
- Date: 2026-07-02  ·  Status: locked  ·  Maps to phase: P2
- Operator-delegated? no — operator directly approved

## Context
D95/IMP-004 prioritize accessibility (F-066–F-070) for P2. Reading the actual specs:
F-066 (high contrast) is defined as a variant token set, F-068 (text scaling) as a
multiplier on typography tokens, and every toggle is hosted in F-064 — but the token
system (F-065) and settings host (F-064) do not exist, and the current UI uses stock
Material defaults (verified: zero hardcoded color/size literals, no token layer).

## Decision
P2 opens with F-065 (token system + light/dark themes + persisted choice), and the
accessibility set follows in dependency order: F-064 thin, F-066, F-068, F-067,
F-069.

## Reasoning
F-065 is itself the first accessibility work — its spec names it the foundation
F-066/F-067/F-068 extend, and it sits on Armature's standing accessibility set
(D31-methodology). Building any axis before the token floor would force rework.

## Alternatives considered
Starting directly with F-066 was rejected: a high-contrast token set cannot exist
before the token system it varies.

## Consequences
The P2 ladder is dependency-ordered (D100); themes are pure token value-sets, which
is also the F-060 (theme import) forward-compat seam.

# Decision D100 — Phase P2 shape: "Accessible Shell," six steps; F-070 explicitly deferred
- Date: 2026-07-02  ·  Status: locked  ·  Maps to phase: P2
- Operator-delegated? no — operator chose option A directly

## Context
F-070 (onboarding) depends on F-019 (library + empty state) and the F-048-family
voice-import flow — neither exists. Including it in P2 would roughly double the
phase (option B). IMP-004's lesson: accessibility deferral must be an explicit
recorded decision, never a silent step-by-step omission.

## Decision
P2 is a tight six-step phase, "Accessible Shell": S12 F-065 theme foundation,
S13 F-064 settings shell (thin) + theme picker, S14 F-066 high contrast,
S15 F-068 text scaling, S16 F-067 reduced motion, S17 F-069 keyboard navigation,
then G4. F-070 is deferred by this decision to the phase that builds library +
voice import, where it becomes priority work and D74 (first-run obeys the
accessibility contracts) takes effect.

## Reasoning
First-run guidance cannot be built before there is anything to guide through.
A small phase lands the accessibility payoff sooner; the deferral is on the
record with an owner, honoring IMP-004 rather than repeating PR-7's silent slide.

## Alternatives considered
Option B (P2 also builds F-019 + voice import so F-070 lands in-phase) was
rejected as roughly doubling the phase and delaying the accessibility payoff.

## Consequences
Step IDs are flat and continue from P1: P2 = S12–S17. F-070 remains fully
specified and queued. P2's G4 re-scores PR-7 against the four built axes.

# Decision D101 — No-hardcode guard is a script check, not a custom lint rule
- Date: 2026-07-02  ·  Status: locked  ·  Maps to phase: P2 (S12)
- Operator-delegated? no — operator approved via the S12 SOW, which named this decision

## Context
F-065 R6 requires a mechanical guard rejecting hardcoded color/size/duration
literals in UI code. Options: a custom Android Lint rule (separate tooling module
to build and maintain) or a script scan wired into the existing guard pipeline.

## Decision
Implement the guard as a script (`guards/no-hardcode-check.sh` or equivalent wired
into `guards/gate-check.sh`) that scans UI code for forbidden literal patterns and
fails the gate on a hit. The theme package is the one allowed home for literals.

## Reasoning
Equally mechanical at this project's size, runs in the guard pipeline that already
exists, and costs minutes instead of days. Mechanical-before-social is satisfied;
sophistication is not the requirement.

## Alternatives considered
A custom Lint rule was rejected for V1 as real scope for zero added safety here;
upgrading later is its own small step if the project outgrows the script.

## Consequences
S12's acceptance criteria require the guard to pass on the migrated codebase and
demonstrably fail on a seeded violation.

## Implementation note (2026-07-08)
S12 implemented D101 as `guards/no-hardcode-check.sh` and wired it into
`guards/gate-check.sh`. The script scans Kotlin UI source outside
`com.golemreader.theme` for raw Compose color, size, and duration literals. A temporary
seeded violation using `Color(0xFF123456)` outside the theme package failed the guard;
the clean migrated codebase passed.

# Decision D102 — Navigation topology: in-house route model, bottom nav, Library hidden until F-019
- Date: 2026-07-12  ·  Status: locked  ·  Maps to phase: P2 (S13)
- Operator-delegated? no — operator approved component A on 2026-07-12

## Context
S13 grounding (IMP-003) found the built app has no navigation at all: no
navigation dependency, and the two screens hang off a private enum + button
switch (the S9/D93 placeholder). The approved visual contract commits to a
bottom nav of Library / Now Playing / Settings, with Reading reached from Now
Playing (preview-strip tap or swipe-left per D51/D52), never as a nav
destination. F-019 (Library) is unbuilt, and both real Reading doors (swipe,
F-073 strip) are unbuilt features.

## Decision
S13 lands the app's real navigation: an in-house plain-Kotlin route model (no
navigation library) driving a bottom nav bar per the visual contract, with two
live tabs — Now Playing and Settings. The Library tab is not rendered until
F-019 exists (D68: absent, never a dead control). Reading stays reachable via a
simplified, tappable preview strip on Now Playing (real sentence text, sits
where F-073's full strip will live); the S9/D93 top switch is removed. Swipe
gestures remain D51/D52 target vision.

## Reasoning
Three destinations don't need a back-stack dependency; a plain route model is
JVM-testable on the only automated rig this repo has. Same
mechanical-before-social economy as D101. The simplified strip is the honest
successor to the D93 placeholder, positioned so F-073 replaces it in place.

## Alternatives considered
A third peer button (rejected: settings becomes co-equal with reading surfaces,
contradicts the contract). Adopting androidx.navigation (rejected for V1: real
scope for zero added safety at three destinations; adopting later is its own
small step). Rendering Library dimmed (rejected: exactly what D68 forbids).

## Consequences
S13's scope includes the nav topology, not just the settings screen. Route
model unit tests join the acceptance criteria. The Library tab's activation is
owned by the F-019 phase; the strip's enrichment is owned by F-073.

# Decision D103 — Prototype v0.3.0 is the frozen visual contract (supersedes v0.2.0)
- Date: 2026-07-12  ·  Status: locked  ·  Maps to phase: P2 (S13)
- Operator-delegated? no — operator approved the v0.3.0 draft on 2026-07-12

## Context
D98 froze prototype v0.2.0 as the visual contract. S13 needed a visible
proposal for the settings surface, which v0.2.0 lacked (its Settings tab was
roadmap-only with no screen behind it). OB-064-1 (section grouping/order) was
open for build.

## Decision
Prototype v0.3.0 (`foundation/prototype/golem-reader-prototype-v0_3_0.jsx`)
supersedes v0.2.0 as the frozen visual contract; v0.2.0 is retained for
rollback. v0.3.0 adds: a live Settings tab; the settings screen (title, grouped
sections, segmented System · Light · Dark theme picker rendered with the real
S12 palette values from GolemThemeTokens.kt); the preview-strip Reading entry
carried forward. This resolves OB-064-1: day-one Settings renders exactly one
section — Appearance. The dimmed "map preview" rows in the prototype are
illustration only and are never rendered by the shipping app (D68).

## Reasoning
The D98 principle stands — screenshots at G4 compare against the contract; the
contract artifact needed to grow to cover the surface S13 builds. Painting the
prototype with the real token values keeps contract and build from drifting.

## Alternatives considered
Keeping v0.2.0 and specifying the settings screen in prose only (rejected: the
operator approves what he can see; prose invited exactly the drift D98 exists
to prevent).

## Consequences
G4's visual comparison target is v0.3.0. The segmented restyle of
ThemeChoicePicker and the launcher icon join S13 scope as riders.

# Decision D104 — Settings Map is a plain-Kotlin code registry
- Date: 2026-07-12  ·  Status: locked  ·  Maps to phase: P2 (S13)
- Operator-delegated? no — operator approved component B on 2026-07-12

## Context
F-064 R3 requires the settings shell to read a declared map so future settings
register rather than modify the shell. OB-064-2 left the map's home open:
doc-of-record or code. Grounding found no Settings Map exists anywhere — the
spec's Appendix A is the open-boundary ledger.

## Decision
The Settings Map is a plain-Kotlin registry object in the app: each entry
declares label, group, owning feature, and how to tell the feature is built.
The shell renders exactly what the registry yields. This resolves OB-064-2.

## Reasoning
A code registry is the single source of truth (a doc mirror can silently
drift), and its yield/absence logic runs as plain JVM unit tests — the only
automated rig this repo has — keeping F-064's promised High-confidence tests
High. S14–S16 each become a one-entry registration.

## Alternatives considered
Markdown doc-of-record mirrored by hand in code (rejected: unenforced
agreement between doc and code is the D98-class gap S12 just spent effort
repairing).

## Consequences
S13 acceptance requires registry tests proving built entries render and
unbuilt entries are absent, plus a seam test that a new in-test entry surfaces
with zero shell changes.

# Decision D105 — High-contrast target is WCAG AAA (7:1 text; 3:1 large-text/UI)
- Date: 2026-07-13  ·  Status: locked  ·  Maps to phase: P2 (S14)
- Operator-delegated? no — operator approved component A on 2026-07-13

## Context
OB-066-1 left the HC palette's ratio target open: WCAG AA (4.5:1) vs AAA
(7:1). The base themes already meet AA, tested since S12.

## Decision
The high-contrast token sets target AAA: ≥ 7.0 for normal text pairs
(textPrimary and textSecondary vs background), ≥ 3.0 for large-text/UI pairs
(accent-as-control, highlight). Base themes keep their existing ≥ 4.5 floor.
Resolves OB-066-1.

## Reasoning
An HC mode that only meets the bar the base theme already meets does nothing;
AAA is the point of the switch. Cost is palette authoring, not architecture —
the central test enforces it either way.

## Alternatives considered
AA target (rejected: indistinguishable from base in guarantee terms).

## Consequences
The F-066 central harness (D69) asserts these numbers; S15's scaling
composition and G4's legibility sweep inherit them.

# Decision D106 — High contrast is two pure-data value-sets; the resolver gains an HC dimension
- Date: 2026-07-13  ·  Status: locked  ·  Maps to phase: P2 (S14)
- Operator-delegated? no — operator approved component B on 2026-07-13

## Context
F-066 R1 defines HC as a token variant of the F-065 palette. Two shapes were
possible: explicit HC value-sets, or a programmatic transform that derives HC
from any theme at runtime.

## Decision
Two explicit pure-data value-sets — hcDark and hcLight — join the existing
pair in GolemThemeValueSets. resolveThemeValueSet() gains a highContrast
parameter mapping (choice × systemDark × HC) to one of four sets. No color
math at runtime.

## Reasoning
Themes-as-pure-data is the F-065/F-060 seam S12 proved by test; a transform
reintroduces logic into what is deliberately data, and its output can't be
palette-authored to hit D105 precisely. Four value-sets is the honest size of
the space.

## Alternatives considered
Programmatic HC transform (rejected: violates the pure-data seam, makes the
AAA target emergent rather than authored).

## Consequences
The token completeness test extends to four sets; adding future themes means
authoring their HC counterpart too (recorded as the known cost of this shape).

# Decision D107 — HC preference is a second key-value row in theme_settings; no schema change
- Date: 2026-07-13  ·  Status: locked  ·  Maps to phase: P2 (S14)
- Operator-delegated? no — operator approved component C on 2026-07-13

## Context
S14 grounding (IMP-003) found S12 shaped theme_settings as a key-value table
(a `key` primary key, the theme stored under "theme_choice"). The expected
Room v4→v5 migration for a new preference is therefore unnecessary.

## Decision
The high-contrast on/off preference is stored as a second row
(key = "high_contrast") in the existing theme_settings table, reusing the
entity and DAO. No schema version bump; the D31 harness is not triggered. The
value column's `choice` name is a recorded wart; renaming it would force the
very migration this decision avoids, so it stays.

## Reasoning
Zero-migration is strictly less risk under the precious-data constraint (D31),
and the KV shape was built for exactly this. Writes follow the S13 rule:
suspend, IO dispatcher, proven by test.

## Alternatives considered
New column with v4→v5 migration (rejected: cost and risk for no functional
gain); separate entity/table (rejected: same, heavier).

## Consequences
Future small preferences may reuse the KV table the same way; anything
structurally richer than a keyed string still gets a real schema change under
D31.

# Decision D108 — Text-scale model: provider-level density composition; five steps; stepper control
- Date: 2026-07-13  ·  Status: locked  ·  Maps to phase: P2 (S15)
- Operator-delegated? no — Component A operator-approved individually 2026-07-13;
  control form (stepper) approved by confirmed informed sweep 2026-07-13

## Context
F-068 R1 requires text size = token size × OS font-scale × in-app control, with
no literal font sizes (D69). S15 grounding (IMP-003) found every text size
already sp and token-owned, and the D101 guard already forbidding size literals
outside the theme package — so the OS half and the guard pre-exist; only the
in-app multiplier and its control were open (OB-068-1).

## Decision
The in-app multiplier is applied once, in `GolemThemeProvider`, by overriding
`LocalDensity` with `Density(density, fontScale = systemFontScale × inAppScale)`.
Because the system fontScale already carries the OS setting, the multiplication
is the combined multiplier, and because all text is sp, every screen scales with
zero screen-code changes. The in-app setting is a five-step enum —
0.85 / 1.0 / 1.15 / 1.3 / 1.5 — default 1.0, unknown stored values resolving to
1.0. The control is a stepper (A− / current % / A+) with ends disabled,
registered under Settings → Accessibility via one D104 entry. Resolves OB-068-1.

## Reasoning
One provider-level seam mirrors the D106 value-set-swap pattern exactly: central,
tested, zero-touch for screens, and a screen doing its own scaling stays a halt
condition (D69). Discrete steps are individually testable and keep the first
non-toggle control simple; 1.5× in-app stacked on the S23's maximum OS scale
gives a worst case the reflow proof can actually pin down.

## Alternatives considered
Scaling the typography value-sets at resolve time (rejected: makes typography a
computed value instead of pure data, more surface for error, same result); a
free-form slider (rejected: untestable continuum, more UI complexity for no
accessibility gain).

## Consequences
Every current and future sp-based text surface inherits scaling automatically.
The provider seam is where any future scale-related axis composes. The reflow
proof, not the multiplier, carries the acceptance weight (D109).

# Decision D109 — Reflow proof surfaces: Settings, Now Playing, Reading View, bottom nav
- Date: 2026-07-13  ·  Status: locked  ·  Maps to phase: P2 (S15)
- Operator-delegated? no — approved by confirmed informed sweep 2026-07-13

## Context
F-068 R3/R5 make reflow-not-clip the real test, with surface priority left open
(OB-068-2). Grounding found zero truncation anywhere in the codebase, and two
named risks: the framework-fixed Material3 NavigationBar height (tab labels are
the most likely clip at max scale) and the two dp-fixed ReservedSlot boxes on
Now Playing (currently textless).

## Decision
The central reflow proof covers four surfaces: Settings, Now Playing, Reading
View, and the bottom navigation bar — JVM layout assertions at max in-app step ×
max-OS-scale density where assertable, plus archived device screenshots at true
maximum combined scale. Bottom nav labels get explicit photographed evidence; if
the framework clips them, that returns as a design question with the screenshot
in hand — never silently accepted or improvised around. The ReservedSlot
dp-height trap is recorded with an owner (the F-073 preview-strip family, which
first puts text there). T-066-B4's scaling half (HC × scaling composition,
deferred from S14) is folded into this same harness. Resolves OB-068-2.

## Reasoning
These four are every text surface that exists today; "each surface checked at
max scale" (R5) is currently satisfiable in full, so no priority ordering is
needed — the priority question dissolves. Naming the bottom nav risk before
Codex builds prevents the two failure modes IMP-003 exists to catch: silent
acceptance and unauthorized improvisation.

## Alternatives considered
Reading View only (rejected: R2/R5 say app-wide, every surface); deferring the
bottom nav check to G4 (rejected: it is the named highest-risk surface — evidence
belongs in the step that owns the axis).

## Consequences
Future screens must join the reflow harness when built (the deferred-to
contract, D69). S14's T-066-B4 deferral is cleared by this step. The G4 phase
gate re-walks the guided max-scale sweep with all axes on (T-068-R1).
