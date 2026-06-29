---
id: GATES
tier: constitution
status: stable
updated: [FILL: YYYY-MM-DD]
if-incomplete: "Return to state/current-state.md and follow process/index.md."
---
# Gates (Definitions of Done) — all blocking

Guard-backed gates pass only when the guard says so (see `guards.md`), not on the
agent's word.

- **G0 Foundation Lock** (pre-repo): prototype visual-complete (dummy data) covering V0
  + V1 roadmap; every charter decision recorded and mapped to a phase; core principles
  declared; coverage target set.
- **G1 Phase Entry**: phase decisions identified; prior retro + open IMP items read;
  steps scoped with non-goals.
- **G2 Step Entry**: prior step closed; current-state fresh; changed files + non-goals
  declared; non-trivial steps human-approved.
- **G3 Step Commit** *(guarded)*: declared check ran + recorded; touched registers
  updated; diff explained + approved; non-goals respected; no secrets; principle
  conformance confirmed; confidence level stated.
- **G4 Phase Acceptance**: build matches prototype; registers current; baseline
  archived; docs/state reconciled; phase principle conformance; coverage + confidence
  summary produced.
- **G5 Version Release** *(guarded)*: changelog written; version tagged; baselines +
  project snapshot archived; release notes prepared.

## Lite paths
The **AI** decides whether something qualifies for a Lite-Step/Lite-Phase and asks a
one-line Y/N; the operator never classifies. Record the grant (what's skipped + why).
Even a docs-only step commits.
