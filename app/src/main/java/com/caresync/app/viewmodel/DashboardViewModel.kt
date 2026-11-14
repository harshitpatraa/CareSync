package com.caresync.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caresync.app.database.ActivityEntity
import com.caresync.app.database.HealthRecordEntity
import com.caresync.app.database.NotificationEntity
import com.caresync.app.database.PatientEntity
import com.caresync.app.database.SyncQueueEntity
import com.caresync.app.database.VitalsEntity
import com.caresync.app.model.PatientCategory
import com.caresync.app.model.RiskLevel
import com.caresync.app.model.SyncStatus
import com.caresync.app.model.WardOutbreakInsight
import com.caresync.app.network.AiMedicalEngine
import com.caresync.app.network.SyncState
import com.caresync.app.repository.AppPreferences
import com.caresync.app.repository.CareSyncRepository
import com.caresync.app.utils.TtsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
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
    val weeklyVisitsTrend: List<Pair<String, Int>> = emptyList(),
    val criticalPatients: List<PatientEntity> = emptyList(),
    val recentActivities: List<ActivityEntity> = emptyList(),
    val pendingQueue: List<SyncQueueEntity> = emptyList(),
    val unreadAlertsCount: Int = 0,
    val syncState: SyncState = SyncState(),
    val isOfflineOnly: Boolean = false,
    val syncFeedbackMessage: String? = null,
    val outbreakInsight: WardOutbreakInsight? = null,
    val isScanningOutbreak: Boolean = false,
    val outbreakError: String? = null,
    val isSpeaking: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: CareSyncRepository,
    private val aiEngine: AiMedicalEngine,
    private val ttsManager: TtsManager
) : ViewModel() {

    private val syncFeedback = MutableStateFlow<String?>(null)
    private val outbreakInsight = MutableStateFlow<WardOutbreakInsight?>(
        WardOutbreakInsight(
            alertLevel = RiskLevel.HIGH,
            headline = "Ward 4 & 6 Febrile & Dengue Surveillance Alert",
            summary = "Epidemiological cohort radar detected acute febrile clusters in Ward 6 and borderline gestational hypertension cases in Ward 4.",
            affectedWards = listOf("Ward 6 · Shivaji Chowk", "Ward 4 · Rampur East", "Ward 7 · Old Mill Colony"),
            recommendations = listOf(
                "Prioritize doorstep follow-up for febrile cases with platelet checks & ORS distribution.",
                "Verify BP stabilization for antenatal patients in Ward 4.",
                "Coordinate vector-control larvicidal spray with municipal sanitation cell."
            ),
            isOffline = false
        )
    )
    private val isScanningOutbreak = MutableStateFlow(false)
    private val outbreakError = MutableStateFlow<String?>(null)

    private val latestPatients = MutableStateFlow<List<PatientEntity>>(emptyList())
    private val latestActivities = MutableStateFlow<List<ActivityEntity>>(emptyList())
    private val latestPrefs = MutableStateFlow(AppPreferences())

    init {
        viewModelScope.launch {
            kotlinx.coroutines.delay(600L)
            scanWardOutbreak()
        }
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.allPatientsFlow,
        repository.allVitalsFlow,
        repository.allRecordsFlow,
        repository.recentActivitiesFlow,
        repository.syncQueueFlow,
        repository.notificationsFlow,
        repository.preferencesFlow,
        repository.syncStateFlow,
        syncFeedback,
        outbreakInsight,
        isScanningOutbreak,
        outbreakError,
        ttsManager.isSpeaking
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val patients = values[0] as List<PatientEntity>
        @Suppress("UNCHECKED_CAST")
        val vitals = values[1] as List<VitalsEntity>
        @Suppress("UNCHECKED_CAST")
        val records = values[2] as List<HealthRecordEntity>
        @Suppress("UNCHECKED_CAST")
        val activities = values[3] as List<ActivityEntity>
        @Suppress("UNCHECKED_CAST")
        val queue = values[4] as List<SyncQueueEntity>
        @Suppress("UNCHECKED_CAST")
        val notifications = values[5] as List<NotificationEntity>
        val prefs = values[6] as AppPreferences
        val syncState = values[7] as SyncState
        val feedback = values[8] as String?
        val outbreak = values[9] as WardOutbreakInsight?
        val scanning = values[10] as Boolean
        val oError = values[11] as String?
        val speaking = values[12] as Boolean

        latestPatients.value = patients
        latestActivities.value = activities
        latestPrefs.value = prefs

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
            weeklyVisitsTrend = computeWeeklyTrend(vitals, records, activities),
            criticalPatients = patients.filter { it.isCritical },
            recentActivities = activities,
            pendingQueue = queue,
            unreadAlertsCount = notifications.count { !it.isRead },
            syncState = syncState,
            isOfflineOnly = prefs.isOfflineOnlyMode,
            syncFeedbackMessage = feedback,
            outbreakInsight = outbreak,
            isScanningOutbreak = scanning,
            outbreakError = oError,
            isSpeaking = speaking
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    private fun computeWeeklyTrend(
        vitals: List<VitalsEntity>,
        records: List<HealthRecordEntity>,
        activities: List<ActivityEntity>
    ): List<Pair<String, Int>> {
        val dayFmt = SimpleDateFormat("EEE", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        val buckets = LinkedHashMap<Long, Pair<String, Int>>()
        for (offset in 6 downTo 0) {
            val dayCal = (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -offset) }
            buckets[dayCal.timeInMillis] = dayFmt.format(dayCal.time) to 0
        }

        val allTimestamps = vitals.map { it.recordedAtMillis } +
            records.map { it.createdAt } +
            activities.map { it.createdAt }

        val oneDayMs = 86_400_000L
        for (ts in allTimestamps) {
            for (dayStart in buckets.keys) {
                if (ts >= dayStart && ts < dayStart + oneDayMs) {
                    val existing = buckets[dayStart]!!
                    buckets[dayStart] = existing.first to (existing.second + 1)
                    break
                }
            }
        }
        return buckets.values.toList()
    }

    fun scanWardOutbreak() {
        if (isScanningOutbreak.value) return
        viewModelScope.launch {
            isScanningOutbreak.value = true
            outbreakError.value = null
            try {
                val insight = aiEngine.analyzeWardOutbreakTrends(
                    patients = latestPatients.value,
                    recentActivities = latestActivities.value,
                    language = latestPrefs.value.language,
                    offlineOnly = latestPrefs.value.isOfflineOnlyMode
                )
                outbreakInsight.value = insight
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                outbreakError.value = e.localizedMessage ?: "Failed to scan ward trends."
            } finally {
                isScanningOutbreak.value = false
            }
        }
    }

    fun speakOutbreakSummary() {
        val insight = outbreakInsight.value ?: return
        val text = "${insight.headline}. ${insight.summary}. Recommendations: ${insight.recommendations.joinToString(". ")}"
        ttsManager.speak(text, latestPrefs.value.language)
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

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
