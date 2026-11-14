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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.caresync.app.components.CareSyncBottomBar
import com.caresync.app.components.CareSyncDrawerContent
import com.caresync.app.components.CareSyncNavigationRail
import com.caresync.app.components.CareSyncTopBar
import com.caresync.app.ui.screens.ai.AiAssistantScreen
import com.caresync.app.ui.screens.ai.MedicalAssessmentScreen
import com.caresync.app.ui.screens.ai.SymptomCheckerScreen
import com.caresync.app.ui.screens.auth.ForgotPasswordScreen
import com.caresync.app.ui.screens.auth.LoginScreen
import com.caresync.app.ui.screens.auth.SignupScreen
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
import com.caresync.app.viewmodel.AiToolsViewModel
import com.caresync.app.viewmodel.AuthViewModel
import com.caresync.app.viewmodel.DashboardViewModel
import com.caresync.app.viewmodel.OperationsViewModel
import com.caresync.app.viewmodel.PatientDetailViewModel
import com.caresync.app.viewmodel.PatientsViewModel
import kotlinx.coroutines.launch

@Composable
fun CareSyncNavHost(
    authViewModel: AuthViewModel = hiltViewModel(),
    dashboardViewModel: DashboardViewModel = hiltViewModel(),
    patientsViewModel: PatientsViewModel = hiltViewModel(),
    aiToolsViewModel: AiToolsViewModel = hiltViewModel(),
    operationsViewModel: OperationsViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route.orEmpty()

    val prefs by authViewModel.preferences.collectAsStateWithLifecycle()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val dashState by dashboardViewModel.uiState.collectAsStateWithLifecycle()
    val patientsState by patientsViewModel.uiState.collectAsStateWithLifecycle()
    val aiState by aiToolsViewModel.uiState.collectAsStateWithLifecycle()
    val emergencyState by operationsViewModel.emergencyState.collectAsStateWithLifecycle()
    val notifications by operationsViewModel.notifications.collectAsStateWithLifecycle()
    val settingsState by operationsViewModel.settingsState.collectAsStateWithLifecycle()
    val syncMsg by operationsViewModel.syncResultMessage.collectAsStateWithLifecycle()

    val isAuthScreen = currentRoute.contains("SplashRoute") ||
        currentRoute.contains("LoginRoute") ||
        currentRoute.contains("SignupRoute") ||
        currentRoute.contains("ForgotPasswordRoute") ||
        currentRoute.isBlank()

    val isTopLevelTab = currentRoute.contains("DashboardRoute") ||
        currentRoute.contains("PatientListRoute") ||
        currentRoute.contains("SymptomCheckerRoute") ||
        currentRoute.contains("AiAssistantRoute") ||
        currentRoute.contains("NotificationsRoute") ||
        currentRoute.contains("EmergencyRoute")

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
        currentRoute.contains("AddPatientRoute") -> "Register Patient"
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

    val navContent: @Composable () -> Unit = {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useRail = maxWidth >= 600.dp && !isAuthScreen
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
                        if (!isAuthScreen) {
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
                        }
                    },
                    bottomBar = {
                        if (!isAuthScreen && !useRail && isTopLevelTab) {
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
                            startDestination = SplashRoute,
                            enterTransition = { fadeIn() },
                            exitTransition = { fadeOut() }
                        ) {
                            composable<SplashRoute> {
                                SplashScreen(
                                    isLoggedIn = prefs.isLoggedIn,
                                    onEnsureSeeded = { authViewModel.ensureSeeded() },
                                    onNavigateNext = { loggedIn ->
                                        val target: Any = if (loggedIn) DashboardRoute else LoginRoute
                                        navController.navigate(target) {
                                            popUpTo<SplashRoute> { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable<LoginRoute> {
                                LoginScreen(
                                    uiState = authState,
                                    onLogin = { email, pass ->
                                        authViewModel.login(email, pass) {
                                            navController.navigate(DashboardRoute) {
                                                popUpTo<LoginRoute> { inclusive = true }
                                            }
                                        }
                                    },
                                    onDemoLogin = {
                                        authViewModel.demoQuickLogin {
                                            navController.navigate(DashboardRoute) {
                                                popUpTo<LoginRoute> { inclusive = true }
                                            }
                                        }
                                    },
                                    onNavigateSignup = { navController.navigate(SignupRoute) },
                                    onNavigateForgot = { navController.navigate(ForgotPasswordRoute) },
                                    onClearError = { authViewModel.clearMessages() }
                                )
                            }
                            composable<SignupRoute> {
                                SignupScreen(
                                    uiState = authState,
                                    onSignup = { name, email, clinic, pass ->
                                        authViewModel.signup(name, email, clinic, pass) {
                                            navController.navigate(DashboardRoute) {
                                                popUpTo<LoginRoute> { inclusive = true }
                                            }
                                        }
                                    },
                                    onBack = { navController.popBackStack() },
                                    onClearError = { authViewModel.clearMessages() }
                                )
                            }
                            composable<ForgotPasswordRoute> {
                                ForgotPasswordScreen(
                                    uiState = authState,
                                    onSendReset = { authViewModel.sendPasswordReset(it) },
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable<DashboardRoute> {
                                HomeDashboardScreen(
                                    state = dashState,
                                    onSyncNow = { dashboardViewModel.triggerSyncNow() },
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
                                    onOpenAssessment = { id -> navController.navigate(MedicalAssessmentRoute(id)) }
                                )
                            }
                            composable<AddPatientRoute> {
                                AddPatientScreen(
                                    onSavePatientOffline = { fullName, age, gender, phone, ward, bloodGroup, category, isCritical, allergies, chronic, familyHead, familyMembers, pregWeeks, dueDate, childWeight, notes, sys, dia, hr, spo2, temp ->
                                        patientsViewModel.registerPatientOffline(
                                            fullName, age, gender, phone, ward, bloodGroup, category, isCritical,
                                            allergies, chronic, familyHead, familyMembers, pregWeeks, dueDate,
                                            childWeight, notes, sys, dia, hr, spo2, temp
                                        ) { newId ->
                                            navController.navigate(PatientDetailRoute(newId)) {
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
                            composable<MedicalAssessmentRoute> {
                                MedicalAssessmentScreen(
                                    state = aiState,
                                    onRunRiskAssessment = aiToolsViewModel::runDiseaseRiskAssessment,
                                    onAnalyzeImageSample = aiToolsViewModel::analyzeImageSample,
                                    onOpenSymptomChecker = { navController.navigate(SymptomCheckerRoute) }
                                )
                            }
                            composable<SymptomCheckerRoute> {
                                SymptomCheckerScreen(
                                    state = aiState,
                                    patients = patientsState.patients,
                                    onToggleSymptom = aiToolsViewModel::toggleSymptom,
                                    onDurationChange = aiToolsViewModel::setDurationDays,
                                    onSeverityChange = aiToolsViewModel::setSeverity,
                                    onSelectPatient = aiToolsViewModel::selectPatient,
                                    onAnalyze = aiToolsViewModel::runSymptomCheck,
                                    onOpenChat = { navController.navigate(AiAssistantRoute) }
                                )
                            }
                            composable<AiAssistantRoute> {
                                AiAssistantScreen(
                                    state = aiState,
                                    patients = patientsState.patients,
                                    onSelectPatient = aiToolsViewModel::selectPatient,
                                    onSendMessage = aiToolsViewModel::sendChatMessage
                                )
                            }
                            composable<EmergencyRoute> {
                                EmergencyScreen(
                                    state = emergencyState,
                                    onTriggerSos = operationsViewModel::triggerSos,
                                    onDismissSos = operationsViewModel::dismissSosBanner,
                                    onToggleCritical = operationsViewModel::toggleCritical,
                                    onOpenPatient = { id -> navController.navigate(PatientDetailRoute(id)) }
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
                                    onOpenSettings = { navController.navigate(SettingsRoute) },
                                    onOpenAbout = { navController.navigate(AboutRoute) },
                                    onOpenHelp = { navController.navigate(HelpRoute) },
                                    onLogout = {
                                        operationsViewModel.logout {
                                            navController.navigate(LoginRoute) {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        }
                                    }
                                )
                            }
                            composable<SettingsRoute> {
                                SettingsScreen(
                                    state = settingsState,
                                    syncResultMessage = syncMsg,
                                    onToggleDarkMode = operationsViewModel::toggleDarkMode,
                                    onToggleOfflineOnly = operationsViewModel::toggleOfflineOnly,
                                    onToggleSecurityLock = operationsViewModel::toggleSecurityLock,
                                    onSelectLanguage = operationsViewModel::setLanguage,
                                    onSyncNow = operationsViewModel::syncNow,
                                    onOpenProfile = { navController.navigate(ProfileRoute) },
                                    onOpenAbout = { navController.navigate(AboutRoute) },
                                    onOpenHelp = { navController.navigate(HelpRoute) },
                                    onLogout = {
                                        operationsViewModel.logout {
                                            navController.navigate(LoginRoute) {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        }
                                    }
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

    if (isAuthScreen) {
        navContent()
    } else {
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
            navContent()
        }
    }
}
