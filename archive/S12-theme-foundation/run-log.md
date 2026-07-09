# S12 Theme Foundation Device Evidence

- Date: 2026-07-08
- Device: Samsung SM-S918U
- Branch: `s12-theme-foundation`

## Screenshots
- `dark-reading.png`
- `dark-now-playing.png`
- `light-reading.png`
- `light-now-playing.png`
- `dark-restart-reading.png` — captured after writing persisted `ThemeChoice.Dark`,
  force-stopping, and relaunching while OS mode had been light.

## Commands / Evidence
- `adb devices` reported `R5CW72ZRMWP device`.
- `adb install -r app/build/outputs/apk/debug/app-debug.apk` succeeded.
- `adb shell cmd uimode night no` switched follow-system to light; light screenshots
  were captured. OS night mode was restored with `adb shell cmd uimode night yes`.
- `adb shell am instrument -w -e class com.golemreader.theme.ThemeSettingsDeviceTest
  com.golemreader.test/androidx.test.runner.AndroidJUnitRunner` returned `OK (1 test)`
  and wrote persisted dark choice through the real precious database.
- Final restart log showed `Displayed com.golemreader/.MainActivity`.

## Notes
- Initial device launch exposed a Room main-thread crash in `MainActivity`; the follow-up
  fix removes the blocking `currentChoice()` call from composition startup.
- Android test/install churn removed app media assets; `tom-sawyer.epub` and Piper
  assets were restored before the final restart proof.
