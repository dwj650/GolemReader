---
id: S9
tier: state
status: approved
updated: 2026-07-02
cross-refs: [P1, D93, D1, D4b, D13k, D50, F-014, F-015, F-016, F-001, F-002]
if-incomplete: "Return to state/current-state.md."
---
# Step S9 — Reading View + Now Playing

## Changelog
- v1.0.0 (2026-07-02) — proposed and approved in the same design session.

Phase: P1 · Feature(s): F-014, F-015 · current-rung: Orient

## Statement of Work
Build the two host screens that make S6-S8's invisible work visible: Reading View
(display text, live highlight, scroll-follow) and Now Playing (transport buttons,
buffering indicator, position). Both minimal, per phase-index. This is the first step
with any visible UI in the app.

## Scope — files that WILL change

New package `com.golemreader.ui.reading`:
- `ReadingViewScreen.kt` — C-014-1/2/3. Renders the chapter's display text (quotes/
  punctuation intact — `SentenceRecord.display`, not `.spoken`), highlights the current
  sentence/clause from `HighlightStateEmitter.currentState()`, auto-scrolls to keep it
  in view, tolerates manual scroll-away and re-centres on the next advance (D50). No
  tap-to-inspect (deferred per D93 — F-043 doesn't exist).

New package `com.golemreader.ui.nowplaying`:
- `NowPlayingScreen.kt` — C-015-1/3. Shows current book/position and the four
  transport buttons wired to `TransportCommands`. Reserves empty slots for F-073/F-075
  (D93) — positioned, unfilled.
- `BufferingIndicator.kt` — C-015-2. Renders `StarvationState.isBuffering` — a visible
  "catching up..." state, not motion-only (a text/state equivalent, since F-067
  reduced-motion governance isn't built yet but the non-motion-only bar still applies
  to what's built now).

Extends existing files:
- `ui/GolemReaderApp.kt` — currently a single static "Golem Reader" title screen.
  Needs a simple screen-switch (in-app state, not a navigation library — no
  `androidx.navigation` dependency exists yet and D93 scopes out the full gesture
  grammar) so both screens are reachable.

New technical bridge (not named in the spec, but required to make it work):
- Neither `HighlightStateEmitter` nor `StarvationState`/`TransportHub` expose
  observable/reactive state (no `Flow`, no Compose `State`) — they're plain classes
  with synchronous getters. Compose won't recompose on its own when they change.
  Reading View and Now Playing each need a polling bridge (e.g. a `LaunchedEffect`
  loop reading current state into Compose state on a short interval) to reflect live
  playback. Exact polling interval and mechanism is Codex's design call within this
  SOW's boundary — note it plainly in the completion report so a later step can
  replace polling with real observables if that becomes worth doing.

New tests:
- `app/src/test/java/com/golemreader/ui/reading/*Test.kt` and
  `.../ui/nowplaying/*Test.kt` — JVM: display-branch text (quotes intact), shared-index
  highlight mapping, transport button → command wiring, buffering indicator state
  mapping.
- `app/src/androidTest/java/com/golemreader/ui/ReadingAndNowPlayingDeviceTest.kt` —
  on-device, agent-run: a real Tom Sawyer read-along — the highlighted sentence is
  visibly on-screen and advances with the voice; transport buttons control a live
  session; the buffering indicator appears if triggered.

## Non-goals — explicitly NOT touched
- F-073 (embedded sync-preview) and F-075 (action-row) slot *content* — slots
  reserved empty per D93.
- F-043 tap-to-inspect (logic doesn't exist; host point skipped, not stubbed).
- Full D52 gesture-grammar navigation — a plain screen-switch instead, per D93.
- Image Viewer (F-074) — out of scope entirely.
- Theme tokens (F-065) beyond Material3 defaults — not yet built as its own system.
- Reduced-motion governance (F-067) as a real setting — the buffering indicator just
  avoids being motion-only by default; no toggle exists yet.

## Decisions in play
- **D93 (this step)** — S9 scope boundary: slots reserved not filled, tap-to-inspect
  skipped, plain screen-switch instead of full gesture grammar. Locked, operator-
  approved.
- D1/D4b (shared sentence index — Reading View's highlight and Now Playing's position
  both read the same index S7/S6 already established), D13k (pause/resume keeps
  buffer — Now Playing's controls don't need to handle a "re-render" state), D50
  (re-follow on next advance, pinned V1 scroll behavior).

## Acceptance criteria (what "done" means)
- [ ] T-014-P1 — Reading View renders display text and subscribes to F-016's
      highlight signal.
- [ ] T-014-B1 — display text keeps quotes/punctuation (uses `.display`, not
      `.spoken`).
- [ ] T-014-B2 — highlighted sentence matches the audio's sentence (shared index).
- [ ] T-014-B3 — scroll-follow keeps the highlight in view; manual scroll-away
      tolerated, re-centres on next advance (D50).
- [ ] T-015-P1/P3 — Now Playing renders book/position/controls; F-073/F-075 slots are
      present and positioned, empty.
- [ ] T-015-B (transport) — the four buttons drive a live session via
      `TransportCommands`, matching S8's behavior exactly (no new command logic here).
- [ ] New: buffering indicator reflects `StarvationState.isBuffering` correctly and
      clears on refill; has a non-motion (text/state) form.
- [ ] New: both screens are reachable from a running app via the screen-switch.
- [ ] On-device (S23): a real read-along — highlighted text visibly advances with the
      voice across a passage; transport buttons control the same live session S8
      proved; buffering indicator appears under an artificially throttled run.

## Test posture
- Form: automated (JVM) + agent-run (device)
- Plan:
  - Automated: display-branch rendering, shared-index highlight mapping, button-to-
    command wiring, buffering-state mapping — maps to T-014-P1/B1/B2, T-015-P1/P3.
  - Agent-run device: real read-along visual + transport control + buffering-indicator
    trigger. Maps to T-014-B3/R1 and the new buffering/reachability criteria.
  - Explicitly deferred: T-014-P2/B4 (tap-to-inspect — skipped per D93), T-014-R2
    (full accessibility guided check — later), anything touching F-073/F-075 content.
- Principle conformance: step-scoping discipline — D93 keeps two real, spec-named
  features from silently absorbing two more (F-073/F-075) plus a third that doesn't
  exist yet (F-043).

## Entry gate (G2)
- [x] Prior step closed — S8 done, merged, commit 5b90fce.
- [x] current-state fresh — pulled 2026-07-02.
- [x] Scope + non-goals declared (above).
- [x] Operator approved — 2026-07-02, approved as-is.

## Rung tracker
- [ ] Orient  - [ ] Scope  - [ ] Inspect  - [ ] Change
- [ ] Verify  - [ ] Record  - [ ] Commit (G3, guarded)  - [ ] Closeout

## Verify result
- Result: —  ·  Confidence: —  ·  T-id: —
- Notes: not yet run.

## Closeout
- Committed: —  ·  Next step: G4 (Phase acceptance gate — plays one book end-to-end)
