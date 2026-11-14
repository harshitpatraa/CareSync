package com.caresync.app.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caresync.app.database.PatientEntity
import com.caresync.app.model.AppLanguage
import com.caresync.app.model.ChatMessage
import com.caresync.app.model.ClinicalAssessmentResult
import com.caresync.app.model.PatientCategory
import com.caresync.app.model.RxAnalysisResult
import com.caresync.app.network.AiMedicalEngine
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
import java.util.UUID
import javax.inject.Inject

private const val TAG = "AiToolsViewModel"

data class AiToolsUiState(
    val selectedSymptoms: Set<String> = emptySet(),
    val durationDays: Int = 2,
    val severity: String = "Moderate",
    val selectedPatientId: String? = null,
    val isEvaluating: Boolean = false,
    val symptomResult: ClinicalAssessmentResult? = null,
    val riskResult: ClinicalAssessmentResult? = null,
    val aiErrorMessage: String? = null,
    val chatMessages: List<ChatMessage> = listOf(
        ChatMessage(
            id = "msg-welcome",
            isUser = false,
            text = "Namaste! I am Sehat Setu, your advanced AI clinical and medical assistant powered by Gemini. Ask me any health question, disease symptoms, maternal & pediatric care guidance, or medication precautions.",
            timestampLabel = "Sehat Setu",
            tags = listOf("Sehat Setu", "Gemini AI")
        )
    ),
    val isChatTyping: Boolean = false,
    val imageAnalysisResult: String? = null,
    val rxAnalysisResult: RxAnalysisResult? = null,
    val rxSavedBanner: String? = null,
    val selectedImageUri: Uri? = null,
    val isAnalyzingImage: Boolean = false,
    val isOfflineOnlyMode: Boolean = false,
    val activeLanguage: AppLanguage = AppLanguage.ENGLISH,
    val isSpeaking: Boolean = false,
    // Pre-filled assessment fields when opened from a specific patient
    val prefillSystolic: String = "142",
    val prefillDiastolic: String = "92",
    val prefillHeartRate: String = "94",
    val prefillSpo2: String = "95",
    val prefillTempC: String = "37.8",
    val prefillBloodSugar: String = "138",
    val prefillIsPregnant: Boolean = false,
    val prefillVersion: Int = 0
)

@HiltViewModel
class AiToolsViewModel @Inject constructor(
    private val repository: CareSyncRepository,
    private val aiEngine: AiMedicalEngine,
    private val ttsManager: TtsManager
) : ViewModel() {

    val patients: StateFlow<List<PatientEntity>> = repository.allPatientsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val preferences: StateFlow<AppPreferences> = repository.preferencesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppPreferences()
    )

    private val _rawState = MutableStateFlow(AiToolsUiState())
    val uiState: StateFlow<AiToolsUiState> = combine(
        _rawState,
        preferences,
        ttsManager.isSpeaking
    ) { raw, prefs, speaking ->
        raw.copy(
            isOfflineOnlyMode = prefs.isOfflineOnlyMode,
            activeLanguage = prefs.language,
            isSpeaking = speaking
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AiToolsUiState()
    )

    fun loadPatientForAssessment(patientId: String?) {
        if (patientId.isNullOrBlank()) return
        viewModelScope.launch {
            val patient = repository.getPatientOnce(patientId) ?: return@launch
            val vitals = repository.getLatestVitalsOnce(patientId)
            val isPregnant = patient.category == PatientCategory.PREGNANCY.name || patient.pregnancyWeeks > 0
            _rawState.value = _rawState.value.copy(
                selectedPatientId = patientId,
                prefillSystolic = (vitals?.systolicBp ?: 122).toString(),
                prefillDiastolic = (vitals?.diastolicBp ?: 80).toString(),
                prefillHeartRate = (vitals?.heartRate ?: 80).toString(),
                prefillSpo2 = (vitals?.spo2Percent ?: 98).toString(),
                prefillTempC = (vitals?.temperatureC ?: 36.8f).toString(),
                prefillBloodSugar = (vitals?.bloodSugarMgDl ?: 105).toString(),
                prefillIsPregnant = isPregnant,
                prefillVersion = _rawState.value.prefillVersion + 1
            )
        }
    }

    fun toggleSymptom(symptom: String) {
        val current = _rawState.value.selectedSymptoms
        val updated = if (symptom in current) current - symptom else current + symptom
        _rawState.value = _rawState.value.copy(selectedSymptoms = updated, aiErrorMessage = null)
    }

    fun setDurationDays(days: Int) {
        _rawState.value = _rawState.value.copy(durationDays = days.coerceIn(1, 30))
    }

    fun setSeverity(severity: String) {
        _rawState.value = _rawState.value.copy(severity = severity)
    }

    fun selectPatient(patientId: String?) {
        _rawState.value = _rawState.value.copy(selectedPatientId = patientId)
    }

    fun setError(message: String?) {
        _rawState.value = _rawState.value.copy(aiErrorMessage = message)
    }

    fun speakText(text: String) {
        ttsManager.speak(text, preferences.value.language)
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    fun runSymptomCheck() {
        val state = _rawState.value
        if (state.selectedSymptoms.isEmpty()) return
        val patient = patients.value.find { it.id == state.selectedPatientId }
        val prefs = preferences.value
        viewModelScope.launch {
            _rawState.value = _rawState.value.copy(isEvaluating = true, aiErrorMessage = null)
            try {
                val latestVitals = patient?.let { repository.getLatestVitalsOnce(it.id) }
                val result = aiEngine.evaluateSymptoms(
                    symptoms = state.selectedSymptoms.toList(),
                    durationDays = state.durationDays,
                    severity = state.severity,
                    patient = patient,
                    latestVitals = latestVitals,
                    language = prefs.language,
                    offlineOnly = prefs.isOfflineOnlyMode,
                    apiKey = prefs.geminiApiKey
                )
                _rawState.value = _rawState.value.copy(isEvaluating = false, symptomResult = result)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Symptom check fallback to local AI engine", e)
                val latestVitals = patient?.let { repository.getLatestVitalsOnce(it.id) }
                val fallback = aiEngine.evaluateSymptomsOffline(
                    symptoms = state.selectedSymptoms.toList(),
                    durationDays = state.durationDays,
                    severity = state.severity,
                    patient = patient,
                    latestVitals = latestVitals
                )
                _rawState.value = _rawState.value.copy(
                    isEvaluating = false,
                    symptomResult = fallback,
                    aiErrorMessage = null
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
        val prefs = preferences.value
        val patient = patients.value.find { it.id == _rawState.value.selectedPatientId }
        viewModelScope.launch {
            _rawState.value = _rawState.value.copy(isEvaluating = true, aiErrorMessage = null)
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
                    language = prefs.language,
                    offlineOnly = prefs.isOfflineOnlyMode,
                    apiKey = prefs.geminiApiKey
                )
                _rawState.value = _rawState.value.copy(isEvaluating = false, riskResult = result)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Disease risk fallback to local AI engine", e)
                val fallback = aiEngine.evaluateVitalsRiskOffline(
                    systolic = systolic,
                    diastolic = diastolic,
                    heartRate = heartRate,
                    spo2 = spo2,
                    tempC = tempC,
                    bloodSugar = bloodSugar,
                    isPregnant = isPregnant,
                    patient = patient
                )
                _rawState.value = _rawState.value.copy(
                    isEvaluating = false,
                    riskResult = fallback,
                    aiErrorMessage = null
                )
            }
        }
    }

    fun runQuestionnaireRiskAssessment(answers: Map<String, String>) {
        val prefs = preferences.value
        val patient = patients.value.find { it.id == _rawState.value.selectedPatientId }
        viewModelScope.launch {
            _rawState.value = _rawState.value.copy(isEvaluating = true, aiErrorMessage = null)
            try {
                val result = aiEngine.evaluateQuestionsRisk(
                    answers = answers,
                    patient = patient,
                    language = prefs.language,
                    offlineOnly = prefs.isOfflineOnlyMode,
                    apiKey = prefs.geminiApiKey
                )
                _rawState.value = _rawState.value.copy(isEvaluating = false, riskResult = result)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Questionnaire risk fallback to local AI engine", e)
                val fallback = aiEngine.evaluateQuestionsRiskOffline(answers, patient)
                _rawState.value = _rawState.value.copy(
                    isEvaluating = false,
                    riskResult = fallback,
                    aiErrorMessage = null
                )
            }
        }
    }

    fun sendChatMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isBlank()) return
        val activePatient = patients.value.find { it.id == _rawState.value.selectedPatientId }
        val prefs = preferences.value
        val offlineOnly = prefs.isOfflineOnlyMode
        val historySnapshot = _rawState.value.chatMessages

        val userMsg = ChatMessage(
            id = "u-${UUID.randomUUID().toString().take(6)}",
            isUser = true,
            text = trimmed,
            timestampLabel = "Just now"
        )
        val streamingBotId = "a-${UUID.randomUUID().toString().take(6)}"

        _rawState.value = _rawState.value.copy(
            chatMessages = _rawState.value.chatMessages + userMsg,
            isChatTyping = true,
            aiErrorMessage = null
        )

        viewModelScope.launch {
            try {
                var botAdded = false
                aiEngine.streamChatReply(
                    userMessage = trimmed,
                    history = historySnapshot,
                    activePatient = activePatient,
                    language = prefs.language,
                    offlineOnly = offlineOnly,
                    apiKey = prefs.geminiApiKey
                ).collect { partialText ->
                    val botMsg = ChatMessage(
                        id = streamingBotId,
                        isUser = false,
                        text = partialText,
                        timestampLabel = if (offlineOnly) "Sehat Setu (Offline)" else "Sehat Setu",
                        tags = if (offlineOnly) listOf("Sehat Setu", "Offline Knowledge") else listOf("Sehat Setu", "Gemini AI")
                    )
                    val currentList = _rawState.value.chatMessages
                    val updatedList = if (!botAdded) {
                        botAdded = true
                        currentList + botMsg
                    } else {
                        currentList.map { if (it.id == streamingBotId) botMsg else it }
                    }
                    _rawState.value = _rawState.value.copy(
                        chatMessages = updatedList,
                        isChatTyping = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Chat streaming fallback to local clinical AI engine", e)
                val (offlineText, tags) = aiEngine.generateChatReplyOfflineDirect(trimmed, activePatient)
                val botMsg = ChatMessage(
                    id = streamingBotId,
                    isUser = false,
                    text = offlineText,
                    timestampLabel = "Sehat Setu",
                    tags = listOf("Sehat Setu", "Local Clinical AI")
                )
                val currentList = _rawState.value.chatMessages.filter { it.id != streamingBotId }
                _rawState.value = _rawState.value.copy(
                    chatMessages = currentList + botMsg,
                    isChatTyping = false,
                    aiErrorMessage = null
                )
            }
        }
    }

    fun analyzeImageSample(label: String, uri: Uri? = null) {
        val prefs = preferences.value
        viewModelScope.launch {
            _rawState.value = _rawState.value.copy(
                isAnalyzingImage = true,
                selectedImageUri = uri,
                rxAnalysisResult = null,
                rxSavedBanner = null,
                aiErrorMessage = null
            )
            try {
                val result = aiEngine.analyzeClinicalImage(
                    scanLabel = label,
                    imageUri = uri,
                    language = prefs.language,
                    offlineOnly = prefs.isOfflineOnlyMode,
                    apiKey = prefs.geminiApiKey
                )
                _rawState.value = _rawState.value.copy(
                    isAnalyzingImage = false,
                    imageAnalysisResult = result
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Image analysis fallback to local clinical vision engine", e)
                val fallback = aiEngine.analyzeImageOfflineDirect(label)
                _rawState.value = _rawState.value.copy(
                    isAnalyzingImage = false,
                    imageAnalysisResult = fallback,
                    aiErrorMessage = null
                )
            }
        }
    }

    fun analyzePrescription(label: String, uri: Uri? = null) {
        val prefs = preferences.value
        val patient = patients.value.find { it.id == _rawState.value.selectedPatientId }
        viewModelScope.launch {
            _rawState.value = _rawState.value.copy(
                isAnalyzingImage = true,
                selectedImageUri = uri ?: _rawState.value.selectedImageUri,
                imageAnalysisResult = null,
                rxSavedBanner = null,
                aiErrorMessage = null
            )
            try {
                val result = aiEngine.analyzePrescriptionStructured(
                    scanLabel = label,
                    imageUri = uri ?: _rawState.value.selectedImageUri,
                    patient = patient,
                    language = prefs.language,
                    offlineOnly = prefs.isOfflineOnlyMode,
                    apiKey = prefs.geminiApiKey
                )
                _rawState.value = _rawState.value.copy(
                    isAnalyzingImage = false,
                    rxAnalysisResult = result
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Prescription analysis fallback to local clinical Rx engine", e)
                val fallback = aiEngine.generateOfflineRxResultDirect(label, patient)
                _rawState.value = _rawState.value.copy(
                    isAnalyzingImage = false,
                    rxAnalysisResult = fallback,
                    aiErrorMessage = null
                )
            }
        }
    }

    fun saveRxToPatient(patientId: String) {
        val rx = _rawState.value.rxAnalysisResult ?: return
        if (rx.medications.isEmpty()) return
        viewModelScope.launch {
            val patient = repository.getPatientOnce(patientId) ?: return@launch
            repository.saveRxMedicationsAndReminders(patient, rx.medications)
            _rawState.value = _rawState.value.copy(
                selectedPatientId = patientId,
                rxSavedBanner = "Saved ${rx.medications.size} medicine(s) & dose reminder for ${patient.fullName}."
            )
        }
    }
}
