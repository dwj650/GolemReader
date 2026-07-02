---
id: P1
tier: state
status: accepted
updated: 2026-07-02
cross-refs: [D85, D86, D87, D88, D89, D90, D91, D92, D93, D94, D95, PR-1, PR-2, PR-3, PR-4, PR-5, PR-6, PR-7]
if-incomplete: "Return to state/current-state.md."
---
# Phase P1 — Walking Skeleton
A meaningful build (Gate G1 in, G4 out).

## Entry (G1)
- [x] Decisions for this phase identified: D85-D95 (build config, S4-S9 scope
      boundaries, S9 mid-flight amendment, G4 principle-conformance judgment).
- [x] Prior phase retro + open IMP items read — N/A, this is the first phase.
- [x] Steps scoped with non-goals — every step (S1-S9) shipped with a written SOW
      declaring scope and explicit non-goals.

## Steps
- S1 — Bootstrap
- S2 — Storage substrate (F-057)
- S3 — Book identity (F-020)
- S4 — Text → sentences (F-018 thin, F-027, F-028)
- S5 — One voice speaks (F-048 substrate, F-008)
- S6 — Streaming engine (F-001, F-004) — D90
- S7 — Synchronized highlight signal (F-016) — D91
- S8 — Transport commands + thin hub (F-002, F-009 thin) — D92
- S9 — Reading View + Now Playing (F-014, F-015) — D93, D94

## Acceptance (G4)
- [x] **Registers current** — current-state, phase-index, decision-log (D-ceiling
      D94 at review time, D95 this document), known-issues all consistent with the
      actual repo as of commit 890a2b8.
- [x] **Docs/state reconciled** — no drift found between registers and code across
      the full S1-S9 review.
- [ ] **Baseline archived** — pending: real on-device end-to-end demonstration +
      screenshots, packaged to `archive/V0-P1/` (this step's remaining work).
- [x] **Build matches prototype** *(reinterpreted)* — no visual-only G0 prototype was
      ever produced for this project (`foundation/prototype/` is empty); the Design
      Spec v0.6.0 is this project's own declared single authority, and every step's
      SOW traced directly to it. The walking skeleton is checked against the spec's
      V0/V1 description instead of a mockup. This substitution is itself recorded,
      not silently assumed.
- [x] **Phase principle conformance** — see table below.
- [x] **Coverage + confidence summary** — see below.

### Principle conformance (PR-1 through PR-7)
| PR | Principle | Status |
|---|---|---|
| PR-1 | Identity = DNA (streaming-first) | **Conforms** — S6's producer/consumer/buffer architecture |
| PR-2 | Never mass-render | **Conforms** — bounded look-ahead, device-proven (S6) |
| PR-3 | Audio quality first | **Partial, tracked** — ear-tuning (OB-001-1, OB-008-1) deferred to build by design |
| PR-4 | Hotswappable engines | **Conforms** — S7 proved identical index track on Kokoro + Piper |
| PR-5 | Resource efficiency | **Partial, tracked** — integrated budget (T-001-C1) deferred to end of T3 by design |
| PR-6 | User controls engine/voice/speed | **Partial, deliberate** — voice-swap UI deferred to P2 (D89) |
| PR-7 | Accessibility by default | **Open, recorded (D95)** — F-066-070 not built in any P1 step; accessed as priority work for the next phase, not silently passed |

### Coverage + confidence summary
Every step (S1-S9) passed its declared JVM automated suite and at least one real
on-device confirmation via direct instrumentation on the SM-S918U (S23 Ultra).
- **High confidence:** all pure-logic behavior (JVM/Robolectric-tested) — identity
  hashing, text pipeline, streaming spine logic, intent loop, highlight mapping,
  transport commands, UI state mapping.
- **Medium confidence:** all real-device behavior — storage substrate device checks,
  cross-chapter streaming, highlight timing drift (measured, within tolerance), live
  transport session over wall-clock time, rendered Compose UI semantics.
- **Owed, not yet measured:** the integrated battery/thermal budget (T-001-C1, by
  design — lands at end of T3), two S2 cost readings (T-057-C1/C2).
- **Recurring, harmless environment note:** APK install churn repeatedly cleared the
  on-device test fixture across S6/S7/S8/S9's device runs; re-pushing the fixture
  resolved it every time. Never a code defect — logged here so the pattern is
  recognized, not re-investigated as new each time.

## Retro (IMP-ids)
- **IMP-001** — The two-machine git sync ritual (push branch → fast-forward merge →
  push main) was mis-run twice (S6's D90 record, S7's initial branch handoff) because
  a guard-blocked command was followed by later commands that ran regardless of the
  failure. Recommendation for P2: verify `git status`/`git branch --show-current`
  before, not just after, a multi-command paste when a guard has just fired.
- **IMP-002** — Spec-driven grooming (per-feature requirements docs) substituted for
  a visual-only G0 prototype on this project. It worked well enough that every step's
  scope was groundable in real decisions, but it means "build matches prototype" has
  no literal artifact to check against at any future gate. Recommendation: continue
  treating the Design Spec as the checked authority; don't retroactively build a
  prototype just to satisfy the checklist's literal wording.
- **IMP-003** — Every step from S6 onward surfaced at least one real scope-boundary
  finding (OB-D48 in S6, the test-posture question in S7, the five-way F-002 split in
  S8, the slot/tap-to-inspect/gesture questions in S9) that wasn't visible from
  phase-index alone and required reading the actual per-feature spec and the actual
  code before writing the SOW. Recommendation for P2: keep doing this — grounding
  every SOW in the real spec text and real repo state, not just the phase-index
  summary line, is what caught each of these before they became build-time surprises.
- **IMP-004** — PR-7 (accessibility) was deferred across every step without an
  explicit decision scoping it out, until G4 surfaced it. Recommendation: the next
  phase's G1 should explicitly scope accessibility work in from the start, rather
  than letting it default to deferred again by step-by-step omission.
