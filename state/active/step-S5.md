---
id: S5
tier: state
status: complete
updated: 2026-07-01
cross-refs: [P1, D2, D22, D23, D27, D40, D44, D45, D88, D89, F-048, F-008]
if-incomplete: "Return to state/current-state.md."
---
# Step S5 — One voice speaks (F-048 substrate + F-008)
Phase: P1  ·  Feature(s): F-048 (substrate only, see boundary below), F-008  ·  current-rung: Closeout

## Statement of Work
Given the first sentence already produced by S4 (Tom Sawyer chapter 1), synthesize it to
real audio on the S23 through **two** neural TTS engines — Kokoro and Piper — via a shared
`VoiceEngine` interface (D2), with F-008's universal text hygiene and edge-silence trim
applied to both. This is the first audible output the app has ever produced. It proves the
`VoiceEngine` abstraction genuinely covers two different engines, not just one. Full F-048
(voice identity, hot-swap, persistence, the Voice Manager UI) is explicitly deferred — see
D89.

## Scope — files that WILL change
- New package `app/src/main/java/com/golemreader/voice/`:
  - `VoiceEngine.kt` — the shared interface (D2): `load`, `speak` (+ abort), `stop`,
    `release`, `reportCapabilities`. Foundation-level; F-048 will extend it later, S5 only
    needs the four core calls.
  - `KokoroVoiceEngine.kt` — adapter wrapping sherpa-onnx + the Kokoro V1 model
    (`~/AndroidAssets/kokoro-en-v0_19/`). Engine-specific quirks stay isolated here (D2d).
  - `PiperVoiceEngine.kt` — adapter wrapping sherpa-onnx + the `en_US-lessac-medium` Piper
    model. Same interface, separate adapter.
  - `VoiceEngineModels.kt` — shared data classes: capability report, synth result (mono
    float PCM `[-1,1]` at the engine sample rate, per D22(b)).
- New package `app/src/main/java/com/golemreader/audio/`:
  - `TerminalCueHygiene.kt` — F-008 C-008-1/C-008-2 (R1–R3, R10–R13): strip space before
    terminal; append period only on bare **sentence-terminal** segments (never clause
    sub-splits — reads the F-028 type tag, D44); suppress quotation marks in the spoken
    branch only (display keeps them); collapse runs of the *same* terminal, protecting
    `…`/`—` first (D45 ordering); leave mixed terminals (`?!`) intact. Engine-blind — no
    Kokoro or Piper special-casing in this file.
  - `EdgeSilenceTrimmer.kt` — F-008 C-008-3/C-008-4 (R4–R7): per-segment leading/trailing
    near-silence trim to a clean zero baseline before the segment is handed off; threshold
    ~0.003, ~15 ms speech-protecting floor as shared defaults, per-engine overridable
    (D2d); no cross-segment access; sets no spacing (player will own the gap later, D13j —
    not relevant yet, there's no player); zero-length segment keeps its sentence index
    anchored, no crash.
  - `SynthesisHarness.kt` — the S5 test entry point only: takes one sentence record from
    S4's pipeline, runs it through `TerminalCueHygiene`, hands it to a `VoiceEngine`
    adapter, runs the result through `EdgeSilenceTrimmer`, and plays it via
    `android.media.AudioTrack`. This is scaffolding for S5's own verification, not a
    production component — S6/F-001 will replace it with the real streaming player.
- `app/src/test/java/com/golemreader/audio/` — JVM unit tests for
  `TerminalCueHygiene`/`EdgeSilenceTrimmer` (pure functions, no device needed).
- `app/src/androidTest/java/com/golemreader/voice/` — one on-device test per engine:
  load → synthesize the S4 test sentence → confirm non-empty, non-silent PCM back →
  play it. Two tests: `KokoroSynthesisDeviceTest.kt`, `PiperSynthesisDeviceTest.kt`.
- Build config:
  - `app/build.gradle.kts` — add a local flat-file repo pointing at `app/libs/` and an
    `implementation(files(...))` (or `fileTree`) dependency on the sherpa-onnx `.aar`.
    Confirm minSdk 29 / target ABI compatibility with the `.aar` at Inspect.
  - `.gitignore` — add `app/libs/*.aar` (the library binary is never committed; D88).
- Local machine setup (not committed, documented in this SOW for whoever runs the step).
  Confirmed source paths on the building machine (T14):
  - `.aar`: copy `~/AndroidAssets/libs/sherpa-onnx-1.13.2.aar` into the repo's
    `app/libs/sherpa-onnx-1.13.2.aar` (gitignored, D88).
  - Kokoro model: `~/AndroidAssets/kokoro-en-v0_19/` (contains `model.onnx`,
    `tokens.txt`, `voices.bin`, `espeak-ng-data/`) — push this whole folder.
  - Piper model: `~/AndroidAssets/vits-piper-en_US-lessac-medium/` (sherpa-onnx-packaged;
    contains `en_US-lessac-medium.onnx`, `.onnx.json`, `tokens.txt`, and
    `espeak-ng-data/`) — push **only this folder**, not the rest of the large Piper voice
    collection at `~/AndroidAssets/en/`.
  - Destination on the S23 (test-readable app media storage):
    `/sdcard/Android/media/com.golemreader/test-voices/kokoro/` and
    `/sdcard/Android/media/com.golemreader/test-voices/piper/`. Exact `adb push`
    commands are recorded in this file's Verify section.
- Registers to update at Record/Closeout: `state/current-state.md`,
  `state/phase-index.md` (mark S5 done, name S6 next), `ledgers/decision-log.md`
  (append D88 and D89 — full text below), `ledgers/known-issues.md` (append the KI below).

## Non-goals — explicitly NOT touched
- **Voice identity / fingerprinting (D38)** — F-048, later step.
- **Active-voice source of truth (D39)** — F-048, later step.
- **Hot-swap mid-playback (D8b)** — F-048, later step; nothing to swap *between* yet
  since there's no continuous playback (that's S6).
- **Settings persistence (D15b/D31)** — F-048, later step; no settings surface exists.
- **Voice Manager UI / import flow** — F-048, later step; no host screen exists (F-064,
  much later).
- **Piper `:`/`;` segment-final clip fix** — F-045 (the Piper quirk pack). Not built yet.
  The raw artifact will be audible on Piper output in S5 and is *expected*, not a bug —
  logged as a known issue below, not fixed here.
- **Kokoro V1 pack** — F-045; not relevant to claim here (D40 says it ships empty anyway).
- **Streaming / continuous playback, the producer-consumer buffer** — F-001, arrives at S6.
  `SynthesisHarness.kt` is throwaway scaffolding, not the real player.
- **Synchronized highlight, Reading View, Now Playing** — F-016/F-014/F-015, later steps.
- **System normalization (F-029) and rule resolution (F-043)** — not built; S5's spoken
  text goes through F-008 hygiene only, same as S4 left those slots as no-ops.

## Decisions in play
- **D2** — the `VoiceEngine` interface (foundation); S5 implements it, does not redefine
  it.
- **D22(b)** — post-render layer invariants: mono float `[-1,1]` in/out, rebuildable
  output, never mutates in place, zero-length segment handled gracefully.
- **D23 a–e, D40, D44, D45** — the F-008 text-hygiene rules and their exact scoping
  (bare-terminal definition, run-collapse ordering) implemented in
  `TerminalCueHygiene.kt`.
- **D27** — the edge-silence trim mechanism and its default numbers, implemented in
  `EdgeSilenceTrimmer.kt`.
- **D88 (new this step)** — S5 test-asset handling: local-only `.aar`, adb-push models,
  nothing large committed to git. *(Append full entry to decision-log.md at Record.)*
- **D89 (new this step)** — S5 boundary: two-engine synthesis harness proven now; voice
  identity, hot-swap, persistence, and the Voice Manager UI deferred to a later step.
  *(Append full entry to decision-log.md at Record.)*

## Acceptance criteria (what "done" means)
**F-008 (fully in scope — no dependency on deferred F-048 machinery)**
- [x] T-008-P1/P2 — hygiene ops + trimmer present and wired at the right stage; no other
      post-render op runs by default.
- [x] T-008-B1 — each D23 op correct on fixtures (strip-space, append-period-when-bare,
      collapse-same-terminal, mixed terminals intact, keep `…`/`—`).
- [x] T-008-B2 — suppress-quotes is spoken-branch-only; display keeps them.
- [x] T-008-B5 — edge-trim mechanism correct: trims to baseline, no cross-segment access,
      sets no spacing.
- [x] T-008-B7 — zero-length segment keeps its sentence index anchored, no crash.
- [x] T-008-B8 — no in-place mutation; output is rebuildable.
- [x] T-008-B9 — bare-terminal definition + terminal-only scope (a clause sub-split from
      S4's type tag never gets a forced period).
- [x] T-008-B10 — run-collapse ordering protects `…`/`—` before collapsing runs.

**F-048 substrate (informal — no formal F-048 acceptance criteria are claimed this step;
those all require identity/hot-swap infrastructure that D89 defers)**
- [x] `VoiceEngine` interface compiles and is implemented by two independent adapters with
      no shared engine-specific code leaking into the interface itself.
- [x] Both adapters load their model, synthesize the S4 test sentence, and release cleanly
      with no native-memory leak across repeated load/release cycles (spot-checked, not a
      formal budget).

**Cross-cutting / result (agent-run, on S23)**
- [x] Kokoro: the S4 test sentence is heard, cleaned and edge-trimmed, on the S23 speaker.
- [x] Piper: the same sentence is heard, cleaned and edge-trimmed, on the S23 speaker (the
      unfixed `:`/`;` artifact, if the test sentence triggers it, is expected — log it,
      don't fix it).
- [x] Record wall-clock time from "synthesize called" to "first audio sample available"
      for each engine — no fixed budget yet (Budget TBD per F-048/F-008 docs), just honest
      numbers.

## Test posture
- Form: automated (JVM) for all F-008 hygiene/trim logic + agent-run on the S23 for engine
  load/synthesize/playback, both engines.
- Plan: same split as S2/S3/S4 (D79-style) — deterministic text/DSP logic is
  gate-enforced; real synthesis latency and audible quality are recorded, not gated on a
  fixed number yet.
- Principle conformance: engine-blind core (D24 sorting test) — confirm
  `TerminalCueHygiene.kt` and `EdgeSilenceTrimmer.kt` contain zero Kokoro- or
  Piper-specific branches; anything engine-specific belongs in the adapters only.

## Entry gate (G2)
- [x] Prior step closed — S4 merged to main at commit `590e111`.
- [x] current-state fresh — pulled 2026-07-01, confirms S5 is next.
- [x] Scope + non-goals declared — above.
- [x] Operator approved — direct operator instruction, 2026-07-01 (D88, D89, and this
      plan approved in chat).

## Rung tracker
- [x] Orient  - [x] Scope  - [x] Inspect  - [x] Change
- [x] Verify  - [x] Record  - [x] Commit (G3, guarded)  - [x] Closeout

## Known issue to log at Orient
Append to `ledgers/known-issues.md`:

> **KI-S5-001 — Piper segment-final `:`/`;` artifact is expected and unfixed in S5.**
> F-008's core hygiene is engine-blind by design (D40); the Piper-specific `:`/`;`
> segment-final clip fix (D23f) belongs to the F-045 Piper quirk pack, which does not
> exist yet. If S5's test sentence ends in or contains a `:`/`;` that triggers this
> artifact on Piper output, it will be audible. This is not a regression and not in scope
> to fix here. Owner: whenever F-044/F-045 (rule-packs, voice-bound packs) are built.

## Verify result
- Result: pass  ·  Confidence: high  ·  T-id: T-008 (+ informal F-048 substrate checks)
- Notes: JVM gate passed with `./gradlew testDebugUnitTest`; debug build passed with
  `./gradlew assembleDebug`. Device setup used:
  `adb push /home/davidt14/AndroidAssets/kokoro-en-v0_19/. /sdcard/Android/media/com.golemreader/test-voices/kokoro/`
  and
  `adb push /home/davidt14/AndroidAssets/vits-piper-en_US-lessac-medium/. /sdcard/Android/media/com.golemreader/test-voices/piper/`.
  The S4 Tom Sawyer fixture was present at
  `/sdcard/Android/media/com.golemreader/fixtures/text/tom-sawyer.epub`.
  Kokoro direct instrumentation passed:
  `adb shell am instrument -w -r -e class com.golemreader.voice.KokoroSynthesisDeviceTest com.golemreader.test/androidx.test.runner.AndroidJUnitRunner`;
  timing was 1272 ms to returned audio, 11,462 samples at 24 kHz. Piper direct
  instrumentation passed:
  `adb shell am instrument -w -r -e class com.golemreader.voice.PiperSynthesisDeviceTest com.golemreader.test/androidx.test.runner.AndroidJUnitRunner`;
  timing was 1007 ms to returned audio, 9,135 samples at 22,050 Hz. The `.aar` declares
  minSdk 21 and includes `arm64-v8a`; the target S23 is API 36 with `arm64-v8a`.

## Closeout
- Committed: s5-voice-speaks / git HEAD  ·  Next step: S6 (F-001 streaming engine — continuous
  chapter playback)
