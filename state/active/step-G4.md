---
id: G4
tier: state
status: accepted
updated: 2026-07-02
cross-refs: [P1, D95, PR-1..PR-7]
if-incomplete: "Return to state/active/phase-P1.md."
---
# G4 — Phase acceptance: on-device demonstration + baseline archive

## Statement of Work
The design-side review of G4 is complete (see `state/active/phase-P1.md`): registers
are current, docs are reconciled, principle conformance is recorded (including the
PR-7 accessibility gap, accepted per D95), and the coverage summary is written. One
piece remains, and it needs the device: a real, unattended, end-to-end demonstration
of the walking skeleton actually playing a book — through the real app, not a test
harness — packaged into the project's first archived baseline.

## Recommendation on the fixture
Every device test so far has used bounded excerpts of Tom Sawyer (a full-length
novel) to keep runs short. For this demonstration, **use a short, complete,
public-domain book instead of a Tom Sawyer excerpt** — something genuinely playable
start-to-finish in a reasonable unattended window (a short story or novella-length
public-domain EPUB, roughly 15-30 minutes of listening). This proves "plays one book
end-to-end" literally and honestly, without requiring an hours-long run to demonstrate
a promise that's already been proven piecewise across S6-S9. If acquiring/preparing
such a fixture is more friction than it's worth, running the full unabridged Tom
Sawyer to completion is the fallback — flag which path was taken in the report either
way.

## Scope — what happens

1. **Real end-to-end run.** Using the actual app (not a device test class calling
   internals directly), open a complete book, press play, and let it run
   **unattended** to the end — every chapter, no manual intervention, through the
   real `PlaybackSession`/`ReadingViewScreen`/`NowPlayingScreen` path built in S6-S9.
   Log start time, end time, sentence count, and any starvation/stall events.
2. **Real screenshots.** Capture the actual running app at least twice: Reading View
   mid-highlight (showing the lit sentence), and Now Playing mid-play (showing
   transport state + position). These stand alone as "what P1 actually looks like"
   — there's no prototype to diff against (see phase-P1.md's G4 acceptance note).
3. **Archive the baseline.** Create `archive/V0-P1/` containing:
   - `build-metadata.md` — commit hash, date, device model, book used, run duration.
   - `test-summary.md` — a pointer/short excerpt of `ledgers/testing.md`'s S1-S9
     coverage, not a full copy (one source of truth stays `ledgers/testing.md`).
   - The screenshots from step 2.
   - `run-log.md` — the unattended run's log (start/end time, sentence count,
     starvation events if any).
4. **Update registers.** `state/current-state.md` moves to reflect G4 accepted;
   `state/phase-index.md`'s G4 row marked done; `state/active/phase-P1.md` (already
   drafted, provided alongside this SOW) gets its "Baseline archived" checkbox
   completed with a pointer to `archive/V0-P1/`.

## Non-goals
- No new features. This is a demonstration and archival task, not a build step.
- No accessibility work — that's next phase's job per D95, not folded in here.
- No fixing of the recurring "APK install churn clears the fixture" environment note
  (IMP-001-adjacent) — it's known, harmless, and re-pushing the fixture already
  resolves it every time.

## Decisions in play
- D95 (G4 accepted with PR-7 recorded, not blocking).

## Acceptance criteria
- [x] A complete book played start to finish, unattended, through the real app UI,
      with no manual intervention and no uncaught starvation/stall.
- [x] At least two real screenshots captured (Reading View highlight, Now Playing
      transport state).
- [x] `archive/V0-P1/` created with build-metadata, test-summary, screenshots, and
      run-log.
- [x] Registers updated to reflect G4 accepted.

## Test posture
- Form: agent-run (device), unattended real-world run — this is itself the test; no
  new automated/JVM tests are expected for a gate-acceptance task.

## Entry gate
- [x] Design-side G4 review complete (`state/active/phase-P1.md`).
- [x] D95 locked, operator-approved.

## Closeout
- Result: pass. On 2026-07-02, the real app on SM-S918U played the staged complete
  short public-domain book content ("The Gift of the Magi") through the actual UI and
  live playback path. Playback was unattended after Play, reached natural completion,
  and the `GolemPlaybackSession` thread remained absent during the post-completion
  check. Archive: `archive/V0-P1/`.
- Committed: git HEAD  ·  Next: Phase P2 kickoff (accessibility prioritized per D95's IMP-004)
