---
id: CURRENT-STATE
tier: state
status: active
updated: 2026-07-01
if-incomplete: "You are at the source of truth. If something isn't here, it isn't current."
---
# Current state — THE source of truth for "now"

- **Active version:** V0 (foundation)   **Phase:** P1 — Walking Skeleton   **Step:** S4 (next)   **current-rung:** —
- **Last committed:** s3-book-identity / S3 book identity / git HEAD / 2026-07-01
- **Coverage target:** see reference/coverage-target.md   ·   **Test posture (active step):** automated + agent-run device

## What's happening right now
S3 book identity is complete and committed. The app now has F-020: an EPUB structural
reader that resolves OPF spine entries without parsing chapter XHTML, a streamed D84
SHA-256 content-hash function with 8-byte big-endian length framing, the first
feature-owned precious table (`book_identity`) at Room schema v3, and a compute/store/lookup
service. The D31 v2→v3 migration is covered by Robolectric, and the S23 device suite covers
same-book recognition, different-book registration, restart persistence, and import-time
cost readings.

## Open items needing attention
- T-057-C1 and T-057-C2 remain owed agent-run measurements; T-057-C3 remains a contributor
  to the integrated listen-loop system-budget test at end of T3.

## Next action
Start **Step S4 — Text → sentences (F-018 thin + F-027 + F-028)**.
