package com.emh.app.data

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Instrumentation tests for SettingsRepository.
 * AUTONOMOUS TESTING: Added to verify persistence.
 */
@RunWith(AndroidJUnit4::class)
class SettingsRepositoryTest {

    private lateinit var context: Context
    private lateinit var testDataStore: androidx.datastore.core.DataStore<Preferences>
    private lateinit var repository: SettingsRepository
    private val testScope = TestScope(UnconfinedTestDispatcher())

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { context.preferencesDataStoreFile("test_settings") }
        )
        repository = SettingsRepository(context)
    }

    @After
    fun tearDown() {
        File(context.filesDir, "datastore/test_settings.preferences_pb").delete()
    }

    @Test
    fun `default ollamaUrl is localhost`() = runBlocking {
        val url = repository.ollamaUrl.first()
        assertEquals("http://localhost:11434", url)
    }

    @Test
    fun `setOllamaUrl persists value`() = runBlocking {
        repository.setOllamaUrl("http://192.168.1.100:11434")
        val url = repository.ollamaUrl.first()
        assertEquals("http://192.168.1.100:11434", url)
    }
}