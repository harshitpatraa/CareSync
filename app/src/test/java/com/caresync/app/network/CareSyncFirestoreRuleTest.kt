package com.caresync.app.network

import com.caresync.app.base.FirestoreEmulatorTestBase
import com.caresync.app.model.HealthRecordDoc
import com.caresync.app.model.NotificationDoc
import com.caresync.app.model.PatientCategory
import com.caresync.app.model.PatientDoc
import com.caresync.app.model.RecordType
import com.caresync.app.model.RiskLevel
import com.caresync.app.model.VitalsDoc
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class CareSyncFirestoreRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun authenticatedOwner_canCreateReadAndObservePatient() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = CareSyncFirestoreRepository(firestore)
        val patientId = "PT-${UUID.randomUUID().toString().take(8)}"

        val profileRes = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.ensureUserProfile(uid, "Dr. Priya Kulkarni", ALICE_EMAIL)
        }
        assertTrue(profileRes.isSuccess)

        val patient = PatientDoc(
            id = patientId,
            fullName = "Meena Kumari",
            age = 27L,
            gender = "Female",
            phone = "+91 98111 22222",
            villageOrWard = "Ward 4",
            bloodGroup = "O+",
            category = PatientCategory.PREGNANCY.name,
            riskLevel = RiskLevel.HIGH.name,
            critical = false,
            todayVisit = true,
            pendingVisit = false,
            familyMembersCount = 4L,
            pregnancyWeeks = 26L,
            childWeightKg = 0.0,
            vaccinationProgress = 100L,
            notes = "Routine antenatal checkup"
        )

        val upsertRes = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.upsertPatient(uid, patient)
        }
        assertTrue(upsertRes.exceptionOrNull()?.message ?: "upsertPatient failed", upsertRes.isSuccess)

        val fetched = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.getPatientById(uid, patientId).getOrThrow()
        }
        assertEquals("Meena Kumari", fetched?.fullName)

        val observed = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observePatients(uid).first { list -> list.any { it.id == patientId } }
        }
        assertTrue(observed.any { it.id == patientId })

        val vitalsId = "VT-${UUID.randomUUID().toString().take(8)}"
        val vitalsRes = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.upsertVitals(
                uid,
                VitalsDoc(
                    id = vitalsId,
                    patientId = patientId,
                    systolicBp = 128L,
                    diastolicBp = 82L,
                    heartRate = 80L,
                    temperatureC = 36.9,
                    spo2Percent = 98L,
                    respiratoryRate = 16L,
                    bloodSugarMgDl = 96L,
                    weightKg = 58.2,
                    recordedAtLabel = "Just now"
                )
            )
        }
        assertTrue(vitalsRes.exceptionOrNull()?.message ?: "upsertVitals failed", vitalsRes.isSuccess)

        val recordRes = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.upsertHealthRecord(
                uid,
                HealthRecordDoc(
                    id = "HR-${UUID.randomUUID().toString().take(8)}",
                    patientId = patientId,
                    patientName = "Meena Kumari",
                    recordType = RecordType.HISTORY.name,
                    title = "ANC Checkup",
                    subtitle = "Week 26 check",
                    detail = "Normal fetal movement",
                    dateLabel = "Today",
                    statusBadge = "Stable"
                )
            )
        }
        assertTrue(recordRes.exceptionOrNull()?.stackTraceToString() ?: "upsertHealthRecord failed", recordRes.isSuccess)

        val notifId = "NT-${UUID.randomUUID().toString().take(8)}"
        val notifRes = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.upsertNotification(
                uid,
                NotificationDoc(
                    id = notifId,
                    title = "Follow-Up Scheduled",
                    message = "Week 28 ANC visit pending",
                    timestampLabel = "Just now",
                    read = false,
                    patientId = patientId
                )
            )
        }
        assertTrue(notifRes.exceptionOrNull()?.message ?: "upsertNotification failed", notifRes.isSuccess)
    }

    @Test
    fun crossUserAccess_bobCannotReadAlicePatient_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = CareSyncFirestoreRepository(firestore)
        val patientId = "PT-${UUID.randomUUID().toString().take(8)}"

        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.upsertPatient(
                aliceUid,
                PatientDoc(
                    id = patientId,
                    fullName = "Private Patient",
                    age = 35L,
                    gender = "Male",
                    villageOrWard = "Ward 2",
                    bloodGroup = "A+"
                )
            ).getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = CareSyncFirestoreRepository(firestore)
        val res = withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.getPatientById(aliceUid, patientId)
        }
        assertTrue(res.isFailure)
        val firestoreEx = res.exceptionOrNull()?.unwrapFirestoreException()
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreEx?.code)
    }

    @Test
    fun unauthenticatedObservePatients_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repository = CareSyncFirestoreRepository(firestore)

        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observePatients("alice_unauth_target").first()
            }
            fail("Expected PERMISSION_DENIED when observing patients while signed out")
        } catch (e: Exception) {
            val firestoreEx = e.unwrapFirestoreException()
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreEx?.code)
        }
    }

    private companion object {
        const val ALICE_EMAIL = "alice.clinician@caresync.test"
        const val BOB_EMAIL = "bob.clinician@caresync.test"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
