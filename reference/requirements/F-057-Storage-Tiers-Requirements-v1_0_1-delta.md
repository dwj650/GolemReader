# F-057 — Storage Tiers — Requirements — v1.0.1 (delta-amendment)

**F-id:** F-057
**Title:** Storage tiers (precious / imported assets / rebuildable)
**Tier:** Reference (per-feature requirements)
**Status:** **COMMITTED.** (Was: GROOMING.)
**Version:** v1.0.1
**Updated:** 2026-06-28
**Supersedes:** v1.0.0 — as a delta only. All v1.0.0 content remains in force; this records only what v1.0.1 adds. **v1.0.0 + this delta = the full requirements doc.** Prior version retained for rollback.
**Authority:** Design Spec v0.6.0 (App. B revision ledger; **D31** precious-schema policy) + Grooming Decision Log v1.0.0 §3 (the D31 migration-test pattern).
**If incomplete:** read v1.0.0 first, then apply this delta.

---

## Changelog v1.0.0 → v1.0.1
- **Committed** the document.
- Cross-referenced the **D31 schema-evolution policy** and its **concrete migration-test pattern**: precious-tier changes are **never-destructive · always-migrate · every migration proven by an automated test** — the pattern being **build the old schema → run the migration → assert the resulting schema + row survival**, first exercised at F-020.
- Added one requirement (R11) and one test.

---

## 1. New requirement (append to §4)

**R11 — Precious-schema evolution follows D31 (with a proven migration test).** Any change to a precious (Room) table is governed by D31: **never-destructive** (no precious data dropped), **always-migrate** (old rows carried onto the new schema), and **every migration proven by an automated test** using the locked pattern — *build the old schema → run the migration → assert the new schema and that rows survived.* F-057 defines the tier and this invariant; each persisting feature exercises the pattern on its own table (first at F-020). *(Names the cross-feature contract so no later precious change can ship without a migration test.)*

---

## 2. Test addition (append to §10, Bucket 2)

| Test | Pass condition | Form | Confidence |
|---|---|---|---|
| T-057-B5 | **Migration-test pattern is enforced (R11/D31).** A representative precious-table migration builds old → migrates → asserts new schema + row survival; a destructive migration fails the test. | Build old schema fixture → run migration → assert schema + rows; negative case (data loss) fails. | **Automated** | High |

---

*End F-057 v1.0.1 (delta). Committed. Prior version v1.0.0 retained for rollback.*
