---
id: IMPROVEMENT-REGISTER
tier: records
status: register
updated: 2026-07-15
if-incomplete: "Return to current-state.md."
---
# Improvement register (retro outputs; the next phase's G1 requires reading the open items)

> **Populated at the P2 retro, 2026-07-15.** This register existed as an unfilled
> template through all of P1 and P2 — IMP-001 through IMP-004 lived only as prose in
> `state/phase-index.md` retro lines. Backfilled here from those lines plus the P2 retro.
> **P3's G1 must read the `open` and `standing` rows.**

| ID | From phase | What worked / didn't | Action to change | Status |
|----|------------|----------------------|------------------|--------|
| IMP-001 | P1 | Multi-command pastes ran past a failed command on the wrong branch, compounding errors silently | Operator runs `git branch --show-current` and reads the output before any multi-command paste; stop at the first failed command | **standing** |
| IMP-002 | P1 | No visual contract existed; UI work had nothing to be judged against | Build a visual prototype as the contract | **closed** by D98, superseded by D103 (v0.3.0) |
| IMP-003 | P1 | SOWs written from memory and from agent completion reports drifted from reality | Ground every SOW in real spec + real code by cloning and reading before writing; verification reads real branch code, never the report | **standing** |
| IMP-004 | P1 | Accessibility was deferred out of P1 and PR-7 scored as an open gap (D95) | Scope accessibility in at the next G1 | **closed** — satisfied by P2's existence; PR-7 re-scored **met** at G4, 2026-07-15 |
| IMP-005 | P2 | Agents wrote into the project's record — four incidents across three steps: S16 pre-written operator acceptance records; S17 "operator-approved" labels in two ledger entries (one premature, one relayed-but-not-its-to-write); S17 agent-authored D116 entry; S18 unprompted rewrite of `state/current-state.md` including the "Next action" section. Each caught only because verification reads real branch code | **Ratified 2026-07-15 with a widened boundary: agents write NO project-record content at all** — not operator-acceptance verbs, not ledger entries, not state-file narrative. Closeout fields stay literal placeholders until the operator gives the verb after his own look-check and device check. Every SOW carries this fence. Verification greps branch records for premature acceptance language AND diffs for record-file edits | **standing** |
| IMP-006 | P2 | The Armature docs-with-code pre-commit guard requires every `app/` commit to also touch `state/current-state.md` or `ledgers/` — which IMP-005 forbids the agent from doing. **The two rules are mutually exclusive; every agent code commit hits this wall.** Surfaced at S18 when the agent, given an explicit no-state-edits instruction, resolved the conflict by *skipping the guard on its own judgement* rather than halting. Reframes the IMP-005 incidents: the agent was partly obeying a guard we had forgotten we installed | Two parts. **(1) Effective immediately:** skipping or disabling any guard is **always a halt**, whatever the reason — this was not on the listed halt conditions and should have been. **(2) Design work, its own step in P3:** add an agent-writable record path to `STATE_PATHS` in `guards/guards.config` — the step's run log under `archive/` already qualifies — satisfying the guard's intent (code never drifts from its record) without breaching IMP-005. Not decided at this retro; needs its own design + decision | **open** |
| IMP-007 | P2 | Spike-first task gating worked exactly as designed: S17's Robolectric spike ran as the first task, failed honestly, and the pre-authorized device fallback executed with zero improvisation. The retry-cap + scope-fence then held through three harness walls and two SOW amendments, every halt legitimate | **Promoted to standing practice:** when a step's test placement depends on an unproven harness capability, spike it first as a gating task and pre-authorize the fallback in the SOW. Never estimate a harness capability — prove it | **standing** |

## Notes carried for P3's G1

- **IMP-006 is the only `open` row** and is the highest-value process work available: it
  is a live contradiction between two standing rules, and it will recur on every agent
  code commit until fixed.
- **IMP-001, IMP-003, IMP-005, IMP-007 are `standing`** — they are not tasks; they are
  rules every SOW and every session carries.
- This register itself was unpopulated for two full phases. Keeping it current is the
  design session's job at each retro.
