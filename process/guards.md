---
id: GUARDS
tier: constitution
status: stable
updated: [FILL: YYYY-MM-DD]
if-incomplete: "See guards/README.md for install + tailoring."
---
# The enforcement layer (guards)

Load-bearing gates are backed by mechanical **guards** so passing a gate is not merely
the agent's word. The guards live in `guards/` and are **tailored to this project's
stack during setup** (the install + tailoring is a key manual step). The operator never
authors them.

- **Secret block** — refuses any commit containing a key/token/password. (G3)
- **Test gate** — runs the project's declared check (`TEST_CMD`) and refuses on failure;
  if there are no automated tests, refuses a code commit lacking a recorded validation. (G3)
- **Branch protection** — refuses direct commits to the main branch. (G3/G5)
- **State-and-register check** — refuses a code commit that didn't also update
  `current-state` + registers. (G3)
- **CI** — re-runs the check + secret scan + lint off-machine on every push. (G3/G5)
- **Gate-check script** — prints a PASS/FAIL report the agent must show.

**Rule:** a guarded gate is passed only when the guard says so.
