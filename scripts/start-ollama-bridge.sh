#!/usr/bin/env bash
# Keep Ollama reachable from phone/emulator on the LAN IP.
set -euo pipefail

HOST_IP="$(ip -4 route get 1.1.1.1 2>/dev/null | awk '{print $7; exit}' || hostname -I | awk '{print $1}')"

if [[ -z "$HOST_IP" ]]; then
  echo "Could not detect LAN IP" >&2
  exit 1
fi

if ! curl -sf --max-time 2 http://127.0.0.1:11434/api/tags >/dev/null; then
  echo "Starting ollama serve..."
  nohup ollama serve >/tmp/ollama-serve.log 2>&1 &
  for _ in $(seq 1 20); do
    sleep 1
    curl -sf --max-time 2 http://127.0.0.1:11434/api/tags >/dev/null && break
  done
fi

if ! curl -sf --max-time 2 "http://${HOST_IP}:11434/api/tags" >/dev/null; then
  echo "Starting LAN bridge ${HOST_IP}:11434 → 127.0.0.1:11434 ..."
  pkill -f "socat TCP-LISTEN:11434,bind=${HOST_IP}" 2>/dev/null || true
  nohup socat "TCP-LISTEN:11434,bind=${HOST_IP},fork,reuseaddr" "TCP:127.0.0.1:11434" \
    >/tmp/ollama-bridge.log 2>&1 &
  sleep 1
fi

if curl -sf --max-time 3 "http://${HOST_IP}:11434/api/tags" >/dev/null; then
  echo "OK — use in EMH Settings: http://${HOST_IP}:11434"
else
  echo "Bridge failed. Check /tmp/ollama-serve.log and /tmp/ollama-bridge.log" >&2
  exit 1
fi