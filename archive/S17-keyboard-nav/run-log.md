---
id: S17-RUN-LOG
tier: archive
status: accepted
updated: 2026-07-14
---
# S17 keyboard-navigation run log

## Test-path decision
- Task 1 Robolectric Tab spike: **FAIL** — the two-Button test compiled, but
  `performKeyInput { pressKey(Key.Tab) }` did not move focus under Robolectric.
- Pre-authorized fallback used: traversal, order, activation, and destination-focus
  placement tests run as device `androidTest` coverage on SM-S918U.
- Token, contrast, shared-ring, and no-animation contracts remain JVM tests.
- Test-only build additions: none.

## Agent-run evidence
- `KeyboardNavigationDeviceTest`: 4/4 passed on SM-S918U (Android 16), 2026-07-14.
  Covers forward/backward D115 traversal, Enter/Space equivalence, permanent preview →
  Reading → Back focus placement, and one nav-tab destination placement assertion.
- `dark-focus-ring.png`: Dark focus ring on Play.
- `hc-dark-focus-ring.png`: hcDark focus ring on High contrast.
- AC7 cost box-check: **N/A — not a T-001-C1 contributor**. Focus state and drawing
  are event-driven and add no steady-state playback polling or synthesis work.
- AC8 inspection: no preference added, no `theme_settings` write added, no D104
  registry entry changed, and no Settings entry added.
- Known environment notice: `androidx.test.services` has no UID on this S23
  (KI-S3-001); the targeted keyboard tests still ran and passed.

## Guided closeout (recorded by the operator's design session, 2026-07-14)
- Look-check: **passed** — operator viewed both archived captures (dark ring on
  Play; hcDark yellow ring on the High-contrast toggle) before the walkthrough.
- Keyboard walkthrough: **passed** — full T-069-R1 script on the S23 with a
  paired Bluetooth keyboard: traversal both directions on all surfaces,
  Enter/Space parity, HC ring visibility, and D116 placement (Settings → System;
  preview → Reading → Back) all confirmed by the operator.
- Acceptance verb: **approve** — given 2026-07-14, after look-check and
  walkthrough, in that order.
- Merge hash: recorded in the next session's bundle after the ff-only ritual
  (S16 precedent — the closeout commit itself becomes main's tip).
