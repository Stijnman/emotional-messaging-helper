# Architecture

> **See [ROADMAP.md](../ROADMAP.md) for the complete remaining work list, phase-by-phase status against the original plan, and what is being closed out systematically.**

## Overview

Emotional Messaging Helper (EMH) is an Android app that provides AI-powered, emotionally intelligent reply suggestions for WhatsApp using local Ollama models (text + vision).

It uses AccessibilityService for real-time message detection, MediaProjection for screenshots, and a floating Compose UI for interaction.

## Key Components

- **Services**:
  - WhatsAppAccessibilityService: Detects incoming messages in WhatsApp.
  - FloatingOverlayService: Manages the floating bubble and panel.
  - ScreenCaptureService: Handles MediaProjection for screenshots.

- **UI (Compose)**:
  - EmotionalPanel: Main interaction UI with sliders, tones, vision, history, generate.
  - SettingsScreen, HistoryScreen, TemplateGallery.

- **AI Layer**:
  - OllamaClient: Communicates with local Ollama (text + images).
  - EmotionalPromptEngine: Builds rich prompts with memory, tone, figurative level, vision.
  - EmotionalAgentOrchestrator (Phase 2): Hierarchical agent for analysis, planning, skill invocation.
  - Skills: Pluggable modules (currently 5: deception, tone, empathy, memory suggester, conflict de-escalator) with per-skill persisted toggles.

- **Memory**:
  - RelationshipMemoryManager: Encrypted per-contact notes and preferences using EncryptedSharedPreferences.
  - Supports export/import.

- **History**:
  - Room database for past generations, with restore to panel.

## Data Flow

1. Accessibility detects new incoming WhatsApp message → triggers panel with contact + message.
2. User interacts (tone, figurative, vision capture).
3. On Generate: Panel → AgentOrchestrator (loads memory + history + skills) → PromptEngine → Ollama (text or vision).
4. Response parsed → displayed in panel.
5. User can copy, speak, paste (via AutoPasteHelper + accessibility), or save to history.

## Phase 2 Enhancements

- Hierarchical agent loop: Analysis → Strategy → Skills → Final Reply.
- Skills are lightweight, return insights that enrich the prompt.
- Multi-turn via recent history in context.
- Skill enable/disable persisted in DataStore and live-configured into SkillRegistry before each run.
- MemoryUpdateSuggester output is now consumable (UI apply → appendNote on the encrypted vault).

## Recent Increments (keep going)

- Multi-frame vision: ScreenCaptureService keeps a small ring buffer of recent JPEG base64 captures. EmotionalPanel forwards up to 2 images to the vision model call when present (falls back gracefully).
- Agent transparency: reasoning card is interactive and opens a full "Why this reply?" dialog containing the complete enriched analysis injected into the final prompt.
- Settings now exposes per-skill switches with descriptions.

## Vision

- Screenshots captured via MediaProjection.
- Converted to base64 JPEG with dynamic quality.
- Passed to llava models for context.

## Extensibility

- Skills in `skills/` package implementing EmhSkill (currently 5, all toggleable via persisted Settings).
- Agent can invoke enabled skills. Skill notes enrich the hierarchical analysis.
- See [ROADMAP.md](../ROADMAP.md) for current status on advanced skills, deeper multi-image support in analysis, and F-Droid distribution.

See README for features and quick start.

## Agent + Skills Flow (Mermaid)

```mermaid
flowchart TD
    A[WhatsApp message detected] --> B[Floating EmotionalPanel]
    B --> C{User: tone/figurative + optional Vision}
    C --> D[On Generate]
    D --> E[Load recentHistory last-3 + Memory]
    E --> F[EmotionalContext built]
    F --> G[SkillRegistry.configureEnabled from DataStore]
    G --> H[EmotionalAgentOrchestrator]
    H --> I[buildAgentAnalysisPrompt]
    I --> J[PromptEngine + Ollama analysis]
    J --> K[runEnabledSkills (enabled skills in parallel)]
    K --> L[Enrich analysis with skill notes]
    L --> M[generateEmotionalReply with enriched]
    M --> N[If vision: ollama.generateWithImages up to 2 recent frames]
    N --> O[parse + show reply + insight]
    O --> P[🧠 Agent card clickable → full reasoning dialog]
    O --> Q[💾 Memory suggestion? → one-tap appendNote]
    K -.->|deception_flag| R[DeceptionFlagSkill]
    K -.->|tone_analyzer| S[ToneAnalyzerSkill]
    K -.->|empathy_booster| T[EmpathyBoosterSkill]
    K -.->|memory_update| U[MemoryUpdateSuggester]
    K -.->|conflict_deescalator| V[ConflictDeescalatorSkill]
```

Skills (5 total) return short insight strings that are appended to reasoning and re-injected. All processing stays on-device. Toggles in Settings (DataStore) control which K branches execute and are live-configured via SkillRegistry before every agent run. Multi-frame vision images (up to recent 2) are forwarded directly to the final vision LLM call.

