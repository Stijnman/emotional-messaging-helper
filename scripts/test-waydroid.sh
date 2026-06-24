#!/usr/bin/env bash
# Install and launch EMH in Waydroid for smoke testing.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
APK="$ROOT/app/build/outputs/apk/debug/app-debug.apk"
PKG="com.emh.app"

echo "=== Waydroid EMH smoke test ==="

if ! command -v waydroid >/dev/null; then
  echo "Waydroid not installed. See https://docs.waydro.id/" >&2
  exit 1
fi

status="$(waydroid status 2>&1 || true)"
if echo "$status" | grep -q "Session:STOPPED"; then
  echo "Starting Waydroid session..."
  waydroid session start &
  for _ in $(seq 1 30); do
    sleep 2
    waydroid status 2>&1 | grep -q "Session:RUNNING" && break
  done
fi

waydroid status

if [[ ! -f "$APK" ]]; then
  echo "Building debug APK..."
  export ANDROID_HOME="${ANDROID_HOME:-$HOME/Android/Sdk}"
  export JAVA_HOME="${JAVA_HOME:-/opt/android-studio/jbr}"
  (cd "$ROOT" && ./gradlew assembleDebug --no-daemon -q)
fi

echo "Installing $APK ..."
waydroid app install "$APK"

echo "Launching $PKG ..."
waydroid app launch "$PKG"

BRIDGE_IP="$(ip -4 -o addr show waydroid0 2>/dev/null | awk '{print $4}' | cut -d/ -f1 || echo 192.168.240.1)"
if ! curl -sf --max-time 2 "http://${BRIDGE_IP}:11434/api/tags" >/dev/null; then
  if command -v socat >/dev/null && curl -sf --max-time 2 http://127.0.0.1:11434/api/tags >/dev/null; then
    echo "Starting Ollama bridge for Waydroid (${BRIDGE_IP}:11434 → 127.0.0.1:11434)..."
    pkill -f "socat TCP-LISTEN:11434,bind=${BRIDGE_IP}" 2>/dev/null || true
    socat "TCP-LISTEN:11434,bind=${BRIDGE_IP},fork,reuseaddr" "TCP:127.0.0.1:11434" &
    sleep 1
  else
    echo "Warning: Ollama not reachable. Start with: ollama serve" >&2
  fi
fi

echo ""
echo "Manual checks in Waydroid UI:"
echo "  1. Tap 'Grant Overlay Permission' → allow"
echo "  2. Tap 'Grant Microphone (Voice)' → allow"
echo "  3. Enable Accessibility for EMH (Settings → Accessibility)"
echo "  4. Tap 'Test Floating Panel (Demo)' → verify panel opens"
echo "  5. Tap Speak / Voice input (mic needs permission)"
echo ""
echo "Limitations in Waydroid:"
echo "  - WhatsApp overlay/paste may not work (no real WhatsApp + limited accessibility)"
echo "  - Ollama must be reachable from container (use host LAN IP, not localhost)"
echo "  - Voice/STT depends on Google services in the image"
echo ""
echo "Ollama URL in EMH Settings: http://${BRIDGE_IP}:11434"
echo "  (Waydroid cannot use localhost — use the bridge IP above)"