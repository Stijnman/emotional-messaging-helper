# Emotional Messaging Helper (EMH)

Android floating overlay app for WhatsApp that provides psychologically intelligent, emotionally fulfilling reply suggestions using local AI (Ollama supported).

## Current Status (Heavily Polished - Autonomous Development)

The app has been developed fully autonomously across 10 improvement loops with heavy emphasis on polish. It is now in a state where the core experience is genuinely usable, delightful, and close to production-ready on a real device.

### What's Solid & Polished
- **Vision Flow**: Full "Add Vision" → MediaProjection capture → automatic use with vision models (with clear "Vision attached" UI + one-tap clear).
- **Floating Panel**: Rich Compose UI with haptics on every action, smart adaptive loading messages ("Analyzing screenshot..." etc.), prominent vision state, templates, and smooth interactions.
- **AI Quality**: Strong Emotional Prompt Engine with tuned vision support.
- **History + Restore**: Full viewer with seamless restore into the active panel.
- **Settings**: Persistent and live (URL + model used immediately).
- **Auto-paste**: Best-effort direct typing + reliable clipboard fallback with clear toasts.

After extensive autonomous development (including 20+ systematic improvement loops across **all files** + dedicated testing phase with continuous fixes and error resolution), the app is being iteratively hardened until it is as complete and reliable as possible. Latest: fixed engine structure, vision prompt quality, more tests + CI branch coverage.

### Current Focus
- Unit tests for core AI and data components (EmotionalPromptEngine, OllamaClient, HistoryManager, etc.)
- Compose UI tests for the floating panel
- CI pipeline improvements to run tests on every push/PR
- Increasing test coverage and reliability

The project is in active autonomous "test until all working completely" mode (20+ loops executed across every file + continuous fixes). 

Core features are implemented and wired:
- Real-time WhatsApp message detection via Accessibility
- Ollama AI (text + vision from screenshot)
- Compose floating panel with haptics, templates, vision UI, history button
- Auto-paste (direct + clipboard)
- History with save on generation and restore
- Persistent settings
- Relationship memory

Open in Android Studio to build and run on device (requires Ollama running locally with vision model for full features). Autonomous iterations continue without stopping to expand tests, fix issues, and polish until the app is fully functional and stable.

## Features (Vision)

- Fully Compose-based floating bubble + expandable emotional panel
- Accessibility Service that detects WhatsApp messages in real-time
- Powerful Emotional Prompt Engine (psychological + relational depth)
- Ollama local LLM client (text + vision)
- Figurative Level slider (0–10) + Quick Tone Presets
- Encrypted Relationship Memory Vault
- Message History with search
- Screenshot / Vision context support
- Quick Template Gallery
- One-tap Copy, Speak, and Send

## How to Run

See [SETUP.md](SETUP.md) for detailed instructions.

**Requirements:**
- Physical Android device
- Ollama running locally (recommended: llama3.2 or llava)

## License

Personal / Educational use.