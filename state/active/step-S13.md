---
id: S13
tier: state
status: done
updated: 2026-07-13
phase: P2
features: [F-064, F-065]
cross-refs: [D51, D52, D68, D93, D98, D100, D101, D102, D103, D104, PR-7, IMP-001, IMP-003]
current-rung: Closeout
if-incomplete: "Return to state/current-state.md."
---
# Step S13 — Settings Host, thin (F-064) + navigation topology + theme picker home

> **Version v1.0.0** — initial approved SOW. Grounded against repo main @ 1a08e91,
> F-064-Settings-Host-Requirements v1.0.0, and the approved prototype v0.3.0
> (D103). Scope resolved with the operator component-by-component on 2026-07-12;
> all components operator-approved (none delegated).

## Objective (plain language)
Give the app its real navigation skeleton and give settings a real home. A bottom
navigation bar (per the approved v0.3.0 visual contract) with two live tabs —
Now Playing and Settings — replaces the S9 placeholder screen-switch. The
Settings screen is a thin shell: it reads a declared **Settings Map** (a
plain-Kotlin registry, D104) and renders exactly the entries whose owning
features exist — on day one, a single **Appearance** section hosting the F-065
theme picker built in S12. Reading is reached by tapping a simplified preview
strip on Now Playing (the strip's full richness remains F-073). Picking a theme
must never touch the main thread with database work.

## Scope
1. **Route model** (`com.golemreader.ui.navigation` or similar): a plain-Kotlin
   destination model (Now Playing, Settings, Reading-overlay) driving a bottom
   nav bar per the v0.3.0 contract. No navigation library (D102 — same
   reasoning as D101: an in-house `when` over three destinations, JVM-testable
   as a plain state machine). The Library tab is **not rendered** until F-019
   exists (D68: absent, never a dead control).
2. **Settings Map registry** (D104): a plain-Kotlin object declaring settings
   entries — label, group, owning feature, and how the shell can tell the
   feature is built. The shell renders what the registry yields; nothing else.
   Adding a future setting (S14–S16) must be a one-entry registration with zero
   shell changes (F-064 R3).
3. **Settings screen shell** (F-064 R1, R2): screen title, grouped sections from
   the registry, controls owned by their parent features and only routed here.
   Day one renders exactly one section: Appearance → Theme.
4. **Theme picker lands** (F-065 → its home): `ThemeChoicePicker` moves into the
   Appearance section, restyled as the segmented System · Light · Dark control
   from the v0.3.0 contract. Same three options, same order, same
   follow-system default; visual form only.
5. **Theme write off the main thread**: `ThemeSettingsRepository.setChoice()`
   becomes a suspend call executing on an IO dispatcher (or a Room suspend DAO
   function). Production code has never called it; the picker's new home is its
   first caller, and a blocking main-thread Room write is the exact defect
   class S12 hit on the read side. **All new repository writes in this step and
   every future step are off the main thread.**
6. **Simplified preview strip on Now Playing** (D102): a tappable row showing
   the current sentence text (driven by the existing highlight state) that
   opens Reading; visual position per the v0.3.0 contract. The S9/D93
   screen-switch is **removed**. Reading keeps an explicit back affordance to
   Now Playing.
7. **Launcher icon** (rider): convert `foundation/assets/golem-reader-icon.png`
   into the Android adaptive launcher icon (foreground/background layers,
   standard mipmap densities). Repo-only change.

## Non-goals (explicit)
- No swipe gestures (swipe-left to Reading remains D51/D52 target vision).
- No full F-073 embedded sync preview — the strip here is deliberately minimal.
- No Library tab, no F-019 work.
- No keyboard traversal (S17), no high contrast (S14), no text scaling (S15),
  no reduced motion (S16), no F-070 onboarding.
- No new settings beyond Theme; the map-preview rows in the v0.3.0 prototype
  are illustration only and MUST NOT be rendered by the shipping app (D68).

## Changed files (expected)
- **New:** route model + bottom nav under `app/src/main/java/com/golemreader/ui/`
  (e.g. `navigation/`, `settings/`), Settings Map registry, settings screen
  composables, adaptive icon resources under `app/src/main/res/mipmap-*`,
  `foundation/assets/golem-reader-icon.png` (lands via the handoff commit).
- **Modified:** `GolemReaderApp.kt` (placeholder switch removed, nav host in),
  `NowPlayingScreen.kt` (preview strip row), `ThemeSettings.kt` (suspend write),
  `AndroidManifest.xml` (icon), tests, and — per docs-with-code —
  `state/current-state.md`, `state/active/step-S13.md`, `ledgers/testing.md`.
- **Not touched:** anything under `playback/`, `audio/`, `voice/`, `text/`,
  `identity/`, `transport/`, `highlight/`, `bootstrap/` — this step is
  presentation and navigation only. A diff touching the listen loop is a halt
  condition.

## Referenced decisions
D102 (navigation topology: in-house route model, bottom nav per contract,
Library hidden until F-019, preview-strip tap replaces the D93 switch),
D103 (prototype v0.3.0 is the frozen visual contract, superseding v0.2.0;
resolves OB-064-1: day-one Settings = one Appearance section),
D104 (Settings Map is a plain-Kotlin code registry; resolves OB-064-2),
D68 (absent, never dead), D98 (contract principle), D100 (phase shape),
D101 (no-hardcode guard covers all new UI code automatically), D15b/D31
(theme setting remains precious; no schema change expected in this step).

## Acceptance criteria
1. Settings Map unit tests (JVM): the menu yields exactly the entries whose
   owning features are built; unbuilt entries are absent — no placeholder, no
   disabled row (T-064-P2, T-064-B1, T-064-B2 against genuinely absent
   F-005/F-006/F-007 parents).
2. Route model unit tests (JVM): tab selection, Reading overlay open/close,
   and back-from-Reading resolve to the correct destination; Library is not a
   destination.
3. Adding a fake in-test registry entry surfaces it in the correct group with
   zero shell changes (F-064 R3 seam, mirroring S12's R7 seam test).
4. `setChoice()` is suspend and executes off the main thread, proven by test;
   no blocking DB I/O on the main thread anywhere in the new code.
5. No-hardcode guard passes over all new UI files (T-064-B3; D101 covers new
   packages automatically) and the full JVM suite + `assembleDebug` pass.
6. On-device (SM-S918U, T-064-R1): bottom nav shows exactly Now Playing and
   Settings; Settings shows exactly one Appearance section; picking Dark
   re-themes live with no crash/ANR and survives force-stop/relaunch; the
   preview strip opens Reading and back returns; the old top switch is gone;
   the launcher shows the new icon. Screenshots of Settings (both themes),
   Now Playing, and the launcher icon archived for G4 comparison against the
   v0.3.0 contract.

## Deferrals (recorded, with owners)
- T-064-B4 (keyboard traversal of the settings screen) → S17 (F-069).
- T-064-R2 (guided check under high contrast + larger text + keyboard-only) →
  G4, after S14–S17 exist.
- Swipe-left to Reading → D51/D52, future phase.
- Full embedded sync preview → F-073, future phase.
- Library tab → the F-019 phase.

## Test posture (AI-recommended: full step)
- **Automated (JVM, High confidence):** Settings Map yield/absence, registry
  seam, route model, suspend-write proof, no-hardcode guard, existing suites.
- **Agent-run device (Medium):** T-064-R1 flow above + screenshots + icon.
- **Guided-manual (Medium):** operator look-check of the settings screen and
  launcher icon against the v0.3.0 contract at closeout; subjective look is
  iterable without reopening scope.

## Handoff notes (IMP-001 standing rule)
Before any multi-command paste on p1: run `git branch --show-current`, read the
output, and stop at the first failed command. Repo path on p1 is
`~/AndroidStudioProjects/Golem`. On T14, `cp` is aliased to `cp -i`: run `cp`
lines one at a time or prefix with `\cp`.

## Implementation record (2026-07-13)
- Replaced the S9 top screen-switch with a plain-Kotlin route state, a two-item
  Now Playing / Settings bottom bar, and a Reading overlay that returns explicitly
  to Now Playing.
- Added the plain-Kotlin Settings Map and generic grouped Settings shell. The production
  map declares Theme plus genuinely unbuilt F-005/F-006/F-007 entries; filtering renders
  only the built Appearance → Theme entry.
- Moved the feature-owned theme picker into Settings and restyled it as the approved
  System · Light · Dark segmented control with selected-state semantics.
- Made `ThemeSettingsRepository.setChoice()` suspend and dispatched its DAO write through
  an injectable IO dispatcher; `MainActivity` calls it from a Compose coroutine.
- Replaced the reserved sync-preview slot with a tappable current-sentence strip and
  added an explicit Reading Back control.
- Converted `foundation/assets/golem-reader-icon.png` into legacy and adaptive launcher
  resources for mdpi through xxxhdpi, with density-specific foreground layers.

## Verification record (2026-07-13)
- Clean baseline: `./gradlew testDebugUnitTest` passed before changes.
- TDD: the first targeted run failed on the deliberately missing navigation, Settings
  Map, preview, and dispatcher APIs; after implementation, all 13 targeted tests passed.
- Full JVM suite, `assembleDebug`, and `assembleDebugAndroidTest` passed before final
  record reconciliation. The no-hardcode guard and `git diff --check` also passed.
- SM-S918U: the rebuilt app/test APKs installed successfully. The S13 navigation device
  test passed once while the device was unlocked (`OK (1 test)`), covering exactly two
  bottom tabs, Appearance → Theme only, absent Speed, preview-to-Reading, and Back.
- After the operator unlocked the phone, both S13 device methods passed independently
  (`OK (1 test)` each): navigation/Settings/Reading flow and live Dark selection.
  Force-stop/relaunch preserved Dark; Android's hierarchy reported the Dark radio segment
  `checked="true"` after restart. The final APK was reinstalled after the vector-icon
  refinement and visually confirmed with headphone/gear bottom-nav icons.
- Settings in Dark and Light, Now Playing, and the Samsung launcher result showing the
  adaptive Golem Reader icon are archived under `archive/S13-settings-host/`. Objective
  device checks pass at medium confidence. The operator reviewed and approved all four
  archived screenshots on 2026-07-13, completing the guided subjective look-check.

## Closeout (2026-07-13)
The operator approved the Dark Settings, Light Settings, Now Playing, and launcher-icon
evidence. All S13 acceptance criteria are satisfied at the declared confidence levels;
recorded deferrals remain owned by S14–S17, F-019, F-073, and D51/D52. Step done;
D-ceiling at close: D104. Next step: S14 — High contrast (F-066), not yet started.
