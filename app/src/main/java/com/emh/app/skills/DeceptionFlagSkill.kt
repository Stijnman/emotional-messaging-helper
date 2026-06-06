package com.emh.app.skills

import com.emh.app.agent.EmotionalContext

/**
 * Skill to detect potential emotional manipulation, gaslighting, or high-pressure language.
 * Returns a flag string if detected (gentle, non-accusatory).
 */
object DeceptionFlagSkill : EmhSkill {
    override val id = "deception_flag"
    override val description = "Detects absolute language, conditional pressure, or social proof tactics in incoming messages."

    override fun run(context: EmotionalContext, extraData: Map<String, Any>): String {
        val lower = context.incomingMessage.lowercase()
        val flags = mutableListOf<String>()

        if (lower.contains("you always") || lower.contains("you never") || lower.contains("you always do this")) {
            flags += "absolute language (you always/never)"
        }
        if (lower.contains("if you really") || lower.contains("you would if you cared") || lower.contains("if you loved me")) {
            flags += "conditional emotional pressure"
        }
        if (lower.contains("everyone thinks") || lower.contains("no one else") || lower.contains("people are saying")) {
            flags += "social proof / isolation tactic"
        }
        if (lower.contains("you're overreacting") || lower.contains("you're too sensitive")) {
            flags += "gaslighting / invalidation"
        }

        return if (flags.isNotEmpty()) {
            "⚠️ Potential emotional pattern: ${flags.joinToString(", ")}. Consider a calm, curious, boundary-setting reply rather than immediate defense."
        } else ""
    }
}
