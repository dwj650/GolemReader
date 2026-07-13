---
id: CURRENT-STATE
tier: state
status: active
updated: 2026-07-13
if-incomplete: "You are at the source of truth. If something isn't here, it isn't current."
---
# Current state — THE source of truth for "now"

- **Active version:** V1 (in progress; V0 foundation complete, archived at `archive/V0-P1/`)   **Phase:** P2 — Accessible Shell (entered at G1)   **Step:** S13 — Settings Host, thin (done; S14 next, not started)   **current-rung:** Closeout
- **Last committed:** S13 completion on branch `s13-settings-host` (this commit); main remains @ 29767de (S13 handoff)
- **Coverage target:** see reference/coverage-target.md   ·   **Test posture (active step):** automated (JVM) + agent-run device + guided-manual look-check at closeout

## What's happening right now
**S12 is done.** The theme foundation (tokens, dark + light value-sets, persisted
choice, live switch, D101 no-hardcode guard) is merged to main; T-065-R2 passed
by operator on-device 2026-07-08; closeout recorded in `state/active/step-S12.md`.

**S13 is done on branch `s13-settings-host`.** The route
model, two-item bottom navigation, Reading overlay/back path, plain-Kotlin Settings
Map, generic Settings shell, segmented Theme picker, off-main theme write, current-
sentence preview strip, launcher resources, and automated/device tests are implemented.
The full JVM suite, debug build, Android-test build, and no-hardcode guard passed. On
SM-S918U, navigation/Settings/Reading and live Dark device tests passed; Dark survived
force-stop/relaunch; the final bottom-nav and adaptive launcher icons rendered; and the
required screenshots were archived under `archive/S13-settings-host/`. The operator
reviewed and approved the screenshot set on 2026-07-13. D-ceiling: **D104**.

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
S14 — High contrast (F-066) is next and not started. Return to the Design Zone to ground
and approve its SOW before any S14 code changes.
