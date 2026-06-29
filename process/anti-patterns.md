---
id: ANTI-PATTERNS
tier: constitution
status: stable
updated: [FILL: YYYY-MM-DD]
if-incomplete: "Return to state/current-state.md and follow process/index.md."
---
# Anti-patterns — when one appears, stop

| Anti-pattern | Instead |
|---|---|
| Code before design | Design Session → approved Step SOW → code |
| Rewrite when an update suffices | surgical update; find exact lines first |
| Skipping a gate | short gates still run; Lite needs a grant; guards block it |
| Process exposed to the operator | keep machinery behind the curtain; plain recommendation |
| Validation confidence unstated | every validated result reports high/medium/low |
| Agent invents scope | halt + course-correct; new Step if scope changes |
| Wishlist idea in an open Step | write it to the wishlist; the Step doesn't change |
| Course-correction resolved in Build Zone | stop, report; Design resolves it |
| Decision without a D-number | number every locked decision before it leaves Design |
| "It probably works" | the declared check ran and the output is recorded |
| Full spec in context | slim index + on-demand sections |
| Stale AGENTS.md | refresh before issuing a Design Baton |
| Flat dump / god file / mixed layers | organize by domain; one responsibility; separate layers |
| Hardcoded secrets | env vars; the guard blocks the commit |
| Theme colors / font sizes hardcoded | design tokens + a scale multiplier |
| Docs stale or absent | update docs in the same commit as code |
