package com.caresync.app.network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.caresync.app.database.PatientEntity
import com.caresync.app.database.VitalsEntity
import com.caresync.app.model.ChatMessage
import com.caresync.app.model.ClinicalAssessmentResult
import com.caresync.app.model.RiskLevel
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

private const val TAG = "AiMedicalEngine"
private const val MODEL_NAME = "gemini-3.8-flash"
private const val AI_TIMEOUT_MS = 60_000L

@Singleton
class AiMedicalEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val assessmentSchema by lazy {
        Schema.obj(
            properties = mapOf(
                "riskScore" to Schema.integer("Risk score from 0 to 100"),
                "riskLevel" to Schema.enumeration(
                    values = listOf("LOW", "MODERATE", "HIGH", "CRITICAL"),
                    description = "Clinical triage risk tier"
                ),
                "primarySummary" to Schema.string("Concise 2-3 sentence clinical triage summary"),
                "possibleConditions" to Schema.array(
                    items = Schema.string(),
                    description = "Probable differential considerations"
                ),
                "medicineSuggestions" to Schema.array(
                    items = Schema.string(),
                    description = "Safe supportive / first-line care and medication precautions"
                ),
                "recommendedActions" to Schema.array(
                    items = Schema.string(),
                    description = "Next clinical steps for field health worker"
                ),
                "redFlags" to Schema.array(
                    items = Schema.string(),
                    description = "Urgent warning signs requiring immediate referral or SOS"
                )
            )
        )
    }

    private val structuredAssessmentModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = assessmentSchema
            },
            systemInstruction = content {
                text(
                    "You are CareSync Clinical AI assisting community health workers with structured triage. " +
                        "Provide clear, evidence-based differential considerations, supportive medicine guidance, " +
                        "and urgent red flags. Never claim definitive diagnosis; recommend licensed physician review."
                )
            }
        )
    }

    private val chatAndVisionModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            systemInstruction = content {
                text(
                    "You are CareSync Clinical Assistant supporting frontline healthcare workers. " +
                        "Give concise, structured, practical clinical guidance covering vitals, maternal/ANC care, " +
                        "pediatric immunization, and triage escalation. Keep responses crisp and readable."
                )
            }
        )
    }

    suspend fun evaluateSymptoms(
        symptoms: List<String>,
        durationDays: Int,
        severity: String,
        patient: PatientEntity? = null,
        latestVitals: VitalsEntity? = null,
        offlineOnly: Boolean = false
    ): ClinicalAssessmentResult {
        if (offlineOnly) {
            return evaluateSymptomsOffline(symptoms, durationDays, severity, patient, latestVitals)
        }

        val prompt = buildString {
            appendLine("Evaluate the following patient presentation for clinical triage:")
            appendLine("Symptoms: ${symptoms.joinToString(", ")}")
            appendLine("Duration: $durationDays day(s)")
            appendLine("Reported Severity: $severity")
            if (patient != null) {
                appendLine("Patient: ${patient.fullName}, Age ${patient.age}, ${patient.gender}, Category: ${patient.category}")
                appendLine("Allergies: ${patient.allergies}; Chronic conditions: ${patient.chronicConditions}")
                if (patient.pregnancyWeeks > 0) appendLine("Pregnancy: ${patient.pregnancyWeeks} weeks gestation")
            }
            if (latestVitals != null) {
                appendLine(
                    "Latest Vitals: BP ${latestVitals.systolicBp}/${latestVitals.diastolicBp}, " +
                        "SpO2 ${latestVitals.spo2Percent}%, HR ${latestVitals.heartRate} bpm, Temp ${latestVitals.temperatureC} C"
                )
            }
        }

        return runStructuredAssessment(prompt)
    }

    suspend fun evaluateVitalsRisk(
        systolic: Int,
        diastolic: Int,
        heartRate: Int,
        spo2: Int,
        tempC: Float,
        bloodSugar: Int,
        isPregnant: Boolean,
        offlineOnly: Boolean = false
    ): ClinicalAssessmentResult {
        if (offlineOnly) {
            return evaluateVitalsRiskOffline(systolic, diastolic, heartRate, spo2, tempC, bloodSugar, isPregnant)
        }

        val prompt = buildString {
            appendLine("Perform a clinical disease risk assessment from these patient vitals:")
            appendLine("Blood Pressure: $systolic/$diastolic mmHg")
            appendLine("Heart Rate: $heartRate bpm")
            appendLine("SpO2: $spo2%")
            appendLine("Temperature: $tempC C")
            appendLine("Blood Sugar: $bloodSugar mg/dL")
            appendLine("Pregnant / ANC Patient: ${if (isPregnant) "Yes" else "No"}")
        }

        return runStructuredAssessment(prompt)
    }

    private suspend fun runStructuredAssessment(prompt: String): ClinicalAssessmentResult {
        try {
            val response = withTimeout(AI_TIMEOUT_MS) {
                structuredAssessmentModel.generateContent(prompt)
            }
            val jsonString = response.text ?: throw IllegalStateException("Gemini returned an empty assessment.")
            val json = JSONObject(jsonString)

            val score = json.optInt("riskScore", 40).coerceIn(0, 100)
            val riskLevel = RiskLevel.entries.find {
                it.name.equals(json.optString("riskLevel", "MODERATE"), ignoreCase = true)
            } ?: RiskLevel.MODERATE

            return ClinicalAssessmentResult(
                riskScore = score,
                riskLevel = riskLevel,
                primarySummary = json.optString("primarySummary", "Gemini triage assessment complete."),
                possibleConditions = json.optJSONArray("possibleConditions").toStringList(),
                medicineSuggestions = json.optJSONArray("medicineSuggestions").toStringList(),
                recommendedActions = json.optJSONArray("recommendedActions").toStringList(),
                redFlags = json.optJSONArray("redFlags").toStringList(),
                isOfflinePrediction = false
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Gemini structured assessment timed out", e)
            throw IllegalStateException("Gemini assessment timed out after 60 seconds. Please try again.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gemini structured assessment failed", e)
            throw e
        }
    }

    fun streamChatReply(
        userMessage: String,
        history: List<ChatMessage>,
        activePatient: PatientEntity?,
        offlineOnly: Boolean = false
    ): Flow<String> = flow {
        if (offlineOnly) {
            val (offlineReply, _) = generateChatReplyOffline(userMessage, activePatient)
            emit(offlineReply)
            return@flow
        }

        val chatHistory: List<Content> = history
            .filter { it.id != "msg-welcome" }
            .takeLast(8)
            .map { msg ->
                content(role = if (msg.isUser) "user" else "model") {
                    text(msg.text)
                }
            }

        val groundedPrompt = buildString {
            if (activePatient != null) {
                appendLine(
                    "[Patient Context: ${activePatient.fullName}, Age ${activePatient.age}, ${activePatient.gender}, " +
                        "Category ${activePatient.category}, Allergies: ${activePatient.allergies}, Conditions: ${activePatient.chronicConditions}]"
                )
            }
            append(userMessage)
        }

        try {
            val chat = chatAndVisionModel.startChat(history = chatHistory)
            val accumulated = StringBuilder()
            withTimeout(AI_TIMEOUT_MS) {
                chat.sendMessageStream(groundedPrompt).collect { chunk ->
                    val part = chunk.text.orEmpty()
                    if (part.isNotEmpty()) {
                        accumulated.append(part)
                        emit(accumulated.toString())
                    }
                }
            }
            if (accumulated.isBlank()) {
                throw IllegalStateException("Gemini returned an empty response.")
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Gemini chat streaming timed out", e)
            throw IllegalStateException("Gemini response timed out after 60 seconds. Please try again.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gemini chat streaming failed", e)
            throw e
        }
    }

    suspend fun analyzeClinicalImage(
        scanLabel: String,
        imageUri: Uri?,
        offlineOnly: Boolean = false
    ): String {
        if (offlineOnly) {
            return analyzeImageOffline(scanLabel)
        }

        try {
            val promptContent = if (imageUri != null) {
                val bitmap = loadScaledBitmap(imageUri)
                    ?: throw IllegalStateException("Unable to decode the selected image.")
                content {
                    image(bitmap)
                    text(
                        "Analyze this clinical photo ($scanLabel) for a frontline health worker. " +
                            "Describe observable visual features, potential concerns, hygiene/care steps, and " +
                            "red-flag signs requiring physician referral."
                    )
                }
            } else {
                content {
                    text(
                        "Provide a clinical screening checklist for a frontline health worker reviewing: $scanLabel. " +
                            "Include key visual markers to inspect, basic care steps, and escalation red flags."
                    )
                }
            }

            val response = withTimeout(AI_TIMEOUT_MS) {
                chatAndVisionModel.generateContent(promptContent)
            }
            return response.text?.takeIf { it.isNotBlank() }
                ?: throw IllegalStateException("Gemini returned an empty image analysis.")
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Gemini image analysis timed out", e)
            throw IllegalStateException("Gemini image analysis timed out after 60 seconds.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gemini image analysis failed", e)
            throw e
        }
    }

    private suspend fun loadScaledBitmap(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        val decoded = context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)
        } ?: return@withContext null

        val maxDim = 1024
        val largest = max(decoded.width, decoded.height)
        if (largest <= maxDim) return@withContext decoded

        val scale = maxDim.toFloat() / largest.toFloat()
        Bitmap.createScaledBitmap(
            decoded,
            (decoded.width * scale).toInt().coerceAtLeast(1),
            (decoded.height * scale).toInt().coerceAtLeast(1),
            true
        )
    }

    private fun org.json.JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        return (0 until length()).mapNotNull { idx -> optString(idx).takeIf { it.isNotBlank() } }
    }

    // --- Offline-Only Mode Fallback Engine (used only when Offline-Only Mode is enabled) ---

    internal suspend fun evaluateSymptomsOffline(
        symptoms: List<String>,
        durationDays: Int,
        severity: String,
        patient: PatientEntity? = null,
        latestVitals: VitalsEntity? = null
    ): ClinicalAssessmentResult {
        delay(350L)
        val joined = symptoms.joinToString(", ").lowercase()
        var score = 18
        val conditions = mutableListOf<String>()
        val meds = mutableListOf<String>()
        val actions = mutableListOf<String>()
        val redFlags = mutableListOf<String>()

        if ("chest pain" in joined || "shortness of breath" in joined || (latestVitals?.spo2Percent ?: 99) < 92) {
            score += 55
            conditions += "Acute Cardiopulmonary Distress / Hypoxia"
            redFlags += "Urgent: Immediate oxygen assessment and PHC/hospital transfer required."
            actions += "Administer supplemental O2 if SpO2 < 94% and trigger Emergency SOS."
        }
        if ("fever" in joined || (latestVitals?.temperatureC ?: 36.8f) >= 38.0f) {
            score += 22
            conditions += "Acute Febrile Illness (Viral / Dengue / Malaria screen)"
            meds += "Paracetamol 500mg (or weight-adjusted pediatric syrup) q6h PRN"
            actions += "Perform Rapid Antigen / Malaria RDT & monitor hydration."
        }
        if ("cough" in joined || "sore throat" in joined) {
            score += 14
            conditions += "Upper / Lower Respiratory Tract Infection"
            meds += "Warm saline gargles + Oral hydration; avoid unnecessary antibiotics"
            actions += "Auscultate chest and track respiratory rate."
        }
        if ("headache" in joined || "dizziness" in joined || "swelling" in joined) {
            score += 18
            if (patient?.pregnancyWeeks ?: 0 > 20) {
                score += 28
                conditions += "Gestational Hypertension / Pre-eclampsia Risk"
                redFlags += "Pregnancy >20w with headache or edema: check BP & urine protein immediately."
            } else {
                conditions += "Hypertensive Episode / Dehydration"
            }
            actions += "Recheck blood pressure after 10 minutes of seated rest."
        }
        if ("diarrhea" in joined || "vomiting" in joined || "abdominal" in joined) {
            score += 18
            conditions += "Acute Gastroenteritis / Dehydration Risk"
            meds += "WHO ORS packets (1L per 24h) + Zinc 20mg daily (10-14 days for children)"
            actions += "Assess skin turgor, capillary refill, and urine output."
        }
        if ("rash" in joined) {
            score += 12
            conditions += "Viral Exanthem / Contact Dermatitis"
            meds += "Calamine soothing lotion; evaluate for petechiae if febrile"
        }

        if (severity == "Severe") score += 20 else if (severity == "Moderate") score += 10
        if (durationDays >= 4) score += 10
        if ((latestVitals?.systolicBp ?: 120) >= 150) score += 20

        val clampedScore = score.coerceIn(10, 98)
        val riskLevel = when {
            clampedScore >= 75 -> RiskLevel.CRITICAL
            clampedScore >= 50 -> RiskLevel.HIGH
            clampedScore >= 30 -> RiskLevel.MODERATE
            else -> RiskLevel.LOW
        }

        if (conditions.isEmpty()) conditions += "Mild Non-Specific Viral Malaise"
        if (meds.isEmpty()) meds += "Oral Hydration Salts (ORS) & supportive rest"
        if (actions.isEmpty()) actions += "Record baseline vitals and schedule a 48-hour CareSync follow-up."

        val summary = buildString {
            append("Offline Clinical Triage evaluated ${symptoms.size} symptom(s) over $durationDays day(s)")
            if (patient != null) append(" for ${patient.fullName} (${patient.age}y)")
            append(". Calculated risk score is $clampedScore/100 (${riskLevel.label}).")
        }

        return ClinicalAssessmentResult(
            riskScore = clampedScore,
            riskLevel = riskLevel,
            primarySummary = summary,
            possibleConditions = conditions.distinct(),
            medicineSuggestions = meds.distinct(),
            recommendedActions = actions.distinct(),
            redFlags = redFlags.distinct(),
            isOfflinePrediction = true
        )
    }

    internal suspend fun evaluateVitalsRiskOffline(
        systolic: Int,
        diastolic: Int,
        heartRate: Int,
        spo2: Int,
        tempC: Float,
        bloodSugar: Int,
        isPregnant: Boolean
    ): ClinicalAssessmentResult {
        delay(300L)
        var score = 12
        val conditions = mutableListOf<String>()
        val actions = mutableListOf<String>()
        val meds = mutableListOf<String>()
        val redFlags = mutableListOf<String>()

        if (spo2 < 92) {
            score += 48
            conditions += "Hypoxemia (SpO2 $spo2%)"
            redFlags += "Critical low oxygen saturation — initiate emergency airway/O2 support."
        } else if (spo2 < 95) {
            score += 20
            conditions += "Borderline Oxygenation (SpO2 $spo2%)"
        }

        if (systolic >= 160 || diastolic >= 100) {
            score += 34
            conditions += if (isPregnant) "Severe Gestational Hypertension" else "Stage 2 Hypertensive Crisis Watch"
            redFlags += "BP $systolic/$diastolic mmHg exceeds safe threshold."
            actions += "Urgent physician consultation for antihypertensive stabilization."
        } else if (systolic >= 140 || diastolic >= 90) {
            score += 18
            conditions += "Elevated Blood Pressure ($systolic/$diastolic mmHg)"
        }

        if (tempC >= 38.5f) {
            score += 20
            conditions += "High-Grade Pyrexia (${tempC}°C)"
            meds += "Paracetamol antipyretic + tepid sponging"
        }

        if (bloodSugar >= 200) {
            score += 18
            conditions += "Hyperglycemia ($bloodSugar mg/dL)"
            actions += "Check urine ketones and review diabetes regimen."
        }

        if (heartRate > 110 || heartRate < 50) {
            score += 16
            conditions += "Abnormal Heart Rate ($heartRate bpm)"
        }

        val clamped = score.coerceIn(8, 99)
        val riskLevel = when {
            clamped >= 70 -> RiskLevel.CRITICAL
            clamped >= 48 -> RiskLevel.HIGH
            clamped >= 28 -> RiskLevel.MODERATE
            else -> RiskLevel.LOW
        }

        if (conditions.isEmpty()) conditions += "Stable Baseline Vitals"
        if (actions.isEmpty()) actions += "Continue routine CareSync monitoring and sync when online."
        if (meds.isEmpty()) meds += "No acute medication escalation indicated"

        return ClinicalAssessmentResult(
            riskScore = clamped,
            riskLevel = riskLevel,
            primarySummary = "Offline Risk Model analyzed BP $systolic/$diastolic, SpO2 $spo2%, HR $heartRate bpm, Temp ${tempC}°C, Sugar $bloodSugar mg/dL.",
            possibleConditions = conditions,
            medicineSuggestions = meds,
            recommendedActions = actions,
            redFlags = redFlags,
            isOfflinePrediction = true
        )
    }

    private suspend fun generateChatReplyOffline(userMessage: String, activePatient: PatientEntity?): Pair<String, List<String>> {
        delay(300L)
        val q = userMessage.lowercase()
        val patientPrefix = activePatient?.let { "Context (${it.fullName}, ${it.age}y, ${it.category}): " } ?: ""

        return when {
            "fever" in q || "dengue" in q || "temp" in q -> {
                "${patientPrefix}For persistent fever, check temperature every 4 hours and verify hydration. Avoid NSAIDs like Ibuprofen if dengue is suspected—use Paracetamol 500mg PRN and ORS." to
                    listOf("Offline Mode", "Fever Protocol")
            }
            "pregnan" in q || "anc" in q || "bp" in q || "anemia" in q -> {
                "${patientPrefix}In antenatal care, monitor BP (<140/90), fetal movement counts, and hemoglobin levels. Escalate immediately if severe headache or blurred vision appears." to
                    listOf("Offline Mode", "ANC Checklist")
            }
            else -> {
                "${patientPrefix}Offline-Only Mode is enabled. Capture current vitals (BP, SpO2, Temp, HR) and disable Offline-Only Mode in Settings when ready to query Gemini AI." to
                    listOf("Offline Mode", "CareSync")
            }
        }
    }

    private suspend fun analyzeImageOffline(scanLabel: String): String {
        delay(300L)
        return "Offline-Only Mode ($scanLabel): Photo cached locally in CareSync. Disable Offline-Only Mode in Settings to analyze this image with Gemini Vision."
    }
}
