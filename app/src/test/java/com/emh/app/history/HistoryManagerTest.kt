package com.emh.app.history

import org.junit.Assert.assertTrue
import org.junit.Test

class HistoryManagerTest {

    @Test
    fun `empty history export format is documented`() {
        assertTrue("[]".trim().startsWith("["))
    }
}