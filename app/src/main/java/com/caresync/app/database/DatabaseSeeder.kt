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
        val existingPatients = patientDao.countPatients()
        val existingActivities = activityDao.getAllActivitiesOnce()
        // If database is already well-populated, do not overwrite
        if (existingPatients >= 8 && existingActivities.size >= 10) return

        val now = System.currentTimeMillis()
        val oneHour = 3_600_000L
        val oneDay = 86_400_000L

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
                updatedAt = now - oneDay
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
                updatedAt = now - 2 * oneDay
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
                updatedAt = now - 3 * oneDay
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
                updatedAt = now - oneHour
            ),
            PatientEntity(
                id = "PT-1009",
                fullName = "Fatima Begum",
                age = 58,
                gender = "Female",
                phone = "+91 98402 41009",
                villageOrWard = "Ward 4 · Rampur East",
                bloodGroup = "B+",
                category = PatientCategory.CHRONIC.name,
                riskLevel = RiskLevel.HIGH.name,
                isCritical = false,
                isTodayVisit = true,
                isPendingVisit = true,
                syncStatus = SyncStatus.PENDING_UPLOAD.name,
                lastVisitLabel = "Today, 11:45 AM",
                nextFollowUpDate = "20 Nov 2026",
                allergies = "Codeine",
                chronicConditions = "Type 2 Diabetes, Diabetic Foot Ulcer Grade 1",
                familyHeadName = "Mohammed Begum",
                familyMembersCount = 6,
                vaccinationProgress = 100,
                notes = "Right foot plantar ulcer 1.2cm, granulating well with sterile saline dressing. Fasting sugar 174 mg/dL.",
                updatedAt = now - 30 * 60_000L
            ),
            PatientEntity(
                id = "PT-1010",
                fullName = "Vikram Singh",
                age = 67,
                gender = "Male",
                phone = "+91 98199 41010",
                villageOrWard = "Ward 7 · Old Mill Colony",
                bloodGroup = "AB-",
                category = PatientCategory.CHRONIC.name,
                riskLevel = RiskLevel.MODERATE.name,
                isCritical = false,
                isTodayVisit = false,
                isPendingVisit = true,
                syncStatus = SyncStatus.SYNCED.name,
                lastVisitLabel = "11 Nov 2026",
                nextFollowUpDate = "22 Nov 2026",
                allergies = "None",
                chronicConditions = "COPD Stage 2, Chronic Bronchitis",
                familyHeadName = "Vikram Singh",
                familyMembersCount = 3,
                vaccinationProgress = 95,
                notes = "Peak expiratory flow stable. Salbutamol inhaler spacer adherence re-demonstrated.",
                updatedAt = now - 4 * oneDay
            )
        )
        patientDao.upsertPatients(patients)

        val vitals = listOf(
            VitalsEntity("VT-1", "PT-1001", 136, 86, 88, 36.9f, 98, 18, 108, 61.5f, "Today, 09:15 AM", now - 600_000L, SyncStatus.PENDING_UPLOAD.name),
            VitalsEntity("VT-2", "PT-1001", 124, 80, 82, 36.7f, 99, 17, 102, 60.8f, "Yesterday, 10:00 AM", now - oneDay + 2 * oneHour, SyncStatus.SYNCED.name),
            VitalsEntity("VT-3", "PT-1002", 96, 62, 104, 36.8f, 99, 24, 88, 14.2f, "Today, 08:30 AM", now - 1_800_000L, SyncStatus.SYNCED.name),
            VitalsEntity("VT-4", "PT-1002", 98, 64, 100, 36.9f, 99, 22, 90, 14.0f, "3 Days Ago", now - 3 * oneDay + 4 * oneHour, SyncStatus.SYNCED.name),
            VitalsEntity("VT-5", "PT-1003", 164, 98, 112, 37.3f, 90, 26, 214, 74.0f, "Today, 10:05 AM", now - 300_000L, SyncStatus.PENDING_UPLOAD.name),
            VitalsEntity("VT-6", "PT-1003", 158, 94, 96, 37.1f, 92, 22, 198, 74.5f, "Yesterday, 05:00 PM", now - oneDay + 6 * oneHour, SyncStatus.SYNCED.name),
            VitalsEntity("VT-7", "PT-1004", 118, 76, 80, 36.6f, 99, 16, 116, 58.0f, "Yesterday, 04:20 PM", now - oneDay, SyncStatus.SYNCED.name),
            VitalsEntity("VT-8", "PT-1004", 114, 72, 78, 36.5f, 99, 16, 110, 57.5f, "4 Days Ago", now - 4 * oneDay + oneHour, SyncStatus.SYNCED.name),
            VitalsEntity("VT-9", "PT-1005", 92, 60, 118, 37.6f, 96, 28, 86, 9.4f, "Today, 11:00 AM", now - 180_000L, SyncStatus.OFFLINE_DRAFT.name),
            VitalsEntity("VT-10", "PT-1005", 94, 62, 110, 37.2f, 98, 24, 90, 9.3f, "2 Days Ago", now - 2 * oneDay + 3 * oneHour, SyncStatus.SYNCED.name),
            VitalsEntity("VT-11", "PT-1006", 128, 80, 72, 36.5f, 98, 16, 104, 71.2f, "2 Days Ago", now - 2 * oneDay, SyncStatus.SYNCED.name),
            VitalsEntity("VT-12", "PT-1006", 132, 84, 74, 36.6f, 98, 16, 108, 71.5f, "5 Days Ago", now - 5 * oneDay + 2 * oneHour, SyncStatus.SYNCED.name),
            VitalsEntity("VT-13", "PT-1007", 112, 72, 98, 38.9f, 96, 22, 99, 55.4f, "3 Days Ago", now - 3 * oneDay, SyncStatus.CONFLICT.name),
            VitalsEntity("VT-14", "PT-1007", 116, 74, 92, 38.2f, 97, 20, 102, 55.0f, "Yesterday, 02:00 PM", now - oneDay + 3 * oneHour, SyncStatus.SYNCED.name),
            VitalsEntity("VT-15", "PT-1008", 118, 78, 74, 36.6f, 99, 16, 92, 68.5f, "Today, 07:50 AM", now - oneHour, SyncStatus.SYNCED.name),
            VitalsEntity("VT-16", "PT-1008", 120, 80, 76, 36.8f, 99, 16, 96, 68.0f, "6 Days Ago", now - 6 * oneDay + 5 * oneHour, SyncStatus.SYNCED.name),
            VitalsEntity("VT-17", "PT-1009", 142, 88, 84, 36.8f, 98, 18, 174, 65.0f, "Today, 11:45 AM", now - 30 * 60_000L, SyncStatus.PENDING_UPLOAD.name),
            VitalsEntity("VT-18", "PT-1009", 146, 90, 86, 37.0f, 97, 18, 188, 65.2f, "2 Days Ago", now - 2 * oneDay + 4 * oneHour, SyncStatus.SYNCED.name),
            VitalsEntity("VT-19", "PT-1010", 130, 82, 88, 36.7f, 93, 22, 122, 62.0f, "4 Days Ago", now - 4 * oneDay, SyncStatus.SYNCED.name),
            VitalsEntity("VT-20", "PT-1010", 128, 80, 84, 36.6f, 94, 20, 118, 62.5f, "6 Days Ago", now - 6 * oneDay + 2 * oneHour, SyncStatus.SYNCED.name)
        )
        vitalsDao.insertAllVitals(vitals)

        val records = listOf(
            HealthRecordEntity("HR-1", "PT-1001", "Meera Devi", RecordType.TIMELINE.name, "ANC Week 28 Checkup", "Fundal height 28cm, fetal movement active", "Administered IFA tablets & calcium 500mg", "Today", "Completed", SyncStatus.PENDING_UPLOAD.name, now - 500_000L),
            HealthRecordEntity("HR-2", "PT-1001", "Meera Devi", RecordType.MEDICINE.name, "Ferrous Sulfate + Folic Acid", "1 tablet once daily after lunch", "30-day supply dispensed offline", "Today", "Active", SyncStatus.PENDING_UPLOAD.name, now - 490_000L),
            HealthRecordEntity("HR-3", "PT-1001", "Meera Devi", RecordType.DOCUMENT.name, "Obstetric Ultrasound Summary", "Single live intrauterine gestation, cephalic, adequate liquor", "Verified by Dr. Priya Kulkarni", "Yesterday", "Verified", SyncStatus.SYNCED.name, now - oneDay),
            HealthRecordEntity("HR-4", "PT-1002", "Aarav Sharma", RecordType.VACCINATION.name, "Pentavalent-3 + OPV-3", "Routine pediatric immunization", "Next due: DPT Booster at 18-24m", "Yesterday", "Vaccinated", SyncStatus.SYNCED.name, now - oneDay + oneHour),
            HealthRecordEntity("HR-5", "PT-1002", "Aarav Sharma", RecordType.TIMELINE.name, "Pediatric Growth Screening", "Height 94cm · Weight 14.2kg · MUAC 15.2cm", "Normal green growth zone", "Today", "Normal", SyncStatus.SYNCED.name, now - 1_700_000L),
            HealthRecordEntity("HR-6", "PT-1003", "Harish Verma", RecordType.HISTORY.name, "Acute Dyspnea & Elevated BP", "BP 164/98 · SpO2 90% · RBS 214 mg/dL", "Oxygen mask applied; PHC emergency referral slip generated", "Today", "Critical", SyncStatus.PENDING_UPLOAD.name, now - 250_000L),
            HealthRecordEntity("HR-7", "PT-1003", "Harish Verma", RecordType.MEDICINE.name, "Telmisartan 40mg + Metformin 500mg", "Morning & evening chronic regimen", "Review dose at District Hospital", "2 Days Ago", "Active", SyncStatus.SYNCED.name, now - 2 * oneDay),
            HealthRecordEntity("HR-8", "PT-1003", "Harish Verma", RecordType.DOCUMENT.name, "ECG Strip & Referral Slip #REF-882", "Sinus tachycardia with ST depression, saved offline", "Ready for hospital handoff", "Today", "Saved Offline", SyncStatus.PENDING_UPLOAD.name, now - 240_000L),
            HealthRecordEntity("HR-9", "PT-1004", "Kavita Patel", RecordType.VACCINATION.name, "TT-2 Maternal Booster", "Tetanus toxoid 0.5 mL IM left deltoid", "Next ANC scheduled for 24 weeks", "Yesterday", "Completed", SyncStatus.SYNCED.name, now - oneDay + 2 * oneHour),
            HealthRecordEntity("HR-10", "PT-1004", "Kavita Patel", RecordType.DOCUMENT.name, "Blood Sugar Glucose Tolerance Test", "Fasting 86 mg/dL · 2hr Post 75g: 134 mg/dL", "Within normal limits for gestational age", "3 Days Ago", "Normal", SyncStatus.SYNCED.name, now - 3 * oneDay),
            HealthRecordEntity("HR-11", "PT-1005", "Ananya Nair", RecordType.VACCINATION.name, "MMR Dose 1 (Scheduled)", "Measles-Mumps-Rubella 0.5 mL SC", "Due on 19 Nov 2026", "19 Nov 2026", "Upcoming", SyncStatus.OFFLINE_DRAFT.name, now - 170_000L),
            HealthRecordEntity("HR-12", "PT-1005", "Ananya Nair", RecordType.MEDICINE.name, "Salbutamol Respirator Solution (Spacer)", "2 puffs via pediatric mask PRN wheezing", "Demonstrated correct spacer technique to mother", "Today", "Active", SyncStatus.OFFLINE_DRAFT.name, now - 160_000L),
            HealthRecordEntity("HR-13", "PT-1006", "Rameshwar Joshi", RecordType.MEDICINE.name, "Amlodipine 5mg", "1 tablet daily at 8:00 AM", "Adherence 96% over 30 days", "2 Days Ago", "Active", SyncStatus.SYNCED.name, now - 2 * oneDay + oneHour),
            HealthRecordEntity("HR-14", "PT-1007", "Sunita Rao", RecordType.DOCUMENT.name, "CBC & NS1 Antigen Lab Report", "Platelets: 118,000 /µL · NS1 Positive", "Re-test CBC tomorrow morning", "3 Days Ago", "Follow-up", SyncStatus.CONFLICT.name, now - 3 * oneDay),
            HealthRecordEntity("HR-15", "PT-1007", "Sunita Rao", RecordType.MEDICINE.name, "WHO Oral Rehydration Salts (ORS)", "1 sachet in 1 liter clean water, sip frequently", "Avoid NSAIDs like Brufen or Aspirin", "3 Days Ago", "Active", SyncStatus.SYNCED.name, now - 3 * oneDay + 2 * oneHour),
            HealthRecordEntity("HR-16", "PT-1008", "Rohan Das", RecordType.TIMELINE.name, "Dermal Wound Review & Dressing", "Linear forearm laceration 3cm, edges approximated", "Clean granulation bed, no signs of cellulitis", "Today", "Healing", SyncStatus.SYNCED.name, now - oneHour),
            HealthRecordEntity("HR-17", "PT-1009", "Fatima Begum", RecordType.DOCUMENT.name, "Diabetic Foot Ulcer Assessment #WND-104", "Grade 1 superficial neuropathic ulcer, right 1st MTP", "Normal saline wash, dry non-adherent dressing", "Today", "Active", SyncStatus.PENDING_UPLOAD.name, now - 45 * 60_000L),
            HealthRecordEntity("HR-18", "PT-1009", "Fatima Begum", RecordType.MEDICINE.name, "Metformin 500mg + Glimepiride 1mg", "1 tablet twice daily with meals", "Advised daily foot inspection and diabetic footwear", "Today", "Active", SyncStatus.PENDING_UPLOAD.name, now - 40 * 60_000L),
            HealthRecordEntity("HR-19", "PT-1010", "Vikram Singh", RecordType.DOCUMENT.name, "Spirometry Pulmonary Function Test", "FEV1/FVC ratio 62% · Moderate airflow limitation", "Prescribed Budesonide/Formoterol 200mcg DPI", "4 Days Ago", "Stable", SyncStatus.SYNCED.name, now - 4 * oneDay),
            HealthRecordEntity("HR-20", "PT-1010", "Vikram Singh", RecordType.MEDICINE.name, "Budesonide 200mcg + Formoterol 6mcg", "1 inhalation twice daily after rinsing mouth", "Inhaler technique verified with clinician", "4 Days Ago", "Active", SyncStatus.SYNCED.name, now - 4 * oneDay + 2 * oneHour)
        )
        healthRecordDao.insertRecords(records)

        val queueItems = listOf(
            SyncQueueEntity("SQ-1", "Patient & ANC", "PT-1001", "Meera Devi", "UPDATE", "ANC Week 28 Vitals + IFA Prescription", "09:15 AM", now - 580_000L, 0, SyncQueueStatus.QUEUED.name),
            SyncQueueEntity("SQ-2", "Critical Referral", "PT-1003", "Harish Verma", "UPLOAD_RECORD", "Emergency Vitals & Referral #REF-882", "10:05 AM", now - 290_000L, 0, SyncQueueStatus.QUEUED.name),
            SyncQueueEntity("SQ-3", "Child Draft", "PT-1005", "Ananya Nair", "CREATE", "Pediatric Triage & MMR Schedule", "11:00 AM", now - 175_000L, 0, SyncQueueStatus.QUEUED.name),
            SyncQueueEntity("SQ-4", "Diabetic Foot Record", "PT-1009", "Fatima Begum", "UPLOAD_RECORD", "Wound Assessment #WND-104 & Sugar Log", "11:45 AM", now - 35 * 60_000L, 0, SyncQueueStatus.QUEUED.name),
            SyncQueueEntity("SQ-5", "Lab Attachment", "PT-1007", "Sunita Rao", "UPLOAD_RECORD", "Dengue CBC Lab Sync Retry", "3 Days Ago", now - 3 * oneDay, 1, SyncQueueStatus.FAILED.name)
        )
        syncQueueDao.enqueueAll(queueItems)

        val notifications = listOf(
            NotificationEntity("NT-1", "Critical SpO2 Alert: Harish Verma", "SpO2 dropped to 90% with BP 164/98. Immediate PHC transfer advised.", NotificationType.EMERGENCY.name, "10 mins ago", false, "PT-1003", now - 600_000L),
            NotificationEntity("NT-2", "Medicine Reminder: Meera Devi", "Verify Ferrous Sulfate + Folic Acid evening dose & monitor ankle edema.", NotificationType.MEDICINE.name, "35 mins ago", false, "PT-1001", now - 2_100_000L),
            NotificationEntity("NT-3", "Diabetic Ulcer Review: Fatima Begum", "Inspect dressing and check fasting blood glucose tomorrow morning.", NotificationType.FOLLOW_UP.name, "1 hr ago", false, "PT-1009", now - oneHour),
            NotificationEntity("NT-4", "Vaccination Due: Ananya Nair", "MMR Dose 1 immunization scheduled for 19 Nov 2026 at Ward 3 Clinic.", NotificationType.VACCINATION.name, "2 hrs ago", false, "PT-1005", now - 2 * oneHour),
            NotificationEntity("NT-5", "5 Offline Records Pending Sync", "Connect to network or tap Sync Now to upload queued patient records.", NotificationType.SYNC.name, "3 hrs ago", false, null, now - 3 * oneHour),
            NotificationEntity("NT-6", "Follow-Up Scheduled: Sunita Rao", "Platelet count re-evaluation & hydration check due tomorrow.", NotificationType.FOLLOW_UP.name, "Yesterday", true, "PT-1007", now - oneDay)
        )
        notificationDao.insertNotifications(notifications)

        // 16 rich recent activities spread across the past 7 days for lively charts & logs
        val activities = listOf(
            ActivityEntity("AC-1", "Critical flag raised for Harish Verma", "SpO2 90% · Referral #REF-882 queued offline for immediate PHC transfer", "10:05 AM", "Emergency", now - 300_000L),
            ActivityEntity("AC-2", "ANC Week 28 checkup logged for Meera Devi", "Vitals, fundal height & 30-day IFA supply recorded locally", "09:15 AM", "Pregnancy", now - 600_000L),
            ActivityEntity("AC-3", "Pediatric growth chart updated for Aarav Sharma", "Weight 14.2 kg · Normal percentile · DPT booster booked", "08:30 AM", "Child", now - 1_800_000L),
            ActivityEntity("AC-4", "Diabetic foot ulcer dressing logged for Fatima Begum", "Wound #WND-104 cleaned with sterile saline · Fasting sugar 174", "11:45 AM", "Chronic", now - 35 * 60_000L),
            ActivityEntity("AC-5", "Morning doorstep triage rounds synced", "14 patient records verified against CareSync cloud ledger", "07:45 AM", "Sync", now - 4 * oneHour),
            ActivityEntity("AC-6", "Dengue fever surveillance visit for Sunita Rao", "Hydration ORS advised · CBC platelet watch protocol initiated", "Yesterday, 03:30 PM", "Emergency", now - oneDay + 2 * oneHour),
            ActivityEntity("AC-7", "TT-2 maternal vaccination completed for Kavita Patel", "Gestation 21 weeks · Fetal heart rate 144 bpm recorded", "Yesterday, 11:20 AM", "Pregnancy", now - oneDay + 4 * oneHour),
            ActivityEntity("AC-8", "Hypertension treatment review for Rameshwar Joshi", "BP 128/80 controlled on Amlodipine 5mg · Medication refill confirmed", "2 Days Ago", "Chronic", now - 2 * oneDay + 3 * oneHour),
            ActivityEntity("AC-9", "MMR vaccination schedule counseling for Ananya Nair", "Spacer nebulizer demo provided to mother for mild wheezing", "2 Days Ago", "Child", now - 2 * oneDay + 5 * oneHour),
            ActivityEntity("AC-10", "Ward 4 community maternal health camp", "38 expectant mothers screened for anemia and gestational hypertension", "3 Days Ago", "Pregnancy", now - 3 * oneDay + 2 * oneHour),
            ActivityEntity("AC-11", "COPD peak flow evaluation for Vikram Singh", "Spirometry reviewed · Inhaler technique validated with patient", "4 Days Ago", "Chronic", now - 4 * oneDay + oneHour),
            ActivityEntity("AC-12", "Vaccine cold-chain temperature audit logged", "Hub refrigerator steady at 4.2°C · 120 vials verified", "4 Days Ago", "Sync", now - 4 * oneDay + 6 * oneHour),
            ActivityEntity("AC-13", "Post-viral laceration dressing check for Rohan Das", "Wound 3cm granulating cleanly · Suture lines intact", "5 Days Ago", "General", now - 5 * oneDay + 2 * oneHour),
            ActivityEntity("AC-14", "Emergency referral dispatch drill completed", "108 ambulance response time mapped for Ward 7 Old Mill Colony", "5 Days Ago", "Emergency", now - 5 * oneDay + 4 * oneHour),
            ActivityEntity("AC-15", "Ward 6 vector-control vector spray advisory", "Dengue preventive notices delivered to 85 households", "6 Days Ago", "General", now - 6 * oneDay + oneHour),
            ActivityEntity("AC-16", "Weekly offline database integrity check", "All 52 offline records reconciled with cloud database without conflicts", "6 Days Ago", "Sync", now - 6 * oneDay + 5 * oneHour)
        )
        activityDao.insertActivities(activities)
    }
}
