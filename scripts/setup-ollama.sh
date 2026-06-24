#!/bin/bash
set -e

echo "=== Emotional Messaging Helper - One-Click Ollama Setup ==="
echo ""
echo "This will pull the recommended models for best results:"
echo "  - Text model: llama3.2:3b (fast, good quality) or llama3.1"
echo "  - Vision model: llava:7b (or llava-llama3 / bakllava for better performance)"
echo ""
echo "Make sure Ollama is installed and running on this machine."
echo "See https://ollama.com for installation instructions."
echo ""

# Check if ollama is available
if ! command -v ollama &> /dev/null; then
    echo "ERROR: 'ollama' command not found."
    echo "Please install Ollama first: https://ollama.com"
    exit 1
fi

echo "Pulling text model (llama3.2:3b)..."
ollama pull llama3.2:3b || echo "Warning: llama3.2:3b pull failed, trying fallback..."

echo ""
echo "Pulling vision model (llava:7b)..."
ollama pull llava:7b || echo "Warning: llava:7b pull failed. You can try 'ollama pull llava-llama3' manually."

echo ""
echo "✅ Setup complete!"
echo ""
echo "Recommended next steps:"
echo "1. Verify models: ollama list"
echo "2. Test text: ollama run llama3.2:3b 'Hello, how are you feeling today?'"
echo "3. Open the EMH project in Android Studio and run on device."
echo "4. In Settings, set Ollama host to your computer's LAN IP (e.g. http://192.168.1.42:11434)"
echo ""
echo "For best emotional depth on device, use a 7B+ model if your hardware allows."
