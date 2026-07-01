---
id: S2
tier: state
status: closed
updated: 2026-06-30
cross-refs: [P1, D15, D31, D32, D68, D79, D80, D81, F-057, PR-4, PR-5, TD-001]
if-incomplete: "Return to state/current-state.md."
---
# Step S2 — Storage substrate (F-057)
Phase: P1  ·  Feature(s): F-057  ·  current-rung: Closeout

## Changelog (SOW versions)
- **v1.0.0 → v1.0.1:** corrected KSP guidance from "match the Kotlin version" to "KSP 2.3.1+";
  added D81; added a resume note.
- **v1.0.1 → v1.0.2:** **corrected the KSP floor from 2.3.1 to 2.3.4.** 2.3.1 added *initial*
  built-in-Kotlin support, but the patch that makes KSP reliably *detect* AGP built-in Kotlin
  (and stop using `kotlin.sourceSets`) landed later; 2.3.1/2.3.2 still throw the error. Google's
  current kapt→KSP docs reference **2.3.4**; latest stable is **2.3.5**. KSP 2.3.x is independent
  of the Kotlin version and supported on Kotlin 2.2.*, so this **still requires no Kotlin bump and
  no `gradle.properties` flag** for the primary path. Added a **bounded temporary-flag fallback**
  (D81) + **TD-001** so the step does not halt a third time on this knot.

## Statement of Work
Stand up the app's storage substrate: three physically-separated tiers (a **precious**
Room database, an **imported-assets** file store + manifest, a **rebuildable** cache/RAM
store), a single **tier-placement rule** every future writer must consult, a
**cache-clear / eviction routine** that provably cannot reach precious, the **D31
never-destructive migration harness** proven by a demonstration migration, and the **D32
per-book addressing-key** skeleton. Hand-off: place the F-057 requirement docs into
`reference/requirements/`. No user-facing screen is added.

## Resume note (agent) — halted twice on the KSP/built-in-Kotlin knot; resume mid-Change
Keep all prior in-scope work (no commit was made). **Set KSP to the latest stable 2.3.x
(floor 2.3.4 — NOT 2.3.1/2.3.2/2.3.3), keep Kotlin/KGP 2.2.10**, declared in the top-level build
file. Re-run the build/test. Only if the latest 2.3.x **still** throws the `kotlin.sourceSets`
error, take the **D81 fallback** (temporary suppress flag + TD-001) rather than halting. Then
continue the rungs from Change.

## Scope — files that WILL change
**Build config (install-class — see the flagged block below):**
- `build.gradle.kts` (root) — declare the KSP and AndroidX-Room Gradle plugins (`apply false`);
  pin KSP to the **latest stable 2.3.x (floor 2.3.4)** per D81 (top-level override above AGP's
  bundled default).
- `app/build.gradle.kts` — apply those plugins; add Room + KSP + test deps; set the Room
  schema-export directory; enable Android resources for JVM unit tests.
- `app/schemas/` — **NEW** committed folder: exported Room schema JSON (v1, and the
  demonstration v2).
- `gradle.properties` — **in scope ONLY under the D81 fallback** (the temporary suppress flag).
  Untouched on the primary path.

**Application code (agent authors the Kotlin; described here in plain language only):**
- `app/src/main/java/com/golemreader/storage/` — **NEW** package: the three tier definitions;
  the placement rule (data-type → tier); the precious Room database class + an F-057-owned
  `db_meta` entity/DAO (substrate metadata, not a feature table); the rebuildable store
  (cache dir + an in-RAM map whose entries can be marked "in-use / near-playhead"); the
  cache-clear / eviction routine; and the D32 addressing-key type (book/sentence/span skeleton).

**Tests:**
- `app/src/test/java/com/golemreader/storage/` — **NEW** JVM unit tests (gate-enforced):
  placement rule; separation path-logic; cache-clear path-logic; Room build/init; the
  migration harness incl. the negative (data-loss) case. (Room-touching tests use Robolectric.)
- `app/src/androidTest/java/com/golemreader/storage/` — **NEW** instrumented tests on the S23:
  precious survives restart + process-death; real OS/user cache-clear leaves precious intact;
  plus the Room/migration tests as the D79 fallback target if Robolectric is incompatible.

**Hand-off + registers:**
- `reference/requirements/F-057-Storage-Tiers-Requirements-v1_0_0.md` — **NEW** (place).
- `reference/requirements/F-057-Storage-Tiers-Requirements-v1_0_1-delta.md` — **NEW** (place).
- `ledgers/decision-log.md` — append **D79**, **D80**, **D81**.
- `ledgers/tech-debt.md` — append **TD-001** *only if the D81 fallback is used*.
- `ledgers/testing.md` — append the `T-057-*` rows + the exact resolved KSP/Room/Robolectric
  versions, and which D81 path was taken (primary vs fallback).
- `process/index.md` — register F-057, C-057-1…5, `T-057-*` (and TD-001 if created).
- `state/current-state.md` — update at Record/Closeout.
- `state/active/step-S2.md` — this SOW, as the live step file.

## Non-goals — explicitly NOT touched
- **When** position is saved / flushed — F-058 (D15f/D63).
- The **real book-identity hash** and the **first feature table** — F-020 (S3).
- **Final column schemas** for any feature's records — each owning feature.
- Any **"clear cache" button / Settings UI** — F-064.
- The **audio buffer's real contents / pipeline cache** population — F-001 / F-027.
- **Export / import** round-trip — F-059.
- **Encryption-at-rest** — separate decision if ever wanted.
- **A Kotlin/Compose version bump** — out of scope; raise separately if ever wanted.

## Decisions in play
- **D15** — storage split + the hard invariant.
- **D31** — precious-schema policy (never-destructive · always-migrate · migration-proven).
- **D32** — record addressing (skeleton this step).
- **D68** — degrade visibly, never silently.
- **PR-4**, **PR-5**.

### New decisions to record this step (lock on operator approval)
- **D79 — S2 test-runtime posture.** Pure-logic tests as JVM unit tests under the commit gate;
  Room build/init + migration harness via Robolectric in the JVM source set (primary), or
  `androidTest` on the S23 (fallback, recorded). Device-only results always instrumented on the
  S23. Operator-delegated: no.
- **D80 — F-057 substrate table + demonstration migration.** `db_meta` table at v1 + a
  demonstration v1→v2 migration to prove the D31 harness now; first feature table at F-020;
  schemas committed under `app/schemas/`. Operator-delegated: no.
- **D81 — KSP under AGP built-in Kotlin (amended v1.0.2).** **Primary:** use KSP **2.3.4 or
  newer** (latest stable 2.3.x; the line where KSP reliably detects AGP built-in Kotlin and
  registers via `android.sourceSets`), keep **Kotlin/KGP 2.2.10** (KSP 2.3.x is version-
  independent and supported on Kotlin 2.2.*), declared in the top-level build file. No
  `gradle.properties` change on this path. **Bounded fallback (last resort only):** if the latest
  stable 2.3.x *still* throws the `kotlin.sourceSets` error, set
  `android.disallowKotlinSourceSets=false` in `gradle.properties` as an **explicitly temporary
  bridge**, record **TD-001**, and continue — do not halt. Do **not** bump Kotlin/Compose inside
  S2. Operator-delegated: no (operator-approved, fallback overridable).
- **TD-001 (created only if the fallback is used)** — "S2 used the temporary
  `android.disallowKotlinSourceSets=false` bridge because the available KSP 2.3.x still emitted
  the built-in-Kotlin sourceSets error. The flag is removed in AGP 10. Durable fix: adopt a KSP
  build with working built-in-Kotlin detection (or align to the Kotlin 2.3 toolchain). Revisit at
  or before the next toolchain-touching step."

## Acceptance criteria (what "done" means)
- [ ] `./gradlew assembleDebug` builds and the S1 smoke test still passes.
- [ ] First run initializes all three tiers; each reachable and reports **empty**. *(T-057-P1)*
- [ ] Placement rule returns the correct tier for every D15b/c/d data type. *(T-057-P2)*
- [ ] Precious Room DB builds at **v1** and contains the F-057 substrate set (`db_meta`).
      *(T-057-P3, scoped — see reconciliation note)*
- [ ] **Hard invariant:** after seeding precious, cache-clear leaves every precious record
      byte-identical. *(T-057-B1)*
- [ ] Eviction removes rebuildable, zero precious; in-use/near-playhead survive while marked.
      *(T-057-B2/B3)*
- [ ] **Physical separation:** precious and rebuildable resolve to non-nested locations.
      *(T-057-B4)*
- [ ] **Migration harness proven:** demo v1→v2 builds old → migrates → asserts rows survive; a
      destructive variant FAILS. *(T-057-B5)*
- [ ] **Survival (device):** precious survives restart + process-death on the S23. *(T-057-R1)*
- [ ] **User-felt promise (device):** on-device cache-clear frees rebuildable, keeps precious.
      *(T-057-R2)*
- [ ] F-057 v1.0.0 + delta placed in `reference/requirements/`.
- [ ] D79/D80/D81 (and TD-001 if used) recorded; `T-057-*` + resolved versions + D81-path in
      `testing.md`; IDs registered in `process/index.md`.
- [ ] `current-state.md` updated; step closed; next step named (**S3 — F-020 Book identity**).

**Deferred (owed, non-blocking):** T-057-C1 (latency), T-057-C2 (footprint) — agent-run,
recorded. T-057-C3 — contributor to the integrated listen-loop budget (end of T3).

## Test posture
- **Form:** automated (JVM) + agent-run (S23). No guided-manual (no perceptual surface).
- **Plan:** per D79.
- **Principle conformance:** PR-4, PR-5. PR-7 n/a.

## Entry gate (G2)
- [x] Prior step closed (S1) - [x] current-state fresh - [x] scope+non-goals declared
- [x] (non-trivial) operator approved (incl. the D81 v1.0.2 amendment)

## Rung tracker
- [x] Orient - [x] Scope - [x] Inspect - [x] Change
- [x] Verify - [x] Record - [x] Commit (G3, guarded) - [x] Closeout

## Verify result
- Result: pass · Confidence: high for JVM/build invariants, medium for S23 OS behavior ·
  T-id: T-057-P1/P2/P3/B1/B2/B3/B4/B5/R1/R2
- Notes: D81 primary path used: KSP 2.3.5, Kotlin/KGP 2.2.10, no `gradle.properties`
  fallback. `./gradlew testDebugUnitTest`, `./gradlew assembleDebug`, and
  `./gradlew connectedDebugAndroidTest` passed. S23 Settings > App info > Storage >
  Clear cache removed rebuildable cache while seeded precious data survived; shell
  `pm/cmd package clear --cache-only` hung on this device, so the real Settings UI path
  was used.

## Closeout
- Committed: branch s2-storage-substrate / git HEAD · Next step: S3 — F-020 Book identity

---

## ⚠ Build-config additions (INSTALL-CLASS — downloads libraries on the next build)
> First build downloads these into Gradle's cache (nothing installed system-wide outside the
> project). Record exact resolved versions in `ledgers/testing.md`.

- **KSP** (`com.google.devtools.ksp`) — **use 2.3.4 or newer (latest stable 2.3.x; currently
  2.3.5).** Floor is **2.3.4** — 2.3.1/2.3.2/2.3.3 still emit the `kotlin.sourceSets` error.
  KSP 2.3.x is version-independent of Kotlin and supported on Kotlin 2.2.*, so **keep KGP/Kotlin
  2.2.10** — no Kotlin bump. Declare the version in the **top-level build file**. Primary path:
  **do NOT add `android.disallowKotlinSourceSets=false`.**
- **AndroidX Room Gradle plugin** (`androidx.room`) — schema export to `app/schemas/`.
- **Room libraries** — `room-runtime`, `room-ktx`, `ksp("…:room-compiler")`,
  `testImplementation("…:room-testing")`. Current stable Room.
- **Robolectric + AndroidX Test** (test-only, D79 primary) — `robolectric`, `androidx.test:core-ktx`,
  `androidx.test.ext:junit-ktx`; `testOptions { unitTests.isIncludeAndroidResources = true }`.

## Reconciliation notes (flagged, not silently resolved)
- **T-057-P3 scope:** asserted entity set at S2 is the F-057 substrate set (`db_meta`); the
  full-set assertion grows as features add tables. Fold into the next F-057 delta / v0.6.0 sweep.
- **D32 addressing key:** built within C-057-4's orbit; mint **C-057-6** in the next F-057 delta
  if it warrants its own ID.

## Halt conditions (per AGENTS.md — report, do not self-resolve)
- The latest stable KSP 2.3.x **and** the D81 temporary fallback **both** fail to build →
  **Halt → Design Zone** (would mean a Kotlin 2.3 bump — a separate stack decision).
- Robolectric-only incompatibility → not a halt; take the D79 fallback, record it.
- Any change touching a file **outside this scope list** → Halt and report. (`gradle.properties`
  is in scope ONLY under the explicit D81 fallback.)

*End Step S2 SOW v1.0.2. Awaiting operator approval before the agent resumes.*
