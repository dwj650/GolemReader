---
id: ARCHIVE-V0-P1-RUN-LOG
tier: archive
status: accepted
updated: 2026-07-02
---
# V0-P1 G4 run log

## Fixture
- Book content: O. Henry, "The Gift of the Magi".
- Device path: `/sdcard/Android/media/com.golemreader/fixtures/text/tom-sawyer.epub`.
- Reason for filename mismatch: current P1 bootstrap is hardcoded to this filename.
- App-loaded content: chapter ordinal 5, the full story chapter.
- Sentence count: 162 app sentence segments, calculated with the same Java `BreakIterator`-style sentence splitting and the current 800-character bootstrap segment limit.

## Timeline
- 2026-07-02T00:55:16-07:00: debug APK installed successfully on `SM-S918U`.
- 2026-07-02T00:55:31-07:00: app force-stopped and logcat cleared for a clean run boundary.
- 2026-07-02T00:55:39-07:00: `MainActivity` launched.
- 2026-07-02T00:56:57-07:00: Play pressed through the real Now Playing UI.
- 2026-07-02T00:57:17-07:00: Now Playing mid-play screenshot captured; UI showed `Chapter 5, sentence 11`.
- 2026-07-02T00:57:58-07:00: Reading View mid-highlight screenshot captured; UI showed a highlighted sentence in the live text.
- 2026-07-02T01:00:29-07:00: poll showed app process alive and `GolemPlaybackSession` thread present.
- 2026-07-02T01:03:50-07:00: poll showed app process alive and `GolemPlaybackSession` thread present.
- 2026-07-02T01:07:28-07:00: natural completion first observed; app process alive, `GolemPlaybackSession` thread absent.
- 2026-07-02T01:08:46-07:00: post-completion stability check; app process alive, `GolemPlaybackSession` thread still absent.
- 2026-07-02T01:09:53-07:00: final screenshot captured after unlock; completion state remained stable.

## Outcome
- Result: pass.
- Manual intervention during playback: none after pressing Play. The screen later locked after natural completion; the operator unlocked it for final capture and reported touching no app controls.
- End condition: natural completion. The playback thread exited and stayed absent after a one-minute post-completion wait.
- End-of-book repeat: not observed.
- Starvation/stall events: no uncaught starvation or stall observed.
- Crash/fatal logs: focused `AndroidRuntime:E`, `DEBUG:E`, and `libc:F` logcat query returned no output after the run.

## Screenshots
- `reading-mid-highlight.png` — Reading View with live highlighted sentence.
- `now-playing-mid-play.png` — Now Playing with transport state and position.
- `final-post-completion.png` — final visible app state after natural completion.

