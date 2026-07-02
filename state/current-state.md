---
id: CURRENT-STATE
tier: state
status: active
updated: 2026-07-02
if-incomplete: "You are at the source of truth. If something isn't here, it isn't current."
---
# Current state — THE source of truth for "now"

- **Active version:** V0 (foundation)   **Phase:** P1 — Walking Skeleton   **Step:** G4 (next)   **current-rung:** —
- **Last committed:** s11-end-of-book-stop / S11 End-of-book clean stop / git HEAD / 2026-07-02
- **Coverage target:** see reference/coverage-target.md   ·   **Test posture (active step):** automated + agent-run device

## What's happening right now
S11 End-of-book clean stop is implemented and ready for G4 to resume. The live
`PlaybackSession` now receives `ChapterContinuity` end-of-book detection from
`BookBootstrap`; once the producer has rendered the true final sentence, later ticks do
not re-render or re-enqueue it, and once the consumer actually plays that final sentence
the session runs the same teardown sequence as stop (`producer.stop()`, buffer flush,
sink flush, `isRunning = false`). `BookBootstrap` no longer keeps a private
`zipWithNext` next-sentence map.

Verification passed on 2026-07-02: RED first failed on missing
`PlaybackSession.isEndOfBook`, then the focused S11 JVM tests passed. Full configured
verification also passed: `./gradlew testDebugUnitTest` and `./gradlew assembleDebug`.
The real on-device proof remains part of resumed G4, per S11's test posture.

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
Resume **G4 — Phase acceptance gate: plays one book end-to-end**.
