package com.emh.app.agent

/**
 * Structured context passed to the emotional agent.
 * Contains everything the agent needs to reason about a reply.
 */
data class EmotionalContext(
    val contactKey: String,
    val incomingMessage: String,
    val relationshipMemory: String,           // from RelationshipMemoryManager.buildContextForAI
    val recentHistory: List<String> = emptyList(), // last few messages for multi-turn awareness
    val desiredFigurativeLevel: Int = 5,      // 0 = direct, 10 = highly poetic/metaphorical
    val preferredTone: String = "",           // e.g. "warm and supportive", "playful", "direct"
    val hasVisionContext: Boolean = false,
    val visionDescription: String = ""
) {
    fun toPromptBlock(): String = buildString {
        appendLine("Contact: $contactKey")
        if (relationshipMemory.isNotBlank()) {
            appendLine("Relationship Memory: $relationshipMemory")
        }
        if (recentHistory.isNotEmpty()) {
            appendLine("Recent conversation turns:")
            recentHistory.takeLast(4).forEachIndexed { i, msg ->
                appendLine("  ${i + 1}. $msg")
            }
        }
        appendLine("User wants tone: ${preferredTone.ifBlank { "natural and emotionally intelligent" }} (Figurative level: $desiredFigurativeLevel/10)")
        if (hasVisionContext) {
            appendLine("Visual context from screenshot is available.")
        }
        appendLine("Incoming message from the other person: \"$incomingMessage\"")
    }
}
