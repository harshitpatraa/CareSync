package com.caresync.app.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.caresync.app.components.AppLockGuard
import com.caresync.app.components.CareSyncBottomBar
import com.caresync.app.components.CareSyncDrawerContent
import com.caresync.app.components.CareSyncNavigationRail
import com.caresync.app.components.CareSyncTopBar
import com.caresync.app.components.SecurityLockOverlay
import com.caresync.app.database.PatientEntity
import com.caresync.app.ui.screens.ai.AiAssistantScreen
import com.caresync.app.ui.screens.ai.MedicalAssessmentScreen
import com.caresync.app.ui.screens.ai.SymptomCheckerScreen
import com.caresync.app.ui.screens.auth.LoginScreen
import com.caresync.app.ui.screens.auth.SplashScreen
import com.caresync.app.ui.screens.dashboard.HomeDashboardScreen
import com.caresync.app.ui.screens.emergency.EmergencyScreen
import com.caresync.app.ui.screens.info.AboutScreen
import com.caresync.app.ui.screens.info.HelpScreen
import com.caresync.app.ui.screens.notifications.NotificationsScreen
import com.caresync.app.ui.screens.patients.AddPatientScreen
import com.caresync.app.ui.screens.patients.PatientDetailScreen
import com.caresync.app.ui.screens.patients.PatientListScreen
import com.caresync.app.ui.screens.profile.ProfileScreen
import com.caresync.app.ui.screens.records.HealthRecordsScreen
import com.caresync.app.ui.screens.settings.SettingsScreen
import com.caresync.app.model.ClinicianUser
import com.caresync.app.utils.PdfReferralGenerator
import com.caresync.app.viewmodel.AiToolsViewModel
import com.caresync.app.viewmodel.AuthViewModel
import com.caresync.app.viewmodel.DashboardViewModel
import com.caresync.app.viewmodel.OperationsViewModel
import com.caresync.app.viewmodel.PatientDetailViewModel
import com.caresync.app.viewmodel.PatientsViewModel
import kotlinx.coroutines.launch

@Composable
fun CareSyncNavHost(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    var splashComplete by remember { mutableStateOf(false) }

    if (!splashComplete) {
        SplashScreen(
            onFinished = { splashComplete = true }
        )
        return
    }

    val user = currentUser
    if (user == null) {
        LoginScreen(
            uiState = authState,
            onOfflineClinicianClick = { name, email -> authViewModel.signInAsOfflineClinician(name, email) },
            onEmailSignInClick = { email, pass -> authViewModel.signInWithEmail(email, pass) },
            onEmailSignUpClick = { email, pass, name -> authViewModel.signUpWithEmail(email, pass, name) },
            onClearMessages = { authViewModel.clearMessages() }
        )
    } else {
        key(user.uid) {
            AuthenticatedCareSyncShell(
                currentUser = user,
                authViewModel = authViewModel
            )
        }
    }
}

@Composable
private fun AuthenticatedCareSyncShell(
    currentUser: ClinicianUser,
    authViewModel: AuthViewModel,
    dashboardViewModel: DashboardViewModel = hiltViewModel(key = currentUser.uid),
    patientsViewModel: PatientsViewModel = hiltViewModel(key = currentUser.uid),
    aiToolsViewModel: AiToolsViewModel = hiltViewModel(key = currentUser.uid),
    operationsViewModel: OperationsViewModel = hiltViewModel(key = currentUser.uid)
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val prefs by authViewModel.preferences.collectAsStateWithLifecycle()
    val isSessionUnlocked by authViewModel.isSessionUnlocked.collectAsStateWithLifecycle()

    LaunchedEffect(currentUser.uid) {
        authViewModel.onSessionAuthenticated(currentUser)
        if (!currentUser.isOfflineOnly) {
            authViewModel.startCloudSyncObserver(currentUser.uid)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    if (!AppLockGuard.suppressNextAutoLock) {
                        authViewModel.lockSessionIfEnabled()
                    }
                }
                Lifecycle.Event.ON_START -> {
                    AppLockGuard.suppressNextAutoLock = false
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route.orEmpty()

    val dashState by dashboardViewModel.uiState.collectAsStateWithLifecycle()
    val patientsState by patientsViewModel.uiState.collectAsStateWithLifecycle()
    val aiState by aiToolsViewModel.uiState.collectAsStateWithLifecycle()
    val emergencyState by operationsViewModel.emergencyState.collectAsStateWithLifecycle()
    val notifications by operationsViewModel.notifications.collectAsStateWithLifecycle()
    val settingsState by operationsViewModel.settingsState.collectAsStateWithLifecycle()
    val syncMsg by operationsViewModel.syncResultMessage.collectAsStateWithLifecycle()

    val isTopLevelTab = currentRoute.contains("DashboardRoute") ||
        currentRoute.contains("PatientListRoute") ||
        currentRoute.contains("SymptomCheckerRoute") ||
        currentRoute.contains("AiAssistantRoute") ||
        currentRoute.contains("NotificationsRoute") ||
        currentRoute.contains("EmergencyRoute") ||
        currentRoute.isBlank()

    val activeTabId = when {
        currentRoute.contains("DashboardRoute") -> "dashboard"
        currentRoute.contains("PatientListRoute") -> "patients"
        currentRoute.contains("SymptomCheckerRoute") || currentRoute.contains("AiAssistantRoute") -> "ai"
        currentRoute.contains("NotificationsRoute") -> "alerts"
        currentRoute.contains("EmergencyRoute") -> "emergency"
        else -> "dashboard"
    }

    val topTitle = when {
        currentRoute.contains("DashboardRoute") -> "CareSync"
        currentRoute.contains("PatientListRoute") -> "Patients"
        currentRoute.contains("PatientDetailRoute") -> "Patient Profile"
        currentRoute.contains("AddPatientRoute") -> "Patient Registration"
        currentRoute.contains("HealthRecordsRoute") -> "Health Records"
        currentRoute.contains("MedicalAssessmentRoute") -> "Medical Assessment"
        currentRoute.contains("SymptomCheckerRoute") -> "AI Symptom Checker"
        currentRoute.contains("AiAssistantRoute") -> "CareSync AI Chat"
        currentRoute.contains("EmergencyRoute") -> "Emergency SOS"
        currentRoute.contains("NotificationsRoute") -> "Notifications"
        currentRoute.contains("ProfileRoute") -> "Profile"
        currentRoute.contains("SettingsRoute") -> "Settings & Sync"
        currentRoute.contains("AboutRoute") -> "About CareSync"
        currentRoute.contains("HelpRoute") -> "Help & Protocols"
        else -> "CareSync"
    }

    fun navigateTopTab(tabId: String) {
        when (tabId) {
            "dashboard" -> navController.navigate(DashboardRoute) { launchSingleTop = true }
            "patients" -> navController.navigate(PatientListRoute()) { launchSingleTop = true }
            "ai" -> navController.navigate(AiAssistantRoute) { launchSingleTop = true }
            "alerts" -> navController.navigate(NotificationsRoute) { launchSingleTop = true }
            "emergency" -> navController.navigate(EmergencyRoute) { launchSingleTop = true }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                CareSyncDrawerContent(
                    userName = settingsState.profile.name,
                    userEmail = settingsState.profile.email,
                    pendingSyncCount = dashState.pendingSyncCount,
                    onNavigate = { target ->
                        scope.launch { drawerState.close() }
                        when (target) {
                            "dashboard" -> navController.navigate(DashboardRoute)
                            "patients" -> navController.navigate(PatientListRoute())
                            "records" -> navController.navigate(HealthRecordsRoute())
                            "symptom" -> navController.navigate(SymptomCheckerRoute)
                            "chat" -> navController.navigate(AiAssistantRoute)
                            "assessment" -> navController.navigate(MedicalAssessmentRoute())
                            "emergency" -> navController.navigate(EmergencyRoute)
                            "notifications" -> navController.navigate(NotificationsRoute)
                            "profile" -> navController.navigate(ProfileRoute)
                            "settings" -> navController.navigate(SettingsRoute)
                            "about" -> navController.navigate(AboutRoute)
                            "help" -> navController.navigate(HelpRoute)
                        }
                    }
                )
            }
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val useRail = maxWidth >= 600.dp
                Row(modifier = Modifier.fillMaxSize()) {
                    if (useRail) {
                        CareSyncNavigationRail(
                            activeTabId = activeTabId,
                            unreadAlertsCount = dashState.unreadAlertsCount,
                            onSelectTab = ::navigateTopTab
                        )
                    }
                    Scaffold(
                        topBar = {
                            CareSyncTopBar(
                                title = topTitle,
                                showBack = !isTopLevelTab,
                                pendingSyncCount = dashState.pendingSyncCount,
                                isOfflineOnly = dashState.isOfflineOnly,
                                unreadAlertsCount = dashState.unreadAlertsCount,
                                onMenuOrBackClick = {
                                    if (!isTopLevelTab) {
                                        navController.popBackStack()
                                    } else {
                                        scope.launch { drawerState.open() }
                                    }
                                },
                                onNotificationsClick = { navController.navigate(NotificationsRoute) },
                                onSyncPillClick = { navController.navigate(SettingsRoute) }
                            )
                        },
                        bottomBar = {
                            if (!useRail && isTopLevelTab) {
                                CareSyncBottomBar(
                                    activeTabId = activeTabId,
                                    unreadAlertsCount = dashState.unreadAlertsCount,
                                    onSelectTab = ::navigateTopTab
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = DashboardRoute,
                                enterTransition = { fadeIn() },
                                exitTransition = { fadeOut() }
                            ) {
                                composable<DashboardRoute> {
                                    HomeDashboardScreen(
                                        state = dashState,
                                        onSyncNow = { dashboardViewModel.triggerSyncNow() },
                                        onScanWardOutbreak = dashboardViewModel::scanWardOutbreak,
                                        onSpeakOutbreak = dashboardViewModel::speakOutbreakSummary,
                                        onStopSpeaking = dashboardViewModel::stopSpeaking,
                                        onOpenPatients = { filter -> navController.navigate(PatientListRoute(filter)) },
                                        onOpenAddPatient = { navController.navigate(AddPatientRoute()) },
                                        onOpenPatientDetail = { id -> navController.navigate(PatientDetailRoute(id)) },
                                        onOpenSymptomChecker = { navController.navigate(SymptomCheckerRoute) },
                                        onOpenAiChat = { navController.navigate(AiAssistantRoute) },
                                        onOpenAssessment = { navController.navigate(MedicalAssessmentRoute()) },
                                        onOpenRecords = { navController.navigate(HealthRecordsRoute()) },
                                        onOpenEmergency = { navController.navigate(EmergencyRoute) }
                                    )
                                }
                                composable<PatientListRoute> { entry ->
                                    val route = entry.toRoute<PatientListRoute>()
                                    LaunchedEffect(route.filter) {
                                        patientsViewModel.applyInitialFilter(route.filter)
                                    }
                                    PatientListScreen(
                                        state = patientsState,
                                        onSearchChange = patientsViewModel::setSearchQuery,
                                        onFilterChange = patientsViewModel::setFilter,
                                        onPatientClick = { id -> navController.navigate(PatientDetailRoute(id)) },
                                        onToggleCritical = patientsViewModel::toggleCritical,
                                        onResolveQrPatientId = patientsViewModel::findCanonicalPatientId,
                                        onAddPatientClick = { navController.navigate(AddPatientRoute()) }
                                    )
                                }
                                composable<PatientDetailRoute> {
                                    val detailVm: PatientDetailViewModel = hiltViewModel()
                                    val detailState by detailVm.uiState.collectAsStateWithLifecycle()
                                    PatientDetailScreen(
                                        state = detailState,
                                        onToggleCritical = detailVm::toggleCritical,
                                        onLogVitals = detailVm::recordNewVitals,
                                        onAddRecord = detailVm::addRecord,
                                        onOpenAssessment = { id -> navController.navigate(MedicalAssessmentRoute(id)) },
                                        onDismissCriticalBanner = detailVm::dismissCriticalVitalsBanner,
                                        onOpenEmergency = { navController.navigate(EmergencyRoute) },
                                        onEditPatient = { id -> navController.navigate(AddPatientRoute(id)) },
                                        onDeletePatient = {
                                            detailVm.deletePatient {
                                                navController.popBackStack()
                                            }
                                        },
                                        onShareReferralPdf = {
                                            detailVm.generateAndShareReferralPdf(context) { uri ->
                                                PdfReferralGenerator.shareUri(
                                                    context = context,
                                                    uri = uri,
                                                    mimeType = "application/pdf",
                                                    subject = "CareSync Patient Referral Slip",
                                                    text = "Attached is the offline-generated CareSync clinical referral slip."
                                                )
                                            }
                                        },
                                        onSpeakBrief = detailVm::speakPatientBrief,
                                        onStopSpeaking = detailVm::stopSpeaking
                                    )
                                }
                                composable<AddPatientRoute> { entry ->
                                    val route = entry.toRoute<AddPatientRoute>()
                                    val existingPatient by produceState<PatientEntity?>(initialValue = null, route.patientId) {
                                        value = route.patientId?.let { patientsViewModel.getPatientForEdit(it) }
                                    }
                                    AddPatientScreen(
                                        existingPatient = existingPatient,
                                        onSavePatientOffline = { fullName, age, gender, phone, ward, bloodGroup, category, isCritical, allergies, chronic, familyHead, familyMembers, pregWeeks, dueDate, childWeight, notes, sys, dia, hr, spo2, temp ->
                                            patientsViewModel.savePatientOffline(
                                                route.patientId,
                                                fullName, age, gender, phone, ward, bloodGroup, category, isCritical,
                                                allergies, chronic, familyHead, familyMembers, pregWeeks, dueDate,
                                                childWeight, notes, sys, dia, hr, spo2, temp
                                            ) { savedId ->
                                                navController.navigate(PatientDetailRoute(savedId)) {
                                                    popUpTo<AddPatientRoute> { inclusive = true }
                                                }
                                            }
                                        }
                                    )
                                }
                                composable<HealthRecordsRoute> { entry ->
                                    val route = entry.toRoute<HealthRecordsRoute>()
                                    HealthRecordsScreen(
                                        records = patientsState.allRecords,
                                        patientIdFilter = route.patientId
                                    )
                                }
                                composable<MedicalAssessmentRoute> { entry ->
                                    val route = entry.toRoute<MedicalAssessmentRoute>()
                                    LaunchedEffect(route.patientId) {
                                        aiToolsViewModel.loadPatientForAssessment(route.patientId)
                                    }
                                    MedicalAssessmentScreen(
                                        state = aiState,
                                        patients = patientsState.allPatientsUnfiltered,
                                        onSelectPatient = aiToolsViewModel::selectPatient,
                                        onRunRiskAssessment = aiToolsViewModel::runDiseaseRiskAssessment,
                                        onRunQuestionnaireAssessment = aiToolsViewModel::runQuestionnaireRiskAssessment,
                                        onAnalyzeImageSample = aiToolsViewModel::analyzeImageSample,
                                        onAnalyzePrescription = aiToolsViewModel::analyzePrescription,
                                        onSaveRxToPatient = aiToolsViewModel::saveRxToPatient,
                                        onSpeakText = aiToolsViewModel::speakText,
                                        onStopSpeaking = aiToolsViewModel::stopSpeaking,
                                        onOpenSymptomChecker = { navController.navigate(SymptomCheckerRoute) },
                                        onOpenChat = { navController.navigate(AiAssistantRoute) }
                                    )
                                }
                                composable<SymptomCheckerRoute> {
                                    SymptomCheckerScreen(
                                        state = aiState,
                                        patients = patientsState.allPatientsUnfiltered,
                                        onToggleSymptom = aiToolsViewModel::toggleSymptom,
                                        onDurationChange = aiToolsViewModel::setDurationDays,
                                        onSeverityChange = aiToolsViewModel::setSeverity,
                                        onSelectPatient = aiToolsViewModel::selectPatient,
                                        onAnalyze = aiToolsViewModel::runSymptomCheck,
                                        onSpeakText = aiToolsViewModel::speakText,
                                        onStopSpeaking = aiToolsViewModel::stopSpeaking,
                                        onOpenChat = { navController.navigate(AiAssistantRoute) }
                                    )
                                }
                                composable<AiAssistantRoute> {
                                    AiAssistantScreen(
                                        state = aiState,
                                        patients = patientsState.allPatientsUnfiltered,
                                        onSelectPatient = aiToolsViewModel::selectPatient,
                                        onSendMessage = aiToolsViewModel::sendChatMessage,
                                        onVoiceError = aiToolsViewModel::setError,
                                        onSpeakMessage = aiToolsViewModel::speakText,
                                        onStopSpeaking = aiToolsViewModel::stopSpeaking
                                    )
                                }
                                composable<EmergencyRoute> {
                                    EmergencyScreen(
                                        state = emergencyState,
                                        onTriggerSos = operationsViewModel::triggerSos,
                                        onDismissSos = operationsViewModel::dismissSosBanner,
                                        onToggleCritical = operationsViewModel::toggleCritical,
                                        onOpenPatient = { id -> navController.navigate(PatientDetailRoute(id)) },
                                        onRefreshLocation = { operationsViewModel.refreshLocation(context) },
                                        onAddContact = operationsViewModel::addEmergencyContact,
                                        onSpeakInstruction = operationsViewModel::speakInstruction,
                                        onStopSpeaking = operationsViewModel::stopSpeaking
                                    )
                                }
                                composable<NotificationsRoute> {
                                    NotificationsScreen(
                                        notifications = notifications,
                                        onMarkRead = operationsViewModel::markNotificationRead,
                                        onMarkAllRead = operationsViewModel::markAllNotificationsRead,
                                        onOpenPatient = { id -> navController.navigate(PatientDetailRoute(id)) }
                                    )
                                }
                                composable<ProfileRoute> {
                                    ProfileScreen(
                                        state = settingsState,
                                        onUpdateProfile = operationsViewModel::updateProfile,
                                        onOpenSettings = { navController.navigate(SettingsRoute) },
                                        onOpenAbout = { navController.navigate(AboutRoute) },
                                        onOpenHelp = { navController.navigate(HelpRoute) },
                                        onLogout = { authViewModel.signOut(context) }
                                    )
                                }
                                composable<SettingsRoute> {
                                    SettingsScreen(
                                        state = settingsState,
                                        syncResultMessage = syncMsg,
                                        onToggleDarkMode = operationsViewModel::toggleDarkMode,
                                        onToggleOfflineOnly = operationsViewModel::toggleOfflineOnly,
                                        onToggleSecurityLock = { enabled ->
                                            if (enabled) authViewModel.unlockSession()
                                            operationsViewModel.toggleSecurityLock(enabled)
                                        },
                                        onSetSecurityPin = operationsViewModel::setSecurityPin,
                                        onSelectLanguage = operationsViewModel::setLanguage,
                                        onUpdateApiKey = operationsViewModel::updateGeminiApiKey,
                                        onUpdateFirebaseConfig = operationsViewModel::updateFirebaseConfig,
                                        onImportModel = operationsViewModel::importGgufModel,
                                        onDeleteModel = operationsViewModel::deleteGgufModel,
                                        onDismissModelFeedback = operationsViewModel::dismissModelFeedback,
                                        onSyncNow = operationsViewModel::syncNow,
                                        onOpenProfile = { navController.navigate(ProfileRoute) },
                                        onOpenAbout = { navController.navigate(AboutRoute) },
                                        onOpenHelp = { navController.navigate(HelpRoute) },
                                        onLogout = { authViewModel.signOut(context) }
                                    )
                                }
                                composable<AboutRoute> { AboutScreen() }
                                composable<HelpRoute> { HelpScreen() }
                            }
                        }
                    }
                }
            }
        }

        if (prefs.isSecurityLockEnabled && !isSessionUnlocked) {
            SecurityLockOverlay(
                userName = settingsState.profile.name,
                onVerifyPin = authViewModel::verifyPin,
                onBiometricSuccess = authViewModel::unlockSession,
                onSignOutFallback = { authViewModel.signOut(context) }
            )
        }
    }
}
