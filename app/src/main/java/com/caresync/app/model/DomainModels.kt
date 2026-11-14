package com.caresync.app.model

enum class SyncStatus(val label: String) {
    SYNCED("Synced"),
    PENDING_UPLOAD("Pending Sync"),
    OFFLINE_DRAFT("Offline Draft"),
    CONFLICT("Sync Retry")
}

enum class RiskLevel(val label: String) {
    LOW("Low Risk"),
    MODERATE("Moderate"),
    HIGH("High Risk"),
    CRITICAL("Critical")
}

enum class PatientCategory(val label: String) {
    GENERAL("General"),
    PREGNANCY("Pregnancy"),
    CHILD("Child Care"),
    CHRONIC("Chronic"),
    EMERGENCY("Emergency")
}

enum class RecordType(val label: String) {
    TIMELINE("Timeline"),
    HISTORY("Medical History"),
    MEDICINE("Medicine"),
    VACCINATION("Vaccination"),
    DOCUMENT("Document")
}

enum class NotificationType(val label: String) {
    MEDICINE("Medicine"),
    VACCINATION("Vaccination"),
    FOLLOW_UP("Follow-up"),
    SYNC("Sync"),
    EMERGENCY("Emergency")
}

enum class SyncQueueStatus(val label: String) {
    QUEUED("Queued Offline"),
    SYNCING("Syncing..."),
    FAILED("Retry Pending")
}

enum class AppLanguage(val displayName: String, val code: String) {
    ENGLISH("English", "en"),
    HINDI("हिन्दी (Hindi)", "hi"),
    MARATHI("मराठी (Marathi)", "mr"),
    TAMIL("தமிழ் (Tamil)", "ta"),
    BENGALI("বাংলা (Bengali)", "bn")
}

data class ClinicalAssessmentResult(
    val riskScore: Int,
    val riskLevel: RiskLevel,
    val primarySummary: String,
    val possibleConditions: List<String>,
    val medicineSuggestions: List<String>,
    val recommendedActions: List<String>,
    val redFlags: List<String>,
    val isOfflinePrediction: Boolean = true
)

data class ChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val timestampLabel: String,
    val tags: List<String> = emptyList()
)

data class EmergencyContact(
    val id: String,
    val name: String,
    val role: String,
    val phone: String,
    val isPrimary: Boolean = false
)

data class NearbyHealthCenter(
    val id: String,
    val name: String,
    val type: String,
    val distanceKm: String,
    val availability: String,
    val phone: String,
    val latitude: Double = 28.6139,
    val longitude: Double = 77.2090
)

data class EmergencyInstruction(
    val id: String,
    val title: String,
    val severityTag: String,
    val steps: List<String>
)

data class UserProfile(
    val name: String,
    val role: String,
    val workerId: String,
    val clinicName: String,
    val district: String,
    val phone: String,
    val email: String
)

data class RxMedicationItem(
    val name: String,
    val dosage: String,
    val frequency: String,
    val duration: String
)

data class RxAnalysisResult(
    val summary: String,
    val medications: List<RxMedicationItem>,
    val warnings: List<String>,
    val isOffline: Boolean = false
)

data class WardOutbreakInsight(
    val alertLevel: RiskLevel,
    val headline: String,
    val summary: String,
    val affectedWards: List<String>,
    val recommendations: List<String>,
    val isOffline: Boolean = false
)

