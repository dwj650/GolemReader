---
id: CURRENT-STATE
tier: state
status: active
updated: 2026-07-13
if-incomplete: "You are at the source of truth. If something isn't here, it isn't current."
---
# Current state — THE source of truth for "now"

- **Active version:** V1 (in progress; V0 foundation complete, archived at `archive/V0-P1/`)   **Phase:** P2 — Accessible Shell (entered at G1)   **Step:** S14 — High contrast (SOW approved; awaiting agent implementation)   **current-rung:** Change
- **Last committed:** main @ e1fe9dd (S13 settings host, fast-forward merged) + this handoff commit (S14 SOW, D105–D107, state refresh)
- **Coverage target:** see reference/coverage-target.md   ·   **Test posture (active step):** automated (JVM) + agent-run device + guided-manual look-check at closeout

## What's happening right now
**S13 is done and merged at e1fe9dd.** Bottom navigation (Now Playing ·
Settings), the Settings Map registry, the settings shell with the theme picker
in its Appearance home, the off-main-thread theme write, the tappable
current-sentence Reading entry, and the adaptive launcher icon are all on main.
Operator approved the four archived screenshots 2026-07-13; closeout recorded
in `state/active/step-S13.md`.

**S14 is active, at the Change rung.** The approved SOW is
`state/active/step-S14.md`: two pure-data high-contrast value-sets — hcDark
and hcLight (D106) — authored to the AAA target (D105, resolving OB-066-1);
the resolver gains a high-contrast dimension; the HC preference stored as a
second key-value row in `theme_settings` with **no schema change** (D107);
the toggle registered into the Settings Map as a one-entry Accessibility
registration (the first live proof of D104); and the F-066 central contrast
test harness (D69) that every other surface defers to. Writes stay suspend +
IO-dispatched per the S13 rule. D-ceiling: **D107**.

## Open items needing attention
- T-057-C1 and T-057-C2 remain owed agent-run measurements; T-057-C3 remains a contributor
  to the integrated listen-loop system-budget test at end of T3.
- KI-S3-001 remains open: `androidx.test.services` has no UID on the S23, so the
  external-cache-clear permission grant is skipped until a later step depends on that
  device path again.
- KI-S5-001 remains open: Piper segment-final `:`/`;` artifacts are expected and unfixed
  until F-044/F-045 rule-packs/voice-bound packs are built.
- T-001-C1 remains deferred until end of T3; S16's reduced-motion highlight path is a
  named contributor (must not cost more than the animated path).
- Full Android MediaSessionService, lock-screen/notification, audio-focus, background
  survival, and resume-after-kill routing remain deferred after S8 per D92.
- Secret scan remains intentionally skipped until configured (KI-S1-001 / D78) —
  re-confirmed at S13 verification; not a defect.
- S13 recorded deferrals: T-064-B4 → S17; T-064-R2 → G4; swipe-left → D51/D52;
  full preview strip → F-073; Library tab → F-019 phase.
- S14 recorded deferrals: T-066-B4 scaling half → S15; T-066-R1 full sweep →
  closeout look-check, re-walked at G4.

## Next action
Coding agent implements S14 from `state/active/step-S14.md` on a new branch
(`s14-high-contrast`), then the operator brings the completion report back for
verification against the acceptance criteria on real branch code.
