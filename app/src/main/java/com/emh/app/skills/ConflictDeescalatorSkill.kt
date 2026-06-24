package com.emh.app.skills

import com.emh.app.agent.EmotionalContext

/**
 * Skill to detect escalating or conflict-charged language and recommend de-escalation strategies.
 * Suggests calm, "I"-statement first approaches, validation, and pauses instead of defensiveness.
 */
object ConflictDeescalatorSkill : EmhSkill {
    override val id = "conflict_deescalator"
    override val description = "Detects escalation cues and suggests calm, validating, de-escalating reply tactics."

    override fun run(context: EmotionalContext, extraData: Map<String, Any>): String {
        val lower = context.incomingMessage.lowercase()
        val cues = mutableListOf<String>()

        if (lower.contains("you always") || lower.contains("you never") || lower.contains("every time")) {
            cues.add("repetitive blame language")
        }
        if (lower.contains("make me") || lower.contains("you make me feel") || lower.contains("you're making")) {
            cues.add("externalizing emotional responsibility")
        }
        if (lower.contains("fight") || lower.contains("fighting") || lower.contains("argue") || lower.contains("yelling") || lower.contains("screaming")) {
            cues.add("conflict escalation reference")
        }
        if (lower.contains("i'm done") || lower.contains("over this") || lower.contains("can't do this anymore")) {
            cues.add("withdrawal or shutdown signals")
        }

        return if (cues.isNotEmpty()) {
            "Conflict cue detected (${cues.joinToString(", ")}). Strongly consider starting with validation and an 'I feel...' statement. Suggest a short pause or 'Can we slow this down?' before problem-solving. Avoid matching energy or defending immediately."
        } else ""
    }
}
