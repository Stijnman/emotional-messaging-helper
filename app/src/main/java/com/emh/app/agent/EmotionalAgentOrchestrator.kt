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
        val suggestedToneAdjustment: String? = null
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
        recentHistory: List<String> = emptyList()
    ): AgentResult {
        val context = EmotionalContext(
            contactKey = contactKey,
            incomingMessage = incomingMessage,
            relationshipMemory = memoryManager.buildContextForAI(contactKey),
            recentHistory = recentHistory,
            desiredFigurativeLevel = desiredFigurativeLevel,
            preferredTone = preferredTone,
            hasVisionContext = hasVision,
            visionDescription = visionDescription
        )

        // Step 1 + 2: Analysis + Planning via the prompt engine (enhanced for agent use)
        val analysisAndPlan = promptEngine.buildAgentAnalysisPrompt(context)

        // For v1 we keep a single strong generation call, but the structure is ready
        // for true multi-step (analysis → plan → final) in future iterations.
        val finalReply = promptEngine.generateEmotionalReply(
            context = context,
            agentAnalysis = analysisAndPlan
        )

        // Step 3: Skill invocation using the registry (clean v1)
        val skillNotes = skillRegistry.runEnabledSkills(context)
        val invoked = skillNotes.mapNotNull { note ->
            when {
                note.contains("deception", ignoreCase = true) || note.contains("pattern", ignoreCase = true) -> "deception_flag"
                note.contains("tone", ignoreCase = true) || note.contains("anger", ignoreCase = true) || note.contains("sad", ignoreCase = true) -> "tone_analyzer"
                else -> null
            }
        }.distinct()

        // Incorporate skill notes into reasoning for the final prompt
        val enrichedAnalysis = if (skillNotes.isNotEmpty()) {
            "$analysisAndPlan\n\nSkill insights: ${skillNotes.joinToString(" | ")}"
        } else analysisAndPlan

        // Re-generate final reply with enriched analysis (skills influence the output)
        val finalReplyWithSkills = promptEngine.generateEmotionalReply(context, enrichedAnalysis)

        // Optional side effect for relationship updater (non-destructive)
        if (skillRegistry.isEnabled("relationship_updater")) {
            skillRegistry.suggestRelationshipUpdate(contactKey, incomingMessage, memoryManager)
            if (!invoked.contains("relationship_updater")) invoked.add("relationship_updater")
        }

        return AgentResult(
            suggestedReply = finalReplyWithSkills,
            reasoning = enrichedAnalysis,
            invokedSkills = invoked,
            suggestedToneAdjustment = null // Future: agent can recommend tone change
        )
    }
}
