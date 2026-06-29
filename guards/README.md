# Guards — the enforcement layer

These convert Armature's load-bearing gates from "the AI promised" to "the tool blocked
it." They are **universal**: the scripts and hooks here never change per project. The
only stack-specific values live in **`guards.config`** — the bucket.

## How it works
- `hooks/pre-commit` + `hooks/pre-push` — block bad commits/pushes (branch, secrets,
  docs-with-code, failing check). They read `guards.config`.
- `gate-check.sh` — prints a PASS/FAIL report the agent must show at G3.
- `ci.yml` — an independent off-machine check (copy to `.github/workflows/`).
- `guards.config` — THE BUCKET: your project's `TEST_CMD`, `BUILD_CMD`,
  `SECRET_SCAN_CMD`, etc.

## Install (one command, per the setup manual)
```
bash guards/install-guards.sh
```

## KEY SETUP STEP — tailor to this project
The default is **not** a hardcoded stack. During project setup your AI assistant gathers
your stack details and fills `guards.config` and `ci.yml`. Until a value is filled, the
guard that needs it is skipped with a notice (it never blocks blindly). Filling these is
a required step in the setup manual.
