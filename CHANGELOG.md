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
- Updated .gitignore handling and build hygiene
- Continue "do not stop until all working completely" + prepare pristine state for Android Studio open + real device runs

Autonomous development continues without pause.

Will keep iterating, testing, and fixing every file until the app is fully working and stable.

## [0.1.0] - Earlier Planning

Initial ambitious vision and documentation created. Many features described were aspirational at the time.

[Unreleased]: https://github.com/Stijnman/emotional-messaging-helper
