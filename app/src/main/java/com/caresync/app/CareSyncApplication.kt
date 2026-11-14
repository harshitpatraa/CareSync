package com.caresync.app

import android.app.Application
import com.caresync.app.database.DatabaseSeeder
import com.caresync.app.network.initCustomFirebaseIfPresent
import com.caresync.app.repository.CareSyncRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class CareSyncApplication : Application() {
    @Inject
    lateinit var seeder: DatabaseSeeder

    @Inject
    lateinit var repository: CareSyncRepository

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            seeder.seedIfEmpty()
            try {
                val prefs = repository.preferencesFlow.first()
                if (prefs.firebaseAppId.isNotBlank() && prefs.geminiApiKey.isNotBlank()) {
                    initCustomFirebaseIfPresent(
                        this@CareSyncApplication,
                        prefs.firebaseProjectId,
                        prefs.firebaseAppId,
                        prefs.geminiApiKey
                    )
                }
            } catch (ignored: Exception) {}
        }
    }
}
