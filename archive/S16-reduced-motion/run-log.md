---
id: S16-DEVICE-RUN
tier: evidence
status: accepted
updated: 2026-07-13
---
# S16 reduced-motion device run

Device: Samsung SM-S918U (Galaxy S23 Ultra), attached over USB.

## Objective evidence completed

- Installed the fresh debug APK with app data retained.
- The new Reduce motion row appeared after Text size under Accessibility and exposed
  an accessibility-labelled switch.
- Turned the in-app setting on, force-stopped/relaunched the app, and confirmed the
  row remained On and checked. Capture: `reduced-motion-on-settings.png`.
- Turned the in-app setting off, temporarily set Android's animator-duration scale
  to `0`, relaunched, and exercised playback. The app advanced to chapter 5,
  sentence 7; the process remained alive; the fatal/starvation log check was clean.
  Capture: `os-reduced-playback.png`.
- Restored the phone's animator-duration setting to its original absent/default state
  (`settings get global animator_duration_scale` returned `null` before and after).
- Automated evidence separately proves the reduced branch selects an instant jump and
  performs no animated scroll; the device path showed no playback/starvation regression.
- After the independent-review fixes, installed the final APK and replayed the same
  chapter-5 opening passage in both modes. Normal motion advanced through sentence 8;
  the reduced run advanced into the same passage and displayed the tracked highlight in
  Reading View. Neither leg emitted a fatal or starvation log entry. Captures:
  `normal-motion-playback.png` and `reduced-motion-playback-final.png`.

## Independent review correction

The first independent review found three integration gaps before commit: the OS-effective
state was incorrectly reused as the switch's stored state, renderer fade was not consumed,
and the tested announcement decision object was not driving delivery. All three were fixed
with focused RED/GREEN regressions. Re-review found no Critical or Important issues and
declared the branch safe to commit.

## Operator closeout

The operator approved the archived visual evidence and guided TalkBack behavior on
2026-07-13. Acceptance covers the single polite “Catching up” announcement after the
500 ms hold and the absence of a recovery announcement.
