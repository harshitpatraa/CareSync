package com.caresync.app.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.caresync.app.model.AppLanguage
import com.caresync.app.theme.CareSyncBlue
import com.caresync.app.theme.CareSyncButtonShape
import com.caresync.app.theme.CareSyncCardShape
import com.caresync.app.theme.CriticalRed
import com.caresync.app.theme.PendingOrange
import com.caresync.app.utils.LocalCareSyncStrings
import com.caresync.app.viewmodel.SettingsUiState

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    syncResultMessage: String?,
    onToggleDarkMode: (Boolean) -> Unit,
    onToggleOfflineOnly: (Boolean) -> Unit,
    onToggleSecurityLock: (Boolean) -> Unit,
    onSetSecurityPin: (String) -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit,
    onUpdateApiKey: (String) -> Unit = {},
    onUpdateFirebaseConfig: (String, String, String) -> Unit = { _, _, _ -> },
    onSyncNow: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenHelp: () -> Unit,
    onLogout: () -> Unit
) {
    val strings = LocalCareSyncStrings.current
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showPinDialog by remember { mutableStateOf(false) }
    var showFirebaseDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Appearance & Localization
        Card(
            shape = CareSyncCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(strings.settingsPreferences, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                SettingsToggleRow(
                    icon = Icons.Rounded.DarkMode,
                    title = strings.darkMode,
                    subtitle = "Low-glare clinical palette for night rounds",
                    checked = state.preferences.isDarkMode,
                    testTag = "settings_dark_mode_switch",
                    onCheckedChange = onToggleDarkMode
                )
                HorizontalDivider()
                SettingsNavRow(
                    icon = Icons.Rounded.Language,
                    title = strings.language,
                    subtitle = state.preferences.language.displayName,
                    onClick = { showLanguageDialog = true }
                )
            }
        }

        // Offline Sync & Pending Queue
        Card(
            shape = CareSyncCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Offline Sync Engine", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                SettingsToggleRow(
                    icon = Icons.Rounded.CloudSync,
                    title = strings.offlineOnlyMode,
                    subtitle = "Keep all records local in Room queue until toggled off",
                    checked = state.preferences.isOfflineOnlyMode,
                    testTag = "settings_offline_switch",
                    onCheckedChange = onToggleOfflineOnly
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pending Sync Queue: ${state.pendingQueue.size}",
                        style = MaterialTheme.typography.titleSmall,
                        color = if (state.pendingQueue.isNotEmpty()) PendingOrange else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = onSyncNow,
                        shape = CareSyncButtonShape,
                        colors = ButtonDefaults.buttonColors(containerColor = CareSyncBlue),
                        modifier = Modifier.testTag("settings_sync_now_button")
                    ) {
                        Text("Sync Queue")
                    }
                }
                if (syncResultMessage != null) {
                    Text(syncResultMessage, style = MaterialTheme.typography.bodySmall, color = CareSyncBlue)
                }
                state.pendingQueue.take(5).forEach { q ->
                    Surface(
                        shape = CareSyncButtonShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("${q.patientName} · ${q.operation}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            Text(q.payloadSummary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Firebase Cloud Connection
        Card(
            shape = CareSyncCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Firebase Cloud Connection", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, softWrap = true)
                Text(
                    text = if (state.preferences.firebaseProjectId.isNotBlank())
                        "Linked: ${state.preferences.firebaseProjectId} (Custom Firebase Project)"
                    else
                        "Default: gen-lang-client-0413618590 (Google AI Studio Cloud)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    softWrap = true
                )
                HorizontalDivider()
                SettingsNavRow(
                    icon = Icons.Rounded.CloudSync,
                    title = "Connect My Firebase",
                    subtitle = if (state.preferences.firebaseProjectId.isNotBlank())
                        "Linked to ${state.preferences.firebaseProjectId} · Tap to update"
                    else
                        "Tap to connect your personal Firebase project & credentials",
                    onClick = { showFirebaseDialog = true }
                )
            }
        }

        // Account, Security & Info
        Card(
            shape = CareSyncCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Account & Security", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                SettingsNavRow(Icons.Rounded.Person, "Profile", state.profile.name, onOpenProfile)
                HorizontalDivider()
                SettingsToggleRow(
                    icon = Icons.Rounded.Lock,
                    title = strings.securityLock,
                    subtitle = "Require PIN / biometric verification on resume",
                    checked = state.preferences.isSecurityLockEnabled,
                    testTag = "settings_security_switch",
                    onCheckedChange = onToggleSecurityLock
                )
                HorizontalDivider()
                SettingsNavRow(
                    icon = Icons.Rounded.Password,
                    title = "Change 4-Digit Security PIN",
                    subtitle = "Current PIN is set (default 1234)",
                    onClick = { showPinDialog = true }
                )
                HorizontalDivider()
                SettingsNavRow(Icons.Rounded.Policy, "Privacy Policy", "Local Room storage & cloud sync security", { showPrivacyDialog = true })
                HorizontalDivider()
                SettingsNavRow(Icons.Rounded.Info, "About CareSync", "Version 1.0.0 · Offline-First Architecture", onOpenAbout)
                HorizontalDivider()
                SettingsNavRow(Icons.AutoMirrored.Rounded.HelpOutline, "Help & Field Guides", "FAQs, triage guide & sync support", onOpenHelp)
            }
        }

        Button(
            onClick = onLogout,
            shape = CareSyncButtonShape,
            colors = ButtonDefaults.buttonColors(containerColor = CriticalRed),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("settings_logout_button")
        ) {
            Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(strings.logout, fontWeight = FontWeight.Bold)
        }
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select CareSync Language") },
            text = {
                Column {
                    AppLanguage.entries.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectLanguage(lang)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = state.preferences.language == lang,
                                onClick = {
                                    onSelectLanguage(lang)
                                    showLanguageDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(lang.displayName)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) { Text("Close") }
            }
        )
    }

    if (showPinDialog) {
        var newPin by remember { mutableStateOf("") }
        var pinError by remember { mutableStateOf<String?>(null) }
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("Set 4-Digit Security PIN") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Choose a 4-digit PIN used when Biometric unlock is unavailable.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = newPin,
                        onValueChange = { input ->
                            newPin = input.filter { it.isDigit() }.take(4)
                            pinError = null
                        },
                        label = { Text("4-Digit PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true
                    )
                    pinError?.let {
                        Text(it, color = CriticalRed, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPin.length == 4) {
                            onSetSecurityPin(newPin)
                            showPinDialog = false
                        } else {
                            pinError = "PIN must be exactly 4 digits."
                        }
                    }
                ) {
                    Text("Save PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("CareSync Privacy Policy") },
            text = {
                Text(
                    "CareSync operates with an offline-first architecture. All patient demographics, vitals, maternal/child tracking, and clinical assessments are stored locally on your device inside Room SQLite storage and only enter the synchronization queue when explicitly approved.",
                    style = MaterialTheme.typography.bodySmall
                )
            },
            confirmButton = {
                Button(onClick = { showPrivacyDialog = false }) { Text("Understood") }
            }
        )
    }

    if (showFirebaseDialog) {
        var projectIdInput by remember { mutableStateOf(state.preferences.firebaseProjectId) }
        var appIdInput by remember { mutableStateOf(state.preferences.firebaseAppId) }
        var apiKeyInput by remember { mutableStateOf(state.preferences.geminiApiKey) }

        AlertDialog(
            onDismissRequest = { showFirebaseDialog = false },
            title = { Text("Connect Your Firebase Project", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, softWrap = true) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Connect your personal Firebase backend to CareSync. You can find these in Firebase Console -> Project Settings -> General -> Your Apps, or place your downloaded google-services.json directly into the project's app/ directory.",
                        style = MaterialTheme.typography.bodySmall,
                        softWrap = true
                    )
                    OutlinedTextField(
                        value = projectIdInput,
                        onValueChange = { projectIdInput = it },
                        label = { Text("Firebase Project ID", softWrap = true) },
                        placeholder = { Text("e.g. caresync-healthcare") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = appIdInput,
                        onValueChange = { appIdInput = it },
                        label = { Text("Firebase App ID", softWrap = true) },
                        placeholder = { Text("1:879174369515:android:...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        label = { Text("Firebase Web API Key", softWrap = true) },
                        placeholder = { Text("AIzaSy...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (projectIdInput.isNotBlank() || appIdInput.isNotBlank()) {
                        TextButton(
                            onClick = {
                                projectIdInput = ""
                                appIdInput = ""
                                apiKeyInput = ""
                                onUpdateFirebaseConfig("", "", "")
                                showFirebaseDialog = false
                            }
                        ) {
                            Text("Reset to Default AI Studio Project", color = CriticalRed, softWrap = true)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateFirebaseConfig(projectIdInput.trim(), appIdInput.trim(), apiKeyInput.trim())
                        showFirebaseDialog = false
                    },
                    shape = CareSyncButtonShape,
                    colors = ButtonDefaults.buttonColors(containerColor = CareSyncBlue)
                ) {
                    Text("Save & Connect", softWrap = false)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFirebaseDialog = false }) {
                    Text("Cancel", softWrap = false)
                }
            }
        )
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    testTag: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = CareSyncBlue, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, softWrap = true)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, softWrap = true)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
private fun SettingsNavRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = CareSyncBlue, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, softWrap = true)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, softWrap = true)
        }
        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
