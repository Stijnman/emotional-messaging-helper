package com.emh.app.memory

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Relationship Memory Vault (Encrypted)
 * 
 * Stores per-contact notes and preferences securely.
 * Uses EncryptedSharedPreferences for better privacy (Item 27).
 */
class RelationshipMemoryManager(context: Context) {

    private val prefs: SharedPreferences = run {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            "relationship_memory_encrypted",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveNote(contactKey: String, note: String) {
        prefs.edit().putString("note_$contactKey", note).apply()
    }

    fun getNote(contactKey: String): String {
        return prefs.getString("note_$contactKey", "") ?: ""
    }

    fun savePreference(contactKey: String, key: String, value: String) {
        prefs.edit().putString("${contactKey}_$key", value).apply()
    }

    fun getPreference(contactKey: String, key: String, default: String = ""): String {
        return prefs.getString("${contactKey}_$key", default) ?: default
    }

    fun buildContextForAI(contactKey: String): String {
        val note = getNote(contactKey)
        val tonePref = getPreference(contactKey, "preferred_tone", "")
        
        return buildString {
            if (note.isNotBlank()) append("Known context about this person: $note. ")
            if (tonePref.isNotBlank()) append("They respond best to a $tonePref tone. ")
        }
    }

    // AUTONOMOUS IMPROVEMENT (Odd loops): Added helper for future relationship strength scoring.
    fun getRelationshipStrength(contactKey: String): Int {
        val noteLength = getNote(contactKey).length
        return (noteLength / 50).coerceAtMost(10)
    }
}