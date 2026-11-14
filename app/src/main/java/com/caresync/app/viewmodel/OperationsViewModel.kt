package com.caresync.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caresync.app.database.NotificationEntity
import com.caresync.app.database.PatientEntity
import com.caresync.app.database.SyncQueueEntity
import com.caresync.app.database.VitalsEntity
import com.caresync.app.model.AppLanguage
import com.caresync.app.model.EmergencyContact
import com.caresync.app.model.EmergencyInstruction
import com.caresync.app.model.NearbyHealthCenter
import com.caresync.app.model.UserProfile
import com.caresync.app.network.SyncState
import com.caresync.app.network.initCustomFirebaseIfPresent
import com.caresync.app.repository.AppPreferences
import com.caresync.app.repository.CareSyncRepository
import com.caresync.app.utils.FieldCoordinates
import com.caresync.app.utils.LocationHelper
import com.caresync.app.utils.TtsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmergencyUiState(
    val sosTriggered: Boolean = false,
    val lastSosPatient: String? = null,
    val criticalPatients: List<PatientEntity> = emptyList(),
    val latestVitalsByPatient: Map<String, VitalsEntity> = emptyMap(),
    val contacts: List<EmergencyContact> = emptyList(),
    val healthCenters: List<NearbyHealthCenter> = emptyList(),
    val instructions: List<EmergencyInstruction> = emptyList(),
    val fieldCoordinates: FieldCoordinates? = null,
    val clinicianName: String = "Dr. Priya Kulkarni",
    val isSpeaking: Boolean = false
)

data class SettingsUiState(
    val preferences: AppPreferences = AppPreferences(),
    val pendingQueue: List<SyncQueueEntity> = emptyList(),
    val syncState: SyncState = SyncState(),
    val syncedPatientCount: Int = 0,
    val totalPatientCount: Int = 0,
    val profile: UserProfile = UserProfile(
        name = "Dr. Priya Kulkarni",
        role = "Senior Community Health Officer",
        workerId = "CS-CHW-409",
        clinicName = "CareSync Community Health Hub · Ward 4",
        district = "North Health Circle · Zone B",
        phone = "+91 98111 70409",
        email = "priya.kulkarni@caresync.org"
    )
)

@HiltViewModel
class OperationsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val repository: CareSyncRepository,
    private val ttsManager: TtsManager
) : ViewModel() {

    private val _sosTriggered = MutableStateFlow(false)
    private val _lastSosPatient = MutableStateFlow<String?>(null)
    private val _coordinates = MutableStateFlow<FieldCoordinates?>(null)
    private val latestLanguage = MutableStateFlow(AppLanguage.ENGLISH)

    val emergencyState: StateFlow<EmergencyUiState> = combine(
        repository.allPatientsFlow,
        repository.allVitalsFlow,
        repository.customContactsFlow,
        repository.preferencesFlow,
        _sosTriggered,
        _lastSosPatient,
        _coordinates,
        ttsManager.isSpeaking
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val patients = values[0] as List<PatientEntity>
        @Suppress("UNCHECKED_CAST")
        val vitals = values[1] as List<VitalsEntity>
        val prefs = values[3] as AppPreferences
        val triggered = values[4] as Boolean
        val sosPatient = values[5] as String?
        val coords = values[6] as FieldCoordinates?
        val speaking = values[7] as Boolean
        latestLanguage.value = prefs.language

        val vitalsMap = vitals.groupBy { it.patientId }.mapValues { (_, list) -> list.first() }
        val updatedCenters = repository.getNearbyHealthCenters().map { center ->
            center.copy(
                distanceKm = LocationHelper.distanceLabelKm(coords, center.latitude, center.longitude, center.distanceKm)
            )
        }

        EmergencyUiState(
            sosTriggered = triggered,
            lastSosPatient = sosPatient,
            criticalPatients = patients.filter { it.isCritical },
            latestVitalsByPatient = vitalsMap,
            contacts = repository.getEmergencyContacts(),
            healthCenters = updatedCenters,
            instructions = repository.getEmergencyInstructions(),
            fieldCoordinates = coords,
            clinicianName = prefs.userName,
            isSpeaking = speaking
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EmergencyUiState(
            contacts = repository.getEmergencyContacts(),
            healthCenters = repository.getNearbyHealthCenters(),
            instructions = repository.getEmergencyInstructions()
        )
    )

    val notifications: StateFlow<List<NotificationEntity>> =
        repository.notificationsFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val settingsState: StateFlow<SettingsUiState> = combine(
        repository.preferencesFlow,
        repository.syncQueueFlow,
        repository.syncStateFlow,
        repository.allPatientsFlow
    ) { prefs, queue, syncState, patients ->
        SettingsUiState(
            preferences = prefs,
            pendingQueue = queue,
            syncState = syncState,
            syncedPatientCount = patients.count { it.syncStatus == "SYNCED" },
            totalPatientCount = patients.size,
            profile = UserProfile(
                name = prefs.userName,
                role = prefs.role,
                workerId = prefs.workerId,
                clinicName = prefs.clinicName,
                district = prefs.district,
                phone = prefs.phone,
                email = prefs.userEmail
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    private val _syncResultMessage = MutableStateFlow<String?>(null)
    val syncResultMessage: StateFlow<String?> = _syncResultMessage.asStateFlow()

    fun refreshLocation(context: Context) {
        viewModelScope.launch {
            _coordinates.value = LocationHelper.getBestLastLocation(context)
        }
    }

    fun addEmergencyContact(name: String, role: String, phone: String) {
        if (name.isBlank() || phone.isBlank()) return
        viewModelScope.launch {
            repository.addEmergencyContact(name, role, phone)
        }
    }

    fun speakInstruction(text: String) {
        ttsManager.speak(text, latestLanguage.value)
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    fun triggerSos(patientName: String? = null) {
        viewModelScope.launch {
            repository.triggerEmergencySos(patientName)
            _lastSosPatient.value = patientName
            _sosTriggered.value = true
        }
    }

    fun dismissSosBanner() {
        _sosTriggered.value = false
    }

    fun toggleCritical(patient: PatientEntity) {
        viewModelScope.launch { repository.toggleCriticalPatient(patient) }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch { repository.markNotificationRead(id) }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch { repository.markAllNotificationsRead() }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch { repository.setDarkMode(enabled) }
    }

    fun toggleOfflineOnly(enabled: Boolean) {
        viewModelScope.launch { repository.setOfflineOnlyMode(enabled) }
    }

    fun toggleSecurityLock(enabled: Boolean) {
        viewModelScope.launch { repository.setSecurityLock(enabled) }
    }

    fun setSecurityPin(pin: String) {
        viewModelScope.launch { repository.setSecurityPin(pin) }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { repository.setLanguage(language) }
    }

    fun updateGeminiApiKey(apiKey: String) {
        viewModelScope.launch { repository.setGeminiApiKey(apiKey) }
    }

    fun updateFirebaseConfig(projectId: String, appId: String, apiKey: String) {
        viewModelScope.launch {
            repository.setFirebaseCredentials(projectId, appId, apiKey)
            initCustomFirebaseIfPresent(context, projectId, appId, apiKey)
        }
    }

    fun updateProfile(
        name: String,
        role: String,
        workerId: String,
        clinicName: String,
        district: String,
        phone: String
    ) {
        viewModelScope.launch {
            repository.updateUserProfile(name, role, workerId, clinicName, district, phone)
        }
    }

    fun syncNow() {
        viewModelScope.launch {
            val res = repository.syncPendingQueue()
            _syncResultMessage.value = res.fold(
                onSuccess = { count -> if (count == 0) "All records already synced." else "Synced $count queued record(s)." },
                onFailure = { err -> err.message ?: "Offline mode active." }
            )
        }
    }
}
