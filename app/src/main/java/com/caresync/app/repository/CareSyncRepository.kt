package com.caresync.app.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.caresync.app.database.ActivityDao
import com.caresync.app.database.ActivityEntity
import com.caresync.app.database.CareSyncDatabase
import com.caresync.app.database.DatabaseSeeder
import com.caresync.app.database.HealthRecordDao
import com.caresync.app.database.HealthRecordEntity
import com.caresync.app.database.NotificationDao
import com.caresync.app.database.NotificationEntity
import com.caresync.app.database.PatientDao
import com.caresync.app.database.PatientEntity
import com.caresync.app.database.SyncQueueDao
import com.caresync.app.database.SyncQueueEntity
import com.caresync.app.database.VitalsDao
import com.caresync.app.database.VitalsEntity
import com.caresync.app.model.AppLanguage
import com.caresync.app.model.EmergencyContact
import com.caresync.app.model.EmergencyInstruction
import com.caresync.app.model.NearbyHealthCenter
import com.caresync.app.model.NotificationType
import com.caresync.app.model.RecordType
import com.caresync.app.model.RiskLevel
import com.caresync.app.model.RxMedicationItem
import com.caresync.app.model.SyncQueueStatus
import com.caresync.app.model.SyncStatus
import com.caresync.app.model.UserProfile
import com.caresync.app.model.toFirestoreDoc
import com.caresync.app.model.toRoomEntity
import com.caresync.app.network.CareSyncFirestoreRepository
import com.caresync.app.network.CareSyncSyncWorker
import com.caresync.app.network.SyncEngine
import com.caresync.app.network.SyncState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

data class AppPreferences(
    val isDarkMode: Boolean = false,
    val isOfflineOnlyMode: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isSecurityLockEnabled: Boolean = false,
    val securityPin: String = "1234",
    val language: AppLanguage = AppLanguage.ENGLISH,
    val userName: String = "Dr. Priya Kulkarni",
    val userEmail: String = "priya.kulkarni@caresync.org",
    val role: String = "Senior Community Health Officer",
    val workerId: String = "CS-CHW-409",
    val clinicName: String = "CareSync Community Health Hub · Ward 4",
    val district: String = "North Health Circle · Zone B",
    val phone: String = "+91 98111 70409",
    val geminiApiKey: String = "",
    val firebaseProjectId: String = "",
    val firebaseAppId: String = ""
)

@Singleton
class CareSyncRepository @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val dataStore: DataStore<Preferences>,
    private val database: CareSyncDatabase,
    private val seeder: DatabaseSeeder,
    private val patientDao: PatientDao,
    private val vitalsDao: VitalsDao,
    private val healthRecordDao: HealthRecordDao,
    private val syncQueueDao: SyncQueueDao,
    private val notificationDao: NotificationDao,
    private val activityDao: ActivityDao,
    private val syncEngine: SyncEngine,
    private val firestoreRepository: CareSyncFirestoreRepository
) {
    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val OFFLINE_ONLY = booleanPreferencesKey("offline_only")
        val LOGGED_IN = booleanPreferencesKey("logged_in")
        val SECURITY_LOCK = booleanPreferencesKey("security_lock")
        val SECURITY_PIN = stringPreferencesKey("security_pin")
        val LANGUAGE = stringPreferencesKey("language")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val ROLE = stringPreferencesKey("user_role")
        val WORKER_ID = stringPreferencesKey("worker_id")
        val CLINIC_NAME = stringPreferencesKey("clinic_name")
        val DISTRICT = stringPreferencesKey("user_district")
        val PHONE = stringPreferencesKey("user_phone")
        val CUSTOM_CONTACTS = stringPreferencesKey("custom_emergency_contacts")
        val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
        val FIREBASE_PROJECT_ID = stringPreferencesKey("firebase_project_id")
        val FIREBASE_APP_ID = stringPreferencesKey("firebase_app_id")
    }

    val preferencesFlow: Flow<AppPreferences> = dataStore.data.map { prefs ->
        AppPreferences(
            isDarkMode = prefs[Keys.DARK_MODE] ?: false,
            isOfflineOnlyMode = prefs[Keys.OFFLINE_ONLY] ?: false,
            isLoggedIn = prefs[Keys.LOGGED_IN] ?: false,
            isSecurityLockEnabled = prefs[Keys.SECURITY_LOCK] ?: false,
            securityPin = prefs[Keys.SECURITY_PIN] ?: "1234",
            language = AppLanguage.entries.find { it.code == prefs[Keys.LANGUAGE] } ?: AppLanguage.ENGLISH,
            userName = prefs[Keys.USER_NAME] ?: "Dr. Priya Kulkarni",
            userEmail = prefs[Keys.USER_EMAIL] ?: "priya.kulkarni@caresync.org",
            role = prefs[Keys.ROLE] ?: "Senior Community Health Officer",
            workerId = prefs[Keys.WORKER_ID] ?: "CS-CHW-409",
            clinicName = prefs[Keys.CLINIC_NAME] ?: "CareSync Community Health Hub · Ward 4",
            district = prefs[Keys.DISTRICT] ?: "North Health Circle · Zone B",
            phone = prefs[Keys.PHONE] ?: "+91 98111 70409",
            geminiApiKey = prefs[Keys.GEMINI_API_KEY] ?: "",
            firebaseProjectId = prefs[Keys.FIREBASE_PROJECT_ID] ?: "",
            firebaseAppId = prefs[Keys.FIREBASE_APP_ID] ?: ""
        )
    }

    val allPatientsFlow: Flow<List<PatientEntity>> = patientDao.observeAllPatients()
    val allVitalsFlow: Flow<List<VitalsEntity>> = vitalsDao.observeAllVitals()
    val allRecordsFlow: Flow<List<HealthRecordEntity>> = healthRecordDao.observeAllRecords()
    val allActivitiesFlow: Flow<List<ActivityEntity>> = activityDao.observeRecentActivities()
    val syncQueueFlow: Flow<List<SyncQueueEntity>> = syncQueueDao.observeSyncQueue()
    val notificationsFlow: Flow<List<NotificationEntity>> = notificationDao.observeAllNotifications()
    val recentActivitiesFlow: Flow<List<ActivityEntity>> = activityDao.observeRecentActivities()
    val syncStateFlow: StateFlow<SyncState> get() = syncEngine.syncState

    private val _customContacts = MutableStateFlow<List<EmergencyContact>>(emptyList())
    val customContactsFlow: StateFlow<List<EmergencyContact>> = _customContacts.asStateFlow()

    fun observePatient(patientId: String): Flow<PatientEntity?> = patientDao.observePatientById(patientId)
    fun observeVitalsForPatient(patientId: String): Flow<List<VitalsEntity>> = vitalsDao.observeVitalsForPatient(patientId)
    fun observeRecordsForPatient(patientId: String): Flow<List<HealthRecordEntity>> = healthRecordDao.observeRecordsForPatient(patientId)

    suspend fun getPatientOnce(patientId: String): PatientEntity? = patientDao.getPatientById(patientId)
    suspend fun getLatestVitalsOnce(patientId: String): VitalsEntity? = vitalsDao.getLatestVitalsForPatient(patientId)

    suspend fun ensureSeeded() {
        seeder.seedIfEmpty()
        loadCustomContactsFromPrefs()
    }

    suspend fun onAuthenticatedSessionStarted(userId: String, displayName: String, email: String?) {
        seeder.seedIfEmpty()
        loadCustomContactsFromPrefs()
        login(email.orEmpty(), displayName)
        syncEngine.registerNetworkMonitor { preferencesFlow.first().isOfflineOnlyMode }

        val profileResult = firestoreRepository.ensureUserProfile(
            userId = userId,
            displayName = displayName,
            email = email
        )
        profileResult.getOrNull()?.let { doc ->
            dataStore.edit { prefs ->
                if (doc.displayName.isNotBlank()) prefs[Keys.USER_NAME] = doc.displayName
                if (!doc.email.isNullOrBlank()) prefs[Keys.USER_EMAIL] = doc.email
                if (doc.clinicName.isNotBlank()) prefs[Keys.CLINIC_NAME] = doc.clinicName
                if (doc.workerId.isNotBlank()) prefs[Keys.WORKER_ID] = doc.workerId
                if (!doc.district.isNullOrBlank()) prefs[Keys.DISTRICT] = doc.district
                if (!doc.role.isNullOrBlank()) prefs[Keys.ROLE] = doc.role
            }
        }
    }

    suspend fun observeAndMergeCloudData(userId: String) = coroutineScope {
        launch {
            firestoreRepository.observePatients(userId)
                .catch { error -> if (error is CancellationException) throw error }
                .collectLatest { cloudDocs ->
                    if (cloudDocs.isNotEmpty()) {
                        val unsyncedIds = patientDao.getUnsyncedPatientIds().toSet()
                        val mergeList = cloudDocs
                            .filterNot { it.id in unsyncedIds }
                            .map { it.toRoomEntity() }
                        if (mergeList.isNotEmpty()) {
                            patientDao.upsertPatients(mergeList)
                        }
                    }
                }
        }
        launch {
            firestoreRepository.observeVitals(userId)
                .catch { error -> if (error is CancellationException) throw error }
                .collectLatest { cloudVitals ->
                    if (cloudVitals.isNotEmpty()) {
                        val unsyncedIds = vitalsDao.getUnsyncedVitalsIds().toSet()
                        val mergeList = cloudVitals
                            .filterNot { it.id in unsyncedIds }
                            .map { it.toRoomEntity() }
                        if (mergeList.isNotEmpty()) {
                            vitalsDao.insertAllVitals(mergeList)
                        }
                    }
                }
        }
        launch {
            firestoreRepository.observeHealthRecords(userId)
                .catch { error -> if (error is CancellationException) throw error }
                .collectLatest { cloudRecords ->
                    if (cloudRecords.isNotEmpty()) {
                        val unsyncedIds = healthRecordDao.getUnsyncedRecordIds().toSet()
                        val mergeList = cloudRecords
                            .filterNot { it.id in unsyncedIds }
                            .map { it.toRoomEntity() }
                        if (mergeList.isNotEmpty()) {
                            healthRecordDao.insertRecords(mergeList)
                        }
                    }
                }
        }
        launch {
            firestoreRepository.observeNotifications(userId)
                .catch { error -> if (error is CancellationException) throw error }
                .collectLatest { cloudNotifs ->
                    if (cloudNotifs.isNotEmpty()) {
                        notificationDao.insertNotifications(cloudNotifs.map { it.toRoomEntity() })
                    }
                }
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[Keys.DARK_MODE] = enabled }
    }

    suspend fun setOfflineOnlyMode(enabled: Boolean) {
        dataStore.edit { it[Keys.OFFLINE_ONLY] = enabled }
        if (!enabled) {
            CareSyncSyncWorker.enqueueWhenConnected(appContext)
        }
    }

    suspend fun setSecurityLock(enabled: Boolean) {
        dataStore.edit { it[Keys.SECURITY_LOCK] = enabled }
    }

    suspend fun setSecurityPin(pin: String) {
        val sanitized = pin.filter { it.isDigit() }.take(6)
        if (sanitized.length >= 4) {
            dataStore.edit { it[Keys.SECURITY_PIN] = sanitized }
        }
    }

    suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { it[Keys.LANGUAGE] = language.code }
    }

    suspend fun setGeminiApiKey(apiKey: String) {
        dataStore.edit { it[Keys.GEMINI_API_KEY] = apiKey.trim() }
    }

    suspend fun setFirebaseCredentials(projectId: String, appId: String, apiKey: String) {
        dataStore.edit { prefs ->
            prefs[Keys.FIREBASE_PROJECT_ID] = projectId.trim()
            prefs[Keys.FIREBASE_APP_ID] = appId.trim()
            prefs[Keys.GEMINI_API_KEY] = apiKey.trim()
        }
    }

    suspend fun updateUserProfile(
        name: String,
        role: String,
        workerId: String,
        clinicName: String,
        district: String,
        phone: String
    ) {
        val cleanName = name.trim().ifBlank { "CareSync Clinician" }
        val cleanRole = role.trim().ifBlank { "Community Health Officer" }
        val cleanWorkerId = workerId.trim().ifBlank { "CS-CHW-409" }
        val cleanClinic = clinicName.trim().ifBlank { "CareSync Hub" }
        val cleanDistrict = district.trim().ifBlank { "North Health Circle" }
        val cleanPhone = phone.trim()

        dataStore.edit { prefs ->
            prefs[Keys.USER_NAME] = cleanName
            prefs[Keys.ROLE] = cleanRole
            prefs[Keys.WORKER_ID] = cleanWorkerId
            prefs[Keys.CLINIC_NAME] = cleanClinic
            prefs[Keys.DISTRICT] = cleanDistrict
            if (cleanPhone.isNotBlank()) prefs[Keys.PHONE] = cleanPhone
        }

        val currentUser = Firebase.auth.currentUser
        val isOfflineOnly = preferencesFlow.first().isOfflineOnlyMode
        if (!isOfflineOnly && currentUser != null) {
            firestoreRepository.updateUserProfile(
                userId = currentUser.uid,
                displayName = cleanName,
                email = currentUser.email,
                clinicName = cleanClinic,
                workerId = cleanWorkerId,
                district = cleanDistrict,
                role = cleanRole
            )
        }
    }

    suspend fun login(email: String, name: String? = null) {
        dataStore.edit { prefs ->
            prefs[Keys.LOGGED_IN] = true
            if (email.isNotBlank()) prefs[Keys.USER_EMAIL] = email
            if (!name.isNullOrBlank()) prefs[Keys.USER_NAME] = name
        }
    }

    suspend fun logoutAndIsolateWorkspace() {
        CareSyncSyncWorker.cancelPending(appContext)
        syncEngine.resetStateOnLogout()
        database.clearAllLocalData()
        dataStore.edit { prefs ->
            prefs[Keys.LOGGED_IN] = false
        }
    }

    fun isCriticalVitalsThreshold(
        systolic: Int,
        diastolic: Int,
        spo2: Int,
        tempC: Float,
        heartRate: Int,
        bloodSugar: Int
    ): Boolean {
        return spo2 < 92 ||
            systolic >= 160 ||
            diastolic >= 110 ||
            tempC >= 39.5f ||
            heartRate > 130 ||
            heartRate < 45 ||
            bloodSugar >= 300 ||
            bloodSugar < 55
    }

    suspend fun savePatientOffline(
        patient: PatientEntity,
        initialVitals: VitalsEntity? = null,
        isNew: Boolean = true
    ) {
        val now = System.currentTimeMillis()
        val offlinePatient = patient.copy(
            syncStatus = SyncStatus.PENDING_UPLOAD.name,
            updatedAt = now
        )
        patientDao.upsertPatient(offlinePatient)

        val vitalsQueueId = "SQ-${UUID.randomUUID().toString().take(6)}"
        if (initialVitals != null) {
            vitalsDao.insertVitals(initialVitals.copy(syncStatus = SyncStatus.PENDING_UPLOAD.name))
        }

        val timelineRecord = HealthRecordEntity(
            id = "HR-${UUID.randomUUID().toString().take(6)}",
            patientId = offlinePatient.id,
            patientName = offlinePatient.fullName,
            recordType = RecordType.TIMELINE.name,
            title = if (isNew) "Patient Registered Offline" else "Patient Record Updated",
            subtitle = "${offlinePatient.category} · ${offlinePatient.villageOrWard}",
            detail = offlinePatient.notes.ifBlank { "Saved securely to local Room storage" },
            dateLabel = "Just now",
            statusBadge = if (isNew) "Registered" else "Updated",
            syncStatus = SyncStatus.PENDING_UPLOAD.name,
            createdAt = now
        )
        healthRecordDao.insertRecord(timelineRecord)

        val patientQueueId = "SQ-${UUID.randomUUID().toString().take(6)}"
        syncQueueDao.enqueue(
            SyncQueueEntity(
                id = patientQueueId,
                entityType = "Patient Record",
                entityId = offlinePatient.id,
                patientName = offlinePatient.fullName,
                operation = if (isNew) "CREATE" else "UPDATE",
                payloadSummary = "${offlinePatient.fullName} (${offlinePatient.category})",
                createdAtLabel = "Just now",
                createdAt = now,
                status = SyncQueueStatus.QUEUED.name
            )
        )
        if (initialVitals != null) {
            syncQueueDao.enqueue(
                SyncQueueEntity(
                    id = vitalsQueueId,
                    entityType = "Vitals",
                    entityId = initialVitals.id,
                    patientName = offlinePatient.fullName,
                    operation = "CREATE",
                    payloadSummary = "Baseline BP ${initialVitals.systolicBp}/${initialVitals.diastolicBp}, SpO2 ${initialVitals.spo2Percent}%",
                    createdAtLabel = "Just now",
                    createdAt = now,
                    status = SyncQueueStatus.QUEUED.name
                )
            )
        }
        val recordQueueId = "SQ-${UUID.randomUUID().toString().take(6)}"
        syncQueueDao.enqueue(
            SyncQueueEntity(
                id = recordQueueId,
                entityType = timelineRecord.recordType,
                entityId = timelineRecord.id,
                patientName = offlinePatient.fullName,
                operation = "UPLOAD_RECORD",
                payloadSummary = timelineRecord.title,
                createdAtLabel = "Just now",
                createdAt = now,
                status = SyncQueueStatus.QUEUED.name
            )
        )

        activityDao.insertActivity(
            ActivityEntity(
                id = "AC-${UUID.randomUUID().toString().take(6)}",
                title = if (isNew) "Registered ${offlinePatient.fullName}" else "Updated ${offlinePatient.fullName}",
                subtitle = "Saved to Room vault · CareSync sync tracked",
                timestampLabel = "Just now",
                categoryTag = offlinePatient.category,
                createdAt = now
            )
        )

        // Opportunistic cloud write if online and signed in — remove synced queue items immediately
        val prefs = preferencesFlow.first()
        val uid = Firebase.auth.currentUser?.uid
        if (!prefs.isOfflineOnlyMode && uid != null) {
            val patientRes = firestoreRepository.upsertPatient(uid, offlinePatient.toFirestoreDoc())
            if (patientRes.isSuccess) {
                patientDao.updateSyncStatus(offlinePatient.id, SyncStatus.SYNCED.name)
                syncQueueDao.deleteItem(patientQueueId)
            }
            if (initialVitals != null) {
                val vitalsRes = firestoreRepository.upsertVitals(uid, initialVitals.toFirestoreDoc())
                if (vitalsRes.isSuccess) {
                    vitalsDao.updateSyncStatus(initialVitals.id, SyncStatus.SYNCED.name)
                    syncQueueDao.deleteItem(vitalsQueueId)
                }
            }
            val recRes = firestoreRepository.upsertHealthRecord(uid, timelineRecord.toFirestoreDoc())
            if (recRes.isSuccess) {
                healthRecordDao.updateSyncStatus(timelineRecord.id, SyncStatus.SYNCED.name)
                syncQueueDao.deleteItem(recordQueueId)
            }
        } else {
            CareSyncSyncWorker.enqueueWhenConnected(appContext)
        }
    }

    suspend fun deletePatient(patientId: String) {
        val existing = patientDao.getPatientById(patientId)
        syncQueueDao.deleteItemsByEntityId(patientId)
        vitalsDao.deleteVitalsForPatient(patientId)
        healthRecordDao.deleteRecordsForPatient(patientId)
        patientDao.deletePatientById(patientId)

        if (existing != null) {
            activityDao.insertActivity(
                ActivityEntity(
                    id = "AC-${UUID.randomUUID().toString().take(6)}",
                    title = "Removed record: ${existing.fullName}",
                    subtitle = "Deleted from local CareSync storage",
                    timestampLabel = "Just now",
                    categoryTag = "Records",
                    createdAt = System.currentTimeMillis()
                )
            )
        }

        val prefs = preferencesFlow.first()
        val uid = Firebase.auth.currentUser?.uid
        if (!prefs.isOfflineOnlyMode && uid != null) {
            firestoreRepository.deletePatient(uid, patientId)
        }
    }

    suspend fun addVitalsOffline(
        patientId: String,
        patientName: String,
        vitals: VitalsEntity
    ): Boolean {
        val now = System.currentTimeMillis()
        val pendingVitals = vitals.copy(
            recordedAtMillis = now,
            syncStatus = SyncStatus.PENDING_UPLOAD.name
        )
        vitalsDao.insertVitals(pendingVitals)
        patientDao.updateSyncStatus(patientId, SyncStatus.PENDING_UPLOAD.name)

        val timelineRecord = HealthRecordEntity(
            id = "HR-${UUID.randomUUID().toString().take(6)}",
            patientId = patientId,
            patientName = patientName,
            recordType = RecordType.TIMELINE.name,
            title = "Vitals Recorded",
            subtitle = "BP ${vitals.systolicBp}/${vitals.diastolicBp} · SpO2 ${vitals.spo2Percent}% · HR ${vitals.heartRate} bpm",
            detail = "Temp ${vitals.temperatureC}°C · Sugar ${vitals.bloodSugarMgDl} mg/dL",
            dateLabel = vitals.recordedAtLabel,
            statusBadge = "Vitals",
            syncStatus = SyncStatus.PENDING_UPLOAD.name,
            createdAt = now
        )
        healthRecordDao.insertRecord(timelineRecord)

        val vitalsQueueId = "SQ-${UUID.randomUUID().toString().take(6)}"
        val recQueueId = "SQ-${UUID.randomUUID().toString().take(6)}"
        syncQueueDao.enqueue(
            SyncQueueEntity(
                id = vitalsQueueId,
                entityType = "Vitals",
                entityId = vitals.id,
                patientName = patientName,
                operation = "CREATE",
                payloadSummary = "BP ${vitals.systolicBp}/${vitals.diastolicBp}, SpO2 ${vitals.spo2Percent}%",
                createdAtLabel = "Just now",
                createdAt = now,
                status = SyncQueueStatus.QUEUED.name
            )
        )
        syncQueueDao.enqueue(
            SyncQueueEntity(
                id = recQueueId,
                entityType = timelineRecord.recordType,
                entityId = timelineRecord.id,
                patientName = patientName,
                operation = "UPLOAD_RECORD",
                payloadSummary = timelineRecord.title,
                createdAtLabel = "Just now",
                createdAt = now,
                status = SyncQueueStatus.QUEUED.name
            )
        )

        activityDao.insertActivity(
            ActivityEntity(
                id = "AC-${UUID.randomUUID().toString().take(6)}",
                title = "Vitals logged for $patientName",
                subtitle = "SpO2 ${vitals.spo2Percent}% · BP ${vitals.systolicBp}/${vitals.diastolicBp}",
                timestampLabel = "Just now",
                categoryTag = "Vitals",
                createdAt = now
            )
        )

        // Auto-Triage Critical Vitals Detection
        val criticalAlert = isCriticalVitalsThreshold(
            systolic = vitals.systolicBp,
            diastolic = vitals.diastolicBp,
            spo2 = vitals.spo2Percent,
            tempC = vitals.temperatureC,
            heartRate = vitals.heartRate,
            bloodSugar = vitals.bloodSugarMgDl
        )

        val prefs = preferencesFlow.first()
        val uid = Firebase.auth.currentUser?.uid

        if (criticalAlert) {
            patientDao.updateCriticalStatus(
                patientId = patientId,
                isCritical = true,
                riskLevel = RiskLevel.CRITICAL.name,
                syncStatus = SyncStatus.PENDING_UPLOAD.name,
                updatedAt = now
            )
            val notif = NotificationEntity(
                id = "NT-${UUID.randomUUID().toString().take(6)}",
                title = "Critical Vitals Alert: $patientName",
                message = "Abnormal reading (BP ${vitals.systolicBp}/${vitals.diastolicBp}, SpO2 ${vitals.spo2Percent}%, Temp ${vitals.temperatureC}°C). Immediate triage advised.",
                type = NotificationType.EMERGENCY.name,
                timestampLabel = "Just now",
                isRead = false,
                patientId = patientId,
                createdAt = now
            )
            notificationDao.insertNotification(notif)
            if (!prefs.isOfflineOnlyMode && uid != null) {
                firestoreRepository.upsertNotification(uid, notif.toFirestoreDoc())
                patientDao.getPatientById(patientId)?.let {
                    firestoreRepository.upsertPatient(uid, it.toFirestoreDoc())
                }
            }
        }

        if (!prefs.isOfflineOnlyMode && uid != null) {
            val vRes = firestoreRepository.upsertVitals(uid, pendingVitals.toFirestoreDoc())
            if (vRes.isSuccess) {
                vitalsDao.updateSyncStatus(pendingVitals.id, SyncStatus.SYNCED.name)
                syncQueueDao.deleteItem(vitalsQueueId)
            }
            val rRes = firestoreRepository.upsertHealthRecord(uid, timelineRecord.toFirestoreDoc())
            if (rRes.isSuccess) {
                healthRecordDao.updateSyncStatus(timelineRecord.id, SyncStatus.SYNCED.name)
                syncQueueDao.deleteItem(recQueueId)
            }
            if (vRes.isSuccess && rRes.isSuccess && !criticalAlert) {
                patientDao.updateSyncStatus(patientId, SyncStatus.SYNCED.name)
            }
        } else {
            CareSyncSyncWorker.enqueueWhenConnected(appContext)
        }

        return criticalAlert
    }

    suspend fun addHealthRecordOffline(record: HealthRecordEntity) {
        val now = System.currentTimeMillis()
        val offlineRecord = record.copy(
            syncStatus = SyncStatus.PENDING_UPLOAD.name,
            createdAt = now
        )
        healthRecordDao.insertRecord(offlineRecord)
        patientDao.updateSyncStatus(record.patientId, SyncStatus.PENDING_UPLOAD.name)

        val queueId = "SQ-${UUID.randomUUID().toString().take(6)}"
        syncQueueDao.enqueue(
            SyncQueueEntity(
                id = queueId,
                entityType = record.recordType,
                entityId = offlineRecord.id,
                patientName = offlineRecord.patientName,
                operation = "UPLOAD_RECORD",
                payloadSummary = offlineRecord.title,
                createdAtLabel = "Just now",
                createdAt = now,
                status = SyncQueueStatus.QUEUED.name
            )
        )

        val prefs = preferencesFlow.first()
        val uid = Firebase.auth.currentUser?.uid
        if (!prefs.isOfflineOnlyMode && uid != null) {
            val res = firestoreRepository.upsertHealthRecord(uid, offlineRecord.toFirestoreDoc())
            if (res.isSuccess) {
                healthRecordDao.updateSyncStatus(offlineRecord.id, SyncStatus.SYNCED.name)
                syncQueueDao.deleteItem(queueId)
                patientDao.updateSyncStatus(record.patientId, SyncStatus.SYNCED.name)
            }
        } else {
            CareSyncSyncWorker.enqueueWhenConnected(appContext)
        }
    }

    suspend fun saveRxMedicationsAndReminders(
        patient: PatientEntity,
        medications: List<RxMedicationItem>
    ) {
        val now = System.currentTimeMillis()
        medications.forEachIndexed { idx, med ->
            val rec = HealthRecordEntity(
                id = "HR-${UUID.randomUUID().toString().take(6)}",
                patientId = patient.id,
                patientName = patient.fullName,
                recordType = RecordType.MEDICINE.name,
                title = "${med.name} ${med.dosage}".trim(),
                subtitle = med.frequency.ifBlank { "As directed" },
                detail = "Duration: ${med.duration.ifBlank { "5 days" }} · Extracted via Gemini Rx OCR",
                dateLabel = "Just now",
                statusBadge = "Rx Active",
                syncStatus = SyncStatus.PENDING_UPLOAD.name,
                createdAt = now + idx
            )
            addHealthRecordOffline(rec)
        }

        val reminderNotif = NotificationEntity(
            id = "NT-${UUID.randomUUID().toString().take(6)}",
            title = "Medicine Schedule Set: ${patient.fullName}",
            message = medications.joinToString(", ") { "${it.name} (${it.frequency})" }.take(580),
            type = NotificationType.MEDICINE.name,
            timestampLabel = "Just now",
            isRead = false,
            patientId = patient.id,
            createdAt = now
        )
        notificationDao.insertNotification(reminderNotif)

        activityDao.insertActivity(
            ActivityEntity(
                id = "AC-${UUID.randomUUID().toString().take(6)}",
                title = "Rx saved for ${patient.fullName}",
                subtitle = "${medications.size} medication(s) & dose reminder added",
                timestampLabel = "Just now",
                categoryTag = "Medicine",
                createdAt = now
            )
        )

        val prefs = preferencesFlow.first()
        val uid = Firebase.auth.currentUser?.uid
        if (!prefs.isOfflineOnlyMode && uid != null) {
            firestoreRepository.upsertNotification(uid, reminderNotif.toFirestoreDoc())
        }
    }

    suspend fun toggleCriticalPatient(patient: PatientEntity) {
        val now = System.currentTimeMillis()
        val nextCritical = !patient.isCritical
        val nextRisk = if (nextCritical) RiskLevel.CRITICAL.name else RiskLevel.MODERATE.name
        patientDao.updateCriticalStatus(
            patientId = patient.id,
            isCritical = nextCritical,
            riskLevel = nextRisk,
            syncStatus = SyncStatus.PENDING_UPLOAD.name,
            updatedAt = now
        )
        val queueId = "SQ-${UUID.randomUUID().toString().take(6)}"
        syncQueueDao.enqueue(
            SyncQueueEntity(
                id = queueId,
                entityType = "Triage Flag",
                entityId = patient.id,
                patientName = patient.fullName,
                operation = "UPDATE",
                payloadSummary = if (nextCritical) "Marked Critical Priority" else "Cleared Critical Flag",
                createdAtLabel = "Just now",
                createdAt = now,
                status = SyncQueueStatus.QUEUED.name
            )
        )
        val prefs = preferencesFlow.first()
        val uid = Firebase.auth.currentUser?.uid
        if (nextCritical) {
            val notif = NotificationEntity(
                id = "NT-${UUID.randomUUID().toString().take(6)}",
                title = "Critical Patient Tagged: ${patient.fullName}",
                message = "Tagged as Critical priority in CareSync Emergency queue.",
                type = NotificationType.EMERGENCY.name,
                timestampLabel = "Just now",
                isRead = false,
                patientId = patient.id,
                createdAt = now
            )
            notificationDao.insertNotification(notif)
            if (!prefs.isOfflineOnlyMode && uid != null) {
                firestoreRepository.upsertNotification(uid, notif.toFirestoreDoc())
            }
        }
        if (!prefs.isOfflineOnlyMode && uid != null) {
            val res = firestoreRepository.updatePatientCriticalStatus(uid, patient.id, nextCritical, nextRisk)
            if (res.isSuccess) {
                patientDao.updateSyncStatus(patient.id, SyncStatus.SYNCED.name)
                syncQueueDao.deleteItem(queueId)
            }
        } else {
            CareSyncSyncWorker.enqueueWhenConnected(appContext)
        }
    }

    suspend fun triggerEmergencySos(patientName: String? = null) {
        val now = System.currentTimeMillis()
        val label = patientName ?: "Field Emergency Dispatch"
        val notif = NotificationEntity(
            id = "NT-${UUID.randomUUID().toString().take(6)}",
            title = "SOS Alert Dispatched",
            message = "CareSync Emergency SOS triggered for $label. District ambulance & PHC alerted.",
            type = NotificationType.EMERGENCY.name,
            timestampLabel = "Just now",
            isRead = false,
            createdAt = now
        )
        notificationDao.insertNotification(notif)
        activityDao.insertActivity(
            ActivityEntity(
                id = "AC-${UUID.randomUUID().toString().take(6)}",
                title = "SOS Emergency Triggered",
                subtitle = "Priority dispatch logged for $label",
                timestampLabel = "Just now",
                categoryTag = "Emergency",
                createdAt = now
            )
        )
        val prefs = preferencesFlow.first()
        val uid = Firebase.auth.currentUser?.uid
        if (!prefs.isOfflineOnlyMode && uid != null) {
            firestoreRepository.upsertNotification(uid, notif.toFirestoreDoc())
        }
    }

    suspend fun syncPendingQueue(): Result<Int> {
        val prefs = preferencesFlow.first()
        return syncEngine.runSync(prefs.isOfflineOnlyMode)
    }

    suspend fun markNotificationRead(id: String) {
        notificationDao.markAsRead(id)
        val prefs = preferencesFlow.first()
        val uid = Firebase.auth.currentUser?.uid
        if (!prefs.isOfflineOnlyMode && uid != null) {
            firestoreRepository.markNotificationRead(uid, id)
        }
    }

    suspend fun markAllNotificationsRead() {
        val unreadIds = notificationDao.getAllNotificationsOnce().filter { !it.isRead }.map { it.id }
        notificationDao.markAllAsRead()
        val prefs = preferencesFlow.first()
        val uid = Firebase.auth.currentUser?.uid
        if (!prefs.isOfflineOnlyMode && uid != null && unreadIds.isNotEmpty()) {
            firestoreRepository.markAllNotificationsRead(uid, unreadIds)
        }
    }

    suspend fun addEmergencyContact(name: String, role: String, phone: String) {
        val contact = EmergencyContact(
            id = "EC-C-${UUID.randomUUID().toString().take(6)}",
            name = name.trim(),
            role = role.trim().ifBlank { "Custom Emergency Contact" },
            phone = phone.trim(),
            isPrimary = false
        )
        val updated = _customContacts.value + contact
        _customContacts.value = updated
        saveCustomContactsToPrefs(updated)
    }

    private suspend fun loadCustomContactsFromPrefs() {
        val raw = dataStore.data.first()[Keys.CUSTOM_CONTACTS].orEmpty()
        if (raw.isBlank()) {
            _customContacts.value = emptyList()
            return
        }
        val list = runCatching {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { idx ->
                val obj = arr.getJSONObject(idx)
                EmergencyContact(
                    id = obj.optString("id"),
                    name = obj.optString("name"),
                    role = obj.optString("role"),
                    phone = obj.optString("phone"),
                    isPrimary = obj.optBoolean("isPrimary", false)
                )
            }
        }.getOrDefault(emptyList())
        _customContacts.value = list
    }

    private suspend fun saveCustomContactsToPrefs(contacts: List<EmergencyContact>) {
        val arr = JSONArray()
        contacts.forEach { c ->
            arr.put(
                JSONObject().apply {
                    put("id", c.id)
                    put("name", c.name)
                    put("role", c.role)
                    put("phone", c.phone)
                    put("isPrimary", c.isPrimary)
                }
            )
        }
        dataStore.edit { it[Keys.CUSTOM_CONTACTS] = arr.toString() }
    }

    fun getEmergencyContacts(): List<EmergencyContact> = listOf(
        EmergencyContact("EC-1", "National Medical Helpline", "24x7 Ambulance Dispatch", "108", true),
        EmergencyContact("EC-2", "Dr. Arvind Mehta", "PHC Duty Medical Officer", "+91 98100 22310", true),
        EmergencyContact("EC-3", "District Maternal ICU", "Obstetric Emergency Desk", "+91 98100 44120"),
        EmergencyContact("EC-4", "CareSync Field Supervisor", "Regional Triage Coordinator", "+91 98100 77890")
    ) + _customContacts.value

    fun getNearbyHealthCenters(): List<NearbyHealthCenter> = listOf(
        NearbyHealthCenter("HC-1", "Rampur Primary Health Center (PHC)", "24x7 Emergency + Maternity", "2.4 km", "4 Beds Open · O2 Ready", "+91 11 2658 1001", 28.6289, 77.2195),
        NearbyHealthCenter("HC-2", "CareSync Partner District Hospital", "ICU · NICU · Trauma", "6.8 km", "11 Beds Open · Blood Bank", "+91 11 2658 2200", 28.6458, 77.2410),
        NearbyHealthCenter("HC-3", "Green Park Community Clinic", "Outpatient & Immunization", "3.9 km", "Open till 8:00 PM", "+91 11 2658 3450", 28.5985, 77.2045)
    )

    fun getEmergencyInstructions(): List<EmergencyInstruction> = listOf(
        EmergencyInstruction(
            "EI-1",
            "Severe Breathing Distress / Low SpO2 (<92%)",
            "CRITICAL",
            listOf(
                "Seat patient upright at 45-90 degrees; loosen restrictive clothing.",
                "Clear airway and initiate portable oxygen at 4-6 L/min if available.",
                "Trigger CareSync SOS and monitor SpO2 every 2 minutes during transport."
            )
        ),
        EmergencyInstruction(
            "EI-2",
            "Obstetric / Postpartum Warning Signs",
            "MATERNAL",
            listOf(
                "Check BP immediately; treat >=160/110 or severe headache/vision blur as emergency.",
                "Position mother on left lateral side to optimize uteroplacental perfusion.",
                "Keep IV access kit & referral slip ready; alert Maternal ICU via SOS."
            )
        ),
        EmergencyInstruction(
            "EI-3",
            "Pediatric High Fever & Febrile Seizure",
            "PEDIATRIC",
            listOf(
                "Place child safely on side on a soft surface; do not restrain or insert objects in mouth.",
                "Time the episode; if >5 minutes, initiate emergency referral immediately.",
                "Perform lukewarm sponging and verify weight-appropriate antipyretic dose."
            )
        ),
        EmergencyInstruction(
            "EI-4",
            "Adult Basic Life Support (CPR)",
            "CARDIAC",
            listOf(
                "Verify responsiveness and breathing; call 108 / trigger SOS immediately.",
                "Start firm chest compressions at center of chest (100-120/min, ~5cm depth).",
                "Continue 30 compressions to 2 rescue breaths until emergency team takes over."
            )
        )
    )

    suspend fun getUserProfile(): UserProfile {
        val p = preferencesFlow.first()
        return UserProfile(
            name = p.userName,
            role = p.role,
            workerId = p.workerId,
            clinicName = p.clinicName,
            district = p.district,
            phone = p.phone,
            email = p.userEmail
        )
    }
}
