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
