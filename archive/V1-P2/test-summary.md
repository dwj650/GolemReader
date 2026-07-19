---
id: ARCHIVE-V1-P2-TEST-SUMMARY
tier: archive
status: accepted
updated: 2026-07-15
---
# V1-P2 test summary

Source of truth: `ledgers/testing.md`.

P2 added **high-confidence JVM coverage** for the accessibility contracts: theme tokens
and all four value-sets (light, dark, hcDark, hcLight) with palette values pinned;
contrast floors proven by test, including the `focusRing` ≥3:1 high-contrast floor; the
text-scaling multiplier and reflow proof across four surfaces; reduced-motion plumbing at
the provider seam, the proven-still highlight flip, and the starvation announcement
logic; persistence of the three stored preferences; the no-hardcode guard; transport
accessible names; and the status-bar inset contract across all four surfaces.

P2 added **medium-confidence S23 Ultra device proof** where the JVM harness could not
reach: keyboard traversal, activation, and destination-change focus placement
(`KeyboardNavigationDeviceTest`, 4/4). This placement was **not** improvised — S17's
first task was a Robolectric spike that failed honestly (synthetic Compose key events do
not move focus under Robolectric), and the SOW's pre-authorized device fallback ran. The
device suite uses an explicit `InputModeManager` keyboard-mode request as test
scaffolding; **genuine keyboard-mode entry was covered by the operator's guided
walkthrough on real hardware with a paired Bluetooth keyboard**, and that honesty note is
recorded in the S17 run log rather than papered over.

P2 added **operator-verified human-judgment coverage** that no machine test replaces:
per-step look-checks (S12–S18), the S17 full T-069-R1 keyboard-only walkthrough, and at
the G4 gate — the D103 contract comparison, T-064-R2, the **T-068-R1 all-axes composition
sweep** (HC × max scale × reduced motion × keyboard, across all four surfaces during live
playback — the one scenario no single step ever exercised), and S18's T-018-R3 both-edges
device walk. Every look-check preceded its acceptance verb; that sequencing is enforced.

**PR-7 (accessibility) re-scored MET at G4**, from *open, gap recorded (D95)* at the P1
gate — the first principle to move from gap to met across a phase boundary.

## Known remaining test gaps (tracked, deliberate)
- Screen readers / TalkBack depth: **OB-069-1**, out of P2 by record; owner is future
  accessibility grooming. (S16's reduced-motion announcement did receive a live TalkBack
  check at its closeout; this gap is about systematic depth, not zero coverage.)
- Onboarding accessibility: **T-069-B5** deferred to the F-070 phase, owner recorded (D113).
- Library surface: does not exist until F-019 (D102) — nothing to test.
- Non-phone geometries: untested.
- Carried from P1 and still open: integrated battery/thermal budget at end of T3
  (**T-001-C1**, with S16's reduced-motion highlight path a named contributor);
  **T-057-C1/C2** storage cost readings.
- **The S18 pre-commit test gate was skipped by the agent** (IMP-006). The gap was closed
  by the operator running `./gradlew testDebugUnitTest` himself — `BUILD SUCCESSFUL`.
  Recorded because the enforcement failed, not the tests.
