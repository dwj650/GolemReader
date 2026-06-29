---
id: COLLABORATION
tier: constitution
status: stable
updated: [FILL: YYYY-MM-DD]
if-incomplete: "Return to state/current-state.md and follow process/index.md."
---
# Zones, actors, batons

## Zones
- **Design** (chat assistant) — decide what to build and why; produce locked decisions
  and the approved Step SOW. No code here.
- **Build** (coding agent, supervised) — implement the approved Step exactly; climb the
  Rungs; commit; never invent scope.
- **Validation** (human; agent assists) — confirm correctness; record results.

## Actors (RACI)
Human = accountable everywhere (approves by understanding). Chat Assistant = Design Zone,
recommends; reviews the agent's work *for* the operator. Coding Agent = Build Zone,
executes; self-explains every change; never makes design decisions.

## Batons (a partial baton blocks the handoff)
- **Design Baton** (Design→Build): approved Step SOW + current AGENTS.md + design doc +
  fresh current-state.
- **Build Baton** (Build→Validation): committed diff on a branch + plain-language diff
  explanation + Verify record + updated registers + G3 passed.
- **Validation Baton** (Validation→Design): result (T-id) + pass/fail + confidence +
  step done + next named.
- **Completion Report** (end of phase) and **Issue Report** (on Halt) — see
  response-contract.md.

## The six operator responses to any component
approve · approve-with-changes · reject-retry · reject-redesign · explain-more ·
decide-for-me (AI chooses, gives reasoning, records it as operator-delegated).
