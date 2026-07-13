# S14 High-Contrast Device Evidence

- Date: 2026-07-13
- Device: Samsung SM-S918U (`R5CW72ZRMWP`)
- Branch: `s14-high-contrast`

## Screenshots

- `hc-dark-settings.png` — SHA-256 `fb881096cbe66ab81a26e0b08cbf4eb2c60aca8b5fff8f3e46d0aac4ec9ece5c`
- `hc-dark-reading.png` — SHA-256 `22d12a96b6f8123ac76e4fd5dae10b51e418a3bf4aeec2364e12e491e33ca9e6`
- `hc-light-settings.png` — SHA-256 `0ca13bc5c0f7894d15f68c976c51b3efef1fe64862918d4d88c7d2a3fbf3295e`
- `hc-light-reading.png` — SHA-256 `1eda87dbbc58c2a40f003f196f57201ef1437e3fc29703dfebe6694222edfcc4`

All images are 1080×2316 RGBA PNG captures from the app installed on the named device.

## Commands and evidence

- `adb install -r app/build/outputs/apk/debug/app-debug.apk` succeeded.
- Android hierarchy inspection reported Dark + HC checked, then Light + HC checked.
- After `adb shell am force-stop com.golemreader`, a cold relaunch reported Light and
  High contrast still checked, proving both precious preferences survived restart.
- During active playback, hierarchy inspection proved HC Off then On, `ps -T` retained
  the `GolemPlaybackSession` thread, and a freshly cleared fatal log returned no entries.
- The final rebuilt switch exposed `content-desc="High contrast"` and no NAF marker.

## Notes

- The first UI pass found the switch itself lacked a semantic label even though the
  visible setting label was adjacent. A focused RED/GREEN JVM regression test and a
  one-line semantic label fixed the device-visible NAF marker before final verification.
- The four screenshots were captured before that semantics-only fix; their pixels are
  identical to the rebuilt control because the change affects accessibility metadata,
  not layout or color.
- The operator approved all four screenshots for the guided legibility look-check on
  2026-07-13.
