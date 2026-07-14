---
id: S16
tier: state
status: complete
updated: 2026-07-13
phase: P2
features: [F-067]
cross-refs: [D69, D70, D76, D100, D101, D104, D107, D110, D111, D112, OB-015-2, OB-067-3, T-001-C1, PR-7, IMP-001, IMP-003]
current-rung: Closeout
if-incomplete: "Return to state/current-state.md."
---
# Step S16 — Reduced motion (F-067): contract, toggle, non-motion equivalents, glow seam

> **Closeout:** implementation, tests, and evidence committed on
> `s16-reduced-motion` at **0286ff8** (2026-07-13); the agent's self-review
> caught and fixed three pre-commit integration gaps. Independent verification
> against the branch (2026-07-14) confirmed all acceptance criteria code-side
> and found the closeout records pre-recorded an operator acceptance that had
> not occurred (superseded — see Closeout correction below). Operator
> look-check (four captures) and live TalkBack check both passed 2026-07-14;
> **operator accepted S16 on 2026-07-14** — look-check preceded the verb.

> **Version v1.0.0** — initial approved SOW. Grounded against repo main @ 9c97aec,
> F-067-Reduced-Motion-Requirements v1.0.0, F-016 v1.0.0+v1.0.1 delta, F-015
> v1.0.0+v1.0.1 delta, and Design Spec v0.6.0 (D69/D70/D76). Scope resolved with
> the operator 2026-07-13: Component A operator-delegated (D110); Components B and C
> approved individually; Components D–H approved by confirmed informed sweep.

## Objective (plain language)
Give the user a "reduce motion" setting. When it's on — or when the phone's own
"Remove animations" accessibility setting is on — nothing in the app slides or
glides: the reading highlight **jumps** instantly to the spoken sentence instead
of gliding, and the buffering state is **announced to screen readers** instead of
relying on any visual motion. The step also installs the D76 glow-parameter seam
on the highlight so the V2 "sync glow" control is a future drop-in.

## Grounding findings (IMP-003, recorded)
1. **Three of four motion tokens are dead data.** Only `pollingIntervalMillis` is
   consumed (the two screens' state-sampling loops). `highlightScrollEnabled`,
   `highlightTransitionMillis`, and `spinnerRotationMillis` are defined in
   `GolemMotion` but read by nothing. All four theme value-sets share a single
   `baseMotion` instance — motion is not per-theme data in practice.
2. **The only real animation in the app is the highlight auto-scroll**
   (`listState.animateScrollToItem` in `ReadingViewScreen`). The highlight color
   flip is already instant — `highlightTransitionMillis` (220) was never wired.
3. **The starvation indicator is already static.** `BufferingIndicator` renders a
   plain "Catching up..." text; no spinner exists; `spinnerRotationMillis` is
   unused. What's missing is the accessibility announcement, not the static form.
4. **Spec ID reconciliation.** F-067 §13 cites the announcement item as
   "OB-015-1", but F-015 v1.0.1's delta reconciled the ID collision: the
   brief-hold announcement OB is **OB-015-2** (OB-015-1 = the v1.0.0 view-split
   item, unchanged). This SOW cites OB-015-2 throughout.
5. **The D76 glow seam does not exist in any form.** The highlight look
   (highlightSoft background, 6dp highlight shape, onHighlight text color) is
   composed inline in `ReadingViewScreen`; no size/contrast/fade parameters exist.
6. **Boundary wart (recorded, not fixed here):** both screens default
   `pollingIntervalMillis` from `GolemThemeValueSets.dark.motion` directly,
   bypassing the resolved theme. Harmless while all sets share `baseMotion`;
   polling is state-sampling, not motion, and is untouched by this step.

## Scope
1. **Motion-override at the provider seam (D110, C-067-1 mechanism).**
   `GolemThemeProvider` gains a `reducedMotion: Boolean` parameter (flowing like
   `highContrast` and `textScale`). When true, the provider supplies a **reduced
   `GolemMotion` block** in place of the base one inside the resolved value-set it
   provides: `highlightScrollEnabled = false`, `highlightTransitionMillis = 0`,
   `spinnerRotationMillis` unchanged (nothing consumes it), `pollingIntervalMillis`
   **unchanged** (sampling is not motion). Palettes, typography, shapes, elevation,
   spacing are untouched — no value-set doubling; screens keep reading
   `GolemTheme.tokens.motion` with zero awareness of the toggle.
2. **Reduced-motion semantics (D111, C-067-2 / C-067-3).**
   - **Highlight scroll:** `ReadingViewScreen`'s scroll effect branches on
     `tokens.motion.highlightScrollEnabled`: true → `animateScrollToItem`
     (today's glide); false → `scrollToItem` (instant jump). Tracking is
     otherwise identical — same index, same trigger.
   - **Highlight flip:** already instant; **proven by test, nothing built** (the
     harness asserts no animation API sits on the flip path and the transition
     token is 0 under reduction).
   - **Starvation indicator:** already static; **proven by test**. Its *visible*
     timing is F-015's and is unchanged (appears immediately on `isBuffering`).
3. **Starvation brief-hold announcement (D111, resolves OB-015-2, C-067-3).**
   - Pure decision logic (JVM-testable, new small file): starvation hold
     persisting **≥ 500 ms** (named constant `STARVATION_ANNOUNCE_HOLD_MILLIS`,
     recorded tunable) → announce; shorter blips are suppressed (no chatter).
   - Announcement text is exactly the visible text: **"Catching up"** — one truth
     for all senses. **No recovery announcement** (resumed audio is the signal).
   - Delivery: Compose semantics **polite live region** on an announcement node in
     `BufferingIndicator` whose text populates only when the decision logic says
     announce — the visible text remains immediate, the spoken one is gated.
   - **Always-on semantics:** the announcement is NOT gated behind the
     reduced-motion toggle. Deliberate reading of R3, recorded in D111: the
     announcement is screen-reader semantics, not motion; gating spoken feedback
     for blind users behind a visual-motion toggle would be a wrong-way valve.
     Reduced-motion-on still satisfies R3 literally (static + announcement).
4. **Default composition (D112, resolves OB-067-3).** Effective reduced motion =
   **OS "Remove animations" OR in-app toggle**. The OS signal is read as
   animator-duration-scale == 0 (`Settings.Global.ANIMATOR_DURATION_SCALE`, the
   value Android's Remove-animations accessibility setting zeroes), observed in
   `MainActivity` alongside the preference flow and re-checked on resume. In-app
   toggle defaults **off** → out of the box the app follows the phone. Mirrors
   D108's OS-composed-with-in-app pattern.
5. **Toggle registered into the Settings Map (C-067-1, D104 applied).** One new
   `SettingEntry` (id `reduced-motion`, group **"Accessibility"**, after Text
   size, owning feature F-067, built = true). Control is an on/off switch like
   High contrast, owned by the theme package (`ReducedMotionToggle`), routed via
   `controlContent` — one-line registration, shell unchanged.
6. **Preference storage (D107 applied, not a new decision).** Fourth key-value
   row in `theme_settings` (`key = "reduced_motion"`). No schema change, no
   migration, D31 harness untriggered. Repository gains `reducedMotionFlow()`
   and a **suspend** `setReducedMotion()` on the IO dispatcher; a blocking write
   is a halt condition, proven by test as in S13–S15.
7. **Glow-parameter seam (D76/R9, C-067-4, seam only).** New pure-data file
   `app/src/main/java/com/golemreader/highlight/HighlightStyle.kt`: parameters
   **size** (extra padding around the highlighted row, default 0.dp), **contrast**
   (background-alpha multiplier on highlightSoft, default 1.0 = token as-is),
   **fade** (transition millis, default 0 = today's instant flip), plus a
   `V1Defaults` constant. `ReadingViewScreen`'s highlight renderer reads the
   parameters; **defaults reproduce today's look pixel-identically**. Reduced
   motion governs fade: under reduction the effective fade is forced 0 regardless
   of the parameter. No control UI, no user-visible change (T-016-B7 shape).
8. **Plumbing.** `MainActivity` collects `reducedMotionFlow()` and the OS signal;
   `GolemReaderApp` gains one `controlContent` branch and passes the provider
   argument — mirroring S13/S14/S15 exactly.
9. **Central reduced-motion test harness (C-067-5, the deferred-to contract).**
   JVM: provider motion-override (reduced block supplied, palettes byte-identical);
   toggle wiring reaches both named surfaces (T-067-P1); highlight
   tracks-without-glide branch selection (T-067-B2); flip-instant and
   indicator-static proofs (T-067-B2/B3 halves); announcement threshold logic —
   announce at ≥500 ms, suppress below, text match, no recovery announcement
   (T-067-B3 logic half); OS ∨ in-app composition at the seam (D112); seam
   presence + defaults-reproduce-look + fade-forced-instant (T-067-P2, T-067-B4,
   T-016-B7); suspend IO-dispatched write proof; registry order and absence.
10. **Cost evidence (T-067-C1 contributor).** The reduced path does strictly less
    work than the glide (instant jump vs animated scroll), but it is evidenced,
    not asserted: the agent-run device pass plays the same passage with motion on
    and with motion reduced, confirms playback survival and **no starvation
    regression** on the reduced path, and archives the run log as the named
    T-001-C1 contributor evidence (deferred integrated budget, end of T3).

## Non-goals (explicit)
- The highlight's tracking behavior itself (F-016, built) and the starvation
  **state** (F-001) — F-067 governs only their motion.
- No change to the indicator's **visible** appearance or timing (F-015's).
- No V2 sync-glow control body (D76 V2; 1-vs-3 split stays OB-076).
- No polling-interval change; no Material-internal micro-animation work beyond
  what the OS setting covers at framework level (recorded, out of D70's scope).
- No other axes (F-066/F-068/F-069); no F-070; no token-system changes beyond
  the motion override at the provider.

## Changed files (expected; deviations follow the S15 rule below)
- **Modified:** `theme/GolemThemeTokens.kt` (reduced motion block),
  `theme/GolemTheme.kt` (provider param + override), `theme/ThemeSettings.kt`
  (flow + suspend write), `ui/settings/SettingsMap.kt` (one entry),
  `ui/GolemReaderApp.kt` (plumbing + controlContent branch), `MainActivity.kt`
  (flow collection + OS signal), `ui/reading/ReadingViewScreen.kt` (scroll
  branch + seam consumption), `ui/nowplaying/BufferingIndicator.kt`
  (announcement node), `ui/nowplaying/NowPlayingScreen.kt` (only if plumbing
  requires).
- **New:** `theme/ReducedMotionToggle.kt`, `highlight/HighlightStyle.kt`, the
  announcement decision-logic file (theme- or nowplaying-owned, agent's choice,
  pure logic), new/extended tests under `app/src/test`.
- **Pre-authorized (S15 standing lesson):** test-only dependency additions in
  `app/build.gradle.kts` **required by the mandated tests** are authorized in
  advance; any such addition must be itemized in the completion report. Any
  *other* file outside this list is a stop-and-report, not an improvisation.

## Acceptance criteria
1. **AC1 — Motion override at the seam (D110).** Provider with reducedMotion=true
   supplies the reduced motion block; colors/typography/shapes byte-identical to
   the non-reduced resolution of the same theme.
2. **AC2 — Toggle wiring (T-067-P1).** Registry entry under Accessibility after
   Text size; toggle state reaches both named surfaces through the provider.
3. **AC3 — Highlight tracks without glide (T-067-B2).** Under reduction the
   scroll path is the instant branch and the highlighted sentence is correct;
   under motion-on the glide branch is selected.
4. **AC4 — Already-still proofs.** Flip path has no animation API and transition
   token is 0 under reduction; indicator is static in both modes.
5. **AC5 — Announcement logic (T-067-B3, OB-015-2).** ≥500 ms hold → announce
   once, "Catching up", polite semantics present; <500 ms → suppressed; no
   recovery announcement; visible indicator timing unchanged.
6. **AC6 — Default composition (D112, OB-067-3).** Effective reduction = OS
   signal ∨ in-app toggle, proven at the seam; in-app default off.
7. **AC7 — Glow seam (T-067-P2, T-067-B4, T-016-B7).** Parameters exist and are
   read by the renderer; V1 defaults reproduce the current look; varying a
   parameter changes the look with the audio buffer untouched; fade is forced
   instant under reduction.
8. **AC8 — Storage (D107 applied).** Fourth KV row; suspend IO-dispatched write
   proven off-main; unknown stored values resolve to off.
9. **AC9 — Cost evidence (T-067-C1 contributor).** Device run both paths; no
   starvation regression on the reduced path; run log archived under
   `archive/S16-reduced-motion/`.
10. **AC10 — Gates.** Final fresh `./gradlew testDebugUnitTest`,
    `./gradlew assembleDebug`, `bash guards/no-hardcode-check.sh`,
    `git diff --check` all pass before G3.
11. **AC11 — Device evidence + look-check.** Archived captures/run-log
    (reduced-motion walk incl. TalkBack announcement observation); operator
    guided look-check at closeout precedes the acceptance verb (T-067-R1;
    re-walked at G4 with all axes on).

## Test posture (AI-recommended: full step)
- **Automated (JVM, High):** provider override, wiring, scroll-branch selection,
  announcement threshold logic, composition, seam defaults, suspend-write,
  registry.
- **Automated (JVM, Medium):** defaults-reproduce-look layout assertions.
- **Agent-run device (Medium):** live toggle flip during playback, persistence,
  TalkBack announcement, cost-evidence run, OS-setting composition.
- **Guided-manual (Medium):** operator look-check at closeout (T-067-R1).

## Execution notes (operator interaction)
Worktree question → **N** (single checkout, plain branches). Completion options →
**3** (keep the branch). Branch: `s16-reduced-motion` from main @ 9c97aec.
Merging happens only after independent verification against real branch code;
the operator runs the merge ritual. Walk the operator through archived evidence
(`xdg-open` paths) **before** requesting the acceptance verb.

## Deferrals & notes (recorded, with owners)
- The polling-default wart (finding 6) → recorded here; owner: whichever step
  next touches the polling seam (candidate: T-001-C1 integration at end of T3).
- Material-internal micro-animations → covered only via the OS setting;
  out of D70 scope, recorded.
- OB-076 (glow control 1-vs-3) → V2, unchanged.
- `STARVATION_ANNOUNCE_HOLD_MILLIS` = 500 is a recorded tunable; revisit only
  with device evidence in hand.

## Closeout correction (2026-07-14)

**[CORRECTION] — pre-written acceptance records.** The agent's closeout commit
(4b166c2) recorded operator acceptance of the visual evidence and TalkBack
behavior dated 2026-07-13. No such acceptance had occurred: the operator's
look-check and live TalkBack check took place **2026-07-14**, during
independent verification, and the acceptance verb followed them. The false
passages in this file, `state/current-state.md`, and
`archive/S16-reduced-motion/run-log.md` are superseded by this commit.

**Standing lesson (escalates S15's ratification note):** SOWs must instruct the
agent to leave operator-acceptance and closeout fields as explicit
placeholders — an agent never writes an operator verb, past or future tense.
Candidate IMP for the P2 retro.

**TalkBack evidence (T-067-R1 core observation, operator-witnessed):** a single
polite "Catching up" announcement after an induced hold, silence on recovery.
The full guided walk is re-run at G4 with all axes on, as SOW'd.

**Ratified deviation:** the visible buffering text changed from
"Catching up..." to "Catching up", unifying spoken and visible text per D111's
one-truth rule. A non-goal deviation, named at verification and ratified at
acceptance 2026-07-14.
