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
import com.caresync.app.theme.CareSyncPurple
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
    var nameInput by remember { mutableStateOf("Dr. Priya Kulkarni") }
    var emailInput by remember { mutableStateOf("priya.kulkarni@gmail.com") }
    var passwordInput by remember { mutableStateOf("CareSync2026") }

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
                        text = "CareSync Local Vault + Cloud Ready",
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
                        text = "Clinician Sign-In",
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
                            tint = CareSyncPurple,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Enter your details to sign in immediately. No cloud account verification required.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Clinician Name") },
                        leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null, tint = CareSyncPurple) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = CareSyncButtonShape
                    )

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Gmail / Email Address") },
                        leadingIcon = { Icon(Icons.Rounded.Email, contentDescription = null, tint = CareSyncPurple) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = CareSyncButtonShape
                    )

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null, tint = CareSyncPurple) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = CareSyncButtonShape
                    )

                    // Single Direct Sign In Button (no new vs existing check, guaranteed sign in)
                    Button(
                        onClick = {
                            onClearMessages()
                            val cleanName = nameInput.ifBlank { "Dr. Priya Kulkarni" }
                            val cleanEmail = emailInput.ifBlank { "priya.kulkarni@gmail.com" }
                            onOfflineClinicianClick(cleanName, cleanEmail)
                        },
                        enabled = !uiState.isLoading,
                        shape = CareSyncButtonShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("offline_clinician_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CareSyncPurple)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CloudDone,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Sign In as Clinician", fontWeight = FontWeight.Bold)
                    }

                    // 1-Tap Quick Demo Sign-In
                    OutlinedButton(
                        onClick = {
                            onClearMessages()
                            onOfflineClinicianClick("Dr. Priya Kulkarni", "priya.kulkarni@gmail.com")
                        },
                        shape = CareSyncButtonShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Icon(Icons.Rounded.Bolt, contentDescription = null, tint = CareSyncPurple, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("⚡ 1-Tap Quick Demo Access")
                    }
                }
            }
        }
    }
}
