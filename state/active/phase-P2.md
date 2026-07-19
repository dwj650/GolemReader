---
id: P2
tier: state
status: entered (G1)
updated: 2026-07-02
cross-refs: [D95, D98, D99, D100, IMP-001, IMP-002, IMP-003, IMP-004, PR-6, PR-7]
if-incomplete: "Return to state/current-state.md."
---
# Phase P2 — Accessible Shell
A meaningful build (Gate G1 in, G4 out): an app anyone can operate — themed,
scalable, high-contrast-capable, motion-optional, keyboard-operable — with a real
settings surface.

> **Version v1.0.0** — first version of this document.
> Changelog: initial G1 entry drafted from D99/D100 and the F-064–F-069 specs,
> grounded against repo main @ 8f7d6f7.

## Entry (G1)
- [x] **Decisions for this phase identified:** D98 (prototype v0.2.0 is the frozen
      visual contract — ledger entry repaired this phase; closes IMP-002),
      D99 (P2 opens with F-065; accessibility set follows in dependency order),
      D100 (P2 = Accessible Shell, six steps; F-070 onboarding explicitly deferred
      to the phase that builds F-019 library + F-048-family voice import — a
      recorded deferral, not a silent omission, per IMP-004). Per-step build
      decisions expected en route: contrast target (OB-066-1), scale ceiling
      (OB-068-1), reduced-motion default (OB-067-3), keyboard checklist surfaces
      (OB-069-2).
- [x] **Prior phase retro + open IMP items read:** IMP-001 (verify branch state
      before multi-command pastes, especially after a guard fires), IMP-002
      (closed by D98 — the v0.2.0 prototype now exists as the visual contract),
      IMP-003 (ground every SOW in real spec text + real repo code — applied to
      this document and to every P2 SOW), IMP-004 (accessibility scoped in at G1 —
      this phase is that scoping).
- [x] **Steps scoped with non-goals** — ladder below; each step still receives a
      full SOW with file scope and acceptance criteria before handoff.

## Steps (flat IDs continue from P1: S12–S17)

**S12 — Theme foundation (F-065).**
Design-token system (color · typography · shape · elevation · spacing · motion);
one light + one dark theme as pure value-sets (dark derives from the D98 prototype
palette); theme choice persisted in the precious tier (Room migration v3→v4 under
the D31 harness); follow-system as default; live app-wide switch including system
status/navigation bars; migration of the four existing UI files off stock Material
defaults onto tokens; a mechanical no-hardcode check.
*Forward-compat criterion:* adding another theme requires only a new value-set —
no screen changes (the F-060 import seam).
*Non-goals:* high contrast (S14), text-scaling behavior (S15), motion toggling
(S16), gradients/glows (V2, F-060), the settings screen (S13 — the theme picker
control is built here but has no home until S13), final aesthetic polish
(iterated in build per F-065 §12).

**S13 — Settings shell, thin (F-064) + theme picker lands.**
The settings surface: grouped navigation, a declared Settings Map, routing to
parent-owned controls, graceful absence of unbuilt entries. F-065's picker
(light · dark · follow-system) becomes its first hosted entry.
*Non-goals:* owning any setting's behavior or storage (parents own them), the
accessibility axes themselves (S14–S17 retrofit conformance onto this shell),
settings for features that don't exist (hidden, not stubbed, per D68).

**S14 — High contrast (F-066).**
High-contrast token value-set meeting a stated contrast target (target chosen at
this step = OB-066-1); one app-wide toggle hosted in S13's shell; the central
contrast test (ratio checks + re-render verification) other features defer to.
*Non-goals:* base theme (S12), other axes, per-screen layout.

**S15 — Text scaling (F-068).**
Scale model combining OS font-scale + in-app control into one multiplier on the
typography tokens; reflow-not-clip verified per surface at max scale (ceiling
chosen at this step = OB-068-1); no-font-size-literal guard.
*Non-goals:* the token system (S12), each screen's layout details beyond reflow,
other axes.

**S16 — Reduced motion (F-067).**
Reduced-motion contract + toggle; non-motion equivalents for the two named
surfaces — the F-016 sync highlight and the F-015 starvation indicator (static +
brief-hold announcement, OB-015-1 wording chosen here); the D76 glow-parameter
seam (size/contrast/fade exposed on the highlight — seam only, no V2 control).
Listen-loop note: the reduced-motion highlight path is a contributor to the
T-001-C1 integrated budget and must not cost more than the animated path.
*Non-goals:* the highlight itself (F-016, built), starvation behavior (F-015,
built), the V2 sync-glow control body.

**S17 — Keyboard navigation (F-069).**
Every interactive control reachable and activatable without touch; logical focus
order per surface; token-driven visible focus indicator that survives high
contrast; the central keyboard test (surfaces chosen at this step = OB-069-2).
*Non-goals:* the controls themselves (parent features), deep screen-reader /
TalkBack semantics (OB-069-1 — explicitly out of P2, owner recorded), onboarding
coverage (D74 applies when F-070 is built, in its own phase).

**G4 — Phase acceptance.**
Registers current; docs/state reconciled; baseline archived (screenshots vs the
D98 prototype — the first gate with a literal visual contract to check against);
principle conformance with **PR-7 re-scored** against the four built axes;
coverage + confidence summary.

## Explicit deferral (recorded, per IMP-004 / D100)
**F-070 Onboarding** is not in P2. Its spec depends on F-019 (library + empty
state) and the F-048-family voice-import flow, neither of which exists. Building
first-run guidance before there is anything to guide through would be scaffolding
without a building. F-070 is priority work in the phase that delivers library +
voice import, where D74 (first-run obeys the accessibility contracts built here)
takes effect.

## Housekeeping carried in the first P2 handoff
- Write the missing **D98** entry into ledgers/decision-log.md (approved 2026-07-02;
  commit 8f7d6f7 references it but the entry never landed).
- Append **D99** and **D100** to the decision log.
- Refresh **state/phase-index.md**: P1 ladder marked complete through S11 + G4,
  retro line pointed at IMP-001–004, P2 ladder added.
- **IMP-001 standing rule for every P2 handoff:** the operator runs
  `git branch --show-current` and reads the output before any multi-command
  paste, and stops at the first failed command in a sequence.

---

## G4 — Phase P2 acceptance record (design session, 2026-07-15)

**Phase P2 — Accessible Shell — ACCEPTED by the operator 2026-07-15.**

### Reconciliation: the visual contract
This file's G4 definition (above) names the **D98** prototype as the gate's visual
contract. That text predates S13, which minted **D103** and superseded D98 with the
**v0.3.0** prototype (`foundation/prototype/golem-reader-prototype-v0_3_0.jsx`, palette
values copied verbatim from `GolemThemeTokens.kt`). **The gate ran against v0.3.0.**
Recorded here rather than edited above, so the drift stays visible.

### Gate checks — all run 2026-07-15 with the operator
| Check | Result |
|---|---|
| Contract comparison vs **D103 (v0.3.0)** | **Conforms.** Palettes match verbatim; Settings structure, two-tab topology (D102), preview strip, Reading View all match. Prototype roadmap illustration correctly absent (D68/D103). Two findings logged: **F1**, **F2** |
| **T-064-R2** (deferred from S13) | **Pass.** Settings shell operable at max scale under HC, keyboard-only, full declared traversal both directions. **Closes at this gate** |
| **T-068-R1** full sweep, all axes stacked (S15 carried note) | **Pass.** HC × max scale × reduced motion × keyboard, all four surfaces, including live playback; highlight jumps cleanly, focus ring survives every surface. **Closes at this gate** |
| **PR-7** re-scored | **MET** — from *open, gap recorded (D95)* at the P1 gate. All four defined contracts built and tested. Caveats ride along, do not block: OB-069-1 screen-reader depth deferred; T-069-B5 onboarding owner recorded |
| Coverage + confidence | **Approved.** High on all four axes; every claim carries a machine test or a dated guided walk |
| Registers current; docs/state reconciled | **Done** — including this reconciliation, and the backfill of `ledgers/improvement-register.md`, an unfilled template through P1 and P2 |
| Baseline archived | **`archive/V1-P2/`** |

### Gate findings and their disposition
- **F1 — status-bar collision (real defect).** Screen titles rendered beneath the system
  status bar on Now Playing and Settings. Code audit found **no window-inset handling
  anywhere** in `app/src/main/java/`. Predated the gate (present in S13 archives);
  survived earlier look-checks because those were per-axis, not app-vs-contract.
- **F2 — transport label wrapping (cosmetic).** Labels wrapped mid-word; at max scale
  Resume wrapped to three lines, others to two, all contained — no collision, no clipping.
- **Disposition:** both remediated by inserted step **S18** (**D117**), following the P1
  precedent (D96/D97) that real gate defects become inserted steps by recorded decision —
  never silent fixes, never silent passes. S18 accepted 2026-07-15. F1 fixed by a single
  `statusBarsPadding()` at the app root; F2 killed permanently by glyphs (**D118**, with a
  ratified correction). The **bottom edge was audited as already correct** (Material3
  `NavigationBar` consumes its inset natively) and was **verified, not modified**.

### What the phase delivered
An app that is themed, high-contrast-capable, text-scalable, motion-optional, and
keyboard-operable — with every claim carrying either a machine test or a dated walk on
real hardware, and the four axes proven to work **stacked**, during live playback, not
merely one at a time. The phase was named "Accessible Shell" and earned the name.

### What it honestly does not have (deliberate, recorded)
Screen readers (OB-069-1 → future accessibility grooming); onboarding (F-070 → the phase
that builds library + voice import, per the deferral above and D100; T-069-B5 owner
recorded); a Library surface (D102 → F-019 phase); non-phone geometries.

### Retro outputs
Recorded in `ledgers/improvement-register.md`: **IMP-005 ratified** (widened boundary —
agents write no project-record content at all); **IMP-006 minted, open** (the
docs-with-code guard structurally contradicts the authorship split; agent guard-skipping
is now always a halt; the `STATE_PATHS` fix is P3 design work); **IMP-007 minted,
standing** (spike-first task gating promoted to standing practice). IMP-001 and IMP-003
carried forward as standing; IMP-002 and IMP-004 closed.

### Next
**G1 for P3** in its own session — scope likely library/sources per the phase index.
P3's G1 must read the register's `open` and `standing` rows (IMP-006 is the only open row).
