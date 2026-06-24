package com.emh.app.memory

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation tests for RelationshipMemoryManager.
 * AUTONOMOUS: Part of testing until all working.
 */
@RunWith(AndroidJUnit4::class)
class RelationshipMemoryManagerTest {

    private lateinit var context: Context
    private lateinit var manager: RelationshipMemoryManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        manager = RelationshipMemoryManager(context)
        manager.saveNote("test-contact", "")
        manager.savePreference("test-contact", "preferred_tone", "")
    }

    @After
    fun tearDown() {
        manager.saveNote("test-contact", "")
    }

    @Test
    fun `save and get note works`() {
        manager.saveNote("test-contact", "Loves dogs and hiking")
        val note = manager.getNote("test-contact")
        assertEquals("Loves dogs and hiking", note)
    }

    @Test
    fun `buildContextForAI includes note and tone`() {
        manager.saveNote("test-contact", "Close friend")
        manager.savePreference("test-contact", "preferred_tone", "playful")
        val context = manager.buildContextForAI("test-contact")
        assertEquals(true, context.contains("Close friend"))
        assertEquals(true, context.contains("playful"))
    }

    @Test
    fun `getRelationshipStrength increases with more notes`() {
        manager.saveNote("test-contact", "a".repeat(100))
        val strength = manager.getRelationshipStrength("test-contact")
        assertEquals(2, strength)
    }

    @Test
    fun `clearNote removes stored note`() {
        manager.saveNote("test-contact", "temp")
        manager.clearNote("test-contact")
        assertEquals("", manager.getNote("test-contact"))
    }

    @Test
    fun `importEncryptedMemory accepts JSON array from exportAllMemory`() {
        manager.saveNote("contact-a", "Note A")
        manager.savePreference("contact-a", "preferred_tone", "warm")
        manager.saveNote("contact-b", "Note B")

        val exported = manager.exportAllMemory()
        manager.saveNote("contact-a", "")
        manager.saveNote("contact-b", "")

        assertEquals(true, manager.importEncryptedMemory(exported))
        assertEquals("Note A", manager.getNote("contact-a"))
        assertEquals("warm", manager.getPreference("contact-a", "preferred_tone", ""))
        assertEquals("Note B", manager.getNote("contact-b"))
    }
}