---
id: CURRENT-STATE
tier: state
status: active
updated: 2026-07-02
if-incomplete: "You are at the source of truth. If something isn't here, it isn't current."
---
# Current state — THE source of truth for "now"

- **Active version:** V0 (foundation)   **Phase:** P1 — Walking Skeleton   **Step:** G4 (accepted)   **current-rung:** Closeout
- **Last committed:** g4-phase-acceptance / G4 Phase acceptance archive / git HEAD / 2026-07-02
- **Coverage target:** see reference/coverage-target.md   ·   **Test posture (active step):** automated + agent-run device

## What's happening right now
G4 Phase acceptance is complete. On 2026-07-02, the real app on the S23 Ultra played
the staged complete short public-domain book content ("The Gift of the Magi") through
the actual `MainActivity` / `BookBootstrap` / `PlaybackSession` / Reading View / Now
Playing path. Playback was unattended after the Play press, reached the true final
sentence, stopped naturally, and stayed stopped during the post-completion check. The
baseline package is archived at `archive/V0-P1/` with build metadata, run log,
test-summary pointer, and screenshots.

S11's end-of-book fix was confirmed in place before the run: `PlaybackSession` now uses
`isEndOfBook` to prevent final-sentence re-render/re-enqueue and to run clean teardown
after the final played sentence, and `BookBootstrap` wires `nextAfter` and end-of-book
detection through `ChapterContinuity` instead of the old private `zipWithNext` map.

## Open items needing attention
- T-057-C1 and T-057-C2 remain owed agent-run measurements; T-057-C3 remains a contributor
  to the integrated listen-loop system-budget test at end of T3.
- KI-S3-001 remains open: `androidx.test.services` has no UID on the S23, so the
  external-cache-clear permission grant is skipped until a later step depends on that
  device path again.
- KI-S5-001 remains open: Piper segment-final `:`/`;` artifacts are expected and unfixed
  until F-044/F-045 rule-packs/voice-bound packs are built.
- T-001-C1 remains deferred until end of T3; S6 records no final battery/thermal threshold.
- Full Android MediaSessionService, lock-screen/notification, audio-focus, background
  survival, and resume-after-kill routing remain deferred after S8 per D92.

## Next action
Kick off **P2 G1** with accessibility prioritized per D95 / IMP-004.
