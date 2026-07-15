---
id: PHASE-INDEX
tier: state
status: active
updated: 2026-07-15
if-incomplete: "Return to current-state.md."
---
# Phase index

| Phase | Version | Goal (meaningful build) | Status |
|-------|---------|-------------------------|--------|
| **P1** | V0 | **Walking skeleton** — load one EPUB, stream it as speech with synchronized highlight and basic transport, in both views | **accepted** |
| **P2** | V1 | **Accessible Shell** — themed, high-contrast-capable, scalable, motion-optional, keyboard-operable, with a real settings surface | **at G4 — gate open, S18 inserted** |
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
> **Note:** `phase-P2.md` names the D98 prototype as the gate's visual contract; it was
> written before S13 minted **D103**, which superseded it with v0.3.0. The gate ran
> against **v0.3.0** — reconciled at G4, 2026-07-15.

| Step | Delivers | Feature(s) |
|---|---|---|
| S12 — done | Theme foundation: design tokens, light + dark themes, persisted choice, live switch, no-hardcode guard (closeout: T-065-R2 passed by operator 2026-07-08; merged at 1a08e91) | F-065 |
| S13 — done | Settings shell (thin) + navigation topology (D102) + theme picker in its home; visual contract v0.3.0 (D103); Settings Map registry (D104) (closeout: screenshots approved by operator 2026-07-13; merged at e1fe9dd) | F-064 |
| S14 — done | High contrast: hcDark + hcLight value-sets (D106), AAA target (D105), KV-row preference (D107), toggle registered via D104, central contrast test (closeout: look-check passed post-commit pre-merge 2026-07-13; merged at a87f5be) | F-066 |
| S15 — done | Text scaling: five-step in-app multiplier at the provider seam (D108), stepper under Accessibility, central reflow proof over four surfaces (D109), T-066-B4 scaling half cleared (closeout: look-check preceded acceptance verb 2026-07-13; merged at 9c97aec) | F-068 |
| S16 — done | Reduced motion: motion-override at the provider seam (D110), semantics + starvation announcement (D111, resolves OB-015-2), OS-or-in-app default (D112, resolves OB-067-3), glow seam (D76), toggle under Accessibility (closeout: accepted 2026-07-14 after look-check + TalkBack; correction commit superseded the agent's pre-written acceptance records; merged at b507c75) | F-067 |
| S17 — done | Keyboard navigation: reachability + activation over all four surfaces (D113, resolves OB-069-2; absorbs T-064-B4), focusRing token + shared ring mechanism (D114), traversal-order contract with ratified content correction (D115), destination-change focus placement (D116), central keyboard test; Robolectric spike failed → pre-authorized S23 device fallback passed 4/4; T-069-B5 deferred to the F-070 phase (closeout: accepted 2026-07-14 after look-check + full T-069-R1 keyboard-only walkthrough; agent-written operator labels and D116 ledger entry corrected in the closeout commit; merged at **7726eed**) | F-069 |
| **S18 — active** | **Inserted at G4 by D117.** Status-bar inset respected on all four surfaces (F1, real defect — no inset handling existed); transport labels → glyphs ▶/❚❚/▶❚/■ with accessible names preserved and testTags unchanged (F2, cosmetic; D118); both-edges verification (bottom already correct via Material3 `NavigationBar` — verified, not modified) | F-002, F-014, F-015, F-064 |
| **G4 — open** | Phase acceptance. Checks run 2026-07-15: contract vs **D103 (v0.3.0)** — conforms, F1/F2 logged; **T-064-R2 — pass** (S13 deferral closed); **T-068-R1 all-axes sweep — pass** (S15 carried note closed); **PR-7 re-scored — MET** (from *gap recorded, D95* at the P1 gate); coverage/confidence — approved. **Gate held open pending S18**, then accepts; P2 retro follows | — |

*Retro (IMP-ids): candidate **IMP-005** (agents never write operator verbs or ledger
entries; closeout fields stay literal placeholders; verification greps for premature
acceptance language) — minted from the S16 correction, **two further data points at
S17**, to be ratified at the P2 retro. Also for the retro as a **positive** pattern:
spike-first task gating JVM-vs-device test placement (S17) — fenced fallback, zero
improvisation; and the retry-cap + scope-fence holding through three harness walls and
two SOW amendments with every halt legitimate.*

*Forward input (not a P2 item): **F-073 spec delta candidate** — operator challenge to
R2's centered-scroll preview model in favour of a batched window; recorded 2026-07-15 at
`reference/F-073-preview-window-delta-candidate.md`, resolves at the F-073 design
session (P3+).*
