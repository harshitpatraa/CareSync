package com.caresync.app

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.caresync.app.model.RiskLevel
import com.caresync.app.network.AiMedicalEngine
import com.caresync.app.utils.FormatUtils
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
}
