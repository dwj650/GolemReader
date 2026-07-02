---
id: S7
tier: state
status: complete
updated: 2026-07-02
cross-refs: [P1, D91, D1, D4b, D12c, D13, F-016, F-001, F-027, F-028]
if-incomplete: "Return to state/current-state.md."
---
# Step S7 — Synchronized highlight signal

## Changelog
- v1.0.0 (2026-07-02) — proposed and approved in the same design session.

Phase: P1 · Feature(s): F-016 · current-rung: Closeout

## Statement of Work
Build the highlight-position signal (F-016): as each sentence (or clause, for a
clause-split sentence) plays, publish which one is currently playing, timed from the
real rendered audio length — not a text-length guess. F-016 emits state only; it does
not render pixels (that's F-014, in S9). Confirm correctness with logged timing
cross-checked against real audio boundaries, on-device.

## Scope — files that WILL change

New package `com.golemreader.highlight`:
- `HighlightClock.kt` — C-016-1. Computes advance timing from a played segment's real
  duration (`samples.size / sampleRateHz`, the same math `StreamingBuffer.depthSeconds()`
  already uses) rather than any text-based estimate.
- `HighlightIndexMapper.kt` — C-016-2/R2/R3. Given a playing `SentenceIndex`, looks up
  its `SentenceRecord` (already carries `clauseTag: ClauseTag(parentSentenceOrdinal,
  clauseOrdinal)` from F-027/F-028) so clause-split sentences step clause-by-clause
  while staying tagged to their parent sentence.
- `HighlightStateEmitter.kt` — C-016-3/R7. Publishes current (sentence index, clause
  tag, glow parameters) for a host to read. No rendering. Glow parameters (size,
  contrast, fade — R9/D76) are exposed with fixed V1-default values; no control surface
  yet.
- `HighlightSync.kt` — C-016-4/R5. Snaps the emitted state to the new target the moment
  `AbortController.changeTarget()` fires, so seek/skip/chapter-change re-syncs the
  highlight instead of leaving it on the old sentence.

Extends existing files:
- `playback/PlaybackConsumer.kt` — `playNext()` currently calls `audioSink.play(item.audio)`
  with no outside notification. Needs a hook (callback or observable) so
  `HighlightClock` knows the instant a given segment starts playing and for how long.
  Exact shape (callback param vs. a small listener interface) is Codex's design call.
- `playback/AbortController.kt` — `changeTarget()` needs to notify `HighlightSync` of
  the new target, alongside its existing five-step abort sequence. Additive only — the
  existing stop/flush/flush/set/re-render order (D13) does not change.

New tests:
- `app/src/test/java/com/golemreader/highlight/*Test.kt` — JVM coverage of the clock,
  mapper, emitter, sync-on-abort, and the parameter-seam (T-016-B7).
- `app/src/androidTest/java/com/golemreader/highlight/HighlightSyncDeviceTest.kt` —
  on-device, agent-run: log the highlight signal's timestamped index changes during a
  real multi-chapter play-through (reuse the Tom Sawyer fixture from S6) and confirm
  they line up with the audio's real per-sentence sample boundaries.

## Non-goals — explicitly NOT touched
- Any rendering of the highlight (F-014) — S9.
- The eyes-on-screen "watch it glow in sync" confirmation (T-016-R1, literal form) —
  deferred to S9 per D91; S7 confirms by logged timing instead.
- Clause-step visual stability tuning on very long sentences (OB-016-1) — F-017, V2.
- The reduced-motion control body — F-067, separate feature.
- The V1 glow parameters' *values* are fixed defaults only — no user control surface.

## Decisions in play
- **D91 (this step)** — S7's on-device confirmation is log-based, not visual; full
  visual confirmation deferred to S9. Locked, operator-approved.
- D1 (sentence/clause-level advance from real sample lengths), D4b (shared sentence
  index across display/audio/highlight), D12c (clause tagging to parent sentence), D13
  (seek/skip/abort re-target — S7 rides S6's existing `AbortController`).

## Acceptance criteria (what "done" means)
- [x] T-016-P1 — clock, mapper, and emitter are present; a host can read the current
      index.
- [x] T-016-B1 — highlight index advances on real per-sentence audio boundaries, not a
      character/word estimate.
- [x] T-016-B2 — a clause-split sentence highlights clause-by-clause, each tagged to
      its parent sentence index.
- [x] T-016-B3 — display index, audio index, and highlight index all resolve to the
      same sentence number for a given passage.
- [x] T-016-B4 — seek/skip/chapter-change snaps the highlight to the new target
      (rides `AbortController.changeTarget()`).
- [x] T-016-B5 — behavior under the one-call-per-segment path is confirmed; note: this
      is the currently-active default (aggregation is off per OB-001-1), so this
      collapses into the same check as T-016-B1 rather than needing a separate
      injected-fallback test.
- [x] T-016-B6 — same passage, both engines (Kokoro, Piper) → same index track.
- [x] T-016-B7 — the emitted state carries size/contrast/fade fields; V1 defaults
      reproduce the V1 look-equivalent values; changing a field changes the emitted
      value with no audio re-render.
- [x] R1 (proxy, per D91) — on-device log of highlight timestamps lines up with real
      audio sample boundaries across a multi-chapter play-through, within a tight
      tolerance Codex proposes and records.

## Test posture
- Form: automated (JVM) + agent-run (device, log-based)
- Plan:
  - Automated: clock timing math, clause/parent mapping, shared-index cross-check,
    sync-on-abort, parameter-seam — maps to T-016-P1/B1/B2/B3/B4/B7.
  - Agent-run device: reuse the S6 Tom Sawyer fixture; log highlight index changes
    during playback across a chapter boundary; cross-check against real audio sample
    boundaries. Maps to T-016-B6 (both engines) and R1-by-proxy (D91).
  - Explicitly deferred: T-016-R1 in its literal visual form — carried to S9.
- Principle conformance: honest-measurement — the log-based tolerance for "lines up" is
  set from what's actually measured on-device, not assumed in advance.

## Entry gate (G2)
- [x] Prior step closed — S6 done, merged, commit d2c24bf (includes D90 record).
- [x] current-state fresh — pulled 2026-07-02.
- [x] Scope + non-goals declared (above).
- [x] Operator approved — 2026-07-02, approved as-is.

## Rung tracker
- [x] Orient  - [x] Scope  - [x] Inspect  - [x] Change
- [x] Verify  - [x] Record  - [x] Commit (G3, guarded)  - [x] Closeout

## Verify result
- Result: passed  ·  Confidence: high for JVM signal logic, medium for S23 real-engine
  timing  ·  T-id: T-016-P1/B1/B2/B3/B4/B5/B6/B7; R1 proxy per D91
- Notes:
  - RED: `./gradlew testDebugUnitTest` failed first on missing `HighlightClock`,
    `HighlightIndexMapper`, `HighlightStateEmitter`, `HighlightSync`, and playback hooks.
  - GREEN: `./gradlew testDebugUnitTest` passed on 2026-07-02.
  - Device: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.golemreader.highlight.HighlightSyncDeviceTest`
    passed on SM-S918U on 2026-07-02 after restoring Tom Sawyer plus Kokoro/Piper test
    voice assets to `/sdcard/Android/media/com.golemreader/`. A fresh rerun first failed
    because APK install churn cleared the fixture from the app media folder; restoring the
    same test assets resolved it with no code change.
  - Device evidence: the cross-chapter passage emitted the same index track for Kokoro
    and Piper: `5:217 -> 6:0`.
  - Proposed/recorded tolerance: 250 ms from each sample-derived boundary. Latest fresh
    run measured second-boundary drift at 209 ms for Kokoro and 77 ms for Piper.
  - The known `androidx.test.services` no-UID warning still appears before device tests;
    it did not block this S7 test.

## Closeout
- Committed: G3 commit for S7 synchronized highlight signal  ·  Next step: S8 (transport controls through MediaSession hub, F-002/F-009)
