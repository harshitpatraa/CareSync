package com.caresync.app.network

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.caresync.app.ai.OfflineAiService
import com.caresync.app.database.ActivityEntity
import com.caresync.app.database.PatientEntity
import com.caresync.app.database.VitalsEntity
import com.caresync.app.model.AppLanguage
import com.caresync.app.model.ChatMessage
import com.caresync.app.model.ClinicalAssessmentResult
import com.caresync.app.model.RxAnalysisResult
import com.caresync.app.model.WardOutbreakInsight
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 100% On-device Clinical AI Engine for CareSync.
 * Operates entirely offline with ZERO external cloud AI, Gemini API, or network dependencies.
 * All medical intelligence, triage, prescription OCR, and disease risk calculations
 * run on the local device powered by the [OfflineAiService] and local MoHFW guidelines.
 */
@Singleton
class AiMedicalEngine @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val offlineAiService: OfflineAiService
) {

    constructor(context: Context) : this(
        context,
        com.caresync.app.ai.OfflineMedicalAiServiceImpl(
            com.caresync.app.ai.OfflinePrescriptionScanner(context)
        )
    )

    suspend fun evaluateSymptoms(
        symptoms: List<String>,
        durationDays: Int,
        severity: String,
        patient: PatientEntity? = null,
        latestVitals: VitalsEntity? = null,
        language: AppLanguage = AppLanguage.ENGLISH,
        offlineOnly: Boolean = true,
        apiKey: String? = null
    ): ClinicalAssessmentResult {
        return offlineAiService.evaluateSymptoms(symptoms, durationDays, severity, patient, latestVitals)
    }

    suspend fun evaluateVitalsRisk(
        systolic: Int,
        diastolic: Int,
        heartRate: Int,
        spo2: Int,
        tempC: Float,
        bloodSugar: Int,
        isPregnant: Boolean,
        patient: PatientEntity? = null,
        language: AppLanguage = AppLanguage.ENGLISH,
        offlineOnly: Boolean = true,
        apiKey: String? = null
    ): ClinicalAssessmentResult {
        return offlineAiService.evaluateVitalsRisk(systolic, diastolic, heartRate, spo2, tempC, bloodSugar, isPregnant, patient)
    }

    suspend fun evaluateQuestionsRisk(
        answers: Map<String, String>,
        patient: PatientEntity? = null,
        language: AppLanguage = AppLanguage.ENGLISH,
        offlineOnly: Boolean = true,
        apiKey: String? = null
    ): ClinicalAssessmentResult {
        return offlineAiService.evaluateQuestionnaireRisk(answers, patient)
    }

    fun streamChatReply(
        userMessage: String,
        history: List<ChatMessage> = emptyList(),
        activePatient: PatientEntity? = null,
        language: AppLanguage = AppLanguage.ENGLISH,
        offlineOnly: Boolean = true,
        apiKey: String? = null
    ): Flow<String> {
        return offlineAiService.streamChatReply(userMessage, activePatient, language)
    }

    suspend fun analyzeClinicalImage(
        scanLabel: String,
        imageUri: Uri? = null,
        language: AppLanguage = AppLanguage.ENGLISH,
        offlineOnly: Boolean = true,
        apiKey: String? = null
    ): String {
        return offlineAiService.analyzeClinicalImage(scanLabel, imageUri, null)
    }

    suspend fun analyzePrescriptionStructured(
        scanLabel: String,
        imageUri: Uri? = null,
        patient: PatientEntity? = null,
        language: AppLanguage = AppLanguage.ENGLISH,
        offlineOnly: Boolean = true,
        apiKey: String? = null
    ): RxAnalysisResult {
        return offlineAiService.analyzePrescription(scanLabel, imageUri, null, patient)
    }

    suspend fun analyzeWardOutbreakTrends(
        patients: List<PatientEntity>,
        recentActivities: List<ActivityEntity> = emptyList(),
        language: AppLanguage = AppLanguage.ENGLISH,
        offlineOnly: Boolean = true,
        apiKey: String? = null
    ): WardOutbreakInsight {
        return offlineAiService.analyzeWardTrends(patients, recentActivities)
    }

    internal suspend fun evaluateSymptomsOffline(
        symptoms: List<String>,
        durationDays: Int,
        severity: String,
        patient: PatientEntity?,
        latestVitals: VitalsEntity?
    ): ClinicalAssessmentResult {
        return offlineAiService.evaluateSymptoms(symptoms, durationDays, severity, patient, latestVitals)
    }

    internal suspend fun evaluateVitalsRiskOffline(
        systolic: Int,
        diastolic: Int,
        heartRate: Int,
        spo2: Int,
        tempC: Float,
        bloodSugar: Int,
        isPregnant: Boolean,
        patient: PatientEntity? = null
    ): ClinicalAssessmentResult {
        return offlineAiService.evaluateVitalsRisk(systolic, diastolic, heartRate, spo2, tempC, bloodSugar, isPregnant, patient)
    }

    internal fun evaluateQuestionsRiskOffline(
        answers: Map<String, String>,
        patient: PatientEntity? = null
    ): ClinicalAssessmentResult {
        return kotlinx.coroutines.runBlocking {
            offlineAiService.evaluateQuestionnaireRisk(answers, patient)
        }
    }

    internal fun generateChatReplyOfflineDirect(
        userMessage: String,
        activePatient: PatientEntity?
    ): Pair<String, List<String>> {
        return kotlinx.coroutines.runBlocking {
            offlineAiService.answerMedicalQuery(userMessage, activePatient, AppLanguage.ENGLISH)
        }
    }

    internal fun generateOfflineRxResultDirect(
        scanLabel: String,
        patient: PatientEntity?
    ): RxAnalysisResult {
        return kotlinx.coroutines.runBlocking {
            offlineAiService.analyzePrescription(scanLabel, null, null, patient)
        }
    }

    internal fun analyzeImageOfflineDirect(scanLabel: String): String {
        return kotlinx.coroutines.runBlocking {
            offlineAiService.analyzeClinicalImage(scanLabel, null, null)
        }
    }

    internal suspend fun analyzeWardOutbreakOffline(patients: List<PatientEntity>): WardOutbreakInsight {
        return offlineAiService.analyzeWardTrends(patients, emptyList())
    }
}
