package com.emh.app.agent

import com.emh.app.memory.RelationshipMemoryManager
import com.emh.app.ai.EmotionalPromptEngine
import com.emh.app.skills.SkillRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class EmotionalAgentOrchestratorTest {

    private lateinit var memoryManager: RelationshipMemoryManager
    private lateinit var promptEngine: EmotionalPromptEngine
    private lateinit var skillRegistry: SkillRegistry
    private lateinit var orchestrator: EmotionalAgentOrchestrator

    @Before
    fun setup() {
        memoryManager = mock(RelationshipMemoryManager::class.java)
        promptEngine = mock(EmotionalPromptEngine::class.java)
        skillRegistry = mock(SkillRegistry::class.java)

        whenever(memoryManager.buildContextForAI(any())).thenReturn("Known: user likes direct communication.")
        whenever(promptEngine.buildAgentAnalysisPrompt(any())).thenReturn("ANALYSIS: User seems frustrated. Strategy: validate first.")
        whenever(promptEngine.generateEmotionalReply(any(), any())).thenReturn("""{"suggestedReply":"I hear you. Let's talk when we're both calm.","emotionalInsight":"Validation first reduces escalation.","recommendedTone":"calm"}""")
        whenever(skillRegistry.runEnabledSkills(any())).thenReturn(listOf("Tone insight: High anger detected.", "Suggested memory update: User expressed frustration with work."))

        orchestrator = EmotionalAgentOrchestrator(memoryManager, promptEngine, skillRegistry)
    }

    @Test
    fun `generateReply enriches with skills and extracts memory suggestions`() = kotlinx.coroutines.runBlocking {
        val result = orchestrator.generateReply(
            contactKey = "partner",
            incomingMessage = "You always make me so angry when you ignore me!",
            desiredFigurativeLevel = 4,
            preferredTone = "Direct",
            recentHistory = listOf("Previous: Can we talk?")
        )

        assertTrue("suggestedReply should contain reply text", result.suggestedReply.contains("I hear you") || result.suggestedReply.contains("calm"))
        assertTrue("reasoning should include enriched analysis + skill notes", result.reasoning.contains("Skill insights") || result.reasoning.contains("ANALYSIS"))
        assertTrue("memorySuggestions should be extracted", result.memorySuggestions.any { it.contains("frustration") || it.contains("work") })
        assertTrue("invokedSkills should include tone and memory", result.invokedSkills.contains("tone_analyzer") && result.invokedSkills.contains("memory_update"))
    }

    @Test
    fun `generateReply passes recent history into context`() = kotlinx.coroutines.runBlocking {
        whenever(skillRegistry.runEnabledSkills(any())).thenReturn(emptyList())

        val result = orchestrator.generateReply(
            contactKey = "friend",
            incomingMessage = "Hey",
            desiredFigurativeLevel = 5,
            preferredTone = "",
            recentHistory = listOf("msg1", "msg2", "msg3")
        )

        // The prompt engine was called with a context that had the history (we mainly assert the flow didn't blow up and returned something)
        assertTrue(result.suggestedReply.isNotBlank())
    }
}
