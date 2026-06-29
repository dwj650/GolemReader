---
id: DESIGN-SPEC
tier: reference
status: committed
updated: 2026-06-28
source: Golem-Reader-Design-Spec-v0.6.0 (single consolidated authority)
if-incomplete: "Return to state/current-state.md and follow process/index.md."
---
# Golem Reader — Design Spec

**Version:** v0.6.0 (committed milestone — **single consolidated decision authority**)
**Status:** COMMITTED — foundation design **and** full feature-matrix grooming complete. This document is now the single authority for **all decisions D1–D76** across tiers T0–T8 and the surface tier, and for features **F-001–F-078**.
**Updated:** 2026-06-28
**Supersedes:** v0.5.0 (retained intact for rollback). **Consolidates** Grooming Decision Log v1.0.0 / v1.1.0 / v1.2.0 (retained as historical record; their ledgers now live in Appendices A–C here).
**Scope:** Engine, identity, text-pipeline, streaming/look-ahead, system normalization, the rule tie-breaking ladder, post-render audio-DSP (D1–D27); plus all grooming decisions — methodology, voice/audio, the listen-loop spine, surface architecture, navigation, playback shaping, system integration, persistence, accessibility, and shell (D28–D76).

---

## Changelog (v0.5.0 → v0.6.0)

- **Consolidated D28–D76 into the base spec** so there is **one decision authority again** (D1–D76). The Grooming Decision Log layers (v1.0.0 D28–D37, v1.1.0 D38–D53, v1.2.0 D54–D76) are now integrated here and **retire to historical record**.
- **D1–D27 carried forward unchanged** from v0.5.0. Their full clause text and feasibility rationale remain in v0.5.0 (retained for rollback); the statements here are the canonical short form, with refinements from later decisions cross-noted inline.
- **Moved the Log's three ledgers into appendices:** Open-Boundary ledger (App. A), per-feature revision ledger (App. B), running ledgers + shared-resource ownership map (App. C). Testing register T1–T5 carried as App. D.
- **Folded the review/note items:** F-033 locale-tag unreliability is now an explicit risk; two wishlist candidates registered; the F-001 integrated budget carries the operator's concrete pass criteria (no fake per-feature numbers).
- **Registered F-078** (accessibility reading-display overrides) and the **D76** sync-glow V1 parameter seam.
- **Passed the D30 reconciliation gate** at the decision-architecture level (companion one-page report, same milestone).

---

## How to read this document

- **Locked** — built and validated together in chat (logic) or on-device + by ear (audio); in force.
- **TBD** — we know this decision is needed; not yet made.
- **Waiting-discovery** — needs research before we even know what to decide.
- *(operator-delegated)* — Claude chose under delegated authority; overridable. *(operator-directed)* — the operator set the direction.

**Governance:** Uploaded documents (product inventory, Cedar material, failure-mode appendices, outside-chat reviews) are **discovery only**. Nothing here is authority unless created and validated together in chat. The Guiding Principles (author: operator) carry more weight as a north star; their appendices remain an unverified risk catalogue.

**Authority hierarchy (effective this version):** **Design Spec v0.6.0 (D1–D76)** is the single authority. Per-feature requirements docs reconcile to it. The Grooming Decision Logs are historical record. Where any per-feature doc disagrees with this spec, **this spec wins**.

---

## Guiding principles (north star)

1. **Identity = DNA** — a streaming speech platform whose content source happens to be text; speech, playback continuity, and synchronized reading are first-class systems.
2. **Streaming = foundation** — playback begins as soon as practical; never mass-render whole books.
3. **Audio quality first** — natural, pleasant long-session listening over implementation convenience.
4. **Total architectural hotswappability** — parsers and voice engines are black boxes; swapping one touches zero core loops.
5. **Resource efficiency is first-class** — battery, memory, thermal, storage, CPU are hard parameters.
6. **User-centric by default** — user controls engine, voice, speed, experience; accessibility designed in.
7. **Accessibility by default**, **features mindfully supported**, **clean-room implementation**.

---

# PART I — Foundation decisions (D1–D27)

> Canonical short form. Full clause text and feasibility rationale retained in Spec v0.5.0. Later-decision refinements cross-noted in **[brackets]**.

### D1 — Sentence/clause highlight via per-sentence playback boundaries
Highlight advances at the sentence level (clause level for long sentences), driven by the per-sentence sample lengths the synthesis callback emits. Engine-independent (shared sherpa-onnx wrapper). Device-validated for clean prose (T1). No waveform seam exists (joins are silence-to-silence); the only inter-sentence concerns are gap pacing (D27) and register continuity (D26).

### D2 — The VoiceEngine interface
(a) One interface: `load`, `speak` (D1 callback + abort), `stop`, `release`, `report-capabilities`. (b) Controls split **universal** (voice, speed, inter-sentence pause) vs **capability-gated**; gated degrade **visibly**, never silently. (c) Pitch is **not** a core engine control. (d) Engine quirks live in the adapter — home for voice-bound quirk packs (D24), per-engine edge-trim config (D27e), Piper `:`/`;` clip (D23f).

### D3 — Book identity
(a) Identity = hash of **spine content-document bytes in reading order** (excludes metadata, cover, images, CSS, fonts). (b) Hash **raw bytes, before parsing** → parser-independent. (c) Re-typeset copy = new book. (d) Same hash = **one logical book, multiple sources**. (e) Missing sources **unavailable, never auto-deleted**; same-hash reappearance relinks. (f) EPUB id + file size are soft hints only. (g) **Active source** user-selectable. (h) **Reparse on demand**. **[Refined by D35: hashes all spine itemrefs incl. `linear="no"` in document order.]**

### D4 — Text pipeline shape
(a) Per chapter: parse+clean → shared (pre-fork) rules → segment → **fork** per sentence into display + spoken renderings, each with its own rule point. (b) The **sentence is the shared anchor** across display/audio/highlight; same index. (c) **Engine boundary:** Golem produces spoken text; the engine phonemizes. (d) Output is **rebuildable cache**. (e) **Rule application points (never silent):** shared/pre-fork; display; spoken + system normalization. **[Anchor refined by D36: composite (chapter ordinal, sentence ordinal).]**

### D5 — Authored rules model
(a) Each rule carries **scope** (book/collection/global) + **target** (audio/display/both). Collection = any grouping; series is one type, deferred. (b) Operations extensible; v1 builds **replace + suppress**; future **annotate + transform**. (c) Rules carry a **source/origin**: parsing-constraint, system-enforced, system-recommended, user-explicit. (d) Resolution deterministic, never silent **(resolved by D21)**. (e) Romanize = system-recommended op; feasibility waiting-discovery.

### D6 — Pronunciation authoring via live preview
(a) Authored with **live preview**. (b) Output is a **respelling**; IPA is capability-gated. (c) **Suggest-and-pick** where a candidate exists. (d) **"Match me"** waiting-discovery.

### D7 — Sentence segmentation
(a) Golem decides boundaries; audio aligns without altering punctuation. (b) **ICU BreakIterator** + a thin rules layer; SaT a possible upgrade on measured need. (c) One synthesis call per segment baseline. (d) Runs on **shared clean text before the fork**. (e) Over-long segments **sub-split at clause boundaries**.

### D8 — Engine lifecycle & switching
(a) Book independent of engine. (b) Switch mid-playback = swap renderer, flush buffer, re-render from current sentence; position preserved. (c) Load-on-demand default; keep-both-resident optional. (d) Native release in the adapter.

### D9 — System normalization (principle & structure)
(a) Golem owns normalization (plain words in the spoken branch). (b) Organized by **class**, resolved by **context**. (c) **Confidence-graded.** (d) Every class **toggleable + overridable**. (e) **Locale-aware.** (f) Scope **pre-render text only**.

### D10 — Rules and rule-packs are toggleable data
Authored rules and named packs persist as **data, not code**; importable; the delivery vehicle for engine/voice quirk fixes (D24).

### Clarification (D5/D9) — declared vs. inferred intent
Declared intent is deterministic (no confidence gate). Inferred intent is the only confidence-graded thing, living in **system-recommended**.

### D11 — Look-ahead buffer
(a) A **producer** synthesizes ahead, a **consumer** plays, separated by the buffer. (b) Holds **finished audio tagged by sentence index**, **edge-trimmed (D27) before entry**. (c) Run-ahead in **real rendered seconds**. (d) RAM/temporary only.

### D12 — Aggregation policy *(amended v0.5.0)*
Callbacks fire **once per engine sentence-unit** (T1). Aggregate by sending multiple segments per `generate()` and slicing per sentence index; floor/ceiling/ramp-up; **slice verification (confirmed necessary, T4)**; default **one-call-per-segment**. **Amendment:** setup cost ≈ 0 (so the speed rationale is void); the floor rationale is superseded upstream by D23; **batching is retained only as register-drift prevention (D26)**; keep/kill as the streaming default is a build-phase A/B (OB-001-1).

### D13 — Interrupt & coalescing engine
(a) **Intent model:** desired position + desired play-state; one loop converges. (b) **Latest-value-wins.** (c) Continuous inputs debounced. (d) Abort sequence. (e) Nav sources: sentence/chapter skip, chapter select, scrubber. (f) Chapter natural end **not** an interrupt. (g) Voice switch keeps position. (h) Rule change flushes forward, applies next boundary. (i) Voice preview isolated. (j) **Inter-sentence pause owned at the player** — the calibrated prosodic-reset point (D26); the trimmer (D27) hands it a clean baseline. (k) Pause/resume not an interrupt. (l) Starvation = graceful hold. (m) Speed at playback rate. (n) **Invariant — buffered audio never mutated in place.**

### D14 — Control surface & audio focus
(a) Single **MediaSession** hub in a foreground service; all surfaces route through it. (b) Caller-independent commands. (c) Play requests focus. (d) Permanent loss → pause, no resume; transient → pause, auto-resume. (e) Becoming-noisy → pause. (f) Focus events write desired-play-state. (g) Background via foreground service. (h) Resume-after-kill via Media3. **[Ownership refined by D62.]**

### D15 — Storage split
(a) Three tiers (regenerable? auto-deletable? survive reinstall?). (b) **Precious** (Room): identity, position, rules/respellings, pack states, settings, library catalog, source access records, book states, asset manifest, stable rule IDs. (c) **Imported assets** (files). (d) **Rebuildable** (cache/RAM). (e) **Hard invariant:** precious physically separate from rebuildable. (f) **Save policy:** position in memory, flushed on pause/background/interrupt/switch/stop + safety tick. (g) Precious never evicted. (h) Source access record. (i) Export/import round-trip. (j) Book states precious. **[Position-flush refined by D63; book states by D67.]**

### D16 — Normalization execution model & engine stance
(a) Runs in Golem's Kotlin layer (single source of truth). (b) No reliance on engine FST files. (c) Engine normalizer **neutralized by starving** it. (d) We handle the whole class list; only residue falls through. (e) On-device catalogue characterizes residue (waiting-discovery).

### D17 — Class catalogue + certainty buckets
Cross-cutting: pass-through default; voice-bound packs; matcher vocabulary (position anchors + literals); flag behavior (best-guess immediate + review list); typed flag reason; lookup tables are data; **text pre-clean** (NFC, whitespace, strip zero-width/controls, smart-quote/dash → straight); disposition vocabulary (transform / pass-through / suppress / flag). Clusters: numbers · dates/times · abbreviations · acronyms/ALL-CAPS · symbols. *Interaction with D23:* terminal `...` and `—` keep "→ pause", rendered by the model.

### D18 — Normalization processing model
(a) Cheap **text phase** + expensive **audio phase**. (b) **Analysis pass per chapter, ahead of the playhead.** (c) Output cached, rebuildable; rule change re-derives forward only. (d) Review batched. (e) Cost posture (patterns for classes, lookups for user rules; never block first audio). (f) Low priority, yields to synthesis/playback.

### D19 — Default on/off states
**On:** number/date/abbreviation/acronym/symbol classes; pre-clean; **terminal-cue hygiene (D23)**. **Flag-on, auto-off:** Roman numerals, year-vs-count, hyphen numerics, Saint-vs-Street, unknown-acronym, slash/dash roles, borderline all-caps. **Off (opt-in packs):** domain acronyms, code/URL, legal symbols, emoji, chemical, math. **User & locale override everything.**

### D20 — Locale model
(a) **Two locales:** input ← book metadata; output ← active voice. (b) Resolution: explicit user → derived source → app default; **never silent**. (c) Locale-parameterized. (d) Device-region defaults; one pair per book. (e) Mismatch allowed. (f) CJK/script handoff = suppress-and-flag. **[F-033 elevates locale-tag unreliability to an explicit risk — see Part II "folded items".]**

### D21 — The rule tie-breaking ladder *(incl. the tier-3 amendment)*
Deterministic, never-silent resolution of `source × scope × specificity` + positional last resort:
- **1 SOURCE:** user-explicit → parsing-constraint → system-enforced → system-recommended. (Voice-packs = system-enforced + active-by-voice flag; system normalization is a peer, not a pre-stage.)
- **2 SCOPE:** book → collection → global.
- **3 SPECIFICITY** *(amended):* literal > pattern → higher anchor-count → whole-token > substring → case-sensitive > insensitive → longer matched length.
- **4 POSITION:** earliest-start → longest-span → stable-rule-ID (guarantees total order).
- **EMIT** a rebuildable resolution record (span · winner · overridden-list · branch), plain-language reasons, override badge only on genuine loss, re-derived forward on change.
- **Signal contract:** every matcher exposes `{matcher_kind, anchor_count, whole_token, case_sensitive, matched_length}` — **F-042 exposes, F-043 ranks.**

### D22 — Post-render audio-DSP layer: frame
(a) **Per-segment, pre-buffer, no cross-segment access** (measurement + ear, both engines). (b) Interface: mono float `[-1,1]` at engine sample rate, returns same shape; shared ops + per-adapter quirk hooks; output rebuildable; never mutates in place; **zero-length segment graceful, index anchored**; off by default until justified (only D27 qualifies).

### D23 — Terminal-cue hygiene (spoken branch, per-segment, after D17)
Preserve terminal *type*; repair only degenerate forms. (a) Strip space before terminal. (b) **Append a period when bare** (def. D44). (c) **Suppress quotation marks** (display keeps). (d) **Collapse a run of the same terminal**; leave mixed (`?!`) intact (def. D45). (e) **Keep `...` / `—`** in engine input (model renders the pause). (f) Piper `:`/`;` clip = adapter concern.

### D24 — Home-and-mechanism split *(operator-delegated)*
Universal text hygiene lives in the **shared core** (on by default); engine/voice-specific quirks are **voice-bound rule-packs** (data, auto-on by voice). Sorting test: *"Would this be true for a TTS engine that doesn't exist yet?"* Yes → core; No → voice-pack. **[Manifested by D40.]**

### D25 — Heavier DSP deferred to build
Crossfade/stitcher, modulation/compression, per-engine trimming **not designed now**; ear-validated on demand, added per-adapter only on need. Crossfade is the one **bounded reopen** of D22(a).

### D26 — Inter-sentence prosody continuity
Register-step drift = a real long-run fatigue risk; amplitude metrics are blind to it. **Batching retained** as cheap source-side prevention; player-owned reset pauses (D13j) + terminal-type preservation (D23) are structural defenses; pitch-aware crossfade/F0-smoothing = bounded fallback. Validated by a build-phase long-run ear test.

### D27 — Edge-silence trimming *(the one surviving post-render op)*
(a) Per-segment, trim leading/trailing near-silence to a clean zero baseline, before the buffer; no adjacent access. (b) Threshold **~0.003**. (c) Safety floor **~15 ms** (speech-protecting). (d) **Gap ownership = the player** (D13j); the trimmer never sets spacing. (e) Per-engine config (D2d). (f) Ear-validated in build.

---

# PART II — Grooming decisions (D28–D76)

> Canonical short form. Full rationale retained in Grooming Decision Log v1.0.0 / v1.1.0 / v1.2.0.

## Grooming methodology (D28–D32)

- **D28 — Four-bucket test taxonomy:** Presence & wiring · Behavior & limits · Cost & budget · Result.
- **D29-coverage — Coverage-target mapping:** logic → automated · device/system → agent-run + recorded · perceptual → guided-manual · numbers → local-measured-or-system-tagged.
- **D30-grooming — Reconciliation gate:** a per-build-tier cross-feature reconciliation pass before any coding handoff.
- **D31 — Precious-DB schema policy:** never-destructive · always-migrate · **every migration proven by an automated test** (pattern: build old schema → run migration → assert schema + row survival; first exercised at F-020).
- **D32 — Record addressing:** per-book records at book/sentence/span granularity, anchored on the F-020 identity hash + the D4b composite sentence index.

## Normalization & rules grooming (D33–D37)

- **D33** *(operator-delegated)* — F-029 stays **one feature**; the class catalogue is groomed as **components (C-029-#)** over a shared engine skeleton.
- **D34** *(operator-delegated)* — F-031 review actions author **book-scoped user-explicit rules** through F-042: **accept** → system-guess, **edit** → user-value, **reject** → **pass-through** by default (suppress opt-in). Book scope V1; global = V3/F-047. Decided tokens are claimed by their winning user rule on re-derive (no duplicate flags).
- **D35** *(operator-delegated)* — F-020 hashes **all spine itemrefs in document order, including `linear="no"`**.
- **D36** *(operator-delegated)* — canonical sentence index is **composite (chapter ordinal from spine order, sentence ordinal within chapter)**, identity-anchored; preserves D18 per-chapter independence.
- **D37** *(operator-delegated)* — F-037 live preview **ducks** active playback + renders on a **separate one-off synthesis** where feasible; fallback = **brief pause-during-preview**.

## T2 — voice & audio (D38–D45)

- **D38** — **Voice identity** = composite `(engine-kind, model-content-fingerprint, intra-model-voice-key)`, content-derived (rename/move/re-import safe). Multi-voice models expose each speaker as a distinct identity. Closes OB-045-2.
- **D39** — **Active-voice handshake:** **F-048 is the single source of truth** for the active voice; **F-045 sets** each pack's active-by-voice flag; **F-043 only reads**; **preview (F-049) never changes the active voice**. No concept owned twice.
- **D40** — **F-008 core-vs-pack manifest:** **Universal core** = D23 a–e (incl. suppress-quotes) + the edge-trim mechanism (D27). **Voice-specific** = Piper `:`/`;` clip, per-engine trim numbers; **Kokoro V1 pack ships empty**.
- **D41** *(operator-delegated)* — voice fingerprint **computed once at import, cached in the precious voice registry** (D31-governed); never recomputed at load; recompute = recovery only.
- **D42** *(operator-delegated)* — a voice switch takes effect **from the next sentence boundary**; the current sentence completes in the old voice.
- **D43** *(operator-delegated)* — the F-049 preview sample is an **app-shipped, locale-keyed fixed string set** (terminal variety), **owned by F-049**, rendered through **F-008 core only** (pack-independent).
- **D44** *(operator-delegated)* — **"bare terminal"**: a segment is bare when its last non-whitespace character, after stripping trailing spoken-branch closers, is not in `{. ! ? … —}`; applied **only to sentence-terminal segments, never clause sub-splits**. Fixture-locked.
- **D45** *(operator-delegated)* — **run-collapse + ellipsis ordering:** a run = two+ identical sentence-terminal characters → collapse to one; `…` and `—` are protected single tokens **before** collapse; mixed terminals intact; order = protect-ellipsis/dash → collapse-runs. Fixture-locked.

## T3 — the listen-loop spine (D46–D50)

- **D46** *(operator-delegated)* — the **intent loop stays live during a starvation hold**; a skip/seek is honored by latest-value-wins, not deferred. Automated test added.
- **D47** *(operator-delegated)* — a **transient focus loss that never releases** falls back, after a timeout, to **manual-resume**; the false "will auto-resume" affordance is cleared.
- **D48** *(operator-delegated)* — the **text phase runs ahead across chapter boundaries**; the producer may pre-render across a chapter end **only because** F-027/F-028 process the next chapter ahead of the playhead. Locked cross-feature dependency (OB-D48 tracks wiring).
- **D49** *(operator-delegated)* — the **slice-fallback highlight is identical** to normal (one-call-per-segment = one callback per sentence; no interpolation).
- **D50** *(operator-delegated)* — **Reading-View re-follow (V1):** "next advance" re-centres on the next sentence the audio reaches, plus a manual **"resume-follow"** affordance. Richer scroll = F-017 (V2).

## Surface architecture & scope (D51–D53)

- **D51** *(operator-directed)* — **three primary surfaces:** Reading View (F-014) ↔ Now Playing (F-015) ↔ Image Viewer (F-074). Now Playing is the **hub**, hosting the embedded sync-preview (F-073) + action row (F-075). The audio↔text sync has **one source (F-016)** consumed by every text surface.
- **D52** *(operator-directed; revised)* — **V1 foundation, V2 bodies, no-redesign contract.** Frozen in V1: **(1) sync** — one source (F-016), stable highlight interface, no surface implements its own; **(2) gesture grammar** — fixed vocabulary (tap-passage → reader; horizontal swipe → adjacent surface; tap-cover → image); **(3) surface topology + named slots**. V1 built: F-073 (embedded preview), F-075 (host + rotation), image destination + gesture seam (minimal V1 surface). V2 bodies behind seams: F-074 viewer, F-076 search, F-077 sleep.
- **D53** *(operator-directed)* — **F-003 owns the full chapter-navigation UI** (marquee, chevron, modal) **and the scrubber time display**: elapsed **mm:ss** (playback-time accumulator) + position as a **sentence ratio** (current : chapter sentence count). Remaining-format = OB-D53.

## F-003 navigation & scrubber (D54–D55)

- **D54 — Skip behavior:** sentence skip moves desired-position **±1**; chapter skip — *next* → next chapter start; *back* → if **>~3 s** into a chapter, jump to its start, else the previous chapter.
- **D55 — Scrubber:** **sentence-granular + chapter-scoped** — represents position **within the current chapter**, snaps to sentence boundaries (seek sets a desired sentence; the loop converges); elapsed mm:ss is chapter-scoped; cross-chapter movement via skip/modal, not the scrubber.

## Surface cluster (D56–D58)

- **D56 — Embedded preview (F-073):** fixed-height frame, current sentence centered with ~2–3 sentences of **display-branch** text (quotes intact), F-016 highlight, content scrolls within the frame, not user-resizable V1; tap → Reading View; horizontal swipe → adjacent surface.
- **D57 — Action row (F-075):** **rotation-lock** pins orientation (open/closed-lock icon); V1 renders the bar with rotation active; the **search + sleep slots are reserved but not rendered** until F-076/F-077 ship — no dead buttons (coming-soon placeholders = OB-075-slots).
- **D58 — Minimal V1 image surface (F-074):** V1 destination shows the **book cover** (+ current-chapter images, simple single/list); tap-cover navigates here; **requires F-018 to extract image resources** (cover + inline) — a parser scope bump (OB-D58). Full pan/zoom/gallery = F-074 body (V2).

## Playback shaping (D59–D61)

- **D59 — Speed (F-005):** **0.5×–3.0× in 0.1× steps**, live at the player layer, with presets (1.0 / 1.25 / 1.5 / 2.0); above ~2.5× is "allowed, not promised".
- **D60 — Inter-sentence pause (F-006):** user-adjustable via **named steps (Tight / Natural / Relaxed)** setting the player-owned boundary pause; applies live; named steps, not raw ms.
- **D61 — Volume (F-007):** V1 rides **system media volume**; **no separate in-app gain in V1** (in-app trim = V2).

## T5 — system integration (D62)

- **D62 — Ownership map (no double-ownership):** **F-009** owns the MediaSession hub object + routing; **F-002** owns the four transport commands written through it; **F-012** owns focus/noisy policy; **F-010/F-011/F-013** own service/surface/resume.

## T6 — persistence & library robustness (D63–D68)

- **D63 — Position save (F-058):** in-memory, flushed on pause/background/interrupt/voice-switch/stop + a **~10 s safety tick**; restore reads the saved composite index (D36).
- **D64 — Sources (F-021):** one book = identity (D3 hash); many files may share it; the user picks **one active source**; same-identity re-import attaches as a source, **never a duplicate book**.
- **D65 — Availability/relink (F-022):** a missing source is **unavailable, never auto-deleted**; same-hash reappearance **auto-relinks**; a broken URI **re-prompts**. Book + precious data survive a missing source.
- **D66 — Reparse (F-023):** user-triggered, runs against the active source; precious data **migrates onto the reparsed structure, never dropped** (D3h + D31). If segmentation changes, sentence-anchored data needs remapping (OB-D66).
- **D67 — Book states (F-024):** finished / favorite / bookmarks / timestamps; bookmarks anchor to the **composite sentence index** (D36) to survive reparse/edition shifts; new/unread/in-progress are **derived**, not stored. Hosted on Book Details.
- **D68 — Robustness posture:** all T6 precious writes are **D31-governed**; every missing/unavailable path **degrades visibly, never silently** (aligned with F-019).

## T8 — accessibility & shell (D69–D74)

- **D69 — Accessibility = four tested contracts:** **F-066** high-contrast, **F-067** reduced-motion, **F-068** text-scaling (scale multiplier, never hardcoded sizes), **F-069** keyboard nav. Every other surface's a11y check **defers to these** — one source of truth per axis.
- **D70 — Reduced-motion scope:** F-067 governs the **F-016 highlight animation** and the **F-015 starvation indicator**; both get a non-motion equivalent.
- **D71 — Settings is a host (F-064):** F-064 owns the settings shell + navigation; each **setting is a component of its parent feature**; F-064 never owns a setting's behavior.
- **D72 — Onboarding critical path (F-070):** permissions → **first voice-model import (hard gate)** → first book add. No model → a clearly-explained "add a voice to begin" state, never a broken silent player (gate-at-play-time relax = OB-072).
- **D73 — Onboarding graceful:** each step degrades gracefully (no model → gate; no book → empty-library guidance, F-019); no dead ends.
- **D74 — Onboarding is accessible:** first-run is itself covered by D69 (keyboard/contrast/scaling tested), not bolted on after.

## Operator accessibility additions (D75–D76)

- **D75** *(operator-directed)* — **Accessibility reading-display override layer (F-078):** a reading-view control set that **overrides the active theme** (accessibility wins over F-065 on shared properties). **V1:** dyslexia-friendly font override, line-spacing, bold/weight. **V2 (seam reserved):** line-numbers + line-ruling. Precedence + override are the locked contract (OB-078).
- **D76** *(operator-directed)* — **Sync-glow controls (V2, V1 parameter seam):** the F-016 highlight exposes **size, contrast, fade** as parameters **from V1** so V2 is a settings drop-in (no re-render); surfaced as one "sync glow intensity" control (1-vs-3 split = OB-076); governed by F-067 reduced-motion (D70).

## Folded items (review / external analysis / operator note)

- **F-033 locale → explicit risk.** Epub language tags are unreliable on the webnovel/lightnovel corpus, so the device-region fallback may fire constantly. F-033 carries an explicit **risk requirement** (not just an OB), with **visible-not-silent** fallback (aligned with D33/never-silent). V1 surfaces the resolved locale + its source.
- **Wishlist candidates registered** (Inventory v0.4.0; not V1): *curly-quotes-in-display* (move quote-straightening to spoken-only so the Reading View can show curly quotes; current V1 shows straightened quotes per D4a/D17g/D23c); *user-created highlight/annotation* (distinct from the F-016 playback highlight; D32 readies its data shape).
- **Performance-note criteria → F-001 integrated budget (T-001-C1).** The pass condition is the operator's real bar: **no thermal throttling · no system CPU-usage warnings · no visibly-watchable battery drain · while sustaining target audio quality.** This replaces any fake per-feature number.

---

# PART III — Feature register (F-001–F-078)

> The detailed inventory lives in **Candidate Feature Inventory v0.4.0** (v0.2.0 + v0.3.0-delta + v0.4.0-delta). Summary of the V1/V2 split below; that document is the per-feature index of record.

**V1 — the walking skeleton + its tiers**
- **Substrate / identity / text (T0/T1/T7):** F-057, F-020, F-018, F-019, F-065, F-027, F-028, F-029 (+ F-030/F-031/F-033), F-036, F-037, F-042, F-043, F-044, F-045.
- **Voice & audio (T2):** F-048, F-049, F-008.
- **Listen-loop spine (T3):** F-001, F-016, F-002, F-014, F-015.
- **Navigation & surfaces:** F-003 (chapter UI + scrubber), F-073 (embedded preview), F-075 (action-row host + rotation), F-074 (minimal V1 image surface / seam).
- **Playback shaping:** F-004–F-007.
- **System integration (T5):** F-009–F-013.
- **Persistence & robustness (T6):** F-058, F-021–F-024.
- **Accessibility & shell (T8):** F-064, F-066–F-070, **F-078**.

**V2 — bodies behind V1 seams**
- F-074 full image viewer (pan/zoom/gallery), F-076 in-book search, F-077 sleep timer, F-017 richer scroll/follow, and the D76 sync-glow control.

---

# Appendix A — Open-Boundary ledger (consolidated)

> Every open boundary across all grooming layers, each with a single owner / resolution point. Closed OBs are recorded in the historical Logs.

| OB | What's open | Owner / resolves at |
|---|---|---|
| OB-001-1 | Aggregation keep/kill as the streaming default (long-run ear A/B). | Build (D26) |
| OB-001-2 | Integrated listen-loop budget thresholds (T-001-C1) from the §C contributor ledger. | Build / D30 |
| OB-002-1 | Media3 resume-after-kill wiring + Android-17 foreground hardening (policy locked). | Build |
| OB-008-1 | Per-engine trim numbers + exact V1 Piper pack contents (ear-tuned). | Build (with F-045) |
| OB-008-2 | Display-branch dialogue prosody (future annotate op, D5b). | Future grooming |
| OB-014-1 | Reading-View scroll detail (lock-follow; Now-Playing vs Reading split). | F-017 (V2) |
| OB-015-1 | Brief-hold starvation-indicator screen-reader announcement. | Build decision |
| OB-016-1 | Clause-step highlight stability across sentence length. | F-017 (V2) |
| OB-018-4 | Block-structure markers = typed structural tokens (encoding = build). | F-018 build |
| OB-048-1 | Voice-fingerprint hash algorithm/extent for large model files (policy locked, D41). | Build |
| OB-065-4 | CI-run no-hardcode lint (color/size/radius/shadow/spacing/duration). | F-065 / build |
| OB-preview-concurrency | Duck (concurrent) vs pause for preview on real hardware (default = pause). | Build (D37) |
| OB-D44 | F-028 sentence-terminal vs clause sub-split tagging (so D44 gates correctly). | F-028 reconcile / build |
| OB-D48 | Cross-chapter text-ahead wiring confirmation (dependency locked, D48). | Build |
| OB-D53 / OB-D53-scrubber-format | Scrubber remaining-time exact format. | F-003 grooming |
| OB-D58 / OB-D58-image | F-018 cover + inline image extraction (parser scope bump). | F-018 v-next / build |
| OB-D66-reparse-remap | How sentence-anchored precious data remaps when reparse changes segmentation. | F-023 / build |
| OB-072-onboarding-gate | Hard voice gate vs gate-at-play-time. | Operator / F-070 |
| OB-075-slots | Reserved-vs-"coming-soon"-placeholder action-row slots. | Operator / F-075 |
| OB-076-glow | Sync-glow as 1 control vs 3. | F-016 / F-067 (V2) |
| OB-078-precedence | Accessibility-overrides-theme precedence test detail. | F-078 / build |
| OB-033-locale-risk | Locale-tag unreliability frequency on the real corpus; fallback UX (now a tracked risk). | F-033 / build |
| OB-surface-V2 | V2 surface bodies (F-074/F-076/F-077) slot into V1 seams; must not touch frozen sync/gesture/topology. | Surface-tier grooming (V2) |

*Carried (resolved-but-recorded): OB-020-2→D35 · OB-027-3→D36 · OB-029-2/OB-031-1→D34 · OB-031-2→D34 · OB-042-3/OB-043-1→D21 amendment · OB-037-2→D37 · OB-043-2/OB-045-2→D38/D39 · OB-045-1→D40 · OB-029-5/OB-030-3→F-030 · OB-044-2 closed · F-042 R4 schema-reserved · F-043 R8 built in F-027.*

---

# Appendix B — Per-feature revision ledger (doc-production worklist)

> The mechanical document work that reconciles per-feature docs to this spec. **None of this changes a decision** — it produces/commits/bumps the per-feature docs. Sequenced by the Reconciliation Plan v1.0.0 Batches 2–5.

**COMMIT (drafted-in-chat, uncommitted) → then BUMP to v1.0.1**
| Doc | Folds |
|---|---|
| F-048 | D41, D42 |
| F-049 | D43; merge preview-concurrency OB |
| F-008 | D44, D45 |
| F-001 | D46, D48, **integrated-budget criteria** |
| F-002 | D47, D62 |
| F-016 | D49, **D76 glow-parameter seam** |
| F-014 | D50 |
| F-015 | OB-015-1 (a11y announcement); D51 host-slot relationship |

**Already committed → BUMP to v1.0.1**
| Doc | Folds |
|---|---|
| F-020 | D35 + D31 migration test |
| F-027 | D36 |
| F-028 | D36 + OB-D44 |
| F-018 | OB-018-4 + **D58 image extraction (scope bump)** |
| F-029 | confirm |
| F-030 | three-state + ALL-CAPS threshold |
| F-031 | D34 |
| F-033 | locale + never-silent + **explicit risk** |
| F-042 | signal contract; collection schema-reserved |
| F-043 | tier-3 semantics; R8; handshake merge |
| F-037 | D37 |
| F-044 | OB-044-2 |
| F-045 | handshake merge |
| F-057 | D31 pattern |
| F-019 | confirm graceful-unavailability (already v1.0.1/.2) |
| F-065 | confirm CI no-hardcode lint (already v1.0.2) |
| F-036 | no change |

**CREATE (new docs)**
| Doc | Target | Notes |
|---|---|---|
| F-003 | V1 | Chapter UI + scrubber + time model (D53–D55) |
| F-073 | V1 | Embedded sync-preview (D56) — consumes F-016 |
| F-075 | V1 | Action-row host + rotation (D57) |
| F-074 | V1 seam / V2 body | Image surface + F-018 dependency (D58) |
| F-076 / F-077 | V2 | In-book search / sleep timer (stubs now) |
| F-078 | V1 + V2 reserved | Accessibility reading-display overrides (D75) |
| F-004–F-007 | V1 | Thin docs (D13f / D59 / D60 / D61) |
| F-009–F-013 | V1 | Thin docs (D14 + D62 ownership map) |
| F-058, F-021–F-024 | V1 | T6 docs (D63–D68) |
| F-064, F-066–F-070 | V1 | T8 docs (D69–D74) |

---

# Appendix C — Running ledgers & shared-resource ownership

**Integrated listen-loop budget (the only budget) — F-001 T-001-C1.** Pass condition: **no thermal throttling · no system CPU-usage warnings · no visibly-watchable battery drain · while sustaining target audio quality.**
**Contributors (all point here):** F-057 (state-flush) · F-018 (chapter-boundary parse) · F-027 (per-chapter text phase; F-028/F-029/F-043 run inside it) · F-008 edge-trim (per-segment) · F-001 buffer/loop overhead · UI-under-load jank (F-014/F-015/F-016/F-073). Import-time (F-020) and settings/review/preview surfaces (F-019, F-030, F-031, F-033, F-042, F-044, F-049) are **N/A**.

**Dependency spine.** F-057 → F-020 → F-018/F-019/F-065 → F-027/F-028 → F-029 (+F-030/F-031/F-033) → F-042 → F-043 (+F-036/F-037/F-044/F-045). **T2:** F-048 → {F-045 sets flag, F-043 reads}; F-008 ← F-027 slot + F-045 packs; F-033/F-048 feed synthesis. **T3 spine:** F-008 → F-001 → {F-016, F-002, F-014/F-015, F-073}; **F-003** rides F-001's intent loop. **Surfaces:** F-073/F-075/F-074 ride the frozen sync/gesture/topology (D51/D52); **F-078** overrides F-065; **F-074** depends on F-018 image extraction.

**Shared-resource single-ownership map (one owner each — the D30 invariant).**
| Shared resource | Single owner | Definition |
|---|---|---|
| Precious schema | D15b tier + D31 policy | F-020/F-058/F-024/F-042/F-048 write their own records |
| Composite sentence index | F-027 assigns (D36) | consumed by F-016/F-001/F-058/F-024/F-003 |
| Active voice | F-048 (D39) | F-045 sets flags, F-043 reads, F-049 never changes it |
| Audio↔text sync | F-016 (D51/D52) | every text surface consumes; none implements its own |
| Gesture grammar | D52 V1 vocabulary | V2 attaches; no new gesture system |
| MediaSession hub | F-009 (D62) | F-002 commands · F-012 focus · F-010/F-011/F-013 service/surface/resume |
| Settings host | F-064 (D71) | each setting is a component of its parent feature |
| Accessibility axes | F-066/F-067/F-068/F-069 (D69) | every surface defers; one source per axis |
| Theme vs a11y precedence | F-078 over F-065 (D75) | accessibility wins on shared properties |

---

# Appendix D — Testing register (T1–T5)

Carried unchanged from Spec v0.5.0 (the five on-device feasibility passes backing D1/D12/D22–D27). **Owed build-phase ear validation:** long-run register-drift listen; batching keep/kill A/B; edge-trim floor tuning; Kokoro sustained-load/thermal behavior. **Owed carried:** abort cleanliness; skip-to-sound gap; inter-sentence pause mechanism; audio-focus behaviors; background-play survival; Room round-trip; persisted-URI survival. *D21–D27 logic and D44/D45 fixtures are unit-testable; audio judgments are guided-manual by design.*

---

## Wishlist, TBD, Waiting-discovery (carried)

**Wishlist (parked; never blocks work):** engine controls-preview at selection; per-voice engine tag pill; app-side pitch-shift; multi-speaker/full-cast + emotion; voice cloning; voice preview from book text; highlight presentation options; separate Now-Playing/Reading highlight settings; highlight stable across sentence length; **optional "Prepare Book" pre-render** (strengthened by the Kokoro RTF finding); resources-hub extensions; book metadata viewer/editor; two-pass composition compatibility checker; cross-book/global rule promotion (post-v1); marginal batching naturalness tuning; **curly-quotes-in-display** (new); **user-created highlight/annotation** (new).

**TBD:** math/formula cluster · chemical-formula cluster · footnote-structure handling · emoji pack · code/URL pack · pattern-era specificity · display artifact-tidying · app-side pitch approach · rule-pack format + UI · formal Principles Register + coverage target · collection management.

**Waiting-discovery:** romanize feasibility · "match me" voice input · true phoneme-level override · us-vs-engine normalization division.

---

*End Design Spec v0.6.0 — single consolidated authority (D1–D76 · F-001–F-078). Prior versions (Spec v0.5.0; Grooming Decision Log v1.0.0/v1.1.0/v1.2.0) retained intact for rollback. D30 reconciliation gate: passed at the decision-architecture level (companion report). Next: Reconciliation Plan Batches 2–5 (per-feature doc production), then the first coding-handoff Step SOW for the walking skeleton.*
