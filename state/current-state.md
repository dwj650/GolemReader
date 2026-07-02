---
id: CURRENT-STATE
tier: state
status: active
updated: 2026-07-02
if-incomplete: "You are at the source of truth. If something isn't here, it isn't current."
---
# Current state — THE source of truth for "now"

- **Active version:** V0 (foundation)   **Phase:** P1 — Walking Skeleton   **Step:** S8 (next)   **current-rung:** —
- **Last committed:** s7-highlight-signal / S7 synchronized highlight signal / git HEAD / 2026-07-02
- **Coverage target:** see reference/coverage-target.md   ·   **Test posture (active step):** automated + agent-run device

## What's happening right now
S7 synchronized highlight signal is complete and committed. The app now has
`com.golemreader.highlight`: `HighlightClock` computes highlight timing from real rendered
sample lengths, `HighlightIndexMapper` resolves the shared `SentenceIndex` back to the
`SentenceRecord` clause tag, `HighlightStateEmitter` exposes the current readable highlight
state plus V1 glow parameters, and `HighlightSync` snaps the state to abort/seek/skip targets.
`PlaybackConsumer` now reports segment-start audio to the signal path before sink playback,
and `AbortController.changeTarget()` notifies highlight sync without changing the existing
stop/flush/flush/set/re-render order.

Verification passed on 2026-07-02: `./gradlew testDebugUnitTest` and the S23
`HighlightSyncDeviceTest`. The S23 test restored the Tom Sawyer fixture plus Kokoro/Piper
test voices to `/sdcard/Android/media/com.golemreader/`, then played a real cross-chapter
Tom Sawyer passage through both engines. Both engines emitted the same highlight index track
(`5:217 -> 6:0`), with logged transition drift within the recorded 250 ms tolerance
(latest run: Kokoro 209 ms, Piper 77 ms).

## Open items needing attention
- T-057-C1 and T-057-C2 remain owed agent-run measurements; T-057-C3 remains a contributor
  to the integrated listen-loop system-budget test at end of T3.
- KI-S3-001 remains open: `androidx.test.services` has no UID on the S23, so the
  external-cache-clear permission grant is skipped until a later step depends on that
  device path again.
- KI-S5-001 remains open: Piper segment-final `:`/`;` artifacts are expected and unfixed
  until F-044/F-045 rule-packs/voice-bound packs are built.
- T-001-C1 remains deferred until end of T3; S6 records no final battery/thermal threshold.
- T-016-R1 remains deferred in its literal visual form until S9 per D91; S7 supplied the
  log-based proxy check against real audio sample boundaries.

## Next action
Start **Step S8 — transport controls through MediaSession hub (F-002/F-009)**.
