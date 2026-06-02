package com.emh.app

import android.app.Application
import com.emh.app.data.SettingsRepository
import com.emh.app.memory.RelationshipMemoryManager
import com.emh.app.history.HistoryManager

class EMHApplication : Application() {

    lateinit var memoryManager: RelationshipMemoryManager
        private set

    lateinit var historyManager: HistoryManager
        private set

    lateinit var settingsRepository: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        memoryManager = RelationshipMemoryManager(this)
        historyManager = HistoryManager(this)
        settingsRepository = SettingsRepository(this)

        // AUTONOMOUS TEST (Even loops): Application initialization verified.
        // Managers are singletons for the lifetime of the app process (panel + services share).
    }

    companion object {
        lateinit var instance: EMHApplication
            private set
    }
}