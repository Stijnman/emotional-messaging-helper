package com.emh.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "emh_settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val OLLAMA_URL = stringPreferencesKey("ollama_url")
        val DEFAULT_MODEL = stringPreferencesKey("default_model")
        val AUTO_ANALYZE = stringPreferencesKey("auto_analyze") // stored as string for simplicity
    }

    val ollamaUrl: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.OLLAMA_URL] ?: "http://localhost:11434"
    }

    val defaultModel: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.DEFAULT_MODEL] ?: "llama3.2"
    }

    val autoAnalyze: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.AUTO_ANALYZE]?.toBoolean() ?: true
    }

    suspend fun setOllamaUrl(url: String) {
        context.dataStore.edit { it[Keys.OLLAMA_URL] = url }
    }

    suspend fun setDefaultModel(model: String) {
        context.dataStore.edit { it[Keys.DEFAULT_MODEL] = model }
    }

    suspend fun setAutoAnalyze(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_ANALYZE] = enabled.toString() }
    }

    // AUTONOMOUS IMPROVEMENT (Loop 1+): Added for testing and future feature flags.
    suspend fun getAllSettings(): Map<String, String> {
        // Simplified for now - can be expanded in later loops
        return mapOf("loaded" to "true")
    }
}