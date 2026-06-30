#!/usr/bin/env bash
# Armature gate-check (G3). The agent RUNS this and shows the output — a gate is not
# passed by the agent's word, but by this report.
# v1.0.1 fix: row() now returns success, so the `cmd && row PASS || row FAIL` idiom no
#   longer fires BOTH rows; checks use explicit if/else; an unconfigured secret scanner
#   reports SKIP (matching the pre-commit hook + guards.config "skip with a notice"
#   policy) instead of a hard FAIL.
ROOT="$(git rev-parse --show-toplevel)"; cd "$ROOT"
CFG="guards/guards.config"; [ -f "$CFG" ] && source "$CFG" || true
overall="PASS"
row(){ printf '  %-44s %s\n' "$1" "$2"; [ "$2" = "FAIL" ] && overall="FAIL"; return 0; }
echo "Armature gate-check (G3):"

BRANCH="$(git rev-parse --abbrev-ref HEAD)"
if [ "$BRANCH" != "${MAIN_BRANCH:-main}" ]; then row "on a working branch ($BRANCH)" "PASS"; else row "on the main branch" "FAIL"; fi

if [ -n "${SECRET_SCAN_CMD:-}" ] && [ "${SECRET_SCAN_CMD}" != "[FILL during setup]" ]; then
  if eval "$SECRET_SCAN_CMD" >/dev/null 2>&1; then row "no secrets staged" "PASS"; else row "no secrets staged" "FAIL"; fi
else
  row "secret scan (deferred - not configured)" "SKIP"
fi

CHANGED="$(git diff --cached --name-only || true)"
if echo "$CHANGED" | grep -q "^${CODE_PREFIX:-src/}"; then
  PATTERN="$(echo "${STATE_PATHS:-state/current-state.md}" | sed 's/[[:space:]]\+/|/g')"
  if echo "$CHANGED" | grep -Eq "($PATTERN)"; then row "state/registers updated with code" "PASS"; else row "state/registers updated with code" "FAIL"; fi
else
  row "no code change (docs-with-code n/a)" "PASS"
fi

echo "RESULT: $overall"
[ "$overall" = "PASS" ]
