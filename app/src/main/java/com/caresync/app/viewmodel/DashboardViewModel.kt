package com.caresync.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caresync.app.database.ActivityEntity
import com.caresync.app.database.PatientEntity
import com.caresync.app.database.SyncQueueEntity
import com.caresync.app.model.PatientCategory
import com.caresync.app.model.SyncStatus
import com.caresync.app.network.SyncState
import com.caresync.app.repository.AppPreferences
import com.caresync.app.repository.CareSyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val userName: String = "Dr. Priya Kulkarni",
    val clinicName: String = "CareSync Community Health Hub",
    val todaysPatientsCount: Int = 0,
    val pendingVisitsCount: Int = 0,
    val emergencyCasesCount: Int = 0,
    val followUpsCount: Int = 0,
    val vaccinationAvgProgress: Int = 0,
    val pregnancyCount: Int = 0,
    val childMonitoringCount: Int = 0,
    val totalPatientsCount: Int = 0,
    val syncedCount: Int = 0,
    val pendingSyncCount: Int = 0,
    val weeklyVisitsTrend: List<Pair<String, Int>> = listOf(
        "Mon" to 9, "Tue" to 12, "Wed" to 8, "Thu" to 15, "Fri" to 11, "Sat" to 14, "Sun" to 7
    ),
    val criticalPatients: List<PatientEntity> = emptyList(),
    val recentActivities: List<ActivityEntity> = emptyList(),
    val pendingQueue: List<SyncQueueEntity> = emptyList(),
    val unreadAlertsCount: Int = 0,
    val syncState: SyncState = SyncState(),
    val isOfflineOnly: Boolean = false,
    val syncFeedbackMessage: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: CareSyncRepository
) : ViewModel() {

    private val syncFeedback = MutableStateFlow<String?>(null)

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.allPatientsFlow,
        repository.recentActivitiesFlow,
        repository.syncQueueFlow,
        repository.notificationsFlow,
        repository.preferencesFlow,
        repository.syncStateFlow,
        syncFeedback
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val patients = values[0] as List<PatientEntity>
        @Suppress("UNCHECKED_CAST")
        val activities = values[1] as List<ActivityEntity>
        @Suppress("UNCHECKED_CAST")
        val queue = values[2] as List<SyncQueueEntity>
        @Suppress("UNCHECKED_CAST")
        val notifications = values[3] as List<com.caresync.app.database.NotificationEntity>
        val prefs = values[4] as AppPreferences
        val syncState = values[5] as SyncState
        val feedback = values[6] as String?

        val avgVax = if (patients.isEmpty()) 0 else patients.map { it.vaccinationProgress }.average().toInt()
        val synced = patients.count { it.syncStatus == SyncStatus.SYNCED.name }

        DashboardUiState(
            userName = prefs.userName,
            clinicName = prefs.clinicName,
            todaysPatientsCount = patients.count { it.isTodayVisit },
            pendingVisitsCount = patients.count { it.isPendingVisit },
            emergencyCasesCount = patients.count { it.isCritical },
            followUpsCount = patients.count { it.nextFollowUpDate.isNotBlank() },
            vaccinationAvgProgress = avgVax,
            pregnancyCount = patients.count { it.category == PatientCategory.PREGNANCY.name },
            childMonitoringCount = patients.count { it.category == PatientCategory.CHILD.name },
            totalPatientsCount = patients.size,
            syncedCount = synced,
            pendingSyncCount = queue.size,
            criticalPatients = patients.filter { it.isCritical },
            recentActivities = activities,
            pendingQueue = queue,
            unreadAlertsCount = notifications.count { !it.isRead },
            syncState = syncState,
            isOfflineOnly = prefs.isOfflineOnlyMode,
            syncFeedbackMessage = feedback
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    fun triggerSyncNow() {
        viewModelScope.launch {
            val result = repository.syncPendingQueue()
            syncFeedback.value = result.fold(
                onSuccess = { count ->
                    if (count == 0) "All records are already synchronized."
                    else "Synchronized $count offline record(s) with CareSync."
                },
                onFailure = { err -> err.message ?: "Sync deferred while offline." }
            )
        }
    }

    fun clearSyncFeedback() {
        syncFeedback.value = null
    }
}
