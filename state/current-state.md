---
id: CURRENT-STATE
tier: state
status: active
updated: 2026-07-14
if-incomplete: "You are at the source of truth. If something isn't here, it isn't current."
---
# Current state — THE source of truth for "now"

- **Active version:** V1 (in progress; V0 foundation complete, archived at `archive/V0-P1/`)   **Phase:** P2 — Accessible Shell (entered at G1)   **Step:** S17 — accepted 2026-07-14; awaiting operator merge ritual
- **Last committed:** S16 merged to `main` at **b507c75** (ff-only, 2026-07-14); feature branch deleted per ritual
- **Coverage target:** see reference/coverage-target.md   ·   **Test posture (active step):** automated JVM token/contracts + pre-authorized S23 fallback for traversal/activation (Task-1 spike failed) + guided keyboard walkthrough at closeout
- **D-ceiling: D116**

## What's happening right now
**S16 is done and merged at b507c75.** Reduced motion is live: the motion-override
at the provider seam (D110), instant-jump highlight scroll with proven-still flip
and static starvation indicator, the "Catching up" polite-live-region announcement
at a 500 ms hold (D111, resolves OB-015-2), the OS-Remove-animations-OR-in-app
default (D112, resolves OB-067-3), the fourth `theme_settings` KV row, and the D76
glow seam with defaults reproducing the prior look. Operator look-check and live
TalkBack check passed 2026-07-14 before the acceptance verb; the agent's
pre-written acceptance records were corrected on-branch (candidate IMP-005).

**S17 — Keyboard navigation (F-069) is implemented, independently verified, and operator-accepted 2026-07-14** (look-check + keyboard walkthrough preceded the verb). SOW v1.0.2
(`state/active/step-S17.md`), grounded against main @ b507c75. Scope: `focusRing`
token across all four value-sets with 3:1 HC floor + one shared no-animation ring
modifier applied to all seven interactive-control files (D114); declared
traversal orders, content-first/nav-last, Tab/Shift-Tab contract (D115); standard
destination focus placement on the first D115 control (D116); central
keyboard test over all four current surfaces, resolving OB-069-2 and absorbing
T-064-B4 (D113); no stored preference (F-069 §7 — always-on, no fifth KV row, no
D104 entry). The Robolectric Tab spike failed to move focus, so the pre-authorized
S23 fallback ran and passed 4/4; JVM token/ring contracts pass. Dark and hcDark
evidence is archived under `archive/S17-keyboard-nav/`. T-069-B5 (onboarding) deferred
to the F-070 phase with owner recorded. Operator has a Bluetooth keyboard; the
closeout is a guided keyboard-only walkthrough (script in the SOW). Per D100,
S17 is the last step before G4.

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
- S13 recorded deferrals: T-064-B4 → **lands in S17 (in the active SOW)**;
  T-064-R2 → G4; swipe-left → D51/D52; full preview strip → F-073; Library tab →
  F-019 phase.
- S15 carried notes: T-068-R1 full sweep → re-walked at G4 with all axes on;
  ReservedSlot dp-height trap → owned by the F-073 preview-strip family.
- S16 recorded notes: polling-interval default params read
  `GolemThemeValueSets.dark.motion` directly (harmless wart; owner: whichever step
  next touches the polling seam — explicitly NOT S17); Material-internal
  micro-animations covered only via the OS setting (out of D70 scope, recorded);
  `STARVATION_ANNOUNCE_HOLD_MILLIS` = 500 is a recorded tunable, revisited only
  with device evidence.
- S17 recorded deferral: T-069-B5 (onboarding keyboard-operable, D74) → the phase
  that builds F-070 (D113).
- OB-069-1 (screen-reader/TalkBack depth) remains out of P2; owner: future
  accessibility grooming.
- p1 likely retains stale local branches — prune (`git fetch --prune` +
  `git branch -vv` review) next time the operator is on p1. Origin is clean
  (verified 2026-07-14: only `main` on remote).

## Next action
Operator runs the approved ff-only merge ritual for `s17-keyboard-navigation`
into `main`. **G4 phase acceptance follows in its own
session after S17 closes.**
