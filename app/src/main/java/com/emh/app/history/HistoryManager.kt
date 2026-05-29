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
        val entries = dao.getAllEntriesSync()
        return com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(entries)
    }

    suspend fun getAllEntriesSync(): List<HistoryEntry> {
        return dao.getAllEntriesSync()
    }

    // AUTONOMOUS IMPROVEMENT (Loop 1+): Added method for future testing/verification of history integrity.
    suspend fun countEntries(): Int = dao.getAllEntriesSync().size
}