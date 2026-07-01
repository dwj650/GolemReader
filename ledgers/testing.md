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

## Resolved tool versions for S2
- KSP Gradle plugin: `com.google.devtools.ksp:2.3.5` (**D81 primary path**, no
  `gradle.properties` fallback, no TD-001).
- AndroidX Room Gradle plugin and libraries: `2.8.4`.
- Robolectric: `4.16`.
- AndroidX Test: `core-ktx 1.7.0`, `ext:junit-ktx 1.3.0`, `runner 1.7.0`.
