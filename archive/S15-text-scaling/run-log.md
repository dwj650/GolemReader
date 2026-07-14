# S15 Text-Scaling Device Evidence

- Date: 2026-07-13
- Device: Samsung SM-S918U (`R5CW72ZRMWP`)
- Branch: `s15-text-scaling`
- Test density: Android `font_scale=2.0` × Golem Reader `150%`; original Android
  `font_scale=1.0` restored after capture.

## Screenshots

- `max-scale-settings.png` — SHA-256 `28dc58a5d5d06b15b47775d83e166fcbcfc5ad608dd38ea6c6614bf5e74a52b2`
- `max-scale-reading.png` — SHA-256 `4fa4dd02d66b3836ff9ec1ae845b4007b2c0f33a6624363f1ca5cc70ba50633b`
- `bottom-nav-max-scale-close-up.png` — SHA-256 `3a18ae8893e102b16eb6869ca9a3c9725655efcc87cb7402799fbb4fa905dd22`
- `high-contrast-max-scale.png` — SHA-256 `90d54c6ec3e23de7b3794b71be264e089ffeffccf8bf8b5447eb3de801333f7d`

Full captures are 1080×2316; the bottom-nav close-up is a 1080×420 crop of the
verified Settings frame.

## Results

- The live stepper advanced 100% → 115% → 130% → 150%; text grew and reflowed
  app-wide. At 150%, A+ was disabled.
- Force-stop/cold relaunch retained 150%, proving the precious preference survived.
- Light + HC + 150% and Dark + HC + 150% were both inspected live.
- Settings, Now Playing, and Reading remained usable at maximum combined scale.
- Bottom-nav labels did not clip. At the tightest point, `Now Playing` occupied
  x=8–519 inside its x=0–528 item and y=2059–2168 inside the navigation area.
- While playback was active, scale changed 150% → 130% → 150%. PID 14614 remained
  alive, `GolemPlaybackSession` and `AudioTrack` threads remained present, and the
  focused AndroidRuntime/DEBUG/libc fatal log was empty.

Confidence: medium (real-device layout and runtime observation).
