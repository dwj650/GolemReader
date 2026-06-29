# Golem Reader

An **Armature** project. Armature is a methodology for building software solo with AI;
the canonical reference is `process/methodology.md` (which points to the full spec).

## Where to start
- **Building with this repo?** An AI agent should read `AGENTS.md` first, then
  `state/current-state.md`.
- **You (the operator)?** You don't operate this machinery directly — your AI assistant
  and coding agent run it. Start by following the Armature **setup manual**, which walks
  you through tailoring this template to your project and installing the guards.

## The 30-second map
- `state/current-state.md` — the single source of truth for *where we are now* (read first).
- `process/` — how we work (rungs, gates, guards, contracts, standards).
- `ledgers/` — what has happened (decisions, issues, tests, changelog…).
- `reference/` — what we're building (features, requirements, principles, prototype).
- `guards/` — the enforcement layer (pre-built; tailored to your stack during setup).
- `templates/` — blanks for new versions, phases, steps, decisions, issues, requirements.

Placeholders look like `[FILL: ...]`. Find them all with:
`grep -rn "\[FILL" . --exclude-dir=.git`
