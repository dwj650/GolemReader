#!/usr/bin/env bash
# Armature gate-check (G3). The agent RUNS this and shows the output — a gate is not
# passed by the agent's word, but by this report.
ROOT="$(git rev-parse --show-toplevel)"; cd "$ROOT"
CFG="guards/guards.config"; [ -f "$CFG" ] && source "$CFG" || true
overall="PASS"
row(){ printf '  %-44s %s\n' "$1" "$2"; [ "$2" = "FAIL" ] && overall="FAIL"; }
echo "Armature gate-check (G3):"

BRANCH="$(git rev-parse --abbrev-ref HEAD)"
[ "$BRANCH" != "${MAIN_BRANCH:-main}" ] && row "on a working branch" "PASS" || row "on a working branch" "FAIL"

if [ -n "${SECRET_SCAN_CMD:-}" ] && [ "${SECRET_SCAN_CMD}" != "[FILL during setup]" ]; then
  eval "$SECRET_SCAN_CMD" >/dev/null 2>&1 && row "no secrets staged" "PASS" || row "no secrets staged" "FAIL"
else row "secret scan configured" "FAIL"; fi

CHANGED="$(git diff --cached --name-only || true)"
if echo "$CHANGED" | grep -q "^${CODE_PREFIX:-src/}"; then
  PATTERN="$(echo "${STATE_PATHS:-state/current-state.md}" | sed 's/[[:space:]]\+/|/g')"
  echo "$CHANGED" | grep -Eq "($PATTERN)" && row "state/registers updated with code" "PASS" || row "state/registers updated with code" "FAIL"
else row "no code change (docs-with-code n/a)" "PASS"; fi

echo "RESULT: $overall"
[ "$overall" = "PASS" ]
