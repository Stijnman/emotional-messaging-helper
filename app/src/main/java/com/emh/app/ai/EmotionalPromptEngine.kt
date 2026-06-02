package com.emh.app.ai

import com.emh.app.memory.RelationshipMemoryManager

/**
 * Builds rich, psychologically intelligent prompts for emotional messaging assistance.
 */
class EmotionalPromptEngine(
    private val memoryManager: RelationshipMemoryManager
) {

    fun buildPrompt(
        contactKey: String,
        originalMessage: String,
        visionDescription: String? = null,
        figurativeLevel: Int = 5,
        tonePreset: String? = null,
        relationshipContext: String? = null
    ): String {
        val hasVision = !visionDescription.isNullOrBlank()
        val memoryContext = memoryManager.buildContextForAI(contactKey)
        val effectiveContext = relationshipContext ?: memoryContext

        val toneGuidance = when {
            !tonePreset.isNullOrBlank() -> "Respond in a $tonePreset tone."
            figurativeLevel <= 2 -> "Be very direct, literal, and clear. Avoid metaphors."
            figurativeLevel in 3..4 -> "Use gentle, warm language with light emotional coloring."
            figurativeLevel == 5 -> "Balance emotional depth with clarity and sincerity."
            figurativeLevel in 6..7 -> "Use richer emotional language and thoughtful metaphors."
            figurativeLevel in 8..9 -> "Be highly expressive, poetic, and emotionally layered."
            else -> "Maximum emotional and figurative intensity. Use powerful, evocative language."
        }

        val visionPart = if (!visionDescription.isNullOrBlank()) {
            """
            
            IMPORTANT - VISUAL CONTEXT (Screenshot provided):
            A screenshot of the WhatsApp conversation was captured right before this request.
            Pay close attention to:
            - Emotional tone visible in the chat (emojis, capitalization, message length, punctuation, reply timing)
            - Facial expressions, body language or scene details if people/photos visible
            - Any images, memes, or shared media visible in the screenshot
            - Timing and conversation flow
            $visionDescription

            Reference specific visual elements (e.g. "the laughing emoji" or "the photo of the beach") in the emotional insight when relevant. Make the suggested reply feel like it belongs in this exact visual conversation.

            The user has also attached the actual screenshot image for your direct analysis. Use the visual details from the image (not just the text description) to craft a more accurate and emotionally resonant reply and insight. You can "see" the image.

            // AUTONOMOUS IMPROVEMENT: When vision is provided, prioritize emotional signals visible in the image and analyze text + visual content together.
            """.trimIndent()
        } else ""

        return """
You are an expert emotional intelligence coach and message crafting assistant specialized in romantic, close relationship, and important personal communication.

Your goal is to help the user craft a reply that is:
- Psychologically intelligent and emotionally attuned
- Authentic to the user's voice while improving emotional impact
- Appropriate to the relationship dynamics and history
- Clear, warm, and effective

### Relationship Context:
$effectiveContext

### Current Message from Other Person:
"$originalMessage"
$visionPart

### Response Style Instructions:
- Figurative intensity level: $figurativeLevel/10
- $toneGuidance
- Keep the reply natural and conversational (not overly long).
- Include emotional insight or subtext when helpful.

### Output Format:
Respond with a JSON object containing exactly these fields:
{
  "suggestedReply": "The actual message the user should send (conversational, ready to copy/paste)",
  "emotionalInsight": "A short, insightful explanation of the emotional dynamics at play and why this reply works well (1-2 sentences)",
  "recommendedTone": "One or two words describing the tone of your suggested reply"
}

Only output valid JSON. Do not add any extra text before or after the JSON.
        """.trimIndent()
    }

<<<<<<< HEAD
=======
    /**
     * AUTONOMOUS: Dedicated method for vision cases to allow future specialization.
     */
    fun buildVisionPrompt(
        contactKey: String,
        originalMessage: String,
        visionDescription: String?,
        figurativeLevel: Int = 5,
        tonePreset: String? = null
    ): String {
        return buildPrompt(contactKey, originalMessage, visionDescription, figurativeLevel, tonePreset)
    }

>>>>>>> f06f07e (test+fix+polish: autonomous continue - fix EmotionalPromptEngine class (parseResponse), strengthen vision prompts, enhance Ollama health+listModels, expand tests (prompt parse, history, memory clear), CI on feature branches, touch ALL files (services, ui, memory, manifest, docs), bump version, update templates)
    fun parseResponse(rawResponse: String): EmotionalResponse {
        return try {
            // Clean up common LLM JSON formatting issues
            val cleaned = rawResponse
                .trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            // Very simple extraction (in real app we'd use proper JSON parsing with fallback)
            val suggestedReply = extractField(cleaned, "suggestedReply")
            val emotionalInsight = extractField(cleaned, "emotionalInsight")
            val recommendedTone = extractField(cleaned, "recommendedTone")

            EmotionalResponse(
                suggestedReply = suggestedReply ?: "I'm here for you. What are you feeling right now?",
                emotionalInsight = emotionalInsight ?: "The other person seems to be seeking connection or reassurance.",
                recommendedTone = recommendedTone ?: "warm"
            )
        } catch (e: Exception) {
            EmotionalResponse(
                suggestedReply = rawResponse.take(280),
                emotionalInsight = "The model returned a direct response instead of structured output.",
                recommendedTone = "direct"
            )
        }
    }

    private fun extractField(json: String, field: String): String? {
        val regex = """"$field"\s*:\s*"((?:\\"|[^"])*)"""".toRegex()
        return regex.find(json)?.groupValues?.get(1)?.replace("\\\"", "\"")
    }
}

data class EmotionalResponse(
    val suggestedReply: String,
    val emotionalInsight: String,
    val recommendedTone: String
)