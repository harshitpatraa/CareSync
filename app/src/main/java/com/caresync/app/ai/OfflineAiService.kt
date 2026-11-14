package com.caresync.app.ai

import android.graphics.Bitmap
import android.net.Uri
import com.caresync.app.database.ActivityEntity
import com.caresync.app.database.PatientEntity
import com.caresync.app.database.VitalsEntity
import com.caresync.app.model.AppLanguage
import com.caresync.app.model.ClinicalAssessmentResult
import com.caresync.app.model.RxAnalysisResult
import com.caresync.app.model.WardOutbreakInsight
import kotlinx.coroutines.flow.Flow

/**
 * Clean, decoupled interface for 100% offline medical AI services.
 * Operates entirely on-device without any cloud AI, Gemini API, external servers, or internet connection.
 * Strictly adheres to healthcare safety: provides guidance based on official MoHFW/NHM ASHA guidelines,
 * flags emergency warning signs, and never prescribes or alters medication independently.
 */
interface OfflineAiService {

    /**
     * Answers medical and clinical queries using local knowledge retrieval
     * based on Indian MoHFW / National Health Mission (NHM) ASHA protocols.
     * Supports English, Hindi (Devanagari), and Roman Hindi / Hinglish.
     */
    fun streamChatReply(
        userQuery: String,
        activePatient: PatientEntity? = null,
        language: AppLanguage = AppLanguage.ENGLISH
    ): Flow<String>

    /**
     * One-shot offline clinical reply generation.
     */
    suspend fun answerMedicalQuery(
        userQuery: String,
        activePatient: PatientEntity? = null,
        language: AppLanguage = AppLanguage.ENGLISH
    ): Pair<String, List<String>>

    /**
     * Performs 100% on-device offline prescription scanning and OCR.
     * Preprocesses the image, extracts text offline, detects medicine candidates,
     * matches against the local essential medicine database, and flags uncertainty warnings.
     */
    suspend fun analyzePrescription(
        scanLabel: String,
        imageUri: Uri? = null,
        bitmap: Bitmap? = null,
        patient: PatientEntity? = null
    ): RxAnalysisResult

    /**
     * Offline clinical triage and risk scoring based on reported symptoms, duration, and severity.
     */
    suspend fun evaluateSymptoms(
        symptoms: List<String>,
        durationDays: Int,
        severity: String,
        patient: PatientEntity? = null,
        latestVitals: VitalsEntity? = null
    ): ClinicalAssessmentResult

    /**
     * Offline multi-parameter vitals risk assessment.
     */
    suspend fun evaluateVitalsRisk(
        systolic: Int,
        diastolic: Int,
        heartRate: Int,
        spo2: Int,
        tempC: Float,
        bloodSugar: Int,
        isPregnant: Boolean,
        patient: PatientEntity? = null
    ): ClinicalAssessmentResult

    /**
     * Offline questionnaire screening (12-point ASHA community assessment).
     */
    suspend fun evaluateQuestionnaireRisk(
        answers: Map<String, String>,
        patient: PatientEntity? = null
    ): ClinicalAssessmentResult

    /**
     * Offline clinical vision and wound/rash guideline evaluation.
     */
    suspend fun analyzeClinicalImage(
        scanLabel: String,
        imageUri: Uri? = null,
        bitmap: Bitmap? = null
    ): String

    /**
     * Offline epidemiological surveillance across community ward records.
     */
    suspend fun analyzeWardTrends(
        patients: List<PatientEntity>,
        activities: List<ActivityEntity>
    ): WardOutbreakInsight

    /**
     * Evaluates whether a query or presentation contains acute medical emergency warning signs.
     */
    fun checkEmergencySigns(text: String): EmergencyAlert?
}

data class EmergencyAlert(
    val title: String,
    val description: String,
    val recommendedImmediateAction: String,
    val severityLevel: String = "CRITICAL"
)
