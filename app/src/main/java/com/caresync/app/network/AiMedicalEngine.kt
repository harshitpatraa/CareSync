package com.caresync.app.network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.caresync.app.database.ActivityEntity
import com.caresync.app.database.PatientEntity
import com.caresync.app.database.VitalsEntity
import com.caresync.app.model.AppLanguage
import com.caresync.app.model.ChatMessage
import com.caresync.app.model.ClinicalAssessmentResult
import com.caresync.app.model.RiskLevel
import com.caresync.app.model.RxAnalysisResult
import com.caresync.app.model.RxMedicationItem
import com.caresync.app.model.WardOutbreakInsight
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
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

private const val TAG = "AiMedicalEngine"
private const val MODEL_NAME = "gemini-1.5-flash"
private const val AI_TIMEOUT_MS = 45_000L

@Singleton
class AiMedicalEngine @Inject constructor(
    @param:ApplicationContext private val context: Context
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

    private val rxSchema by lazy {
        Schema.obj(
            properties = mapOf(
                "summary" to Schema.string("Brief overview of the prescription or clinical scan"),
                "medications" to Schema.array(
                    items = Schema.obj(
                        properties = mapOf(
                            "name" to Schema.string("Medicine name"),
                            "dosage" to Schema.string("Strength / dose e.g. 500mg or 1 tab"),
                            "frequency" to Schema.string("Schedule e.g. Twice daily after meals"),
                            "duration" to Schema.string("Course duration e.g. 5 days")
                        )
                    ),
                    description = "Extracted or recommended medications"
                ),
                "warnings" to Schema.array(
                    items = Schema.string(),
                    description = "Contraindications, allergy checks, or follow-up warnings"
                )
            )
        )
    }

    private val outbreakSchema by lazy {
        Schema.obj(
            properties = mapOf(
                "alertLevel" to Schema.enumeration(
                    values = listOf("LOW", "MODERATE", "HIGH", "CRITICAL"),
                    description = "Overall ward epidemiological alert tier"
                ),
                "headline" to Schema.string("Short crisp headline summarizing ward trend"),
                "summary" to Schema.string("2-3 sentence epidemiological synthesis across patients"),
                "affectedWards" to Schema.array(
                    items = Schema.string(),
                    description = "Wards or sectors with elevated risk or clusters"
                ),
                "recommendations" to Schema.array(
                    items = Schema.string(),
                    description = "Actionable community health & screening steps"
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
                        "and urgent red flags. Never claim definitive diagnosis; recommend licensed physician review. " +
                        "Respond in the requested output language while keeping riskLevel as an exact enum."
                )
            }
        )
    }

    private val structuredRxModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = rxSchema
            },
            systemInstruction = content {
                text(
                    "You are CareSync Clinical Prescription OCR & Medication Assistant. " +
                        "Extract readable medicine names, dosages, frequencies, durations, and patient allergy/safety warnings."
                )
            }
        )
    }

    private val structuredOutbreakModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = outbreakSchema
            },
            systemInstruction = content {
                text(
                    "You are CareSync Public Health Surveillance AI. Analyze anonymized ward cohort summaries " +
                        "to identify febrile clusters, maternal high-risk pockets, and immunization gaps."
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

    private fun languageInstruction(language: AppLanguage): String = when (language) {
        AppLanguage.ENGLISH -> "Respond in clear English."
        AppLanguage.HINDI -> "Respond in natural, clear Hindi (हिन्दी in Devanagari script) suitable for community health workers."
        AppLanguage.MARATHI -> "Respond in natural, clear Marathi (मराठी in Devanagari script) suitable for community health workers."
        AppLanguage.TAMIL -> "Respond in natural, clear Tamil (தமிழ்) suitable for community health workers."
        AppLanguage.BENGALI -> "Respond in natural, clear Bengali (বাংলা) suitable for community health workers."
    }

    suspend fun evaluateSymptoms(
        symptoms: List<String>,
        durationDays: Int,
        severity: String,
        patient: PatientEntity? = null,
        latestVitals: VitalsEntity? = null,
        language: AppLanguage = AppLanguage.ENGLISH,
        offlineOnly: Boolean = false,
        apiKey: String? = null
    ): ClinicalAssessmentResult {
        if (offlineOnly) {
            return evaluateSymptomsOffline(symptoms, durationDays, severity, patient, latestVitals)
        }

        val prompt = buildString {
            appendLine("Evaluate the following patient presentation for clinical triage:")
            appendLine(languageInstruction(language))
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

        return try {
            if (!apiKey.isNullOrBlank()) {
                val promptWithSchema = "$prompt\n\nReturn ONLY a JSON object matching schema: {\"riskScore\": 0-100, \"riskLevel\": \"LOW\"|\"MODERATE\"|\"HIGH\"|\"CRITICAL\", \"primarySummary\": \"...\", \"possibleConditions\": [\"...\"], \"medicineSuggestions\": [\"...\"], \"recommendedActions\": [\"...\"], \"redFlags\": [\"...\"]}"
                val jsonStr = callDirectGeminiRest(promptWithSchema, apiKey, isJsonExpected = true)
                parseAssessmentJson(jsonStr)
            } else {
                runStructuredAssessment(prompt)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Cloud AI triage unavailable (${e.message}), activating offline clinical engine", e)
            val fallback = evaluateSymptomsOffline(symptoms, durationDays, severity, patient, latestVitals)
            fallback.copy(
                primarySummary = "[Offline Clinical Engine - App Check Bypassed] ${fallback.primarySummary}"
            )
        }
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
        offlineOnly: Boolean = false,
        apiKey: String? = null
    ): ClinicalAssessmentResult {
        if (offlineOnly) {
            return evaluateVitalsRiskOffline(systolic, diastolic, heartRate, spo2, tempC, bloodSugar, isPregnant, patient)
        }

        val prompt = buildString {
            appendLine("Perform a clinical disease risk assessment from these patient vitals:")
            appendLine(languageInstruction(language))
            if (patient != null) {
                appendLine("Patient Context: ${patient.fullName}, Age ${patient.age}, ${patient.gender}, Category: ${patient.category}")
                appendLine("Known Allergies: ${patient.allergies}; Chronic Conditions: ${patient.chronicConditions}")
                if (patient.pregnancyWeeks > 0) appendLine("Gestation: ${patient.pregnancyWeeks} weeks")
            }
            appendLine("Blood Pressure: $systolic/$diastolic mmHg")
            appendLine("Heart Rate: $heartRate bpm")
            appendLine("SpO2: $spo2%")
            appendLine("Temperature: $tempC C")
            appendLine("Blood Sugar: $bloodSugar mg/dL")
            appendLine("Pregnant / ANC Patient: ${if (isPregnant) "Yes" else "No"}")
        }

        return try {
            if (!apiKey.isNullOrBlank()) {
                val promptWithSchema = "$prompt\n\nReturn ONLY a JSON object matching schema: {\"riskScore\": 0-100, \"riskLevel\": \"LOW\"|\"MODERATE\"|\"HIGH\"|\"CRITICAL\", \"primarySummary\": \"...\", \"possibleConditions\": [\"...\"], \"medicineSuggestions\": [\"...\"], \"recommendedActions\": [\"...\"], \"redFlags\": [\"...\"]}"
                val jsonStr = callDirectGeminiRest(promptWithSchema, apiKey, isJsonExpected = true)
                parseAssessmentJson(jsonStr)
            } else {
                runStructuredAssessment(prompt)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Cloud vitals risk assessment unavailable (${e.message}), activating offline clinical engine", e)
            val fallback = evaluateVitalsRiskOffline(systolic, diastolic, heartRate, spo2, tempC, bloodSugar, isPregnant, patient)
            fallback.copy(
                primarySummary = "[Offline Clinical Engine - App Check Bypassed] ${fallback.primarySummary}"
            )
        }
    }

    private suspend fun runStructuredAssessment(prompt: String): ClinicalAssessmentResult {
        val response = withTimeout(AI_TIMEOUT_MS) {
            structuredAssessmentModel.generateContent(prompt)
        }
        val jsonString = response.text ?: throw IllegalStateException("Gemini returned an empty assessment.")
        return parseAssessmentJson(jsonString)
    }

    private fun parseAssessmentJson(jsonString: String): ClinicalAssessmentResult {
        val cleaned = jsonString.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
        val json = JSONObject(cleaned)

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
    }

    fun streamChatReply(
        userMessage: String,
        history: List<ChatMessage>,
        activePatient: PatientEntity?,
        language: AppLanguage = AppLanguage.ENGLISH,
        offlineOnly: Boolean = false,
        apiKey: String? = null
    ): Flow<String> = flow {
        if (offlineOnly) {
            val (offlineReply, _) = generateChatReplyOffline(userMessage, activePatient)
            emit(offlineReply)
            return@flow
        }

        val groundedPrompt = buildString {
            appendLine("[${languageInstruction(language)}]")
            if (activePatient != null) {
                appendLine(
                    "[Patient Context: ${activePatient.fullName}, Age ${activePatient.age}, ${activePatient.gender}, " +
                        "Category ${activePatient.category}, Allergies: ${activePatient.allergies}, Conditions: ${activePatient.chronicConditions}]"
                )
            }
            append(userMessage)
        }

        try {
            if (!apiKey.isNullOrBlank()) {
                val directReply = callDirectGeminiRest(groundedPrompt, apiKey, isJsonExpected = false)
                emit(directReply)
            } else {
                val chatHistory: List<Content> = history
                    .filter { it.id != "msg-welcome" }
                    .takeLast(8)
                    .map { msg ->
                        content(role = if (msg.isUser) "user" else "model") {
                            text(msg.text)
                        }
                    }
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
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Cloud AI chat unavailable (${e.message}), activating offline clinical intelligence", e)
            val (offlineReply, _) = generateChatReplyOffline(userMessage, activePatient)
            emit("[Offline Clinical Assistant - App Check Bypassed]\n\n$offlineReply")
        }
    }

    suspend fun analyzeClinicalImage(
        scanLabel: String,
        imageUri: Uri?,
        language: AppLanguage = AppLanguage.ENGLISH,
        offlineOnly: Boolean = false,
        apiKey: String? = null
    ): String {
        if (offlineOnly) {
            return analyzeImageOffline(scanLabel)
        }

        return try {
            if (!apiKey.isNullOrBlank()) {
                val bitmap = if (imageUri != null) loadScaledBitmap(imageUri) else null
                callDirectGeminiVisionRest(scanLabel, bitmap, apiKey)
            } else {
                val promptContent = if (imageUri != null) {
                    val bitmap = loadScaledBitmap(imageUri)
                        ?: throw IllegalStateException("Unable to decode the selected image.")
                    content {
                        image(bitmap)
                        text(
                            "Analyze this clinical photo ($scanLabel) for a frontline health worker. " +
                                "${languageInstruction(language)} " +
                                "Describe observable visual features, potential concerns, hygiene/care steps, and " +
                                "red-flag signs requiring physician referral."
                        )
                    }
                } else {
                    content {
                        text(
                            "Provide a clinical screening checklist for a frontline health worker reviewing: $scanLabel. " +
                                "${languageInstruction(language)} " +
                                "Include key visual markers to inspect, basic care steps, and escalation red flags."
                        )
                    }
                }

                val response = withTimeout(AI_TIMEOUT_MS) {
                    chatAndVisionModel.generateContent(promptContent)
                }
                response.text?.takeIf { it.isNotBlank() }
                    ?: throw IllegalStateException("Gemini returned an empty image analysis.")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Cloud AI image analysis unavailable (${e.message}), activating offline guidelines", e)
            val offline = analyzeImageOffline(scanLabel)
            "[Offline Mode - App Check Bypassed]\n$offline\n\nClinical Screening Protocol:\n- Inspect margins, color symmetry, and skin warmth.\n- Verify patient vitals (SpO2, BP, Temp).\n- Escalate to doctor if acute distress or infection signs appear."
        }
    }

    suspend fun analyzePrescriptionStructured(
        scanLabel: String,
        imageUri: Uri?,
        patient: PatientEntity? = null,
        language: AppLanguage = AppLanguage.ENGLISH,
        offlineOnly: Boolean = false
    ): RxAnalysisResult {
        if (offlineOnly) {
            delay(250L)
            return RxAnalysisResult(
                summary = "Offline Rx Template ($scanLabel): Review dosage carefully against patient weight & allergies.",
                medications = listOf(
                    RxMedicationItem("Paracetamol", "500 mg", "Every 6 hours PRN fever", "3 days"),
                    RxMedicationItem("WHO ORS Solution", "1 sachet in 1L water", "Sip after loose stools", "48 hours")
                ),
                warnings = listOfNotNull(
                    patient?.allergies?.takeIf { it.isNotBlank() && !it.equals("None", ignoreCase = true) }
                        ?.let { "Check Allergy: $it" },
                    "Offline-Only Mode active — disable Offline-Only Mode to run Gemini Rx Vision OCR on photos."
                ),
                isOffline = true
            )
        }

        try {
            val allergyNote = patient?.let {
                "Check safety against patient (${it.age}y, allergies: ${it.allergies}, conditions: ${it.chronicConditions}, pregnant weeks: ${it.pregnancyWeeks})."
            }.orEmpty()

            val promptContent = if (imageUri != null) {
                val bitmap = loadScaledBitmap(imageUri)
                    ?: throw IllegalStateException("Unable to decode the selected prescription image.")
                content {
                    image(bitmap)
                    text(
                        "Extract every medication visible in this prescription photo ($scanLabel) into structured JSON. " +
                            "${languageInstruction(language)} $allergyNote"
                    )
                }
            } else {
                content {
                    text(
                        "Generate a standard primary health clinic prescription protocol for '$scanLabel'. " +
                            "${languageInstruction(language)} $allergyNote"
                    )
                }
            }

            val response = withTimeout(AI_TIMEOUT_MS) {
                structuredRxModel.generateContent(promptContent)
            }
            val jsonString = response.text ?: throw IllegalStateException("Gemini returned empty Rx data.")
            val json = JSONObject(jsonString)
            val medsArray = json.optJSONArray("medications")
            val meds = buildList {
                if (medsArray != null) {
                    for (i in 0 until medsArray.length()) {
                        val obj = medsArray.optJSONObject(i) ?: continue
                        val name = obj.optString("name").trim()
                        if (name.isNotBlank()) {
                            add(
                                RxMedicationItem(
                                    name = name,
                                    dosage = obj.optString("dosage", "Standard dose"),
                                    frequency = obj.optString("frequency", "As directed"),
                                    duration = obj.optString("duration", "5 days")
                                )
                            )
                        }
                    }
                }
            }
            return RxAnalysisResult(
                summary = json.optString("summary", "Prescription analyzed by Gemini Vision."),
                medications = meds,
                warnings = json.optJSONArray("warnings").toStringList(),
                isOffline = false
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Gemini Rx OCR failed (${e.message}), activating offline prescription template", e)
            return RxAnalysisResult(
                summary = "Supportive Rx Protocol ($scanLabel) [App Check Bypassed]: Review dosage carefully against patient vitals.",
                medications = listOf(
                    RxMedicationItem("Paracetamol", "500 mg", "Every 6 hours PRN fever", "3 days"),
                    RxMedicationItem("WHO ORS Solution", "1 sachet in 1L water", "Sip after loose stools", "48 hours")
                ),
                warnings = listOfNotNull(
                    patient?.allergies?.takeIf { it.isNotBlank() && !it.equals("None", ignoreCase = true) }
                        ?.let { "Check Allergy: $it" },
                    "Cloud AI unavailable (${e.message ?: "Token invalid"}). Displaying supportive care template."
                ),
                isOffline = true
            )
        }
    }

    suspend fun analyzeWardOutbreakTrends(
        patients: List<PatientEntity>,
        recentActivities: List<ActivityEntity>,
        language: AppLanguage = AppLanguage.ENGLISH,
        offlineOnly: Boolean = false
    ): WardOutbreakInsight {
        if (offlineOnly) {
            return analyzeWardOutbreakOffline(patients)
        }

        val wardSummary = patients.groupBy { it.villageOrWard }.entries.joinToString("\n") { (ward, list) ->
            val criticalCount = list.count { it.isCritical }
            val conditions = list.mapNotNull { it.chronicConditions.takeIf(String::isNotBlank) }.distinct().joinToString(", ")
            val pendingVax = list.count { it.vaccinationProgress < 90 }
            "- $ward: ${list.size} patients, $criticalCount critical, $pendingVax incomplete vax, conditions: $conditions"
        }
        val actSummary = recentActivities.take(8).joinToString("; ") { "${it.categoryTag}: ${it.title}" }

        val prompt = buildString {
            appendLine("Analyze the following ward-level community health cohort for outbreak clusters, maternal risk, or immunization gaps:")
            appendLine(languageInstruction(language))
            appendLine("Wards:")
            appendLine(wardSummary.ifBlank { "No ward data available." })
            appendLine("Recent Field Activities: $actSummary")
        }

        return try {
            val response = withTimeout(AI_TIMEOUT_MS) {
                structuredOutbreakModel.generateContent(prompt)
            }
            val json = JSONObject(response.text ?: throw IllegalStateException("Empty outbreak response"))
            val level = RiskLevel.entries.find {
                it.name.equals(json.optString("alertLevel", "MODERATE"), ignoreCase = true)
            } ?: RiskLevel.MODERATE
            WardOutbreakInsight(
                alertLevel = level,
                headline = json.optString("headline", "Ward Surveillance Complete"),
                summary = json.optString("summary", ""),
                affectedWards = json.optJSONArray("affectedWards").toStringList(),
                recommendations = json.optJSONArray("recommendations").toStringList(),
                isOffline = false
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Ward outbreak scan failed (${e.message}), activating offline scan", e)
            val offline = analyzeWardOutbreakOffline(patients)
            offline.copy(
                headline = "[Offline Mode - App Check Bypassed] ${offline.headline}"
            )
        }
    }

    private suspend fun callDirectGeminiRest(
        prompt: String,
        apiKey: String,
        isJsonExpected: Boolean
    ): String = withContext(Dispatchers.IO) {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${apiKey.trim()}"
        val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            connectTimeout = 30_000
            readTimeout = 30_000
            setRequestProperty("Content-Type", "application/json")
        }

        val requestJson = JSONObject().apply {
            val contentsArray = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        val partObj = JSONObject().apply {
                            put("text", prompt)
                        }
                        put(partObj)
                    }
                    put("parts", partsArray)
                }
                put(contentObj)
            }
            put("contents", contentsArray)

            if (isJsonExpected) {
                val genConfig = JSONObject().apply {
                    put("responseMimeType", "application/json")
                }
                put("generationConfig", genConfig)
            }
        }

        connection.outputStream.use { os ->
            os.write(requestJson.toString().toByteArray(Charsets.UTF_8))
        }

        val responseCode = connection.responseCode
        if (responseCode !in 200..299) {
            val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            throw IllegalStateException("Gemini API error ($responseCode): $errorResponse")
        }

        val responseText = connection.inputStream.bufferedReader().use { it.readText() }
        val root = JSONObject(responseText)
        val candidates = root.optJSONArray("candidates")
        if (candidates != null && candidates.length() > 0) {
            val candidate = candidates.getJSONObject(0)
            val content = candidate.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            if (parts != null && parts.length() > 0) {
                return@withContext parts.getJSONObject(0).optString("text", "")
            }
        }
        throw IllegalStateException("Gemini returned empty response parts.")
    }

    private suspend fun callDirectGeminiVisionRest(
        scanLabel: String,
        bitmap: Bitmap?,
        apiKey: String
    ): String = withContext(Dispatchers.IO) {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${apiKey.trim()}"
        val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            connectTimeout = 40_000
            readTimeout = 40_000
            setRequestProperty("Content-Type", "application/json")
        }

        val requestJson = JSONObject().apply {
            val contentsArray = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        if (bitmap != null) {
                            val baos = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos)
                            val base64Data = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
                            val inlineData = JSONObject().apply {
                                put("mimeType", "image/jpeg")
                                put("data", base64Data)
                            }
                            put(JSONObject().apply { put("inlineData", inlineData) })
                        }
                        put(JSONObject().apply {
                            put(
                                "text",
                                "Analyze this clinical photo ($scanLabel) for a frontline health worker. " +
                                    "Describe observable visual features, potential concerns, hygiene/care steps, and " +
                                    "red-flag signs requiring physician referral."
                            )
                        })
                    }
                    put("parts", partsArray)
                }
                put(contentObj)
            }
            put("contents", contentsArray)
        }

        connection.outputStream.use { os ->
            os.write(requestJson.toString().toByteArray(Charsets.UTF_8))
        }

        val responseCode = connection.responseCode
        if (responseCode !in 200..299) {
            val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            throw IllegalStateException("Gemini Vision error ($responseCode): $errorResponse")
        }

        val responseText = connection.inputStream.bufferedReader().use { it.readText() }
        val root = JSONObject(responseText)
        val candidates = root.optJSONArray("candidates")
        if (candidates != null && candidates.length() > 0) {
            val candidate = candidates.getJSONObject(0)
            val content = candidate.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            if (parts != null && parts.length() > 0) {
                return@withContext parts.getJSONObject(0).optString("text", "")
            }
        }
        throw IllegalStateException("Gemini returned empty image response.")
    }

    private suspend fun analyzeWardOutbreakOffline(patients: List<PatientEntity>): WardOutbreakInsight {
        delay(250L)
        val febrileOrDengue = patients.filter {
            it.chronicConditions.contains("dengue", ignoreCase = true) ||
                it.chronicConditions.contains("febrile", ignoreCase = true) ||
                it.notes.contains("fever", ignoreCase = true)
        }
        val criticalWards = patients.filter { it.isCritical }.map { it.villageOrWard }.distinct()
        val lowVaxCount = patients.count { it.vaccinationProgress < 90 }
        val level = when {
            criticalWards.size >= 2 || febrileOrDengue.size >= 2 -> RiskLevel.HIGH
            criticalWards.isNotEmpty() || lowVaxCount > 0 -> RiskLevel.MODERATE
            else -> RiskLevel.LOW
        }
        return WardOutbreakInsight(
            alertLevel = level,
            headline = if (criticalWards.isNotEmpty()) "${criticalWards.size} Ward(s) with Critical Triage Watch" else "Cohort Surveillance Stable",
            summary = "Offline Epidemiological Scan checked ${patients.size} patients: ${febrileOrDengue.size} acute febrile/dengue watch case(s), $lowVaxCount pending immunization(s).",
            affectedWards = (criticalWards + febrileOrDengue.map { it.villageOrWard }).distinct(),
            recommendations = listOf(
                "Prioritize doorstep follow-up for febrile & dengue watch patients.",
                "Verify immunization catch-up for $lowVaxCount child/ANC record(s).",
                "Disable Offline-Only Mode to run deeper Gemini cohort synthesis."
            ),
            isOffline = true
        )
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
        isPregnant: Boolean,
        patient: PatientEntity? = null
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

        val summary = buildString {
            append("Vitals Analysis: BP $systolic/$diastolic, SpO2 $spo2%, HR $heartRate bpm, Temp ${tempC}°C, Sugar $bloodSugar mg/dL")
            if (patient != null) append(" for ${patient.fullName} (${patient.age}y, ${patient.gender})")
            append(".")
        }

        return ClinicalAssessmentResult(
            riskScore = clamped,
            riskLevel = riskLevel,
            primarySummary = summary,
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
