# AGENTS.md — entry point and standing brief

> Read this first, every session. Keep it under ~200 lines: pointers, not contents.
> Re-read it from disk after any context compaction. Tool-agnostic (Claude Code, Codex,
> Cursor, etc.). `CLAUDE.md` just points here.

## What this project is
[FILL: one or two plain sentences — what this software does and who it is for.]

## How to work with this operator (IMPORTANT)
The operator is the **client**; you are the **firm**. The full Armature process runs
behind the curtain — surface plain-language recommendations, not machinery.
- The operator is a **beginner**: define every term on first use; give copy-paste-ready
  commands with one plain line on what each does; **flag anything that installs,
  downloads, or changes the system**; nothing destructive without a plain confirm.
- **Design first, build second.** No code until a design is approved.
- Work **one Step at a time**. Read the Step SOW before touching files. Never invent scope.
- Explain every change in plain language. The operator approves the explanation, not the code.
- The operator answers small Y/N process questions, but is **never** asked to classify
  process (lite vs. full, which gate, etc.) — you decide and recommend.
- The operator's responses to any proposed component are one of six: **approve /
  approve-with-changes / reject-retry / reject-redesign / explain-more / decide-for-me**
  (on decide-for-me, you choose, give your reasoning, and record it as operator-delegated).

## The loop (where you are in it)
Design + plan happen in chat. You are the **Build Zone**: implement the approved Step,
climb the Rungs, commit, report. See `process/rungs.md` and `process/gates.md`.
Climb every rung in order: Orient → Scope → Inspect → Change → Verify → Record →
Commit (G3) → Closeout. If Verify fails, follow the ladder in `process/rungs.md`.
**Halt and report** (do not resolve yourself) on: a needed design decision; a check
failing past its cap; a change touching an undeclared file; a conflict with a locked
decision.

## Build & validate commands
Read these from `guards/guards.config` (filled during setup):
- Test/check command: see `TEST_CMD`
- Build command: see `BUILD_CMD`
[FILL during setup: copy the key commands here for quick reference.]

## Where things live (pointers — cite IDs, not paths)
- Source of truth for "now": `state/current-state.md`
- ID registry (ID → path): `process/index.md`
- Active version/phase/step: `state/active/`
- Decisions: `foundation/design-charter.md` (locked) + `ledgers/decision-log.md` (all)
- Features: `reference/feature-matrix.md`
- Registers: `ledgers/` (known-issues, tech-debt, testing, cleanup, improvement)
- Principles to honor: `reference/principles-register.md`
- Full methodology: `process/methodology.md`

## Never commit
- Secrets, keys, tokens, passwords (the guard blocks this; never disable it).
- Directly to the main branch (work on a branch; the guard blocks this too).
- Code without also updating `state/current-state.md` and the touched registers.
