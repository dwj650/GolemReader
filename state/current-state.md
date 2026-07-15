---
id: CURRENT-STATE
tier: state
status: active
updated: 2026-07-15
if-incomplete: "You are at the source of truth. If something isn't here, it isn't current."
---
# Current state — THE source of truth for "now"

- **Active version:** V1 (in progress; V0 foundation complete, archived at `archive/V0-P1/`)   **Phase:** P2 — Accessible Shell (entered at G1)   **Gate:** G4 — **open**, awaiting inserted step S18   **Step:** S18 — active, handed to agent 2026-07-15
- **Last committed:** S17 merged to `main` at **7726eed** (ff-only, 2026-07-14); feature branch deleted per ritual
- **Coverage target:** see reference/coverage-target.md   ·   **Test posture (active step):** JVM accessible-name + inset contracts; S17 device keyboard suite as an unchanged-regression guard; operator look-check + both-edges device walk at closeout
- **D-ceiling: D118**

## What's happening right now
**S18 implementation is pushed and verification-ready on
`feature/s18-inset-icon-transport`.**
The branch replaces the four transport labels with accessible Material glyphs, applies
the status-bar inset at the root app shell, retains all S17 transport tags and focus
rings, and archives the eight T-018-R1/R2 captures. Agent-run B1–B4, the configured
JVM check, and the debug build are recorded in the S18 run log. No S17 test file was
modified. The SOW closeout fields remain literal placeholders pending the external
look-check and device walk.

**The P2 step ladder is complete — S12–S17 all merged.** S17 (keyboard navigation,
F-069) was accepted 2026-07-14 after look-check and the full T-069-R1 keyboard-only
walkthrough, and merged at **7726eed**. What it added: the `focusRing` token across all
four value-sets with a 3:1 HC floor proven by test plus one shared no-animation ring
modifier (D114); the declared traversal-order contract, content-first/nav-last,
Tab/Shift-Tab tested, with a ratified content correction (D115 — nav is Now Playing →
Settings per D102; Settings begins System → Light → Dark); destination-change focus
placement self-gated by touch mode (D116); and the central keyboard test over all four
surfaces, resolving OB-069-2 and absorbing T-064-B4 (D113). The Robolectric Tab spike
failed, so the pre-authorized S23 fallback ran and passed 4/4.

**G4 — Phase P2 acceptance — is IN PROGRESS and OPEN.** All five gate checks ran
2026-07-15 with the operator:
- **Check 1 — D103 (v0.3.0) contract comparison: conforms.** Six operator-captured base-theme
  screenshots vs. the prototype. Palettes match verbatim; Settings structure, two-tab
  topology (D102), preview strip, and Reading View all match. Prototype roadmap
  illustration correctly absent per D68/D103. **Two findings logged: F1 and F2** (see below).
- **Check 2 — T-064-R2: PASS.** The S13 deferral closes at this gate. Settings shell
  operable at max scale under HC, keyboard-only, full declared traversal both directions.
- **Check 3 — T-068-R1 full sweep, all axes stacked: PASS.** The S15 carried note closes
  at this gate. HC × max scale × reduced motion × keyboard, across all four surfaces
  including live playback; highlight jumps cleanly, ring survives every surface.
- **Check 4 — PR-7 re-scored: MET.** Moves from *open/gap recorded (D95)* at the P1 gate
  to **met** — all four defined contracts (HC, scaling, reduced motion, keyboard) built
  and tested. Caveats ride along, do not block: screen-reader depth deferred (OB-069-1),
  onboarding keyboard support owner recorded (T-069-B5).
- **Check 5 — coverage/confidence: approved.** Confidence high on all four axes; every
  claim carries a machine test or a dated guided walk. Deliberate exclusions: screen
  readers, onboarding, Library (D102), non-phone geometries.

**S18 — inserted at the gate by D117** (following the P1 precedent D96/D97: real gate
defects become inserted steps by recorded decision, never silent fixes or silent passes).
SOW v1.0.0 at `state/active/step-S18.md`, grounded against main @ 7726eed. Remediates:
- **F1 (real defect)** — screen titles render beneath the system status bar on Now
  Playing and Settings. Code audit found **no window-inset handling anywhere** in
  `app/src/main/java/`. Predates this gate (present in S13 archives); survived earlier
  look-checks because those were per-axis, not app-vs-contract.
- **F2 (cosmetic)** — transport labels wrap mid-word. Operator evidence at max scale:
  Resume wraps to three lines, others to two, but all stay contained — no collision, no
  clipping. Fixed permanently by **D118**: labels become glyphs (▶ / ❚❚ / ▶❚ / ■), each
  keeping its accessible name; the four `testTag`s are unchanged and load-bearing for the
  S17 keyboard tests.
- The **bottom edge is already correct** (Material3 `NavigationBar` consumes its inset
  natively) — verified, not modified, per operator request (T-018-R3).

## Open items needing attention
- **G4 cannot accept until S18 merges.** Gate findings F1/F2 are the only blockers; the
  P2 retro follows gate acceptance.
- **F-073 spec delta candidate (2026-07-15, NOT locked)** — operator challenge to F-073
  R2's centered-scroll model: he prefers a batched window where the highlight walks
  through held-still text. R1's fixed-height frame is already committed and answers his
  second ask. Recorded at `reference/F-073-preview-window-delta-candidate.md`; resolves
  by decision at the F-073 design session (P3+). R2 stands as written until then.
- T-057-C1 and T-057-C2 remain owed agent-run measurements; T-057-C3 remains a contributor
  to the integrated listen-loop system-budget test at end of T3.
- KI-S3-001 remains open: `androidx.test.services` has no UID on the S23, so the
  external-cache-clear permission grant is skipped until a later step depends on that
  device path again.
- KI-S5-001 remains open: Piper segment-final `:`/`;` artifacts are expected and unfixed
  until F-044/F-045 rule-packs/voice-bound packs are built.
- T-001-C1 remains deferred until end of T3; S16's reduced-motion highlight path is a
  named contributor.
- Full Android MediaSessionService, lock-screen/notification, audio-focus, background
  survival, and resume-after-kill routing remain deferred after S8 per D92.
- Secret scan remains intentionally skipped until configured (KI-S1-001 / D78) —
  not a defect.
- S13 recorded deferrals: T-064-B4 → **absorbed by S17 (D113), done**; T-064-R2 →
  **closed at G4, passed 2026-07-15**; swipe-left → D51/D52; full preview strip → F-073;
  Library tab → F-019 phase.
- S15 carried note: T-068-R1 full sweep → **closed at G4, passed 2026-07-15**.
  ReservedSlot dp-height trap → still owned by the F-073 preview-strip family.
- S16 recorded notes: polling-interval default params read
  `GolemThemeValueSets.dark.motion` directly (harmless wart; owner: whichever step
  next touches the polling seam — explicitly NOT S17 or S18); Material-internal
  micro-animations covered only via the OS setting (out of D70 scope, recorded);
  `STARVATION_ANNOUNCE_HOLD_MILLIS` = 500 is a recorded tunable.
- S17 recorded deferral: T-069-B5 (onboarding keyboard-operable, D74) → the phase
  that builds F-070 (D113).
- OB-069-1 (screen-reader/TalkBack depth) remains out of P2; owner: future
  accessibility grooming.
- **IMP-005 now carries three data points** (S16 pre-written acceptance records; S17
  agent-written "operator-approved" labels and a D116 ledger entry; both corrected
  on-branch). Sharpened for the P2 retro: agents write neither operator verbs nor ledger
  entries; closeout fields stay literal placeholders; verification greps branch records
  for premature acceptance language.
- Carry to the P2 retro as a **positive** pattern: S17's spike-first task gating
  JVM-vs-device test placement worked exactly as designed (fenced fallback, zero
  improvisation); the retry-cap + scope-fence held through three harness walls and two
  SOW amendments, every halt legitimate.
- p1 likely retains stale local branches — prune (`git fetch --prune` +
  `git branch -vv` review) next time the operator is on p1. Origin is clean; T14 is the
  machine in use and was clean after the S17 ritual.

## Next action
Run independent verification against the pushed branch code (never the report; grep for
premature acceptance language per IMP-005)
→ operator look-check of the eight archived captures → operator's T-018-R3 both-edges
walk on the S23 → acceptance verb → ff-only merge ritual. **G4 then accepts and the P2
retro runs**, followed by G1 for P3 in its own session.
