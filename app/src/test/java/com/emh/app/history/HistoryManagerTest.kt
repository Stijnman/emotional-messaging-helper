package com.emh.app.history

import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class HistoryManagerTest {

    private lateinit var context: Context
    private lateinit var historyManager: HistoryManager

    @Before
    fun setup() {
        context = mock(Context::class.java)
        // Note: In a real test we would use an in-memory Room database.
        // For now this is a structural placeholder that will be expanded.
        historyManager = HistoryManager(context)
    }

    @Test
    fun `exportHistoryAsJson returns valid JSON even when empty`() = runBlocking {
        val json = historyManager.exportHistoryAsJson()
        assertEquals("[]", json.trim())
    }

    // TODO (AUTONOMOUS TEST TODO): Add proper tests with in-memory database
    // Example future test:
    // - saveEntry → getAllEntries should return it
    // - searchEntries should filter correctly
}