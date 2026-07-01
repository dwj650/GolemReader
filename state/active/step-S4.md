---
id: S4
tier: state
status: draft
updated: 2026-07-01
cross-refs: [P1, D82, D83, D84, D87, F-018, F-027, F-028]
if-incomplete: "Return to state/current-state.md."
---
# Step S4 — Text → sentences (F-018 thin + F-027 + F-028)
Phase: P1  ·  Feature(s): F-018 (thin), F-027, F-028  ·  current-rung: Orient

## Statement of Work
Given one chapter of a real EPUB, produce an ordered list of clean, indexed sentences —
both a display rendering (for the screen) and a spoken rendering (for the voice engine),
sharing one composite sentence index — proving the parse → clean → segment → fork pipeline
end to end on a single chapter. Multi-chapter looping is explicitly out of scope (arrives
with S6). Image extraction is explicitly out of scope (arrives with F-074, whenever that
surface is built).

## Scope — files that WILL change
- New package `app/src/main/java/com/golemreader/text/`:
  - `EpubTextExtractor.kt` — F-018 C-018-1/C-018-2: reuses `EpubStructuralReader`
    (`com.golemreader.identity`, C-020-1) for spine order; extracts plain text per
    spine content-document; preserves paragraph/heading/chapter-boundary as typed
    structural tokens (OB-018-4); honors declared encoding; flags a bad chapter
    without aborting the book (R8).
  - `TextPipeline.kt` — F-027 C-027-1: per-chapter orchestrator; consumes
    `EpubTextExtractor` output; runs pre-clean; invokes segmentation; forks each
    sentence into display + spoken renderings on the shared composite index.
  - `PreCleanStage.kt` — F-027 C-027-2: NFC normalize, whitespace normalize, strip
    zero-width/directional controls, smart-quote/dash → straight (D17g). Always-on,
    pre-fork.
  - `SentenceSegmenter.kt` — F-028 C-028-1/C-028-2/C-028-3/C-028-4: `BreakIterator`
    (locale-aware, default locale for S4) sentence boundaries + thin correction rules
    (abbreviations, decimals, ellipsis) + clause sub-split for over-long sentences,
    tagged `(parent sentence ordinal, clause ordinal)`. Segments carry a
    sentence-terminal vs. clause-sub-split type tag (OB-D44 shape; representation
    decided here, reconciled later with F-008).
  - `SentenceIndex.kt` — F-027 R3/R11 (D36): the composite
    `(chapter ordinal, sentence ordinal-in-chapter)` index type, anchored to book
    identity (F-020 hash). One owner: `TextPipeline` assigns it; `SentenceSegmenter`
    never does.
  - `TextPipelineModels.kt` (or split as needed) — shared data classes: structural
    token, sentence record (display + spoken + index), clause tag, chapter-parse
    result / per-chapter failure flag.
  - Rule-application-point wiring (R5/C-027-4): three named slots (shared/pre-fork,
    display branch, spoken branch) — present and reachable, but **no-op pass-through**
    in S4 since F-029 (normalization) and F-043 (rule resolution) aren't built yet.
    This is the intended proof of D4e/R10: the pipeline must run correctly with the
    slots empty.
- `app/src/test/java/com/golemreader/text/` — JVM/Robolectric unit tests (see
  Acceptance criteria).
- `app/src/test/resources/fixtures/text/` — one or more small fixture EPUBs with real,
  known chapter prose (existing `identity/` fixtures are structural-only and likely
  too minimal for segmentation testing — confirm at Inspect; add a new fixture if
  needed, e.g. a chapter with ordinary prose, an abbreviation, a decimal, dialogue
  with quotes, and one deliberately over-long sentence).
- `app/src/androidTest/java/com/golemreader/text/` — one on-device smoke test reading
  a real chapter on the S23 (Bucket 3/4 checks below).
- Output placement: parsed/pipeline output goes through the existing
  `RebuildableStore` (F-057, already built in S2) — confirm placement rule returns
  "rebuildable" for this cache type; do not create a new storage tier.
- Registers to update at Record/Closeout: `state/current-state.md`,
  `state/phase-index.md` (mark S4 done, name S5 next),
  `ledgers/decision-log.md` (append D87 — see Decisions in play),
  `ledgers/known-issues.md` (append the KI below).

## Non-goals — explicitly NOT touched
- **Multi-chapter looping / continuous playback** — F-001, arrives at S6.
- **Image extraction** (F-018 R10 / D58) — deferred to whenever F-074 is built.
- **Normalization class content** (numbers/dates/abbreviations/acronyms/symbols) —
  F-029. The spoken-branch rule slot exists and is reachable, but empty in S4.
- **Rule resolution** (the D21 tie-breaking ladder) — F-043. Slots are wired, not
  resolved.
- **Terminal-cue hygiene / append-period on sentence-terminal segments** — F-008,
  downstream; F-028 only supplies the type tag it will consume.
- **Highlight, Reading View, or any UI presentation** of the sentences produced here —
  F-014/F-016, later steps.
- **Reparse trigger/UI** — F-023; F-018 only needs to stay re-runnable/deterministic
  (R6), not wire the trigger.
- **Structured footnote handling** — F-072 (V3); footnote markers pass through inline
  per R9.
- **SaT/ONNX segmentation upgrade** — reserved, measured-need only, not this step.

## Decisions in play
- **D82** — P1 ladder renumbering (S4 = this step).
- **D83** — S3 identity-only boundary (why F-018 wasn't touched in S3).
- **D84** — permanent identity recipe; `EpubStructuralReader` (C-020-1) is reused
  as-is, not modified, for spine order.
- **D87** — S4 scope is a single-chapter text pipeline; image extraction (F-018 R10)
  explicitly deferred; recorded operator-delegated, 2026-07-01. *(Append full entry
  to decision-log.md at Record.)*
- Backing decisions from the requirement docs this step implements: D4a–D4e (pipeline
  shape, rule points, engine boundary, rebuildable cache), D7a/b/d/e (segmentation
  approach, clean-text input, no text alteration, clause sub-split), D12c/D1 (clause
  tagging), D17g (pre-clean), D20 (locale-aware — default locale acceptable for S4),
  D36 (composite sentence index), D44/OB-D44 (segment type tag shape — representation
  decided here).

## Acceptance criteria (what "done" means)
**F-018 (thin: R1–R9, R10 explicitly excluded)**
- [ ] T-018-P1 — real chapter → ordered text out, via reused `EpubStructuralReader`.
- [ ] T-018-P3 — output placement resolves to rebuildable, not precious.
- [ ] T-018-B1 — markup stripped, text content preserved.
- [ ] T-018-B2 — block structure (paragraph/heading/chapter) preserved as typed tokens.
- [ ] T-018-B4 — declared encoding respected (non-UTF-8 fixture, if used, extracts correctly).
- [ ] T-018-B5 — reparse determinism: same source parsed twice → byte-identical text.
- [ ] T-018-B6 — a malformed/missing chapter is flagged, not fatal.

**F-027**
- [ ] T-027-P1 — pipeline runs end to end on a fixture chapter; no stage skipped.
- [ ] T-027-P3 — the three rule-application points exist and are reachable (no-op in S4).
- [ ] T-027-P4 — output lands in the rebuildable tier.
- [ ] T-027-B1 — pre-clean correctness (NFC, whitespace, zero-width/directional strip, smart-quote/dash straighten).
- [ ] T-027-B2 / T-027-B8 — display[i], spoken[i], and the composite `(chapter, sentence)` index agree across all three.
- [ ] T-027-B3 — fork correctness: display keeps quotation marks; spoken is plain words; same index.
- [ ] T-027-B4 — rule points are observable, not silently swallowed, even as no-ops.
- [ ] T-027-B5 — spoken rendering is plain words/respellings only, no phonemes.
- [ ] T-027-B6 — pipeline produces a valid spoken rendering with F-029 absent (proves the decoupling — genuinely testable in S4 since F-029 doesn't exist yet).

**F-028**
- [ ] T-028-P1 / T-028-B1 — correct sentence boundaries on clean prose fixture.
- [ ] T-028-B2 — thin rules prevent false splits (abbreviation, decimal, ellipsis cases).
- [ ] T-028-B3 — reassembling segments reproduces input text byte-for-byte (no alteration).
- [ ] T-028-B4 — an over-long sentence sub-splits at clause boundaries, not mid-clause.
- [ ] T-028-B5 — clause tags `(parent sentence ordinal, clause ordinal)` correct and complete.
- [ ] T-028-B7 — determinism: same input + locale → identical boundaries twice.
- [ ] T-028-B9 — every segment tagged sentence-terminal or clause-sub-split; a sub-split is never tagged terminal.

**Cross-cutting / result (agent-run, on S23)**
- [ ] One real chapter from a real EPUB, on-device: extracts, cleans, segments, and
      forks correctly with no crash (combines T-018-R1 / T-027-R1 / T-028-R1 intent).
      Record wall-clock time and peak memory for the chapter — no fixed budget yet
      (Budget TBD per the requirement docs); just record honest numbers.

## Test posture
- Form: automated (JVM/Robolectric) for all pipeline/extraction/segmentation logic +
  agent-run on the S23 for the one real-chapter smoke test.
- Plan: mirrors the S2/S3 split (D79-style) — deterministic logic is gate-enforced;
  real-device behavior and cost are recorded, not gated on a fixed number yet.
- Principle conformance: Principle 4 (parser hotswap boundary / black-box interface,
  C-018-2) — confirm `TextPipeline` only calls through the interface, no EPUB-specific
  code outside `EpubTextExtractor`.

## Entry gate (G2)
- [ ] Prior step closed — S3 merged to main at commit `9157df7`.
- [ ] current-state fresh — pulled 2026-07-01, confirms S4 is next.
- [ ] Scope + non-goals declared — above.
- [ ] Operator approved — pending (this document).

## Rung tracker
- [ ] Orient — read current-state + this step; log the known issue below; confirm
      location; wait for operator acknowledgment.
- [ ] Scope  - [ ] Inspect  - [ ] Change
- [ ] Verify  - [ ] Record  - [ ] Commit (G3, guarded)  - [ ] Closeout

## Known issue to log at Orient
Append to `ledgers/known-issues.md`:

> **KI-S3-001 — Device test-services UID gap blocks `MANAGE_EXTERNAL_STORAGE` grant.**
> During S3's device run on the S23, `ExternalCacheClearDeviceTest` (S2/F-057) was
> SKIPPED: `androidx.test.services` had no UID on-device, so the test couldn't set
> `MANAGE_EXTERNAL_STORAGE`. S3's own device suite passed; build was SUCCESSFUL.
> Not blocking S4 (no S4 component depends on this permission path). Owner: build /
> whenever a step next depends on verified external-cache-clear behavior on-device.

## Verify result
- Result: [pass|fail]  ·  Confidence: [high|medium|low]  ·  T-id: [T-…]
- Notes: [FILL by Codex]

## Closeout
- Committed: [branch / sha]  ·  Next step: S5 (F-048, F-008 — first synthesized sentence)
