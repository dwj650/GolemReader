---
id: CURRENT-STATE
tier: state
status: active
updated: 2026-07-02
if-incomplete: "You are at the source of truth. If something isn't here, it isn't current."
---
# Current state — THE source of truth for "now"

- **Active version:** V0 (foundation)   **Phase:** P1 — Walking Skeleton   **Step:** G4 (next)   **current-rung:** —
- **Last committed:** s9-reading-nowplaying / S9 Reading View + Now Playing / git HEAD / 2026-07-02
- **Coverage target:** see reference/coverage-target.md   ·   **Test posture (active step):** automated + agent-run device

## What's happening right now
S9 Reading View + Now Playing is complete and committed. The app now has a minimal
visible Reading View that renders `SentenceRecord.display`, highlights the shared
sentence index from `HighlightStateEmitter.currentState()`, and scroll-follows highlight
advance. The app also has a minimal Now Playing screen with book/position text, four
transport buttons wired through `TransportCommands`, empty reserved slots for F-073/F-075,
and a visible text buffering indicator backed by `StarvationState.isBuffering`.

Because the existing highlight and buffering sources are synchronous getters, S9 uses a
local 100 ms Compose polling bridge in the two screens. This should be replaced by a real
observable source only if a later step needs it.

Verification passed on 2026-07-02: RED first failed on missing S9 UI APIs, then
`./gradlew testDebugUnitTest` passed. D94 approved adding Compose UI test-only
dependencies in `app/build.gradle.kts`; `./gradlew assembleDebugAndroidTest` passed.
On the S23, `./gradlew installDebug installDebugAndroidTest` passed, the Tom Sawyer
fixture was restored to
`/sdcard/Android/media/com.golemreader/fixtures/text/tom-sawyer.epub`, and direct
instrumentation:
`adb shell am instrument -w -e class com.golemreader.ui.ReadingAndNowPlayingDeviceTest com.golemreader.test/androidx.test.runner.AndroidJUnitRunner`
returned `OK (1 test)`. The device test asserts against the rendered Compose semantics
tree that the highlighted Tom Sawyer sentence is displayed, then advances and displays a
later highlighted sentence.

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
Start **G4 — Phase acceptance gate: plays one book end-to-end**.
