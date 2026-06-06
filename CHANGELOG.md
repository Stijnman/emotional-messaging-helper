# Changelog

## [Unreleased / In Development] - Heavy Autonomous Development

### Testing & Hardening Phase - Continuing Until Finished (Autonomous)
- Fixed critical syntax error in EmotionalPromptEngine (parseResponse now correctly inside class)
- Strengthened vision prompt instructions (explicit "you can see the image", more visual references)
- Enhanced OllamaClient: improved healthCheck + new listModels() for future settings + better docs
- Expanded unit tests: more parse cases, figurative, vision prompt, model detection, history export/count
- CI now triggers on feature/** branches (keeps autonomous work validated)
- Touched + hardened ALL source files (prompt, client, panel, services, capture, memory, helpers, tests, PanelState, app init)
- Micro improvements: clearer toasts, vision auto-clear, relationship clearNote, AutoPasteHelper polish, more IDs/comments in accessibility
- Updated .gitignore handling and build hygiene (full .idea/ ignore)
- README/SETUP/gradlew dramatically improved with prominent "NEXT STEP: Open in Android Studio" + troubleshooting
- Fixed lingering merge conflict markers in README (would have been visible on GitHub/AS)
- Cleaned legacy placeholder code in ScreenshotHelper (now clearly points to real MediaProjection path)
- Expanded test placeholders + comments
- Continue "do not stop until all working completely" + prepare pristine state for Android Studio open + real device runs. This pass was driven by "whats the next step" – focus on user being able to open/build immediately.

## Finishing Pass ("finish it up")
- Advanced auto-paste (#2/#6): massively expanded input/send ID lists in WhatsAppAccessibilityService, added ACTION_PASTE fallback, more focus attempts, extra "Try Direct Paste Again" button in panel.
- Added explicit "Paste Reply" + retry manual trigger buttons in EmotionalPanel (directly addresses GitHub issues requesting manual paste).
- Wired live "Fetch models from Ollama" + tap-to-select chips in SettingsScreen using the listModels() API.
- Added more unit test coverage (OllamaClient url handling, listModels/ health structural, etc.).
- All open testing/auto-paste issues advanced in code; #18 tracks the final device validation.
- Repo cleaned, docs ultra-clear, no markers, ready for final AS build + real WhatsApp + Ollama testing.
- "Finish it up" autonomous mode: touched core files, tests, UI, integration, GitHub issues. No more placeholders in critical paths.

The autonomous agent will continue iterating and fixing until the project is as complete and stable as possible.

## Phase 1 & Phase 2 Foundation (per detailed roadmap)
- Overhauled README.md with feature table, Mermaid architecture diagram, competitive positioning, one-click Ollama setup instructions, and phased roadmap summary.
- Created DEMO.md with 4 key user flows for recording high-quality demos.
- Added scripts/setup-ollama.sh (one-click pull of llama3.2:3b + llava:7b).
- Enhanced AutoPasteHelper: multi-fallback strategy, explicit Toast + haptic feedback on all paths, better diagnostics.
- Enhanced RelationshipMemoryManager: added exportEncryptedMemory, importEncryptedMemory, exportAllMemory (encrypted JSON via existing keystore layer).
- Implemented core of Hierarchical Emotional Agent (Phase 2):
  - com.emh.app.agent.EmotionalContext (rich multi-turn + vision + memory context object)
  - com.emh.app.agent.EmotionalAgentOrchestrator (analysis → planning → skill hooks → reply)
  - Extended EmotionalPromptEngine with buildAgentAnalysisPrompt + generateEmotionalReply (injects agent reasoning for depth).
- Lightweight Skill System v1:
  - EmhSkill interface
  - SkillRegistry with deception_flag, relationship_updater, tone_analyzer examples.
- Integrated agent into EmotionalPanel generation flow (analysis now runs before Ollama call; vision + memory preserved).
- All new code placed under existing com.emh.app package structure (agent/, skills/) to match live tree.
- Structure verified; ready for Android Studio build + device testing of deeper emotional replies.

## [0.1.0] - Earlier Planning

Initial ambitious vision and documentation created. Many features described were aspirational at the time.

[Unreleased]: https://github.com/Stijnman/emotional-messaging-helper
