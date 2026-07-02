---
id: ARCHIVE-V0-P1-TEST-SUMMARY
tier: archive
status: accepted
updated: 2026-07-02
---
# V0-P1 test summary

Source of truth: `ledgers/testing.md`.

P1 accumulated high-confidence JVM coverage for the core spine: storage substrate, EPUB identity, text extraction and segmentation, streaming producer/consumer/session logic, highlight mapping, transport routing, Reading View, Now Playing, bootstrap wiring, and the S11 end-of-book repeat regression.

P1 accumulated medium-confidence S23 Ultra device proof for each integrated layer: storage behavior, text pipeline smoke, streaming playback across chapter boundary, real-engine highlight timing with Kokoro and Piper, wall-clock transport, rendered Reading/Now Playing UI, real bootstrap launch, and this G4 end-to-end app demonstration.

Known remaining test gaps stay tracked in `state/current-state.md` and `ledgers/testing.md`: integrated battery/thermal budget at end of T3, T-057-C1/C2 storage cost readings, and the already-recorded PR-7 accessibility gap accepted for P2 per D95.

