---
id: DESIGN-CHARTER
tier: reference
status: locked
updated: 2026-06-28
if-incomplete: "Full clause text lives in foundation/design-spec.md (Parts I–II)."
---
# Design Charter — Golem Reader (locked decisions D1–D76)

All 76 decisions are **locked**. This is the scannable register (ID · one-line · type);
the authoritative full text is in `foundation/design-spec.md`. New build-time decisions
append to `ledgers/decision-log.md` as D77+.

**Type key:** (D) operator-delegated · (O) operator-directed · (—) jointly built in chat.

| D# | One-line | Type |
|---|---|---|
| D1 | Sentence/clause highlight from per-sentence playback boundaries (engine-independent) | — |
| D2 | VoiceEngine interface; universal vs capability-gated; quirks in adapter | — |
| D3 | Book identity = hash of spine content bytes in reading order; many sources, one book | — |
| D4 | Text pipeline: parse→clean→shared rules→segment→fork; sentence is shared anchor | — |
| D5 | Authored rules: scope×target×source×operation; v1 replace+suppress | — |
| D6 | Pronunciation authoring via live preview; output = respelling | — |
| D7 | Segmentation: ICU BreakIterator + thin rules; one synth call/segment; clause sub-split | — |
| D8 | Engine lifecycle: book independent of engine; switch keeps position | — |
| D9 | System normalization: Golem-owned, class-organized, confidence-graded, toggleable | — |
| D10 | Rules/packs are toggleable data, not code; importable | — |
| D11 | Look-ahead buffer: producer/consumer; audio tagged by sentence index, edge-trimmed; RAM only | — |
| D12 | Aggregation: callback per sentence-unit; batching = register-drift prevention; default one-call/segment | — |
| D13 | Interrupt/coalescing: intent loop (position+play-state), latest-value-wins; buffer never mutated in place | — |
| D14 | Control surface: single MediaSession hub in foreground service; focus policy; resume-after-kill | — |
| D15 | Storage split: precious(Room)/imported/rebuildable; hard separation; save policy + tick | — |
| D16 | Normalization runs in Golem's Kotlin layer; engine normalizer starved | — |
| D17 | Class catalogue (numbers/dates/abbr/acronyms/symbols) + certainty buckets + pre-clean | — |
| D18 | Normalization: text phase + audio phase; analysis per chapter ahead of playhead; review batched | — |
| D19 | Default on/off states per class; user & locale override | — |
| D20 | Locale model: input(book) + output(voice); never silent | — |
| D21 | Rule tie-breaking ladder: source×scope×specificity×position; tier-3 signal contract | — |
| D22 | Post-render DSP: per-segment, pre-buffer, no cross-segment access; off until justified | — |
| D23 | Terminal-cue hygiene (spoken branch): strip-space/append-period/suppress-quotes/collapse-runs/keep …— | — |
| D24 | Home-and-mechanism split: universal hygiene core vs voice-bound packs | D |
| D25 | Heavier DSP deferred to build; crossfade = bounded reopen | — |
| D26 | Inter-sentence prosody continuity: batching retained; pitch-aware crossfade = fallback | — |
| D27 | Edge-silence trimming: per-segment to clean baseline (~0.003 / ~15ms); player owns gap | — |
| D28 | Four-bucket test taxonomy: presence&wiring / behavior&limits / cost&budget / result | — |
| D29 | Coverage mapping: logic→automated, device→agent-run, perceptual→guided-manual, numbers→measured | — |
| D30 | Reconciliation gate per build-tier before any coding handoff | — |
| D31 | Precious-DB schema: never-destructive, always-migrate, every migration proven by automated test | — |
| D32 | Record addressing at book/sentence/span, anchored on identity hash + composite index | — |
| D33 | F-029 one feature; class catalogue as components | D |
| D34 | F-031 review authors book-scoped F-042 rules; reject=pass-through default | D |
| D35 | F-020 hashes all spine itemrefs in document order incl linear=no | D |
| D36 | Sentence index = composite (chapter ordinal, sentence ordinal), identity-anchored | D |
| D37 | Live preview ducks playback + separate synthesis; fallback pause | D |
| D38 | Voice identity = (engine-kind, model-content-fingerprint, intra-model-voice-key) | — |
| D39 | Active-voice handshake: F-048 single source; F-045 sets flags; F-043 reads; preview never changes | — |
| D40 | F-008 core-vs-pack manifest: suppress-quotes core; Piper clip pack; Kokoro V1 pack empty | — |
| D41 | Voice fingerprint computed once at import, cached precious; never recomputed at load | D |
| D42 | Voice switch takes effect next sentence boundary; current sentence completes | D |
| D43 | F-049 preview = app-shipped locale-keyed sample, rendered through F-008 core only | D |
| D44 | "Bare terminal" definition; append-period only on sentence-terminal segments | D |
| D45 | Run-collapse + ellipsis-protection ordering; fixture-locked | D |
| D46 | Intent loop stays live during starvation hold | D |
| D47 | Transient focus-loss never-releasing → manual-resume after timeout | D |
| D48 | Text phase runs ahead across chapter boundaries; locked cross-feature dependency | D |
| D49 | Slice-fallback highlight identical to normal; no interpolation | D |
| D50 | Reading-View re-follow: next sentence re-centres + manual resume-follow | D |
| D51 | Three surfaces: Reading↔NowPlaying↔Image; one sync source (F-016) | O |
| D52 | Surface set: V1 foundation frozen (sync/gesture/topology), V2 bodies behind seams | O |
| D53 | F-003 owns chapter-nav UI + scrubber time model | O |
| D54 | Skip behavior: sentence ±1; chapter-back 3s rule | — |
| D55 | Scrubber sentence-granular, chapter-scoped | — |
| D56 | Embedded preview: fixed frame, display-branch text, F-016 highlight | — |
| D57 | Action-row rotation lock + reserved (unrendered) search/sleep slots | — |
| D58 | Minimal V1 image surface (cover); requires F-018 image extraction | — |
| D59 | Speed 0.5–3.0× / 0.1 steps / presets, live | — |
| D60 | Inter-sentence pause named steps (Tight/Natural/Relaxed), live | — |
| D61 | Volume = system media volume; no in-app gain V1 | — |
| D62 | T5 ownership map: F-009 hub, F-002 commands, F-012 focus, F-010/11/13 service/surface/resume | — |
| D63 | Position save flushed on pause/bg/interrupt/switch/stop + ~10s tick | — |
| D64 | One book, many sources, explicit active source | — |
| D65 | Missing≠deleted; same-hash relink; broken URI re-prompts | — |
| D66 | Reparse against active source, never destructive; remap OB | — |
| D67 | Book states + bookmarks = composite index; new/unread derived | — |
| D68 | T6 robustness: precious writes D31-governed; degrade visibly | — |
| D69 | Accessibility = four tested contracts (contrast/motion/scaling/keyboard) | — |
| D70 | Reduced-motion covers highlight + starvation indicator | — |
| D71 | Settings is a host; each setting a component of its parent feature | — |
| D72 | Onboarding: voice-model import is a hard gate | — |
| D73 | Onboarding degrades gracefully; no dead ends | — |
| D74 | Onboarding obeys the accessibility contract | — |
| D75 | Accessibility reading-display overrides (F-078); accessibility > theme | O |
| D76 | Sync-glow controls V2, V1 parameter seam | O |
