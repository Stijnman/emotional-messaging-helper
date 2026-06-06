# Architecture

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
  - Skills: Pluggable modules for deception detection, tone analysis, empathy boost, memory suggestions.

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

## Vision

- Screenshots captured via MediaProjection.
- Converted to base64 JPEG with dynamic quality.
- Passed to llava models for context.

## Extensibility

- Skills in `skills/` package implementing EmhSkill.
- Agent can invoke enabled skills.
- Future: more advanced skills, multi-image, F-Droid distribution.

See README for features and quick start.
