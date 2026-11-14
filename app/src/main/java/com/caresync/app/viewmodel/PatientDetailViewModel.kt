package com.caresync.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caresync.app.database.HealthRecordEntity
import com.caresync.app.database.PatientEntity
import com.caresync.app.database.VitalsEntity
import com.caresync.app.model.RecordType
import com.caresync.app.model.SyncStatus
import com.caresync.app.repository.CareSyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class PatientDetailUiState(
    val patient: PatientEntity? = null,
    val vitalsHistory: List<VitalsEntity> = emptyList(),
    val records: List<HealthRecordEntity> = emptyList()
)

@HiltViewModel
class PatientDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: CareSyncRepository
) : ViewModel() {

    private val patientId: String = savedStateHandle.get<String>("patientId") ?: "PT-1001"

    val uiState: StateFlow<PatientDetailUiState> = combine(
        repository.observePatient(patientId),
        repository.observeVitalsForPatient(patientId),
        repository.observeRecordsForPatient(patientId)
    ) { patient, vitals, records ->
        PatientDetailUiState(
            patient = patient,
            vitalsHistory = vitals,
            records = records
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PatientDetailUiState()
    )

    fun toggleCritical() {
        val current = uiState.value.patient ?: return
        viewModelScope.launch {
            repository.toggleCriticalPatient(current)
        }
    }

    fun recordNewVitals(
        systolic: Int,
        diastolic: Int,
        heartRate: Int,
        tempC: Float,
        spo2: Int,
        bloodSugar: Int
    ) {
        val current = uiState.value.patient ?: return
        viewModelScope.launch {
            repository.addVitalsOffline(
                patientId = current.id,
                patientName = current.fullName,
                vitals = VitalsEntity(
                    id = "VT-${UUID.randomUUID().toString().take(6)}",
                    patientId = current.id,
                    systolicBp = systolic,
                    diastolicBp = diastolic,
                    heartRate = heartRate,
                    temperatureC = tempC,
                    spo2Percent = spo2,
                    respiratoryRate = 18,
                    bloodSugarMgDl = bloodSugar,
                    weightKg = if (current.childWeightKg > 0f) current.childWeightKg else 62f,
                    recordedAtLabel = "Just now",
                    syncStatus = SyncStatus.PENDING_UPLOAD.name
                )
            )
        }
    }

    fun addRecord(type: RecordType, title: String, subtitle: String, detail: String, statusBadge: String) {
        val current = uiState.value.patient ?: return
        viewModelScope.launch {
            repository.addHealthRecordOffline(
                HealthRecordEntity(
                    id = "HR-${UUID.randomUUID().toString().take(6)}",
                    patientId = current.id,
                    patientName = current.fullName,
                    recordType = type.name,
                    title = title.trim(),
                    subtitle = subtitle.trim(),
                    detail = detail.trim(),
                    dateLabel = "Just now",
                    statusBadge = statusBadge,
                    syncStatus = SyncStatus.PENDING_UPLOAD.name
                )
            )
        }
    }
}
