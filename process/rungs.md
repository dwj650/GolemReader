---
id: RUNGS
tier: constitution
status: stable
updated: [FILL: YYYY-MM-DD]
if-incomplete: "Return to state/current-state.md and follow process/index.md."
---
# The Rungs (the step SOP)

Every Step climbs these eight in order. The step file tracks `current-rung` so an
interrupted session resumes correctly.

1. **Orient** — read `current-state` + the active step; confirm location; summarize and
   wait for the operator's acknowledgment.
2. **Scope** — declare the files that will change and the explicit non-goals.
3. **Inspect** — read every file before touching it.
4. **Change** — make one narrow change.
5. **Verify** — run the declared check; record the result + confidence.
6. **Record** — update every register the change touched.
7. **Commit** — pass G3 (and its guards), then commit.
8. **Closeout** — update state; mark the step done; name the next step.

## When Verify fails (the ladder — first match wins, top to bottom)
| Cause | Route | Limit |
|---|---|---|
| Expected failure (declared in the step) | continue | — |
| Transient (build env, network, flaky) | re-run; no code change | — |
| Local bug, scope still correct | back to **Change** | **2 attempts** |
| Needs files outside scope, or the 2 attempts failed | back to **Scope** | — |
| Wrong approach, or conflicts with a locked decision | **Halt → Design Zone** | — |

On any failure, emit a plain-language report: what broke, the exact error, the rung
you're routing to and why, and what you need from the operator. Never silently loop.
