package com.emh.app.skills

import com.emh.app.agent.EmotionalContext
import org.junit.Assert.assertTrue
import org.junit.Test

class DeceptionFlagSkillTest {

    private fun ctx(msg: String) = EmotionalContext(
        contactKey = "test",
        incomingMessage = msg,
        relationshipMemory = ""
    )

    @Test
    fun `flags absolute language`() {
        val res = DeceptionFlagSkill.run(ctx("You always do this and you never listen"))
        assertTrue(res.contains("absolute language", ignoreCase = true))
    }

    @Test
    fun `flags conditional pressure and gaslighting`() {
        val res = DeceptionFlagSkill.run(ctx("If you really loved me you would do it. You're overreacting."))
        assertTrue(res.contains("conditional", ignoreCase = true) || res.contains("gaslighting", ignoreCase = true))
    }

    @Test
    fun `returns empty on clean message`() {
        val res = DeceptionFlagSkill.run(ctx("Hey, how was your day?"))
        assertTrue(res.isBlank())
    }
}
