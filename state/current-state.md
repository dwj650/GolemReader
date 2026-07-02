---
id: CURRENT-STATE
tier: state
status: active
updated: 2026-07-02
if-incomplete: "You are at the source of truth. If something isn't here, it isn't current."
---
# Current state — THE source of truth for "now"

- **Active version:** V0 (foundation)   **Phase:** P1 — Walking Skeleton   **Step:** G4 (next)   **current-rung:** —
- **Last committed:** s10-app-bootstrap / S10 App bootstrap + closeout / git HEAD / 2026-07-02
- **Coverage target:** see reference/coverage-target.md   ·   **Test posture (active step):** automated + agent-run device

## What's happening right now
S10 App bootstrap is complete and committed. Launching `MainActivity` now builds a real
`BookBootstrap` result from the hardcoded Tom Sawyer fixture, computes identity through
`BookIdentityService`, runs `TextPipeline.processChapterWithReadAhead()`, creates a
Piper-backed `PlaybackSession`, attaches it to `TransportHub`, starts the session paused,
and passes the live sentence/highlight/starvation/transport objects into
`GolemReaderApp`. The app still does not auto-play; pressing Play in Now Playing resumes
the real session.

Verification passed on 2026-07-02: RED first failed on missing `BookBootstrap`, then
`./gradlew testDebugUnitTest` passed. `./gradlew assembleDebugAndroidTest` passed.
On the S23, the Tom Sawyer fixture was present at
`/sdcard/Android/media/com.golemreader/fixtures/text/tom-sawyer.epub`, Piper assets were
restored to `/sdcard/Android/media/com.golemreader/test-voices/piper/`,
`./gradlew installDebug installDebugAndroidTest` passed, and direct instrumentation:
`adb shell am instrument -w -e class com.golemreader.BootstrapLaunchDeviceTest com.golemreader.test/androidx.test.runner.AndroidJUnitRunner`
returned `OK (1 test)`.

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
