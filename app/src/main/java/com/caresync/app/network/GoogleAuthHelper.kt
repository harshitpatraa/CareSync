package com.caresync.app.network

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.caresync.app.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val TAG = "Auth"

sealed interface GoogleSignInOutcome {
    data class Success(val user: FirebaseUser) : GoogleSignInOutcome
    data object Cancelled : GoogleSignInOutcome
    data object NoSavedCredential : GoogleSignInOutcome
    data class Error(val message: String) : GoogleSignInOutcome
}

fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        trySend(firebaseAuth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

object GoogleAuthHelper {

    suspend fun attemptSilentSignIn(activityContext: Context): GoogleSignInOutcome {
        val credentialManager = CredentialManager.create(activityContext)
        val serverClientId = activityContext.getString(R.string.default_web_client_id)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(true)
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return executeCredentialRequest(activityContext, credentialManager, request, isSilent = true)
    }

    suspend fun signInInteractive(activityContext: Context): GoogleSignInOutcome {
        val credentialManager = CredentialManager.create(activityContext)
        val serverClientId = activityContext.getString(R.string.default_web_client_id)
        val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(serverClientId)
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        return executeCredentialRequest(activityContext, credentialManager, request, isSilent = false)
    }

    private suspend fun executeCredentialRequest(
        activityContext: Context,
        credentialManager: CredentialManager,
        request: GetCredentialRequest,
        isSilent: Boolean
    ): GoogleSignInOutcome {
        return try {
            val result = credentialManager.getCredential(
                request = request,
                context = activityContext
            )
            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult = Firebase.auth.signInWithCredential(firebaseCredential).await()
                val user = authResult.user
                if (user != null) {
                    GoogleSignInOutcome.Success(user)
                } else {
                    GoogleSignInOutcome.Error("Firebase authentication returned no user profile.")
                }
            } else {
                GoogleSignInOutcome.Error("Unexpected credential type received from Credential Manager.")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w(TAG, "Google Sign-In cancelled or dismissed: ${e.message}", e)
            GoogleSignInOutcome.Cancelled
        } catch (e: NoCredentialException) {
            if (isSilent) {
                Log.d(TAG, "No authorized Google account cached for silent sign-in.")
                GoogleSignInOutcome.NoSavedCredential
            } else {
                Log.w(TAG, "No Google credential available: ${e.message}", e)
                GoogleSignInOutcome.Error("No Google account found on device. Add a Google account to sign in.")
            }
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Credential Manager error: ${e.message}", e)
            GoogleSignInOutcome.Error(e.localizedMessage ?: "Google Sign-In failed. Please try again.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Firebase Google Sign-In failed: ${e.message}", e)
            GoogleSignInOutcome.Error(e.localizedMessage ?: "Authentication failed.")
        }
    }

    suspend fun signOut(context: Context) {
        withContext(NonCancellable) {
            try {
                CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
            } catch (e: ClearCredentialException) {
                Log.w(TAG, "Failed to clear Credential Manager state: ${e.message}", e)
            } catch (e: Exception) {
                Log.w(TAG, "Credential state cleanup skipped: ${e.message}", e)
            }
            Firebase.auth.signOut()
        }
    }
}
