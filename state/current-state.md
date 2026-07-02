---
id: CURRENT-STATE
tier: state
status: active
updated: 2026-07-01
if-incomplete: "You are at the source of truth. If something isn't here, it isn't current."
---
# Current state — THE source of truth for "now"

- **Active version:** V0 (foundation)   **Phase:** P1 — Walking Skeleton   **Step:** S7 (next)   **current-rung:** —
- **Last committed:** s6-streaming-engine / S6 streaming engine / git HEAD / 2026-07-01
- **Coverage target:** see reference/coverage-target.md   ·   **Test posture (active step):** automated + agent-run device

## What's happening right now
S6 streaming engine is complete and committed. The app now has the F-001
producer/consumer/buffer spine under `com.golemreader.playback`: a RAM-only
sentence-index-tagged buffer with rendered-second depth accounting, a bounded look-ahead
producer, a consumer-owned playback gap, latest-value-wins intent handling, abort ordering,
starvation hold state, and chapter continuity. `TextPipeline` now supports chapter read-ahead
so chapter N+1 can be processed before the playhead reaches the boundary, closing OB-D48.
`SynthesisHarness` still supports the S5 one-shot `play()` helper and now exposes an
`AudioSink` for streaming consumers.

Verification passed on 2026-07-01: `./gradlew testDebugUnitTest` and the S23
`StreamingPlaybackDeviceTest`. The S23 test restored the Tom Sawyer fixture to
`/sdcard/Android/media/com.golemreader/fixtures/text/tom-sawyer.epub`, then streamed 371
Tom Sawyer chapter-5/6 sentences start-to-finish through the S6 playback path using a short
generated `VoiceEngine` tone.

## Open items needing attention
- T-057-C1 and T-057-C2 remain owed agent-run measurements; T-057-C3 remains a contributor
  to the integrated listen-loop system-budget test at end of T3.
- KI-S3-001 remains open: `androidx.test.services` has no UID on the S23, so the
  external-cache-clear permission grant is skipped until a later step depends on that
  device path again.
- KI-S5-001 remains open: Piper segment-final `:`/`;` artifacts are expected and unfixed
  until F-044/F-045 rule-packs/voice-bound packs are built.
- T-001-C1 remains deferred until end of T3; S6 records no final battery/thermal threshold.

## Next action
Start **Step S7 — Synchronized highlight (F-016)**.
