---
id: KNOWN-ISSUES
tier: ledger
status: active
updated: 2026-07-01
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

- **KI-S1-001 — Upstream gate-check template control-flow defect.**
  During S1, `guards/gate-check.sh` emitted both PASS and FAIL rows for passing checks
  because the `row()` helper returned the nonzero status of `[ "$2" = "FAIL" ]` on PASS
  rows, which triggered the `cmd && row PASS || row FAIL` fallback. It also treated the
  intentionally deferred `SECRET_SCAN_CMD` as a hard G3 failure, conflicting with the
  pre-commit hook and guard README policy. Corrected locally under D78; upstream template
  cleanup remains owed if this Armature template is reused.

- **KI-S3-001 — Device test-services UID gap blocks `MANAGE_EXTERNAL_STORAGE` grant.**
  During S3's device run on the S23, `ExternalCacheClearDeviceTest` (S2/F-057) was
  SKIPPED: `androidx.test.services` had no UID on-device, so the test couldn't set
  `MANAGE_EXTERNAL_STORAGE`. S3's own device suite passed; build was SUCCESSFUL.
  Not blocking S4 (no S4 component depends on this permission path). Owner: build /
  whenever a step next depends on verified external-cache-clear behavior on-device.
