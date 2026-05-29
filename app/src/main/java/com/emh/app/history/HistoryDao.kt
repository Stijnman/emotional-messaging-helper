package com.emh.app.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: HistoryEntry)

    @Query("SELECT * FROM message_history ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<HistoryEntry>>

    @Query("""
        SELECT * FROM message_history 
        WHERE originalMessage LIKE '%' || :query || '%' 
           OR emotionalInsight LIKE '%' || :query || '%' 
           OR (visionDescription LIKE '%' || :query || '%')
        ORDER BY timestamp DESC
    """)
    fun searchEntries(query: String): Flow<List<HistoryEntry>>

    @Query("DELETE FROM message_history")
    suspend fun clearAll()

    @Query("SELECT * FROM message_history ORDER BY timestamp DESC")
    suspend fun getAllEntriesSync(): List<HistoryEntry>

    // AUTONOMOUS IMPROVEMENT (Loop 1+): Added for testing and statistics in future loops.
    @Query("SELECT COUNT(*) FROM message_history")
    suspend fun getEntryCount(): Int
}