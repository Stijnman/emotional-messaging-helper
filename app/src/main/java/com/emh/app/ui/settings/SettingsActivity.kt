package com.emh.app.ui.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                SettingsScreen()
            }
        }
    }
}

// Note: The real polished SettingsScreen composable lives in SettingsScreen.kt
// and is the one used here. The old duplicate was removed during autonomous cleanup.