#!/usr/bin/env bash
set -euo pipefail

display=":1"
novnc_port=6080

echo "Stopping noVNC (port ${novnc_port}) if running..."
if [[ -f /tmp/novnc.pid ]]; then
  kill "$(cat /tmp/novnc.pid)" 2>/dev/null || true
  rm -f /tmp/novnc.pid
fi
pkill -f "websockify.*:${novnc_port}" 2>/dev/null || true
pkill -f "/usr/share/novnc/utils/launch.sh.*--listen ${novnc_port}" 2>/dev/null || true

echo "Stopping TigerVNC on ${display} if running..."
vncserver -kill "$display" >/dev/null 2>&1 || true

echo "Done."
