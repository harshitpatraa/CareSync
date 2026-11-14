package com.caresync.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caresync.app.database.NotificationEntity
import com.caresync.app.database.PatientEntity
import com.caresync.app.database.SyncQueueEntity
import com.caresync.app.model.AppLanguage
import com.caresync.app.model.EmergencyContact
import com.caresync.app.model.EmergencyInstruction
import com.caresync.app.model.NearbyHealthCenter
import com.caresync.app.model.UserProfile
import com.caresync.app.network.SyncState
import com.caresync.app.repository.AppPreferences
import com.caresync.app.repository.CareSyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val contacts: List<EmergencyContact> = emptyList(),
    val healthCenters: List<NearbyHealthCenter> = emptyList(),
    val instructions: List<EmergencyInstruction> = emptyList()
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
    private val repository: CareSyncRepository
) : ViewModel() {

    private val _sosTriggered = MutableStateFlow(false)
    private val _lastSosPatient = MutableStateFlow<String?>(null)

    val emergencyState: StateFlow<EmergencyUiState> = combine(
        repository.allPatientsFlow,
        _sosTriggered,
        _lastSosPatient
    ) { patients, triggered, sosPatient ->
        EmergencyUiState(
            sosTriggered = triggered,
            lastSosPatient = sosPatient,
            criticalPatients = patients.filter { it.isCritical },
            contacts = repository.getEmergencyContacts(),
            healthCenters = repository.getNearbyHealthCenters(),
            instructions = repository.getEmergencyInstructions()
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
                role = "Senior Community Health Officer",
                workerId = prefs.workerId,
                clinicName = prefs.clinicName,
                district = "North Health Circle · Zone B",
                phone = "+91 98111 70409",
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

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { repository.setLanguage(language) }
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

    fun clearSyncResultMessage() {
        _syncResultMessage.value = null
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            onDone()
        }
    }
}
