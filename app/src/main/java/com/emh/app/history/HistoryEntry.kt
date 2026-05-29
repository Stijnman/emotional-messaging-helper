package com.emh.app.history

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message_history")
data class HistoryEntry(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val contactKey: String,
    val originalMessage: String,
    val visionDescription: String? = null,
    val suggestedReply: String,
    val emotionalInsight: String,
    val toneUsed: String,
    val figurativeLevel: Int,
    val wasSent: Boolean = false,

    // AUTONOMOUS IMPROVEMENT (Odd loops): Added metadata field for future analytics and testing.
    val visionUsed: Boolean = false
)