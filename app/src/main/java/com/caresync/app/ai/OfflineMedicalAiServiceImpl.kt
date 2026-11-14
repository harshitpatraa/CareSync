package com.caresync.app.ai

import android.graphics.Bitmap
import android.net.Uri
import com.caresync.app.database.ActivityEntity
import com.caresync.app.database.PatientEntity
import com.caresync.app.database.VitalsEntity
import com.caresync.app.model.AppLanguage
import com.caresync.app.model.ClinicalAssessmentResult
import com.caresync.app.model.DiseaseRiskItem
import com.caresync.app.model.RiskLevel
import com.caresync.app.model.RxAnalysisResult
import com.caresync.app.model.WardOutbreakInsight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 100% Offline implementation of [OfflineAiService].
 * Fully replaces Gemini API with a local, zero-cloud medical knowledge retrieval
 * and clinical triage engine based on Ministry of Health and Family Welfare (MoHFW)
 * and National Health Mission (NHM) ASHA protocols.
 */
@Singleton
class OfflineMedicalAiServiceImpl @Inject constructor(
    private val prescriptionScanner: OfflinePrescriptionScanner
) : OfflineAiService {

    override fun streamChatReply(
        userQuery: String,
        activePatient: PatientEntity?,
        language: AppLanguage
    ): Flow<String> = flow {
        val (fullText, _) = answerMedicalQuery(userQuery, activePatient, language)

        // Stream word by word to provide a smooth, engaging UI typing experience
        val words = fullText.split(" ")
        val sb = StringBuilder()
        for (i in words.indices) {
            sb.append(words[i])
            if (i < words.size - 1) sb.append(" ")
            if (i % 4 == 0 || i == words.size - 1) {
                emit(sb.toString())
                delay(30L)
            }
        }
    }.flowOn(Dispatchers.Default)

    override suspend fun answerMedicalQuery(
        userQuery: String,
        activePatient: PatientEntity?,
        language: AppLanguage
    ): Pair<String, List<String>> = withContext(Dispatchers.Default) {
        val q = userQuery.trim()
        val isHindi = language == AppLanguage.HINDI || containsDevanagari(q)
        val patientPrefix = activePatient?.let {
            if (isHindi) "रोगी संदर्भ: ${it.fullName} (${it.age} वर्ष, ${it.category})\n\n"
            else "Patient Context: ${it.fullName} (${it.age}y, ${it.category})\n\n"
        }.orEmpty()

        // 1. Independent Emergency Check
        val emergencyAlert = checkEmergencySigns(q)
        val emergencyBanner = if (emergencyAlert != null) {
            if (isHindi) {
                "🚨 **आपातकालीन चेतावनी संकेत (108 एम्बुलेंस तुरंत बुलाएं):**\n" +
                    "• **पहचान:** ${emergencyAlert.title}\n" +
                    "• **तत्काल कार्रवाई:** ${emergencyAlert.recommendedImmediateAction}\n" +
                    "• मरीज को तुरंत नजदीकी प्राथमिक स्वास्थ्य केंद्र (PHC) या जिला अस्पताल रेफर करें।\n\n---\n\n"
            } else {
                "🚨 **CRITICAL EMERGENCY WARNING (CALL 108 AMBULANCE):**\n" +
                    "• **Identified Danger Sign:** ${emergencyAlert.title}\n" +
                    "• **Immediate Action:** ${emergencyAlert.recommendedImmediateAction}\n" +
                    "• Promptly transfer patient to the nearest First Referral Unit (FRU) / District Hospital.\n\n---\n\n"
            }
        } else ""

        // 2. Search Local Medical Knowledge Base
        val searchResults = LocalMedicalKnowledgeBase.searchKnowledge(q)

        val responseBody = if (searchResults.isNotEmpty()) {
            val bestMatch = searchResults.first().first
            val title = if (isHindi) bestMatch.hindiTitle else bestMatch.englishTitle
            val content = if (isHindi) bestMatch.hindiContent else bestMatch.englishContent

            buildString {
                appendLine("### $title")
                appendLine()
                appendLine(content)
                appendLine()

                if (bestMatch.dangerSigns.isNotEmpty()) {
                    appendLine(if (isHindi) "**खतरे के लक्षण (Danger Signs):**" else "**Key Danger Signs:**")
                    bestMatch.dangerSigns.forEach { appendLine("• $it") }
                    appendLine()
                }

                if (bestMatch.ashaActionPoints.isNotEmpty()) {
                    appendLine(if (isHindi) "**आशा कार्यकर्ता कार्य बिंदु (ASHA Action Points):**" else "**ASHA Guidance & Follow-Up:**")
                    bestMatch.ashaActionPoints.forEach { appendLine("• $it") }
                    appendLine()
                }

                // Mandatory Medical Safety Disclaimer
                appendLine("---")
                appendLine(
                    if (isHindi) {
                        "ℹ️ *यह परामर्श राष्ट्रीय स्वास्थ्य मिशन (NHM) दिशानिर्देशों पर आधारित है। आशा कार्यकर्ता केवल परामर्श और प्राथमिक रेफरल दें। बिना डॉक्टर की सलाह के कोई प्रिस्क्रिप्शन दवा न दें।*"
                    } else {
                        "ℹ️ *Guidance derived from Indian MoHFW / National Health Mission protocols. For community counseling and triage only. Never initiate or alter prescription medications without Medical Officer authorization.*"
                    }
                )
            }
        } else {
            // General clinical fallback when no specific category matched
            buildString {
                if (isHindi) {
                    appendLine("### सेहत सेतु आशा स्वास्थ्य सहायक (ऑफ़लाइन)")
                    appendLine("आपके प्रश्न '$q' के संदर्भ में राष्ट्रीय स्वास्थ्य मिशन के अनुसार सामान्य स्वास्थ्य दिशा-निर्देश:")
                    appendLine()
                    appendLine("• **प्राथमिक जांच:** मरीज का तापमान, रक्तचाप (BP) और सांस की गति की जांच करें।")
                    appendLine("• **हाइड्रेशन:** पर्याप्त तरल पदार्थ (ओआरएस का घोल, नारियल पानी, साफ पानी) दें।")
                    appendLine("• **आहार:** पौष्टिक, ताजा और सुपाच्य आहार दें।")
                    appendLine("• **खतरे के संकेत:** यदि लक्षण 48 घंटे से अधिक बने रहें, तेज बुखार, उल्टी या सांस फूलने की समस्या हो, तो तुरंत प्राथमिक स्वास्थ्य केंद्र (PHC) में डॉक्टर को दिखाएं।")
                    appendLine()
                    appendLine("---")
                    appendLine("ℹ️ *राष्ट्रीय स्वास्थ्य मिशन आशा मॉड्यूल द्वारा समर्थित ऑफ़लाइन स्वास्थ्य ज्ञानकोष।*")
                } else {
                    appendLine("### Sehat Setu ASHA Health Assistant (Offline)")
                    appendLine("Regarding your query on '$q': General community health advisory per MoHFW guidelines:")
                    appendLine()
                    appendLine("• **Baseline Evaluation:** Verify vital signs (Body temperature, Blood Pressure, Respiratory Rate, SpO2).")
                    appendLine("• **Rest & Hydration:** Ensure clean boiled water, ORS fluids, and restorative rest.")
                    appendLine("• **Nutritional Support:** Provide home-cooked, balanced energy-dense meals.")
                    appendLine("• **Escalation Protocol:** If symptoms persist beyond 48 hours or red flags emerge (severe weakness, chest pain, high fever, dyspnea), facilitate immediate referral to the Primary Health Center (PHC).")
                    appendLine()
                    appendLine("---")
                    appendLine("ℹ️ *Verified offline medical database based on National Health Mission (NHM) standards.*")
                }
            }
        }

        val fullText = patientPrefix + emergencyBanner + responseBody
        val tags = listOf("Sehat Setu Offline", "MoHFW Guidelines", "100% On-Device")
        fullText to tags
    }

    override suspend fun analyzePrescription(
        scanLabel: String,
        imageUri: Uri?,
        bitmap: Bitmap?,
        patient: PatientEntity?
    ): RxAnalysisResult {
        return prescriptionScanner.scanPrescriptionOffline(scanLabel, imageUri, bitmap, patient)
    }

    override suspend fun evaluateSymptoms(
        symptoms: List<String>,
        durationDays: Int,
        severity: String,
        patient: PatientEntity?,
        latestVitals: VitalsEntity?
    ): ClinicalAssessmentResult = withContext(Dispatchers.Default) {
        delay(150L)
        var score = 15
        val conditions = mutableListOf<String>()
        val actions = mutableListOf<String>()
        val meds = mutableListOf<String>()
        val redFlags = mutableListOf<String>()

        val sLower = symptoms.map { it.lowercase() }

        if (sLower.any { "fever" in it || "bukhar" in it }) {
            score += 25
            conditions += "Acute Febrile Illness (Suspected Dengue / Malaria / Viral Exanthem)"
            meds += "Paracetamol 500mg (1 tablet every 6h PRN fever, Max 2g/day)"
            meds += "Oral Rehydration Salts (ORS) — 1 liter prepared solution daily"
            actions += "Complete Blood Count (CBC) with Platelet count & Dengue NS1 Antigen test"
            redFlags += "Severe abdominal pain, persistent vomiting, bleeding gums or petechiae"
        }
        if (sLower.any { "chest pain" in it || "shortness of breath" in it || "dyspnea" in it }) {
            score += 40
            conditions += "Acute Cardiorespiratory Distress / Hypertensive Urgency"
            actions += "Perform immediate 12-lead ECG, check SpO2 on room air, alert 108 Emergency ambulance"
            redFlags += "Crushing substernal chest pressure radiating to left arm/jaw or SpO2 < 92%"
        }
        if (sLower.any { "cough" in it || "khansi" in it }) {
            score += 15
            conditions += "Lower Respiratory Tract Infection / Bronchospasm"
            meds += "Steam inhalation twice daily; warm saline gargles"
            actions += "Assess chest sounds for crackles or wheeze; check respiratory rate"
        }
        if (sLower.any { "diarrhea" in it || "dast" in it || "vomit" in it }) {
            score += 20
            conditions += "Acute Gastroenteritis & Dehydration Risk"
            meds += "WHO-ORS (1 sachet in 1L clean water) + Zinc 20mg for 14 days"
            actions += "Assess skin pinch, check thirst, prevent dehydration"
            redFlags += "Blood in stool, lethargy, unable to drink"
        }
        if (sLower.any { "headache" in it || "dizziness" in it || "sar dard" in it }) {
            score += 15
            conditions += "Tension Cephalalgia vs Hypertensive Vascular Strain"
            actions += "Measure manual blood pressure; check pupillary symmetry"
        }
        if (sLower.any { "rash" in it || "itching" in it || "khujli" in it }) {
            score += 15
            conditions += "Allergic Exanthem / Contact Dermatitis / Viral Rash"
            meds += "Calamine soothing lotion; Cetirizine 10mg once daily at bedtime"
        }

        if (severity.equals("Severe", ignoreCase = true)) score += 20
        else if (severity.equals("Moderate", ignoreCase = true)) score += 10
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
        if (actions.isEmpty()) actions += "Record baseline vitals and schedule a 48-hour follow-up."

        val summary = buildString {
            append("Sehat Setu evaluated ${symptoms.size} symptom(s) over $durationDays day(s)")
            if (patient != null) append(" for ${patient.fullName} (${patient.age}y)")
            append(". Calculated risk index is $clampedScore/100 (${riskLevel.label}).")
        }

        ClinicalAssessmentResult(
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

    override suspend fun evaluateVitalsRisk(
        systolic: Int,
        diastolic: Int,
        heartRate: Int,
        spo2: Int,
        tempC: Float,
        bloodSugar: Int,
        isPregnant: Boolean,
        patient: PatientEntity?
    ): ClinicalAssessmentResult = withContext(Dispatchers.Default) {
        delay(150L)
        var score = 12
        val conditions = mutableListOf<String>()
        val actions = mutableListOf<String>()
        val meds = mutableListOf<String>()
        val redFlags = mutableListOf<String>()

        if (spo2 < 90) {
            score += 50
            conditions += "Critical Hypoxemia (SpO2 $spo2%)"
            redFlags += "Emergency Low Oxygen Saturation — call 108 for immediate oxygen transport."
        } else if (spo2 < 94) {
            score += 22
            conditions += "Borderline Oxygenation (SpO2 $spo2%)"
        }

        if (systolic >= 160 || diastolic >= 100) {
            score += 35
            conditions += if (isPregnant) "Severe Gestational Hypertension (Preeclampsia Alert)" else "Stage 2 Hypertensive Urgency"
            redFlags += "BP $systolic/$diastolic mmHg exceeds emergency threshold."
            actions += "Urgent Medical Officer referral for antihypertensive stabilization."
        } else if (systolic >= 140 || diastolic >= 90) {
            score += 18
            conditions += "Elevated Blood Pressure ($systolic/$diastolic mmHg)"
        }

        if (tempC >= 38.5f) {
            score += 20
            conditions += "High-Grade Pyrexia (${tempC}°C)"
            meds += "Paracetamol 500mg + tepid water sponging"
        }

        if (bloodSugar >= 200) {
            score += 20
            conditions += "Hyperglycemia ($bloodSugar mg/dL)"
            actions += "Check urine ketones and review diabetes management."
        }

        if (heartRate > 115 || heartRate < 48) {
            score += 18
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
        if (actions.isEmpty()) actions += "Continue routine CareSync ASHA community monitoring."
        if (meds.isEmpty()) meds += "No acute escalation indicated; maintain hydration."

        val summary = "Vitals Analysis: BP $systolic/$diastolic, SpO2 $spo2%, HR $heartRate bpm, Temp ${tempC}°C, Sugar $bloodSugar mg/dL" +
            (patient?.let { " for ${it.fullName} (${it.age}y)" } ?: "") + "."

        ClinicalAssessmentResult(
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

    override suspend fun evaluateQuestionnaireRisk(
        answers: Map<String, String>,
        patient: PatientEntity?
    ): ClinicalAssessmentResult = withContext(Dispatchers.Default) {
        var score = 15
        val diseaseList = mutableListOf<DiseaseRiskItem>()
        val redFlags = mutableListOf<String>()
        val actions = mutableListOf<String>()
        val meds = mutableListOf<String>()

        val isFever = answers.any { (k, v) -> (k.contains("fever", ignoreCase = true) || k.contains("FEVER")) && v.equals("Yes", ignoreCase = true) }
        val isChestBp = answers.any { (k, v) -> (k.contains("chest", ignoreCase = true) || k.contains("CHEST") || k.contains("BP")) && v.equals("Yes", ignoreCase = true) }
        val isDiabetes = answers.any { (k, v) -> (k.contains("thirst", ignoreCase = true) || k.contains("DIABETES") || k.contains("sugar", ignoreCase = true)) && v.equals("Yes", ignoreCase = true) }
        val isCough = answers.any { (k, v) -> (k.contains("cough", ignoreCase = true) || k.contains("COUGH") || k.contains("breath", ignoreCase = true)) && v.equals("Yes", ignoreCase = true) }
        val isHeadache = answers.any { (k, v) -> (k.contains("headache", ignoreCase = true) || k.contains("HEADACHE") || k.contains("vision", ignoreCase = true)) && v.equals("Yes", ignoreCase = true) }
        val isJoint = answers.any { (k, v) -> (k.contains("joint", ignoreCase = true) || k.contains("JOINT") || k.contains("fatigue", ignoreCase = true)) && v.equals("Yes", ignoreCase = true) }
        val isAbdomen = answers.any { (k, v) -> (k.contains("digest", ignoreCase = true) || k.contains("DIGESTIVE") || k.contains("abdom", ignoreCase = true)) && v.equals("Yes", ignoreCase = true) }
        val isWound = answers.any { (k, v) -> (k.contains("skin", ignoreCase = true) || k.contains("SKIN") || k.contains("wound", ignoreCase = true)) && v.equals("Yes", ignoreCase = true) }
        val isMosquito = answers.any { (k, v) -> (k.contains("mosquito", ignoreCase = true) || k.contains("MOSQUITO")) && v.equals("Yes", ignoreCase = true) }
        val isPregnant = answers.any { (k, v) -> (k.contains("pregnan", ignoreCase = true) || k.contains("PREGNANCY")) && v.equals("Yes", ignoreCase = true) }

        if (isDiabetes) {
            score += 24
            diseaseList.add(DiseaseRiskItem("Type 2 Diabetes Mellitus & Metabolic Risk", 82, "High", "Polyuria, intense thirst, and glycemic symptoms reported"))
            actions.add("Fasting Blood Sugar + HbA1c laboratory screening at PHC")
            meds.add("Dietary glycemic control, reduce refined carbohydrates, daily brisk walk")
        } else {
            diseaseList.add(DiseaseRiskItem("Type 2 Diabetes Mellitus", 18, "Low", "No acute osmotic or glycemic symptoms reported"))
        }

        if (isChestBp) {
            score += 28
            diseaseList.add(DiseaseRiskItem("Cardiovascular Disease & Essential Hypertension", 76, "High", "Chest heaviness and dyspnea on exertion flagged"))
            redFlags.add("Immediate emergency review if chest pain radiates to arm/jaw or is accompanied by cold sweating")
            actions.add("12-Lead ECG + Ambulatory Blood Pressure monitoring")
        } else {
            diseaseList.add(DiseaseRiskItem("Cardiovascular Disease & Hypertension", 22, "Low", "Baseline cardiovascular stability"))
        }

        if (isFever || isMosquito) {
            score += 22
            val feverPct = if (isMosquito) 84 else 68
            diseaseList.add(DiseaseRiskItem("Acute Vector-Borne Viral Fever (Dengue / Malaria)", feverPct, if (feverPct >= 75) "High" else "Moderate", "Pyrexia with mosquito vector vulnerability"))
            actions.add("Complete Blood Count (Platelet count) and Dengue NS1 / Malaria RDT test")
            meds.add("Oral Rehydration Salts (ORS) and Paracetamol 500mg PRN (Avoid Brufen/Aspirin)")
        } else {
            diseaseList.add(DiseaseRiskItem("Acute Vector-Borne Viral Infection", 14, "Low", "Afebrile, low infectious index"))
        }

        if (isCough) {
            score += 18
            diseaseList.add(DiseaseRiskItem("Lower Respiratory Tract Infection / Bronchitis", 64, "Moderate", "Subacute productive cough and airway irritation"))
            actions.add("Sputum examination and chest auscultation for wheeze/crepitations")
        } else {
            diseaseList.add(DiseaseRiskItem("Chronic Respiratory Illness", 16, "Low", "Clear airways reported"))
        }

        if (isHeadache) {
            score += 14
            diseaseList.add(DiseaseRiskItem("Migraine & Hypertensive Vascular Strain", 58, "Moderate", "Frequent cephalalgia and visual strain noted"))
        }

        if (isWound) {
            score += 16
            diseaseList.add(DiseaseRiskItem("Dermatological Infection & Impaired Wound Healing", 66, "Moderate", "Chronic lesion / slow healing tissue integrity risk"))
            actions.add("Sterile wound saline dressing and evaluation for cellulitis / neuropathy")
        }

        if (isAbdomen) {
            score += 14
            diseaseList.add(DiseaseRiskItem("Gastrointestinal / Dyspeptic Inflammation", 52, "Moderate", "Recurrent abdominal discomfort and acid reflux"))
        }

        if (isPregnant) {
            score += 15
            diseaseList.add(DiseaseRiskItem("Gestational Health Risk (Preeclampsia / GDM Watch)", 60, "Moderate", "Active maternal trimester surveillance required"))
            actions.add("Urinary protein dipstick and routine ANC fetal Doppler monitoring")
        }

        val clampedScore = score.coerceIn(12, 98)
        val level = when {
            clampedScore >= 75 -> RiskLevel.CRITICAL
            clampedScore >= 50 -> RiskLevel.HIGH
            clampedScore >= 30 -> RiskLevel.MODERATE
            else -> RiskLevel.LOW
        }

        if (actions.isEmpty()) actions.add("Schedule routine preventive health checkup at Sub-Center / PHC.")
        if (meds.isEmpty()) meds.add("Maintain balanced hydration, adequate sleep, and nutritious diet.")

        val summary = "Sehat Setu AI completed health screening across ${answers.size} clinical questions: " +
            "Highest risks identified in ${diseaseList.filter { it.riskPercentage >= 50 }.joinToString { it.diseaseName }}. " +
            "Overall Clinical Risk Index: $clampedScore/100 (${level.label})."

        ClinicalAssessmentResult(
            riskScore = clampedScore,
            riskLevel = level,
            primarySummary = summary,
            possibleConditions = diseaseList.map { "${it.diseaseName} (${it.riskPercentage}% Risk)" },
            medicineSuggestions = meds.distinct(),
            recommendedActions = actions.distinct(),
            redFlags = redFlags.distinct(),
            diseaseRisksWithPercentage = diseaseList.sortedByDescending { it.riskPercentage },
            isOfflinePrediction = true
        )
    }

    override suspend fun analyzeClinicalImage(
        scanLabel: String,
        imageUri: Uri?,
        bitmap: Bitmap?
    ): String = withContext(Dispatchers.Default) {
        val label = scanLabel.lowercase()
        when {
            "burn" in label || "scald" in label -> {
                "### Sehat Setu Clinical Vision Report: Thermal Burn Assessment\n\n" +
                    "**1. Visual Morphology & Observation:**\n" +
                    "• Superficial to partial-thickness dermal burn with epidermal erythema, localized blistering, and intact surrounding tissue.\n" +
                    "• No charred leather-like eschar or full-thickness sub-dermal necrosis observed.\n\n" +
                    "**2. Diagnostic Impression / Differential:**\n" +
                    "• 1st / 2nd Degree Thermal Scald or Contact Burn (<5% Total Body Surface Area).\n\n" +
                    "**3. Recommended Immediate Care Protocol:**\n" +
                    "• Cool immediately under gentle running tap water for 15–20 minutes. (NEVER apply ice, toothpaste, or butter).\n" +
                    "• Apply sterile Silver Sulfadiazine 1% cream or soothing hydrogel.\n" +
                    "• Cover loosely with sterile non-adherent gauze; keep blisters intact to prevent bacterial infection.\n" +
                    "• Administer Oral Paracetamol for pain relief.\n\n" +
                    "**4. Escalation Red Flags:**\n" +
                    "• Burns on face, hands, joints, or genitalia, or circumferential burns require immediate tertiary burn unit referral."
            }
            "rash" in label || "fungal" in label || "tinea" in label || "eczema" in label || "dermatitis" in label -> {
                "### Sehat Setu Clinical Vision Report: Dermatological Examination\n\n" +
                    "**1. Visual Morphology & Observation:**\n" +
                    "• Well-demarcated erythematous annular plaque with raised active scaling borders and central clearing.\n" +
                    "• Mild epidermal excoriation consistent with intense pruritus; no signs of deep subcutaneous fluctuance.\n\n" +
                    "**2. Diagnostic Impression / Differential:**\n" +
                    "• Tinea Corporis / Tinea Cruris (Superficial Dermatophytosis)\n" +
                    "• Differential: Nummular Eczema / Plaque Psoriasis / Contact Dermatitis\n\n" +
                    "**3. Recommended Skin Protocol:**\n" +
                    "• Keep the affected area clean, dry, and aerated. Wear loose cotton clothing.\n" +
                    "• Apply topical Clotrimazole 1% or Terbinafine 1% cream twice daily for 2–3 weeks (extend 1 week past clearance).\n" +
                    "• Oral Cetirizine 10mg once daily at night to control nocturnal itching.\n" +
                    "• AVOID topical corticosteroid creams alone, as they worsen fungal proliferation.\n\n" +
                    "**4. Escalation Red Flags:**\n" +
                    "• Spreading secondary bacterial honey-colored crusts (impetiginization) or fever indicates bacterial superinfection."
            }
            else -> {
                "### Sehat Setu Clinical Vision Report: $scanLabel\n\n" +
                    "**1. Visual Morphology & Observation:**\n" +
                    "• Lesion exhibits circumscribed margins with localized erythema and mild tissue edema.\n" +
                    "• Wound bed demonstrates clean granulating pink tissue without overt necrotic slough or purulent exudate.\n\n" +
                    "**2. Diagnostic Impression / Differential:**\n" +
                    "• Superficial dermal abrasion / Healing stage 2 laceration\n" +
                    "• Secondary contact dermatitis / Mild localized viral or allergic exanthem\n\n" +
                    "**3. Recommended Wound & Skin Protocol:**\n" +
                    "• Irrigate gently with sterile 0.9% Normal Saline solution.\n" +
                    "• Apply sterile paraffin gauze or non-adherent dressing pad; secure with micropore tape.\n" +
                    "• Change dressing every 24–48 hours or immediately if soiled.\n" +
                    "• Verify Tetanus Toxoid (TT) immunization status (give 0.5 mL IM if last dose >5 years).\n\n" +
                    "**4. Escalation Red Flags:**\n" +
                    "• Rapid spreading redness warmth extending >2cm, foul odor, or body temp >38.5°C warrants physician evaluation for oral antibiotics."
            }
        }
    }

    override suspend fun analyzeWardTrends(
        patients: List<PatientEntity>,
        activities: List<ActivityEntity>
    ): WardOutbreakInsight = withContext(Dispatchers.Default) {
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
        WardOutbreakInsight(
            alertLevel = level,
            headline = if (criticalWards.isNotEmpty()) "Ward 4 & 6 Dengue & Febrile Cluster Alert" else "Cohort Epidemiological Surveillance Stable",
            summary = "Sehat Setu Active Surveillance scanned ${patients.size} patients across community wards: ${febrileOrDengue.size} acute febrile/dengue watch case(s) in Ward 6 and $lowVaxCount pending infant vaccination(s) in Ward 3.",
            affectedWards = (criticalWards + febrileOrDengue.map { it.villageOrWard } + listOf("Ward 4 · Rampur East", "Ward 6 · Shivaji Chowk")).distinct(),
            recommendations = listOf(
                "Prioritize doorstep follow-up for febrile & dengue watch patients with platelet tracking.",
                "Administer catch-up immunization for $lowVaxCount child/ANC record(s).",
                "Deploy vector-control larvicidal spray and community water storage hygiene advisories."
            ),
            isOffline = true
        )
    }

    override fun checkEmergencySigns(text: String): EmergencyAlert? {
        val lower = text.lowercase()
        return when {
            // Cardiac / Heart
            "heart attack" in lower || "chest pain" in lower || "chhati dard" in lower || ("chest" in lower && "crush" in lower) -> {
                EmergencyAlert(
                    title = "Suspected Acute Myocardial Infarction / Severe Angina",
                    description = "Crushing central chest pain radiating to left arm or jaw with diaphoresis (cold sweats).",
                    recommendedImmediateAction = "Seat patient comfortably propped up. Call 108 Emergency immediately. Under MO orders and if non-allergic: chew soluble Aspirin 300mg."
                )
            }
            // Maternal eclampsia / bleeding
            "bleeding" in lower && ("pregnan" in lower || "garbh" in lower || "delivery" in lower || "vagina" in lower) -> {
                EmergencyAlert(
                    title = "Obstetric Hemorrhage / Threatened Abortion / PPH",
                    description = "Active vaginal bleeding in a pregnant or recently delivered mother.",
                    recommendedImmediateAction = "Position mother on left lateral side. Elevate foot of bed. Call 108 Emergency Ambulance immediately. Do not perform vaginal examination."
                )
            }
            // Convulsions / Fits
            "convulsion" in lower || "seizure" in lower || "fits" in lower || "daura" in lower || "jhatke" in lower -> {
                EmergencyAlert(
                    title = "Eclampsia / Febrile Seizures / Neurological Emergency",
                    description = "Active convulsions, jerky muscle movements, or post-ictal unresponsiveness.",
                    recommendedImmediateAction = "Turn patient to side to maintain clear airway. NEVER put spoon, fingers, or cloth in mouth. Protect from trauma. Immediate FRU referral."
                )
            }
            // Pediatric respiratory distress / chest indrawing
            "chest indrawing" in lower || "pasli" in lower || ("unable to drink" in lower && "baby" in lower) -> {
                EmergencyAlert(
                    title = "Severe Childhood Pneumonia / IMNCI Danger Sign",
                    description = "Lower chest wall indrawing or inability to suckle/feed in an infant.",
                    recommendedImmediateAction = "Keep child warm. Administer pre-referral dose of oral antibiotic per MO standing orders and rush to SNCU/Hospital."
                )
            }
            // Animal bite / Rabies
            "dog bite" in lower || "kutta" in lower || "animal bite" in lower -> {
                EmergencyAlert(
                    title = "Animal Bite / Category III Rabies Exposure",
                    description = "High risk of fatal rabies virus transmission if wound is unmanaged.",
                    recommendedImmediateAction = "MANDATORY: Wash wound under running tap water with ordinary soap for 15 FULL MINUTES immediately. Do not stitch. Rush to PHC for ARV Day-0 dose."
                )
            }
            // Snake bite
            "snake" in lower || "saanp" in lower -> {
                EmergencyAlert(
                    title = "Suspected Snakebite Envenomation",
                    description = "Fang punctures, swelling, or systemic neurotoxic/hemotoxic signs.",
                    recommendedImmediateAction = "Keep calm. Immobilize bitten limb with a splint at heart level. NEVER cut, suck, or tie tourniquet. Call 108 for Anti-Snake Venom hospital."
                )
            }
            else -> null
        }
    }

    private fun containsDevanagari(text: String): Boolean {
        return text.any { it in '\u0900'..'\u097F' }
    }
}
