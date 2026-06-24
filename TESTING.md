# EMH Testing & Device Validation

**Project status:** Code complete at v0.3.1. `./gradlew test assembleDebug` passes.

## Quick Test (ADB)

```bash
export JAVA_HOME=/opt/android-studio/jbr
export ANDROID_HOME=~/Android/Sdk
./scripts/test-android.sh
```

Auto-detects connected phone or emulator, installs APK, grants mic permission, launches EMH.

**Ollama URLs:**
| Target | URL in Settings |
|--------|-----------------|
| Physical phone | `http://<pc-lan-ip>:11434` |
| Android emulator | `http://10.0.2.2:11434` |
| Waydroid | `http://192.168.240.1:11434` |

## Unit Tests

```bash
./gradlew test
```

Covers: skills, agent orchestrator, prompt engine, Ollama client, history manager.

## Instrumentation Tests

```bash
./gradlew connectedAndroidTest   # requires device/emulator
```

Skeletons: `EmotionalPanelTest`, `RelationshipMemoryManagerTest`.

## Device Checklist

| # | Test | Code | Device validation |
|---|------|------|-------------------|
| 1 | Unit tests | ✅ Pass | `./gradlew test` |
| 2 | WhatsApp overlay detection | ✅ | Confirm on physical device |
| 3 | Paste (accessibility + clipboard fallback) | ✅ | Confirm in WhatsApp |
| 4 | Ollama + vision (Gemma/llava, multi-frame) | ✅ | Check Ollama in Settings |
| 5 | Memory export/import (incl. JSON array) | ✅ | Export All → paste → Import |
| 6 | Agent depth + multi-turn history | ✅ | Compare with agent off/on |
| 7 | Skills toggle affects output | ✅ | Toggle in Settings, regenerate |
| 8 | Voice TTS + speech input | ✅ | Grant mic, test 🎤 and Speak |
| 9 | Full debug build | ✅ | `./gradlew assembleDebug` |

## Validated Environments (2026-06-24)

- **Samsung Galaxy A54** (Android 16, wireless ADB): APK install + launch OK
- **Android SDK Emulator** (API 34, headless): boot + install + launch OK
- **Waydroid** (Lineage 20): install + launch OK (overlay/WhatsApp limited in container)
- **Ollama bridge**: socat `LAN-IP:11434 → 127.0.0.1:11434` for physical devices

## Manual Flow (5 min)

1. Run `./scripts/setup-ollama.sh` on PC; start `ollama serve`
2. Install EMH; enable Accessibility + Overlay permissions
3. Settings → Ollama URL → Save → Check Ollama
4. Open WhatsApp chat → floating panel appears
5. Generate reply → verify agent reasoning card
6. Test voice: Speak / 🎤 buttons
7. Settings → Export All Memory → paste back → Import

## F-Droid Graphics

Capture real screenshots on device (see `fastlane/metadata/android/en-US/graphics/README.txt`) before store submission.

See [RELEASE.md](RELEASE.md) for release build steps.