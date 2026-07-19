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
| **P2** | V1 | **Accessible Shell** — themed, high-contrast-capable, scalable, motion-optional, keyboard-operable, with a real settings surface | **accepted** (G4, 2026-07-15; baseline `archive/V1-P2/`) |
| **P3** | V1 | Next tier — likely library + sources (F-019, F-021, F-022, F-024) | **next — G1 pending, own session** |
| P4+ | V1 | Remaining V1 tiers (normalization/rules, voice manager, persistence widening, onboarding) | planned |

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
| S18 — done | **Inserted at G4 by D117.** Status-bar inset respected on all four surfaces via one `statusBarsPadding()` at the app root (F1, real defect — no inset handling existed anywhere); transport labels → glyphs PlayArrow / Pause / **PlayCircle** / Stop with accessible names preserved and testTags unchanged (F2, killed permanently; D118 **with a ratified correction** — the specified ▶❚ was `SkipNext`, i.e. "next track", a design-session error caught at verification); bottom edge audited as already correct (Material3 `NavigationBar`) and **verified, not modified**. Pre-authorized `material-icons-extended` added. Closeout findings: agent edited `current-state.md` unauthorized (reverted; fourth IMP-005 data point) and skipped the pre-commit guard (root cause: the design session's instruction contradicted the docs-with-code hook → **IMP-006**); the skipped test gate was closed by the operator running the JVM suite himself (closeout: accepted 2026-07-15 after look-check + T-018-R3 both-edges device walk; merged ff-only 2026-07-15) | F-002, F-014, F-015, F-064 |
| **G4 — accepted 2026-07-15** | Phase acceptance. D103 (v0.3.0) contract comparison — **conforms**, F1/F2 found and remediated by S18; **T-064-R2 — pass** (S13 deferral closed); **T-068-R1 all-axes composition sweep — pass** (S15 carried note closed); **PR-7 — MET**, from *open, gap recorded (D95)* at the P1 gate; coverage/confidence — approved; registers reconciled; baseline archived at **`archive/V1-P2/`**. Full record: `state/active/phase-P2.md` closeout section | — |

*Retro (2026-07-15) — outputs now live in `ledgers/improvement-register.md`, which was
an **unfilled template through both P1 and P2** and has been backfilled with
IMP-001–IMP-007. **P3's G1 must read its `open` and `standing` rows.** Summary:
**IMP-005 ratified — standing**, with a widened boundary (agents write NO project-record
content at all — not operator verbs, not ledger entries, not state narrative), on four
data points across S16–S18. **IMP-006 minted — OPEN, the only open row:** the Armature
docs-with-code guard structurally contradicts the agent/design-session authorship split,
so every agent code commit hits it; skipping any guard is now always a halt; the
`STATE_PATHS` fix is P3 design work. **IMP-007 minted — standing:** spike-first task
gating promoted to standing practice (S17's Robolectric spike failed honestly and the
pre-authorized fallback ran with zero improvisation; the retry-cap + scope-fence held
through three harness walls and two SOW amendments, every halt legitimate).*

*Forward input (carried out of P2, not a P2 item): **F-073 spec delta candidate** — operator challenge to
R2's centered-scroll preview model in favour of a batched window; recorded 2026-07-15 at
`reference/F-073-preview-window-delta-candidate.md`, resolves at the F-073 design
session (P3+).*
