#!/usr/bin/env bash
# Install and launch EMH on Android emulator or physical device via ADB.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
APK="$ROOT/app/build/outputs/apk/debug/app-debug.apk"
PKG="com.emh.app"
export ANDROID_HOME="${ANDROID_HOME:-$HOME/Android/Sdk}"
export PATH="$ANDROID_HOME/emulator:$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"

HOST_IP="$(ip -4 route get 1.1.1.1 2>/dev/null | awk '{print $7; exit}' || hostname -I | awk '{print $1}')"
EMULATOR_OLLAMA="http://10.0.2.2:11434"
PHONE_OLLAMA="http://${HOST_IP}:11434"

echo "=== EMH Android smoke test ==="

if [[ ! -f "$APK" ]]; then
  echo "Building debug APK..."
  export JAVA_HOME="${JAVA_HOME:-/opt/android-studio/jbr}"
  (cd "$ROOT" && ./gradlew assembleDebug --no-daemon -q)
fi

# Ollama bridge for physical devices (emulator uses 10.0.2.2 → host loopback)
if curl -sf --max-time 2 http://127.0.0.1:11434/api/tags >/dev/null; then
  if [[ -n "$HOST_IP" ]] && ! curl -sf --max-time 2 "http://${HOST_IP}:11434/api/tags" >/dev/null; then
    echo "Starting Ollama LAN bridge (${HOST_IP}:11434 → 127.0.0.1:11434)..."
    if ! pgrep -f "socat TCP-LISTEN:11434,bind=${HOST_IP}" >/dev/null 2>&1; then
      socat "TCP-LISTEN:11434,bind=${HOST_IP},fork,reuseaddr" "TCP:127.0.0.1:11434" &
      sleep 1
    fi
  fi
else
  echo "Warning: Ollama not running. Start with: ollama serve" >&2
fi

pick_device() {
  if [[ -n "${ANDROID_SERIAL:-}" ]]; then
    echo "$ANDROID_SERIAL"
    return
  fi
  local emu phone
  emu="$(adb devices | awk '/^emulator-[0-9]+\tdevice$/ {print $1; exit}')"
  phone="$(adb devices | awk '/\._adb-tls-connect\._tcp\tdevice$/ {print $1; exit}')"
  if [[ -n "$emu" ]]; then echo "$emu"
  elif [[ -n "$phone" ]]; then echo "$phone"
  else adb devices | awk 'NR>1 && $2=="device" {print $1; exit}'
  fi
}

SERIAL="$(pick_device)"
if [[ -z "$SERIAL" ]]; then
  echo "No ADB device found. Options:" >&2
  echo "  1. Connect phone via USB/wireless debugging" >&2
  echo "  2. Start emulator: emulator -avd EMH_Test -no-window -gpu swiftshader_indirect" >&2
  exit 1
fi

echo "Using device: $SERIAL"
adb -s "$SERIAL" install -r "$APK"
adb -s "$SERIAL" shell pm grant "$PKG" android.permission.RECORD_AUDIO 2>/dev/null || true
adb -s "$SERIAL" shell am start -n "$PKG/.MainActivity"

if [[ "$SERIAL" == emulator-* ]]; then
  OLLAMA_URL="$EMULATOR_OLLAMA"
else
  OLLAMA_URL="$PHONE_OLLAMA"
fi

echo ""
echo "EMH launched. In app Settings set Ollama URL to:"
echo "  $OLLAMA_URL"
echo ""
echo "Manual checks:"
echo "  1. Grant Overlay + Microphone permissions"
echo "  2. Enable Accessibility service for EMH"
echo "  3. Save Ollama URL → Check Ollama"
echo "  4. Test Floating Panel (Demo) → Generate → Voice/Speak"