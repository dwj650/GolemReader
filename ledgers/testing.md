---
id: TESTING
tier: ledger
status: active
updated: 2026-06-28
if-incomplete: "Coverage policy is reference/coverage-target.md."
---
# Testing register

## Foundation feasibility passes (carried from design — on-device, S23 Ultra)
- **T1** — sentence-callback model (Kokoro, clean prose): one callback per sentence. Medium.
- **T3** — post-render artifact + timing: DC negligible; joins silence-to-silence; Kokoro RTF ~0.9, Piper ~0.08. Medium.
- **T4** — short-utterance/dialogue + floor-batching: terminal punctuation is the trigger; slice mismatch confirms D12g. Medium.
- **T5** — terminal-cue verify: trailing-quote/repeated-terminal artifacts; `...`/`—` render well. Medium.

## Owed — build-phase (validate during P1+)
- Long-run register-drift listen (D26); batching keep/kill A/B (D12/OB-001-1); edge-trim floor tuning (D27); Kokoro sustained-load/thermal; abort cleanliness; skip-to-sound gap; audio-focus behaviors; background survival; Room round-trip; persisted-URI survival.

> Per-step tests (T-###-B#) are added to the step's req doc and recorded here as they're written.
