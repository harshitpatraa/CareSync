package com.caresync.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caresync.app.repository.AppPreferences
import com.caresync.app.repository.CareSyncRepository
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
    val resetSentMessage: String? = null
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

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun ensureSeeded() {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.length < 4) {
            _uiState.value = AuthUiState(errorMessage = "Enter a valid email and at least 4-character PIN/password.")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            repository.ensureSeeded()
            repository.login(email.trim())
            _uiState.value = AuthUiState()
            onSuccess()
        }
    }

    fun demoQuickLogin(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            repository.ensureSeeded()
            repository.login("priya.kulkarni@caresync.org", "Dr. Priya Kulkarni")
            _uiState.value = AuthUiState()
            onSuccess()
        }
    }

    fun signup(name: String, email: String, clinic: String, password: String, onSuccess: () -> Unit) {
        if (name.isBlank() || email.isBlank() || password.length < 4) {
            _uiState.value = AuthUiState(errorMessage = "Please fill in your name, email, and password (4+ chars).")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            repository.ensureSeeded()
            repository.login(email.trim(), name.trim())
            _uiState.value = AuthUiState()
            onSuccess()
        }
    }

    fun sendPasswordReset(email: String) {
        if (email.isBlank() || !email.contains("@")) {
            _uiState.value = AuthUiState(errorMessage = "Enter a valid registered email address.")
            return
        }
        _uiState.value = AuthUiState(
            resetSentMessage = "Offline recovery code CS-8421 queued for $email. Contact hub admin if offline."
        )
    }

    fun clearMessages() {
        _uiState.value = AuthUiState()
    }
}
