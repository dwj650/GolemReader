---
id: S11
tier: state
status: approved
updated: 2026-07-02
cross-refs: [P1, D97, D13k, F-001, F-004]
if-incomplete: "Return to state/current-state.md."
---
# Step S11 — End-of-book clean stop

## Changelog
- v1.0.0 (2026-07-02) — proposed and approved in the same design session, in response
  to a G4 halt report describing an infinite end-of-book audio repeat.

Phase: P1 · Feature(s): none new — fixing a defect in existing F-001/F-004 behavior ·
current-rung: Orient

## Statement of Work
Fix a real defect found by G4's on-device demonstration: on reaching the true last
sentence of a book, the live app repeats that sentence's audio forever instead of
stopping. Root cause: `PlaybackSession.runOneIteration()` treats "no next sentence"
(a null result from `nextAfter`) as "stay on the current cursor," and the render loop
re-invokes `producer.renderLookAheadFrom()` on that same stuck cursor every tick,
re-enqueueing duplicate audio each time. `ChapterContinuity.isEndOfBook()` (built in
S6 for exactly this) was never wired into the live session. `BookBootstrap` also
builds its own ad-hoc next-sentence map (`zipWithNext`) instead of using
`ChapterContinuity` — replace it as part of this fix.

## Scope — files that WILL change

- `app/src/main/java/com/golemreader/playback/PlaybackSession.kt` — required
  behavior, exact implementation shape is Codex's call:
  1. Once the producer has rendered the true last sentence, it must not re-render or
     re-enqueue that sentence again on any later tick — no duplicate audio for the
     final sentence ever enters the buffer.
  2. Once the consumer has actually played the true last sentence (not just
     rendered — played), the session stops cleanly: same teardown as the existing
     `PlayState.Stopped` path (`producer.stop()`, `flushBuffer()`,
     `consumer.flushSink()`, `isRunning = false`) — without requiring an external
     `stop()` command. This is a graceful natural completion, distinct from a
     user-initiated stop, but reuses the same teardown sequence (D13k: nothing here
     changes pause/resume/stop's existing meaning for a still-playing book).
- `app/src/main/java/com/golemreader/bootstrap/BookBootstrap.kt` — replace the
  private `zipWithNext`-based `nextAfter()` helper with one backed by
  `ChapterContinuity` (already built, already tested in S6), so detection and
  live-session wiring share one source of truth instead of two parallel
  implementations of "what comes next."

New/updated tests:
- `app/src/test/java/com/golemreader/playback/PlaybackSessionTest.kt` — add: driving
  a session through a short bounded sentence list to its true last sentence results
  in exactly one playback of the final sentence and a clean stop — no repeat, no
  further render calls after completion.
- `app/src/test/java/com/golemreader/bootstrap/BookBootstrapTest.kt` — confirm the
  bootstrap's `nextAfter` wiring is `ChapterContinuity`-backed, not the old ad-hoc map
  (a direct assertion this old code path is gone, not just that behavior looks right).

## Non-goals — explicitly NOT touched
- G4's own demonstration/archive work — that resumes once this step closes, same SOW,
  unchanged.
- Any new feature behavior beyond the fix itself.
- Multi-book/library concerns (F-019) — still out of scope, same as S10.

## Decisions in play
- **D97 (this step)** — S11 inserted to fix the end-of-book repeat defect before G4
  resumes a second time. Locked, arising directly from a Codex halt report.
- D13k (pause/resume/stop's existing meaning is unchanged by this fix — natural
  completion is a new, distinct path, not a redefinition of user-initiated stop).

## Acceptance criteria
- [ ] A JVM test drives a session through a short bounded list to its true last
      sentence: the final sentence plays exactly once, the session stops cleanly, and
      no further render/enqueue call happens afterward.
- [ ] `BookBootstrap`'s next-sentence logic is `ChapterContinuity`-backed; the old
      `zipWithNext` map is gone, not just unused.
- [ ] On-device (folded into G4's resumed demonstration, not a separate device test
      here): a real complete book played end-to-end stops cleanly at the true last
      sentence with no repeat.

## Test posture
- Form: automated (JVM) only for this step — the real on-device proof is G4's own
  resumed demonstration, not duplicated here.

## Entry gate (G2)
- [x] Prior step closed — S10 done, merged, commit 300abfe.
- [x] current-state fresh — pulled 2026-07-02.
- [x] Scope + non-goals declared (above).
- [x] Operator approved — 2026-07-02, approved via direct instruction to fix and
      proceed.

## Rung tracker
- [ ] Orient  - [ ] Scope  - [ ] Inspect  - [ ] Change
- [ ] Verify  - [ ] Record  - [ ] Commit (G3, guarded)  - [ ] Closeout

## Verify result
- Result: —  ·  Confidence: —  ·  T-id: —
- Notes: not yet run.

## Closeout
- Committed: —  ·  Next step: resume G4 (state/active/step-G4.md, unchanged)
