package com.emh.app.ui

import com.emh.app.history.HistoryEntry

/**
 * Simple shared state so that HistoryScreen can restore an entry into the currently open floating panel.
 */
object PanelState {
    var onRestoreRequest: ((HistoryEntry) -> Unit)? = null

    // AUTONOMOUS: Reset for clean panel state between sessions (used on close)
    fun clearRestoreHandler() {
        onRestoreRequest = null
    }
}