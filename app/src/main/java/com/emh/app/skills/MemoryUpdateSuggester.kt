package com.emh.app.skills

import com.emh.app.agent.EmotionalContext

/**
 * Skill that analyzes the exchange and suggests a memory update for the contact.
 * Non-destructive: returns a suggested note string that the user/UI can confirm before saving.
 */
object MemoryUpdateSuggester : EmhSkill {
    override val id = "memory_update"
    override val description = "Suggests updates to relationship memory based on the conversation (e.g. new preferences, events)."

    override fun run(context: EmotionalContext, extraData: Map<String, Any>): String {
        val lower = context.incomingMessage.lowercase()
        val suggestions = mutableListOf<String>()

        if (lower.contains("i prefer") || lower.contains("i like when") || lower.contains("please don't")) {
            suggestions.add("User expressed a preference or boundary.")
        }
        if (lower.contains("my") && (lower.contains("job") || lower.contains("family") || lower.contains("birthday"))) {
            suggestions.add("Mention of personal life event or detail.")
        }
        if (lower.contains("thank") || lower.contains("appreciate")) {
            suggestions.add("User values gratitude or specific responses.")
        }

        return if (suggestions.isNotEmpty()) {
            "Suggested memory update: ${suggestions.joinToString(" ")} Consider adding to notes for ${context.contactKey}."
        } else ""
    }
}
