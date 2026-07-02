---
id: CURRENT-STATE
tier: state
status: active
updated: 2026-07-02
if-incomplete: "You are at the source of truth. If something isn't here, it isn't current."
---
# Current state — THE source of truth for "now"

- **Active version:** V0 (foundation)   **Phase:** P1 — Walking Skeleton   **Step:** S9 (next)   **current-rung:** —
- **Last committed:** s8-transport-session-hub / S8 transport commands + thin hub / git HEAD / 2026-07-02
- **Coverage target:** see reference/coverage-target.md   ·   **Test posture (active step):** automated + agent-run device

## What's happening right now
S8 transport commands + thin hub is complete and committed. The app now has
`com.golemreader.transport`: one in-app `TransportHub` singleton and thin
`TransportCommands` wrappers for play/pause/resume/stop, all writing desired playback state
through the hub. The app also has `PlaybackSession`, a dedicated-thread orchestrator that
keeps the S6 `IntentLoop`, `PlaybackProducer`, `PlaybackConsumer`, `AbortController`, and
`StarvationState` path alive over real time.

Verification passed on 2026-07-02: `./gradlew testDebugUnitTest`, then on the S23
`./gradlew installDebug installDebugAndroidTest`, fixture restore to
`/sdcard/Android/media/com.golemreader/fixtures/text/tom-sawyer.epub`, and direct
instrumentation:
`adb shell am instrument -w -e class com.golemreader.playback.PlaybackSessionDeviceTest com.golemreader.test/androidx.test.runner.AndroidJUnitRunner`.
The device test ran a real Tom Sawyer playback session over wall-clock time through
play -> pause -> resume -> seek -> stop and returned `OK (1 test)`.

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
- Full Android MediaSessionService, lock-screen/notification, audio-focus, background
  survival, and resume-after-kill routing remain deferred after S8 per D92.

## Next action
Start **Step S9 — Reading View + Now Playing (F-014/F-015)**.
