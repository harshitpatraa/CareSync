package com.caresync.app.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Query("SELECT * FROM patients ORDER BY isCritical DESC, updatedAt DESC")
    fun observeAllPatients(): Flow<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE id = :patientId LIMIT 1")
    fun observePatientById(patientId: String): Flow<PatientEntity?>

    @Query("SELECT * FROM patients WHERE id = :patientId LIMIT 1")
    suspend fun getPatientById(patientId: String): PatientEntity?

    @Query("SELECT * FROM patients")
    suspend fun getAllPatientsOnce(): List<PatientEntity>

    @Query("SELECT id FROM patients WHERE syncStatus != 'SYNCED'")
    suspend fun getUnsyncedPatientIds(): List<String>

    @Query("SELECT COUNT(*) FROM patients")
    suspend fun countPatients(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPatient(patient: PatientEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPatients(patients: List<PatientEntity>)

    @Query("UPDATE patients SET isCritical = :isCritical, riskLevel = :riskLevel, syncStatus = :syncStatus, updatedAt = :updatedAt WHERE id = :patientId")
    suspend fun updateCriticalStatus(
        patientId: String,
        isCritical: Boolean,
        riskLevel: String,
        syncStatus: String,
        updatedAt: Long
    )

    @Query("UPDATE patients SET syncStatus = :syncStatus WHERE id = :patientId")
    suspend fun updateSyncStatus(patientId: String, syncStatus: String)

    @Query("UPDATE patients SET syncStatus = 'SYNCED'")
    suspend fun markAllSynced()

    @Query("DELETE FROM patients WHERE id = :patientId")
    suspend fun deletePatientById(patientId: String)

    @Query("DELETE FROM patients")
    suspend fun clearAll()
}

@Dao
interface VitalsDao {
    @Query("SELECT * FROM vitals WHERE patientId = :patientId ORDER BY recordedAtMillis DESC")
    fun observeVitalsForPatient(patientId: String): Flow<List<VitalsEntity>>

    @Query("SELECT * FROM vitals ORDER BY recordedAtMillis DESC")
    fun observeAllVitals(): Flow<List<VitalsEntity>>

    @Query("SELECT * FROM vitals WHERE id = :vitalsId LIMIT 1")
    suspend fun getVitalsById(vitalsId: String): VitalsEntity?

    @Query("SELECT * FROM vitals WHERE patientId = :patientId ORDER BY recordedAtMillis DESC LIMIT 1")
    suspend fun getLatestVitalsForPatient(patientId: String): VitalsEntity?

    @Query("SELECT * FROM vitals")
    suspend fun getAllVitalsOnce(): List<VitalsEntity>

    @Query("SELECT id FROM vitals WHERE syncStatus != 'SYNCED'")
    suspend fun getUnsyncedVitalsIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVitals(vitals: VitalsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllVitals(vitals: List<VitalsEntity>)

    @Query("UPDATE vitals SET syncStatus = :syncStatus WHERE id = :vitalsId")
    suspend fun updateSyncStatus(vitalsId: String, syncStatus: String)

    @Query("UPDATE vitals SET syncStatus = 'SYNCED'")
    suspend fun markAllSynced()

    @Query("DELETE FROM vitals WHERE patientId = :patientId")
    suspend fun deleteVitalsForPatient(patientId: String)

    @Query("DELETE FROM vitals")
    suspend fun clearAll()
}

@Dao
interface HealthRecordDao {
    @Query("SELECT * FROM health_records WHERE patientId = :patientId ORDER BY createdAt DESC")
    fun observeRecordsForPatient(patientId: String): Flow<List<HealthRecordEntity>>

    @Query("SELECT * FROM health_records ORDER BY createdAt DESC")
    fun observeAllRecords(): Flow<List<HealthRecordEntity>>

    @Query("SELECT * FROM health_records WHERE id = :recordId LIMIT 1")
    suspend fun getRecordById(recordId: String): HealthRecordEntity?

    @Query("SELECT * FROM health_records")
    suspend fun getAllRecordsOnce(): List<HealthRecordEntity>

    @Query("SELECT id FROM health_records WHERE syncStatus != 'SYNCED'")
    suspend fun getUnsyncedRecordIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: HealthRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<HealthRecordEntity>)

    @Query("UPDATE health_records SET syncStatus = :syncStatus WHERE id = :recordId")
    suspend fun updateSyncStatus(recordId: String, syncStatus: String)

    @Query("UPDATE health_records SET syncStatus = 'SYNCED'")
    suspend fun markAllSynced()

    @Query("DELETE FROM health_records WHERE patientId = :patientId")
    suspend fun deleteRecordsForPatient(patientId: String)

    @Query("DELETE FROM health_records")
    suspend fun clearAll()
}

@Dao
interface SyncQueueDao {
    @Query("SELECT * FROM sync_queue ORDER BY createdAt DESC")
    fun observeSyncQueue(): Flow<List<SyncQueueEntity>>

    @Query("SELECT * FROM sync_queue ORDER BY createdAt ASC")
    suspend fun getAllQueueItems(): List<SyncQueueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(item: SyncQueueEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueueAll(items: List<SyncQueueEntity>)

    @Query("UPDATE sync_queue SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun deleteItem(id: String)

    @Query("DELETE FROM sync_queue WHERE entityId = :entityId")
    suspend fun deleteItemsByEntityId(entityId: String)

    @Query("DELETE FROM sync_queue")
    suspend fun clearAll()
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    fun observeAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications")
    suspend fun getAllNotificationsOnce(): List<NotificationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM notifications")
    suspend fun clearAll()
}

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities ORDER BY createdAt DESC LIMIT 20")
    fun observeRecentActivities(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities ORDER BY createdAt DESC")
    suspend fun getAllActivitiesOnce(): List<ActivityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<ActivityEntity>)

    @Query("DELETE FROM activities")
    suspend fun clearAll()
}
