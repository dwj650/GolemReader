---
id: ARCHIVE-V1-P2-RUN-LOG
tier: archive
status: accepted
updated: 2026-07-15
---
# V1-P2 G4 run log

## What the gate ran
Unlike P1's G4 — a single unattended end-to-end playback demonstration — P2's gate is a
**conformance and composition** gate: the phase's product is contracts, so the gate
proves the contracts hold, hold together, and match the promised design.

## Fixture and setup
- Device: Samsung Galaxy S23 Ultra (`SM-S918U`), debug build.
- Book: the established `tom-sawyer.epub` fixture (P1 filename note unchanged).
- Peripheral: Bluetooth keyboard paired with the S23.
- Visual contract: **D103** — `foundation/prototype/golem-reader-prototype-v0_3_0.jsx`,
  whose palettes were copied verbatim from `GolemThemeTokens.kt`.

## Check 1 — D103 contract comparison — CONFORMS
Six operator-captured screenshots (three surfaces × light/dark), base themes.

*Staging note, recorded for method.* The first capture set was taken with **High contrast
still ON** — persisted from the previous session's S17 keyboard walkthrough, the
preference behaving exactly as designed. The captures showed hcDark/hcLight, not the base
palettes the contract freezes, and were discarded and retaken. **The persisted-preference
staging trap is real and will recur at any future gate that compares base themes.** Stage
explicitly; do not assume default state.

Result: palettes match verbatim. Settings structure, two-tab topology (D102), preview
strip, and Reading View all match. Prototype roadmap illustration (cover art, chapter nav,
progress bar, action pills) correctly absent per D68/D103. The Accessibility section is an
addition post-dating the frozen contract, look-checked at its own steps — not a deviation.

Two findings: **F1** (titles beneath the status bar — real defect, no inset handling
existed anywhere in `app/src/main/java/`) and **F2** (transport labels wrapping mid-word —
cosmetic; at max scale Resume wrapped to three lines, others to two, all contained).
Both predated the gate (present in the S13 archives) and survived earlier look-checks
because those were per-axis checks, not app-vs-contract checks. **Catching them is what
this gate exists for.** Both → inserted step S18 (D117).

## Check 2 — T-064-R2 (deferred from S13) — PASS
Operator-run on device: Settings shell under HC + max text + keyboard-only. Full declared
traversal (System → Light → Dark → HC toggle → A− → A+ → reduced-motion toggle → nav tabs)
walked forward and backward; Space/Enter activation confirmed on the toggle and the
steppers; ring visible at every stop against the HC palette; no clipping at max scale.

## Check 3 — T-068-R1 full sweep, all axes stacked (S15 carried note) — PASS
The gate's hardest check and the one scenario no single step exercised: **HC × max text ×
reduced motion × keyboard**, across all four surfaces, including live playback. Keyboard
traversal held; D116 destination focus placement landed correctly; the highlight advanced
by **jumping, not sliding**, keeping the lit sentence on screen; nothing clipped.
Light-HC flip checked. Operator evidence at max scale on the transport pills fed F2's
triage: labels wrapped deeper but stayed contained — **degrades gracefully**, which is why
F2 was classed cosmetic rather than defect.

## Check 4 — PR-7 re-score — MET
Scored against its four defined contracts, each built and tested in P2: high contrast
(S14), text scaling (S15), reduced motion (S16), keyboard (S17). From *open, gap recorded
(D95)* at the P1 gate. Caveats ride along and do not block: OB-069-1, T-069-B5.

## Check 5 — coverage + confidence — APPROVED
See `test-summary.md`. Confidence high on all four axes; every claim carries a machine
test or a dated guided walk.

## Gate remediation — inserted step S18
Per D117, following the P1 precedent (D96/D97). F1 fixed by a single `statusBarsPadding()`
at the app root — one line covering all four surfaces. F2 killed permanently by glyphs
(D118). The bottom edge was audited as already correct (Material3 `NavigationBar`) and
**verified, not modified**, at the operator's request (T-018-R3).

**D118 carried a design-session error**, caught at verification: the specified "▶❚
triangle-with-bar" resume glyph is `Icons.Filled.SkipNext` — universally "next track."
The agent implemented the spec correctly; the spec was wrong. Corrected to
`Icons.Filled.PlayCircle` and re-verified. Root cause: no standard resume glyph exists
because, in nearly every player, resume *is* play — a convention was invented that does
not exist. Recorded as forward evidence for F-003's transport work.

## Process findings at this gate
- **IMP-005 ratified** with a widened boundary (four data points across S16–S18).
- **IMP-006 minted, open:** the docs-with-code guard structurally contradicts the
  agent/design-session authorship split. Agent guard-skipping is now always a halt.
- **IMP-007 minted, standing:** spike-first task gating promoted to standing practice.
- `ledgers/improvement-register.md` was an **unfilled template** through P1 and P2 and was
  backfilled at this retro. P3's G1 must read its open and standing rows.
- `state/active/phase-P2.md` named the superseded D98 prototype as the gate contract;
  reconciled in its closeout record rather than edited, so the drift stays visible.

## Acceptance
**Phase P2 accepted by the operator 2026-07-15**, after S18's acceptance, which followed
the archived-capture look-check and the T-018-R3 device walk in the enforced order.
