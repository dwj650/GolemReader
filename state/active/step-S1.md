---
id: S1
tier: state
status: done
updated: 2026-06-30
cross-refs: [P1, D77, D78, D31, F-057]
if-incomplete: "Return to state/current-state.md."
---
# Step S1 — Project bootstrap (toolchain heartbeat)
Phase: P1  ·  Feature(s): none (infrastructure)  ·  current-rung: Closeout
Branch: `s1-project-bootstrap`  (the guards block commits to main — ALL work for this step happens on this branch)

## Statement of Work
Stand up a minimal native Android app (Kotlin + Jetpack Compose) that builds, installs, and launches on the Samsung Galaxy S23 Ultra showing a single placeholder screen, and that has one passing JVM unit test — proving the full toolchain (Android Studio → Gradle → device → test runner → guards) works end to end. No feature logic of any kind.

## Scope — files that WILL change
- Gradle project root: `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`, `gradle/` (wrapper files), `gradlew`, `gradlew.bat`
- App module `app/`: `app/build.gradle.kts`, `app/src/main/AndroidManifest.xml`, `app/src/main/java/com/golemreader/MainActivity.kt`, a placeholder Compose screen under `app/src/main/java/com/golemreader/ui/`, `app/src/main/res/**` (theme, `strings.xml`, launcher icon), `app/proguard-rules.pro`
- Test: `app/src/test/java/com/golemreader/SmokeTest.kt` (one trivial passing test)
- `guards/guards.config`: set `TEST_CMD="./gradlew testDebugUnitTest"` and `CODE_PREFIX="app/"`
- `.gitignore`: add Android/Gradle entries (`/build`, `/app/build`, `.gradle/`, `local.properties`, `.idea/`, `*.iml`, `.cxx/`, `/captures`)
- `ledgers/decision-log.md`: append **D77** (bootstrap stack) using `templates/decision.md`
- `ledgers/testing.md`: record the smoke-test result (`T-S1-smoke`)
- `ledgers/known-issues.md`: record the D78 gate-check template defect found during G3
- `guards/gate-check.sh`: corrected G3 report control flow accepted under D78
- `state/current-state.md`: advance to "S1 done → next S2"
- `state/active/step-S1.md`: this file — set `status: done` and fill Verify/Closeout at the end

## Non-goals — explicitly NOT touched
- No EPUB parsing / text pipeline / segmentation (F-018 / F-027 / F-028).
- No TTS, no sherpa-onnx, no audio pipeline, no voices (F-048 / F-008 / F-001).
- No Room / storage / precious schema (F-057) — that is the next step, S2.
- No MediaSession, foreground service, navigation, or any UI beyond one static placeholder screen.
- No dependency-injection framework, no networking, no CI secret wiring.
- Do NOT modify `process/`, `foundation/`, or `reference/`, or any other step file.

## Decisions in play
- **D77 (operator-delegated) — bootstrap stack.** UI = Jetpack Compose + Material3; package/namespace = `com.golemreader` (applicationId `com.golemreader`); `minSdk = 29`; `compileSdk`/`targetSdk` = the current stable level the installed Android Studio defaults to; Kotlin + Android Gradle Plugin versions = those bundled with current stable Android Studio; Gradle **Kotlin DSL** (`.kts`).
  - *Reasoning:* Compose is the current-standard Android UI toolkit and is the right base for the custom synchronized-highlight rendering coming later (PR-1/PR-4); `minSdk 29` covers the S23 Ultra comfortably while keeping modern media/foreground-service APIs available; `com.golemreader` is simple and trivial to change now but painful later, so it is fixed at bootstrap.
- **D78 (operator-delegated) — gate-check control-flow fix.** Accept the corrected
  `guards/gate-check.sh` so G3 reports only real failures and treats the unconfigured
  secret scanner as SKIP, matching the guard README and pre-commit hook policy.
- Establishes the floor that F-057 (S2) and the listen-loop spine build on; implements no feature decision.

## Acceptance criteria (what "done" means)
- [x] **Presence & wiring** — `./gradlew assembleDebug` prints **BUILD SUCCESSFUL**; the app installs and launches on the S23 Ultra showing a placeholder screen reading "Golem Reader".
- [x] **Behavior & limits** — `./gradlew testDebugUnitTest` runs and the one smoke test **passes**.
- [x] **Cost & budget** — N/A at bootstrap (no listen-loop yet); recorded as contributor-N/A in `ledgers/testing.md`.
- [x] **Result** — a fresh clone can build, test, and run the heartbeat; `guards.config` now runs the real test command, so the next `app/` commit is gated by it.

## Test posture
- Form: **automated** (one JVM unit test) + **agent-run / guided-manual** (launch on the device, verified by eye).
- Plan: `SmokeTest` asserts a trivial truth (e.g., an app-name constant equals "Golem Reader"); device launch confirmed visually.
- Principle conformance: PR-4 (a clean module boundary that won't fight later hotswappability), PR-7 (clean-room — write original code; copy nothing).

## Entry gate (G2)
- [x] Prior step closed (bring-up committed & pushed)
- [x] current-state fresh
- [x] scope + non-goals declared
- [x] (non-trivial) operator approved — S1 approved 2026-06-29

## Rung tracker
- [x] Orient  - [x] Scope  - [x] Inspect  - [x] Change
- [x] Verify  - [x] Record  - [x] Commit (G3, guarded)  - [x] Closeout

## Verify result
- Result: pass  ·  Confidence: high  ·  T-id: T-S1-smoke
- Notes: `./gradlew assembleDebug` and `./gradlew testDebugUnitTest` passed. The debug APK
  installed and launched on attached device `R5CW72ZRMWP`; screenshot showed the placeholder
  screen reading "Golem Reader".

## Closeout
- Committed: branch `s1-project-bootstrap` / S1 project bootstrap commit  ·  Next step: **S2 — Storage substrate (F-057)**
