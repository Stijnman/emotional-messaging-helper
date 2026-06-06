# EMH Testing & Device Validation

This file tracks the original testing checklist from the implementation plan and current status after systematic closeout.

## Unit Tests
- Skills (DeceptionFlag, MemoryUpdateSuggester, SkillRegistry, ConflictDeescalator)
- EmotionalAgentOrchestrator (mocks for memory, prompt, registry)
- Prompt engine, history, ollama client (pre-existing)

Run with: `./gradlew test` (in Android Studio after sync).

## Original Device Checklist (from roadmap) — Status

1. **Unit tests for agent + skills** — Done (multiple added during closeout)
2. **Physical device: WhatsApp overlay detection stable** — Code complete. Pending real-device run.
3. **Paste stable** (direct Accessibility ACTION_PASTE + clipboard fallback + Toast + vibration on all paths) — Code complete + manual retry buttons. Pending confirmation.
4. **Ollama + vision reliable** (llama3.2 + llava, dynamic quality, multi-frame 2+ frames sent) — Code complete (ring buffer + in-session multi acquire + images passed to context). Pending device + llava test.
5. **Memory export/import** — Backend + full UI in Settings (export all to clipboard, import by paste). Pending device test.
6. **Agent produces deeper, context-aware replies** (multi-turn history last-3 + memory + 5 skills) — Core complete. Reasoning UI (bottom sheet + expand + copy) makes the "why" transparent.
7. **Skills can be enabled/disabled and visibly affect output** — Full persisted toggles + live configure before generate. Global "Use Hierarchical Agent" toggle also added.
8. **Full build + no breakage on Android Studio** — Structure verified. Full AS + device build is the final gate.

## How to Validate on Device
1. Open project in Android Studio 2024.3+.
2. Run `./scripts/setup-ollama.sh` on a machine with Ollama (or point Settings to your Ollama IP).
3. Install on physical Android device (emulators have poor accessibility + MediaProjection support).
4. Enable the app as Accessibility Service + grant screen capture when prompted.
5. Open WhatsApp, receive messages, exercise all flows from DEMO.md + ROADMAP.md.
6. Toggle skills on/off and compare reply depth/strategy.
7. Use vision (Add Vision), multi-frame should send 2 images.
8. Test memory apply and export/import in Settings.
9. Record results here or in PR.

After successful device validation, this project will be ready for F-Droid / release tagging.

See ROADMAP.md for the full list of areas closed during the "do all one by one" pass.
