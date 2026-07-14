---
id: S17-RUN-LOG
tier: archive
status: implementation-complete
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

## Guided closeout
- Look-check: **[OPERATOR: pending]**
- Keyboard walkthrough: **[OPERATOR: pending]**
- Acceptance verb: **[OPERATOR: pending]**
- Merge hash: **[OPERATOR: pending]**
