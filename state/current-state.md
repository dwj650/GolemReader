---
id: CURRENT-STATE
tier: state
status: active
updated: 2026-07-01
if-incomplete: "You are at the source of truth. If something isn't here, it isn't current."
---
# Current state — THE source of truth for "now"

- **Active version:** V0 (foundation)   **Phase:** P1 — Walking Skeleton   **Step:** S6 (next)   **current-rung:** —
- **Last committed:** s5-voice-speaks / S5 one voice speaks / git HEAD / 2026-07-01
- **Coverage target:** see reference/coverage-target.md   ·   **Test posture (active step):** automated + agent-run device

## What's happening right now
S5 one voice speaks is complete and committed. The app now has an F-048 substrate
`VoiceEngine` interface with Kokoro and Piper sherpa-onnx adapters, plus F-008 terminal-cue
hygiene and edge-silence trimming wired through the S5 `SynthesisHarness`. JVM tests cover
the deterministic hygiene and trim behavior. On the S23, Kokoro and Piper both loaded,
synthesized, returned non-silent PCM, edge-trimmed, and played the first S4 Tom Sawyer
sentence. Recorded synthesize-to-audio-return timings: Kokoro 1272 ms (11,462 samples at
24 kHz); Piper 1007 ms (9,135 samples at 22,050 Hz).

## Open items needing attention
- T-057-C1 and T-057-C2 remain owed agent-run measurements; T-057-C3 remains a contributor
  to the integrated listen-loop system-budget test at end of T3.
- KI-S3-001 remains open: `androidx.test.services` has no UID on the S23, so the
  external-cache-clear permission grant is skipped until a later step depends on that
  device path again.
- KI-S5-001 remains open: Piper segment-final `:`/`;` artifacts are expected and unfixed
  until F-044/F-045 rule-packs/voice-bound packs are built.

## Next action
Start **Step S6 — Streaming engine (F-001)**.
