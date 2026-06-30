---
id: DECISION-LOG
tier: ledger
status: active
updated: 2026-06-30
if-incomplete: "The locked register is foundation/design-charter.md; full text in foundation/design-spec.md."
---
# Decision log (all decisions, over time)

- **D1–D76 — LOCKED.** Canonical register: `foundation/design-charter.md`. Full clause
  text: `foundation/design-spec.md` (Parts I–II). Consolidated from Design Spec v0.5.0 +
  Grooming Decision Log v1.0.0/v1.1.0/v1.2.0; the **D30 reconciliation gate passed**.
- **Foundation gate G0 — prototype lite-waived (operator-delegated, 2026-06-28).**
  The G0 visual-prototype requirement was waived for Golem Reader because: (a) the design
  is exceptionally complete (76 locked decisions, 70+ requirements, gate passed);
  (b) the dominant risk is the audio listen-loop, already feasibility-tested on-device
  (T1–T5), not screen layout; (c) the walking skeleton is itself the minimal runnable
  proof a prototype would stand in for. Operator approved 2026-06-28.

> New build-time decisions append below as **D77+**, each via `templates/decision.md`.

# Decision D77 — Bootstrap stack
- Date: 2026-06-30  ·  Status: locked  ·  Maps to phase: P1
- Operator-delegated? yes — AI chose, reasoning below

## Context
S1 needs a minimal native Android project that proves the build, install, launch, JVM test,
and guard workflow before feature work begins.
## Decision
Use Jetpack Compose + Material3 for UI, package and namespace `com.golemreader`, application
ID `com.golemreader`, `minSdk = 29`, `compileSdk`/`targetSdk = 36`, Kotlin + Android Gradle
Plugin versions available from the installed stable Android Studio toolchain, and Gradle
Kotlin DSL.
## Reasoning
Compose is the current-standard Android UI toolkit and fits the synchronized-highlight
rendering planned later. `minSdk = 29` covers the S23 Ultra while retaining modern Android
media/service APIs. The package name is simple to fix during bootstrap and costly to change
later.
## Alternatives considered
XML views were rejected because the planned reading surface is custom and state-driven.
Raising `minSdk` was unnecessary for the target device. A different package namespace was
unnecessary churn.
## Consequences
Future P1 steps build on a standard Compose Android module with a stable application ID and
Gradle Kotlin DSL wiring.

# Decision D78 — Gate-check control-flow fix
- Date: 2026-06-30  ·  Status: locked  ·  Maps to phase: P1
- Operator-delegated? yes — AI accepted corrected guard behavior, reasoning below

## Context
S1 could not commit after successful build/test verification because `guards/gate-check.sh`
reported both PASS and FAIL rows for checks that had passed, and treated an intentionally
deferred secret scanner as a hard G3 failure.
## Decision
Use the corrected `guards/gate-check.sh` that makes `row()` return success, changes
PASS/FAIL checks to explicit if/else branches, and reports an unconfigured secret scan as
SKIP to match the pre-commit hook and guard README setup policy.
## Reasoning
The guard should block real failures, not control-flow artifacts. The corrected behavior
keeps the working-branch and docs-with-code checks enforceable while allowing S1 to proceed
with the secret scanner explicitly deferred by `guards.config`.
## Alternatives considered
Configuring a scanner during S1 was rejected because S1 only scoped the Android bootstrap
test/build guard values. Bypassing G3 was rejected because S1 requires a shown gate-check
PASS report before commit.
## Consequences
S1 can complete with G3 evidence. The upstream Armature template defect is recorded in
`ledgers/known-issues.md` for later template cleanup.
