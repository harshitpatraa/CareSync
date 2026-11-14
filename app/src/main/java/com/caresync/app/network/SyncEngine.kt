package com.caresync.app.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
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
import com.caresync.app.model.toFirestoreDoc
import com.caresync.app.model.toRoomEntity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

data class SyncState(
    val isSyncing: Boolean = false,
    val lastSyncLabel: String = "Not synced yet",
    val statusMessage: String = "Offline-first storage active",
    val isNetworkConnected: Boolean = true
)

@Singleton
class SyncEngine @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val syncQueueDao: SyncQueueDao,
    private val patientDao: PatientDao,
    private val vitalsDao: VitalsDao,
    private val healthRecordDao: HealthRecordDao,
    private val activityDao: ActivityDao,
    private val notificationDao: NotificationDao,
    private val firestoreRepository: CareSyncFirestoreRepository
) {
    private val _syncState = MutableStateFlow(SyncState())
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val syncScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var networkCallbackRegistered = false

    fun registerNetworkMonitor(isOfflineOnlyProvider: suspend () -> Boolean) {
        if (networkCallbackRegistered) return
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return
        networkCallbackRegistered = true

        val activeNetwork = cm.activeNetwork
        val caps = activeNetwork?.let { cm.getNetworkCapabilities(it) }
        val connectedNow = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        _syncState.value = _syncState.value.copy(isNetworkConnected = connectedNow)

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        runCatching {
            cm.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    _syncState.value = _syncState.value.copy(isNetworkConnected = true)
                    syncScope.launch {
                        val offlineOnly = isOfflineOnlyProvider()
                        if (!offlineOnly && Firebase.auth.currentUser != null) {
                            val pending = syncQueueDao.getAllQueueItems()
                            if (pending.isNotEmpty()) {
                                runSync(isOfflineOnlyMode = false)
                            }
                        }
                    }
                }

                override fun onLost(network: Network) {
                    _syncState.value = _syncState.value.copy(
                        isNetworkConnected = false,
                        statusMessage = "Offline mode · Queued records safe in Room"
                    )
                }
            })
        }
    }

    fun resetStateOnLogout() {
        _syncState.value = SyncState()
    }

    suspend fun runSync(isOfflineOnlyMode: Boolean): Result<Int> {
        if (isOfflineOnlyMode) {
            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                statusMessage = "Offline-only mode enabled. Records stay queued safely."
            )
            return Result.failure(IllegalStateException("Offline-only mode is enabled in Settings."))
        }

        val currentUser = Firebase.auth.currentUser
        val uid = currentUser?.uid
        if (uid == null) {
            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                statusMessage = "Sign in with Google required to synchronize with CareSync Cloud."
            )
            return Result.failure(IllegalStateException("Please sign in with Google to sync records."))
        }

        val pending = syncQueueDao.getAllQueueItems()
        _syncState.value = _syncState.value.copy(
            isSyncing = true,
            statusMessage = if (pending.isEmpty()) {
                "Refreshing records from CareSync Cloud..."
            } else {
                "Synchronizing ${pending.size} queued record(s) with Cloud Firestore..."
            }
        )

        return try {
            firestoreRepository.ensureUserProfile(
                userId = uid,
                displayName = currentUser.displayName ?: "CareSync Clinician",
                email = currentUser.email
            )

            var syncedCount = 0
            for (item in pending) {
                syncQueueDao.updateStatus(item.id, SyncQueueStatus.SYNCING.name)
                when (item.entityType) {
                    "Patient Record", "Triage Flag" -> {
                        val patient = patientDao.getPatientById(item.entityId)
                        if (patient != null) {
                            firestoreRepository.upsertPatient(uid, patient.toFirestoreDoc()).getOrThrow()
                        }
                    }
                    "Vitals" -> {
                        val vitals = vitalsDao.getVitalsById(item.entityId)
                        if (vitals != null) {
                            firestoreRepository.upsertVitals(uid, vitals.toFirestoreDoc()).getOrThrow()
                        }
                    }
                    else -> {
                        val record = healthRecordDao.getRecordById(item.entityId)
                        if (record != null) {
                            firestoreRepository.upsertHealthRecord(uid, record.toFirestoreDoc()).getOrThrow()
                        }
                    }
                }
                syncQueueDao.deleteItem(item.id)
                syncedCount++
            }

            patientDao.markAllSynced()
            vitalsDao.markAllSynced()
            healthRecordDao.markAllSynced()

            // Bidirectional sync: pull cloud patients, vitals, records & notifications
            val cloudPatients = firestoreRepository.getPatients(uid).getOrNull().orEmpty()
            if (cloudPatients.isEmpty()) {
                // Bootstrap local workspace to cloud on first sync
                for (local in patientDao.getAllPatientsOnce()) {
                    firestoreRepository.upsertPatient(uid, local.toFirestoreDoc())
                }
                for (localVitals in vitalsDao.getAllVitalsOnce()) {
                    firestoreRepository.upsertVitals(uid, localVitals.toFirestoreDoc())
                }
                for (localRec in healthRecordDao.getAllRecordsOnce()) {
                    firestoreRepository.upsertHealthRecord(uid, localRec.toFirestoreDoc())
                }
                for (localNotif in notificationDao.getAllNotificationsOnce()) {
                    firestoreRepository.upsertNotification(uid, localNotif.toFirestoreDoc())
                }
            } else {
                val unsyncedPatientIds = patientDao.getUnsyncedPatientIds().toSet()
                val safePatients = cloudPatients
                    .filterNot { it.id in unsyncedPatientIds }
                    .map { it.toRoomEntity() }
                if (safePatients.isNotEmpty()) {
                    patientDao.upsertPatients(safePatients)
                }

                val cloudVitals = firestoreRepository.getVitals(uid).getOrNull().orEmpty()
                val unsyncedVitalsIds = vitalsDao.getUnsyncedVitalsIds().toSet()
                val safeVitals = cloudVitals
                    .filterNot { it.id in unsyncedVitalsIds }
                    .map { it.toRoomEntity() }
                if (safeVitals.isNotEmpty()) {
                    vitalsDao.insertAllVitals(safeVitals)
                }

                val cloudRecords = firestoreRepository.getHealthRecords(uid).getOrNull().orEmpty()
                val unsyncedRecordIds = healthRecordDao.getUnsyncedRecordIds().toSet()
                val safeRecords = cloudRecords
                    .filterNot { it.id in unsyncedRecordIds }
                    .map { it.toRoomEntity() }
                if (safeRecords.isNotEmpty()) {
                    healthRecordDao.insertRecords(safeRecords)
                }

                val cloudNotifs = firestoreRepository.getNotifications(uid).getOrNull().orEmpty()
                if (cloudNotifs.isNotEmpty()) {
                    notificationDao.insertNotifications(cloudNotifs.map { it.toRoomEntity() })
                }
            }

            val now = System.currentTimeMillis()
            val formattedTime = "Today, " + SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(now))
            if (syncedCount > 0) {
                activityDao.insertActivity(
                    ActivityEntity(
                        id = "AC-${UUID.randomUUID().toString().take(6)}",
                        title = "Synced $syncedCount CareSync record(s)",
                        subtitle = "Room queue synchronized to CareSync Cloud Firestore",
                        timestampLabel = "Just now",
                        categoryTag = "Sync",
                        createdAt = now
                    )
                )
                val notif = NotificationEntity(
                    id = "NT-${UUID.randomUUID().toString().take(6)}",
                    title = "Cloud Sync Complete",
                    message = "$syncedCount pending offline record(s) synchronized to Firestore.",
                    type = NotificationType.SYNC.name,
                    timestampLabel = "Just now",
                    isRead = false,
                    createdAt = now
                )
                notificationDao.insertNotification(notif)
                firestoreRepository.upsertNotification(uid, notif.toFirestoreDoc())
            }

            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                lastSyncLabel = formattedTime,
                statusMessage = "Synchronized with CareSync Cloud"
            )
            Result.success(syncedCount)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            for (item in pending) {
                syncQueueDao.updateStatus(item.id, SyncQueueStatus.FAILED.name)
            }
            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                statusMessage = e.localizedMessage ?: "Cloud sync paused; records remain safe offline."
            )
            Result.failure(e)
        }
    }
}
