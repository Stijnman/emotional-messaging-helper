package com.emh.app.history

import android.content.Context
import kotlinx.coroutines.flow.Flow

class HistoryManager(context: Context) {

    private val database = HistoryDatabase.getDatabase(context)
    private val dao = database.historyDao()

    suspend fun saveEntry(entry: HistoryEntry) {
        dao.insert(entry)
    }

    fun getAllEntries(): Flow<List<HistoryEntry>> {
        return dao.getAllEntries()
    }

    fun searchEntries(query: String): Flow<List<HistoryEntry>> {
        return if (query.isBlank()) {
            dao.getAllEntries()
        } else {
            dao.searchEntries(query)
        }
    }

    suspend fun clearHistory() {
        dao.clearAll()
    }

    suspend fun exportHistoryAsJson(): String {
        return "[] // TODO: Implement proper JSON export"
    }
}