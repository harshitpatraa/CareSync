package com.caresync.app.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caresync.app.database.HealthRecordEntity
import com.caresync.app.database.PatientEntity
import com.caresync.app.database.VitalsEntity
import com.caresync.app.model.RecordType
import com.caresync.app.model.SyncStatus
import com.caresync.app.repository.AppPreferences
import com.caresync.app.repository.CareSyncRepository
import com.caresync.app.utils.PdfReferralGenerator
import com.caresync.app.utils.TtsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
    val records: List<HealthRecordEntity> = emptyList(),
    val criticalVitalsDetected: Boolean = false,
    val clinicianName: String = "Dr. Priya Kulkarni",
    val clinicName: String = "CareSync Community Health Hub",
    val isSpeaking: Boolean = false
)

@HiltViewModel
class PatientDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: CareSyncRepository,
    private val ttsManager: TtsManager
) : ViewModel() {

    private val patientId: String = savedStateHandle.get<String>("patientId") ?: "PT-1001"
    private val criticalVitalsFlag = MutableStateFlow(false)
    private val latestPrefs = MutableStateFlow(AppPreferences())

    val uiState: StateFlow<PatientDetailUiState> = combine(
        repository.observePatient(patientId),
        repository.observeVitalsForPatient(patientId),
        repository.observeRecordsForPatient(patientId),
        repository.preferencesFlow,
        criticalVitalsFlag,
        ttsManager.isSpeaking
    ) { values ->
        val patient = values[0] as PatientEntity?
        @Suppress("UNCHECKED_CAST")
        val vitals = values[1] as List<VitalsEntity>
        @Suppress("UNCHECKED_CAST")
        val records = values[2] as List<HealthRecordEntity>
        val prefs = values[3] as AppPreferences
        val criticalFlag = values[4] as Boolean
        val speaking = values[5] as Boolean
        latestPrefs.value = prefs

        PatientDetailUiState(
            patient = patient,
            vitalsHistory = vitals,
            records = records,
            criticalVitalsDetected = criticalFlag,
            clinicianName = prefs.userName,
            clinicName = prefs.clinicName,
            isSpeaking = speaking
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
            val isCritical = repository.addVitalsOffline(
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
            if (isCritical) {
                criticalVitalsFlag.value = true
            }
        }
    }

    fun dismissCriticalVitalsBanner() {
        criticalVitalsFlag.value = false
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

    fun deletePatient(onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deletePatient(patientId)
            onDeleted()
        }
    }

    fun generateAndShareReferralPdf(context: Context, onReady: (Uri) -> Unit) {
        val state = uiState.value
        val patient = state.patient ?: return
        viewModelScope.launch {
            val uri = PdfReferralGenerator.generateReferralPdf(
                context = context,
                patient = patient,
                latestVitals = state.vitalsHistory.firstOrNull(),
                records = state.records,
                clinicianName = state.clinicianName,
                clinicName = state.clinicName
            )
            onReady(uri)
        }
    }

    fun speakPatientBrief() {
        val state = uiState.value
        val patient = state.patient ?: return
        val latest = state.vitalsHistory.firstOrNull()
        val text = buildString {
            append("${patient.fullName}, age ${patient.age}, ${patient.gender}, blood group ${patient.bloodGroup}. ")
            append("Allergies: ${patient.allergies}. ")
            if (latest != null) {
                append("Latest vitals: blood pressure ${latest.systolicBp} over ${latest.diastolicBp}, oxygen saturation ${latest.spo2Percent} percent, temperature ${latest.temperatureC} degrees.")
            }
        }
        ttsManager.speak(text, latestPrefs.value.language)
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }
}
