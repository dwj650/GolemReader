---
id: KNOWN-ISSUES
tier: ledger
status: active
updated: 2026-06-28
if-incomplete: "Full open-boundary roster is design-spec.md Appendix A (every OB + owner)."
---
# Known issues & open boundaries

The complete open-boundary roster (every OB, owner, resolve-at) is **design-spec.md
Appendix A**. None block design; each resolves at build or later grooming. The
**build-facing OBs relevant to Phase P1 (walking skeleton)** are surfaced here:

| ID | What's open | Resolve at |
|---|---|---|
| OB-001-1 | Aggregation keep/kill as streaming default (long-run ear A/B) | build (D26) |
| OB-001-2 | Integrated listen-loop budget thresholds (T-001-C1) | build / D30 |
| OB-002-1 | Media3 resume-after-kill wiring + Android-17 hardening | build |
| OB-008-1 | Per-engine trim numbers + V1 Piper pack contents (ear-tuned) | build (with F-045) |
| OB-D44 | F-028 sentence-terminal vs clause sub-split tag shape | F-028 / build |
| OB-D48 | Cross-chapter text-ahead wiring confirmation | build |
| OB-015-1 | Brief-hold starvation a11y announcement | build |

> KI-### entries (defects found during build) append below via templates/issue.md.
