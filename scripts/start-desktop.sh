#!/usr/bin/env bash
set -euo pipefail

# Start an XFCE desktop session via TigerVNC and expose it via noVNC on port 6080.
# Requirements (already installed by setup step): xfce4, tigervnc-standalone-server, novnc, websockify, dbus-x11

display=":1"
vnc_port=5901
novnc_port=6080
novnc_launch="/usr/share/novnc/utils/launch.sh"
novnc_proxy="/usr/share/novnc/utils/novnc_proxy"

mkdir -p "$HOME/.vnc"
cat > "$HOME/.vnc/xstartup" <<'EOF'
#!/bin/sh
unset SESSION_MANAGER
unset DBUS_SESSION_BUS_ADDRESS
exec startxfce4
EOF
chmod +x "$HOME/.vnc/xstartup"

# Stop any existing sessions
vncserver -kill "$display" >/dev/null 2>&1 || true
pkill -f "websockify.*:${novnc_port}" >/dev/null 2>&1 || true
pkill -f "/usr/share/novnc/utils/launch.sh.*--listen ${novnc_port}" >/dev/null 2>&1 || true

# Start TigerVNC (no auth; keep Codespaces port private or rely on Codespaces auth)
echo "Starting TigerVNC on ${display} (port ${vnc_port})..."
VNC_ARGS=("$display" -localhost yes -geometry 1600x900 -xstartup "$HOME/.vnc/xstartup")
# Prefer password auth unless running in Codespaces where port forwarding adds auth.
if [[ -n "${CODESPACE_NAME-}" ]]; then
  # Codespaces: rely on noVNC + Codespaces port authentication; keep VNC bound to localhost.
  VNC_ARGS+=( -SecurityTypes None )
else
  # Set a default password if none exists (password: vncpassword)
  if [[ ! -f "$HOME/.vnc/passwd" ]]; then
    echo "vncpassword
vncpassword
n" | vncpasswd >/dev/null 2>&1 || true
  fi
fi

if ! vncserver "${VNC_ARGS[@]}"; then
  echo "ERROR: Failed to start vncserver; check ~/.vnc/*.log for details." >&2
  exit 1
fi

# Start noVNC proxy
echo "Starting noVNC on port ${novnc_port} -> VNC localhost:${vnc_port}..."
if [[ -x "$novnc_launch" ]]; then
  nohup "$novnc_launch" --listen "${novnc_port}" --vnc "localhost:${vnc_port}" >/tmp/novnc.log 2>&1 &
elif [[ -x "$novnc_proxy" ]]; then
  nohup "$novnc_proxy" --listen "${novnc_port}" --vnc "localhost:${vnc_port}" >/tmp/novnc.log 2>&1 &
else
  echo "ERROR: noVNC launcher not found. Checked: $novnc_launch and $novnc_proxy" >&2
  exit 1
fi
echo $! > /tmp/novnc.pid

sleep 1
echo "Active listeners:"
ss -lntp | grep -E ":(${vnc_port}|${novnc_port})" || true

if [[ -n "${CODESPACE_NAME-}" ]]; then
  echo "Open this URL: https://${novnc_port}-${CODESPACE_NAME}.githubpreview.dev"
else
  echo "Forward port ${novnc_port} in the Ports panel and open it in your browser."
fi
