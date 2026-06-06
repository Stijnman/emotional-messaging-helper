package com.emh.app.skills

import com.emh.app.agent.EmotionalContext

/**
 * Skill to boost empathy in responses.
 * Suggests adding validation or "I hear you" language when the message shows vulnerability.
 */
object EmpathyBoosterSkill : EmhSkill {
    override val id = "empathy_booster"
    override val description = "Suggests adding empathy/validation phrases when the other person seems vulnerable."

    override fun run(context: EmotionalContext, extraData: Map<String, Any>): String {
        val lower = context.incomingMessage.lowercase()
        if (lower.contains("i feel") || lower.contains("i'm sad") || lower.contains("hurts") || lower.contains("lonely")) {
            return "The other person is expressing vulnerability. Strongly consider starting with validation like 'I hear you' or 'That sounds really tough' before offering solutions."
        }
        return ""
    }
}
