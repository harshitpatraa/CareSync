package com.caresync.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val statusMessage: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: CareSyncRepository
) : ViewModel() {

    val preferences: StateFlow<AppPreferences> = repository.preferencesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppPreferences()
    )

    val currentUser: StateFlow<FirebaseUser?> = Firebase.auth.authStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Firebase.auth.currentUser
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
                        statusMessage = "Sign-in was cancelled. Tap Sign in with Google to continue."
                    )
                }
                is GoogleSignInOutcome.NoSavedCredential -> {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        errorMessage = "No Google account available on this device."
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

    fun onSessionAuthenticated(user: FirebaseUser) {
        viewModelScope.launch {
            repository.onAuthenticatedSessionStarted(
                userId = user.uid,
                displayName = user.displayName ?: "CareSync Clinician",
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
