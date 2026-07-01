---
id: S3
tier: state
status: draft — awaiting operator approval
updated: 2026-06-30
cross-refs: [P1, D3a, D3b, D3c, D35, D84, D15b, D31, D79, D80, D82, D83, F-020, F-057]
if-incomplete: "Return to state/current-state.md."
---
# Step S3 — Book Identity (content-hash)
Phase: P1 · Feature(s): F-020 · current-rung: Orient

> **Codex reads this SOW, not the design chat.** Build exactly what is scoped here.
> Climb the rungs in order (Orient → Scope → Inspect → Change → Verify → Record →
> Commit (G3) → Closeout). **Halt and report** — do not resolve yourself — on any of:
> a needed design decision, a check failing past its cap, a change touching an
> undeclared file, or a conflict with a locked decision. See `process/rungs.md`.

---

## Statement of Work
Give every book a permanent content fingerprint so the app can recognize the same book
across renames/repackaging and never confuse two different books. Deliver F-020's four
components — EPUB structural reader, content-hash function, the first feature-owned
precious table (`book_identity`), and the compute/store/lookup service — plus the first
D31 migration test. **No text extraction, no parser (F-018), no linking (F-021).**

---

## Scope — files that WILL change

**New (F-020, identity feature) — `app/src/main/java/com/golemreader/identity/`**
- `EpubStructuralReader.kt` — component **C-020-1**. Opens the EPUB (ZIP), reads
  `META-INF/container.xml` to locate the OPF package file, parses the OPF manifest +
  spine, resolves each spine itemref (in document order, **including `linear="no"`**) to
  its content-document ZIP entry, and exposes ordered access to each entry's
  **decompressed** byte stream and byte length. **Structural read only — never parses
  chapter XHTML content.** Emits typed errors on malformed input (see §Acceptance R11).
- `BookIdentityHasher.kt` — component **C-020-2**. Streams the spine content-document
  bytes through the digest per the **D84 recipe** (see §Identity recipe). Returns a
  lowercase-hex SHA-256 string. Streamed — never holds the whole book in memory.
- `BookIdentityEntity.kt` — component **C-020-3** (entity + DAO). The `book_identity`
  Room entity and its DAO (insert-if-absent, lookup-by-hash, count). Skeleton only.
- `BookIdentityService.kt` — component **C-020-4**. `computeIdentity(file)` → hash;
  `register(file)` → idempotent upsert returning **known|new**; `isKnown(hash)` → Boolean.
  Compute + store + lookup only — **no linking of multiple sources** (that is F-021).
- `EpubStructuralException.kt` — the sealed typed-error set (see R11).

**Edited (F-057 database shell — declared cross-touch, expected pattern)**
- `app/src/main/java/com/golemreader/storage/PreciousDatabase.kt` — register
  `BookIdentityEntity` in the `@Database` entities list; add a `bookIdentityDao()`
  accessor; bump `SCHEMA_VERSION` **2 → 3**; add `Migrations.V2_TO_V3`.

**Generated + committed**
- `app/schemas/com.golemreader.storage.PreciousDatabase/3.json` — Room-exported schema
  for version 3 (produced by the build; commit it).

**New tests — automated (JVM, run by `./gradlew testDebugUnitTest`)**
- `app/src/test/java/com/golemreader/identity/BookIdentityHasherTest.kt`
- `app/src/test/java/com/golemreader/identity/EpubStructuralReaderTest.kt`
- `app/src/test/java/com/golemreader/identity/BookIdentityServiceTest.kt` (Robolectric —
  in-memory precious DB, matching the S2 posture in D79)

**Edited test — automated (Robolectric)**
- `app/src/test/java/com/golemreader/storage/PreciousMigrationTest.kt` — add the V2→V3
  non-destructive migration test (T-020-B8).

**New tests — agent-run on the S23 (`./gradlew connectedDebugAndroidTest`)**
- `app/src/androidTest/java/com/golemreader/identity/BookIdentityDeviceTest.kt` — the
  three promises (R1–R3) + the two recorded cost readings (C1–C2).

**New test fixtures — `app/src/test/resources/fixtures/identity/`** (tiny hand-built EPUBs;
Codex authors these as test data):
`valid.epub` (≥3 spine docs, known order, plus excluded `cover.jpg` + `style.css`),
`reordered.epub` (same docs, spine order swapped), `repackaged.epub` (identical
decompressed spine bytes, different ZIP compression), `excluded-mutated.epub` (spine bytes
identical, cover/CSS changed), `content-mutated.epub` (one spine doc's bytes changed),
`with-appendix.epub` (includes a `linear="no"` itemref), `distinct-a.epub` /
`distinct-b.epub` (two different books), `malformed-no-container.epub`,
`malformed-empty-spine.epub`.

---

## Non-goals — explicitly NOT touched
- **Text/content extraction, XHTML → plain text, block markers** — F-018, deferred to S4 (D83).
- **Linking multiple files to one book** (same hash → many sources) — F-021.
- **Source availability / relink** — F-022. **Reparse** — F-023.
- **Soft-match restore** for re-typeset copies — F-025.
- **Displayable metadata** (title / author / cover) — F-019 / F-026; excluded from the
  hash by D3a and not stored here.
- **Non-EPUB formats** — future.
- **Placement rule changes** — `StoredDataType.BookIdentityHash → Precious` already exists
  in `StoragePlacement.kt`; use it, do not modify it.

---

## Identity recipe (D84 — permanent; do not vary)
Compute identity as follows. **Any ambiguity in these steps is a Halt → Design Zone
condition**, because the recipe is fixed for the life of the app.

1. **Selection.** Take every **spine itemref in document order**, **including
   `linear="no"`** entries (appendices, pop-up notes) — per **D35**. Resolve each to its
   manifest item, then to its content-document ZIP entry.
2. **Exclusions (D3a).** Never feed the digest: `container.xml`, the OPF package file,
   package metadata, cover image, any images, CSS, fonts, or any manifest item **not**
   referenced by a spine itemref.
3. **Bytes (D3b).** Use each entry's **decompressed** bytes as stored inside the EPUB —
   not the ZIP-archive bytes, not any parsed/rendered form.
4. **Framing (R3).** For each entry in spine order, feed an **8-byte big-endian length
   prefix** (the entry's decompressed byte count) into the digest, then the entry's
   decompressed bytes. Length-prefix framing (not a separator byte) so order and
   boundaries are themselves part of the fingerprint.
5. **Digest (R5).** **SHA-256**. Output lowercase hex.
6. **Streaming (R6).** Stream each entry through the digest; never hold the whole book
   in memory at once.

The stored record is **self-describing**: it records `algorithm = "SHA-256"` and
`recipe_version = 1`, so any future recipe change is explicit and app-wide, never silent
(OB-020-1).

---

## `book_identity` table (skeleton — R8)
| Column | Type | Notes |
|---|---|---|
| `hash` | TEXT NOT NULL, **PRIMARY KEY** | lowercase-hex SHA-256 digest — the identity key |
| `algorithm` | TEXT NOT NULL | e.g. `"SHA-256"` — identity is self-describing (R5) |
| `recipe_version` | INTEGER NOT NULL | `1` for the D84 recipe |
| `created_at_epoch_ms` | INTEGER NOT NULL DEFAULT 0 | first-seen timestamp |

Skeleton only: **no** title/author/cover here (D3a). Other features add their own tables
keyed by `hash`.

**Migration V2 → V3:** `CREATE TABLE book_identity (…)` with the columns above. **Do not
touch `db_meta`.** Existing precious rows must survive unchanged (D31 non-destructive).

---

## Decisions in play
- **D3a/D3b/D3c** — identity = content in reading order, raw bytes before parsing;
  re-typeset copy is a new book.
- **D35** — hash covers all spine itemrefs incl `linear="no"`.
- **D84** — the recipe above (SHA-256 + 8-byte big-endian length-prefix framing +
  document order + stated exclusions). **Permanent.**
- **D15b** — identity is precious (Room).
- **D31** — precious schema: never-destructive, always-migrate, proven by an automated
  test. `book_identity` is the **first feature-owned** table this governs.
- **D79** — pure logic → JVM unit tests; Room build/migration → Robolectric in the JVM
  source set; real device behavior → S23.
- **D80** — follow the S2 substrate pattern (exported schemas, migration object shape).
- **D82 / D83** — renumbered ladder; S3 scope = identity-only.

---

## Acceptance criteria (what "done" means)

**Presence & wiring (automated)**
- [ ] **T-020-P1** — reader resolves exact spine order from `valid.epub` (R1).
- [ ] **T-020-P2** — hasher returns a fixed 256-bit SHA-256 digest matching a known
      expected value for a known byte input (R5).
- [ ] **T-020-P3** — `book_identity` builds at schema v3, entity present, reads empty on
      first run (R8/R10).

**Behavior & limits (automated)**
- [ ] **T-020-B1** — determinism: same EPUB → identical hash across repeated runs (R5).
- [ ] **T-020-B2** — decompression-independence: `valid.epub` vs `repackaged.epub`
      (identical decompressed spine, different ZIP packaging) → identical hash (R2).
- [ ] **T-020-B3** — exclusion correctness: `excluded-mutated.epub` → hash unchanged (R4).
- [ ] **T-020-B4** — content sensitivity: `content-mutated.epub` → hash differs (R7).
- [ ] **T-020-B5** — order sensitivity: `reordered.epub` → hash differs (R3).
- [ ] **T-020-B6** — distinctness: `distinct-a` vs `distinct-b` → different hashes (R5).
- [ ] **T-020-B7** — malformed input: `malformed-no-container.epub` and
      `malformed-empty-spine.epub` → typed error, **no** identity, no crash (R11).
- [ ] **D35 coverage** — `with-appendix.epub`'s `linear="no"` itemref **is** included:
      removing/altering that appendix changes the hash.
- [ ] **T-020-B8** — **D31 first exercise:** create the DB at v2 with `db_meta` rows, run
      `V2_TO_V3`, assert `db_meta` rows intact **and** `book_identity` created and usable
      (non-destructive). Include a destructive-variant test that Room **rejects** (mirrors
      the S2 `PreciousMigrationTest` shape).

**Cost & budget (agent-run on S23 — measure and RECORD, no pass/fail threshold)**
- [ ] **T-020-C1** — hash time for a few-MB EPUB: record the number. (Import-time only;
      never on the audio path. No threshold — honest measurement per the S3 plan.)
- [ ] **T-020-C2** — peak memory during hashing does not scale with book size (streamed).
- Contributor tag: **N/A** — F-020 is import-time, not steady playback; not registered in
  the integrated listen-loop budget. (Record the reason per D28.)

**Result (agent-run on S23)**
- [ ] **T-020-R1** — add a book, note its identity; remove and re-add the same file →
      recognized as the same book (same hash, existing record found).
- [ ] **T-020-R2** — add a genuinely different book → new hash, new record.
- [ ] **T-020-R3** — write an identity, force-stop the app, relaunch → record reads back
      identical (R10/D31 survives restart).

---

## Test posture
- **Form:** automated (JVM + Robolectric) for all logic and the migration; agent-run on
  the S23 for R1–R3 and the C1–C2 readings.
- **Plan:** Robolectric only where Room is involved (P3, B8, service DB lookup); plain JVM
  JUnit for reader and hasher. Device suite runs last, on the S23.
- **Principle conformance:** Principle 5 (resource efficiency) via streamed hashing (R6).
  No perceptual output (identity has none).

## Entry gate (G2)
- [x] Prior step (S2) closed  - [x] current-state fresh  - [x] scope + non-goals declared
- [x] (non-trivial) operator approved — D82, D83, D84 locked in the S3 design session

## Rung tracker
- [ ] Orient  - [ ] Scope  - [ ] Inspect  - [ ] Change
- [ ] Verify  - [ ] Record  - [ ] Commit (G3, guarded)  - [ ] Closeout

## Registers to update at Record
- `state/current-state.md` (S3 done, next = S4)
- `ledgers/decision-log.md` (append D82, D83, D84 if not already present)
- `ledgers/testing.md` (T-020-* results + confidence)
- `state/phase-index.md` (mark S3 done under the D82 renumbering)

## Build & validate commands (for Codex)
- Build: `./gradlew assembleDebug`
- Automated tests: `./gradlew testDebugUnitTest`
- Device tests (S23 attached): `./gradlew connectedDebugAndroidTest`

## Verify result
- Result: [pass|fail] · Confidence: [high|medium|low] · T-id: T-020-*
- Notes: [FILL]

## Closeout
- Committed: [branch / sha] · Next step: **S4 — Text → sentences (F-018 thin + F-027 + F-028)**

---
*Step S3 SOW v1.0.0 — draft, awaiting operator approval. On approval this becomes the
active S3 step doc and is committed to the repo. Prior versions retained for rollback.*
