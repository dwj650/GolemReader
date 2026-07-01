---
id: CURRENT-STATE
tier: state
status: active
updated: 2026-06-30
if-incomplete: "You are at the source of truth. If something isn't here, it isn't current."
---
# Current state — THE source of truth for "now"

- **Active version:** V0 (foundation)   **Phase:** P1 — Walking Skeleton   **Step:** S3 (next)   **current-rung:** —
- **Last committed:** s2-storage-substrate / S2 storage substrate / git HEAD / 2026-06-30
- **Coverage target:** see reference/coverage-target.md   ·   **Test posture (active step):** automated + agent-run device

## What's happening right now
S2 storage substrate is complete and committed. The app now has the F-057 storage substrate:
precious Room database with `db_meta`, exported schemas v1/v2, imported-assets and
rebuildable locations, centralized tier placement, safe cache-clear/eviction, D31 migration
harness, and D32 addressing skeleton. D79, D80, and D81 are recorded in the decision log.
D81 used the primary KSP 2.3.5 path; no `gradle.properties` fallback and no TD-001.

## Open items needing attention
- T-057-C1 and T-057-C2 remain owed agent-run measurements; T-057-C3 remains a contributor
  to the integrated listen-loop system-budget test at end of T3.

## Next action
Start **Step S3 — F-020 Book identity**.
