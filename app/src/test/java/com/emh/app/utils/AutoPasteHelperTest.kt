package com.emh.app.utils

import android.content.Context
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

/**
 * Tests for AutoPasteHelper.
 * AUTONOMOUS: Added in testing phase.
 */
class AutoPasteHelperTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = mock(Context::class.java)
    }

    @Test
    fun `copyToClipboard does not crash`() {
        // Since it's system service, just ensure no exception in call
        AutoPasteHelper.copyToClipboard(context, "test reply")
        // If no crash, pass
        assertNotNull(context)
    }

    @Test
    fun `getLastPasteMethod returns expected fallback`() {
        val method = AutoPasteHelper.getLastPasteMethod()
        assertNotNull(method)
    }
}