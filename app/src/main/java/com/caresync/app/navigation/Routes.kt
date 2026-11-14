package com.caresync.app.navigation

import kotlinx.serialization.Serializable

@Serializable
object SplashRoute

@Serializable
object LoginRoute

@Serializable
object DashboardRoute

@Serializable
data class PatientListRoute(val filter: String? = null)

@Serializable
data class PatientDetailRoute(val patientId: String)

@Serializable
data class AddPatientRoute(val patientId: String? = null)

@Serializable
data class HealthRecordsRoute(val patientId: String? = null)

@Serializable
data class MedicalAssessmentRoute(val patientId: String? = null)

@Serializable
object SymptomCheckerRoute

@Serializable
object AiAssistantRoute

@Serializable
object EmergencyRoute

@Serializable
object NotificationsRoute

@Serializable
object ProfileRoute

@Serializable
object SettingsRoute

@Serializable
object AboutRoute

@Serializable
object HelpRoute
