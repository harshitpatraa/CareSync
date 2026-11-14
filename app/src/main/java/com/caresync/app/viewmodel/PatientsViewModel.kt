package com.caresync.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caresync.app.database.HealthRecordEntity
import com.caresync.app.database.PatientEntity
import com.caresync.app.database.VitalsEntity
import com.caresync.app.model.PatientCategory
import com.caresync.app.model.RiskLevel
import com.caresync.app.model.SyncStatus
import com.caresync.app.repository.CareSyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

enum class PatientFilter(val label: String) {
    ALL("All"),
    CRITICAL("Critical"),
    PREGNANCY("Pregnancy"),
    CHILD("Child Care"),
    PENDING_SYNC("Pending Sync"),
    TODAY("Today")
}

data class PatientsUiState(
    val searchQuery: String = "",
    val activeFilter: PatientFilter = PatientFilter.ALL,
    val patients: List<PatientEntity> = emptyList(),
    val allRecords: List<HealthRecordEntity> = emptyList(),
    val totalCount: Int = 0
)

@HiltViewModel
class PatientsViewModel @Inject constructor(
    private val repository: CareSyncRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val activeFilter = MutableStateFlow(PatientFilter.ALL)

    val uiState: StateFlow<PatientsUiState> = combine(
        repository.allPatientsFlow,
        repository.allRecordsFlow,
        searchQuery,
        activeFilter
    ) { patients, records, query, filter ->
        val filtered = patients.filter { p ->
            val matchesQuery = query.isBlank() ||
                p.fullName.contains(query, ignoreCase = true) ||
                p.villageOrWard.contains(query, ignoreCase = true) ||
                p.id.contains(query, ignoreCase = true) ||
                p.chronicConditions.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                PatientFilter.ALL -> true
                PatientFilter.CRITICAL -> p.isCritical
                PatientFilter.PREGNANCY -> p.category == PatientCategory.PREGNANCY.name
                PatientFilter.CHILD -> p.category == PatientCategory.CHILD.name
                PatientFilter.PENDING_SYNC -> p.syncStatus != SyncStatus.SYNCED.name
                PatientFilter.TODAY -> p.isTodayVisit
            }
            matchesQuery && matchesFilter
        }
        PatientsUiState(
            searchQuery = query,
            activeFilter = filter,
            patients = filtered,
            allRecords = records,
            totalCount = patients.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PatientsUiState()
    )

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setFilter(filter: PatientFilter) {
        activeFilter.value = filter
    }

    fun applyInitialFilter(filterKey: String?) {
        if (filterKey.isNullOrBlank()) return
        val matched = PatientFilter.entries.find { it.name.equals(filterKey, ignoreCase = true) }
        if (matched != null) activeFilter.value = matched
    }

    fun toggleCritical(patient: PatientEntity) {
        viewModelScope.launch {
            repository.toggleCriticalPatient(patient)
        }
    }

    fun registerPatientOffline(
        fullName: String,
        age: Int,
        gender: String,
        phone: String,
        villageOrWard: String,
        bloodGroup: String,
        category: PatientCategory,
        isCritical: Boolean,
        allergies: String,
        chronicConditions: String,
        familyHeadName: String,
        familyMembersCount: Int,
        pregnancyWeeks: Int,
        expectedDueDate: String,
        childWeightKg: Float,
        notes: String,
        systolicBp: Int,
        diastolicBp: Int,
        heartRate: Int,
        spo2: Int,
        tempC: Float,
        onSaved: (String) -> Unit
    ) {
        viewModelScope.launch {
            val id = "PT-${(1009..9999).random()}"
            val now = System.currentTimeMillis()
            val riskLevel = when {
                isCritical || spo2 < 92 || systolicBp >= 160 -> RiskLevel.CRITICAL
                category == PatientCategory.PREGNANCY && systolicBp >= 135 -> RiskLevel.HIGH
                tempC >= 38.0f -> RiskLevel.MODERATE
                else -> RiskLevel.LOW
            }
            val patient = PatientEntity(
                id = id,
                fullName = fullName.trim(),
                age = age,
                gender = gender,
                phone = phone.trim().ifBlank { "+91 98000 00000" },
                villageOrWard = villageOrWard.trim().ifBlank { "Ward 4 · Community Hub" },
                bloodGroup = bloodGroup.ifBlank { "O+" },
                category = category.name,
                riskLevel = riskLevel.name,
                isCritical = isCritical || riskLevel == RiskLevel.CRITICAL,
                isTodayVisit = true,
                isPendingVisit = false,
                syncStatus = SyncStatus.PENDING_UPLOAD.name,
                lastVisitLabel = "Just now",
                nextFollowUpDate = "21 Nov 2026",
                allergies = allergies.ifBlank { "None reported" },
                chronicConditions = chronicConditions.ifBlank { "None" },
                familyHeadName = familyHeadName.ifBlank { fullName.trim() },
                familyMembersCount = familyMembersCount.coerceAtLeast(1),
                pregnancyWeeks = pregnancyWeeks,
                expectedDueDate = expectedDueDate,
                childWeightKg = childWeightKg,
                vaccinationProgress = if (category == PatientCategory.CHILD) 80 else 100,
                notes = notes.ifBlank { "Registered offline via CareSync mobile field kit." },
                updatedAt = now
            )
            val vitals = VitalsEntity(
                id = "VT-${UUID.randomUUID().toString().take(6)}",
                patientId = id,
                systolicBp = systolicBp,
                diastolicBp = diastolicBp,
                heartRate = heartRate,
                temperatureC = tempC,
                spo2Percent = spo2,
                respiratoryRate = 18,
                bloodSugarMgDl = 104,
                weightKg = if (childWeightKg > 0f) childWeightKg else 62f,
                recordedAtLabel = "Just now",
                recordedAtMillis = now,
                syncStatus = SyncStatus.PENDING_UPLOAD.name
            )
            repository.savePatientOffline(patient, vitals, isNew = true)
            onSaved(id)
        }
    }
}
