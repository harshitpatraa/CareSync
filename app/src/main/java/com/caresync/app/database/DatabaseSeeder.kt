package com.caresync.app.database

import com.caresync.app.model.NotificationType
import com.caresync.app.model.PatientCategory
import com.caresync.app.model.RecordType
import com.caresync.app.model.RiskLevel
import com.caresync.app.model.SyncQueueStatus
import com.caresync.app.model.SyncStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val patientDao: PatientDao,
    private val vitalsDao: VitalsDao,
    private val healthRecordDao: HealthRecordDao,
    private val syncQueueDao: SyncQueueDao,
    private val notificationDao: NotificationDao,
    private val activityDao: ActivityDao
) {
    suspend fun seedIfEmpty() {
        if (patientDao.countPatients() > 0) return
        val now = System.currentTimeMillis()

        val patients = listOf(
            PatientEntity(
                id = "PT-1001",
                fullName = "Meera Devi",
                age = 26,
                gender = "Female",
                phone = "+91 98765 41001",
                villageOrWard = "Ward 4 · Rampur East",
                bloodGroup = "O+",
                category = PatientCategory.PREGNANCY.name,
                riskLevel = RiskLevel.HIGH.name,
                isCritical = false,
                isTodayVisit = true,
                isPendingVisit = true,
                syncStatus = SyncStatus.PENDING_UPLOAD.name,
                lastVisitLabel = "Today, 09:15 AM",
                nextFollowUpDate = "18 Nov 2026",
                allergies = "Penicillin",
                chronicConditions = "Mild Gestational Anemia (Hb 9.8 g/dL)",
                familyHeadName = "Rajesh Kumar",
                familyMembersCount = 4,
                pregnancyWeeks = 28,
                expectedDueDate = "06 Feb 2027",
                vaccinationProgress = 100,
                notes = "Third trimester ANC visit completed offline. Iron + Folic Acid supplemented; BP borderline elevated.",
                updatedAt = now - 600_000L
            ),
            PatientEntity(
                id = "PT-1002",
                fullName = "Aarav Sharma",
                age = 3,
                gender = "Male",
                phone = "+91 98112 41002",
                villageOrWard = "Sector 2 · Green Park",
                bloodGroup = "B+",
                category = PatientCategory.CHILD.name,
                riskLevel = RiskLevel.LOW.name,
                isCritical = false,
                isTodayVisit = true,
                isPendingVisit = false,
                syncStatus = SyncStatus.SYNCED.name,
                lastVisitLabel = "Today, 08:30 AM",
                nextFollowUpDate = "24 Nov 2026",
                allergies = "None reported",
                chronicConditions = "None",
                familyHeadName = "Vikram Sharma",
                familyMembersCount = 5,
                childWeightKg = 14.2f,
                vaccinationProgress = 85,
                notes = "Growth percentile steady (62nd). DPT Booster scheduled next week.",
                updatedAt = now - 1_800_000L
            ),
            PatientEntity(
                id = "PT-1003",
                fullName = "Harish Verma",
                age = 62,
                gender = "Male",
                phone = "+91 98204 41003",
                villageOrWard = "Ward 7 · Old Mill Colony",
                bloodGroup = "A+",
                category = PatientCategory.EMERGENCY.name,
                riskLevel = RiskLevel.CRITICAL.name,
                isCritical = true,
                isTodayVisit = true,
                isPendingVisit = true,
                syncStatus = SyncStatus.PENDING_UPLOAD.name,
                lastVisitLabel = "Today, 10:05 AM",
                nextFollowUpDate = "Immediate Referral",
                allergies = "Sulfa drugs",
                chronicConditions = "Hypertension, Type 2 Diabetes",
                familyHeadName = "Harish Verma",
                familyMembersCount = 3,
                vaccinationProgress = 100,
                notes = "Reported chest tightness & SpO2 drop to 90%. Flagged Critical for immediate PHC transfer.",
                updatedAt = now - 300_000L
            ),
            PatientEntity(
                id = "PT-1004",
                fullName = "Kavita Patel",
                age = 31,
                gender = "Female",
                phone = "+91 97650 41004",
                villageOrWard = "Ward 1 · Lotus Nagar",
                bloodGroup = "AB+",
                category = PatientCategory.PREGNANCY.name,
                riskLevel = RiskLevel.MODERATE.name,
                isCritical = false,
                isTodayVisit = false,
                isPendingVisit = true,
                syncStatus = SyncStatus.SYNCED.name,
                lastVisitLabel = "Yesterday, 04:20 PM",
                nextFollowUpDate = "16 Nov 2026",
                allergies = "None",
                chronicConditions = "Gestational Diabetes Screen Pending",
                familyHeadName = "Suresh Patel",
                familyMembersCount = 6,
                pregnancyWeeks = 21,
                expectedDueDate = "28 Mar 2027",
                vaccinationProgress = 90,
                notes = "TT-2 booster administered. Fetal heart rate 144 bpm.",
                updatedAt = now - 86_400_000L
            ),
            PatientEntity(
                id = "PT-1005",
                fullName = "Ananya Nair",
                age = 1,
                gender = "Female",
                phone = "+91 98451 41005",
                villageOrWard = "Ward 3 · Lakeview",
                bloodGroup = "O-",
                category = PatientCategory.CHILD.name,
                riskLevel = RiskLevel.MODERATE.name,
                isCritical = false,
                isTodayVisit = true,
                isPendingVisit = false,
                syncStatus = SyncStatus.OFFLINE_DRAFT.name,
                lastVisitLabel = "Today, 11:00 AM",
                nextFollowUpDate = "19 Nov 2026",
                allergies = "Dust sensitivity",
                chronicConditions = "Mild seasonal wheeze",
                familyHeadName = "Deepak Nair",
                familyMembersCount = 4,
                childWeightKg = 9.4f,
                vaccinationProgress = 72,
                notes = "MMR-1 due in 5 days. Nebulizer spacer demo provided to mother.",
                updatedAt = now - 180_000L
            ),
            PatientEntity(
                id = "PT-1006",
                fullName = "Rameshwar Joshi",
                age = 54,
                gender = "Male",
                phone = "+91 98901 41006",
                villageOrWard = "Ward 5 · Station Road",
                bloodGroup = "B-",
                category = PatientCategory.CHRONIC.name,
                riskLevel = RiskLevel.LOW.name,
                isCritical = false,
                isTodayVisit = false,
                isPendingVisit = false,
                syncStatus = SyncStatus.SYNCED.name,
                lastVisitLabel = "12 Nov 2026",
                nextFollowUpDate = "26 Nov 2026",
                allergies = "Aspirin",
                chronicConditions = "Controlled Hypertension",
                familyHeadName = "Rameshwar Joshi",
                familyMembersCount = 2,
                vaccinationProgress = 100,
                notes = "Medication adherence steady. BP well controlled on Amlodipine 5mg.",
                updatedAt = now - 172_800_000L
            ),
            PatientEntity(
                id = "PT-1007",
                fullName = "Sunita Rao",
                age = 44,
                gender = "Female",
                phone = "+91 98333 41007",
                villageOrWard = "Ward 6 · Shivaji Chowk",
                bloodGroup = "A-",
                category = PatientCategory.GENERAL.name,
                riskLevel = RiskLevel.HIGH.name,
                isCritical = true,
                isTodayVisit = false,
                isPendingVisit = true,
                syncStatus = SyncStatus.CONFLICT.name,
                lastVisitLabel = "13 Nov 2026",
                nextFollowUpDate = "15 Nov 2026",
                allergies = "Ibuprofen",
                chronicConditions = "Acute Febrile Illness (Dengue Watch)",
                familyHeadName = "Prakash Rao",
                familyMembersCount = 5,
                vaccinationProgress = 100,
                notes = "Platelet count re-check scheduled; hydration ORS advised.",
                updatedAt = now - 108_000_000L
            ),
            PatientEntity(
                id = "PT-1008",
                fullName = "Rohan Das",
                age = 29,
                gender = "Male",
                phone = "+91 98710 41008",
                villageOrWard = "Ward 2 · Canal View",
                bloodGroup = "O+",
                category = PatientCategory.GENERAL.name,
                riskLevel = RiskLevel.LOW.name,
                isCritical = false,
                isTodayVisit = true,
                isPendingVisit = false,
                syncStatus = SyncStatus.SYNCED.name,
                lastVisitLabel = "Today, 07:50 AM",
                nextFollowUpDate = "30 Nov 2026",
                allergies = "None",
                chronicConditions = "Post-viral recovery",
                familyHeadName = "Suman Das",
                familyMembersCount = 3,
                vaccinationProgress = 100,
                notes = "Afebrile for 48 hours. Wound dressing clean.",
                updatedAt = now - 3_600_000L
            )
        )
        patientDao.upsertPatients(patients)

        val vitals = listOf(
            VitalsEntity("VT-1", "PT-1001", 136, 86, 88, 36.9f, 98, 18, 108, 61.5f, "Today, 09:15 AM", now - 600_000L, SyncStatus.PENDING_UPLOAD.name),
            VitalsEntity("VT-2", "PT-1001", 124, 80, 82, 36.7f, 99, 17, 102, 60.8f, "02 Nov 2026", now - 900_000_000L, SyncStatus.SYNCED.name),
            VitalsEntity("VT-3", "PT-1002", 96, 62, 104, 36.8f, 99, 24, 88, 14.2f, "Today, 08:30 AM", now - 1_800_000L, SyncStatus.SYNCED.name),
            VitalsEntity("VT-4", "PT-1003", 164, 98, 112, 37.3f, 90, 26, 214, 74.0f, "Today, 10:05 AM", now - 300_000L, SyncStatus.PENDING_UPLOAD.name),
            VitalsEntity("VT-5", "PT-1004", 118, 76, 80, 36.6f, 99, 16, 116, 58.0f, "Yesterday, 04:20 PM", now - 86_400_000L, SyncStatus.SYNCED.name),
            VitalsEntity("VT-6", "PT-1005", 92, 60, 118, 37.6f, 96, 28, 86, 9.4f, "Today, 11:00 AM", now - 180_000L, SyncStatus.OFFLINE_DRAFT.name),
            VitalsEntity("VT-7", "PT-1006", 128, 80, 72, 36.5f, 98, 16, 104, 71.2f, "12 Nov 2026", now - 172_800_000L, SyncStatus.SYNCED.name),
            VitalsEntity("VT-8", "PT-1007", 112, 72, 98, 38.9f, 96, 22, 99, 55.4f, "13 Nov 2026", now - 108_000_000L, SyncStatus.CONFLICT.name),
            VitalsEntity("VT-9", "PT-1008", 118, 78, 74, 36.6f, 99, 16, 92, 68.5f, "Today, 07:50 AM", now - 3_600_000L, SyncStatus.SYNCED.name)
        )
        vitalsDao.insertAllVitals(vitals)

        val records = listOf(
            HealthRecordEntity("HR-1", "PT-1001", "Meera Devi", RecordType.TIMELINE.name, "ANC Week 28 Checkup", "Fundal height 28cm, fetal movement active", "Administered IFA tablets & calcium 500mg", "14 Nov 2026", "Completed", SyncStatus.PENDING_UPLOAD.name, now - 500_000L),
            HealthRecordEntity("HR-2", "PT-1001", "Meera Devi", RecordType.MEDICINE.name, "Ferrous Sulfate + Folic Acid", "1 tablet once daily after lunch", "30-day supply dispensed offline", "14 Nov 2026", "Active", SyncStatus.PENDING_UPLOAD.name, now - 490_000L),
            HealthRecordEntity("HR-3", "PT-1001", "Meera Devi", RecordType.VACCINATION.name, "Td-2 Maternal Booster", "Tetanus & Diphtheria toxoid 0.5 mL IM", "Completed on schedule", "02 Nov 2026", "Vaccinated", SyncStatus.SYNCED.name, now - 900_000_000L),
            HealthRecordEntity("HR-4", "PT-1002", "Aarav Sharma", RecordType.VACCINATION.name, "Pentavalent-3 + OPV-3", "Routine pediatric immunization", "Next due: DPT Booster at 18-24m", "05 Nov 2026", "Vaccinated", SyncStatus.SYNCED.name, now - 750_000_000L),
            HealthRecordEntity("HR-5", "PT-1002", "Aarav Sharma", RecordType.TIMELINE.name, "Pediatric Growth Screening", "Height 94cm · Weight 14.2kg · MUAC 15.2cm", "Normal nutritional status", "14 Nov 2026", "Normal", SyncStatus.SYNCED.name, now - 1_700_000L),
            HealthRecordEntity("HR-6", "PT-1003", "Harish Verma", RecordType.HISTORY.name, "Acute Dyspnea & Elevated BP", "BP 164/98 · SpO2 90% · RBS 214 mg/dL", "Oxygen mask applied; PHC emergency referral slip generated", "14 Nov 2026", "Critical", SyncStatus.PENDING_UPLOAD.name, now - 250_000L),
            HealthRecordEntity("HR-7", "PT-1003", "Harish Verma", RecordType.MEDICINE.name, "Telmisartan 40mg + Metformin 500mg", "Morning & evening chronic regimen", "Review dose at District Hospital", "10 Nov 2026", "Active", SyncStatus.SYNCED.name, now - 340_000_000L),
            HealthRecordEntity("HR-8", "PT-1003", "Harish Verma", RecordType.DOCUMENT.name, "ECG Strip & Referral Slip #REF-882", "Local PDF snapshot saved offline", "Ready for hospital handoff", "14 Nov 2026", "Saved Offline", SyncStatus.PENDING_UPLOAD.name, now - 240_000L),
            HealthRecordEntity("HR-9", "PT-1005", "Ananya Nair", RecordType.VACCINATION.name, "MMR Dose 1 (Scheduled)", "Measles-Mumps-Rubella 0.5 mL SC", "Due on 19 Nov 2026", "19 Nov 2026", "Upcoming", SyncStatus.OFFLINE_DRAFT.name, now - 170_000L),
            HealthRecordEntity("HR-10", "PT-1006", "Rameshwar Joshi", RecordType.MEDICINE.name, "Amlodipine 5mg", "1 tablet daily at 8:00 AM", "Adherence 96% over 30 days", "12 Nov 2026", "Active", SyncStatus.SYNCED.name, now - 170_000_000L),
            HealthRecordEntity("HR-11", "PT-1007", "Sunita Rao", RecordType.DOCUMENT.name, "CBC & NS1 Antigen Lab Report", "Platelets: 118,000 /µL · NS1 Positive", "Re-test CBC tomorrow morning", "13 Nov 2026", "Follow-up", SyncStatus.CONFLICT.name, now - 100_000_000L)
        )
        healthRecordDao.insertRecords(records)

        val queueItems = listOf(
            SyncQueueEntity("SQ-1", "Patient & ANC", "PT-1001", "Meera Devi", "UPDATE", "ANC Week 28 Vitals + IFA Prescription", "09:15 AM", now - 580_000L, 0, SyncQueueStatus.QUEUED.name),
            SyncQueueEntity("SQ-2", "Critical Referral", "PT-1003", "Harish Verma", "UPLOAD_RECORD", "Emergency Vitals & Referral #REF-882", "10:05 AM", now - 290_000L, 0, SyncQueueStatus.QUEUED.name),
            SyncQueueEntity("SQ-3", "Child Draft", "PT-1005", "Ananya Nair", "CREATE", "Pediatric Triage & MMR Schedule", "11:00 AM", now - 175_000L, 0, SyncQueueStatus.QUEUED.name),
            SyncQueueEntity("SQ-4", "Lab Attachment", "PT-1007", "Sunita Rao", "UPLOAD_RECORD", "Dengue CBC Lab Sync Retry", "Yesterday", now - 90_000_000L, 1, SyncQueueStatus.FAILED.name)
        )
        syncQueueDao.enqueueAll(queueItems)

        val notifications = listOf(
            NotificationEntity("NT-1", "Critical SpO2 Alert: Harish Verma", "SpO2 dropped to 90% with BP 164/98. Immediate PHC transfer advised.", NotificationType.EMERGENCY.name, "10 mins ago", false, "PT-1003", now - 600_000L),
            NotificationEntity("NT-2", "Medicine Reminder: Meera Devi", "Verify Ferrous Sulfate + Folic Acid evening dose & monitor ankle edema.", NotificationType.MEDICINE.name, "35 mins ago", false, "PT-1001", now - 2_100_000L),
            NotificationEntity("NT-3", "Vaccination Due: Ananya Nair", "MMR Dose 1 immunization scheduled for 19 Nov 2026 at Ward 3 Clinic.", NotificationType.VACCINATION.name, "2 hrs ago", false, "PT-1005", now - 7_200_000L),
            NotificationEntity("NT-4", "4 Offline Records Pending Sync", "Connect to network or tap Sync Now to upload queued patient records.", NotificationType.SYNC.name, "3 hrs ago", false, null, now - 10_800_000L),
            NotificationEntity("NT-5", "Follow-Up Scheduled: Sunita Rao", "Platelet count re-evaluation & hydration check due tomorrow.", NotificationType.FOLLOW_UP.name, "Yesterday", true, "PT-1007", now - 86_400_000L)
        )
        notificationDao.insertNotifications(notifications)

        val activities = listOf(
            ActivityEntity("AC-1", "Critical flag raised for Harish Verma", "SpO2 90% · Referral #REF-882 queued offline", "10:05 AM", "Emergency", now - 300_000L),
            ActivityEntity("AC-2", "ANC Week 28 logged for Meera Devi", "Vitals & IFA prescription saved locally", "09:15 AM", "Pregnancy", now - 600_000L),
            ActivityEntity("AC-3", "Growth chart updated for Aarav Sharma", "Weight 14.2 kg · Synced to CareSync hub", "08:30 AM", "Child", now - 1_800_000L),
            ActivityEntity("AC-4", "Morning rounds synced", "12 patient records verified with cloud ledger", "07:45 AM", "Sync", now - 3_900_000L)
        )
        activityDao.insertActivities(activities)
    }
}
