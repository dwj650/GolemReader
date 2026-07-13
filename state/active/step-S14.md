---
id: S14
tier: state
status: approved
updated: 2026-07-13
phase: P2
features: [F-066]
cross-refs: [D31, D68, D69, D100, D101, D103, D104, D105, D106, D107, PR-7, IMP-001, IMP-003]
current-rung: Change
if-incomplete: "Return to state/current-state.md."
---
# Step S14 — High contrast (F-066): HC token sets, toggle, central contrast test

> **Version v1.0.0** — initial approved SOW. Grounded against repo main @ e1fe9dd,
> F-066-High-Contrast-Requirements v1.0.0, and the D103 visual contract. Scope
> resolved with the operator 2026-07-13; all components operator-approved.

## Objective (plain language)
Give the app a real high-contrast mode: one switch in Settings that makes the
whole app markedly more legible. High contrast is a **token variant** — two new
pure-data value-sets (high-contrast dark, high-contrast light) whose text
colors hit the AAA contrast target, proven by one central automated test that
every other feature defers to (D69). No screen does its own contrast handling;
flipping the switch just swaps which token values every screen already reads.

## Scope
1. **Two HC value-sets** (D106): `hcDark` and `hcLight` in
   `GolemThemeValueSets`, pure data like the existing pair, palette authored to
   the D105 target. `resolveThemeValueSet()` gains a high-contrast dimension:
   `(choice, systemDark, highContrast)` → one of four value-sets. HC composes
   with the theme choice (R5): HC + Dark → hcDark, HC + Light/system-light →
   hcLight.
2. **HC preference, precious, no migration** (D107): stored as a **second
   key-value row** in the existing `theme_settings` table
   (`key = "high_contrast"`), reusing the entity/DAO shape. **No schema
   change, no version bump** — the D31 harness is not triggered. Repository
   gains a `highContrastFlow()` and a **suspend** `setHighContrast()` on the
   IO dispatcher (same off-main-thread rule as S13; a blocking write is a halt
   condition).
3. **Toggle registered into the Settings Map** (C-066-2): one new
   `SettingEntry` (id `high-contrast`, group **"Accessibility"**, owning
   feature F-066, built = true) — the first live proof of D104's one-entry
   registration promise. The toggle **control** is owned by the theme/F-066
   package and routed into the shell via `controlContent`, like the theme
   picker. A second section (Accessibility) appears under Appearance, per the
   D103 contract's section pattern and F-066 §7.
4. **Central contrast test harness** (C-066-3, R4): grow the existing
   `GolemThemeTokensTest` contrast check into F-066's owned contract:
   (a) **ratio checks** — on both HC value-sets, textPrimary/background and
   textSecondary/background ≥ 7.0; accent-as-control vs background and
   highlight pairs ≥ 3.0 (D105); base themes keep their existing ≥ 4.5 floor;
   (b) **re-render check** — toggling HC swaps the resolved value-set with
   zero screen changes (extends the S12 seam test).
5. **Plumbing**: `GolemThemeProvider` accepts the HC flag; `MainActivity`
   collects `highContrastFlow()` alongside the theme flow and passes a
   `onHighContrastToggled` callback, mirroring the S13 pattern exactly.

## Non-goals (explicit)
- No text scaling (F-068/S15), no reduced motion (F-067/S16), no keyboard work
  (F-069/S17), no base-theme changes beyond none-at-all (F-065 palettes are
  untouched; HC adds value-sets, never edits existing ones).
- No per-screen contrast logic anywhere (R2/D69) — a screen referencing HC
  state directly is a halt condition.
- No `theme_settings` schema change and no column rename (the `choice` column
  name is a recorded wart; renaming would force the migration D107 avoids).

## Changed files (expected)
- **New:** HC toggle composable in the theme package; `SettingsMapTest`
  additions; HC-specific tests.
- **Modified:** `GolemThemeTokens.kt` (two value-sets + resolver dimension),
  `ThemeSettings.kt` (second KV row, suspend write, flow), `GolemTheme.kt`
  (provider param), `MainActivity.kt`, `SettingsMap.kt` (one entry),
  `GolemReaderApp.kt` (one `controlContent` branch + provider arg),
  `GolemThemeTokensTest.kt` (central harness), and — per docs-with-code —
  `state/current-state.md`, `state/active/step-S14.md`, `ledgers/testing.md`.
- **Not touched:** anything under `playback/`, `audio/`, `voice/`, `text/`,
  `identity/`, `transport/`, `highlight/`, `bootstrap/`; `app/schemas/`
  (no migration). A diff in either is a halt condition.

## Referenced decisions
D105 (contrast target: AAA — 7:1 normal text pairs, 3:1 large-text/UI pairs;
resolves OB-066-1), D106 (HC = two pure-data value-sets; resolver gains the HC
dimension; no programmatic color transforms), D107 (HC preference is a second
KV row in `theme_settings`; no schema change), D69 (central tested contract),
D104 (one-entry registration), D101 (guard covers all changes automatically),
D68/D103 (section appears because its feature now exists).

## Acceptance criteria
1. Both HC value-sets resolve every token (existing completeness test extends
   to four sets) and the resolver maps all (choice × systemDark × HC)
   combinations correctly (T-066-P1, JVM).
2. Central ratio check passes at D105 targets on both HC sets; base themes
   unchanged at their existing floor (T-066-B2, JVM). A deliberately failing
   in-test palette demonstrably fails the harness (guard-style negative
   proof).
3. Toggle-on swaps the resolved value-set with zero screen changes; toggle-off
   restores (T-066-B1 + seam, JVM).
4. HC composes with each theme choice (T-066-B4 theme half, JVM). **Scaling
   half deferred → S15** (F-068 does not exist).
5. `setHighContrast()` is suspend and proven off the calling thread (same test
   pattern as S13); registry test proves the Accessibility entry appears and
   the map still hides unbuilt entries.
6. No-hardcode guard and full JVM suite + `assembleDebug` pass (T-066-B3).
7. On-device (SM-S918U): toggle on → visibly higher contrast app-wide on
   Settings, Now Playing, Reading; survives force-stop/relaunch; composes
   with Dark and Light; no measurable playback cost while toggling during
   playback (T-066-C1, agent-run — contributor tag N/A per spec, reason:
   token swap, not listen-loop work). Screenshots (HC-dark + HC-light on
   Settings and Reading) archived for G4.

## Deferrals (recorded, with owners)
- T-066-B4 scaling composition → S15 (F-068).
- T-066-R1 full guided legibility sweep → operator look-check at closeout,
  re-walked at G4 with all axes on.

## Test posture (AI-recommended: full step)
- **Automated (JVM, High):** completeness ×4, resolver map, central ratio
  harness + negative proof, toggle/seam swap, suspend-write proof, registry.
- **Agent-run device (Medium):** T-066-C1 + persistence + composition
  screenshots.
- **Guided-manual (Medium):** operator legibility look-check at closeout.

## Handoff notes (IMP-001 standing rule)
Before any multi-command paste: `git branch --show-current`, read it, stop at
first failure. p1 path: `~/AndroidStudioProjects/Golem`. T14 `cp` is aliased
interactive — run `cp` lines singly or use `\cp`. Downloads hygiene: bundle
files are uniquely named (`-s14` suffix) to avoid the stale-duplicate trap;
after placing files, verify `updated:` dates read 2026-07-13.
