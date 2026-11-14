package com.caresync.app.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.caresync.app.database.ActivityDao
import com.caresync.app.database.ActivityEntity
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
import com.caresync.app.model.SyncQueueStatus
import com.caresync.app.model.SyncStatus
import com.caresync.app.model.UserProfile
import com.caresync.app.network.SyncEngine
import com.caresync.app.network.SyncState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

data class AppPreferences(
    val isDarkMode: Boolean = false,
    val isOfflineOnlyMode: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isSecurityLockEnabled: Boolean = true,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val userName: String = "Dr. Priya Kulkarni",
    val userEmail: String = "priya.kulkarni@caresync.org",
    val workerId: String = "CS-CHW-409",
    val clinicName: String = "CareSync Community Health Hub · Ward 4",
    val geminiApiKey: String = ""
)

@Singleton
class CareSyncRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val seeder: DatabaseSeeder,
    private val patientDao: PatientDao,
    private val vitalsDao: VitalsDao,
    private val healthRecordDao: HealthRecordDao,
    private val syncQueueDao: SyncQueueDao,
    private val notificationDao: NotificationDao,
    private val activityDao: ActivityDao,
    private val syncEngine: SyncEngine
) {
    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val OFFLINE_ONLY = booleanPreferencesKey("offline_only")
        val LOGGED_IN = booleanPreferencesKey("logged_in")
        val SECURITY_LOCK = booleanPreferencesKey("security_lock")
        val LANGUAGE = stringPreferencesKey("language")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
    }

    val preferencesFlow: Flow<AppPreferences> = dataStore.data.map { prefs ->
        AppPreferences(
            isDarkMode = prefs[Keys.DARK_MODE] ?: false,
            isOfflineOnlyMode = prefs[Keys.OFFLINE_ONLY] ?: false,
            isLoggedIn = prefs[Keys.LOGGED_IN] ?: false,
            isSecurityLockEnabled = prefs[Keys.SECURITY_LOCK] ?: true,
            language = AppLanguage.entries.find { it.code == prefs[Keys.LANGUAGE] } ?: AppLanguage.ENGLISH,
            userName = prefs[Keys.USER_NAME] ?: "Dr. Priya Kulkarni",
            userEmail = prefs[Keys.USER_EMAIL] ?: "priya.kulkarni@caresync.org",
            geminiApiKey = prefs[Keys.GEMINI_API_KEY] ?: ""
        )
    }

    val allPatientsFlow: Flow<List<PatientEntity>> = patientDao.observeAllPatients()
    val allVitalsFlow: Flow<List<VitalsEntity>> = vitalsDao.observeAllVitals()
    val allRecordsFlow: Flow<List<HealthRecordEntity>> = healthRecordDao.observeAllRecords()
    val syncQueueFlow: Flow<List<SyncQueueEntity>> = syncQueueDao.observeSyncQueue()
    val notificationsFlow: Flow<List<NotificationEntity>> = notificationDao.observeAllNotifications()
    val recentActivitiesFlow: Flow<List<ActivityEntity>> = activityDao.observeRecentActivities()
    val syncStateFlow: StateFlow<SyncState> get() = syncEngine.syncState

    fun observePatient(patientId: String): Flow<PatientEntity?> = patientDao.observePatientById(patientId)
    fun observeVitalsForPatient(patientId: String): Flow<List<VitalsEntity>> = vitalsDao.observeVitalsForPatient(patientId)
    fun observeRecordsForPatient(patientId: String): Flow<List<HealthRecordEntity>> = healthRecordDao.observeRecordsForPatient(patientId)

    suspend fun ensureSeeded() {
        seeder.seedIfEmpty()
    }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[Keys.DARK_MODE] = enabled }
    }

    suspend fun setOfflineOnlyMode(enabled: Boolean) {
        dataStore.edit { it[Keys.OFFLINE_ONLY] = enabled }
    }

    suspend fun setSecurityLock(enabled: Boolean) {
        dataStore.edit { it[Keys.SECURITY_LOCK] = enabled }
    }

    suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { it[Keys.LANGUAGE] = language.code }
    }

    suspend fun setGeminiApiKey(apiKey: String) {
        dataStore.edit { it[Keys.GEMINI_API_KEY] = apiKey.trim() }
    }

    suspend fun login(email: String, name: String? = null) {
        dataStore.edit { prefs ->
            prefs[Keys.LOGGED_IN] = true
            if (email.isNotBlank()) prefs[Keys.USER_EMAIL] = email
            if (!name.isNullOrBlank()) prefs[Keys.USER_NAME] = name
        }
    }

    suspend fun logout() {
        dataStore.edit { it[Keys.LOGGED_IN] = false }
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

        if (initialVitals != null) {
            vitalsDao.insertVitals(initialVitals.copy(syncStatus = SyncStatus.PENDING_UPLOAD.name))
        }

        healthRecordDao.insertRecord(
            HealthRecordEntity(
                id = "HR-${UUID.randomUUID().toString().take(6)}",
                patientId = offlinePatient.id,
                patientName = offlinePatient.fullName,
                recordType = RecordType.TIMELINE.name,
                title = if (isNew) "Patient Registered Offline" else "Patient Record Updated",
                subtitle = "${offlinePatient.category} · ${offlinePatient.villageOrWard}",
                detail = offlinePatient.notes.ifBlank { "Saved securely to local Room storage" },
                dateLabel = "Just now",
                statusBadge = "Saved Offline",
                syncStatus = SyncStatus.PENDING_UPLOAD.name,
                createdAt = now
            )
        )

        syncQueueDao.enqueue(
            SyncQueueEntity(
                id = "SQ-${UUID.randomUUID().toString().take(6)}",
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

        activityDao.insertActivity(
            ActivityEntity(
                id = "AC-${UUID.randomUUID().toString().take(6)}",
                title = if (isNew) "Registered ${offlinePatient.fullName}" else "Updated ${offlinePatient.fullName}",
                subtitle = "Saved offline · Queued for CareSync upload",
                timestampLabel = "Just now",
                categoryTag = offlinePatient.category,
                createdAt = now
            )
        )
    }

    suspend fun addVitalsOffline(patientId: String, patientName: String, vitals: VitalsEntity) {
        val now = System.currentTimeMillis()
        val pendingVitals = vitals.copy(
            recordedAtMillis = now,
            syncStatus = SyncStatus.PENDING_UPLOAD.name
        )
        vitalsDao.insertVitals(pendingVitals)
        patientDao.updateSyncStatus(patientId, SyncStatus.PENDING_UPLOAD.name)

        healthRecordDao.insertRecord(
            HealthRecordEntity(
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
        )

        syncQueueDao.enqueue(
            SyncQueueEntity(
                id = "SQ-${UUID.randomUUID().toString().take(6)}",
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
    }

    suspend fun addHealthRecordOffline(record: HealthRecordEntity) {
        val now = System.currentTimeMillis()
        val offlineRecord = record.copy(
            syncStatus = SyncStatus.PENDING_UPLOAD.name,
            createdAt = now
        )
        healthRecordDao.insertRecord(offlineRecord)
        patientDao.updateSyncStatus(record.patientId, SyncStatus.PENDING_UPLOAD.name)

        syncQueueDao.enqueue(
            SyncQueueEntity(
                id = "SQ-${UUID.randomUUID().toString().take(6)}",
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
        syncQueueDao.enqueue(
            SyncQueueEntity(
                id = "SQ-${UUID.randomUUID().toString().take(6)}",
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
        if (nextCritical) {
            notificationDao.insertNotification(
                NotificationEntity(
                    id = "NT-${UUID.randomUUID().toString().take(6)}",
                    title = "Critical Patient Tagged: ${patient.fullName}",
                    message = "Tagged as Critical priority in CareSync Emergency queue.",
                    type = NotificationType.EMERGENCY.name,
                    timestampLabel = "Just now",
                    isRead = false,
                    patientId = patient.id,
                    createdAt = now
                )
            )
        }
    }

    suspend fun triggerEmergencySos(patientName: String? = null) {
        val now = System.currentTimeMillis()
        val label = patientName ?: "Field Emergency Dispatch"
        notificationDao.insertNotification(
            NotificationEntity(
                id = "NT-${UUID.randomUUID().toString().take(6)}",
                title = "SOS Alert Dispatched",
                message = "CareSync Emergency SOS triggered for $label. District ambulance & PHC alerted.",
                type = NotificationType.EMERGENCY.name,
                timestampLabel = "Just now",
                isRead = false,
                createdAt = now
            )
        )
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
    }

    suspend fun syncPendingQueue(): Result<Int> {
        val prefs = preferencesFlow.first()
        return syncEngine.runSync(prefs.isOfflineOnlyMode)
    }

    suspend fun markNotificationRead(id: String) = notificationDao.markAsRead(id)
    suspend fun markAllNotificationsRead() = notificationDao.markAllAsRead()

    fun getEmergencyContacts(): List<EmergencyContact> = listOf(
        EmergencyContact("EC-1", "National Medical Helpline", "24x7 Ambulance Dispatch", "108", true),
        EmergencyContact("EC-2", "Dr. Arvind Mehta", "PHC Duty Medical Officer", "+91 98100 22310", true),
        EmergencyContact("EC-3", "District Maternal ICU", "Obstetric Emergency Desk", "+91 98100 44120"),
        EmergencyContact("EC-4", "CareSync Field Supervisor", "Regional Triage Coordinator", "+91 98100 77890")
    )

    fun getNearbyHealthCenters(): List<NearbyHealthCenter> = listOf(
        NearbyHealthCenter("HC-1", "Rampur Primary Health Center (PHC)", "24x7 Emergency + Maternity", "2.4 km", "4 Beds Open · O2 Ready", "+91 11 2658 1001"),
        NearbyHealthCenter("HC-2", "CareSync Partner District Hospital", "ICU · NICU · Trauma", "6.8 km", "11 Beds Open · Blood Bank", "+91 11 2658 2200"),
        NearbyHealthCenter("HC-3", "Green Park Community Clinic", "Outpatient & Immunization", "3.9 km", "Open till 8:00 PM", "+91 11 2658 3450")
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
            role = "Senior Community Health Officer",
            workerId = p.workerId,
            clinicName = p.clinicName,
            district = "North Health Circle · Zone B",
            phone = "+91 98111 70409",
            email = p.userEmail
        )
    }
}
