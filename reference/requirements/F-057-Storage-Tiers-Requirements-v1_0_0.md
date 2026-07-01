# F-057 — Storage Tiers — Requirements

**F-id:** F-057
**Title:** Storage tiers (precious / imported assets / rebuildable)
**Tier:** Reference (per-feature requirements)
**Status:** GROOMING — for operator review; not yet committed
**Version:** v1.0.0
**Updated:** 2026-06-26
**Supersedes:** — (first version)
**Backs:** Design Spec v0.5.0 — D15 (storage split); supporting D3, D4d, D11, D13n, D15h–j, D21q, D22b
**Cross-refs:** D28 (test taxonomy), D29-coverage (coverage target); Candidate Inventory v0.2.0 (F-057, App. A, App. C)
**If incomplete:** return to `current-state` → Design Spec v0.5.0 §D15 → Candidate Inventory v0.2.0 §I.

---

## 1. Summary

F-057 is the app's filing system. It defines **which kind of storage** every piece of data lives in, and guarantees those kinds are kept physically separate so they cannot contaminate one another. The governing rule (D15e) is the **hard invariant**: irreplaceable ("precious") data is physically separate from regenerable ("rebuildable") data, so clearing the cache can never delete something that cannot be rebuilt. Every later feature that persists anything depends on this feature to answer "which tier?" — so it is groomed and built first.

This is a **backend / substrate feature.** It has no user-facing screen of its own; it is the foundation other features write to.

---

## 2. Target & status

- **Target:** V1.
- **Grooming status:** Planned → (this doc) confirmed candidate for commit.

---

## 3. Backing decisions

| Clause | What it fixes |
|---|---|
| **D15a** | Three tiers, classified by three questions: regenerable? auto-deletable? must-survive-reinstall? |
| **D15b** | **Precious** tier (Room database): identity, position, rules/respellings, rule-pack states, settings, library catalog, source access records, book states, asset manifest, stable rule IDs. |
| **D15c** | **Imported assets** tier (files): models, fonts, packs, themes; manifest-tracked, re-importable. |
| **D15d** | **Rebuildable** tier (cache/RAM): audio buffer, pipeline cache, extracted images, resolution records. |
| **D15e** | **Hard invariant:** precious physically separate from rebuildable; cache-clear never touches precious. |
| **D15g** | **Eviction:** precious never evicted; rebuildable cleared freely, protecting near-playhead data. |
| D15f | Save policy (timing of position flush) — **owned by F-058**, referenced here only for tier placement. |
| D15h–j | Source access records, export bundle, book states — their **data shapes** live in precious; their **behavior** is owned by F-022/F-059/F-024. |

> **Scope boundary:** F-057 owns the **tiers, their separation, and the placement rule.** It does **not** own *when* position is saved (F-058), *how* sources relink (F-022), or *how* export works (F-059). Those features decide behavior; F-057 only guarantees each has a correct, separated home.

---

## 4. Requirements (numbered, testable)

**R1 — Three distinct tiers exist.** The app provides exactly three storage tiers: Precious, Imported Assets, Rebuildable. Each is a distinct, addressable location.

**R2 — Precious tier is a structured database.** Precious data is stored in Room (Android's local SQLite-backed database library). It holds at minimum: book identity hashes, reading positions, authored rules/respellings, rule-pack enabled/disabled states, app settings, library catalog, source access records, book states, the asset manifest, and stable rule IDs.

**R3 — Imported Assets tier is file-based and manifest-tracked.** Imported binary assets (voice models, fonts, rule-packs, themes) are stored as files in a dedicated location, and every asset is recorded in a manifest entry in the precious tier (type + path + association).

**R4 — Rebuildable tier is cache/RAM only.** The audio buffer, pipeline cache, extracted images, and resolution records live in cache or RAM. Nothing in this tier is required to survive an app restart, and the audio buffer specifically is RAM/temporary only (D11d).

**R5 — Hard separation (the core invariant).** Precious and rebuildable storage occupy physically separate locations such that an operation targeting rebuildable storage **cannot** reach or affect precious storage.

**R6 — Cache-clear safety.** Clearing all rebuildable storage (a cache wipe, whether user-initiated or OS-initiated) leaves every precious record intact.

**R7 — Precious survives reinstall-class events.** Precious data persists across app restarts and process death. (True reinstall survival depends on Android backup/SAF and is exercised by F-059's export path; R7's automated scope is restart + process-death survival.)

**R8 — Eviction policy.** Rebuildable storage may be evicted freely; precious storage is never evicted by any eviction path. Near-playhead rebuildable data is protected from eviction while in use.

**R9 — Tier placement is explicit and centralized.** A single, named placement rule maps each data type to its tier. No feature chooses a tier ad hoc; all writers consult the placement rule. (This is what makes "which drawer?" answerable consistently by every later feature.)

**R10 — Graceful first-run / empty state.** On first launch with no data, all three tiers initialize cleanly and report empty rather than erroring.

---

## 5. Components (C-### minted here)

| C-id | Component | part-of | Notes |
|---|---|---|---|
| C-057-1 | Room database schema (precious) | F-057 | The table/entity definitions for the D15b precious set. Schema only; per-record behavior is the owning feature's. |
| C-057-2 | Imported-assets file store + manifest | F-057 | Dedicated file directory + the manifest entries (D15c) that index it. |
| C-057-3 | Rebuildable cache/RAM store | F-057 | The cache directory + in-RAM structures for buffer/pipeline/images/resolution records. |
| C-057-4 | Tier placement rule | F-057 | The single mapping data-type → tier (R9). The contract every writer consults. |
| C-057-5 | Cache-clear / eviction routine | F-057 | The operation that wipes rebuildable while provably not touching precious (R6/R8). |

---

## 6. Dependencies

- **depends-on:** none. F-057 is the dependency floor; it is deliberately first.
- **blocks:** F-058 (position save), F-020 (identity records need the precious home), F-019 (library catalog), F-024 (book states), F-001 (buffer needs the rebuildable home), F-027 (pipeline cache), F-022/F-059 (source records / export schema). In practice, *every* persisting feature.
- **part-of:** —

---

## 7. Data touched (from App. C; owned by D15)

Precious: book identity hash (D3), source access record (D15h), rule (D5/D21q), book state record (D15j), asset manifest entry (D15c), locale pair (D20), settings. Rebuildable: audio segment + look-ahead buffer entry (D11b/D22b), resolution record (D21), pipeline cache (D4d), extracted images.

> F-057 provides the *homes* and *schema*; the field-level shape of each type is finalized with the feature that owns it. F-057's job is correct tier assignment, not final column design for every table.

---

## 8. UI surface(s)

**None — backend substrate.** One indirect, future touch point: a "clear cache" control (likely in Settings, F-064) will call C-057-5. The control is groomed with F-064; F-057 only guarantees the routine it calls is safe.

---

## 9. Runtime states handled (from App. B)

F-057 underlies these states rather than driving them: *Source unavailable* (precious record persists though the file is gone, D3e/D15h), and it is the safe target for *cache eviction* under memory pressure. No F-057-owned runtime state; it is the substrate other states rest on.

---

## 10. Acceptance criteria & tests — the four buckets (D28)

> Coverage postures follow D29-coverage: logic→automated; device/system behavior→agent-run + recorded; perceptual→guided (none here — storage has no perceptual surface). Each test states its pass condition up front.

### Bucket 1 — Presence & wiring ("is it there, reachable, switched on at the right time?")

| Test | Pass condition | Form | Target confidence |
|---|---|---|---|
| T-057-P1 | App start initializes all three tiers; each is reachable and reports empty on first run (R1, R10). | Code opens each tier and reads empty without error. | **Automated** | High |
| T-057-P2 | The tier placement rule (C-057-4) exists and returns the correct tier for every D15b/c/d data type (R9). | For each named data type, the rule returns its D15-specified tier; a parameterized test covers the full list. | **Automated** | High |
| T-057-P3 | Room schema (C-057-1) builds and contains the full precious entity set (R2). | Database opens at the declared version; every named entity is present. | **Automated** | High |

### Bucket 2 — Behavior & limits ("does it obey the rules and stay inside allowances?")

| Test | Pass condition | Form | Target confidence |
|---|---|---|---|
| T-057-B1 | **The hard invariant (R5/R6).** After writing known precious records, clearing all rebuildable storage leaves every precious record byte-identical. | Seed precious; run C-057-5 cache-clear; re-read precious → all records present and unchanged. | **Automated** | High |
| T-057-B2 | **Eviction respects precious (R8).** An eviction pass over rebuildable removes rebuildable entries and removes zero precious entries. | After eviction, rebuildable count drops as expected; precious count unchanged. | **Automated** | High |
| T-057-B3 | **Near-playhead protection (R8).** Rebuildable data marked in-use/near-playhead is not evicted while marked. | Mark a buffer entry in-use; run eviction; entry survives; unmark; it becomes evictable. | **Automated** | High |
| T-057-B4 | **Physical separation (R5).** Rebuildable and precious resolve to separate, non-nested locations such that a rebuildable-targeted wipe cannot enumerate precious. | Inspect resolved paths/handles: no shared parent that a cache wipe would traverse. | **Automated** (path assertion) + **agent-run** (real device cache-clear) | High (logic) / Medium (device) |

### Bucket 3 — Cost & budget ("does it stay within speed/memory/battery/storage allowances?")

| Test | Pass condition | Form | Target confidence |
|---|---|---|---|
| T-057-C1 | **Local cost — write/read latency.** A single precious write and read complete fast enough to never block first audio or UI. | Pass condition: **budget TBD — set at build** (proposed: a precious write ≤ a few ms on the S23; not on the audio-critical path regardless). Recorded, not assumed. | **Agent-run** device measurement | Medium |
| T-057-C2 | **Local cost — storage footprint sanity.** Precious DB growth per book is bounded and small (it stores metadata, never audio/text bodies). | Add N books; precious size grows roughly linearly and stays small (target order: kilobytes/book, **confirm at build**). | **Agent-run** | Medium |
| T-057-C3 | **System cost — contributor tag (not a solo number).** F-057's write activity during steady playback contributes to the integrated listen-loop budget (no thermal throttle, no CPU warning, no fast battery drain, audio quality held). | **No standalone pass condition.** F-057 is **registered as a contributor** to the integrated listen-loop system-budget test (runnable end of T3). Its specific contributor concern: position/state-flush frequency under playback. | **Deferred — agent-run at integration** | N/A until T3 |

### Bucket 4 — Result ("does the end output match what we wanted?")

| Test | Pass condition | Form | Target confidence |
|---|---|---|---|
| T-057-R1 | **Survival across restart + process death (R7).** Precious data written, app killed and relaunched → data is fully present. | Write known precious records; force-stop; relaunch; every record reads back identical. | **Agent-run** device | Medium |
| T-057-R2 | **The promise a user feels.** A cache clear frees space but loses nothing the user cares about (positions, rules, library, settings all intact). | After an OS/user cache clear on-device, library + positions + rules + settings all present; only regenerable data is gone. | **Agent-run** device | Medium |

**Buckets with N/A:** none fully N/A. Perceptual/guided-manual: **N/A — storage has no perceptual output** (one-line reason recorded per D28).

---

## 11. Test posture summary

- **Automated (High):** the invariant (T-057-B1), eviction safety (B2/B3), placement rule (P2), schema/init (P1/P3), path separation logic (B4-logic). This is the right place for High confidence — these are exactly the silent-corruption risks, and they are mechanically checkable.
- **Agent-run (Medium):** real-device cache-clear, restart/process-death survival, latency and footprint numbers (B4-device, R1, R2, C1, C2).
- **Deferred contributor:** C3 → integrated listen-loop budget test at end of T3.

This matches D29-coverage: the riskiest, most reusable logic is automated; device behavior is verified once and recorded; nothing rests on "it probably works."

---

## 12. Non-goals (explicit out of scope)

- **When** position is saved/flushed — F-058 (D15f).
- **How** a missing source relinks or re-prompts — F-022 (D15h).
- **How** export/import round-trips precious data — F-059 (D15i).
- **Final column-level schema** for every owning feature's records — finalized with each feature; F-057 sets tier + skeleton only.
- **The user-facing "clear cache" control** — groomed with Settings (F-064); F-057 supplies only the safe routine it calls.
- **Encryption-at-rest / security hardening** — not specified by D15; if wanted, it's a separate decision (flagged, not assumed).

---

## 13. Open boundaries (carried to build, with owner)

- **OB-1 — Latency & footprint budgets (T-057-C1/C2):** concrete numbers set at build via agent-run measurement on the S23. Owner: build-phase Verify.
- **OB-2 — Reinstall survival depth (R7):** automated scope is restart + process-death; true uninstall→reinstall survival is exercised through F-059's export path. Owner: F-059 grooming. Flagged so R7 isn't over-claimed.
- **OB-3 — Schema versioning/migration strategy:** Room supports migrations; the *policy* (how schema changes roll forward without data loss) is worth a small decision before precious tables proliferate. Recommend raising at F-020 grooming (first real precious tables). Owner: next session.

---

*End F-057 requirements v1.0.0. Awaiting component-by-component approval.*
