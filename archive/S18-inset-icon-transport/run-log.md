---
id: S18-RUN-LOG
tier: archive
status: verification-ready
updated: 2026-07-15
---
# S18 inset and icon-transport run log

## Dependency path

- The bundled core icon set supplied `PlayArrow` only.
- The S18-pre-authorized `androidx.compose.material:material-icons-extended` artifact
  supplies `Pause`, `SkipNext`, and `Stop`. No other dependency was added.

## Machine results

- T-018-B1 — `BootstrapLaunchDeviceTest`: `OK (1 test)` on SM-S918U (Android 16).
- T-018-B2 — `transportButtonsExposeAccessibleNames`: `OK`; Play, Pause, Resume,
  and Stop were found by content description on SM-S918U.
- T-018-B3 — `KeyboardNavigationDeviceTest`: `OK (4 tests)` on SM-S918U. The S17
  test source file was not modified.
- T-018-B4 — `allFourSurfacesRespectTheStatusBarInset`: `OK`; Now Playing, Reading
  View, Settings, and bottom navigation were checked against the device status-bar inset.
- Configured JVM check plus debug build — `./gradlew testDebugUnitTest assembleDebug`:
  `BUILD SUCCESSFUL`.
- Known environment notice — the `androidx.test.services` no-UID message appeared
  during Gradle-connected setup, consistent with KI-S3-001. Direct installed-runner
  execution was used after restoring the established EPUB and Piper device assets.

## T-018-R1 — base axes

High contrast off, text 100%, reduced motion off:

- `r1-light-now-playing.png`
- `r1-light-reading-view.png`
- `r1-light-settings.png`
- `r1-dark-now-playing.png`
- `r1-dark-reading-view.png`
- `r1-dark-settings.png`

## T-018-R2 — stacked axes

High contrast on, text 150%, reduced motion off:

- `r2-light-now-playing-max-text-hc.png`
- `r2-dark-now-playing-max-text-hc.png`

## Remaining operator sequence

- Operator look-check of the eight archived captures: _[placeholder]_
- T-018-R3 both-edges device walk: _[placeholder]_
- Operator acceptance: _[placeholder]_
