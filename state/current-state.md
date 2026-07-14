---
id: CURRENT-STATE
tier: state
status: active
updated: 2026-07-13
if-incomplete: "You are at the source of truth. If something isn't here, it isn't current."
---
# Current state — THE source of truth for "now"

- **Active version:** V1 (in progress; V0 foundation complete, archived at `archive/V0-P1/`)   **Phase:** P2 — Accessible Shell (entered at G1)   **Step:** S15 — Text scaling (implementation complete; independent verification pending)   **current-rung:** Commit
- **Last committed:** S15 handoff bundle on `s15-handoff` → merged ff-only to main; before that, S14 merged at a87f5be
- **Coverage target:** see reference/coverage-target.md   ·   **Test posture (active step):** automated (JVM) + agent-run device + guided-manual look-check at closeout

## What's happening right now
**S14 is done and merged at a87f5be.** High contrast is live: hcDark/hcLight
value-sets at the D105 AAA ratios, the D106 resolver dimension, the D107
key-value preference, the Accessibility settings section, and the D69 central
contrast harness with its negative proof. Operator approved all four archived
captures 2026-07-13; the actual look-check sequence is recorded in
`state/active/step-S14.md`.

**S15 — Text scaling (F-068) is implemented on `s15-text-scaling`.** The five-step
multiplier is applied only at `GolemThemeProvider`; the IO-dispatched third KV row,
Accessibility stepper, central JVM harness, and four SM-S918U evidence captures are
complete. At Android 2.0 × app 1.5, all D109 surfaces reflowed; bottom-nav labels did
not clip. Final JVM, build, no-hardcode, and diff checks passed; G3 remains before push.
D-ceiling: **D109**.

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
  not a defect.
- S13 recorded deferrals: T-064-B4 → S17; T-064-R2 → G4; swipe-left → D51/D52;
  full preview strip → F-073; Library tab → F-019 phase.
- S14 recorded deferrals: T-066-B4 scaling half → **owned by S15's harness (in
  the approved SOW)**; T-066-R1 full sweep → re-walked at G4.
- S15 recorded notes: ReservedSlot dp-height trap → owned by the F-073
  preview-strip family; bottom nav max-scale clipping, if evidenced on device,
  returns as a design question with the screenshot in hand.
- p1 likely retains stale local branches — prune (`git fetch --prune` +
  `git branch -vv` review) next time the operator is on p1.

## Next action
Run fresh G3 verification, commit and push `s15-text-scaling`, then keep the branch
for independent verification. Guided T-068-R1 look-check remains for closeout/G4.
