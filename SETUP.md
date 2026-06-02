# Setup Instructions

## 1. Open in Android Studio
1. Open Android Studio (2024.3+ or newer recommended)
2. Open the `emotional-messaging-helper` folder as a project

## 2. Let Gradle Sync
This project was heavily developed in autonomous mode. The recommended (and easiest) way to build is:

1. Open the `emotional-messaging-helper` folder in **Android Studio**.
2. Android Studio will automatically download the correct Gradle version and wrapper.
3. Sync the project.
4. Run on a physical device.

If you want to generate the wrapper manually (advanced):
```bash
cd emotional-messaging-helper
gradle wrapper --gradle-version 8.9
./gradlew assembleDebug
```

The `gradlew` script has been improved with better guidance for this autonomously-built project. (Continue autonomous fixes applied - engine, tests, CI, vision quality, all files touched).

## 3. Required Permissions (on device)
- **Display over other apps** (Overlay)
- **Accessibility Service** → Enable "Emotional Messaging Helper"

## 4. Ollama (required)
Make sure Ollama is running on your computer and reachable from the Android device (same WiFi recommended).

Recommended models:
- `llama3.2` or `llama3.1` for text
- `llava` or `llama3.2-vision` for screenshot analysis

Test with:
```bash
ollama run llama3.2
```

## 5. Run
- Run the app on a **physical Android device** (emulators have very limited accessibility + overlay support).
- Grant Overlay + Accessibility permissions.
- Make sure Ollama is running with a vision-capable model if you want screenshot analysis (e.g. `llava` or `llama3.2-vision`).
- Open WhatsApp. New messages should trigger the floating emotional assistant.

## Polish Notes
- The panel has haptic feedback, dynamic loading messages ("Analyzing screenshot..." when vision is attached), and a clear "Vision attached" indicator with a one-tap Clear button.
- Vision works best when you tap "Add Vision" right before generating on an important message.
- Auto-paste tries to type directly into WhatsApp first, then falls back to clipboard.
