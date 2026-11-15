package com.caresync.app

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.caresync.app.model.AppLanguage
import com.caresync.app.model.RiskLevel
import com.caresync.app.network.AiMedicalEngine
import com.caresync.app.utils.FormatUtils
import com.caresync.app.utils.LocationHelper
import com.caresync.app.utils.QrUtils
import com.caresync.app.utils.stringsForLanguage
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CareSyncUnitTest {
    @Test
    fun initials_handlesMultipleWords() {
        assertEquals("MD", FormatUtils.initials("Meera Devi"))
        assertEquals("PK", FormatUtils.initials("Priya Kulkarni"))
    }

    @Test
    fun appName_isCareSync() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertEquals("CareSync", context.getString(R.string.app_name))
    }

    @Test
    fun offlineAiEngine_flagsCriticalSpO2() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val engine = AiMedicalEngine(context)
        val result = engine.evaluateVitalsRiskOffline(
            systolic = 165,
            diastolic = 100,
            heartRate = 115,
            spo2 = 89,
            tempC = 37.2f,
            bloodSugar = 210,
            isPregnant = false
        )
        assertEquals(RiskLevel.CRITICAL, result.riskLevel)
        assertTrue(result.redFlags.isNotEmpty())
    }

    @Test
    fun offlineAiEngine_evaluatesQuestionsRiskAccurately() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val engine = AiMedicalEngine(context)
        val answers = mapOf(
            "fever" to "Yes",
            "mosquito_exposure" to "Yes",
            "sugar_thirst" to "No",
            "chest_pain" to "No"
        )
        val result = engine.evaluateQuestionsRiskOffline(answers)
        assertTrue(result.diseaseRisksWithPercentage.isNotEmpty())
        val dengue = result.diseaseRisksWithPercentage.find { it.diseaseName.contains("Dengue", ignoreCase = true) }
        assertTrue(dengue != null && dengue.riskPercentage >= 70)
    }

    @Test
    fun qrUtils_extractsPatientIdFromStructuredAndRawPayloads() {
        assertEquals("PT-1001", QrUtils.extractPatientIdFromQr("CARESYNC|PT-1001|Meera Devi|BG:O+|ALLERGY:None|RISK:HIGH"))
        assertEquals("PT-A1B2C3", QrUtils.extractPatientIdFromQr("Patient ID: PT-A1B2C3"))
        assertNull(QrUtils.extractPatientIdFromQr("INVALID_QR_PAYLOAD"))
    }

    @Test
    fun locationHelper_computesHaversineDistanceAccurately() {
        val km = LocationHelper.haversineKm(28.6139, 77.2090, 28.6289, 77.2195)
        assertTrue(km > 1.5 && km < 2.5)
    }

    @Test
    fun localization_returnsDistinctTranslationsForAllSupportedLanguages() {
        val en = stringsForLanguage(AppLanguage.ENGLISH)
        val hi = stringsForLanguage(AppLanguage.HINDI)
        val mr = stringsForLanguage(AppLanguage.MARATHI)
        val ta = stringsForLanguage(AppLanguage.TAMIL)
        val bn = stringsForLanguage(AppLanguage.BENGALI)
        assertEquals("Home", en.navHome)
        assertTrue(hi.navHome != en.navHome && mr.navHome != en.navHome && ta.navHome != en.navHome && bn.navHome != en.navHome)
    }

    @Test
    fun offlineAiService_answersHindiPregnancyQuery() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val service = com.caresync.app.ai.OfflineMedicalAiServiceImpl(
            com.caresync.app.ai.OfflinePrescriptionScanner(context)
        )
        val (response, tags) = service.answerMedicalQuery(
            userQuery = "गर्भावस्था में क्या खाना चाहिए और IFA की गोली कब लेनी चाहिए?",
            activePatient = null,
            language = AppLanguage.HINDI
        )
        assertTrue(response.isNotBlank())
        assertTrue(response.contains("गर्भावस्था") || response.contains("ANC") || response.contains("आहार") || response.contains("IFA"))
        assertTrue(tags.isNotEmpty())
    }

    @Test
    fun offlineAiService_answersEnglishImmunizationQuery() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val service = com.caresync.app.ai.OfflineMedicalAiServiceImpl(
            com.caresync.app.ai.OfflinePrescriptionScanner(context)
        )
        val (response, _) = service.answerMedicalQuery(
            userQuery = "What vaccines should a newborn child receive at birth?",
            activePatient = null,
            language = AppLanguage.ENGLISH
        )
        assertTrue(response.isNotBlank())
        assertTrue(response.contains("BCG", ignoreCase = true) || response.contains("OPV", ignoreCase = true) || response.contains("Hepatitis B", ignoreCase = true))
    }

    @Test
    fun offlineAiService_detectsEmergencyWarningSigns() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val service = com.caresync.app.ai.OfflineMedicalAiServiceImpl(
            com.caresync.app.ai.OfflinePrescriptionScanner(context)
        )
        val warning = service.checkEmergencySigns("Patient has severe chest pain radiating to left arm and cold sweating")
        assertTrue(warning != null)
        assertEquals("CRITICAL", warning!!.severityLevel)
    }

    @Test
    fun localMedicineDatabase_fuzzyMatchesMisspelledOcr() {
        val matches = com.caresync.app.ai.LocalMedicineDatabase.findMedicineMatches("Paracetmol 500mg tab")
        assertTrue(matches.isNotEmpty())
        val pcm = matches.find { it.matchedMedicine.genericName == "Paracetamol" }
        assertTrue(pcm != null)
        assertTrue(pcm!!.confidencePercent >= 80)
    }

    @Test
    fun localMedicalKnowledgeBase_coversAllMandatoryAshaDomains() {
        val allTopics = com.caresync.app.ai.LocalMedicalKnowledgeBase.knowledgeItems
        assertTrue(allTopics.size >= 10)
        val categories = allTopics.map { it.category }.toSet()
        assertTrue(categories.any { it.contains("Pregnancy", ignoreCase = true) })
        assertTrue(categories.any { it.contains("Newborn", ignoreCase = true) })
        assertTrue(categories.any { it.contains("Immunization", ignoreCase = true) })
        assertTrue(categories.any { it.contains("Anemia", ignoreCase = true) })
        assertTrue(categories.any { it.contains("Emergency", ignoreCase = true) })
    }

    @Test
    fun localModelManager_validatesGgufMagicHeaderSuccessfully() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val validGgufStream = java.io.ByteArrayInputStream(
            byteArrayOf(
                0x47, 0x47, 0x55, 0x46, // 'G', 'G', 'U', 'F'
                0x03, 0x00, 0x00, 0x00  // Version 3 in Little Endian uint32
            )
        )
        val manager = com.caresync.app.ai.LocalModelManager(context)
        val result = manager.validateStreamGgufHeader(validGgufStream)
        assertTrue(result.isValid)
        assertEquals(3, result.version)
        assertNull(result.errorReason)
    }

    @Test
    fun localModelManager_rejectsNonGgufHeader() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val invalidStream = java.io.ByteArrayInputStream("NOT_A_VALID_GGUF_FILE".toByteArray())
        val manager = com.caresync.app.ai.LocalModelManager(context)
        val result = manager.validateStreamGgufHeader(invalidStream)
        assertTrue(!result.isValid)
        assertTrue(result.errorReason != null && result.errorReason!!.contains("Invalid magic"))
    }

    @Test
    fun localModelManager_formatsBytesAccurately() {
        val formatted = com.caresync.app.ai.LocalModelManager.formatBytes(491400032L)
        assertTrue(formatted.contains("468") || formatted.contains("491") || formatted.contains("MB"))
    }

    @Test
    fun localModelManager_validatesActualDesktopModelIfPresent() {
        val desktopModel = java.io.File("C:\\Users\\Harshit\\Desktop\\qwen2.5-0.5b-instruct-q4_k_m.gguf")
        if (desktopModel.exists()) {
            val context = ApplicationProvider.getApplicationContext<Context>()
            val manager = com.caresync.app.ai.LocalModelManager(context)
            val result = manager.validateGgufHeader(desktopModel)
            assertTrue("Desktop Qwen GGUF model must have valid header", result.isValid)
            assertEquals(3, result.version)
        }
    }
}
