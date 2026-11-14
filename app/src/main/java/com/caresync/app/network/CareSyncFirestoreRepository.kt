package com.caresync.app.network

import android.content.Context
import com.caresync.app.R
import com.caresync.app.model.HealthRecordDoc
import com.caresync.app.model.NotificationDoc
import com.caresync.app.model.PatientDoc
import com.caresync.app.model.UserProfileDoc
import com.caresync.app.model.VitalsDoc
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CareSyncFirestoreRepository private constructor(
    private val dbProvider: () -> FirebaseFirestore
) {
    @Inject
    constructor(lazyDb: dagger.Lazy<FirebaseFirestore>) : this({ lazyDb.get() })

    constructor(db: FirebaseFirestore) : this({ db })

    constructor(context: Context) : this({
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    })

    private val db: FirebaseFirestore
        get() = dbProvider()

    private fun userDoc(userId: String) = db.collection("users").document(userId)

    private fun patientsCol(userId: String) = userDoc(userId).collection("patients")
    private fun vitalsCol(userId: String) = userDoc(userId).collection("vitals")
    private fun healthRecordsCol(userId: String) = userDoc(userId).collection("health_records")
    private fun notificationsCol(userId: String) = userDoc(userId).collection("notifications")

    suspend fun ensureUserProfile(
        userId: String,
        displayName: String,
        email: String?,
        clinicName: String = "CareSync Community Health Hub · Ward 4",
        workerId: String = "CS-CHW-409"
    ): Result<UserProfileDoc> {
        val docRef = userDoc(userId)
        return try {
            val snap = docRef.get().await()
            if (!snap.exists()) {
                val created = UserProfileDoc(
                    id = userId,
                    userId = userId,
                    displayName = displayName.ifBlank { "CareSync Clinician" }.take(120),
                    email = email?.take(200),
                    clinicName = clinicName.take(150),
                    workerId = workerId.take(60),
                    district = "North Health Circle · Zone B",
                    role = "Senior Community Health Officer",
                    createdAt = null,
                    updatedAt = null
                )
                docRef.set(created).await()
                Result.success(created)
            } else {
                val existing = snap.toObject(
                    UserProfileDoc::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ) ?: UserProfileDoc(userId = userId, displayName = displayName)
                Result.success(existing)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(userId: String): Result<UserProfileDoc?> {
        val docRef = userDoc(userId)
        return try {
            val snap = docRef.get().await()
            val profile = snap.toObject(
                UserProfileDoc::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )
            Result.success(profile)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            Result.failure(e)
        }
    }

    fun observePatients(userId: String): Flow<List<PatientDoc>> =
        patientsCol(userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    PatientDoc::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }

    fun observeVitals(userId: String): Flow<List<VitalsDoc>> =
        vitalsCol(userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    VitalsDoc::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }

    fun observeHealthRecords(userId: String): Flow<List<HealthRecordDoc>> =
        healthRecordsCol(userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    HealthRecordDoc::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }

    fun observeNotifications(userId: String): Flow<List<NotificationDoc>> =
        notificationsCol(userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    NotificationDoc::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }

    suspend fun getPatients(userId: String): Result<List<PatientDoc>> {
        val colRef = patientsCol(userId)
        return try {
            val snapshot = colRef.get().await()
            val items = snapshot.toObjects(
                PatientDoc::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )
            Result.success(items)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, colRef.path)
            Result.failure(e)
        }
    }

    suspend fun getPatientById(userId: String, patientId: String): Result<PatientDoc?> {
        val docRef = patientsCol(userId).document(patientId)
        return try {
            val snap = docRef.get().await()
            val doc = snap.toObject(
                PatientDoc::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )
            Result.success(doc)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun upsertPatient(userId: String, patient: PatientDoc): Result<String> {
        require(patient.id.isNotBlank()) { "Patient ID must not be blank" }
        val docRef = patientsCol(userId).document(patient.id)
        return try {
            val existingSnap = docRef.get().await()
            val payload = if (!existingSnap.exists()) {
                patient.copy(createdAt = null, updatedAt = null)
            } else {
                val created = existingSnap.getTimestamp("createdAt") ?: Timestamp.now()
                patient.copy(createdAt = created, updatedAt = null)
            }
            docRef.set(payload).await()
            Result.success(patient.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(
                e,
                if (patient.createdAt == null) OperationType.CREATE else OperationType.UPDATE,
                docRef.path
            )
            Result.failure(e)
        }
    }

    suspend fun updatePatientCriticalStatus(
        userId: String,
        patientId: String,
        critical: Boolean,
        riskLevel: String
    ): Result<Unit> {
        val docRef = patientsCol(userId).document(patientId)
        return try {
            docRef.update(
                mapOf(
                    "critical" to critical,
                    "riskLevel" to riskLevel,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deletePatient(userId: String, patientId: String): Result<Unit> {
        val docRef = patientsCol(userId).document(patientId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun upsertVitals(userId: String, vitals: VitalsDoc): Result<String> {
        require(vitals.id.isNotBlank()) { "Vitals ID must not be blank" }
        val docRef = vitalsCol(userId).document(vitals.id)
        return try {
            val existingSnap = docRef.get().await()
            val payload = if (!existingSnap.exists()) {
                vitals.copy(createdAt = null, updatedAt = null)
            } else {
                val created = existingSnap.getTimestamp("createdAt") ?: Timestamp.now()
                vitals.copy(createdAt = created, updatedAt = null)
            }
            docRef.set(payload).await()
            Result.success(vitals.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun upsertHealthRecord(userId: String, record: HealthRecordDoc): Result<String> {
        require(record.id.isNotBlank()) { "HealthRecord ID must not be blank" }
        val docRef = healthRecordsCol(userId).document(record.id)
        return try {
            val existingSnap = docRef.get().await()
            val payload = if (!existingSnap.exists()) {
                record.copy(createdAt = null, updatedAt = null)
            } else {
                val created = existingSnap.getTimestamp("createdAt") ?: Timestamp.now()
                record.copy(createdAt = created, updatedAt = null)
            }
            docRef.set(payload).await()
            Result.success(record.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun upsertNotification(userId: String, notification: NotificationDoc): Result<String> {
        require(notification.id.isNotBlank()) { "Notification ID must not be blank" }
        val docRef = notificationsCol(userId).document(notification.id)
        return try {
            val existingSnap = docRef.get().await()
            val payload = if (!existingSnap.exists()) {
                notification.copy(createdAt = null, updatedAt = null)
            } else {
                val created = existingSnap.getTimestamp("createdAt") ?: Timestamp.now()
                notification.copy(createdAt = created, updatedAt = null)
            }
            docRef.set(payload).await()
            Result.success(notification.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun markNotificationRead(userId: String, notificationId: String): Result<Unit> {
        val docRef = notificationsCol(userId).document(notificationId)
        return try {
            docRef.update(
                mapOf(
                    "read" to true,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun markAllNotificationsRead(userId: String, notificationIds: List<String>): Result<Unit> {
        if (notificationIds.isEmpty()) return Result.success(Unit)
        return try {
            for (id in notificationIds) {
                notificationsCol(userId).document(id).update(
                    mapOf(
                        "read" to true,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, notificationsCol(userId).path)
            Result.failure(e)
        }
    }

    suspend fun getVitals(userId: String): Result<List<VitalsDoc>> {
        val colRef = vitalsCol(userId)
        return try {
            val snapshot = colRef.get().await()
            val items = snapshot.toObjects(
                VitalsDoc::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )
            Result.success(items)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, colRef.path)
            Result.failure(e)
        }
    }

    suspend fun getHealthRecords(userId: String): Result<List<HealthRecordDoc>> {
        val colRef = healthRecordsCol(userId)
        return try {
            val snapshot = colRef.get().await()
            val items = snapshot.toObjects(
                HealthRecordDoc::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )
            Result.success(items)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, colRef.path)
            Result.failure(e)
        }
    }

    suspend fun getNotifications(userId: String): Result<List<NotificationDoc>> {
        val colRef = notificationsCol(userId)
        return try {
            val snapshot = colRef.get().await()
            val items = snapshot.toObjects(
                NotificationDoc::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )
            Result.success(items)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, colRef.path)
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(
        userId: String,
        displayName: String,
        email: String?,
        clinicName: String,
        workerId: String,
        district: String,
        role: String
    ): Result<Unit> {
        val docRef = userDoc(userId)
        return try {
            val snap = docRef.get().await()
            if (!snap.exists()) {
                val created = UserProfileDoc(
                    id = userId,
                    userId = userId,
                    displayName = displayName.ifBlank { "CareSync Clinician" }.take(120),
                    email = email?.take(200),
                    clinicName = clinicName.ifBlank { "CareSync Hub" }.take(150),
                    workerId = workerId.ifBlank { "CS-CHW-409" }.take(60),
                    district = district.take(120),
                    role = role.take(100)
                )
                docRef.set(created).await()
            } else {
                docRef.update(
                    mapOf(
                        "displayName" to displayName.ifBlank { "CareSync Clinician" }.take(120),
                        "email" to email?.take(200),
                        "clinicName" to clinicName.ifBlank { "CareSync Hub" }.take(150),
                        "workerId" to workerId.ifBlank { "CS-CHW-409" }.take(60),
                        "district" to district.take(120),
                        "role" to role.take(100),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }
}
