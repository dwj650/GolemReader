---
id: S8
tier: state
status: completed
updated: 2026-07-02
cross-refs: [P1, D92, D13a, D13k, D14a, D14b, D14f, F-002, F-009, F-001]
if-incomplete: "Return to state/current-state.md."
---
# Step S8 — Transport commands + thin hub

## Changelog
- v1.0.0 (2026-07-02) — proposed and approved in the same design session.

Phase: P1 · Feature(s): F-002, F-009 (thin) · current-rung: Closeout

## Statement of Work
Build the four transport commands (play/pause/resume/stop) as desired-play-state writes
(F-002), routed through a single in-app hub (F-009, thin). Critically: build the
orchestrator that keeps S6's playback pieces (`IntentLoop`, `PlaybackProducer`,
`PlaybackConsumer`, `AbortController`, `StarvationState`) running as one continuously-
alive session — S6's own device test wired these manually and synchronously inside a
test method; nothing today runs them as a live loop that responds to commands issued
over real time. No UI, no Android `MediaSessionService`, no focus/lock-screen/background/
resume-after-kill (all deferred per D92).

## Scope — files that WILL change

New package `com.golemreader.playback` (orchestrator — lives beside the engine pieces
it drives):
- `PlaybackSession.kt` — the missing glue. Runs a background loop that: periodically
  calls `IntentLoop.consumeReadyIntent()`; on a target change, calls
  `AbortController.changeTarget()`; keeps `PlaybackProducer.renderLookAheadFrom()` fed
  while playing; calls `PlaybackConsumer.playNext()` on the play/pause cadence; stops
  cleanly on `stop()`. This is what makes "press pause" mean something in real time
  instead of only in a unit test. Threading approach (dedicated thread vs. coroutine —
  no `kotlinx.coroutines` dependency exists in the project yet, so adding one is
  Codex's call to make and note) is Codex's design decision within this SOW's boundary.

New package `com.golemreader.transport`:
- `TransportCommands.kt` — C-002-1. The four public commands (`play()`, `pause()`,
  `resume()`, `stop()`) as thin wrappers that write intent through the hub — no direct
  engine manipulation, per F-002 R1.
- `TransportHub.kt` — C-009-1/C-009-2 (thin). The single in-app hub instance a
  `PlaybackSession` is wired behind; routes commands so two different callers invoking
  the same hub produce identical resulting state (satisfies the spirit of R2's caller-
  independence in-process; real cross-surface caller-independence — lock screen,
  headset — is deferred to the full F-009 build per D92).

New tests:
- `app/src/test/java/com/golemreader/playback/PlaybackSessionTest.kt` — JVM: the
  session loop correctly drives producer/consumer/abort/starvation over simulated time.
- `app/src/test/java/com/golemreader/transport/*Test.kt` — JVM: the four commands write
  the correct intent; caller-independence in-process.
- `app/src/androidTest/java/com/golemreader/playback/PlaybackSessionDeviceTest.kt` —
  on-device, agent-run: start a real session on the Tom Sawyer fixture, issue play/
  pause/resume/seek/stop **over real wall-clock time** (not a synchronous unrolled
  loop), confirm each command takes effect correctly while the session keeps running.

## Non-goals — explicitly NOT touched
- Any visible UI/buttons — F-002's own spec assigns transport *layout* to F-015 (Now
  Playing), which is S9's job, not S8's.
- Real Android `MediaSessionService`, lock-screen/notification surface (F-011),
  background/foreground-service survival (F-010), audio-focus/becoming-noisy policy
  (F-012), resume-after-kill (F-013) — all deferred per D92 to a dedicated later step.
- Navigation/seek as a user-facing feature (chapter skip, scrubber) — F-003, separate.
- Speed/volume/inter-sentence pause controls — F-005/F-007/F-006, separate.

## Decisions in play
- **D92 (this step)** — S8 scope boundary: F-002 + thin F-009 only; F-010/F-011/F-012/
  F-013 and full Media3 integration deferred to a dedicated post-G4 step. Locked,
  operator-approved.
- D13a (commands write desired-play-state, not replayed events), D13k (pause/resume
  keeps the buffer, resume is instant — already true of S6's `IntentLoop`/
  `PlaybackConsumer`; S8 just makes it reachable via commands over real time).

## Acceptance criteria (what "done" means)
- [x] T-002-P1 — the four commands route through a single hub; each writes desired-
      play-state, none replays an event sequence.
- [x] T-002-B1 — pause then resume is instant: no buffer flush, same position.
- [x] T-002-B2 — stop flushes the buffer and ends the session; F-058's saved position
      (already written independently by the storage substrate) is untouched by S8's
      stop path — S8 does not need to trigger a save itself.
- [x] T-002-B3 (in-process form) — the same command issued via two different callers
      of `TransportHub` yields identical resulting state.
- [x] T-009-P1 — exactly one hub instance; no second session possible.
- [x] T-009-B1 (in-process form) — caller-independent routing, as above.
- [x] New: the orchestrator itself — `PlaybackSession` keeps producer/consumer/abort/
      starvation running correctly across a play → pause → resume → seek → stop
      sequence issued over real elapsed time on-device, not a synchronous test-only
      unroll.

## Test posture
- Form: automated (JVM) + agent-run (device)
- Plan:
  - Automated: intent-write correctness for all four commands, caller-independence
    in-process, the session loop's behavior under simulated time.
  - Agent-run device: a real running session on the S23, commands issued with real
    delays between them, confirming the loop stays alive and responds correctly —
    this is the genuinely new proof S8 adds beyond S6/S7's synchronous test harnesses.
  - Explicitly deferred: T-002-B4-B9 (focus/noisy/lock-screen/unplug/resume — all
    F-012/F-010/F-011/F-013 territory), T-009-B3/R1 (real cross-surface routing),
    T-002-R1/T-009-R1 (the full system-integration "promise" results).
- Principle conformance: step-scoping discipline — D92 explicitly separates what's
  genuinely self-contained (commands + a live in-app hub) from what depends on Android
  system integration work not yet scheduled.

## Entry gate (G2)
- [x] Prior step closed — S7 done, merged, commit 3b5fdae.
- [x] current-state fresh — pulled 2026-07-02.
- [x] Scope + non-goals declared (above).
- [x] Operator approved — 2026-07-02, approved as-is.

## Rung tracker
- [x] Orient  - [x] Scope  - [x] Inspect  - [x] Change
- [x] Verify  - [x] Record  - [x] Commit (G3, guarded)  - [x] Closeout

## Verify result
- Result: PASS  ·  Confidence: high for JVM behavior, medium for S23 device proof  ·
  T-id: T-002-P1, T-002-B1, T-002-B2, T-002-B3, T-009-P1, T-009-B1, S8 orchestrator
- Notes: `./gradlew testDebugUnitTest` passed on 2026-07-02. S23 verification passed by
  installing app/test APKs, restoring the Tom Sawyer fixture after install, then running
  `adb shell am instrument -w -e class com.golemreader.playback.PlaybackSessionDeviceTest com.golemreader.test/androidx.test.runner.AndroidJUnitRunner`,
  which returned `OK (1 test)`. A direct `connectedDebugAndroidTest` run compiled but
  cleared the app media fixture during reinstall, matching the known fixture-restore
  pattern from S6/S7; restoring the fixture and running the installed instrumentation
  proved the S8 behavior with no code change.

## Closeout
- Committed: git HEAD / s8-transport-session-hub / 2026-07-02  ·  Next step: S9
  (Reading View + Now Playing, F-014/F-015)
