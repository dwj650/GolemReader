---
id: S18
tier: state
status: active
updated: 2026-07-15
phase: P2
features: [F-002, F-014, F-015, F-064]
cross-refs: [D68, D69, D102, D103, D113, D114, D115, D116, D117, D118, IMP-001, IMP-003, IMP-005]
current-rung: Handoff
if-incomplete: "Return to state/current-state.md."
---
# Step S18 — Gate-inserted remediation: status-bar inset (F1) and icon transport (F2)

> **Version v1.0.0** — initial approved SOW. Grounded against repo main @ **7726eed**
> on 2026-07-15 by the design session (IMP-003): transport labels read from
> `ui/nowplaying/NowPlayingScreen.kt` lines 176–194; test-selector audit run across
> `app/src/test/` and `app/src/androidTest/`; inset handling audited across
> `app/src/main/java/`. Inserted at the **G4 phase acceptance gate** by decision
> **D117** after gate findings F1 and F2; follows the P1 precedent (D96/D97) that real
> gate defects become inserted steps by recorded decision rather than silent fixes or
> silent passes. New decisions minted: **D117** (insert-step + inset contract),
> **D118** (transport glyph mapping). Operator approved Components 1–3 individually
> on 2026-07-15.

## Origin — why this step exists

The G4 gate produced two findings against the D103 (v0.3.0) prototype visual contract:

- **F1 — status-bar collision.** Screen titles on Now Playing ("tom-sawyer") and
  Settings ("Settings") render underneath the system status bar, colliding with the
  clock. The contract places headers inside the content area. The code audit found
  **no `WindowInsets` / `safeDrawing` / `statusBar` padding anywhere in
  `app/src/main/java/`** — the top inset is unhandled, not mishandled. Reading View is
  visually clear only because its Back control happens to sit lower. Real defect.
- **F2 — transport label wrapping.** The four transport pills wrap mid-word
  ("Paus e", "Resu me") at default scale. Operator evidence at max text scale
  (2026-07-15, gate Check 3 step 8): labels wrap deeper — Resume to three lines, the
  others to two — but stay contained within their pills; nothing collides or clips.
  Cosmetic, degrades gracefully.

The bottom edge was audited and is **already correct**: `ui/navigation/GolemBottomNavigation.kt`
uses Material3 `NavigationBar`, which consumes the navigation-bar inset natively. Gate
captures confirm this. No bottom-edge change is in scope; the bottom edge is
**verified**, not modified (T-018-R3).

## Scope — three parts

### (a) Transport labels become icons — F2, permanent
In `ui/nowplaying/NowPlayingScreen.kt` only, the four `Text(...)` label calls at
lines 176/182/188/194 are replaced with icon glyphs per **D118**:

| Control | Glyph | Accessible name (unchanged semantics) |
|---|---|---|
| Play | ▶ (filled triangle) | "Play" |
| Pause | ❚❚ (two bars) | "Pause" |
| Resume | ▶❚ (triangle-with-bar) | "Resume" |
| Stop | ■ (square) | "Stop" |

Every icon **must** carry its accessible name via `contentDescription`. The four
`testTag` values (`transport-play`, `transport-pause`, `transport-resume`,
`transport-stop`) are **unchanged** — they are load-bearing for the S17 keyboard
tests. The `golemFocusRing()` modifier on each button is **unchanged** (D114). Pill
shape, arrangement, `weight(1f)` distribution, and the four-control behavior set are
**unchanged** — this is a label-to-glyph swap, not a transport redesign.

### (b) Status-bar inset respected — F1
Screen content is padded to respect the system status-bar inset so no screen content
renders beneath it, on **all four surfaces** (Reading View, Now Playing, Settings,
bottom nav). Implementation is the agent's within these fences: standard Compose
window-inset APIs only; **no** change to the existing theme system-bar coloring in
`theme/GolemTheme.kt` lines 87–88 (`statusBarColor` / `navigationBarColor` stay as
they are — colors are correct, only padding is missing); **no** change to
`GolemBottomNavigation.kt`; no layout change beyond the header inset.

### (c) Both-edges verification — evidence
Both top and bottom edges verified across all four surfaces. See T-018-R3.

## Out of scope — explicit fence

No transport redesign or control-set change (Play and Resume remain **separate**
controls — collapsing them was considered and rejected at design; it belongs to F-003).
No chapter navigation, progress bar, cover art, or action pills (roadmap; D68/D103 —
the shipping app never renders prototype roadmap illustration). No new settings. No
theme-token changes. No layout changes beyond the header inset. No changes to any
S17 keyboard test file (see T-018-B3 — needing to is a **halt**, not a fix).

## Pre-authorized dependency — SOW-level, one only

The three standard glyphs (▶, ❚❚, ■) plus the triangle-with-bar (▶❚) may require the
Material **extended** icon set (`androidx.compose.material:material-icons-extended`)
rather than the bundled core set. **This one dependency is pre-authorized.** If the
bundled core set suffices, add nothing. Any *other* dependency is a **halt and report**,
not an agent decision (S16 lesson). Record which path was taken in the run log.

## Acceptance criteria

**Machine-proven (tests):**

- **T-018-B1** — `BootstrapLaunchDeviceTest` passes. Its line-18 selector
  `onNodeWithText("Play")` is updated to find the control by accessible name
  (`onNodeWithContentDescription("Play")`) or by tag. This is the **only** test in the
  repo that finds a transport control by visible text — audited 2026-07-15.
- **T-018-B2** — all four transport buttons expose their correct accessible names
  ("Play", "Pause", "Resume", "Stop"). Safety net: if a glyph is wrong or missing, the
  name still declares the control.
- **T-018-B3** — **the S17 keyboard tests pass unchanged.** `KeyboardNavigationDeviceTest`
  finds transport controls by `testTag`, so the swap must not touch them. Regression
  guard for traversal, activation, and D116 focus placement. **If any S17 test requires
  modification to pass, HALT and report** — it means the change reached beyond scope.
- **T-018-B4** — screen content respects the status-bar inset on every surface,
  asserted by test rather than by eye.

**Operator-proven (evidence — captured by agent, judged by operator):**

- **T-018-R1** — six captures archived: Now Playing, Reading View, Settings × light,
  dark. **Base themes: HC off, text 100%, reduced motion off.** Proves F1 resolved
  (titles fully clear of the status bar) and the four glyphs render correctly.
- **T-018-R2** — two captures archived: Now Playing at **max text scale with HC on**,
  light and dark. Proves F2 is permanently dead (icons cannot wrap) and the top inset
  holds when content grows.
- **T-018-R3** — operator's guided both-edges walk on the S23: all four surfaces, top
  and bottom. Confirms nothing hides under the status bar or the system button bar.

## Evidence posture

Archive path: `archive/S18-inset-icon-transport/`. Unique filenames. Agent captures
T-018-R1 and T-018-R2 via `adb exec-out screencap -p`; the operator's look-check of the
archived captures happens **before** any acceptance verb, and T-018-R3 is walked on real
hardware before acceptance (look-check sequencing is enforced, not optional).

## IMP-005 fence — standing

**The agent writes no operator-acceptance verbs and no ledger entries.** All closeout
acceptance fields remain **literal placeholders** until the operator gives the actual
verb after his own look-check and device walk. Ledger entries (D117, D118) are the
design session's to author. Verification greps branch records for premature acceptance
language.

## Agent handoff mechanics

- Worktree question → **N**.
- Completion options → **3** (keep the branch).
- Branch: `feature/s18-inset-icon-transport`. **Push it.** The completion report must
  confirm the branch is pushed; verification begins with `git ls-remote --heads`.
- Never commit to main (guards block it).
- Secret-scan `SKIP` in guard output is intentional (KI-S1-001 / D78) — not a problem.
- Retry cap and scope fence apply: three failed attempts on the same wall → halt and
  report rather than improvise.

## Closeout record

- Branch pushed: _[placeholder]_
- Independent verification against real branch code: _[placeholder]_
- Operator look-check of archived captures: _[placeholder]_
- T-018-R3 both-edges walk: _[placeholder]_
- Operator acceptance: _[placeholder]_
- Merge: _[placeholder]_
