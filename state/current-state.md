---
id: CURRENT-STATE
tier: state
status: active
updated: 2026-07-12
if-incomplete: "You are at the source of truth. If something isn't here, it isn't current."
---
# Current state — THE source of truth for "now"

- **Active version:** V1 (in progress; V0 foundation complete, archived at `archive/V0-P1/`)   **Phase:** P2 — Accessible Shell (entered at G1)   **Step:** S13 — Settings Host, thin (SOW approved; awaiting agent implementation)   **current-rung:** Change
- **Last committed:** main @ 1a08e91 (S12 theme foundation, fast-forward merged) + this handoff commit (S13 SOW, D102–D104, prototype v0.3.0, state refresh)
- **Coverage target:** see reference/coverage-target.md   ·   **Test posture (active step):** automated (JVM) + agent-run device + guided-manual look-check at closeout

## What's happening right now
**S12 is done.** The theme foundation (tokens, dark + light value-sets, persisted
choice, live switch, D101 no-hardcode guard) is merged to main; T-065-R2 passed
by operator on-device 2026-07-08; closeout recorded in `state/active/step-S12.md`.

**S13 is active, at the Change rung.** The approved SOW is
`state/active/step-S13.md`: navigation topology (D102 — in-house route model,
bottom nav per the visual contract, Library hidden until F-019, simplified
preview-strip Reading entry replacing the S9/D93 switch), the F-064 settings
shell reading a plain-Kotlin Settings Map registry (D104, resolving OB-064-2),
the F-065 theme picker landing in a single day-one Appearance section (D103,
resolving OB-064-1), the theme write moved off the main thread, and the
adaptive launcher icon rider. The visual contract is now prototype v0.3.0
(`foundation/prototype/golem-reader-prototype-v0_3_0.jsx`), superseding v0.2.0
(retained for rollback). D-ceiling: **D104**.

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
- S13 recorded deferrals: T-064-B4 → S17; T-064-R2 → G4; swipe-left → D51/D52;
  full preview strip → F-073; Library tab → F-019 phase.

## Next action
Coding agent implements S13 from `state/active/step-S13.md` on a new branch
(`s13-settings-host`), then the operator brings the completion report back for
verification against the acceptance criteria on real branch code.
