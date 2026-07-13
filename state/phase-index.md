---
id: PHASE-INDEX
tier: state
status: active
updated: 2026-07-13
if-incomplete: "Return to current-state.md."
---
# Phase index

| Phase | Version | Goal (meaningful build) | Status |
|-------|---------|-------------------------|--------|
| **P1** | V0 | **Walking skeleton** — load one EPUB, stream it as speech with synchronized highlight and basic transport, in both views | **accepted** |
| **P2** | V1 | **Accessible Shell** — themed, high-contrast-capable, scalable, motion-optional, keyboard-operable, with a real settings surface | **entered (G1)** |
| P3+ | V1 | Remaining V1 tiers (library, sources, normalization/rules, voice manager, persistence widening, onboarding) | planned |

## Phase P1 — Walking Skeleton — Step ladder (as executed)
> One narrow change per step; each climbed the rungs and committed. S10 and S11 were
> inserted during G4 (D96, D97) when the gate surfaced two real defects.

| Step | Delivered | Feature(s) |
|---|---|---|
| S1 — done | Project bootstrap: Android toolchain heartbeat | infrastructure |
| S2 — done | Storage substrate: precious / rebuildable tiers + D31 migration-test harness | F-057 |
| S3 — done | Book identity: EPUB structural spine read + D84 content hash + `book_identity` | F-020 |
| S4 — done | Text → sentences: thin EPUB text extraction + composite index + segmentation | F-018 (thin), F-027, F-028 |
| S5 — done | One voice speaks: voice load + audio pipeline → first synthesized sentence | F-048, F-008 |
| S6 — done | Streaming engine: producer/consumer buffer + intent loop → continuous chapter playback | F-001, F-004 |
| S7 — done | Synchronized highlight follows the audio | F-016 |
| S8 — done | Transport (play/pause/skip) through the MediaSession hub | F-002, F-009 (thin) |
| S9 — done | The two surfaces, minimal: Reading View + Now Playing | F-014, F-015 |
| S10 — done | App bootstrap: real `MainActivity` path wired end-to-end (inserted, D96) | infrastructure |
| S11 — done | End-of-book clean stop defect fixed (inserted, D97) | F-001 |
| **G4 — accepted** | Phase acceptance: one book played end-to-end unattended on the S23; baseline archived at `archive/V0-P1/`; PR-7 gap recorded, not silently passed (D95) | — |

*Retro (IMP-ids): IMP-001 (verify branch state before multi-command pastes), IMP-002
(visual prototype — closed by D98), IMP-003 (ground SOWs in real spec + real code),
IMP-004 (accessibility scoped in at next G1 — satisfied by P2's existence).*

## Phase P2 — Accessible Shell — Step ladder (entered per D99/D100)
> Dependency-ordered: the token floor first, then the room the toggles live in, then
> the axes. F-070 (onboarding) is explicitly deferred by D100 to the phase that builds
> library + voice import. Full G1 entry: `state/active/phase-P2.md`.

| Step | Delivers | Feature(s) |
|---|---|---|
| S12 — done | Theme foundation: design tokens, light + dark themes, persisted choice, live switch, no-hardcode guard (closeout: T-065-R2 passed by operator 2026-07-08; merged at 1a08e91) | F-065 |
| S13 — done | Settings shell (thin) + navigation topology (D102) + theme picker in its home; visual contract v0.3.0 (D103); Settings Map registry (D104) (closeout: screenshots approved by operator 2026-07-13; merged at e1fe9dd) | F-064 |
| **S14 — active** | High contrast: hcDark + hcLight value-sets (D106), AAA target (D105), KV-row preference (D107), toggle registered via D104, central contrast test | F-066 |
| S15 | Text scaling: OS + in-app combined multiplier, reflow-not-clip | F-068 |
| S16 | Reduced motion: contract + toggle + non-motion equivalents (highlight, starvation) | F-067 |
| S17 | Keyboard navigation: reachability, focus order, visible focus | F-069 |
| G4 | Phase acceptance: screenshots vs the D103 (v0.3.0) prototype contract; PR-7 re-scored | — |

*Retro (IMP-ids): (none yet)*
