package com.caresync.app.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.caresync.app.model.NotificationType
import com.caresync.app.model.PatientCategory
import com.caresync.app.model.RecordType
import com.caresync.app.model.RiskLevel
import com.caresync.app.model.SyncQueueStatus
import com.caresync.app.model.SyncStatus

@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val age: Int,
    val gender: String,
    val phone: String,
    val villageOrWard: String,
    val bloodGroup: String,
    val category: String = PatientCategory.GENERAL.name,
    val riskLevel: String = RiskLevel.LOW.name,
    val isCritical: Boolean = false,
    val isTodayVisit: Boolean = false,
    val isPendingVisit: Boolean = false,
    val syncStatus: String = SyncStatus.SYNCED.name,
    val lastVisitLabel: String,
    val nextFollowUpDate: String,
    val allergies: String,
    val chronicConditions: String,
    val familyHeadName: String,
    val familyMembersCount: Int,
    val pregnancyWeeks: Int = 0,
    val expectedDueDate: String = "",
    val childWeightKg: Float = 0f,
    val vaccinationProgress: Int = 100,
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "vitals")
data class VitalsEntity(
    @PrimaryKey val id: String,
    val patientId: String,
    val systolicBp: Int,
    val diastolicBp: Int,
    val heartRate: Int,
    val temperatureC: Float,
    val spo2Percent: Int,
    val respiratoryRate: Int,
    val bloodSugarMgDl: Int,
    val weightKg: Float,
    val recordedAtLabel: String,
    val recordedAtMillis: Long = System.currentTimeMillis(),
    val syncStatus: String = SyncStatus.SYNCED.name
)

@Entity(tableName = "health_records")
data class HealthRecordEntity(
    @PrimaryKey val id: String,
    val patientId: String,
    val patientName: String,
    val recordType: String = RecordType.TIMELINE.name,
    val title: String,
    val subtitle: String,
    val detail: String,
    val dateLabel: String,
    val statusBadge: String,
    val syncStatus: String = SyncStatus.SYNCED.name,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey val id: String,
    val entityType: String,
    val entityId: String,
    val patientName: String,
    val operation: String,
    val payloadSummary: String,
    val createdAtLabel: String,
    val createdAt: Long = System.currentTimeMillis(),
    val retryCount: Int = 0,
    val status: String = SyncQueueStatus.QUEUED.name
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val type: String = NotificationType.FOLLOW_UP.name,
    val timestampLabel: String,
    val isRead: Boolean = false,
    val patientId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val timestampLabel: String,
    val categoryTag: String,
    val createdAt: Long = System.currentTimeMillis()
)
