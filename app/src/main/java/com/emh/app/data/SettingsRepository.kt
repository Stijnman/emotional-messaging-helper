package com.emh.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "emh_settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val OLLAMA_URL = stringPreferencesKey("ollama_url")
        val DEFAULT_MODEL = stringPreferencesKey("default_model")
        val AUTO_ANALYZE = stringPreferencesKey("auto_analyze") // stored as string for simplicity

        // Phase 2/3: Per-skill enablement persisted via DataStore (used by SkillRegistry + Settings UI)
        val SKILL_DECEPTION = booleanPreferencesKey("skill_deception_enabled")
        val SKILL_TONE = booleanPreferencesKey("skill_tone_enabled")
        val SKILL_EMPATHY = booleanPreferencesKey("skill_empathy_enabled")
        val SKILL_MEMORY = booleanPreferencesKey("skill_memory_enabled")
        val SKILL_CONFLICT = booleanPreferencesKey("skill_conflict_enabled")  // 5th skill: ConflictDeescalator
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

    // === Agent Skill Toggles (Phase 2/3) ===

    val skillDeceptionEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SKILL_DECEPTION] ?: true
    }
    val skillToneEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SKILL_TONE] ?: true
    }
    val skillEmpathyEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SKILL_EMPATHY] ?: true
    }
    val skillMemoryEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SKILL_MEMORY] ?: true
    }
    val skillConflictEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SKILL_CONFLICT] ?: true
    }

    suspend fun setSkillDeceptionEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SKILL_DECEPTION] = enabled }
    }
    suspend fun setSkillToneEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SKILL_TONE] = enabled }
    }
    suspend fun setSkillEmpathyEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SKILL_EMPATHY] = enabled }
    }
    suspend fun setSkillMemoryEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SKILL_MEMORY] = enabled }
    }
    suspend fun setSkillConflictEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SKILL_CONFLICT] = enabled }
    }

    /** Snapshot of current skill enables (call from suspend context before agent runs). */
    suspend fun getSkillEnables(): Map<String, Boolean> = try {
        mapOf(
            "deception_flag" to skillDeceptionEnabled.first(),
            "tone_analyzer" to skillToneEnabled.first(),
            "empathy_booster" to skillEmpathyEnabled.first(),
            "memory_update" to skillMemoryEnabled.first(),
            "conflict_deescalator" to skillConflictEnabled.first()
        )
    } catch (_: Exception) {
        mapOf(
            "deception_flag" to true,
            "tone_analyzer" to true,
            "empathy_booster" to true,
            "memory_update" to true,
            "conflict_deescalator" to true
        )
    }
}