---
id: CURRENT-STATE
tier: state
status: active
updated: 2026-07-01
if-incomplete: "You are at the source of truth. If something isn't here, it isn't current."
---
# Current state — THE source of truth for "now"

- **Active version:** V0 (foundation)   **Phase:** P1 — Walking Skeleton   **Step:** S5 (next)   **current-rung:** —
- **Last committed:** s4-text-pipeline / S4 text pipeline / git HEAD / 2026-07-01
- **Coverage target:** see reference/coverage-target.md   ·   **Test posture (active step):** automated + agent-run device

## What's happening right now
S4 text pipeline is complete and committed. The app now has F-018 thin text extraction over
the existing EPUB structural reader, F-027 parse -> pre-clean -> segment -> display/spoken
fork orchestration with observable empty rule slots, and F-028 BreakIterator-based sentence
segmentation with thin corrections and clause sub-split tags. The primary fixture is the
real public-domain Tom Sawyer EPUB chapter 1. JVM tests cover the deterministic
extraction/clean/segment/index behavior; the S23 device smoke test extracted, cleaned,
segmented, and forked the same chapter in 409 ms with a 143,360-byte post-GC memory delta.

## Open items needing attention
- T-057-C1 and T-057-C2 remain owed agent-run measurements; T-057-C3 remains a contributor
  to the integrated listen-loop system-budget test at end of T3.
- KI-S3-001 remains open: `androidx.test.services` has no UID on the S23, so the
  external-cache-clear permission grant is skipped until a later step depends on that
  device path again.

## Next action
Start **Step S5 — One voice speaks (F-048, F-008)**.
