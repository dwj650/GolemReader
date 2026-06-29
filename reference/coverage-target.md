---
id: COVERAGE-TARGET
tier: reference
status: committed
updated: 2026-06-28
if-incomplete: "Return to state/current-state.md."
---
# Coverage target & test taxonomy (D28 / D29)

**Four-bucket taxonomy (D28)** — every feature's acceptance is stated in four buckets:
1. **Presence & wiring** — is it there, reachable, switched on at the right time?
2. **Behavior & limits** — does it obey the rules and stay inside allowances?
3. **Cost & budget** — does it stay within speed/memory/battery/storage allowances?
4. **Result** — does the end output match what we wanted?

**Coverage mapping (D29) — how each kind of claim is verified:**
| Claim kind | Verification form |
|---|---|
| Logic / pure functions | **Automated** unit tests |
| Device / system behavior | **Agent-run** on-device, recorded |
| Perceptual (audio quality) | **Guided-manual** (by ear) |
| Numbers / budgets | **Local-measured** where a real number exists, else **system-tagged** to the integrated listen-loop budget (F-001 T-001-C1) |

**Integrated budget pass condition (F-001 T-001-C1):** no thermal throttling · no system
CPU-usage warnings · no visibly-watchable battery drain · while sustaining target audio quality.
