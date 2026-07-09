---
id: CURRENT-STATE
tier: state
status: active
updated: 2026-07-08
if-incomplete: "You are at the source of truth. If something isn't here, it isn't current."
---
# Current state — THE source of truth for "now"

- **Active version:** V1 (in progress; V0 foundation complete, archived at `archive/V0-P1/`)   **Phase:** P2 — Accessible Shell (entered at G1)   **Step:** S12 — Theme foundation (implemented; device tier completed after follow-up)   **current-rung:** Closeout
- **Last committed:** latest HEAD on `s12-theme-foundation` / theme foundation + device proof / 2026-07-08
- **Coverage target:** see reference/coverage-target.md   ·   **Test posture (active step):** automated (JVM/Robolectric) + agent-run device + guided-manual look-check

## What's happening right now
Phase P2 ("Accessible Shell") was entered at G1 on 2026-07-02: six steps (S12–S17)
delivering the accessibility priority recorded at P1's G4 (D95 / IMP-004) — theme
foundation, settings shell, high contrast, text scaling, reduced motion, keyboard
navigation — ending at P2's own G4. F-070 (onboarding) is explicitly deferred by
D100 to the phase that builds library + voice import. Full entry:
`state/active/phase-P2.md`.

**S12 is implemented on branch `s12-theme-foundation`.** The build now has a
`com.golemreader.theme` token system across color, typography, shape, elevation,
spacing, and motion; dark + light value-sets; a composition-level provider; Room
precious schema v4 with `theme_settings`; follow-system default; current UI files
migrated to tokens; and the D101 no-hardcode guard wired into `guards/gate-check.sh`.
Automated JVM/Robolectric, build, clean guard, seeded guard-failure, and agent-run
device checks passed on 2026-07-08. Device screenshots are archived at
`archive/S12-theme-foundation/`.

Ledger housekeeping repaired in this commit: the D98 entry (prototype v0.2.0 as the
frozen visual contract) was referenced by commit 8f7d6f7 but never landed in the
decision log — found at P2's G1 and written now, alongside D99 (F-065 first), D100
(phase shape), and D101 (no-hardcode guard mechanism). D-ceiling: **D101**.

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

## Next action
Operator reviews the S12 completion report and archived screenshots before merge/G4
visual comparison.
