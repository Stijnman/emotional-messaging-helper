# Setup Instructions

> **THE NEXT STEP RIGHT NOW**
>
> 1. Clone or pull the latest `main` branch.
> 2. **Open the folder in Android Studio** (this is the intended and only reliable way to build).
> 3. Let it sync + download the Gradle wrapper.
> 4. Build and run on a **physical Android device**.
> 5. Test with real Ollama (llava recommended for vision).
>
> Report back any build errors, sync problems, or runtime behavior from the device. Autonomous fixes will continue based on that feedback.

## 1. Open in Android Studio (Primary Path)
1. Use Android Studio Hedgehog / Iguana / Koala (2024.3+) or newer.
2. `File > Open` → select the `emotional-messaging-helper` folder.
3. Wait for Gradle sync (it will download Gradle 8.9 + the wrapper jar automatically).
4. If it complains about missing wrapper jar:
   - Run `File > Sync Project with Gradle Files`
   - Or `Build > Clean Project` then `Rebuild Project`

## 2. Gradle Sync & Build
This project was developed autonomously (many full-file improvement + test loops). Android Studio handles the wrapper for you.

Common issues & fixes:
- "Gradle wrapper not found" → Sync Project with Gradle Files (AS will fetch it).
- SDK not found → Let Android Studio install the required SDK platforms when prompted.
- "Could not find ... " after first sync → `Build > Clean` + `Rebuild`.
- Still stuck → Invalidate Caches / Restart.

Manual fallback (only if you have Gradle on PATH):
```bash
cd emotional-messaging-helper
gradle wrapper --gradle-version 8.9
./gradlew assembleDebug
```

The `gradlew` script prints clear instructions when run from terminal.

## 3. Required Permissions (on device)
- **Display over other apps** (Overlay / SYSTEM_ALERT_WINDOW)
- **Accessibility Service** → Enable "Emotional Messaging Helper" (this is what lets it read WhatsApp messages and auto-paste)

## 4. Ollama (easiest path for full features - YOLO Gemma)
Ollama must be running and reachable from the Android device (same WiFi is easiest; use your computer's LAN IP).

**Gemma recommendations** (Google models, great for the emotional agent + skills):
- `gemma3:4b` — Best balance, multimodal (vision works for screenshots), ~3.3GB
- `gemma4:e4b` — Stronger edge model
- `gemma4:e2b` — Lightest (great for lower-RAM phones)

Vision: Use Gemma 3 4B+ or 12B+ (they handle images natively, similar to llava).

Quick test:
```bash
ollama run gemma3:4b
# lightest on-device-friendly
ollama run gemma4:e2b
```

In the app: Change model in Settings. "Fetch models from Ollama" populates chips. The panel auto-suggests Gemma vision models when you attach screenshots.

## 5. True On-Device Gemma (no PC/Ollama server needed)
For maximum privacy and offline use:
- Install **Google AI Edge Gallery** (free official Android app).
- Download and run Gemma 4 E2B/E4B models directly on your phone (fully offline).
- Test model quality there — these are optimized for mobile.

Longer term: We plan native integration using Google's MediaPipe LLM Inference API + LiteRT so the app can run Gemma completely on-device without any external server (big future win for "no setup" users).

Current architecture keeps Ollama as the primary (flexible + mature vision), but is designed to support hybrid on-device clients. See ROADMAP.md.

## 5. Run & Test the App
- Install/run on a **physical Android device** (emulators have very limited accessibility + overlay support).
- Grant the two permissions above.
- Open WhatsApp and receive (or send) a message → the floating emotional panel should appear.
- Use the slider, tones, "Add Vision" (screenshot), Generate, Copy/Speak/Send.
- "Send to WhatsApp" tries direct paste via accessibility (best effort) and always falls back to clipboard with a clear toast.

## Polish & Known Behaviors
- Panel has haptics on every button.
- Vision flow: Add Vision → grant MediaProjection once → screenshot is attached until you clear it or change contact.
- Auto-paste is best-effort (WhatsApp changes input IDs often). You will often see "Copied to clipboard (paste manually)" — this is expected and reliable.
- History + restore works across the floating panel.
- Everything is local (no cloud).

## Troubleshooting After Opening in Android Studio
- Build fails on first try → Clean + Rebuild + Sync.
- No floating panel appears → Double-check Accessibility is enabled for the app.
- Vision not working → Make sure you're using a vision model (llava) and Ollama can see the image (the app sends base64 JPEG).
- Want to help improve? Run on device, use it in real WhatsApp conversations, and report what works / what breaks (auto-paste on your WhatsApp version, vision quality, model suggestions, etc.).

Autonomous development continues. The goal is "test until all working completely". Your Android Studio + device run is the next critical data point.

## 7. Device Testing Feedback
Run on a real phone, try Gemma models (via Ollama or Google AI Edge Gallery), and report results. This drives the next autonomous iterations.

The goal remains "test until everything is reliably working on device".
