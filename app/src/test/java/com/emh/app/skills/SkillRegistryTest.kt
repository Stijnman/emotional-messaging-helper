package com.emh.app.skills

import com.emh.app.agent.EmotionalContext
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SkillRegistryTest {

    private fun ctx(msg: String) = EmotionalContext(
        contactKey = "t",
        incomingMessage = msg,
        relationshipMemory = ""
    )

    @Test
    fun `configureEnabled controls runEnabledSkills output`() {
        val reg = SkillRegistry()
        val c = ctx("You always say that and if you really cared you would")

        // default on
        val withAll = reg.runEnabledSkills(c)
        assertTrue(withAll.any { it.contains("absolute", ignoreCase = true) || it.contains("pattern", ignoreCase = true) })

        // turn deception off
        reg.configureEnabled(mapOf("deception_flag" to false, "tone_analyzer" to true, "empathy_booster" to true, "memory_update" to true))
        val withoutDeception = reg.runEnabledSkills(c)
        assertFalse(withoutDeception.any { it.contains("absolute", ignoreCase = true) || it.contains("pattern", ignoreCase = true) })
    }

    @Test
    fun `disabled skill produces no notes`() {
        val reg = SkillRegistry()
        reg.configureEnabled(mapOf("deception_flag" to false, "tone_analyzer" to false, "empathy_booster" to false, "memory_update" to false))
        val notes = reg.runEnabledSkills(ctx("I feel really sad and lonely today"))
        assertTrue(notes.isEmpty())
    }
}
