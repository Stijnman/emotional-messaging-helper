package com.emh.app.ai

import com.emh.app.memory.RelationshipMemoryManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class EmotionalPromptEngineTest {

    private lateinit var memoryManager: RelationshipMemoryManager
    private lateinit var promptEngine: EmotionalPromptEngine

    @Before
    fun setup() {
        memoryManager = mock(RelationshipMemoryManager::class.java)
        promptEngine = EmotionalPromptEngine(memoryManager)
    }

    @Test
    fun `buildPrompt includes relationship context when available`() {
        // This is a basic structural test
        val prompt = promptEngine.buildPrompt(
            contactKey = "test-contact",
            originalMessage = "I miss you",
            figurativeLevel = 6,
            tonePreset = "Warm"
        )

        assertTrue("Prompt should contain relationship context section", prompt.contains("Relationship Context:"))
        assertTrue("Prompt should contain the user's message", prompt.contains("I miss you"))
    }

    @Test
    fun `buildPrompt handles vision context correctly`() {
        val prompt = promptEngine.buildPrompt(
            contactKey = "test-contact",
            originalMessage = "Look at this",
            visionDescription = "User sent a photo of a sunset",
            figurativeLevel = 7
        )

        assertTrue("Prompt should mention visual context when provided", 
            prompt.contains("VISUAL CONTEXT") || prompt.contains("screenshot"))
    }

    @Test
    fun `parseResponse handles valid JSON response`() {
        val fakeResponse = """
            {
              "suggestedReply": "I love you too",
              "emotionalInsight": "The user is expressing affection",
              "recommendedTone": "warm"
            }
        """.trimIndent()

        val result = promptEngine.parseResponse(fakeResponse)

        assertTrue(result.suggestedReply.contains("I love you"))
        assertTrue(result.emotionalInsight.isNotBlank())
    }

    @Test
    fun `parseResponse falls back gracefully on bad JSON`() {
        val badResponse = "This is not valid JSON at all"

        val result = promptEngine.parseResponse(badResponse)

        // Should not crash and should return something usable
        assertTrue(result.suggestedReply.isNotBlank())
    }

    @Test
    fun `buildPrompt includes vision instructions when image is provided`() {
        val prompt = promptEngine.buildPrompt(
            contactKey = "test",
            originalMessage = "Test message",
            visionDescription = "User shared a photo",
            figurativeLevel = 5
        )

        assertTrue(
            "Prompt should instruct the model about attached image when vision is used",
            prompt.contains("actual screenshot image") || prompt.contains("visual details from the image")
        )
    }

    // AUTONOMOUS TEST: Basic sanity check for vision-related prompt behavior
    @Test
    fun `buildPrompt produces longer prompt when vision is present`() {
        val withoutVision = promptEngine.buildPrompt("c", "msg", figurativeLevel = 5)
        val withVision = promptEngine.buildPrompt("c", "msg", visionDescription = "photo", figurativeLevel = 5)

        assertTrue("Vision prompt should be longer", withVision.length > withoutVision.length)
    }

    // AUTONOMOUS TEST (new in testing phase)
    @Test
    fun `OllamaClient correctly identifies vision models`() {
        assertTrue(OllamaClient.isLikelyVisionModel("llava"))
        assertTrue(OllamaClient.isLikelyVisionModel("llava-llama3"))
        assertFalse(OllamaClient.isLikelyVisionModel("llama3.2"))
    }

    // AUTONOMOUS TEST - Added during final completion push
    @Test
    fun `buildPrompt respects figurative level in tone guidance`() {
        val lowFig = promptEngine.buildPrompt("c", "msg", figurativeLevel = 1)
        val highFig = promptEngine.buildPrompt("c", "msg", figurativeLevel = 9)

        assertTrue(lowFig.contains("very direct") || lowFig.contains("literal"))
        assertTrue(highFig.contains("expressive") || highFig.contains("poetic") || highFig.contains("intensity"))
    }

    @Test
    fun `parseResponse handles JSON with extra whitespace and code fences`() {
        val fenced = """
            ```json
            {
              "suggestedReply": "Test reply here",
              "emotionalInsight": "Insight text",
              "recommendedTone": "playful"
            }
            ```
        """.trimIndent()
        val result = promptEngine.parseResponse(fenced)
        assertTrue(result.suggestedReply.contains("Test reply"))
    }

    @Test
    fun `buildVisionPrompt delegates and includes vision markers`() {
        val prompt = promptEngine.buildVisionPrompt("c", "hi", "photo of smile")
        assertTrue(prompt.contains("VISUAL CONTEXT") || prompt.contains("screenshot"))
        assertTrue(prompt.contains("photo of smile"))
    }
}