package com.caresync.app.model

import com.caresync.app.database.HealthRecordEntity
import com.caresync.app.database.NotificationEntity
import com.caresync.app.database.PatientEntity
import com.caresync.app.database.VitalsEntity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfileDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val email: String? = null,
    val clinicName: String = "CareSync Community Health Hub · Ward 4",
    val workerId: String = "CS-CHW-409",
    val district: String? = "North Health Circle · Zone B",
    val role: String? = "Senior Community Health Officer",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class PatientDoc(
    @DocumentId val id: String = "",
    val fullName: String = "",
    val age: Long = 0L,
    val gender: String = "Female",
    val phone: String? = null,
    val villageOrWard: String = "",
    val bloodGroup: String = "O+",
    val category: String = PatientCategory.GENERAL.name,
    val riskLevel: String = RiskLevel.LOW.name,
    val critical: Boolean = false,
    val todayVisit: Boolean = false,
    val pendingVisit: Boolean = false,
    val lastVisitLabel: String? = null,
    val nextFollowUpDate: String? = null,
    val allergies: String? = null,
    val chronicConditions: String? = null,
    val familyHeadName: String? = null,
    val familyMembersCount: Long = 1L,
    val pregnancyWeeks: Long = 0L,
    val expectedDueDate: String? = null,
    val childWeightKg: Double = 0.0,
    val vaccinationProgress: Long = 100L,
    val notes: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class VitalsDoc(
    @DocumentId val id: String = "",
    val patientId: String = "",
    val systolicBp: Long = 120L,
    val diastolicBp: Long = 80L,
    val heartRate: Long = 72L,
    val temperatureC: Double = 37.0,
    val spo2Percent: Long = 98L,
    val respiratoryRate: Long = 16L,
    val bloodSugarMgDl: Long = 100L,
    val weightKg: Double = 60.0,
    val recordedAtLabel: String = "Just now",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class HealthRecordDoc(
    @DocumentId val id: String = "",
    val patientId: String = "",
    val patientName: String = "",
    val recordType: String = RecordType.TIMELINE.name,
    val title: String = "",
    val subtitle: String? = null,
    val detail: String? = null,
    val dateLabel: String? = null,
    val statusBadge: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class NotificationDoc(
    @DocumentId val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = NotificationType.FOLLOW_UP.name,
    val timestampLabel: String = "Just now",
    val read: Boolean = false,
    val patientId: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

fun PatientEntity.toFirestoreDoc(): PatientDoc = PatientDoc(
    id = id,
    fullName = fullName.take(120).ifBlank { "Patient" },
    age = age.toLong().coerceIn(0L, 150L),
    gender = gender.take(40).ifBlank { "Other" },
    phone = phone.take(40).ifBlank { null },
    villageOrWard = villageOrWard.take(150).ifBlank { "Ward 4" },
    bloodGroup = bloodGroup.take(16).ifBlank { "O+" },
    category = category,
    riskLevel = riskLevel,
    critical = isCritical,
    todayVisit = isTodayVisit,
    pendingVisit = isPendingVisit,
    lastVisitLabel = lastVisitLabel.take(80).ifBlank { null },
    nextFollowUpDate = nextFollowUpDate.take(80).ifBlank { null },
    allergies = allergies.take(500).ifBlank { null },
    chronicConditions = chronicConditions.take(500).ifBlank { null },
    familyHeadName = familyHeadName.take(120).ifBlank { null },
    familyMembersCount = familyMembersCount.toLong().coerceIn(1L, 50L),
    pregnancyWeeks = pregnancyWeeks.toLong().coerceIn(0L, 45L),
    expectedDueDate = expectedDueDate.take(80).ifBlank { null },
    childWeightKg = childWeightKg.toDouble().coerceIn(0.0, 300.0),
    vaccinationProgress = vaccinationProgress.toLong().coerceIn(0L, 100L),
    notes = notes.take(2000).ifBlank { null }
)

fun PatientDoc.toRoomEntity(): PatientEntity = PatientEntity(
    id = id,
    fullName = fullName,
    age = age.toInt(),
    gender = gender,
    phone = phone.orEmpty(),
    villageOrWard = villageOrWard,
    bloodGroup = bloodGroup,
    category = category,
    riskLevel = riskLevel,
    isCritical = critical,
    isTodayVisit = todayVisit,
    isPendingVisit = pendingVisit,
    syncStatus = SyncStatus.SYNCED.name,
    lastVisitLabel = lastVisitLabel.orEmpty(),
    nextFollowUpDate = nextFollowUpDate.orEmpty(),
    allergies = allergies.orEmpty(),
    chronicConditions = chronicConditions.orEmpty(),
    familyHeadName = familyHeadName.orEmpty(),
    familyMembersCount = familyMembersCount.toInt(),
    pregnancyWeeks = pregnancyWeeks.toInt(),
    expectedDueDate = expectedDueDate.orEmpty(),
    childWeightKg = childWeightKg.toFloat(),
    vaccinationProgress = vaccinationProgress.toInt(),
    notes = notes.orEmpty(),
    updatedAt = updatedAt?.toDate()?.time ?: System.currentTimeMillis()
)

fun VitalsEntity.toFirestoreDoc(): VitalsDoc = VitalsDoc(
    id = id,
    patientId = patientId,
    systolicBp = systolicBp.toLong().coerceIn(30L, 300L),
    diastolicBp = diastolicBp.toLong().coerceIn(20L, 200L),
    heartRate = heartRate.toLong().coerceIn(20L, 260L),
    temperatureC = temperatureC.toDouble().coerceIn(25.0, 46.0),
    spo2Percent = spo2Percent.toLong().coerceIn(40L, 100L),
    respiratoryRate = respiratoryRate.toLong().coerceIn(5L, 80L),
    bloodSugarMgDl = bloodSugarMgDl.toLong().coerceIn(20L, 800L),
    weightKg = weightKg.toDouble().coerceIn(0.0, 400.0),
    recordedAtLabel = recordedAtLabel.take(80).ifBlank { "Just now" }
)

fun VitalsDoc.toRoomEntity(): VitalsEntity = VitalsEntity(
    id = id,
    patientId = patientId,
    systolicBp = systolicBp.toInt(),
    diastolicBp = diastolicBp.toInt(),
    heartRate = heartRate.toInt(),
    temperatureC = temperatureC.toFloat(),
    spo2Percent = spo2Percent.toInt(),
    respiratoryRate = respiratoryRate.toInt(),
    bloodSugarMgDl = bloodSugarMgDl.toInt(),
    weightKg = weightKg.toFloat(),
    recordedAtLabel = recordedAtLabel,
    recordedAtMillis = createdAt?.toDate()?.time ?: System.currentTimeMillis(),
    syncStatus = SyncStatus.SYNCED.name
)

fun HealthRecordEntity.toFirestoreDoc(): HealthRecordDoc = HealthRecordDoc(
    id = id,
    patientId = patientId,
    patientName = patientName.take(120).ifBlank { "Patient" },
    recordType = recordType,
    title = title.take(160).ifBlank { "Health Record" },
    subtitle = subtitle.take(240).ifBlank { null },
    detail = detail.take(2000).ifBlank { null },
    dateLabel = dateLabel.take(80).ifBlank { null },
    statusBadge = statusBadge.take(60).ifBlank { null }
)

fun HealthRecordDoc.toRoomEntity(): HealthRecordEntity = HealthRecordEntity(
    id = id,
    patientId = patientId,
    patientName = patientName,
    recordType = recordType,
    title = title,
    subtitle = subtitle.orEmpty(),
    detail = detail.orEmpty(),
    dateLabel = dateLabel.orEmpty(),
    statusBadge = statusBadge.orEmpty(),
    syncStatus = SyncStatus.SYNCED.name,
    createdAt = createdAt?.toDate()?.time ?: System.currentTimeMillis()
)

fun NotificationEntity.toFirestoreDoc(): NotificationDoc = NotificationDoc(
    id = id,
    title = title.take(160).ifBlank { "CareSync Alert" },
    message = message.take(600).ifBlank { "Clinical update" },
    type = type,
    timestampLabel = timestampLabel.take(80).ifBlank { "Just now" },
    read = isRead,
    patientId = patientId?.takeIf { it.isNotBlank() }
)

fun NotificationDoc.toRoomEntity(): NotificationEntity = NotificationEntity(
    id = id,
    title = title,
    message = message,
    type = type,
    timestampLabel = timestampLabel,
    isRead = read,
    patientId = patientId,
    createdAt = createdAt?.toDate()?.time ?: System.currentTimeMillis()
)
