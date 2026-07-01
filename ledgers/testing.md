---
id: TESTING
tier: ledger
status: active
updated: 2026-06-30
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

## Resolved tool versions for S2
- KSP Gradle plugin: `com.google.devtools.ksp:2.3.5` (**D81 primary path**, no
  `gradle.properties` fallback, no TD-001).
- AndroidX Room Gradle plugin and libraries: `2.8.4`.
- Robolectric: `4.16`.
- AndroidX Test: `core-ktx 1.7.0`, `ext:junit-ktx 1.3.0`, `runner 1.7.0`.
