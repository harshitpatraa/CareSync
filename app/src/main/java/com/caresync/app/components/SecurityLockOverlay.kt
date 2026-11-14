package com.caresync.app.components

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.caresync.app.theme.CareSyncBlue
import com.caresync.app.theme.CareSyncButtonShape
import com.caresync.app.theme.CriticalRed

/**
 * Prevents system camera / photo picker / speech / biometric flows from immediately
 * locking the CareSync security overlay when MainActivity receives ON_STOP.
 */
object AppLockGuard {
    @Volatile
    var suppressNextAutoLock: Boolean = false
}

@Composable
fun SecurityLockOverlay(
    userName: String,
    onVerifyPin: (String) -> Boolean,
    onBiometricSuccess: () -> Unit,
    onSignOutFallback: () -> Unit
) {
    val context = LocalContext.current
    var enteredPin by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    val biometricAvailable = remember(context) {
        val manager = BiometricManager.from(context)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        manager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun launchBiometricPrompt() {
        val activity = context as? FragmentActivity ?: return
        AppLockGuard.suppressNextAutoLock = true
        val executor = ContextCompat.getMainExecutor(activity)
        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    AppLockGuard.suppressNextAutoLock = false
                    onBiometricSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    AppLockGuard.suppressNextAutoLock = false
                }
            }
        )
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock CareSync Clinical Workspace")
            .setSubtitle("Verify identity for $userName")
            .setNegativeButtonText("Use 4-Digit PIN")
            .build()
        runCatching { prompt.authenticate(promptInfo) }
    }

    LaunchedEffect(biometricAvailable) {
        if (biometricAvailable) {
            launchBiometricPrompt()
        }
    }

    fun appendDigit(digit: String) {
        if (enteredPin.length >= 4) return
        val next = enteredPin + digit
        enteredPin = next
        errorText = null
        if (next.length == 4) {
            if (!onVerifyPin(next)) {
                errorText = "Incorrect PIN. Try again."
                enteredPin = ""
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("security_lock_overlay"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(16.dp))
                CareSyncLogo(size = 64.dp, animateNodes = false)
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Lock, contentDescription = null, tint = CareSyncBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CareSync Clinical Lock",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Enter your 4-digit CareSync PIN to unlock $userName's patient records.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 4-dot PIN indicator
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    repeat(4) { idx ->
                        val filled = idx < enteredPin.length
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(
                                    if (filled) CareSyncBlue
                                    else MaterialTheme.colorScheme.outlineVariant
                                )
                        )
                    }
                }

                errorText?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = CriticalRed,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Keypad
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val rows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9")
                )
                rows.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        row.forEach { digit ->
                            PinKeyButton(label = digit, onClick = { appendDigit(digit) })
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (biometricAvailable) {
                        FilledTonalButton(
                            onClick = { launchBiometricPrompt() },
                            shape = CircleShape,
                            modifier = Modifier.size(68.dp).testTag("pin_biometric_button")
                        ) {
                            Icon(Icons.Rounded.Fingerprint, contentDescription = "Unlock with biometric")
                        }
                    } else {
                        Spacer(modifier = Modifier.size(68.dp))
                    }
                    PinKeyButton(label = "0", onClick = { appendDigit("0") })
                    FilledTonalButton(
                        onClick = {
                            if (enteredPin.isNotEmpty()) {
                                enteredPin = enteredPin.dropLast(1)
                                errorText = null
                            }
                        },
                        shape = CircleShape,
                        modifier = Modifier.size(68.dp).testTag("pin_backspace_button")
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.Backspace, contentDescription = "Delete digit")
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onSignOutFallback,
                    shape = CareSyncButtonShape,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Switch Account")
                }
                if (biometricAvailable) {
                    Button(
                        onClick = { launchBiometricPrompt() },
                        shape = CareSyncButtonShape,
                        colors = ButtonDefaults.buttonColors(containerColor = CareSyncBlue),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.Fingerprint, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Biometric")
                    }
                }
            }
        }
    }
}

@Composable
private fun PinKeyButton(label: String, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        shape = CircleShape,
        modifier = Modifier
            .size(68.dp)
            .testTag("pin_key_$label")
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
