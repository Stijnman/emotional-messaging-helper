package com.emh.app.agent

import com.emh.app.memory.RelationshipMemoryManager
import com.emh.app.skills.SkillRegistry

/**
 * Hierarchical Emotional Agent Orchestrator (Phase 2 Core)
 *
 * This is the brain of EMH v2.
 * Instead of a single prompt shot, it runs a lightweight reasoning loop:
 *   1. Analyze emotional state + context
 *   2. Plan response strategy (multi-turn aware)
 *   3. (Optionally) invoke skills
 *   4. Generate final reply
 *
 * Designed to be called from the UI layer (EmotionalPanel).
 */
class EmotionalAgentOrchestrator(
    private val memoryManager: RelationshipMemoryManager,
    private val promptEngine: com.emh.app.ai.EmotionalPromptEngine,
    private val skillRegistry: SkillRegistry = SkillRegistry()
) {

    data class AgentResult(
        val suggestedReply: String,
        val reasoning: String = "",
        val invokedSkills: List<String> = emptyList(),
        val suggestedToneAdjustment: String? = null,
        val memorySuggestions: List<String> = emptyList()   // surfaced from MemoryUpdateSuggester for UI apply
    )

    /**
     * Main entry point.
     * Produces a high-quality, context-aware reply suggestion.
     */
    suspend fun generateReply(
        contactKey: String,
        incomingMessage: String,
        desiredFigurativeLevel: Int,
        preferredTone: String,
        hasVision: Boolean = false,
        visionDescription: String = "",
        recentHistory: List<String> = emptyList(),
        visionImages: List<String> = emptyList()   // Phase 3: pass multi-frame images into context
    ): AgentResult {
        val context = EmotionalContext(
            contactKey = contactKey,
            incomingMessage = incomingMessage,
            relationshipMemory = memoryManager.buildContextForAI(contactKey),
            recentHistory = recentHistory,
            desiredFigurativeLevel = desiredFigurativeLevel,
            preferredTone = preferredTone,
            hasVisionContext = hasVision,
            visionDescription = visionDescription,
            visionImages = visionImages
        )

        // Step 1 + 2: Analysis + Planning via the prompt engine (enhanced for agent use)
        val analysisAndPlan = promptEngine.buildAgentAnalysisPrompt(context)

        // Current design (efficient for on-device LLM): single strong generation call with rich pre-analysis + skill enrichment.
        // The structure (separate analysis prompt + final) makes future true multi-step easy to add if needed.
        val finalReply = promptEngine.generateEmotionalReply(
            context = context,
            agentAnalysis = analysisAndPlan
        )

        // Step 3: Skill invocation using the registry (clean v1). Registry must be pre-configured by caller (panel) with persisted enables.
        val skillNotes = skillRegistry.runEnabledSkills(context)

        val invoked = mutableListOf<String>()
        val memSuggestions = mutableListOf<String>()
        skillNotes.forEach { note ->
            when {
                note.contains("deception", ignoreCase = true) || note.contains("pattern", ignoreCase = true) -> invoked.add("deception_flag")
                note.contains("tone", ignoreCase = true) || note.contains("anger", ignoreCase = true) || note.contains("sad", ignoreCase = true) -> invoked.add("tone_analyzer")
                note.contains("vulnerab", ignoreCase = true) || note.contains("empathy", ignoreCase = true) -> invoked.add("empathy_booster")
                note.contains("memory", ignoreCase = true) || note.contains("update", ignoreCase = true) || note.contains("Suggested memory", ignoreCase = true) -> {
                    invoked.add("memory_update")
                    // Extract a clean suggestion string for one-click apply in UI
                    val clean = note.substringAfter("Suggested memory update:", note).trim()
                    if (clean.isNotBlank()) memSuggestions.add(clean)
                }
                note.contains("conflict", ignoreCase = true) || note.contains("escalat", ignoreCase = true) || note.contains("de-escalat", ignoreCase = true) -> invoked.add("conflict_deescalator")
            }
        }

        // Incorporate skill notes into reasoning for the final prompt
        val enrichedAnalysis = if (skillNotes.isNotEmpty()) {
            "$analysisAndPlan\n\nSkill insights: ${skillNotes.joinToString(" | ")}"
        } else analysisAndPlan

        // Re-generate final reply with enriched analysis (skills influence the output)
        val finalReplyWithSkills = promptEngine.generateEmotionalReply(context, enrichedAnalysis)

        return AgentResult(
            suggestedReply = finalReplyWithSkills,
            reasoning = enrichedAnalysis,
            invokedSkills = invoked.distinct(),
            suggestedToneAdjustment = null, // Future: agent can recommend tone change
            memorySuggestions = memSuggestions.distinct()
        )
    }
}
