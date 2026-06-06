# Emotional Messaging Helper (EMH)

Android floating overlay app for WhatsApp that provides psychologically intelligent, emotionally fulfilling reply suggestions using **local AI** (Ollama).

**Local-first • Private • No cloud • Deep emotional intelligence**

## Features

| Feature                      | Description |
|------------------------------|-------------|
| Real-time WhatsApp Detection | Accessibility Service monitors chats live |
| Local AI (Ollama)            | Text + Vision (llava) – everything stays on your device |
| Hierarchical Emotional Agent | Multi-turn psychological depth + relationship memory (Phase 2) |
| Screenshot Vision Context    | Capture screen + analyze with vision models |
| Tone & Figurative Control    | 0-10 figurative slider + quick tone presets |
| Encrypted Relationship Memory| Per-contact notes & preferences (vault) |
| Extensible Skill System      | 5 lightweight skills (tone, deception, empathy, memory updates, conflict de-escalation) — all toggleable (Phase 2/3) |
| History + Restore            | Save generations, search, one-tap restore into panel |
| Smart Auto-Paste             | Direct accessibility paste + reliable clipboard fallback + haptics/toasts |
| Floating Overlay Panel       | Rich Compose UI with haptics, templates, vision UI |

## Architecture

```mermaid
graph TD
    A[WhatsAppAccessibilityService] -->|detect message| B[FloatingOverlayService]
    B --> C[EmotionalPanel Compose UI]
    C --> D[EmotionalAgentOrchestrator]
    D --> E[RelationshipMemoryVault]
    D --> F[EmotionalPromptEngine]
    D -->|optional| G[SkillRegistry]
    F --> H[OllamaClient]
    H --> I[Ollama Local LLM text + llava vision]
    C --> J[AutoPasteHelper]
    J --> K[Clipboard + Accessibility paste]
```

See `docs/architecture.md` for full details (includes Mermaid of the agent + skills loop + multi-frame vision).

**Latest autonomous progress**: 5 skills with live Settings toggles, multi-frame vision (up to 2 images to llava), one-tap memory suggestion application, expandable + copyable agent reasoning, new orchestrator tests, fastlane graphics + fdroid metadata skeletons. All on `feature/phase2-agent-skills`.

## Quick Start (One-Click Ollama)

```bash
cd emotional-messaging-helper
./scripts/setup-ollama.sh
```

This pulls the recommended models:
- `llama3.2:3b` (or llama3.1)
- `llava:7b` (vision)

Then open the project in **Android Studio**, sync, and run on a physical device.

**Full instructions:** See [SETUP.md](SETUP.md)

## Competitive Differentiation

| App                  | Local AI | Vision | Emotional Depth | Skills/Extensibility | Relationship Memory | Open Source |
|----------------------|----------|--------|-----------------|----------------------|---------------------|-------------|
| EMH (this)           | ✅ Yes  | ✅ llava (multi-frame) | ✅ Hierarchical Agent + 5 Skills (configurable) | ✅ Live + persisted toggles | ✅ Encrypted Vault + apply UX | ✅ Apache-2.0     |
| Replyfy / AutoResponder | ❌     | ❌     | 🟡 Basic templates | ❌                  | ❌                 | ❌         |
| Generic Ollama apps  | ✅      | 🟡 Limited | 🟡 Single-shot | ❌                  | 🟡 Basic           | Varies     |

EMH's moat is **psychological depth + local privacy + extensibility via skills** while staying 100% on-device.

## Phase Roadmap (High Level)

**Phase 1 (Done):** Professional docs, one-click Ollama script, paste reliability (multi-fallback + haptics), encrypted memory export methods.

**Phase 2 (Done — Core Moat):** Hierarchical Emotional Agent Orchestrator + Skill System.
- Agent now drives all reply generation.
- 5 skills active and individually toggleable in Settings (with live DataStore persistence): deception_flag, tone_analyzer, empathy_booster, memory_update, conflict_deescalator.
- Recent history (last 3 turns) for multi-turn awareness.
- Full agent reasoning visible (expandable + copy + dialog) and skill notes enrich the prompt.
- Memory suggestions from skills are one-tap applyable.

**Phase 3 (In Progress):** Vision hardening (multi-frame, images in agent context), full F-Droid + release prep, memory export UI, bottom-sheet reasoning, testing & device validation.

**Full remaining work, priorities, and original checklist status:** See [ROADMAP.md](ROADMAP.md).

This document is being closed out systematically ("do all areas one by one until it's all done").

## License

Apache-2.0 (see LICENSE). Recommended for open community distribution and F-Droid compatibility. Personal, educational, and commercial use permitted with attribution.

See [ROADMAP.md](ROADMAP.md) for full remaining work, phase status vs original plan, and checklist.  
See [docs/architecture.md](docs/architecture.md) for detailed architecture + Mermaid.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) and [SETUP.md](SETUP.md).

---

**Built autonomously with heavy iteration loops for reliability on real devices.**

## Building for Release / F-Droid
- Bump version in `app/build.gradle.kts`.
- For graphics: capture real screenshots on device (see `fastlane/metadata/android/en-US/graphics/README.txt` and `TESTING.md`).
- Build signed release APK in Android Studio (Build > Generate Signed Bundle/APK).
- F-Droid metadata skeleton lives in `fdroid/metadata/com.emh.app/`.
- See [ROADMAP.md](ROADMAP.md) and [TESTING.md](TESTING.md) for the full closeout status and device validation steps.
