package com.caresync.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.caresync.app.database.ActivityDao
import com.caresync.app.database.CareSyncDatabase
import com.caresync.app.database.HealthRecordDao
import com.caresync.app.database.NotificationDao
import com.caresync.app.database.PatientDao
import com.caresync.app.database.SyncQueueDao
import com.caresync.app.database.VitalsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.careSyncDataStore: DataStore<Preferences> by preferencesDataStore(name = "caresync_prefs")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.careSyncDataStore

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CareSyncDatabase =
        Room.databaseBuilder(
            context,
            CareSyncDatabase::class.java,
            "caresync_offline.db"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()

    @Provides
    fun providePatientDao(db: CareSyncDatabase): PatientDao = db.patientDao()

    @Provides
    fun provideVitalsDao(db: CareSyncDatabase): VitalsDao = db.vitalsDao()

    @Provides
    fun provideHealthRecordDao(db: CareSyncDatabase): HealthRecordDao = db.healthRecordDao()

    @Provides
    fun provideSyncQueueDao(db: CareSyncDatabase): SyncQueueDao = db.syncQueueDao()

    @Provides
    fun provideNotificationDao(db: CareSyncDatabase): NotificationDao = db.notificationDao()

    @Provides
    fun provideActivityDao(db: CareSyncDatabase): ActivityDao = db.activityDao()
}
