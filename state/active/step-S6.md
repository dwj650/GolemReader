---
id: S6
tier: state
status: complete
updated: 2026-07-01
cross-refs: [P1, D90, D11, D12i, D13a-n, D36, D46, D48, D87, D89, F-001, F-004, F-027]
if-incomplete: "Return to state/current-state.md."
---
# Step S6 — Streaming engine: continuous chapter playback

Phase: P1 · Feature(s): F-001, F-004 · current-rung: Closeout

## Statement of Work
Build the producer/consumer/buffer streaming core (F-001) and the automatic
chapter-to-chapter continuity behavior that rides on it (F-004), so the app plays a real
multi-chapter book continuously — start to finish across at least one real chapter
boundary — without manual intervention between sentences. Extend the S4 text pipeline so
the next chapter's sentences are ready before the producer reaches the boundary
(resolves OB-D48).

## Scope — files that WILL change

New package `com.golemreader.playback`:
- `StreamingBuffer.kt` — RAM-only, sentence-index-tagged buffer; depth reported in
  rendered seconds (Σ samples ÷ sample rate), never a text-length estimate (R2/R3).
- `PlaybackProducer.kt` — look-ahead synthesizer; pulls sentences from `TextPipeline`
  output, calls `SynthesisHarness.synthesize()` per sentence, writes finished audio into
  the buffer; small-first ramp-up after start/seek (R5).
- `PlaybackConsumer.kt` — pulls from the buffer and plays; owns the inter-sentence gap.
- `IntentLoop.kt` — the two-value convergent loop (desired sentence index, desired
  play-state); latest-value-wins coalescing; debounce for rapid inputs (R7/R8).
- `AbortController.kt` — on a real target change: stop → flush buffer → flush audio
  sink → set new target → re-render small-first (R9).
- `StarvationState.kt` — graceful hold + exposed buffering flag when the consumer
  outruns the producer (R11); intent stays live during a hold (R16).
- `ChapterContinuity.kt` — F-004: chapter end is not an interrupt; pre-render across the
  boundary; end-of-book stops cleanly with no phantom render past the last sentence.

Extends existing files:
- `text/TextPipeline.kt` — today `processChapter()` runs one chapter, synchronously, on
  request. Needs a way to run chapter N+1 ahead of the playhead, at low priority, while
  chapter N is still playing (closes OB-D48). Exact mechanism (background scheduling vs.
  simple read-ahead call) is Codex's design call within this SOW's boundary.
- `audio/SynthesisHarness.kt` — `play()` currently blocks the calling thread with
  `Thread.sleep()` for the clip's duration. That single-shot harness assumption doesn't
  hold once a consumer is pulling continuously from a buffer; harness needs to separate
  "synthesize" from "play" so the new `PlaybackConsumer` owns playback timing.

New tests:
- `app/src/test/java/com/golemreader/playback/*Test.kt` — JVM/automated coverage of the
  intent loop, abort sequence, starvation hold, buffer depth accounting, chapter
  continuity logic.
- `app/src/androidTest/java/com/golemreader/playback/StreamingPlaybackDeviceTest.kt` —
  on-device, agent-run: continuous playback across a real chapter boundary.

## Non-goals — explicitly NOT touched
- Transport buttons / MediaSession hub (F-002, F-009) — S8.
- Synchronized highlight (F-016) — S7.
- Aggregation / batched synthesis + slice-verification fallback (C-001-6) — locked
  default stays one-call-per-segment (OB-001-1); no batching code path built now.
- Final integrated battery/thermal budget thresholds (T-001-C1's real numbers) — these
  land at end of T3 once more contributors exist to sum (per current-state.md).
- Position persistence across app kill (F-058) — pause/resume in S6 only needs to keep
  the buffer *within* a session, not survive a kill.
- Voice identity, hot-swap, Voice Manager UI — already deferred by D89.

## Decisions in play
- **D90 (this step)** — S6 scope boundary: F-001 core spine + F-004 cross-chapter
  continuity + text-ahead wiring in scope; aggregation, final budget thresholds,
  persistence, and transport/highlight UI deferred. Locked, operator-approved.
- D11 (producer/consumer/buffer shape), D12i (one-call-per-segment default), D13a-n
  (intent loop, abort, starvation hold, no in-place mutation), D36 (composite sentence
  index — already implemented, S6 consumes it), D46 (intent stays live in starvation
  hold), D48 (cross-chapter pre-render depends on text-ahead), D87 (S4's single-chapter
  boundary — this step is what extends past it), D89 (voice boundary — unaffected,
  S6 consumes `VoiceEngine` as-is).

## Acceptance criteria (what "done" means)
- [x] R1/R2/R3 — producer/consumer/buffer exist; buffer is RAM-only; depth is real
      rendered seconds, never a text-length guess.
- [x] R5/R15 — small first batch after start/seek; producer never renders beyond the
      bounded look-ahead window (no mass-render).
- [x] R7/R8 — a burst of position changes converges to one final target
      (latest-value-wins); scrubber-style rapid input is debounced to a single re-render.
- [x] R9 — a real target change follows the exact abort order: flush buffer → flush
      sink → set target → re-render small-first.
- [x] R10 — pause/resume within a session is instant; buffer is kept, not re-rendered.
- [x] R11/R16 — producer falling behind triggers a graceful hold (no glitch); a skip/seek
      made during the hold is still honored once synthesis catches up.
- [x] R13 — no already-buffered audio is ever edited in place; render-affecting changes
      flush forward from the next boundary.
- [x] F-004 R1/R2/R3/R4 — a chapter ending does not interrupt playback; the next
      chapter's opening is already rendered before the boundary is reached; the sentence
      index increments across the boundary with no reset; the last sentence of the last
      chapter stops cleanly with no phantom render past it.
- [x] OB-D48 closed — text pipeline demonstrably runs chapter N+1 ahead of the playhead
      before the producer reaches the boundary; recorded with evidence, not asserted.
- [x] On-device (S23): a real multi-chapter fixture (Tom Sawyer, ≥2 chapters) plays start
      to finish across the boundary with no audible gap, stutter, or starvation.

## Test posture
- Form: automated (JVM) + agent-run (device)
- Plan:
  - Automated: intent loop convergence/debounce, abort sequence ordering, starvation
    hold + intent-survives-hold, buffer depth accounting, no-in-place-mutation,
    chapter-boundary-is-not-an-interrupt logic, end-of-book clean stop — all mechanically
    checkable, maps to T-001-B1/B2/B3/B6/B8/B11/B12 and T-004-P1/B1/B2/B4.
  - Agent-run device: T-001-B10 (starvation hold recovers on refill, throttled
    artificially) and T-004-B3/T-004-R1 (real multi-chapter listen-through on the S23,
    seamless across a real chapter boundary).
  - Explicitly deferred: T-001-C1 (full battery/thermal budget — thresholds set later),
    T-001-B4 (slice-verification — no batching path exists yet to verify).
- Principle conformance: honest-measurement (no thresholds invented for the deferred
  budget test — it stays TBD until end of T3, per current-state.md's own note).

## Entry gate (G2)
- [x] Prior step closed — S5 done, merged, commit b1f5d85.
- [x] current-state fresh — pulled 2026-07-01.
- [x] Scope + non-goals declared (above).
- [x] Operator approved — approved in chat at Orient, Scope, and S6 implementation design.

## Rung tracker
- [x] Orient  - [x] Scope  - [x] Inspect  - [x] Change
- [x] Verify  - [x] Record  - [x] Commit (G3, guarded)  - [x] Closeout

## Verify result
- Result: passed  ·  Confidence: high for JVM logic, medium for S23 device continuity  ·
  T-id: T-001-B1/B2/B3/B6/B8/B10/B11/B12; T-004-P1/B1/B2/B3/B4/R1
- Notes:
  - `./gradlew testDebugUnitTest` passed on 2026-07-01.
  - `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.golemreader.playback.StreamingPlaybackDeviceTest`
    passed on SM-S918U on 2026-07-01 after restoring the Tom Sawyer fixture to
    `/sdcard/Android/media/com.golemreader/fixtures/text/tom-sawyer.epub`.
  - Device evidence: `StreamingPlaybackDevice` logged
    `S6 streamed 371 Tom Sawyer chapter-5/6 sentences start-to-finish.`
  - The device test uses the real Tom Sawyer two-chapter text path and a short generated
    `VoiceEngine` tone to exercise streaming continuity without requiring a long
    Kokoro/Piper listen pass in S6.
  - The known `androidx.test.services` no-UID warning still appears before device tests;
    it did not block this S6 test.

## Closeout
- Committed: G3 commit for S6 streaming engine  ·  Next step: S7 (synchronized highlight, F-016)
