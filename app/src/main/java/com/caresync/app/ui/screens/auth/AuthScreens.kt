package com.caresync.app.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.caresync.app.components.CareSyncLogo
import com.caresync.app.theme.CareSyncBlue
import com.caresync.app.theme.CareSyncButtonShape
import com.caresync.app.theme.CareSyncCardShape
import com.caresync.app.theme.CareSyncCyan
import com.caresync.app.theme.CareSyncGreen
import com.caresync.app.viewmodel.AuthUiState
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val logoScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.72f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "splash_logo_scale"
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(950L)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A0C0E), Color(0xFF14171B), Color(0xFF0F172A))
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            CareSyncLogo(
                size = 104.dp,
                animateNodes = true,
                showBadgeBackground = false,
                modifier = Modifier.scale(logoScale)
            )
            Spacer(modifier = Modifier.height(20.dp))
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically { it / 3 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "CareSync",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Synchronized Offline-First Healthcare",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.88f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    Surface(
                        shape = CareSyncButtonShape,
                        color = Color.White.copy(alpha = 0.08f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = CareSyncCyan,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Initializing CareSync clinician vault...",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onOfflineClinicianClick: (name: String, email: String) -> Unit,
    onEmailSignInClick: (email: String, pass: String) -> Unit,
    onEmailSignUpClick: (email: String, pass: String, name: String) -> Unit,
    onClearMessages: () -> Unit
) {
    var emailInput by remember { mutableStateOf("priya.kulkarni@caresync.org") }
    var passwordInput by remember { mutableStateOf("CareSync2026") }
    var nameInput by remember { mutableStateOf("Dr. Priya Kulkarni") }
    var isSignUpMode by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CareSyncLogo(size = 80.dp, animateNodes = true)
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Welcome to CareSync",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Offline-First Healthcare · Patient Records · Sehat Setu AI",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(50),
                color = CareSyncGreen.copy(alpha = 0.12f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CloudDone,
                        contentDescription = "Cloud & Offline Sync Ready",
                        tint = CareSyncGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "CareSync Cloud + Offline Vault",
                        style = MaterialTheme.typography.labelMedium,
                        color = CareSyncGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                shape = CareSyncCardShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Clinician Workspace Sign-In",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = null,
                            tint = CareSyncBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Patient records and vitals are encrypted and stored locally in Room.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Sync,
                            contentDescription = null,
                            tint = CareSyncCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Works 100% offline. Syncs with cloud ledger when network is available.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (uiState.errorMessage != null) {
                        Surface(
                            shape = CareSyncButtonShape,
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = uiState.errorMessage,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    if (uiState.statusMessage != null) {
                        Surface(
                            shape = CareSyncButtonShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = uiState.statusMessage,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // PRIMARY OPTION 1: Continue as Clinician (Offline Vault) - 1-Click Access
                    Button(
                        onClick = {
                            onClearMessages()
                            onOfflineClinicianClick("Dr. Priya Kulkarni", "priya.kulkarni@caresync.org")
                        },
                        enabled = !uiState.isLoading,
                        shape = CareSyncButtonShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("offline_clinician_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CareSyncGreen)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CloudDone,
                            contentDescription = "Offline Vault Access",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Continue as Clinician (Offline Vault)", fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f))
                        Text(
                            text = "  OR SIGN IN WITH EMAIL  ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f))
                    }

                    // OPTION 2: Email & Password Authentication
                    if (isSignUpMode) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Clinician Full Name") },
                            leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = CareSyncButtonShape
                        )
                    }

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Rounded.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = CareSyncButtonShape
                    )

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = CareSyncButtonShape
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                onClearMessages()
                                if (isSignUpMode) {
                                    onEmailSignUpClick(emailInput, passwordInput, nameInput)
                                } else {
                                    onEmailSignInClick(emailInput, passwordInput)
                                }
                            },
                            enabled = !uiState.isLoading,
                            shape = CareSyncButtonShape,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CareSyncBlue)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text(if (isSignUpMode) "Register" else "Sign In", fontWeight = FontWeight.Bold)
                            }
                        }

                        TextButton(
                            onClick = { isSignUpMode = !isSignUpMode },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (isSignUpMode) "Existing User? Sign In" else "New Clinician? Register")
                        }
                    }

                    // OPTION 3: 1-Tap Quick Demo Sign-In
                    OutlinedButton(
                        onClick = {
                            onClearMessages()
                            onOfflineClinicianClick("Dr. Priya Kulkarni", "priya.kulkarni@caresync.org")
                        },
                        shape = CareSyncButtonShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Icon(Icons.Rounded.Bolt, contentDescription = null, tint = CareSyncCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Quick Demo Login (Dr. Priya Kulkarni)")
                    }
                }
            }
        }
    }
}
