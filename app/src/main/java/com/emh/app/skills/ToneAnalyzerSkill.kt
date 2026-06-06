package com.emh.app.skills

import com.emh.app.agent.EmotionalContext

/**
 * Analyzes the emotional tone of the incoming message and provides guidance.
 * Can be used to suggest tone adjustments.
 */
object ToneAnalyzerSkill : EmhSkill {
    override val id = "tone_analyzer"
    override val description = "Analyzes emotional tone (anger, sadness, pressure, question) and suggests reply strategy."

    override fun run(context: EmotionalContext, extraData: Map<String, Any>): String {
        val lower = context.incomingMessage.lowercase()
        val suggestions = mutableListOf<String>()

        when {
            lower.contains("angry") || lower.contains("furious") || lower.contains("pissed") -> {
                suggestions.add("High anger detected. Prioritize de-escalation and validation before problem-solving.")
            }
            lower.contains("sad") || lower.contains("down") || lower.contains("hurt") || lower.contains("crying") -> {
                suggestions.add("Sadness or hurt. Lean heavily into empathy and presence; avoid fixing too quickly.")
            }
            lower.contains("?") && context.incomingMessage.length > 60 -> {
                suggestions.add("Complex emotional question. Offer both emotional attunement and a clear, warm response.")
            }
            lower.contains("you should") || lower.contains("you need to") -> {
                suggestions.add("Directive/pressure language. Respond with autonomy-respecting language and gentle pushback if needed.")
            }
        }

        return if (suggestions.isNotEmpty()) {
            "Tone insight: ${suggestions.joinToString(" ")}"
        } else ""
    }
}
