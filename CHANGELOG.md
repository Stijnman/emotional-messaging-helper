# Changelog

All notable changes to the Emotional Messaging Helper will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.0] - 2026-05-29

### Added
- Message History viewer with search (Item 21)
  - Auto-saves every AI interaction
  - Search across original messages, emotional insights, and vision descriptions
  - Tap any history entry to restore it into the main suggestion panel
- Real vision model support using Ollama + Llava (screenshot context analysis)
- Encrypted Relationship Memory Vault using EncryptedSharedPreferences (Item 27)
- Model selection foundation (Item 15) — easy switching between local models
- Full Room database for persistent history storage
- Settings screen foundation with model and preference controls
- Haptic feedback and improved loading states
- "Copy + Send" combined action
- Demo Reel export for social sharing
- Quick Template Gallery with popular emotional styles

### Changed
- Major UI/UX polish across the floating panel
- Improved error handling when Ollama is unreachable
- Auto-save to history after every successful AI response
- Relationship Memory now uses encrypted storage by default
- Better navigation inside the overlay (main panel ↔ history)

### Technical
- Added Room + KSP for structured data persistence
- Added androidx.security:security-crypto for encrypted preferences
- Improved CI/CD workflow with optimized Gradle caching
- Better code organization (history package, clear separation of concerns)

### Notes
- First public release candidate
- Requires local Ollama instance to function
- Auto-paste into WhatsApp is best-effort (clipboard fallback available)

[0.1.0]: https://github.com/Stijnman/emotional-messaging-helper/releases/tag/v0.1.0
