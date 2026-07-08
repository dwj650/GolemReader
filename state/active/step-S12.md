---
id: S12
tier: state
status: implemented (device screenshot tier blocked)
updated: 2026-07-08
phase: P2
features: [F-065]
cross-refs: [D15b, D31, D98, D99, D100, D101, PR-6, PR-7, IMP-001, IMP-003]
current-rung: Closeout
if-incomplete: "Return to state/current-state.md."
---
# Step S12 — Theme foundation (F-065)

> **Version v1.0.1** — supersedes v1.0.0 (retained in Design Zone for rollback).
> Changelog: status proposed → approved (operator, 2026-07-02); approval locks
> D101 (script-based no-hardcode guard). No scope change from v1.0.0.
> v1.0.0 changelog: initial draft, grounded against repo main @ 8f7d6f7 and
> F-065-Theme-Requirements v1.0.1.

## Objective (plain language)
Give the app a real theming foundation: every visual value a screen uses (colors,
text sizes, corner radii, shadows, spacing, animation timing) becomes a **named
token** the screen asks for, instead of a raw value written in place. Ship one
complete **dark** theme (values derived from the approved v0.2.0 prototype
palette, per D98) and one complete **light** theme. The user's choice —
light / dark / follow-system — persists across restarts, defaults to
follow-system, and switching re-themes the whole app live, including the phone's
status and navigation bars.

## Scope
1. **Token system** (`com.golemreader.theme`): named tokens across six
   categories — color, typography, shape, elevation, spacing, motion (F-065 R1).
2. **Two theme value-sets**: dark (from the D98 prototype palette: deep
   green-black surfaces, mist text, lime accent, lamp amber highlight) and light
   (derived in build to the same token map; aesthetic iterated per F-065 §12).
   A theme is **pure data** — a value for every token, no logic (F-060 seam).
3. **Theme setting, precious**: a settings entity in the Room precious database
   (schema v3 → v4) storing the choice; migration proven under the existing D31
   harness (F-065 R3, D15b).
4. **Application layer**: one composition-level provider that resolves the active
   theme and exposes tokens to all screens; follow-system reads the OS light/dark
   state and is the default (R4, R5). System status/navigation bar appearance
   follows the active theme (grounding finding: `themes.xml` currently hardcodes
   light system bars).
5. **Migration of existing UI**: the four current UI files
   (`GolemReaderApp.kt`, `ReadingViewScreen.kt`, `NowPlayingScreen.kt`,
   `BufferingIndicator.kt`) plus `MainActivity.kt` move off stock Material
   defaults onto tokens. Visual appearance may change (it now follows the
   prototype palette); behavior must not.
6. **No-hardcode guard (D101)**: a script check in the guard pipeline that fails
   gate-check if UI code contains raw color/size/duration literals outside the
   theme package (R6). The theme package itself is the one allowed home for
   literal values.
7. **Seam criteria (R7)**: token structure demonstrably allows — without
   re-architecting — an additional token value-set (F-066 high contrast, and any
   future imported theme via F-060), a motion-token toggle (F-067), and a
   typography multiplier (F-068). Proven by test: adding a third in-test theme
   value-set requires zero screen changes.

## Non-goals (explicit)
- No settings screen — the picker **control** is built and unit-tested, but has
  no home until S13 (F-064); device verification of switching uses the persisted
  setting directly.
- No high-contrast token set (S14), no text-scaling behavior (S15), no
  reduced-motion behavior (S16), no keyboard-nav work (S17).
- No gradients/glows (V2 rich theming, OB-065-1) and no theme import (F-060).
- No new user-facing features of any kind — this step changes how screens are
  painted, not what they do.

## Changed files (expected)
- **New:** `app/src/main/java/com/golemreader/theme/` (token definitions, light +
  dark value-sets, theme provider, theme-setting entity/DAO/repository, picker
  control composable), `guards/no-hardcode-check.sh` (or equivalent wired into
  `guards/gate-check.sh`), `app/schemas/.../4.json`.
- **Modified:** `PreciousDatabase.kt` (v4 + migration), the four UI files,
  `MainActivity.kt`, `app/src/main/res/values/themes.xml`, `guards/gate-check.sh`
  (wire the new check), tests, and — per docs-with-code — `state/current-state.md`,
  `state/active/step-S12.md`, `ledgers/decision-log.md`, `ledgers/testing.md`.
- **Not touched:** anything under `playback/`, `audio/`, `voice/`, `text/`,
  `identity/`, `transport/`, `highlight/`, `bootstrap/` — theming is presentation
  only. A diff touching the listen loop is a halt condition.

## Referenced decisions
D98 (prototype = visual contract; dark palette source), D99 (F-065 first),
D100 (phase shape), D101 (script-based no-hardcode guard), D15b (theme setting is
precious), D31 (migration proven by test), D85/D86 (per-machine build config,
JDK 21).

## Acceptance criteria
1. Every token in all six categories resolves a value in both themes; no missing
   tokens (T-065-P1).
2. Theme choice persists across restart (T-065-P2); Room migration v3→v4 passes
   the D31 harness with existing data intact.
3. Live switch re-themes sampled screens with no restart (T-065-B1); system bars
   follow.
4. Follow-system tracks an OS light/dark change (T-065-B2) and is the default.
5. No-hardcode guard passes on the migrated codebase and demonstrably fails on a
   seeded violation (T-065-B3).
6. Baseline contrast on primary text passes on both themes (T-065-B4 / R8).
7. Seam test: a third in-test theme value-set applies with zero screen changes
   (R7 / F-060 forward-compat).
8. On-device: pick dark → whole app dark, survives restart (T-065-R1);
   screenshots of both themes on both screens archived for the G4 comparison
   against the D98 prototype.

## Test posture (AI-recommended: full step)
- **Automated (JVM/Robolectric, High confidence):** token completeness,
  persistence + migration, follow-system resolution logic, no-hardcode script,
  baseline contrast math, seam test.
- **Agent-run device (Medium):** live switch, restart survival, system-bar
  appearance, screenshots.
- **Guided-manual (Medium):** T-065-R2 "it actually looks right" — the operator
  eyeballs both themes on both screens; subjective look is explicitly iterable
  without reopening this SOW's scope.

## Open boundaries carried
- OB-065-2 (exactly two themes in V1) — honored, unchanged.
- Light-theme aesthetic — iterated in build (F-065 §12); a follow-up polish pass
  is in-scope for later steps without re-approval of the system.
- S12 implementation boundary finding: `GolemStorageSubstrate.kt` needed a one-line
  migration-list update because it is the existing central Room database builder.
  This stayed inside storage wiring for the precious schema change and did not touch
  any forbidden listen-loop directory.
- Device tier boundary: agent-run screenshots and persisted dark restart proof were
  not captured on 2026-07-08 because `adb devices` returned no attached/authorized
  device.

## Implementation record (2026-07-08)
- Built token definitions and light/dark value-sets in `com.golemreader.theme`.
- Added theme setting entity/DAO/repository in the precious Room database, schema
  v4 export, and v3→v4 D31 migration test preserving `db_meta` and `book_identity`.
- Added `GolemThemeProvider`, follow-system resolution, and system-bar color/icon
  application.
- Migrated `GolemReaderApp`, `ReadingViewScreen`, `NowPlayingScreen`,
  `BufferingIndicator`, `MainActivity`, and `themes.xml` off stock visual defaults
  and onto tokens.
- Added `ThemeChoicePicker` control and option-model unit coverage; no settings screen
  or picker home was added.
- Added `guards/no-hardcode-check.sh`, wired it into `guards/gate-check.sh`, and
  verified both clean pass and seeded violation failure.

## Verification record (2026-07-08)
- JVM/Robolectric: `ANDROID_HOME=/home/davidt14/Android/Sdk ./gradlew testDebugUnitTest`
  passed.
- Build: `ANDROID_HOME=/home/davidt14/Android/Sdk ./gradlew assembleDebug` passed.
- Guard clean: `bash guards/no-hardcode-check.sh` passed.
- Guard seeded violation: temporary `SeededHardcodeViolation.kt` with
  `Color(0xFF123456)` failed the guard as expected, then was removed and the guard
  passed cleanly.
- Device: deferred/blocked; `adb devices` listed no devices.

## Handoff notes (IMP-001 standing rule)
Before any multi-command paste on p1: run `git branch --show-current`, read the
output, and stop at the first failed command. Repo path on p1 is
`~/AndroidStudioProjects/Golem`.
