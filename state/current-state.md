---
id: CURRENT-STATE
tier: state
status: active
updated: 2026-07-13
if-incomplete: "You are at the source of truth. If something isn't here, it isn't current."
---
# Current state — THE source of truth for "now"

- **Active version:** V1 (in progress; V0 foundation complete, archived at `archive/V0-P1/`)   **Phase:** P2 — Accessible Shell (entered at G1)   **Step:** S16 — Reduced motion (SOW approved; handoff to agent)
- **Last committed:** S15 accepted and merged ff-only to main at **9c97aec** (closeout + build.gradle ratification correction recorded in `state/active/step-S15.md`); before that, S14 at a87f5be
- **Coverage target:** see reference/coverage-target.md   ·   **Test posture (active step):** automated (JVM) + agent-run device + guided-manual look-check at closeout

## What's happening right now
**S15 is done and merged at 9c97aec.** Text scaling is live: the five-step
in-app multiplier at the provider seam (D108), the A−/%/A+ stepper under
Accessibility, the third `theme_settings` KV row, the central scaling/reflow
harness over four surfaces (D109) including the HC × scale composition that
cleared S14's T-066-B4 deferral, and the D101 negative-proof guard test.
Bottom-nav labels did **not** clip at 3.0× combined scale (pixel evidence in
`archive/S15-text-scaling/run-log.md`). One recorded correction in the S15
closeout: the agent's two test-only build.gradle.kts lines were ratified at
verification, not approved during execution — future SOWs pre-authorize
test-only dependency additions required by mandated tests.

**S16 — Reduced motion (F-067) is SOW'd and handed off** on the approved scope:
motion-override at the provider seam (D110, operator-delegated), instant-jump
scroll + proven-still flip/indicator + untouched polling with the "Catching up"
polite-live-region announcement at a 500 ms hold threshold (D111, resolves
OB-015-2 under its reconciled ID), OS-Remove-animations OR in-app toggle
default (D112, resolves OB-067-3), fourth `theme_settings` KV row (D107
applied), one D104 registry entry, the D76 glow-parameter seam with defaults
reproducing today's look, and the central reduced-motion harness. The reduced
highlight path is a named T-001-C1 contributor and carries device cost
evidence. Full SOW: `state/active/step-S16.md`. D-ceiling: **D112**.

## Open items needing attention
- T-057-C1 and T-057-C2 remain owed agent-run measurements; T-057-C3 remains a contributor
  to the integrated listen-loop system-budget test at end of T3.
- KI-S3-001 remains open: `androidx.test.services` has no UID on the S23, so the
  external-cache-clear permission grant is skipped until a later step depends on that
  device path again.
- KI-S5-001 remains open: Piper segment-final `:`/`;` artifacts are expected and unfixed
  until F-044/F-045 rule-packs/voice-bound packs are built.
- T-001-C1 remains deferred until end of T3; S16's reduced-motion highlight path is a
  named contributor (must not cost more than the animated path — evidence per S16 AC9).
- Full Android MediaSessionService, lock-screen/notification, audio-focus, background
  survival, and resume-after-kill routing remain deferred after S8 per D92.
- Secret scan remains intentionally skipped until configured (KI-S1-001 / D78) —
  not a defect.
- S13 recorded deferrals: T-064-B4 → S17; T-064-R2 → G4; swipe-left → D51/D52;
  full preview strip → F-073; Library tab → F-019 phase.
- S15 carried notes: T-068-R1 full sweep → re-walked at G4 with all axes on;
  ReservedSlot dp-height trap → owned by the F-073 preview-strip family.
- S16 recorded notes: polling-interval default params read
  `GolemThemeValueSets.dark.motion` directly (harmless wart while motion is a
  shared block; owner: whichever step next touches the polling seam);
  Material-internal micro-animations covered only via the OS setting (out of
  D70 scope, recorded); `STARVATION_ANNOUNCE_HOLD_MILLIS` = 500 is a recorded
  tunable, revisited only with device evidence.
- p1 likely retains stale local branches — prune (`git fetch --prune` +
  `git branch -vv` review) next time the operator is on p1.

## Next action
Agent executes S16 on branch `s16-reduced-motion` from main @ 9c97aec per
`state/active/step-S16.md`. Then independent verification against real branch
code, operator look-check (including TalkBack announcement observation)
**before** the acceptance verb, then the operator merge ritual. After S16:
S17 (F-069 keyboard navigation), then G4.
