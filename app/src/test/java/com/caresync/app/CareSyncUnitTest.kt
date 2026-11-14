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
}
