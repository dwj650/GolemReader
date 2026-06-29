---
id: PRINCIPLES-REGISTER
tier: reference
status: committed
updated: 2026-06-28
if-incomplete: "Return to foundation/design-spec.md (Guiding principles)."
---
# Principles register (PR-#) — honor every step

| PR | Principle | Conformance signal |
|---|---|---|
| PR-1 | Identity = DNA (streaming speech platform; speech/continuity/sync are first-class) | core loops treat text as a stream, not an ebook |
| PR-2 | Streaming = foundation (never mass-render) | playback starts before whole-book render |
| PR-3 | Audio quality first | long-session listening over convenience |
| PR-4 | Total architectural hotswappability | swapping a parser/engine touches zero core loops |
| PR-5 | Resource efficiency is first-class | battery/memory/thermal/storage/CPU are hard params (F-001 T-001-C1 budget) |
| PR-6 | User-centric by default | user controls engine/voice/speed; accessibility designed in |
| PR-7 | Accessibility by default; clean-room implementation | a11y = four tested contracts (D69); no third-party-derived code |

> A formal measurable Principles Register is a foundation-phase artifact (TBD); this is the working register.
