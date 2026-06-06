package com.emh.app.skills

import com.emh.app.agent.EmotionalContext
import com.emh.app.memory.RelationshipMemoryManager

/**
 * Central registry for all available skills.
 * In v1 skills are simple and can be toggled.
 *
 * Future: load from assets/skills/ or allow user-installed prompt skills.
 */
class SkillRegistry {

    private val enabledSkills = mutableSetOf("deception_flag", "tone_analyzer", "empathy_booster", "memory_update")

    // Concrete skill objects (v1 - loaded from separate files)
    private val deceptionSkill = DeceptionFlagSkill
    private val toneSkill = ToneAnalyzerSkill
    private val empathySkill = EmpathyBoosterSkill
    private val memoryUpdateSkill = MemoryUpdateSuggester
    private val conflictSkill = ConflictDeescalatorSkill  // 5th skill (Phase 3 hardening)

    /** Configure enables from persisted settings (called before agent runs). */
    fun configureEnabled(enables: Map<String, Boolean>) {
        enables.forEach { (id, on) ->
            if (on) enabledSkills.add(id) else enabledSkills.remove(id)
        }
    }

    fun isEnabled(skillId: String): Boolean = enabledSkills.contains(skillId)

    fun enable(skillId: String) = enabledSkills.add(skillId)
    fun disable(skillId: String) = enabledSkills.remove(skillId)

    /**
     * Run deception check via the concrete skill.
     */
    fun runDeceptionCheck(message: String, relationshipMemory: String): String {
        if (!isEnabled("deception_flag")) return ""
        return deceptionSkill.run(
            EmotionalContext(
                contactKey = "",
                incomingMessage = message,
                relationshipMemory = relationshipMemory
            )
        )
    }

    /**
     * Suggest relationship update (heuristic for now).
     */
    fun suggestRelationshipUpdate(
        contactKey: String,
        incomingMessage: String,
        memoryManager: RelationshipMemoryManager
    ) {
        if (!isEnabled("relationship_updater")) return
        val lower = incomingMessage.lowercase()
        if (lower.contains("thank you") || lower.contains("appreciate")) {
            // Surface suggestion in future UI
        }
    }

    fun runToneAnalyzer(context: EmotionalContext): String {
        if (!isEnabled("tone_analyzer")) return ""
        return toneSkill.run(context)
    }

    /**
     * Run all enabled skills and return combined notes.
     * This is what the orchestrator should call for clean integration.
     * Call configureEnabled(...) first (from SettingsRepository snapshot) for persistence.
     */
    fun runEnabledSkills(context: EmotionalContext): List<String> {
        val results = mutableListOf<String>()
        if (isEnabled("deception_flag")) {
            val res = runDeceptionCheck(context.incomingMessage, context.relationshipMemory)
            if (res.isNotBlank()) results.add(res)
        }
        if (isEnabled("tone_analyzer")) {
            val res = runToneAnalyzer(context)
            if (res.isNotBlank()) results.add(res)
        }
        if (isEnabled("empathy_booster")) {
            val res = empathySkill.run(context)
            if (res.isNotBlank()) results.add(res)
        }
        if (isEnabled("memory_update")) {
            val res = memoryUpdateSkill.run(context)
            if (res.isNotBlank()) results.add(res)
        }
        if (isEnabled("conflict_deescalator")) {
            val res = conflictSkill.run(context)
            if (res.isNotBlank()) results.add(res)
        }
        return results
    }
}
