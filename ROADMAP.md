# Emotional Messaging Helper (EMH) — Roadmap

**Status: COMPLETE (v0.3.1)**

All planned phases are implemented. Remaining work is optional polish (real F-Droid screenshots, signed release keystore) and ongoing device validation on your hardware.

## Phase Summary

| Phase | Scope | Status |
|-------|-------|--------|
| **Phase 1** | Docs, Ollama setup script, paste reliability, memory export/import | **Done** |
| **Phase 2** | Hierarchical agent, 5 skills, multi-turn history, reasoning UI | **Done** |
| **Phase 3** | Vision multi-frame, F-Droid/fastlane metadata, testing, release docs | **Done** |
| **Voice** | TTS, speech input, auto-speak, settings toggles | **Done** (v0.3.1) |

## Delivered Features

- WhatsApp accessibility detection + floating overlay panel
- Local Ollama integration (text + vision, Gemma 3/4 recommended)
- Hierarchical emotional agent with 5 toggleable skills
- Encrypted relationship memory vault + export/import UI (JSON + array round-trip)
- Multi-frame vision capture → agent context
- Agent reasoning: inline expand + ModalBottomSheet + copy
- Global hierarchical agent toggle (fallback to simple prompt)
- Voice: read-aloud TTS, microphone input, auto-speak replies
- History save/search/restore
- Smart auto-paste with clipboard fallback + haptics
- Unit tests (agent, skills, prompt, ollama, history)
- Instrumentation test skeletons
- CI workflows (build + test)
- F-Droid + fastlane metadata skeletons
- Device test scripts: `scripts/test-android.sh`, `scripts/test-waydroid.sh`

## Original Checklist — Final Status

| # | Item | Status |
|---|------|--------|
| 1 | Unit tests for agent + skills | **Done** — `./gradlew test` passes |
| 2 | WhatsApp overlay detection | **Code complete** — validate on device |
| 3 | Paste reliability | **Code complete** — validate on device |
| 4 | Ollama + vision | **Code complete** — tested via ADB/emulator |
| 5 | Memory export/import | **Done** — UI + array import fix |
| 6 | Agent depth + multi-turn | **Done** |
| 7 | Skills toggle affects output | **Done** — persisted toggles |
| 8 | Full build | **Done** — `./gradlew assembleDebug` passes |

## Optional Follow-ups (Post-v0.3.1)

- Capture real device screenshots for F-Droid graphics
- Submit to F-Droid (requires maintainer)
- True on-device inference via MediaPipe (documented, not implemented)
- Dynamic skill loading from assets (extension point documented in `SkillRegistry`)
- Multi-step LLM chain (current design: efficient single-call enrichment)

## References

- [README.md](README.md) — features + quick start
- [SETUP.md](SETUP.md) — full install guide
- [TESTING.md](TESTING.md) — validation checklist
- [RELEASE.md](RELEASE.md) — build + tag + F-Droid
- [DEMO.md](DEMO.md) — demo flows
- [docs/architecture.md](docs/architecture.md) — architecture + Mermaid
- [CHANGELOG.md](CHANGELOG.md) — version history

**Last updated:** 2026-06-24 — Project marked complete at v0.3.1.