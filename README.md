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

**Phase 1 (Done):** Professional docs, one-click Ollama, paste reliability, memory export.

**Phase 2 (In Progress / Core Moat):** Hierarchical Emotional Agent Orchestrator + lightweight Skill System (multi-turn, context-aware, skill invocation). 
- Agent now drives reply generation in the panel.
- 4 skills active: deception_flag, tone_analyzer, empathy_booster, memory_update.
- Recent history context for multi-turn.
- UI shows agent reasoning + invoked skills.

**Phase 3 (Started):** Vision pipeline improvements (dynamic quality, multi-frame prep), F-Droid metadata structure.

See the full autonomous implementation plan in the project issues / previous roadmap notes.

## License

Apache-2.0 (see LICENSE). Recommended for open community distribution and F-Droid compatibility. Personal, educational, and commercial use permitted with attribution.

See [docs/architecture.md](docs/architecture.md) for detailed architecture.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) and [SETUP.md](SETUP.md).

---

**Built autonomously with heavy iteration loops for reliability on real devices.**
