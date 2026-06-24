# EMH Release Guide

## Current Version

- **versionName:** 0.3.1
- **versionCode:** 5

## Build Debug APK

```bash
export JAVA_HOME=/opt/android-studio/jbr
export ANDROID_HOME=~/Android/Sdk
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

## Build Release APK

1. Open project in Android Studio.
2. **Build → Generate Signed Bundle / APK → APK**.
3. Use your release keystore (or debug keystore for local testing only).
4. Release build uses ProGuard (`isMinifyEnabled = true`).

## Device Testing

```bash
# Physical device or emulator via ADB
./scripts/test-android.sh

# Waydroid (Linux container)
./scripts/test-waydroid.sh
```

See [TESTING.md](TESTING.md) for the full checklist.

## Ollama Setup

```bash
./scripts/setup-ollama.sh
```

**URLs by environment:**
| Environment | Ollama URL in EMH Settings |
|-------------|---------------------------|
| Physical phone (same Wi‑Fi) | `http://<your-pc-ip>:11434` |
| Android emulator | `http://10.0.2.2:11434` |
| Waydroid | `http://192.168.240.1:11434` |

If Ollama only listens on localhost, bridge it:

```bash
socat TCP-LISTEN:11434,bind=<your-pc-ip>,fork,reuseaddr TCP:127.0.0.1:11434
```

## F-Droid Metadata

- `fdroid/metadata/com.emh.app/` — summary, description, license, links
- `fastlane/metadata/android/en-US/` — store listing, changelogs, graphics

Replace placeholder `.txt` screenshot instructions with real PNG captures from a device before submission.

## Tagging a Release

```bash
git tag -a v0.3.1 -m "EMH 0.3.1 — voice, complete project"
git push origin v0.3.1
```

GitHub Actions (`.github/workflows/release.yml`) can build on tag push.

## Permissions Required

- **Overlay** — floating panel
- **Accessibility** — WhatsApp detection + paste
- **Microphone** — voice input (optional)
- **Screen capture** — vision context (optional)