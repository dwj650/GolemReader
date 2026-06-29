---
id: FEATURE-MATRIX
tier: reference
status: committed
updated: 2026-06-28
source: Candidate Feature Inventory v0.4.0
if-incomplete: "Per-feature detail lives in reference/requirements/req-F-###.md (placed just-in-time per step)."
---
# Feature matrix — Golem Reader (F-001–F-078)

Index of features and their version band. Per-feature requirements are placed in
`reference/requirements/` just-in-time as each Step needs them.

## V1 — the walking skeleton + its tiers
| Tier | Features |
|---|---|
| T0 substrate / identity / library | F-057 storage tiers · F-020 book identity · F-018 EPUB parser · F-019 library catalog · F-065 theme |
| T1 text→sentences | F-027 text pipeline · F-028 sentence segmentation |
| T7 normalization & rules | F-029 system normalization · F-030 class toggles · F-031 review list · F-033 locale · F-036 pronunciation rules · F-037 live preview · F-042 authored rules · F-043 rule resolution · F-044 rule-packs · F-045 voice-bound packs |
| T2 voice & audio | F-048 engine/voice select+hot-swap · F-049 isolated preview · F-008 audio quality pipeline |
| T3 listen-loop spine | **F-001 streaming engine · F-016 sync highlight · F-002 transport · F-014 reading view · F-015 now playing** |
| Navigation & surfaces | F-003 chapter nav + scrubber · F-073 embedded preview · F-075 action-row host + rotation · F-074 minimal image surface |
| Playback shaping | F-004 cross-chapter · F-005 speed · F-006 inter-sentence pause · F-007 volume |
| System integration (T5) | F-009 MediaSession hub · F-010 background · F-011 lock-screen · F-012 audio focus · F-013 resume-after-kill |
| Persistence & robustness (T6) | F-058 position save/restore · F-021 sources · F-022 availability/relink · F-023 reparse · F-024 book states |
| Accessibility & shell (T8) | F-064 settings host · F-066 high-contrast · F-067 reduced-motion · F-068 text-scaling · F-069 keyboard nav · F-070 onboarding · F-078 a11y reading-display overrides |

## V2 — bodies behind V1 seams
F-074 full image viewer · F-076 in-book search · F-077 sleep timer · F-017 richer scroll/follow · D76 sync-glow control.

## Wishlist (parked) / V3
See `reference/wishlist.md` and design-spec Wishlist. Global rule promotion = V3 (F-047).
