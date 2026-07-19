---
id: CURRENT-STATE
tier: state
status: active
updated: 2026-07-15
if-incomplete: "You are at the source of truth. If something isn't here, it isn't current."
---
# Current state — THE source of truth for "now"

- **Active version:** V1 (in progress; V0 foundation archived at `archive/V0-P1/`)   **Phase:** P2 — Accessible Shell — **ACCEPTED at G4, 2026-07-15**; baseline archived at `archive/V1-P2/`   **Next:** G1 for P3, in its own session
- **Last committed:** S18 merged to `main` (ff-only, 2026-07-15) — this closeout commit is main's tip. Prior: S17 at **7726eed**, S16 at **b507c75**
- **Coverage target:** see reference/coverage-target.md   ·   **Test posture:** no step active — P2 closed, P3 not yet scoped
- **D-ceiling: D118**

## What's happening right now
**Phase P2 — Accessible Shell — is complete and accepted.** The P1 walking skeleton is
now themed and accessible along four axes, each proven individually and — at the gate —
proven **stacked** during live playback: high contrast (S14), text scaling (S15),
reduced motion (S16), keyboard navigation (S17). S12 laid the token floor; S13 built the
settings shell and froze the **D103 (v0.3.0)** visual contract. **S18 was inserted at the
gate** (D117) to remediate two findings and was accepted 2026-07-15.

**G4 accepted 2026-07-15.** All checks passed: D103 contract comparison (conforms, F1/F2
found and remediated), **T-064-R2** pass (S13 deferral closed), **T-068-R1** all-axes
composition sweep pass (S15 carried note closed), **PR-7 re-scored MET** (from *open,
gap recorded (D95)* at the P1 gate — the first principle to move gap→met across a phase
boundary), coverage/confidence approved. Full record: `state/active/phase-P2.md` closeout
section. Baseline: `archive/V1-P2/`.

**S18 delivered:** the status-bar inset respected on all four surfaces via a single
`statusBarsPadding()` at the app root (F1 — a real defect; no inset handling existed
anywhere in `app/src/main/java/`); transport labels replaced by glyphs — PlayArrow /
Pause / **PlayCircle** / Stop, each carrying its accessible name, all four `testTag`s and
focus rings unchanged (F2, killed permanently — glyphs cannot wrap; D118 **with a ratified
correction**: the originally specified ▶❚ was `SkipNext`, which means "next track" — a
design-session error, not the agent's). The bottom edge was audited as already correct
(Material3 `NavigationBar`) and **verified, not modified**.

**The P2 retro is done.** Outputs in `ledgers/improvement-register.md` — which was an
**unfilled template through both P1 and P2** and has been backfilled with IMP-001–IMP-007.
**P3's G1 must read its `open` and `standing` rows.**

## Open items needing attention
- **IMP-006 (open — the only open improvement row, and the highest-value process work
  available).** The Armature docs-with-code pre-commit guard requires every `app/` commit
  to also touch `state/current-state.md` or `ledgers/`; IMP-005 forbids the agent from
  touching exactly those. **The two rules are mutually exclusive and every agent code
  commit hits this wall.** Effective now: skipping or disabling any guard is **always a
  halt**. The fix — adding an agent-writable record path (the step run log under
  `archive/`) to `STATE_PATHS` in `guards/guards.config` — is **design work for its own
  step in P3**, not yet decided.
- **F-073 spec delta candidate (NOT locked).** Operator challenge to F-073 R2's
  centered-scroll preview model in favour of a batched window where the highlight walks
  through held-still text. R1's fixed-height frame already answers the second half of the
  ask and is committed. Recorded at `reference/F-073-preview-window-delta-candidate.md`;
  resolves by decision at the F-073 design session (P3+). **R2 stands as written until
  then.** The open question the candidate leaves: batch-boundary behavior, which is where
  the two models actually differ.
- T-057-C1 and T-057-C2 remain owed agent-run measurements; T-057-C3 remains a contributor
  to the integrated listen-loop system-budget test at end of T3.
- T-001-C1 deferred until end of T3; S16's reduced-motion highlight path is a named
  contributor.
- KI-S3-001 open: `androidx.test.services` has no UID on the S23; the external-cache-clear
  permission grant is skipped until a later step depends on that device path again.
- KI-S5-001 open: Piper segment-final `:`/`;` artifacts expected and unfixed until
  F-044/F-045 rule-packs are built.
- Full Android MediaSessionService, lock-screen/notification, audio-focus, background
  survival, and resume-after-kill routing remain deferred after S8 per D92.
- Secret scan intentionally skipped until configured (KI-S1-001 / D78) — not a defect.
- **Deferred items leaving P2:** T-069-B5 (onboarding keyboard support) → the F-070 phase,
  owner recorded (D113); OB-069-1 (screen-reader/TalkBack depth) → future accessibility
  grooming; ReservedSlot dp-height trap → the F-073 preview-strip family;
  polling-interval default-params wart (reads `GolemThemeValueSets.dark.motion` directly)
  → whichever step next touches the polling seam; Library tab → F-019 phase (D102);
  swipe-left → D51/D52.
- **Closed at G4, not deferred:** T-064-R2, T-068-R1, PR-7 (now met), T-064-B4 (absorbed
  by S17 per D113), IMP-002, IMP-004.
- p1 likely retains stale local branches — prune (`git fetch --prune` + `git branch -vv`
  review) next time the operator is on p1. **T14** is the machine in use and is clean.

## Method notes worth carrying
- **The persisted-preference staging trap.** The first G4 capture set was taken with High
  contrast still on from the prior session — the preference behaving exactly as designed.
  Any future gate comparing base themes must stage explicitly and never assume defaults.
- **Gate findings that predate the gate are the point.** F1 and F2 both existed in the
  S13 archives and survived every per-axis look-check. Only an app-vs-contract comparison
  found them.
- **Verification reads real code, not reports** (IMP-003). At S18 this caught a wrong
  glyph, an unauthorized state-file edit, and a skipped guard — none of which the
  completion report's green checkmarks would have revealed.

## Next action
**G1 for Phase P3**, in its own session. Likely scope per the phase index: library and
sources (F-019, F-021, F-022, F-024). That session must read the improvement register's
open and standing rows, and should consider taking **IMP-006** as an early step — the
guard contradiction will otherwise recur on every agent code commit in P3.
