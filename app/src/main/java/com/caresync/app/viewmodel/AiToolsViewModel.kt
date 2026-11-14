package com.caresync.app.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caresync.app.database.PatientEntity
import com.caresync.app.database.VitalsEntity
import com.caresync.app.model.ChatMessage
import com.caresync.app.model.ClinicalAssessmentResult
import com.caresync.app.network.AiMedicalEngine
import com.caresync.app.repository.AppPreferences
import com.caresync.app.repository.CareSyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

private const val TAG = "AiToolsViewModel"

data class AiToolsUiState(
    val selectedSymptoms: Set<String> = emptySet(),
    val durationDays: Int = 2,
    val severity: String = "Moderate",
    val selectedPatientId: String? = null,
    val prefillVitals: VitalsEntity? = null,
    val isEvaluating: Boolean = false,
    val symptomResult: ClinicalAssessmentResult? = null,
    val riskResult: ClinicalAssessmentResult? = null,
    val aiErrorMessage: String? = null,
    val chatMessages: List<ChatMessage> = listOf(
        ChatMessage(
            id = "msg-welcome",
            isUser = false,
            text = "Hello! I am CareSync Clinical Assistant powered by Gemini. Ask about symptom triage, maternal/pediatric guidance, medicine precautions, or sync workflows.",
            timestampLabel = "Gemini AI",
            tags = listOf("Gemini 3.8 Flash", "Streaming")
        )
    ),
    val isChatTyping: Boolean = false,
    val imageAnalysisResult: String? = null,
    val selectedImageUri: Uri? = null,
    val isAnalyzingImage: Boolean = false
)

@HiltViewModel
class AiToolsViewModel @Inject constructor(
    private val repository: CareSyncRepository,
    private val aiEngine: AiMedicalEngine
) : ViewModel() {

    val patients: StateFlow<List<PatientEntity>> = repository.allPatientsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allVitals: StateFlow<List<VitalsEntity>> = repository.allVitalsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val preferences: StateFlow<AppPreferences> = repository.preferencesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppPreferences()
    )

    private val _uiState = MutableStateFlow(AiToolsUiState())
    val uiState: StateFlow<AiToolsUiState> = _uiState.asStateFlow()

    fun toggleSymptom(symptom: String) {
        val current = _uiState.value.selectedSymptoms
        val updated = if (symptom in current) current - symptom else current + symptom
        _uiState.value = _uiState.value.copy(selectedSymptoms = updated, aiErrorMessage = null)
    }

    fun setDurationDays(days: Int) {
        _uiState.value = _uiState.value.copy(durationDays = days.coerceIn(1, 30))
    }

    fun setSeverity(severity: String) {
        _uiState.value = _uiState.value.copy(severity = severity)
    }

    fun selectPatient(patientId: String?) {
        val latestVitals = patientId?.let { id ->
            allVitals.value.filter { it.patientId == id }.maxByOrNull { it.recordedAtMillis }
        }
        _uiState.value = _uiState.value.copy(
            selectedPatientId = patientId,
            prefillVitals = latestVitals
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(aiErrorMessage = null)
    }

    fun runSymptomCheck() {
        val state = _uiState.value
        if (state.selectedSymptoms.isEmpty()) return
        val patient = patients.value.find { it.id == state.selectedPatientId }
        val offlineOnly = preferences.value.isOfflineOnlyMode
        val apiKey = preferences.value.geminiApiKey
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isEvaluating = true, aiErrorMessage = null)
            try {
                val result = aiEngine.evaluateSymptoms(
                    symptoms = state.selectedSymptoms.toList(),
                    durationDays = state.durationDays,
                    severity = state.severity,
                    patient = patient,
                    offlineOnly = offlineOnly,
                    apiKey = apiKey
                )
                _uiState.value = _uiState.value.copy(isEvaluating = false, symptomResult = result)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Symptom check failed", e)
                _uiState.value = _uiState.value.copy(
                    isEvaluating = false,
                    aiErrorMessage = e.localizedMessage ?: "Gemini symptom check failed."
                )
            }
        }
    }

    fun runDiseaseRiskAssessment(
        systolic: Int,
        diastolic: Int,
        heartRate: Int,
        spo2: Int,
        tempC: Float,
        bloodSugar: Int,
        isPregnant: Boolean
    ) {
        val patient = patients.value.find { it.id == _uiState.value.selectedPatientId }
        val offlineOnly = preferences.value.isOfflineOnlyMode
        val apiKey = preferences.value.geminiApiKey
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isEvaluating = true, aiErrorMessage = null)
            try {
                val result = aiEngine.evaluateVitalsRisk(
                    systolic = systolic,
                    diastolic = diastolic,
                    heartRate = heartRate,
                    spo2 = spo2,
                    tempC = tempC,
                    bloodSugar = bloodSugar,
                    isPregnant = isPregnant,
                    patient = patient,
                    offlineOnly = offlineOnly,
                    apiKey = apiKey
                )
                _uiState.value = _uiState.value.copy(isEvaluating = false, riskResult = result)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Disease risk assessment failed", e)
                _uiState.value = _uiState.value.copy(
                    isEvaluating = false,
                    aiErrorMessage = e.localizedMessage ?: "Gemini risk assessment failed."
                )
            }
        }
    }

    fun sendChatMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isBlank()) return
        val activePatient = patients.value.find { it.id == _uiState.value.selectedPatientId }
        val offlineOnly = preferences.value.isOfflineOnlyMode
        val apiKey = preferences.value.geminiApiKey
        val historySnapshot = _uiState.value.chatMessages

        val userMsg = ChatMessage(
            id = "u-${UUID.randomUUID().toString().take(6)}",
            isUser = true,
            text = trimmed,
            timestampLabel = "Just now"
        )
        val streamingBotId = "a-${UUID.randomUUID().toString().take(6)}"

        _uiState.value = _uiState.value.copy(
            chatMessages = _uiState.value.chatMessages + userMsg,
            isChatTyping = true,
            aiErrorMessage = null
        )

        viewModelScope.launch {
            try {
                var botAdded = false
                aiEngine.streamChatReply(trimmed, historySnapshot, activePatient, offlineOnly, apiKey).collect { partialText ->
                    val botMsg = ChatMessage(
                        id = streamingBotId,
                        isUser = false,
                        text = partialText,
                        timestampLabel = if (offlineOnly || apiKey.isBlank()) "Clinical Engine" else "Gemini AI",
                        tags = if (offlineOnly || apiKey.isBlank()) listOf("Clinical Engine") else listOf("Gemini 2.5 Flash")
                    )
                    val currentList = _uiState.value.chatMessages
                    val updatedList = if (!botAdded) {
                        botAdded = true
                        currentList + botMsg
                    } else {
                        currentList.map { if (it.id == streamingBotId) botMsg else it }
                    }
                    _uiState.value = _uiState.value.copy(
                        chatMessages = updatedList,
                        isChatTyping = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Chat streaming failed", e)
                _uiState.value = _uiState.value.copy(
                    isChatTyping = false,
                    aiErrorMessage = e.localizedMessage ?: "Gemini chat failed."
                )
            }
        }
    }

    fun analyzeImageSample(label: String, uri: Uri? = null) {
        val offlineOnly = preferences.value.isOfflineOnlyMode
        val apiKey = preferences.value.geminiApiKey
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isAnalyzingImage = true,
                selectedImageUri = uri ?: _uiState.value.selectedImageUri,
                aiErrorMessage = null
            )
            try {
                val result = aiEngine.analyzeClinicalImage(
                    scanLabel = label,
                    imageUri = uri ?: _uiState.value.selectedImageUri,
                    offlineOnly = offlineOnly,
                    apiKey = apiKey
                )
                _uiState.value = _uiState.value.copy(
                    isAnalyzingImage = false,
                    imageAnalysisResult = result
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Image analysis failed", e)
                _uiState.value = _uiState.value.copy(
                    isAnalyzingImage = false,
                    aiErrorMessage = e.localizedMessage ?: "Gemini image analysis failed."
                )
            }
        }
    }
}
