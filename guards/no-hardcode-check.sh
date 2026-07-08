#!/usr/bin/env bash
set -euo pipefail

ROOT="$(git rev-parse --show-toplevel)"
cd "$ROOT"

status=0

check_file() {
  local file="$1"
  local matches
  matches="$(
    grep -nE 'Color\(|0x[0-9A-Fa-f]{6,8}|[0-9]+(\.[0-9]+)?\.(dp|sp)|durationMillis[[:space:]]*=[[:space:]]*[0-9]+|delay\([0-9]+[Ll]?\)' "$file" || true
  )"
  if [ -n "$matches" ]; then
    printf '%s\n%s\n' "$file" "$matches"
    status=1
  fi
}

while IFS= read -r file; do
  case "$file" in
    app/src/main/java/com/golemreader/theme/*) continue ;;
    app/src/main/java/com/golemreader/playback/*) continue ;;
    app/src/main/java/com/golemreader/audio/*) continue ;;
    app/src/main/java/com/golemreader/voice/*) continue ;;
    app/src/main/java/com/golemreader/text/*) continue ;;
    app/src/main/java/com/golemreader/identity/*) continue ;;
    app/src/main/java/com/golemreader/transport/*) continue ;;
    app/src/main/java/com/golemreader/highlight/*) continue ;;
    app/src/main/java/com/golemreader/bootstrap/*) continue ;;
  esac
  check_file "$file"
done < <(find app/src/main/java/com/golemreader -name '*.kt' -type f | sort)

if [ "$status" -eq 0 ]; then
  echo "no hardcoded UI color, size, or duration literals outside com.golemreader.theme"
else
  echo "hardcoded UI literals found outside com.golemreader.theme"
fi

exit "$status"
