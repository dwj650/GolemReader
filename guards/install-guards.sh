#!/usr/bin/env bash
# Installs the Armature guards into this repo's git hooks. Run once, from anywhere in the repo.
set -euo pipefail
ROOT="$(git rev-parse --show-toplevel)"
install -m 0755 "$ROOT/guards/hooks/pre-commit" "$ROOT/.git/hooks/pre-commit"
install -m 0755 "$ROOT/guards/hooks/pre-push"   "$ROOT/.git/hooks/pre-push"
chmod +x "$ROOT/guards/gate-check.sh" || true
echo "Armature guards installed (pre-commit, pre-push)."
echo "NEXT (key setup step): fill in guards/guards.config for this project's stack,"
echo "and tailor guards/ci.yml. Your AI assistant will gather these details with you."
