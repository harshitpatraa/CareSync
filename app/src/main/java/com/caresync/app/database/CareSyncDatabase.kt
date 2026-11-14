package com.caresync.app.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        PatientEntity::class,
        VitalsEntity::class,
        HealthRecordEntity::class,
        SyncQueueEntity::class,
        NotificationEntity::class,
        ActivityEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CareSyncDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun vitalsDao(): VitalsDao
    abstract fun healthRecordDao(): HealthRecordDao
    abstract fun syncQueueDao(): SyncQueueDao
    abstract fun notificationDao(): NotificationDao
    abstract fun activityDao(): ActivityDao

    suspend fun clearAllLocalData() {
        syncQueueDao().clearAll()
        vitalsDao().clearAll()
        healthRecordDao().clearAll()
        notificationDao().clearAll()
        activityDao().clearAll()
        patientDao().clearAll()
    }
}
