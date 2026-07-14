---
id: S17
tier: state
status: active
updated: 2026-07-14
phase: P2
features: [F-069]
cross-refs: [D68, D69, D74, D100, D101, D104, D105, D106, D113, D114, D115, OB-069-1, OB-069-2, T-064-B4, IMP-001, IMP-003]
current-rung: Change
if-incomplete: "Return to state/current-state.md."
---
# Step S17 — Keyboard navigation (F-069): reachability, focus order, visible focus ring, central keyboard test

> **Version v1.0.0** — initial approved SOW. Grounded against repo main @ **b507c75**,
> F-069-Keyboard-Navigation-Requirements v1.0.0, F-064 v1.0.0 (T-064-B4 deferral),
> F-066 v1.0.0 (focus visible at high contrast), and Design Spec v0.6.0 (D68/D69/D74).
> Scope resolved with the operator 2026-07-14: Components 1–5 approved individually.
> New decisions minted: **D113** (test surfaces, resolves OB-069-2), **D114**
> (focusRing token + shared ring mechanism), **D115** (traversal-order contract).
> Per D100 this is the **last step before G4**.

## Objective (plain language)
Make the whole app operable without touching the screen. With a keyboard attached,
pressing Tab moves a clearly visible focus ring from control to control in reading
order on every screen; Enter/Space activates the focused control exactly as a tap
would. The ring is drawn from a new theme token and stays clearly visible in high
contrast. This step also installs the central keyboard test that every current and
future surface defers to (D69).

## Grounding findings (IMP-003, recorded — verified against b507c75 on 2026-07-14)
1. **Zero focus handling exists.** No `focusable`, `FocusRequester`, `onKeyEvent`,
   `focusOrder`, `focusProperties`, or `FocusManager` usage anywhere in
   `app/src/main`. This step starts the axis from scratch.
2. **Interactive-control inventory is exactly seven files:**
   - `ui/reading/ReadingViewScreen.kt` — one `TextButton` (Back).
   - `ui/nowplaying/NowPlayingScreen.kt` — one `clickable` preview row (opens
     Reading View) + four `Button`s: Play, Pause, Resume, Stop.
   - `ui/navigation/GolemBottomNavigation.kt` — three `NavigationBarItem`s
     (Library, Now Playing, Settings).
   - `theme/ThemeChoicePicker.kt`, `theme/HighContrastToggle.kt`,
     `theme/TextScaleStepper.kt` (A− and A+), `theme/ReducedMotionToggle.kt` —
     the four Settings entries in D104 registry order.
3. **All controls are standard Material/foundation components** — keyboard-focusable
   and Enter/Space-activatable by default. The build is the *ring*, the *order
   proof*, and the *central test* — not reachability plumbing.
4. **No focus-indicator token exists.** `GolemColors` has no focus-related field;
   one must be authored across all four value-sets (D106 pattern).
5. **Test toolkit is already present:** `ui-test-junit4` + Robolectric 4.16 in the
   JVM `test` source set, with existing Robolectric screen tests since S9. Whether
   simulated Tab traversal works under Robolectric is unproven — hence Task 1.
6. **F-069 §7: no stored preference.** Keyboard navigation is always-on. No
   `theme_settings` row, no D104 registry entry, no Settings UI change.

## Scope
1. **`focusRing` color token (D114, C-069-2).** Add `focusRing: Color` to
   `GolemColors`, authored in **all four value-sets** (dark, light, hcDark,
   hcLight). Proposed values (operator judges at look-check; adjust only if the
   contrast math fails):
   - dark: `Color(0xFF80CBC4)` (matches accent family, distinct from highlight)
   - light: `Color(0xFF00675B)`
   - hcDark: `Color(0xFFFFFF00)` — must meet **≥ 3:1** against `background`,
     `surface`, and `surfaceRaised` of hcDark (D105 non-text floor)
   - hcLight: `Color(0xFF0000CC)` — same 3:1 floor against hcLight surfaces
   The D101 no-hardcode guard automatically polices the new token.
2. **Shared ring mechanism (D114, C-069-2).** One reusable modifier, e.g.
   `Modifier.golemFocusRing()` in a new `theme/FocusRing.kt`:
   - `onFocusChanged` tracks focus state; when focused, draw a **3dp border**
     (rounded with `tokens.shapes.control`) in `tokens.colors.focusRing`, offset
     outside the control's bounds so it never repaints content.
   - **No animation of any kind** — appears/disappears instantly (keeps F-069
     outside F-067's jurisdiction by construction). Do not use `animate*AsState`,
     `Animatable`, or transitions in this file.
   - Apply the modifier to every control in the seven-file inventory. No control
     gets a custom focus look.
3. **Traversal-order contract (D115, C-069-1).** The declared orders the central
   test asserts. Compose's default layout order is expected to already satisfy
   these; add `focusGroup`/`focusProperties` **only** where a test proves the
   default deviates:
   - **Settings:** Theme picker → High contrast toggle → Text size A− → A+ →
     Reduced motion toggle → nav (Library → Now Playing → Settings).
   - **Now Playing:** preview row → Play → Pause → Resume → Stop → nav tabs.
   - **Reading View:** Back → nav tabs. Sentence rows are display, **not**
     focusable — do not add them to traversal.
   - Rule: **screen content first, nav tabs last**, nav left-to-right.
   - Contract keys: **Tab / Shift-Tab**. Arrow keys stay library-native — build
     and promise nothing beyond what Material provides.
4. **Central keyboard test (D113, C-069-3, resolves OB-069-2; absorbs T-064-B4).**
   Covers all four current surfaces. See test plan below. A future surface not in
   this test is a visible gap.
5. **Deferral recorded:** T-069-B5 (onboarding keyboard-operable, D74) is
   **deferred to the phase that builds F-070** (per D100/D113), owner recorded in
   the closeout. Not built, not silently skipped.

## Task order (execute in this order; Task 1 gates the test strategy)
**Task 1 — Robolectric Tab spike (≤ 30 min, throwaway allowed).**
One minimal Robolectric compose test: two `Button`s in a `Column`, request focus
on the first, `performKeyInput { pressKey(Key.Tab) }` (or inject via the root),
assert focus moved to the second.
- **PASS →** all traversal/activation tests are JVM tests in the commit gate
  (primary path below).
- **FAIL →** invoke the **pre-authorized fallback**: traversal-order and
  key-activation tests move to `androidTest` (agent-run on the S23, same
  assertions); ring-token and contrast-math tests stay JVM. Record which path ran
  in the run log. Do **not** halt, do not improvise a third strategy.

**Task 2 —** token + ring mechanism + application to all seven files.
**Task 3 —** traversal-order and activation tests per the plan; fix order only
where tests prove deviation.
**Task 4 —** agent-run device evidence (cost box-check + screenshots of the
focused ring in dark and hcDark for the operator's look-check).

## Acceptance criteria
- **AC1** (T-069-P1): the ring renders from `tokens.colors.focusRing` — asserted
  by test; no hardcoded ring color anywhere (D101 guard green).
- **AC2** (T-069-B1 + T-064-B4): on each of the four surfaces, Tab traversal
  reaches **every** control in the inventory; Settings traversal hits each D104
  entry.
- **AC3** (T-069-B2): traversal order matches the D115 declared order per surface,
  forward (Tab) and backward (Shift-Tab).
- **AC4** (T-069-B4): Enter and Space on each focused control produce the same
  observable result as tap (nav tab switches screen; toggles flip their
  preference value; stepper steps; transport buttons invoke their callbacks).
- **AC5** (T-069-B3, automated half): contrast-math test proves hcDark/hcLight
  `focusRing` values meet ≥ 3:1 against their set's background/surface/
  surfaceRaised (same style as the S14 contrast tests).
- **AC6**: `FocusRing.kt` contains no animation API (asserted by the harness, same
  pattern as S16's flip-path proof).
- **AC7** (T-069-C1): agent-run device check confirms no measurable steady-state
  playback cost from focus handling; record the "N/A — not a T-001-C1
  contributor" tag per F-069 §10.
- **AC8**: no new preference, no `theme_settings` write, no D104 registry change,
  no Settings UI addition (asserted by inspection in verification).
- **AC9** (T-069-B3 guided half + T-069-R1): **operator-only, at closeout** —
  keyboard walkthrough per the script below; look-check precedes the acceptance
  verb. The agent does not perform, simulate, or record this.

## Pre-authorizations
- Test-only additions to `build.gradle.kts` required by mandated tests are
  pre-authorized (S15 lesson) — list each in the completion report.
- The Task-1 fallback path (device-side traversal tests) is pre-authorized.

## Agent conduct (standing, from the S16 correction — read carefully)
- **Never write an operator verb.** All operator-acceptance and closeout fields in
  this file, the run log, `current-state.md`, and anywhere else remain **literal
  placeholders** (`[OPERATOR: pending]`). An agent never records an operator
  action, past or future tense. Violations are superseded on-branch at
  verification (candidate IMP-005).
- Branch name: `s17-keyboard-navigation`, from main @ b507c75. Never commit to
  main.
- **Push the branch** and state in the completion report that it is pushed; the
  operator verifies with `git ls-remote --heads` before verification begins.
- Completion report lists: files changed (diffed against this SOW's scope), tests
  added and their path (JVM vs device per Task 1's outcome), any pre-authorized
  build additions used, archived evidence locations.

## Evidence posture
- **Automated (JVM commit gate):** AC1, AC5, AC6, AC8 always; AC2–AC4 if Task 1
  passes.
- **Agent-run (S23):** AC7 always; AC2–AC4 if Task 1 fails; ring screenshots
  (dark + hcDark, one focused control each) archived under
  `archive/S17-keyboard-nav/`.
- **Guided (operator, closeout):** AC9.

## Operator closeout script (AC9) — operator runs this, not the agent
*Pairing preamble:* Settings → Connections → Bluetooth on the S23 → pair the
Bluetooth keyboard (hold its pairing button until it flashes; tap its name; type
the code if prompted). One-time; it reconnects automatically afterward.
1. Open Golem Reader. Press **Tab** — a ring must appear on a control.
2. On Now Playing: Tab through preview row → Play → Pause → Resume → Stop → the
   three nav tabs, in that order; **Shift-Tab** walks it backward.
3. With Play focused, press **Enter** — playback starts. Press Tab to Pause,
   press **Space** — playback pauses.
4. Tab to the Settings tab, press Enter. Tab through: theme picker → HC toggle →
   A− → A+ → reduced-motion toggle. Space on the HC toggle — high contrast flips.
5. In high contrast, confirm the ring is still clearly visible while Tabbing.
6. Tab to Now Playing tab → Enter → Tab to preview row → Enter — Reading View
   opens. Tab reaches Back; Enter returns. Ring visible the whole run.
- **What good looks like:** the ring is never invisible, never skips a control,
  never lands anywhere you can't see, and every key action matches its tap.

## Closeout
> **[OPERATOR: pending]** — implementation status, verification record, look-check
> and walkthrough result, acceptance verb, merge hash: all recorded by the
> operator's session, never pre-written by the agent.
