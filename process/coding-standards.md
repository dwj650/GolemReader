---
id: CODING-STANDARDS
tier: constitution
status: stable
updated: [FILL: YYYY-MM-DD]
if-incomplete: "Return to state/current-state.md and follow process/index.md."
---
# Coding standards (universal; the agent follows, the assistant explains)

- **Structure**: organize by feature/layer, not a flat dump; separate presentation,
  logic, data; one responsibility per file; centralize shared types/constants.
- **Naming**: descriptive, consistent; name by what a thing does.
- **Design**: single responsibility; composition over inheritance; small functions;
  explicit over implicit; abstractions at boundaries; derive, don't duplicate.
- **Error handling**: fail fast and loud; handle at the right layer; structured logging.
- **Testing**: pyramid (many unit, fewer integration, fewest e2e); tests with the code;
  deterministic; test behavior not implementation; a first-run pass is suspect.
- **Version control**: small atomic commits; feature branches; main always deployable;
  docs in the same commit as code.
- **Security**: never hardcode secrets; validate external input; least privilege; patch deps.
- **Dependencies**: pin versions; prefer well-maintained; no heavy dep for a trivial need.
- **Performance**: measure before optimizing.

## Surgical-update discipline
Read first → find the exact location → check dependents → prefer a targeted update over a
rewrite when local (~<20 lines, <5 places). Reserve rewrites for structural change.
