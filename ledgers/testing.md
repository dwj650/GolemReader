---
id: TESTING
tier: ledger
status: active
updated: 2026-07-13
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

## P2 step tests
- **T-064-P2 / T-064-B1 / T-064-B2 / F-064 R3** — `SettingsMapTest` verifies the
  production menu yields only built Theme, genuinely unbuilt F-005/F-006/F-007 parents
  are absent rather than disabled, and a fake registered entry groups correctly without
  shell changes. RED failed on the missing registry; GREEN passed in the targeted and
  full JVM suites on 2026-07-13. Confidence: high.
- **S13 route topology** — `GolemNavigationStateTest` verifies the bottom bar exposes
  exactly Now Playing and Settings, Library is not a destination, selecting a tab closes
  Reading, and explicit close/system back return Reading to Now Playing. RED failed on
  the missing route API; GREEN passed on 2026-07-13. Confidence: high.
- **S13 preview strip** — `NowPlayingScreenTest` verifies the simplified strip resolves
  the display text for the current shared highlight index. The device navigation test
  exercised preview tap → Reading → explicit Back on SM-S918U. Confidence: high for
  model logic, medium for the device path.
- **S13 off-main theme write** — `ThemeSettingsRepositoryTest` injects a dedicated
  dispatcher and proves `setChoice()` executes the DAO write on that thread rather than
  its caller; persistence coverage remains green. Confidence: high.
- **T-064-B3 / S13 builds** — `./gradlew testDebugUnitTest`, `./gradlew assembleDebug`,
  `./gradlew assembleDebugAndroidTest`, `bash guards/no-hardcode-check.sh`, and
  `git diff --check` passed during implementation on 2026-07-13. Confidence: high;
  a fresh final gate run remains due after device closeout and record reconciliation.
- **T-064-R1** — SM-S918U installed the final rebuilt app/test APKs. The navigation flow
  and live Dark selection methods each passed independently (`OK (1 test)`), verifying
  exactly two live bottom tabs, Settings with exactly Appearance → Theme, absent Speed,
  preview → Reading → Back, and selected-state change without crash/ANR. After
  force-stop/relaunch, Android reported Dark `checked="true"`. The final headphone/gear
  bottom-nav icons and adaptive launcher icon rendered. Settings Dark/Light, Now Playing,
  and launcher screenshots are archived in `archive/S13-settings-host/`. The operator
  reviewed and approved the four-image evidence set on 2026-07-13. Confidence: medium.

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
- **T-065-P1 / T-065-B4 / R7** — S12 JVM token coverage:
  `GolemThemeTokensTest` verifies both shipped themes resolve color, typography, shape,
  elevation, spacing, and motion tokens; the dark theme carries the D98 palette anchors
  (`#0D0F0C`, `#181C15`, `#D8DDCC`, `#A8CC3A`, `#E8A040`); primary text contrast is
  at least 4.5:1 in both themes; follow-system resolves from the OS dark flag; and a
  third in-test value-set can be added without changing screen code. RED first failed
  on the missing theme API; GREEN passed under `./gradlew testDebugUnitTest` on
  2026-07-08. Confidence: high.
- **T-065-P2 / D31** — S12 precious theme setting coverage:
  `ThemeSettingsRepositoryTest` verifies missing settings default to follow-system and
  updates persist through the DAO. `PreciousMigrationTest` now covers v3→v4 by building
  the v3 schema, seeding `db_meta` and `book_identity`, migrating to v4, validating the
  exported Room schema, proving existing rows survive, and proving the default
  `theme_settings(theme_choice, follow_system)` row exists. Passed under
  `./gradlew testDebugUnitTest` on 2026-07-08. Confidence: high.
- **T-065-B3 / D101** — S12 no-hardcode guard coverage:
  `NoHardcodeGuardTest` runs `guards/no-hardcode-check.sh` from the repo root and
  verifies the migrated codebase passes. It then writes a temporary UI violation using
  `Color(0xFF123456)` outside `com.golemreader.theme` and verifies the guard fails.
  The same seeded failure was demonstrated manually on 2026-07-08: the guard reported
  `SeededHardcodeViolation.kt` and exited nonzero; after removing the seed, the guard
  passed cleanly. Confidence: high.
- **T-065 picker control** — S12 picker option coverage:
  `ThemeChoicePickerTest` verifies the unmounted picker control exposes exactly
  follow-system, light, and dark options. No settings screen/home was added, matching
  the S12 non-goal. Passed under `./gradlew testDebugUnitTest` on 2026-07-08.
  Confidence: high.
- **S12 build check** — `ANDROID_HOME=/home/davidt14/Android/Sdk ./gradlew
  assembleDebug` passed on 2026-07-08. Confidence: high.
- **S12 device tier deferred** — Agent-run device verification for live switching,
  restart survival, system bars, and screenshots of both themes on Reading and Now
  Playing could not run on 2026-07-08 because `adb devices` listed no attached or
  authorized devices. Confidence: not executed; blocker is environmental.
- **S12 device tier follow-up** — SM-S918U verification completed on 2026-07-08 after
  the device became available. `adb install -r app/build/outputs/apk/debug/app-debug.apk`
  succeeded. Initial launch exposed a real S12 regression: `MainActivity` called
  `ThemeSettingsRepository.currentChoice()` from Compose startup, and Room crashed on
  the main thread. `MainActivityThemeStartupTest` now guards against that synchronous
  startup read, RED failed first, and GREEN passed after switching the initial Compose
  theme state to `ThemeChoice.FollowSystem` while collecting the Room `Flow`. Follow-system
  live switching was verified with `adb shell cmd uimode night no`; light Reading and
  Now Playing screenshots were captured, then OS night mode was restored with
  `adb shell cmd uimode night yes`. `ThemeSettingsDeviceTest` wrote persisted
  `ThemeChoice.Dark` through the real precious database via direct instrumentation
  (`OK (1 test)`), then a force-stop/relaunch rendered dark while OS mode had been
  light. Final log showed `Displayed com.golemreader/.MainActivity`. Screenshots and
  run notes are archived in `archive/S12-theme-foundation/`. Android test/install churn
  cleared media assets once; restoring `tom-sawyer.epub` and Piper assets resolved that
  environmental issue. Confidence: medium.
- **T-066-P1 / T-066-B1 / T-066-B2 / T-066-B4** — S14 central JVM contract:
  `GolemThemeTokensTest` verifies all four value-sets are complete; all theme choice ×
  system state × HC resolver combinations; HC primary/secondary text ≥ 7:1; HC control
  and highlight pairs ≥ 3:1; the unchanged base-theme floor; and toggle-on/toggle-off
  restoration through the shared resolver. A deliberately weak palette is rejected by
  the same helper as negative proof. RED failed on missing S14 APIs; GREEN passed under
  `./gradlew testDebugUnitTest` on 2026-07-13. Confidence: high.
- **T-066 preference / registry / control** — `ThemeSettingsRepositoryTest` verifies HC
  defaults off, persists independently as the `high_contrast` row, and dispatches its
  suspend write off the calling thread. `SettingsMapTest` verifies the built F-066 entry
  appears under Accessibility while unbuilt entries remain absent. Device inspection
  exposed an unlabeled-switch NAF marker; `HighContrastToggleTest` failed RED, then
  passed GREEN after the switch gained `content-desc="High contrast"`. Confidence: high.
- **T-066-B3 / S14 build checks** — the full JVM suite, `assembleDebug`, D101
  no-hardcode guard, and `git diff --check` passed on 2026-07-13. Confidence: high.
- **T-066-C1 / S14 device proof** — on SM-S918U, HC Dark and HC Light rendered live on
  Settings and Reading; Light + HC survived force-stop/cold relaunch; and toggling HC
  off/on during playback preserved the `GolemPlaybackSession` thread with no entries in
  a freshly cleared fatal log. Four screenshots are archived under
  `archive/S14-high-contrast/`. Objective checks: medium confidence. Guided legibility
  look-check: operator approved all four captures on 2026-07-13. Confidence: medium.

- **T-068-P1 / T-068-B4** — `TextScaleStepTest` verifies all five stored values and
  multipliers, unknown/default resolution, step boundaries, OS × in-app multiplication,
  and a 2.0 × 1.5 combined scale of 3.0. `TextScalingLayoutTest` captures the same 3.0
  density at the provider seam. Confidence: high.
- **T-068-B1 / T-068-B2 / T-066-B4 scaling half** — `TextScalingLayoutTest` composes
  Settings, Now Playing, Reading, and bottom navigation at maximum combined scale and
  asserts their text nodes remain laid out/displayed. It compares density-resolved text
  pixels for every surface role at normal/raised in-app scale, separately raises OS scale
  with the app step fixed, and proves hcDark tokens coexist with font scale 3.0.
  Robolectric 4.16 does not change final glyph bounds when `fontScale` changes, so the
  SM-S918U screenshots close that layout-engine gap. Confidence: medium automated/device.
- **T-068 preference / registry / control** — `ThemeSettingsRepositoryTest` proves the
  `text_scale` row defaults independently, persists, and its suspend write runs on the
  supplied IO dispatcher off the calling thread. `SettingsMapTest` proves Accessibility
  lists High contrast then Text size and still omits unbuilt entries. Stepper and startup
  tests cover labels, end disabling, routing, flow collection, and no synchronous read.
  Confidence: high.
- **T-068-B3 / D101** — `NoHardcodeGuardTest` is the negative proof: its temporary
  UI `18.sp` font-size seed outside the theme package makes the real guard fail; the
  clean guard passes. Confidence: high.
- **T-068-C1 / S15 device proof** — SM-S918U at Android font scale 2.0 × app 1.5 showed
  live app-wide growth, persistence through force-stop, Light/Dark/HC composition, no
  clipping on all four D109 surfaces, and no playback interruption while scaling. Four
  named captures and the run log are under `archive/S15-text-scaling/`. Confidence: medium.

## Resolved tool versions for S2
- KSP Gradle plugin: `com.google.devtools.ksp:2.3.5` (**D81 primary path**, no
  `gradle.properties` fallback, no TD-001).
- AndroidX Room Gradle plugin and libraries: `2.8.4`.
- Robolectric: `4.16`.
- AndroidX Test: `core-ktx 1.7.0`, `ext:junit-ktx 1.3.0`, `runner 1.7.0`.
