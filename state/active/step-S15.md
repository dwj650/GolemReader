---
id: S15
tier: state
status: active
updated: 2026-07-13
phase: P2
features: [F-068]
cross-refs: [D69, D100, D101, D104, D105, D106, D107, D108, D109, PR-7, IMP-001, IMP-003]
current-rung: Commit
if-incomplete: "Return to state/current-state.md."
---
# Step S15 — Text scaling (F-068): combined multiplier, stepper control, reflow proof

> **Version v1.0.0** — initial approved SOW. Grounded against repo main @ a87f5be,
> F-068-Text-Scaling-Requirements v1.0.0, F-078 v1.0.0 (boundary check), and the
> D103 visual contract. Scope resolved with the operator 2026-07-13: Component A
> approved individually; Components B–E approved by confirmed informed sweep.

## Objective (plain language)
Let the user make all text in the app bigger (or slightly smaller) and have
every screen stay readable with nothing cut off. Text size everywhere becomes
**token size × OS font-scale × in-app setting**. The OS half already works —
every text size in the app is in sp and token-owned (verified at grounding).
What S15 builds is the **in-app multiplier**, the **stepper control** that sets
it, and — the part the spec calls the real test — the **reflow proof** that at
maximum combined scale nothing truncates or overlaps.

## Grounding findings (IMP-003, recorded)
1. All six typography roles use sp for fontSize and lineHeight; the D101 guard
   already forbids sp/dp literals outside `com.golemreader.theme`. **C-068-3
   (no-hardcode guard) already exists** — S15 confirms coverage, builds nothing.
2. **T-068-B4 (honors OS font-scale) is largely free** via sp; S15 proves it
   rather than implements it.
3. Zero `maxLines` / ellipsis / truncation anywhere in the codebase — clean slate
   for reflow.
4. Named clip risks: (a) Material3 `NavigationBar` has a framework-fixed height;
   its tab labels are the most likely clip at max scale — explicit device
   evidence required; (b) the two 48dp `ReservedSlot` boxes on Now Playing are
   dp-fixed but currently contain no text — recorded as a trap note for whichever
   feature fills them (they must reflow or scale when they gain content).
5. **F-068 / F-078 boundary:** F-078 owns dyslexia font, line-spacing, and
   bold-weight for the reading display — not a reading text size. Neither spec
   grants Reading View an independent size. **Boundary note:** no per-surface
   reading size exists or is owed in V1; F-068's app-wide scale is the only
   text-size axis.

## Scope
1. **Scale model (D108, C-068-1).** The in-app multiplier applies at the theme
   provider level: `GolemThemeProvider` wraps content in a
   `CompositionLocalProvider` that overrides `LocalDensity` with
   `Density(density, fontScale = systemFontScale × inAppScale)`. Because the
   system's fontScale already carries the OS setting, the multiplication IS the
   combined multiplier — and because all text is sp, **every screen scales with
   zero screen changes** (same zero-touch pattern as the D106 value-set swap).
   In-app steps: **0.85 / 1.0 / 1.15 / 1.3 / 1.5**, default **1.0**, modeled as
   an enum (`TextScaleStep`) with a stored string value, resolver-style
   `fromStoredValue` defaulting to 1.0 on unknown input.
2. **Stepper control registered into the Settings Map (D108, C-068-2).** One new
   `SettingEntry` (id `text-scale`, group **"Accessibility"**, owning feature
   F-068, built = true) — one-line D104 registration, joining High contrast. The
   control is a **stepper** (`A−  <current %>  A+`), owned by the theme package,
   routed into the shell via `controlContent` like the theme picker and HC
   toggle. Buttons disable at the ends of the range. First non-toggle control;
   kept deliberately simple.
3. **Preference storage — D107 applied, not a new decision.** A **third
   key-value row** in `theme_settings` (`key = "text_scale"`, value = the step's
   stored string). No schema change, no version bump, D31 harness not triggered.
   Repository gains `textScaleFlow()` and a **suspend** `setTextScale()` on the
   IO dispatcher. A blocking write is a halt condition (standing rule since S12's
   crash; re-proven by test as in S13/S14).
4. **Central scaling/reflow test harness (D109, C-068-4).** F-068's owned
   contract, the test other features defer to:
   (a) **model checks** — every step resolves the expected multiplier; unknown
   stored values default to 1.0; combined multiplier = OS × in-app proven at the
   provider seam;
   (b) **app-wide application** — raising the step grows rendered text on the
   checked surfaces (Compose layout assertion, JVM);
   (c) **reflow at max scale** — on the four D109 surfaces (Settings, Now
   Playing, Reading View, bottom nav), at max in-app step with a max-OS-scale
   density, layout assertions prove no truncation/overlap where JVM-assertable;
   (d) **T-066-B4 scaling half (inherited from S14)** — high contrast composes
   with text scaling: HC on + max scale renders with HC tokens and scaled text
   simultaneously, no interference.
5. **Plumbing.** `MainActivity` collects `textScaleFlow()` alongside theme and
   HC flows and passes an `onTextScaleChanged` callback; `GolemReaderApp` gains
   one `controlContent` branch and the provider argument — mirroring S13/S14
   exactly.
6. **Guard confirmation (C-068-3).** Prove (by the existing guard's own run +
   the harness) that the D101 check catches a font-size literal in UI code —
   negative-proof style, matching S14's failing-palette pattern.

## Non-goals (explicit)
- **No independent Reading View text size** (boundary note above — owned by no
  V1 spec; proposing one is scope creep).
- No F-078 territory: no font override, line-spacing, or weight controls.
- No reduced motion (F-067/S16), no keyboard work (F-069/S17), no token-system
  changes beyond the provider seam (F-065 palettes and typography values
  untouched — the multiplier wraps them, never edits them).
- No free-form slider; steps only (testable, simple).
- No fix for the ReservedSlot dp-height trap (no text lives there yet; recorded,
  owned by the feature that fills the slots).
- No per-screen scaling logic anywhere — a screen referencing the scale
  directly is a halt condition (same D69 pattern as HC).

## Changed files (expected)
- **New:** `TextScaleStep` model + stepper composable in the theme package;
  scaling/reflow tests; `SettingsMapTest` additions.
- **Modified:** `GolemTheme.kt` (provider gains the scale param + density
  override), `ThemeSettings.kt` (third KV row, flow, suspend write),
  `MainActivity.kt`, `SettingsMap.kt` (one entry), `GolemReaderApp.kt` (one
  `controlContent` branch + provider arg), theme/token tests as needed, and —
  per docs-with-code — `state/current-state.md`, `state/active/step-S15.md`,
  `ledgers/testing.md`.
- **Not touched:** anything under `playback/`, `audio/`, `voice/`, `text/`,
  `identity/`, `transport/`, `highlight/`, `bootstrap/`; `app/schemas/` (no
  migration); `GolemThemeTokens.kt` value-sets (typography stays pure). A diff
  in any of these is a halt condition.

## Referenced decisions
**D108** (scale model: provider-level density composition; steps 0.85–1.5,
default 1.0; stepper control — resolves OB-068-1), **D109** (reflow proof
surfaces: Settings, Now Playing, Reading View, bottom nav — resolves OB-068-2),
D107 (applied: third KV row, no schema change), D104 (one-entry registration),
D101 (guard already covers font-size literals), D69 (central tested contract),
D105/D106 (HC composition target of T-066-B4), D68/D103 (entry appears because
its feature now exists).

## Acceptance criteria
1. `TextScaleStep` resolves every step to its multiplier; unknown stored values
   default to 1.0; the provider seam proves combined = OS × in-app
   (T-068-P1, JVM).
2. Raising the step grows rendered text on every checked surface with zero
   screen-code changes (T-068-B1, JVM).
3. At max in-app step × max-OS-scale density, the four D109 surfaces show no
   truncation or overlap in layout assertions (T-068-B2, JVM, Medium — device
   evidence closes the gap).
4. The D101 guard demonstrably fails on an injected font-size literal
   (T-068-B3, negative proof).
5. Changing the density's system fontScale changes rendered text size with the
   in-app step held constant (T-068-B4, JVM).
6. High contrast composes with max text scale — HC tokens + scaled text
   simultaneously, no interference (T-066-B4 scaling half, JVM — clears the S14
   deferral).
7. `setTextScale()` is suspend and proven off the calling thread (same pattern
   as S13/S14); registry test proves the Accessibility group now lists High
   contrast + Text size and still hides unbuilt entries.
8. Full JVM suite + `assembleDebug` + no-hardcode guard pass.
9. On-device (SM-S918U): stepper changes text size app-wide live; setting
   survives force-stop/relaunch; composes with Dark, Light, and HC; **at max OS
   font size × 1.5 in-app**, walk Settings, Now Playing, Reading — nothing
   clipped, **bottom nav labels explicitly photographed**; no measurable
   playback cost while changing scale during playback (T-068-C1, agent-run —
   contributor tag N/A per spec: layout concern, not listen-loop work).
   Screenshots (max-scale Settings, Reading, bottom nav close-up, HC+max-scale)
   archived for G4.

## Deferrals & notes (recorded, with owners)
- T-068-R1 full guided max-scale legibility sweep → operator look-check at
  closeout, re-walked at G4 with all axes on.
- ReservedSlot dp-height trap note → owned by the feature that first puts text
  in the reserved slots (F-073 preview strip family).
- Bottom nav at max scale: if device evidence shows framework-level clipping,
  that returns here as a design question with the screenshot in hand — not
  silently accepted, not pre-solved.

## Test posture (AI-recommended: full step)
- **Automated (JVM, High):** step model, provider seam, app-wide growth,
  guard negative proof, suspend-write proof, registry, HC composition.
- **Automated (JVM, Medium):** reflow layout assertions at max scale.
- **Agent-run device (Medium):** live scaling, persistence, HC/theme
  composition, max-scale walk with archived screenshots.
- **Guided-manual (Medium):** operator look-check at closeout (T-068-R1).

## Implementation record (2026-07-13)
- Added the five-step `TextScaleStep` model and theme-owned A− / percentage / A+
  stepper. The provider alone multiplies system font scale by the selected step;
  screens and typography value-sets are unchanged.
- Reused `theme_settings` for the third `text_scale` row. The write is suspend and
  IO-dispatched; no schema or exported-schema file changed.
- Registered Text size after High contrast in Accessibility and routed it through the
  generic shell control seam. `MainActivity` collects the preference as a Flow.
- Operator approved `app/build.gradle.kts` as a narrow changed-file addition solely
  for the JVM Compose test dependency after the existing classpath proved insufficient.

## Verification record (2026-07-13)
- Clean baseline `./gradlew testDebugUnitTest` passed. TDD RED runs failed on missing
  S15 APIs/registration/plumbing; focused GREEN runs then passed.
- `TextScaleStepTest`, `ThemeSettingsRepositoryTest`, `SettingsMapTest`, startup,
  stepper, plumbing, and `TextScalingLayoutTest` cover AC1–7. The layout harness
  composes all four D109 surfaces at maximum density, compares token text pixels at
  normal/raised in-app and OS scale, and proves HC + captured 3.0 font scale. Robolectric
  does not alter final glyph bounds for font-scale changes, so device screenshots close
  the layout-engine gap while JVM density-to-pixel assertions prove growth centrally.
- Final fresh `./gradlew testDebugUnitTest`, `./gradlew assembleDebug`,
  `bash guards/no-hardcode-check.sh`, and `git diff --check` passed before G3.
- D101 negative proof is `NoHardcodeGuardTest`, which injects a temporary `18.sp` UI
  font-size literal and requires the real guard to fail before confirming clean pass.

## Device evidence (2026-07-13)
- SM-S918U `R5CW72ZRMWP`: Android font scale 2.0 × app 1.5. Live scaling, persistence,
  Light/Dark/HC composition, Settings/Now Playing/Reading reflow, and playback survival
  passed. Original Android font scale 1.0 was restored afterward.
- Bottom navigation did **not** clip. `Now Playing` remained within its item by 8px on
  the left and 9px on the right at the tightest observed frame, so the named halt
  condition did not trigger.
- Evidence: `archive/S15-text-scaling/max-scale-settings.png`,
  `max-scale-reading.png`, `bottom-nav-max-scale-close-up.png`,
  `high-contrast-max-scale.png`, and `run-log.md`.

## Acceptance evidence map
1. AC1 — `TextScaleStepTest` + `TextScalingLayoutTest` provider capture.
2. AC2 — provider-only source/plumbing tests + SM-S918U live-growth evidence.
3. AC3 — four `TextScalingLayoutTest` surface tests + four archived device captures.
4. AC4 — `NoHardcodeGuardTest` negative seed + clean guard output.
5. AC5 — `TextScaleStepTest.providerSeamCombinesSystemAndInAppFontScaleMultiplicatively`
   and device Android-scale observation.
6. AC6 — `TextScalingLayoutTest.highContrastTokensAndMaximumTextScaleComposeWithoutInterference`
   + HC/max screenshot.
7. AC7 — repository off-thread test + `SettingsMapTest` Accessibility ordering/absence.
8. AC8 — final fresh commands recorded at G3 below.
9. AC9 — `archive/S15-text-scaling/run-log.md` and four named screenshots.

## Closeout status
Implementation and agent-run evidence are complete at high automated / medium device
confidence. T-068-R1 remains the operator look-check at G4. Branch disposition is fixed
by operator instruction: keep `s15-text-scaling` after push for independent verification.
