package com.emh.app.ui.history

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.emh.app.history.HistoryEntry

class HistoryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                HistoryScreen(
                    onEntrySelected = { entry ->
                        // Restore into the currently open floating panel if available
                        com.emh.app.ui.PanelState.onRestoreRequest?.invoke(entry)
                        finish()
                    },
                    onClose = { finish() }
                )
            }
        }
    }
}