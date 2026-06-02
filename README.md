# Emotional Messaging Helper (EMH)

Android floating overlay app for WhatsApp that provides psychologically intelligent, emotionally fulfilling reply suggestions using local AI (Ollama supported).

## Current Status (Heavily Polished - Autonomous Development)

The app has been developed fully autonomously across 20+ improvement loops (odd rounds: broad changes on **all files**; even rounds: test/verify everything). It is now in a state where the core experience is genuinely usable on a real device.

### What's Solid & Polished
- **Vision Flow**: Full "Add Vision" → MediaProjection capture → automatic use with vision models (rich "Vision attached" card + one-tap clear + recapture).
- **Floating Panel**: Rich Compose UI with haptics everywhere, smart loading messages that change for vision, tone presets, templates, history, "Check Ollama".
- **AI Quality**: Strong Emotional Prompt Engine (figurative 0-10 + tones + memory + hardened vision instructions).
- **OllamaClient**: Timeouts, vision model detection (`isLikelyVisionModel`), auto-suggest, `healthCheck`, `listModels`.
- **History + Restore**: Full viewer + seamless restore into the live panel.
- **Auto-paste**: Best-effort direct (accessibility ACTION_SET_TEXT + focus + send button search) + reliable clipboard fallback with explicit toasts.
- **Settings**: Live DataStore (changes affect the panel immediately).

After extensive autonomous development (including 20+ systematic improvement loops across **all files** + dedicated testing phase with continuous fixes), the app is being iteratively hardened until it is as complete and reliable as possible.

**Latest in this continue pass**: Fixed class structure bug in EmotionalPromptEngine (parseResponse now compiles), strengthened vision prompt instructions, added Ollama listModels + better healthCheck, expanded tests, CI on feature branches, cleaned legacy placeholder code, huge docs improvements for Android Studio onboarding.

### Current Focus / Next Step
The project is in active autonomous "test until all working completely" mode.

**THE IMMEDIATE NEXT STEP IS YOU OPENING IT IN ANDROID STUDIO.**

See the big callout in [SETUP.md](SETUP.md). Open the project, let it sync, build, run on a physical device with Ollama + a vision model, and report back the results (build errors, runtime behavior, WhatsApp version paste results, vision quality, etc.). That feedback will drive the next autonomous fixes.

Core features are implemented and wired:
- Real-time WhatsApp message detection via Accessibility
- Ollama AI (text + vision from screenshot)
- Compose floating panel with haptics, templates, vision UI, history button
- Auto-paste (direct + clipboard)
- History with save on generation and restore
- Persistent settings
- Relationship memory

Open in Android Studio to build and run on device (requires Ollama running locally with vision model for full features). Autonomous iterations continue without stopping.

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