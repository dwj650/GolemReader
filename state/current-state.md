---
id: CURRENT-STATE
tier: state
status: active
updated: 2026-07-18
if-incomplete: "You are at the source of truth. If something isn't here, it isn't current."
---
# Current state — THE source of truth for "now"

- **Active version:** V1 (in progress; V0 foundation archived at `archive/V0-P1/`)   **Phase:** P3 — A Real Library — **ENTERED at G1, 2026-07-18** (full entry: `state/active/phase-P3.md`)   **Next:** S19 (guard reconciliation, IMP-006), own session
- **Last committed:** P3 G1 closeout on `main` (this commit). Prior tip: P2 closeout at **12fe1ad**; S17 at **7726eed**
- **Coverage target:** see reference/coverage-target.md   ·   **Test posture:** no step active — P3 entered, S19 not yet opened
- **D-ceiling: D123**

## What's happening right now
**Phase P3 — A Real Library — entered at G1, 2026-07-18.** The meaningful build:
import a book from the phone, see it on a shelf, search a ~10,000-book library, open
a book, and resume where you left off — and losing a file never loses the book.
Scope locked as **D119** (F-019, F-021, F-022, F-024, **F-058 pulled in from P4+**,
the real import flow, a thin Book Details shell; F-023 and F-070 fenced out on the
record). Ladder locked as **D120**: S19 → S24, G4. Import model locked as **D121**
(two system-picker doors; folder import is a one-time scan, not sync; batch
progress + skip-and-report). **D122** records the operator's ~10k-book library as
standing context: day-1 shelf search (title/author), T-019-C1 re-priced to a
10,000-row scroll fixture, first folder import designed as a long-running job;
operator input to S21 — evaluate **Paging 3** first (prior 10k-scale success in
the Cedar project). **D123** freezes prototype **v0.4.0** as the visual contract
(supersedes v0.3.0/D103; adds Library home + thin Book Details; drafts 1–3
retained).

**G1 grounding findings (IMP-003, against main @ 12fe1ad):** no user-facing import
exists (only the adb-pushed fixture in `BookBootstrap`); the live app runs on the
**in-memory** identity DAO, not the tested Room-backed one; F-024 hard-depends on
F-058. All three reshaped scope — the candidate list alone would have missed them.

## Open items needing attention
- **S19 is next and first for a reason:** IMP-006's guard contradiction hits every
  agent code commit until `STATE_PATHS` gains the agent-writable run-log path under
  `archive/`. Expected lite grant, decided at step entry. Skipping or disabling any
  guard remains **always a halt**.
- **S20 carries the phase's two heaviest design questions:** the long-running
  first-import job (~10k books, plausibly tens of minutes to an hour; must survive
  screen-off/app-switch and be resumable) and skipped-file reporting. **S20 may
  split at its design session** — expected, not a scope change.
- **F-019 v1.0.2 delta owed at S21's design session** recording the day-1 search
  requirement (D122).
- **F-073 spec delta candidate (NOT locked)** — unchanged; parked at
  `reference/F-073-preview-window-delta-candidate.md`; F-073 is not in P3; R2
  stands until its design session.
- T-057-C1/C2 remain owed agent-run measurements; T-057-C3 and T-001-C1 remain
  contributors to the end-of-T3 integrated budget tests.
- KI-S3-001 open (no UID for `androidx.test.services` on the S23); KI-S5-001 open
  (Piper `:`/`;` artifacts await F-044/F-045); D92 deferral set unchanged
  (MediaSessionService, lock-screen, audio focus, background survival,
  resume-after-kill routing); secret scan intentionally unconfigured
  (KI-S1-001/D78).
- **Deferred items standing:** T-069-B5 → F-070 phase; OB-069-1 → accessibility
  grooming; ReservedSlot dp-height trap → F-073 family; polling-interval
  default-params wart → next step touching that seam; swipe-left → D51/D52.
- p1 likely retains stale local branches — prune (`git fetch --prune` +
  `git branch -vv`) next time on p1. **T14** was the machine in use at this G1.

## Method notes worth carrying
- **The candidate list is not the scope.** Three of P3's defining facts (no import
  path, in-memory DAO at runtime, the F-058 hard dependency) were only visible in
  real code and full spec text. G1s ground before they propose (IMP-003).
- **Operator context can re-price a phase.** The ~10k-book fact (D122) surfaced
  mid-G1 in conversation and moved a test budget by two orders of magnitude.
- **The persisted-preference staging trap** and **gates find what look-checks
  can't** — both carried from P2, both still true.

## Next action
**S19 — guard reconciliation (IMP-006)**, in its own session: design session →
SOW → agent build. Read `state/active/phase-P3.md` and the improvement register's
open + standing rows first.
