package com.emh.app.skills

import com.emh.app.agent.EmotionalContext

/**
 * Lightweight Skill interface for EMH.
 * Skills can be prompt enhancers, analyzers, or side-effect actions.
 *
 * v1 is intentionally simple (no complex reflection or dependency injection)
 * so it can run quickly on-device and be easily extended.
 */
interface EmhSkill {
    val id: String
    val description: String

    /**
     * Run the skill. Return a short result string (or empty if no output needed).
     */
    fun run(context: EmotionalContext, extraData: Map<String, Any> = emptyMap()): String
}
