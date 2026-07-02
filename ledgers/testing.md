---
id: TESTING
tier: ledger
status: active
updated: 2026-07-01
if-incomplete: "Coverage policy is reference/coverage-target.md."
---
# Testing register

## Foundation feasibility passes (carried from design — on-device, S23 Ultra)
- **T1** — sentence-callback model (Kokoro, clean prose): one callback per sentence. Medium.
- **T3** — post-render artifact + timing: DC negligible; joins silence-to-silence; Kokoro RTF ~0.9, Piper ~0.08. Medium.
- **T4** — short-utterance/dialogue + floor-batching: terminal punctuation is the trigger; slice mismatch confirms D12g. Medium.
- **T5** — terminal-cue verify: trailing-quote/repeated-terminal artifacts; `...`/`—` render well. Medium.

## Owed — build-phase (validate during P1+)
- Long-run register-drift listen (D26); batching keep/kill A/B (D12/OB-001-1); edge-trim floor tuning (D27); Kokoro sustained-load/thermal; abort cleanliness; skip-to-sound gap; audio-focus behaviors; background survival; Room round-trip; persisted-URI survival.

> Per-step tests (T-###-B#) are added to the step's req doc and recorded here as they're written.

## P1 step tests
- **T-S1-smoke** — JVM smoke test: `./gradlew testDebugUnitTest` passed on 2026-06-30.
  Confirms `AppInfo.name == "Golem Reader"`. Cost/budget: contributor-N/A.
- **S1 launch check** — `./gradlew assembleDebug` passed; debug APK installed with
  `adb install -r app/build/outputs/apk/debug/app-debug.apk`; launched with
  `adb shell am start -n com.golemreader/.MainActivity`; screenshot verified the placeholder
  screen reading "Golem Reader" on the attached device. Confidence: high.
- **T-057-P1** — JVM/Robolectric first-run substrate init: all three tiers initialize, are
  reachable, and report empty. `./gradlew testDebugUnitTest` passed on 2026-06-30.
  Confidence: high.
- **T-057-P2** — JVM placement-rule coverage for every D15b/c/d data type. Passed under
  `./gradlew testDebugUnitTest` on 2026-06-30. Confidence: high.
- **T-057-P3** — Room precious DB opens with F-057 substrate table `db_meta`; schema v1/v2
  exported under `app/schemas/`. Passed under `./gradlew testDebugUnitTest` on 2026-06-30.
  Confidence: high.
- **T-057-B1** — Cache-clear invariant: seeded precious `db_meta` row remains byte-identical
  after rebuildable clear. Passed under `./gradlew testDebugUnitTest` on 2026-06-30.
  Confidence: high.
- **T-057-B2 / T-057-B3** — Rebuildable eviction removes unprotected entries, never touches
  precious, and preserves in-use / near-playhead entries until unmarked. Passed under
  `./gradlew testDebugUnitTest` on 2026-06-30. Confidence: high.
- **T-057-B4** — Physical separation logic: precious DB and rebuildable cache resolve to
  non-nested locations. JVM path assertion passed; S23 paths were exercised by
  `./gradlew connectedDebugAndroidTest` on SM-S918U. Confidence: high for logic, medium for
  device.
- **T-057-B5** — D31 migration harness: v1 `db_meta` schema builds, v1→v2 migration preserves
  rows and validates schema, destructive variant fails validation. Passed under
  `./gradlew testDebugUnitTest` on 2026-06-30. Confidence: high.
- **T-057-R1** — S23 device check: precious `db_meta` row survives database close/reopen,
  standing in for restart/process-death substrate survival at S2 scope. Passed via
  `./gradlew connectedDebugAndroidTest` on SM-S918U on 2026-06-30. Confidence: medium.
- **T-057-R2** — S23 Settings cache-clear check: seeded precious row + rebuildable cache file;
  Android Settings > App info > Storage > Clear cache removed cache and the verification
  instrumentation confirmed the precious row survived. Passed on SM-S918U on 2026-06-30.
  Note: shell `pm/cmd package clear --cache-only` hung on this device, so the real Settings UI
  path was used. Confidence: medium.
- **T-057-C1 / T-057-C2** — Deferred owed measurements: precious write/read latency and
  precious footprint per book remain agent-run measurement items for a later build pass.
- **T-057-C3** — Registered contributor to the integrated listen-loop system-budget test at
  end of T3; no solo number for S2.
- **T-020-P1** — EPUB structural reader resolves `valid.epub` spine order exactly:
  chapter-1, chapter-2, chapter-3. Passed under `./gradlew testDebugUnitTest` on
  2026-07-01. Confidence: high.
- **T-020-P2** — D84 hasher returns fixed SHA-256 digest
  `a8e69c8b4b53fb33e34bc2ef16b950f27a1d51a225d2fec4b249f642391565ac` for the known
  `valid.epub` fixture. Passed under `./gradlew testDebugUnitTest` on 2026-07-01.
  Confidence: high.
- **T-020-P3** — `book_identity` builds at Room schema v3, is present in exported schema
  `app/schemas/com.golemreader.storage.PreciousDatabase/3.json`, and reads empty on first
  run. Passed under `./gradlew testDebugUnitTest` on 2026-07-01. Confidence: high.
- **T-020-B1 / B2 / B3** — Identity is deterministic, independent of ZIP compression, and
  unchanged when non-spine cover/CSS bytes change. Passed under `./gradlew testDebugUnitTest`
  on 2026-07-01. Confidence: high.
- **T-020-B4 / B5 / B6** — Identity changes when spine content changes, when spine order
  changes, and between genuinely distinct books. Passed under `./gradlew testDebugUnitTest`
  on 2026-07-01. Confidence: high.
- **T-020-B7** — Malformed EPUBs without `META-INF/container.xml` or with an empty spine
  throw typed `EpubStructuralException` errors and write no identity. Passed under
  `./gradlew testDebugUnitTest` on 2026-07-01. Confidence: high.
- **D35 coverage** — `linear="no"` spine itemrefs are included: `with-appendix.epub` resolves
  the appendix in order and hashes to
  `f2f29b161f698ebafaa28c889da1ba905f83b51c1d38cfebe68855424959377b`, differing from
  `valid.epub`. Passed under `./gradlew testDebugUnitTest` on 2026-07-01. Confidence: high.
- **T-020-B8** — D31 first feature-table exercise: v2 `db_meta` rows survive V2→V3,
  `book_identity` is created and usable, and a destructive V2→V3 variant is rejected by
  Room validation. Passed under `./gradlew testDebugUnitTest` on 2026-07-01. Confidence:
  high.
- **T-020-R1 / R2 / R3** — S23 device checks: registering the same EPUB twice returns the
  same hash and known status on the second add; a different EPUB creates a second identity;
  and a written identity reads back after database close/reopen. Passed via
  `./gradlew connectedDebugAndroidTest` on SM-S918U on 2026-07-01. Confidence: medium.
- **T-020-C1** — S23 import-time hash measurement: 3,147,653-byte EPUB hashed in 6 ms.
  Recorded from `BookIdentityDevice` log after `./gradlew connectedDebugAndroidTest` on
  SM-S918U on 2026-07-01. No threshold. Confidence: medium.
- **T-020-C2** — S23 streamed-hashing memory reading for the same 3,147,653-byte EPUB:
  before=5,886,576 bytes, after=5,804,656 bytes, delta=-81,920 bytes. No threshold.
  Confidence: medium.
- **T-020 contributor tag** — N/A for the integrated listen-loop budget: F-020 runs at
  import time, not on the steady playback path.
- **T-018-P1 / T-018-B1 / T-018-B2** — JVM extraction of
  `fixtures/text/tom-sawyer.epub` chapter 1 through `EpubStructuralReader`: ordered text
  comes out, markup is stripped, and chapter boundary / heading / paragraph tokens are
  preserved. Passed under `./gradlew testDebugUnitTest` on 2026-07-01. Confidence: high.
- **T-018-P3 / T-027-P4** — Pipeline cache placement resolves through the existing
  F-057 placement rule to `StorageTier.Rebuildable`, not precious. Passed under
  `./gradlew testDebugUnitTest` on 2026-07-01. Confidence: high.
- **T-018-B4 / T-018-B5 / T-018-B6** — Declared non-UTF-8 XHTML encoding is respected,
  Tom Sawyer chapter extraction is deterministic, and a malformed spine content-document is
  flagged without aborting a later good chapter. Passed under `./gradlew testDebugUnitTest`
  on 2026-07-01. Confidence: high.
- **T-027-P1 / T-027-P3 / T-027-B2 / T-027-B3 / T-027-B4 / T-027-B5 / T-027-B6 / T-027-B8**
  — Tom Sawyer chapter 1 runs through parse -> pre-clean -> segment -> display/spoken fork
  with shared `SentenceIndex` values, observable no-op rule slots, display quotes retained,
  spoken quotes removed, no phonemes, and F-029 absent. Passed under
  `./gradlew testDebugUnitTest` on 2026-07-01. Confidence: high.
- **T-027-B1** — Pre-clean performs NFC normalization, whitespace normalization,
  zero-width/directional control stripping, and smart quote / dash straightening. Passed
  under `./gradlew testDebugUnitTest` on 2026-07-01. Confidence: high.
- **T-028-P1 / T-028-B1 / T-028-B2 / T-028-B3 / T-028-B4 / T-028-B5 / T-028-B7 / T-028-B9**
  — BreakIterator segmentation plus thin corrections covers clean prose, `St.`
  abbreviation, decimals, ellipsis, byte-for-byte reassembly, over-long clause sub-splits,
  complete clause tags, determinism, and terminal/sub-split tags. Passed under
  `./gradlew testDebugUnitTest` on 2026-07-01. Confidence: high.
- **T-018-R1 / T-027-R1 / T-028-R1** — S23 device smoke: the same Tom Sawyer EPUB fixture
  was pushed to `/sdcard/Android/media/com.golemreader/fixtures/text/tom-sawyer.epub`;
  chapter 1 extracted, cleaned, segmented, and forked with no crash under
  `./gradlew connectedDebugAndroidTest` on SM-S918U on 2026-07-01. The known
  `ExternalCacheClearDeviceTest` service-UID gap skipped two S2 tests as logged in
  KI-S3-001; S4's device test passed. Confidence: medium.
- **T-028-C1 / S4 cost reading** — S23 Tom Sawyer chapter-1 text pipeline measurement:
  wall-clock 409 ms; memory before=2,778,288 bytes, after=2,921,648 bytes,
  delta=143,360 bytes. No threshold. Confidence: medium.
- **T-001-B1 / B2 / B3 / B6 / B8 / B11 / B12** — S6 JVM playback spine coverage:
  rendered-second buffer depth, RAM-only copy behavior, latest-value-wins intent debounce,
  pause/resume without target re-render, abort ordering, starvation hold with live intent,
  no in-place mutation of buffered audio, and end-of-book clean stop. Passed under
  `./gradlew testDebugUnitTest` on 2026-07-01. Confidence: high.
- **T-004-P1 / B1 / B2 / B4** — S6 JVM chapter-continuity coverage: chapter boundary is
  not an interrupt, composite sentence indexes advance from chapter 5 to chapter 6 without
  reset, and no phantom sentence is exposed after the final sentence. Passed under
  `./gradlew testDebugUnitTest` on 2026-07-01. Confidence: high.
- **OB-D48 closure evidence** — `TextPipeline.processChapterWithReadAhead()` processed
  Tom Sawyer chapter 6 while chapter 5 was the active chapter and recorded
  `processed chapter 6 ahead of boundary 5`; covered by `TextPipelineReadAheadTest` under
  `./gradlew testDebugUnitTest` on 2026-07-01. Confidence: high.
- **T-001-B10 / T-004-B3 / T-004-R1** — S23 device streaming check: the Tom Sawyer EPUB
  fixture was restored to `/sdcard/Android/media/com.golemreader/fixtures/text/tom-sawyer.epub`;
  `StreamingPlaybackDeviceTest` streamed 371 chapter-5/6 sentences start-to-finish through
  the S6 producer/buffer/consumer path with no starvation. Passed via
  `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.golemreader.playback.StreamingPlaybackDeviceTest`
  on SM-S918U on 2026-07-01. Device audio used a short generated `VoiceEngine` tone rather
  than Kokoro/Piper to keep the agent-run boundary test bounded; S5 separately proved both
  real engines synthesize and play one sentence. Confidence: medium.
- **S6 verify note** — The first rerun of `StreamingPlaybackDeviceTest` failed because the
  Tom Sawyer fixture was absent from the S23 media path after install/test churn. Restoring
  the fixture with `adb push app/src/test/resources/fixtures/text/tom-sawyer.epub
  /sdcard/Android/media/com.golemreader/fixtures/text/tom-sawyer.epub` resolved the
  environment issue; the same device test then passed. The known `androidx.test.services`
  no-UID warning from KI-S3-001 still appears before device tests and did not block S6.
- **T-016-P1 / B1 / B2 / B3 / B4 / B5 / B7** — S7 JVM highlight signal coverage:
  `HighlightClock` derives duration from `samples.size / sampleRateHz`, the mapper resolves
  clause tags from shared `SentenceRecord` data, the emitter exposes readable current state
  with size/contrast/fade parameters, `PlaybackConsumer` reports segment start before sink
  playback, and `AbortController.changeTarget()` notifies highlight sync without changing
  the existing abort order. RED failed first on missing S7 API/hooks; GREEN passed under
  `./gradlew testDebugUnitTest` on 2026-07-02. Confidence: high.
- **T-016-B6 / R1 proxy per D91** — S23 real-engine highlight timing check:
  `HighlightSyncDeviceTest` played a Tom Sawyer cross-chapter passage through Kokoro and
  Piper using the S6 producer/buffer/consumer path. Both engines emitted the same index track
  (`5:217 -> 6:0`). Boundary tolerance was recorded at 250 ms; latest measured
  second-boundary drift was 209 ms for Kokoro and 77 ms for Piper. Passed via
  `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.golemreader.highlight.HighlightSyncDeviceTest`
  on SM-S918U on 2026-07-02 after restoring the fixture and test voices to
  `/sdcard/Android/media/com.golemreader/`. A fresh rerun first failed because APK install
  churn cleared the fixture from the app media folder; restoring the same test assets
  resolved it with no code change. Confidence: medium.
- **T-002-P1 / T-002-B1 / T-002-B2 / T-002-B3 / T-009-P1 / T-009-B1** — S8 JVM
  transport/session coverage: `TransportCommands` route play/pause/resume/stop through
  the single in-app `TransportHub`; two callers using the same hub produce identical
  desired-state writes; `PlaybackSession` drives producer/consumer while playing, pauses
  without flushing or aborting, resumes at the same cursor, aborts to a seek target, and
  stops by stopping the producer, flushing the buffer, flushing the sink, and ending the
  session. RED first failed on missing S8 APIs and then on missing stop-buffer flushing;
  GREEN passed under `./gradlew testDebugUnitTest` on 2026-07-02. Confidence: high.
- **S8 orchestrator device proof** — S23 wall-clock transport check:
  `PlaybackSessionDeviceTest` installed the app/test APKs, restored
  `tom-sawyer.epub` to `/sdcard/Android/media/com.golemreader/fixtures/text/`, started a
  real `PlaybackSession` thread over a Tom Sawyer chapter-5/6 passage, and issued
  play/pause/resume/seek/stop over elapsed time through the hub/commands. Direct
  instrumentation returned `OK (1 test)` via
  `adb shell am instrument -w -e class com.golemreader.playback.PlaybackSessionDeviceTest com.golemreader.test/androidx.test.runner.AndroidJUnitRunner`
  on SM-S918U on 2026-07-02. A direct `connectedDebugAndroidTest` run first failed after
  APK install churn cleared the fixture from app media; restoring the fixture after install
  resolved the environment issue with no code change. Confidence: medium.
- **T-014-P1 / T-014-B1 / T-014-B2 / T-015-P1/P3 / S9 buffering** — JVM Reading View
  and Now Playing coverage: display rows use `SentenceRecord.display` with quotes and
  punctuation intact, highlight rows map from the shared `SentenceIndex` exposed by
  `HighlightStateEmitter`, Now Playing transport controls forward play/pause/resume/stop
  through `TransportCommands`, and the buffering indicator exposes a visible
  `Catching up...` text state only while `StarvationState.isBuffering` is true. RED first
  failed on missing S9 UI APIs; GREEN passed under `./gradlew testDebugUnitTest` on
  2026-07-02. Confidence: high.
- **T-014-B3 / T-015-B / S9 device proof** — S23 read-along, rendered Reading View, and transport check:
  `ReadingAndNowPlayingDeviceTest` installed app/test APKs, restored `tom-sawyer.epub` to
  `/sdcard/Android/media/com.golemreader/fixtures/text/`, started a real `PlaybackSession`
  over a Tom Sawyer passage, emitted highlight state from actual segment starts, verified
  the rendered Compose semantics tree displays a `reading-highlight` node for the current
  Tom Sawyer sentence and then a later highlighted sentence after emitter advance, drove
  pause/resume/stop through `NowPlayingTransportControls` -> `TransportCommands`, and
  verified the buffering text appears and clears. D94 added only test dependencies:
  `androidx.compose.ui:ui-test-junit4` for androidTest and `ui-test-manifest` for debug.
  Direct instrumentation returned `OK (1 test)` via
  `adb shell am instrument -w -e class com.golemreader.ui.ReadingAndNowPlayingDeviceTest com.golemreader.test/androidx.test.runner.AndroidJUnitRunner`
  on SM-S918U on 2026-07-02. Confidence: medium.
- **T-S10 bootstrap wiring** — JVM app-bootstrap coverage:
  `BookBootstrapTest` runs the real Tom Sawyer fixture through identity + text pipeline,
  creates a real session attached to `TransportHub`, confirms no autoplay before Play,
  then presses Play through `NowPlayingTransportControls` and observes the shared
  `HighlightStateEmitter` advance from the same playback path. RED first failed on
  missing `BookBootstrap`; GREEN passed under `./gradlew testDebugUnitTest` on
  2026-07-02. Confidence: high.
- **T-S10 launch device proof** — S23 real app launch + real Play path:
  `BootstrapLaunchDeviceTest` launches `MainActivity`, confirms the Reading View renders
  real Tom Sawyer rows immediately, presses Play through the real Now Playing controls,
  returns to Reading, and observes a visible highlight emitted by the real session. The
  bootstrap uses the S5 Piper adapter; Piper assets were restored to
  `/sdcard/Android/media/com.golemreader/test-voices/piper/`. `./gradlew
  assembleDebugAndroidTest` and `./gradlew installDebug installDebugAndroidTest` passed;
  direct instrumentation returned `OK (1 test)` via
  `adb shell am instrument -w -e class com.golemreader.BootstrapLaunchDeviceTest com.golemreader.test/androidx.test.runner.AndroidJUnitRunner`
  on SM-S918U on 2026-07-02. Confidence: medium.
- **T-S11 end-of-book clean stop** — JVM regression coverage:
  `PlaybackSessionTest` drives a two-sentence session through the true final sentence and
  confirms the final sentence is rendered once, played once, and followed by the same
  producer/buffer/sink teardown used by stop; a later tick performs no extra render or
  playback. `BookBootstrapTest` confirms live next-sentence wiring now names
  `ChapterContinuity` and the old `zipWithNext` helper is absent. RED first failed on
  missing `PlaybackSession.isEndOfBook`; GREEN passed under `./gradlew testDebugUnitTest`
  on 2026-07-02. Confidence: high.
- **G4 phase acceptance device proof** — S23 real end-to-end app demonstration:
  the debug APK from commit `5517726` was installed on SM-S918U, launched through
  `MainActivity`, and played the staged complete public-domain short book content
  "The Gift of the Magi" through the real `BookBootstrap` / `PlaybackSession` /
  Reading View / Now Playing path. After pressing Play at 2026-07-02T00:56:57-07:00,
  playback was unattended to natural completion. Completion was first observed at
  2026-07-02T01:07:28-07:00 when the app process remained alive and the
  `GolemPlaybackSession` thread was absent; it remained absent at 01:08:46 after a
  post-completion wait. Required screenshots and run metadata are archived in
  `archive/V0-P1/`. No end-of-book repeat, uncaught starvation/stall, or fatal app log
  was observed. Confidence: medium.

## Resolved tool versions for S2
- KSP Gradle plugin: `com.google.devtools.ksp:2.3.5` (**D81 primary path**, no
  `gradle.properties` fallback, no TD-001).
- AndroidX Room Gradle plugin and libraries: `2.8.4`.
- Robolectric: `4.16`.
- AndroidX Test: `core-ktx 1.7.0`, `ext:junit-ktx 1.3.0`, `runner 1.7.0`.
