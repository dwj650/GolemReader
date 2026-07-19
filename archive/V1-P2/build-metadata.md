---
id: ARCHIVE-V1-P2-BUILD-METADATA
tier: archive
status: accepted
updated: 2026-07-15
---
# V1-P2 build metadata

- Code baseline: `d295c66` — the accepted S18 branch tip, merged to `main` by ff-only
  ritual 2026-07-15. Under `--ff-only` the P2 closeout commit becomes main's tip; this
  archive's code state is that commit's tree.
- Phase: **P2 — Accessible Shell**. Gate **G4 accepted 2026-07-15**.
- Date: 2026-07-15
- Device: Samsung Galaxy S23 Ultra (`SM-S918U`)
- App package: `com.golemreader`
- App version: `0.1.0` / versionCode `1`
- SDK: minSdk 29 · targetSdk 36
- Build used for gate evidence: debug build installed with `./gradlew installDebug`
- Peripherals: Bluetooth keyboard paired with the S23 (required for the keyboard axis —
  T-064-R2, T-068-R1 composition sweep, and the S17 T-069-R1 walkthrough)
- Book fixture: `/sdcard/Android/media/com.golemreader/fixtures/text/tom-sawyer.epub`
  (the P1 fixture-name note still applies and is unchanged by P2)
- Steps in phase: S12–S17 as planned, plus **S18 inserted at the gate** by D117
- Decisions minted in phase: **D102–D118** (D-ceiling at phase close: **D118**)
- Dependency added in phase (S18): `androidx.compose.material:material-icons-extended`

## What this baseline is
The **Accessible Shell**: the P1 walking skeleton, now themed and accessible along four
axes — high contrast, text scaling, reduced motion, keyboard — each proven individually
and **proven stacked** during live playback at the gate. Visual contract: **D103**
(`foundation/prototype/golem-reader-prototype-v0_3_0.jsx`).

## Screenshots in this archive
Base themes (HC off, 100%, reduced motion off), captured at S18 closeout and
operator-look-checked 2026-07-15:
`now-playing-light.png`, `now-playing-dark.png`, `reading-view-light.png`,
`reading-view-dark.png`, `settings-light.png`, `settings-dark.png`.
Stacked axes (HC on, 150% — the top of the five-step D108 scale):
`now-playing-max-text-hc-light.png`, `now-playing-max-text-hc-dark.png`.

Source: `archive/S18-inset-icon-transport/`, copied here as the phase baseline.
