---
id: S10
tier: state
status: complete
updated: 2026-07-02
cross-refs: [P1, D96, F-001, F-002, F-009, F-016, F-020, F-027]
if-incomplete: "Return to state/current-state.md."
---
# Step S10 — App bootstrap

## Changelog
- v1.0.0 (2026-07-02) — proposed and approved in the same design session, in response
  to a G4 halt report.

Phase: P1 · Feature(s): none new — wiring existing pieces together · current-rung: Closeout

## Statement of Work
Make the real app actually run the real pipeline for one book. On launch, load a
hardcoded fixture EPUB through the existing identity + text pipeline, build a real
`PlaybackSession` wired to `TransportHub`, and pass live state (sentences, the
highlight emitter, starvation state, transport controls backed by the real session)
into `GolemReaderApp` instead of its current empty defaults. The app does not
auto-play — pressing Play in the real Now Playing screen is what starts playback,
so S8's transport commands get their first real exercise outside a test.

## Scope — files that WILL change

New package `com.golemreader.bootstrap`:
- `BookBootstrap.kt` — the single wiring point. In order: resolve the fixture EPUB
  path (a hardcoded constant — no picker, no library, that's F-019 later); compute
  identity via `BookIdentityService`; run `TextPipeline.processChapterWithReadAhead()`
  (or equivalent) to build the sentence list; construct a `VoiceEngine` (reuse S5's
  Kokoro or Piper adapter — Codex's call which, note it in the report); build
  `PlaybackProducer`/`PlaybackConsumer`/`AbortController`/`StarvationState`/
  `IntentLoop`, wrap into a `PlaybackSession` using the existing `asDriver()`
  adapters; call `TransportHub.attach(session)`; call `session.start()`. Returns
  everything `GolemReaderApp` needs to receive as real (non-default) parameters.

Extends existing files:
- `MainActivity.kt` — calls `BookBootstrap` in `onCreate()` and passes its result into
  `GolemReaderApp(...)` instead of relying on the composable's defaults.

New tests:
- `app/src/test/java/com/golemreader/bootstrap/BookBootstrapTest.kt` — JVM: bootstrap
  produces a non-empty sentence list, a `TransportHub` with a session attached, and a
  `HighlightStateEmitter`/`StarvationState` pair backed by that same session (not
  fresh, disconnected instances).
- `app/src/androidTest/java/com/golemreader/BootstrapLaunchDeviceTest.kt` — on-device,
  agent-run: launch the real `MainActivity`, confirm Reading View shows real text (not
  empty), press Play through the real Now Playing controls, confirm the highlight
  advances and audio plays — using the actual launched app, not internals called
  directly.

## Non-goals — explicitly NOT touched
- Book picker / library browsing (F-019) — a hardcoded fixture path is enough to
  unblock G4; choosing *which* book is later, separate work.
- Auto-play on launch — pressing Play is the real transport path being exercised;
  auto-starting would skip proving S8's commands work from the actual UI.
- Any new feature behavior — this step only wires existing, already-proven pieces
  together in a place nothing has connected them before.
- G4's own demonstration/archive work — that resumes once this step closes, using
  the same G4 SOW already written.

## Decisions in play
- **D96 (this step)** — S10 inserted before G4 resumes, scoped to bootstrap wiring
  only. Locked, operator-approved, arising directly from a Codex halt report.

## Acceptance criteria
- [x] `BookBootstrap` produces a non-empty sentence list from a real fixture EPUB.
- [x] The produced `TransportHub` has a real `PlaybackSession` attached (not the
      unattached default).
- [x] `HighlightStateEmitter` and `StarvationState` passed to `GolemReaderApp` are the
      same instances the session updates — not fresh, disconnected ones.
- [x] On-device: launching the real app shows real book text in Reading View
      immediately (not an empty screen).
- [x] On-device: pressing Play in the real Now Playing screen starts real audio and
      the highlight advances — through the actual launched app.

## Test posture
- Form: automated (JVM) + agent-run (device)
- Plan:
  - Automated: bootstrap wiring correctness — non-empty sentences, attached session,
    shared (not fresh) state instances.
  - Agent-run device: real launch → real text visible → real Play press → real audio
    + highlight advance, all through the actual app, not a test class calling
    internals.
- Principle conformance: this step exists specifically to close the gap between
  "proven in a test" and "actually works when you open the app" — the same gap G4's
  own acceptance criteria are designed to catch.

## Entry gate (G2)
- [x] Prior step closed — S9 done, merged, commit 890a2b8.
- [x] current-state fresh — pulled 2026-07-02.
- [x] Scope + non-goals declared (above).
- [x] Operator approved — 2026-07-02, approved as-is.

## Rung tracker
- [x] Orient  - [x] Scope  - [x] Inspect  - [x] Change
- [x] Verify  - [x] Record  - [x] Commit (G3, guarded)  - [x] Closeout

## Verify result
- Result: pass  ·  Confidence: high for JVM wiring, medium for S23 real launch/audio  ·  T-id: T-S10
- Notes: RED first failed on missing `BookBootstrap`. GREEN passed with
  `./gradlew testDebugUnitTest`; `./gradlew assembleDebugAndroidTest` passed.
  On the S23, the Tom Sawyer fixture was already present, Piper assets were restored to
  `/sdcard/Android/media/com.golemreader/test-voices/piper/`, `./gradlew installDebug
  installDebugAndroidTest` passed, and direct instrumentation
  `adb shell am instrument -w -e class com.golemreader.BootstrapLaunchDeviceTest com.golemreader.test/androidx.test.runner.AndroidJUnitRunner`
  returned `OK (1 test)`. Bootstrap uses the S5 Piper adapter.

## Closeout
- Committed: 9076959 (`s10 app bootstrap`)  ·  Next step: resume G4
  (state/active/step-G4.md, already written)
