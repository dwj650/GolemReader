---
id: INDEX
tier: constitution
status: stable
updated: [FILL: YYYY-MM-DD]
if-incomplete: "This IS the map. If an ID isn't here, it doesn't exist yet."
---
# Index — folder map + ID registry

## Folder map
- foundation/ — prototype, charter, design spec (locked at G0)
- process/ — how we work (this Constitution)
- state/ — where we are now (read first); active/ holds live V/P/S files
- ledgers/ — what happened (registers + logs)
- reference/ — what we're building (matrix, requirements, principles, coverage, wishlist)
- docs/ — specs/ (design docs) and plans/ (step plans)
- archive/ — baselines + snapshots (never overwritten)
- templates/ — blanks for V/P/S/decision/issue/requirement
- guards/ — the enforcement layer (tailored to the stack at setup)
- src/ tests/ scripts/ data/ — the application

## ID registry  (the single move-point: cite IDs, not paths; update ONE line here when a file moves)
| ID prefix | Thing | Current home |
|---|---|---|
| V / P / S | version / phase / step | state/ (active in state/active/) |
| D | design decision | foundation/design-charter.md (locked) · ledgers/decision-log.md (all) |
| F / C | feature / component | reference/feature-matrix.md · reference/requirements/ |
| KI | known issue / limitation | ledgers/known-issues.md |
| TD | tech debt | ledgers/tech-debt.md |
| T | test | ledgers/testing.md |
| CL | cleanup item | ledgers/cleanup.md |
| IMP | improvement / lesson | ledgers/improvement-register.md |
| PR | core principle | reference/principles-register.md |

## Specific-item registry (add a row when you create a stable ID)
| ID | Title | Path |
|----|-------|------|
| F-057 | Storage tiers requirements | reference/requirements/F-057-Storage-Tiers-Requirements-v1_0_0.md |
| F-057-delta-1 | Storage tiers v1.0.1 delta | reference/requirements/F-057-Storage-Tiers-Requirements-v1_0_1-delta.md |
| C-057-1 | Room database schema (precious) | app/src/main/java/com/golemreader/storage/ |
| C-057-2 | Imported-assets file store + manifest home | app/src/main/java/com/golemreader/storage/ |
| C-057-3 | Rebuildable cache/RAM store | app/src/main/java/com/golemreader/storage/ |
| C-057-4 | Tier placement rule | app/src/main/java/com/golemreader/storage/ |
| C-057-5 | Cache-clear / eviction routine | app/src/main/java/com/golemreader/storage/ |
| T-057-P1 | First-run storage substrate initialization | app/src/test/java/com/golemreader/storage/ |
| T-057-P2 | Placement-rule coverage | app/src/test/java/com/golemreader/storage/ |
| T-057-P3 | Precious Room schema build/init | app/src/test/java/com/golemreader/storage/ |
| T-057-B1 | Cache-clear preserves precious | app/src/test/java/com/golemreader/storage/ |
| T-057-B2 | Eviction removes rebuildable only | app/src/test/java/com/golemreader/storage/ |
| T-057-B3 | In-use / near-playhead eviction protection | app/src/test/java/com/golemreader/storage/ |
| T-057-B4 | Physical separation path/device check | app/src/test/java/com/golemreader/storage/ |
| T-057-B5 | D31 migration harness | app/src/test/java/com/golemreader/storage/ |
| T-057-C1 | Precious write/read latency measurement | ledgers/testing.md |
| T-057-C2 | Precious footprint measurement | ledgers/testing.md |
| T-057-C3 | Listen-loop budget contributor | ledgers/testing.md |
| T-057-R1 | Precious restart/process survival device check | app/src/androidTest/java/com/golemreader/storage/ |
| T-057-R2 | OS/user cache-clear device check | app/src/androidTest/java/com/golemreader/storage/ |
