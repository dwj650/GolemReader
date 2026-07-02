---
id: ARCHIVE-V0-P1-BUILD-METADATA
tier: archive
status: accepted
updated: 2026-07-02
---
# V0-P1 build metadata

- Commit: `551772619b9b3d5f4a1655c56ee0ce68b5300778`
- Date: 2026-07-02
- Device: Samsung Galaxy S23 Ultra (`SM-S918U`)
- App package: `com.golemreader`
- App version: `0.1.0` / versionCode `1`
- Build used for run: debug APK from `./gradlew assembleDebug`, installed with `adb install -r app/build/outputs/apk/debug/app-debug.apk`
- Voice assets: Piper `en_US-lessac-medium` under `/sdcard/Android/media/com.golemreader/test-voices/piper/`
- Book used: O. Henry, "The Gift of the Magi"
- Device fixture path: `/sdcard/Android/media/com.golemreader/fixtures/text/tom-sawyer.epub`
- Fixture note: the current bootstrap loads the hardcoded `tom-sawyer.epub` filename, but the staged EPUB metadata identifies the content as "The Gift of the Magi". The EPUB contains five front-matter chapters plus the full story in chapter ordinal 5, matching the current `BookBootstrap` start chapter.
- App sentence segments loaded: 162
- Observed run duration: 10m31s from Play press to first observed natural completion.

