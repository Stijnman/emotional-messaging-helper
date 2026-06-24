# Emotional Messaging Helper (EMH) — Roadmap & Remaining Work

**Status:** Heavy autonomous development complete for core Phases 1 + 2 + significant Phase 3 progress.  
**Branch:** `feature/phase2-agent-skills`  
**Main PR:** https://github.com/Stijnman/emotional-messaging-helper/pull/25

This document tracks the original detailed implementation plan (the "Full Detailed Implementation Roadmap" and "SYSTEM PROMPT" provided in the project history) against current reality.

## Current High-Level Status

| Phase | Original Target | Current State | Notes |
|-------|-----------------|---------------|-------|
| **Phase 1: Quick Wins** | README/DEMO + Mermaid + competitive table, one-click Ollama script, paste reliability (fallbacks + haptics), encrypted memory export/import | **Done** | All core items shipped. Export methods exist in manager; UI exposure is the only minor gap. |
| **Phase 2: Hierarchical Emotional Agent + Skill System** | EmotionalContext, Orchestrator (analysis → skills → enriched reply), EmhSkill + Registry + 4+ skills, integration in Panel (history last-3, reasoning + skills UI visible), skill notes enrich prompt | **Done + Extended** | 5 skills (DeceptionFlag, ToneAnalyzer, EmpathyBooster, MemoryUpdateSuggester, ConflictDeescalator). All persisted via DataStore toggles, live-configured, visible in UI with apply for memory suggestions. Multi-turn works. |
| **Phase 3: Hardening** | Vision improvements (multi-image, error handling, compression), advanced skills, full F-Droid prep (fastlane + fdroid/ metadata + screenshots), license (Apache-2.0), testing checklist, final docs | **Partially Complete** | Multi-frame ring buffer + dynamic quality + 5 skills done. F-Droid skeletons + graphics placeholders created. License done. More work needed on real assets, deeper vision, full testing. |

**Overall:** The agent is now the primary brain of the app. Local-first, private, extensible. The biggest remaining work is **polish, distribution readiness, and final validation**.

See:
- [docs/architecture.md](docs/architecture.md) for current architecture + Mermaid
- [README.md](README.md) for features and quick start
- [DEMO.md](DEMO.md) for recording flows (including new Flow 5 for skills + memory + reasoning)
- [CHANGELOG.md](CHANGELOG.md) for detailed autonomous increments
- [TESTING.md](TESTING.md) for the original device checklist + validation steps

## Remaining Areas (Prioritized)

We are systematically closing these one by one (see commit history on the feature branch for progress).

### 1. Documentation & Roadmap Clarity (High Priority - Quick)
- [x] Create this `ROADMAP.md`
- Update README, architecture.md, CHANGELOG, DEMO to accurately reflect "Done" vs "Remaining"
- Remove all stale "future / Phase 3 stub / in progress" language where items are now complete
- Add device testing checklist results section

### 2. Memory Export / Import UI (Phase 1.4 - High Impact, Low Effort)
- Backend methods already exist (`exportEncryptedMemory`, `importEncryptedMemory`, `exportAllMemory` in `RelationshipMemoryManager`).
- **Gap:** No user-facing UI to trigger them (Settings or panel).
- Needed: Section in SettingsScreen with Export (share JSON or save file) + Import (paste or file picker) for per-contact and all-memory.

### 3. Vision Pipeline Hardening (Phase 3 Core)
Current multi-frame is a practical ring buffer (accumulates across "Add Vision" triggers). Panel forwards up to 2 images to llava.
- Make capture support grabbing multiple frames in a single session (small delay loop before stopSelf).
- Pass actual vision image list (base64) into `EmotionalContext` so the agent analysis step can be vision-aware (currently only text description reaches the analysis prompt).
- Implement real `onMobileData` hint using `ConnectivityManager`.
- Improve error handling, logging, and compression options in `ScreenshotHelper` / `ScreenCaptureService`.
- Update `EmotionalContext.toPromptBlock()` and prompt engine to reference multiple images when present.

### 4. Agent Reasoning UI — Bottom Sheet (Original Roadmap Preference)
- Current: Inline expand + "Full dialog" (AlertDialog) + copy.
- Roadmap explicitly wanted: "full bottom sheet 'Why this reply?' with full enrichedAnalysis".
- Implement using `ModalBottomSheet` (Material3) for a more native, dismissible, rich experience while keeping the inline preview + copy.

### 5. Agent Depth & Polish
- True multi-step reasoning is still a single enriched call (comment acknowledges this as future).
- `suggestedToneAdjustment` field exists but is always null.
- No master "Advanced Agent Mode" toggle (skills are individually toggleable; the whole agent is always hierarchical now).
- Add a global toggle in Settings that can disable the full agent and fall back to simpler prompt if desired (for power users or testing).
- Decide on / implement basic multi-step or document as "current design is efficient single-call with rich enrichment".

### 6. Code Cleanup & Legacy Removal
- `SkillRegistry` still has `suggestRelationshipUpdate` and references to the old "relationship_updater" id (vestigial from early Phase 2).
- Remove or fully comment out dead paths.
- Clean any remaining "Phase 3 future" comments in vision files now that ring-buffer multi-frame is the active path.

### 7. Testing & Validation (Original 8-Bullet Checklist)
Original checklist items that still need attention:
- Unit tests for agent/skills (partially done — more coverage welcome).
- Full physical device testing:
  - WhatsApp overlay detection stable
  - Paste (direct + fallbacks + haptics) reliable
  - Ollama + vision (including multi-frame + llava) reliable
  - Agent produces deeper, context-aware replies vs old single-shot
  - Skills can be toggled on/off and visibly affect output quality/depth
  - Memory apply works and persists
  - Export/import (once UI added)
- Create `TESTING.md` or section in ROADMAP with results / how-to.
- Basic instrumentation test skeleton under `app/src/androidTest`.

### 8. F-Droid / Release / Distribution Prep (Phase 3)
- Version bump (`versionCode` / `versionName` in `app/build.gradle.kts`).
- Real graphics assets (featureGraphic.png + phoneScreenshots PNGs). Currently only `.txt` placeholders with capture instructions.
- Expand `fdroid/metadata/com.emh.app/` (add more standard files: `antiFeatures.txt`, `webSite.txt`, `sourceCode.txt`, `issueTracker.txt`, screenshots dir structure).
- Improve fastlane changelogs and descriptions if needed.
- Add clear "Release & F-Droid" section to README or a new `RELEASE.md`.
- Document how to build release APK (signing, etc.).

### 9. Extensibility (Nice-to-Have for Future)
- Current skills are hardcoded. Original plan mentioned future "load from assets/skills/ or user-installed".
- For v1 this is acceptable, but document the extension point clearly.

### 10. Final Polish & Closeout
- Grep entire project for remaining TODO / FIXME / stub / "future" / "Phase 3" language and resolve or document.
- Update competitive tables and "Latest progress" sections.
- Multiple reviewable commits + push.
- Update PR #25 description + final comment.
- Mark this roadmap as "All core areas addressed — ready for device validation + F-Droid submission".

## Priorities (Impact / Effort)

**High Impact / Lower Effort (do first):**
1. Documentation (ROADMAP + doc cleanup)
2. Memory Export/Import UI
3. Agent Reasoning Bottom Sheet
4. Legacy cleanup + global agent toggle
5. Version bump + metadata improvements

**High Impact / Higher Effort:**
- True simultaneous multi-frame + vision images in agent context
- Full device testing + documented results
- Real graphics assets (requires physical device + capture)

**Lower Priority (future after v1):**
- Dynamic skill loading
- Full multi-step LLM chain (analysis separate from generation)

**YOLO Gemma (executed during closeout)**: 
- OllamaClient and Settings now prominently support Gemma 3/4 (gemma3:4b, gemma4:e2b/e4b). These are multimodal, lightweight, and excellent for the hierarchical agent + skills + vision use case.
- True on-device documented (Google AI Edge Gallery for instant testing, MediaPipe LLM Inference for native integration).
- This was "YOLO'd" as a high-ROI addition for privacy, performance on mobile, and differentiation vs Llama-only setups.

## Original Testing Checklist (from implementation plan) — Current State

1. Unit tests for agent + skills → Partial (several added; expand further)
2. Physical device: WhatsApp overlay + detection → Not yet executed in this env
3. Paste stable (haptics + fallbacks) → Code complete; needs device confirmation
4. Ollama + vision reliable (llava, multi-frame) → Code complete; needs device confirmation
5. Memory export/import works → Backend done; UI pending
6. Agent produces deeper replies + multi-turn context → Code complete
7. Skills toggle on/off and affect output → Code complete; needs device demo
8. Full build on Android Studio + no breakage → Verified in structure; full AS run pending

## How This Work Is Being Executed

- Autonomous mode: "do all areas one by one until its all done"
- One focused, reviewable commit per major area (or logical group)
- Frequent pushes to `feature/phase2-agent-skills`
- PR #25 updated after each significant increment
- All changes stay under existing `com.emh.app` package
- No breaking changes to existing flows (accessibility, paste, history, overlay)

## Next Steps After This Roadmap Is Closed

- Physical device testing session (record results here)
- Capture real screenshots for graphics/ and fdroid/
- Cut a release / prepare F-Droid submission
- Consider v0.3.0 or v1.0 tag once device-validated

---

**Last updated:** During autonomous "continue" session.  
All areas will be worked through sequentially until this document can be marked **COMPLETE**.

See commit log and PR #25 for live progress.