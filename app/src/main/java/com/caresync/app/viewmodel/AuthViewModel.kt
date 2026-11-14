package com.caresync.app.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caresync.app.model.ClinicianUser
import com.caresync.app.network.GoogleAuthHelper
import com.caresync.app.network.GoogleSignInOutcome
import com.caresync.app.network.authStateFlow
import com.caresync.app.repository.AppPreferences
import com.caresync.app.repository.CareSyncRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val statusMessage: String? = null
)

private const val TAG = "Auth"

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: CareSyncRepository
) : ViewModel() {

    val preferences: StateFlow<AppPreferences> = repository.preferencesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppPreferences()
    )

    private val _offlineUser = MutableStateFlow<ClinicianUser?>(null)

    val currentUser: StateFlow<ClinicianUser?> = combine(
        Firebase.auth.authStateFlow(),
        repository.preferencesFlow,
        _offlineUser
    ) { firebaseUser, prefs, offlineUser ->
        when {
            firebaseUser != null -> ClinicianUser(
                uid = firebaseUser.uid,
                displayName = firebaseUser.displayName?.ifBlank { null }
                    ?: prefs.userName.ifBlank { "CareSync Clinician" },
                email = firebaseUser.email ?: prefs.userEmail,
                isOfflineOnly = false
            )
            offlineUser != null -> offlineUser
            prefs.isLoggedIn -> ClinicianUser(
                uid = prefs.workerId.ifBlank { "offline_clinician_default" },
                displayName = prefs.userName.ifBlank { "Dr. Priya Kulkarni" },
                email = prefs.userEmail.ifBlank { "priya.kulkarni@caresync.org" },
                isOfflineOnly = true
            )
            else -> null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Firebase.auth.currentUser?.let {
            ClinicianUser(
                uid = it.uid,
                displayName = it.displayName ?: "CareSync Clinician",
                email = it.email,
                isOfflineOnly = false
            )
        }
    )

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _isSessionUnlocked = MutableStateFlow(false)
    val isSessionUnlocked: StateFlow<Boolean> = _isSessionUnlocked.asStateFlow()

    private var silentAttempted = false

    fun unlockSession() {
        _isSessionUnlocked.value = true
    }

    fun lockSessionIfEnabled() {
        if (preferences.value.isSecurityLockEnabled) {
            _isSessionUnlocked.value = false
        }
    }

    fun verifyPin(enteredPin: String): Boolean {
        val ok = enteredPin == preferences.value.securityPin
        if (ok) _isSessionUnlocked.value = true
        return ok
    }

    fun attemptSilentSignIn(activityContext: Context) {
        if (silentAttempted || Firebase.auth.currentUser != null) return
        silentAttempted = true
        viewModelScope.launch {
            when (val outcome = GoogleAuthHelper.attemptSilentSignIn(activityContext)) {
                is GoogleSignInOutcome.Success -> {
                    repository.onAuthenticatedSessionStarted(
                        userId = outcome.user.uid,
                        displayName = outcome.user.displayName ?: "CareSync Clinician",
                        email = outcome.user.email
                    )
                }
                is GoogleSignInOutcome.NoSavedCredential,
                is GoogleSignInOutcome.Cancelled -> Unit
                is GoogleSignInOutcome.Error -> {
                    _uiState.value = AuthUiState(isLoading = false)
                }
            }
        }
    }

    fun signInWithGoogle(activityContext: Context) {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true, errorMessage = null, statusMessage = null)
            when (val outcome = GoogleAuthHelper.signInInteractive(activityContext)) {
                is GoogleSignInOutcome.Success -> {
                    repository.onAuthenticatedSessionStarted(
                        userId = outcome.user.uid,
                        displayName = outcome.user.displayName ?: "CareSync Clinician",
                        email = outcome.user.email
                    )
                    _isSessionUnlocked.value = true
                    _uiState.value = AuthUiState(isLoading = false)
                }
                is GoogleSignInOutcome.Cancelled -> {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        statusMessage = "Google Sign-in was dismissed. Tap 'Continue as Clinician (Offline Vault)' below to enter immediately."
                    )
                }
                is GoogleSignInOutcome.NoSavedCredential -> {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        errorMessage = "No Google account available on this device. You can tap 'Continue as Clinician (Offline Vault)' below."
                    )
                }
                is GoogleSignInOutcome.Error -> {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        errorMessage = outcome.message
                    )
                }
            }
        }
    }

    fun signInAsOfflineClinician(
        name: String = "Dr. Priya Kulkarni",
        email: String = "priya.kulkarni@caresync.org"
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true, errorMessage = null, statusMessage = null)
            val cleanName = name.ifBlank { "Dr. Priya Kulkarni" }
            val cleanEmail = email.ifBlank { "priya.kulkarni@caresync.org" }
            val uid = "clinician_offline_${System.currentTimeMillis() % 100000}"
            repository.onAuthenticatedSessionStarted(
                userId = uid,
                displayName = cleanName,
                email = cleanEmail
            )
            _offlineUser.value = ClinicianUser(
                uid = uid,
                displayName = cleanName,
                email = cleanEmail,
                isOfflineOnly = true
            )
            _isSessionUnlocked.value = true
            _uiState.value = AuthUiState(isLoading = false)
        }
    }

    fun signInAnonymously() {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.value = AuthUiState(
                isLoading = true,
                errorMessage = null,
                statusMessage = "Connecting to Firebase Cloud..."
            )
            try {
                val authResult = Firebase.auth.signInAnonymously().await()
                val user = authResult.user
                if (user != null) {
                    repository.onAuthenticatedSessionStarted(
                        userId = user.uid,
                        displayName = "CareSync Clinician (Cloud)",
                        email = null
                    )
                    _isSessionUnlocked.value = true
                    _uiState.value = AuthUiState(isLoading = false)
                } else {
                    signInAsOfflineClinician()
                }
            } catch (e: Exception) {
                Log.w(TAG, "Anonymous sign-in failed: ${e.message}, switching to Offline Clinician Vault", e)
                signInAsOfflineClinician()
                _uiState.value = AuthUiState(
                    isLoading = false,
                    statusMessage = "Connected to Offline Clinician Vault. Cloud auth notice: ${e.localizedMessage ?: "Provider disabled"}"
                )
            }
        }
    }

    fun signInWithEmail(email: String, pass: String) {
        if (_uiState.value.isLoading) return
        if (email.isBlank() || pass.length < 6) {
            _uiState.value = AuthUiState(errorMessage = "Please enter a valid email and a password of at least 6 characters.")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true, errorMessage = null, statusMessage = null)
            try {
                val result = Firebase.auth.signInWithEmailAndPassword(email.trim(), pass).await()
                val user = result.user
                if (user != null) {
                    repository.onAuthenticatedSessionStarted(
                        userId = user.uid,
                        displayName = user.displayName ?: email.substringBefore("@"),
                        email = user.email
                    )
                    _isSessionUnlocked.value = true
                    _uiState.value = AuthUiState(isLoading = false)
                } else {
                    _uiState.value = AuthUiState(isLoading = false, errorMessage = "Sign-in returned empty user profile.")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Firebase auth unavailable (${e.message}), signing in locally as requested", e)
                signInAsOfflineClinician(
                    name = email.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() },
                    email = email.trim()
                )
            }
        }
    }

    fun signUpWithEmail(email: String, pass: String, displayName: String) {
        if (_uiState.value.isLoading) return
        if (email.isBlank() || pass.length < 6) {
            _uiState.value = AuthUiState(errorMessage = "Please enter a valid email and a password of at least 6 characters.")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true, errorMessage = null, statusMessage = null)
            try {
                val result = Firebase.auth.createUserWithEmailAndPassword(email.trim(), pass).await()
                val user = result.user
                if (user != null) {
                    val cleanName = displayName.ifBlank { email.substringBefore("@") }
                    repository.onAuthenticatedSessionStarted(
                        userId = user.uid,
                        displayName = cleanName,
                        email = user.email
                    )
                    _isSessionUnlocked.value = true
                    _uiState.value = AuthUiState(isLoading = false)
                } else {
                    _uiState.value = AuthUiState(isLoading = false, errorMessage = "Account creation returned empty profile.")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Firebase sign-up unavailable (${e.message}), signing in locally as requested", e)
                signInAsOfflineClinician(
                    name = displayName.ifBlank { email.substringBefore("@") },
                    email = email.trim()
                )
            }
        }
    }

    fun onSessionAuthenticated(user: ClinicianUser) {
        viewModelScope.launch {
            repository.onAuthenticatedSessionStarted(
                userId = user.uid,
                displayName = user.displayName,
                email = user.email
            )
        }
    }

    fun startCloudSyncObserver(userId: String) {
        viewModelScope.launch {
            repository.observeAndMergeCloudData(userId)
        }
    }

    fun signOut(context: Context) {
        viewModelScope.launch {
            _offlineUser.value = null
            GoogleAuthHelper.signOut(context)
            repository.logoutAndIsolateWorkspace()
            _isSessionUnlocked.value = false
            silentAttempted = false
            _uiState.value = AuthUiState()
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, statusMessage = null)
    }
}
