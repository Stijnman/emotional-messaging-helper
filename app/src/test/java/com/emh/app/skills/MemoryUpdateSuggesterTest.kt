package com.emh.app.skills

import com.emh.app.agent.EmotionalContext
import org.junit.Assert.assertTrue
import org.junit.Test

class MemoryUpdateSuggesterTest {

    private fun ctx(msg: String) = EmotionalContext(
        contactKey = "alex",
        incomingMessage = msg,
        relationshipMemory = ""
    )

    @Test
    fun `suggests on preference language`() {
        val res = MemoryUpdateSuggester.run(ctx("I prefer when you text me good night"))
        assertTrue(res.contains("preference", ignoreCase = true) || res.contains("memory update", ignoreCase = true))
    }

    @Test
    fun `suggests on personal event mention`() {
        val res = MemoryUpdateSuggester.run(ctx("My birthday is next week and my family is visiting"))
        assertTrue(res.contains("memory update", ignoreCase = true) || res.contains("personal", ignoreCase = true))
    }

    @Test
    fun `no suggestion for neutral message`() {
        val res = MemoryUpdateSuggester.run(ctx("See you tomorrow"))
        assertTrue(res.isBlank())
    }
}
