---
id: CURRENT-STATE
tier: state
status: active
updated: 2026-06-28
if-incomplete: "You are at the source of truth. If something isn't here, it isn't current."
---
# Current state — THE source of truth for "now"

- **Active version:** V0 (foundation)   **Phase:** P1 — Walking Skeleton   **Step:** S0 (bring-up)   **current-rung:** —
- **Last committed:** main / (bring-up pending) / 2026-06-28
- **Coverage target:** see reference/coverage-target.md   ·   **Test posture (active step):** —

## What's happening right now
Repo brought up from the Armature template. All 76 design decisions are loaded and locked
(charter + spec); the D30 reconciliation gate passed; the G0 visual prototype was
lite-waived (operator-delegated, recorded in the decision-log). Foundation, feature
matrix, principles, coverage target, and registers are populated. Per-feature requirement
docs are placed into reference/requirements/ just-in-time, per step.

## Open items needing attention
- Guards not yet installed (guards/install-guards.sh) and guards.config build/test commands not yet set.
- Per-feature req docs for P1 features placed per step (none placed yet).

## Next action
Install the guards and set the Kotlin/Gradle build+test commands, then write **Step S1 —
Storage substrate (F-057)**.
