package com.emh.app.ai

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OllamaClientTest {

    @Test
    fun `isLikelyVisionModel detects common vision models`() {
        assertTrue(OllamaClient.isLikelyVisionModel("llava"))
        assertTrue(OllamaClient.isLikelyVisionModel("llava-llama3"))
        assertTrue(OllamaClient.isLikelyVisionModel("moondream"))
        assertFalse(OllamaClient.isLikelyVisionModel("llama3.2"))
        assertFalse(OllamaClient.isLikelyVisionModel("mistral"))
    }

    // AUTONOMOUS TESTING: More tests will be added with mocked HTTP client in future loops.
    // Example future test:
    // @Test
    // fun `generateWithImages sends correct payload`() { ... }

    @Test
    fun `suggestVisionModelIfNeeded returns vision model when needed`() {
        assertEquals("llava", OllamaClient.suggestVisionModelIfNeeded("llama3.2"))
        assertEquals("llava", OllamaClient.suggestVisionModelIfNeeded("mistral"))
        assertEquals("llava-llama3", OllamaClient.suggestVisionModelIfNeeded("llava-llama3"))
    }

    @Test
    fun `RECOMMENDED_VISION_MODELS contains expected entries`() {
        assertTrue(OllamaClient.RECOMMENDED_VISION_MODELS.contains("llava"))
        assertTrue(OllamaClient.RECOMMENDED_VISION_MODELS.any { it.contains("llava") })
    }
}