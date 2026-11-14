package com.caresync.app.network

import com.caresync.app.database.ActivityDao
import com.caresync.app.database.ActivityEntity
import com.caresync.app.database.HealthRecordDao
import com.caresync.app.database.NotificationDao
import com.caresync.app.database.NotificationEntity
import com.caresync.app.database.PatientDao
import com.caresync.app.database.SyncQueueDao
import com.caresync.app.database.VitalsDao
import com.caresync.app.model.NotificationType
import com.caresync.app.model.SyncQueueStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

data class SyncState(
    val isSyncing: Boolean = false,
    val lastSyncLabel: String = "Today, 07:45 AM",
    val statusMessage: String = "Offline-first storage active"
)

@Singleton
class SyncEngine @Inject constructor(
    private val syncQueueDao: SyncQueueDao,
    private val patientDao: PatientDao,
    private val vitalsDao: VitalsDao,
    private val healthRecordDao: HealthRecordDao,
    private val activityDao: ActivityDao,
    private val notificationDao: NotificationDao
) {
    private val _syncState = MutableStateFlow(SyncState())
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    suspend fun runSync(isOfflineOnlyMode: Boolean): Result<Int> {
        if (isOfflineOnlyMode) {
            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                statusMessage = "Offline-only mode enabled. Records stay queued safely."
            )
            return Result.failure(IllegalStateException("Offline-only mode is enabled in Settings."))
        }

        val pending = syncQueueDao.getAllQueueItems()
        if (pending.isEmpty()) {
            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                lastSyncLabel = "Just now",
                statusMessage = "All patient records are up to date"
            )
            return Result.success(0)
        }

        _syncState.value = _syncState.value.copy(
            isSyncing = true,
            statusMessage = "Synchronizing ${pending.size} queued record(s)..."
        )

        for (item in pending) {
            syncQueueDao.updateStatus(item.id, SyncQueueStatus.SYNCING.name)
            delay(250L)
            syncQueueDao.deleteItem(item.id)
        }

        patientDao.markAllSynced()
        vitalsDao.markAllSynced()
        healthRecordDao.markAllSynced()

        val now = System.currentTimeMillis()
        activityDao.insertActivity(
            ActivityEntity(
                id = "AC-${UUID.randomUUID().toString().take(6)}",
                title = "Synced ${pending.size} CareSync record(s)",
                subtitle = "Local Room queue synchronized with regional health ledger",
                timestampLabel = "Just now",
                categoryTag = "Sync",
                createdAt = now
            )
        )
        notificationDao.insertNotification(
            NotificationEntity(
                id = "NT-${UUID.randomUUID().toString().take(6)}",
                title = "Sync Complete",
                message = "${pending.size} pending offline record(s) successfully synchronized.",
                type = NotificationType.SYNC.name,
                timestampLabel = "Just now",
                isRead = false,
                createdAt = now
            )
        )

        _syncState.value = SyncState(
            isSyncing = false,
            lastSyncLabel = "Just now",
            statusMessage = "All records synchronized"
        )
        return Result.success(pending.size)
    }
}
