---
id: PHASE-INDEX
tier: state
status: active
updated: 2026-07-02
if-incomplete: "Return to current-state.md."
---
# Phase index

| Phase | Version | Goal (meaningful build) | Status |
|-------|---------|-------------------------|--------|
| **P1** | V0 | **Walking skeleton** — load one EPUB, stream it as speech with synchronized highlight and basic transport, in both views | **accepted** |
| P2+ | V1 | Widen each tier to full V1 (full normalization/rules, surfaces, persistence, accessibility, onboarding) | planned |

## Phase P1 — Walking Skeleton — proposed Step ladder (vertical slice, bottom-up)
> One narrow change per step; each climbs the rungs and commits. Build order follows the
> dependency spine so every step runs on something real beneath it.

| Step | Delivers | Feature(s) |
|---|---|---|
| **S1** | Project bootstrap: Android toolchain heartbeat | infrastructure |
| **S2** | Storage substrate: precious / rebuildable tiers + D31 migration-test harness | F-057 |
| **S3** | Book identity: EPUB structural spine read + D84 content hash + `book_identity` | F-020 |
| **S4 — done** | Text → sentences: thin EPUB text extraction + composite index + segmentation | F-018 (thin), F-027, F-028 |
| **S5 — done** | One voice speaks: voice load + audio pipeline → first synthesized sentence | F-048, F-008 |
| **S6 — next** | Streaming engine: producer/consumer buffer + intent loop → continuous chapter playback | F-001 |
| **S7** | Synchronized highlight follows the audio | F-016 |
| **S8** | Transport (play/pause/skip) through the MediaSession hub | F-002, F-009 (thin) |
| **S9** | The two surfaces, minimal: Reading View + Now Playing | F-014, F-015 |
| **G4 — done** | Phase acceptance: plays one book end-to-end; registers current; baseline archived | — |

*Retro (IMP-ids): (none yet)*
