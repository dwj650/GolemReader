---
id: P3
tier: state
status: entered (G1)
updated: 2026-07-18
cross-refs: [D119, D120, D121, D122, D123, IMP-001, IMP-003, IMP-005, IMP-006, IMP-007, D100, D102, D113]
if-incomplete: "Return to state/current-state.md."
---
# Phase P3 — A Real Library

A meaningful build (Gate G1 in, G4 out): **import a book from your phone, see it
on a shelf, search a 10,000-book library, open a book, and resume where you left
off — and losing a file never loses the book.**

> **Version v1.0.0** — first version of this document.
> Changelog: G1 entry drafted at the P3 G1 session (2026-07-18), grounded against
> repo main @ 12fe1ad (clone + real-code read per IMP-003) and the F-019 v1.0.1,
> F-021/F-022/F-024/F-058 v1.0.0 specs.

## Entry (G1)

- [x] **Decisions for this phase identified:** D119 (scope + feature set + fence),
      D120 (step ladder S19–S24), D121 (two-door import model: system pickers,
      one-time folder scan, batch rules), D122 (day-1 shelf search; ~10k-book
      operator library recorded; budgets re-priced), D123 (prototype v0.4.0 is
      the frozen visual contract, superseding v0.3.0/D103). Per-step build
      decisions expected en route: default active-source rule (OB-021-1),
      in-progress threshold (OB-024-2), bookmark note format (OB-024-1),
      availability probing strategy (OB-022-1), re-prompt placement (OB-022-2),
      long-running-import job design (OB new, S20), skipped-file reporting
      (OB new, S20), 10k scroll fixture + Paging 3 evaluation (S21), Book
      Details entry affordance ⓘ-vs-long-press (S23), position flush constant
      (F-058 R3, S22).
- [x] **Prior phase retro + open IMP items read:** IMP-006 is the only open row —
      the docs-with-code guard vs. IMP-005 contradiction — and is taken as this
      phase's first step (S19), before any agent code commit can hit the wall.
      Standing rows carried into every P3 SOW and session: IMP-001 (branch
      verification before multi-command pastes), IMP-003 (ground in real spec +
      real code; verify against real branch code, never the report), IMP-005
      (agents write NO project-record content; closeout fields stay literal
      placeholders), IMP-007 (spike-first gating when test placement depends on
      an unproven harness capability).
- [x] **Steps scoped with non-goals** — ladder below; each step still receives a
      full SOW with file scope and acceptance criteria before handoff.

## Grounding findings that shaped this scope (IMP-003)

1. **No import path exists.** The app plays one adb-pushed fixture
   (`tom-sawyer.epub`, chapter 5, hardcoded in `BookBootstrap`). F-018's parsing
   half exists (S4); its user-facing import half was never built. P3 names it.
2. **Runtime identity is in-memory.** `BookBootstrap` uses `InMemoryBookIdentityDao`,
   not the Room-backed DAO (which exists and is tested but is never touched by the
   live app). The open-a-book path is a rebuild, not a tweak.
3. **F-024 hard-depends on F-058** (derived states compute from position). The
   phase index had tentatively parked F-058 in P4+; it is pulled into P3 (D119).
4. **Operator library scale is ~10,000 books** (recorded at this G1, D122). This
   re-prices T-019-C1 (10k-row fixture, not "hundreds") and makes the first
   folder import a long-running background job (S20 design question).

## Steps (flat IDs continue from P2: S19–S24)

**S19 — Guard reconciliation (IMP-006).** Add an agent-writable record path (the
step run log under `archive/`) to `STATE_PATHS` in `guards/guards.config`, so the
docs-with-code guard and IMP-005 stop being mutually exclusive; write "skipping or
disabling any guard is always a halt" into the agent's standing instructions
(`AGENTS.md`). Process-only. Expected lite grant (AI-recommended, operator Y/N at
step entry). Non-goals: no app code, no guard behavior change beyond the path list.

**S20 — Book intake (F-018 import half · F-021 records + attach rule · F-019
catalog + metadata).** Two import doors via Android's own pickers (D121): Choose
files (multi-select `ACTION_OPEN_DOCUMENT`) and Import a folder (one-time
`ACTION_OPEN_DOCUMENT_TREE` scan of every EPUB). Per file: identity hash →
attach-as-source if matching (never a duplicate shelf entry) or new catalog
entry → title/author/cover extracted and stored. Batch rules: progress
indication; a file that fails to parse is skipped and reported, never fatal.
Switches the live app from the in-memory identity DAO to the Room-backed one.
Schema migration under the D31 harness. Design session must answer the
long-running-job question (~10k first import: survives screen-off/app-switch,
resumable). **This step may split at its design session** (data layer vs. import
job) — expected, not a scope change. Non-goals: no sync/watched folder (one-time
scan only, D121); no in-app file browser (system pickers only); no shelf UI.

**S21 — The shelf (F-019 home screen).** Library tab activates (owner: this
phase, per D102). Shelf lists books with cover/title/author + state badge slot
(graceful without F-024 until S23, per OB-019-3); **day-1 search over title or
author** (D122); sort recent · title · author · state; empty/first-run state;
tap-to-open replaces the fixture bootstrap as the app's entry path. Joins the
central keyboard test (D113 pattern: new surfaces join when built), the reflow
proof (D109 deferred-to contract), and the D101 no-hardcode guard. T-019-C1
budget: smooth scroll on a **10,000-row fixture** on the S23; operator input
recorded: evaluate **Paging 3** first (operator shipped a 10k-scale library with
it before). Requires prototype v0.4.0 committed (D123). Non-goals: collections /
series / richer ordering (F-062/F-063, V2); in-book search (F-076, V2);
by-folder view (Wishlist).

**S22 — Resume (F-058).** Position held in memory during playback; flushed on
pause / background / interrupt / voice-switch / stop plus a ~10s safety tick
(final constant at build); restored on open; book-level, source-independent,
never-destructive (D31). Non-goals: reparse remap (F-023-owned); no
resume-after-kill of *playback* (D92 unchanged — this is the position record,
not process survival).

**S23 — States + thin Book Details (F-024).** finished/favorite/timestamps
stored; new/in-progress **derived, never stored**; bookmarks anchored to the
composite sentence index; hosted on a thin Book Details shell (S13 settings-shell
pattern); shelf badges appear. Entry affordance (ⓘ vs long-press) decided at this
step's design session. New surface joins keyboard/reflow/no-hardcode harnesses.
Non-goals: reparse migration tests T-024-B4/B5 (defer, owner F-023); metadata
editing (F-026, V2).

**S24 — Losing a file never loses the book (F-022 · F-021 selector UI).**
Availability check on open/access; unavailable badge, book and precious data
retained always; auto-relink on same-hash reappearance; re-prompt on broken URI;
relink accepts only a hash-matching file (wrong book rejected visibly);
active-source selector on Book Details. Non-goals: background availability
scanning (OB-022-1 default is lazy-at-access unless its design session decides
otherwise).

**G4 — Phase acceptance.** App vs. the D123 (v0.4.0) contract; all four
accessibility axes stacked on the new surfaces; 10k-fixture evidence on the S23;
registers reconciled; baseline archived at `archive/V1-P3/`.

## Fence (out of P3, on the record)

- **F-023 (reparse)** — nothing in P3 upgrades the parser; T-024-B4/B5 defer
  with owner F-023.
- **F-070 (onboarding)** — D100's condition ("library **+ voice import**") is
  only half-met by P3; stays deferred with T-069-B5. Recorded, not forgotten.
- **F-062/F-063** (collections/series/richer ordering), **F-076** (in-book
  search) — V2 by existing decision.
- **By-folder shelf view** — Wishlist (operator's framing preserved there);
  nothing in P3 forecloses it (source locations already stored by F-021).
- **Watched-folder sync** — explicitly not built; folder import is a one-time
  scan (D121).
- **Standing deferrals unchanged:** full MediaSession/background/lock-screen/
  audio-focus/resume-after-kill set (D92); F-003 transport rework (incl. the
  D118 Play/Resume collapse question); voice manager (F-048 family); F-073
  preview-window delta candidate (parked at
  `reference/F-073-preview-window-delta-candidate.md`, R2 stands, resolves at
  the F-073 design session).
