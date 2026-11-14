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
import androidx.compose.ui.unit.dp
import com.caresync.app.model.AppLanguage
import com.caresync.app.theme.CareSyncBlue
import com.caresync.app.theme.CareSyncButtonShape
import com.caresync.app.theme.CareSyncCardShape
import com.caresync.app.theme.CriticalRed
import com.caresync.app.theme.PendingOrange
import com.caresync.app.viewmodel.SettingsUiState

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    syncResultMessage: String?,
    onToggleDarkMode: (Boolean) -> Unit,
    onToggleOfflineOnly: (Boolean) -> Unit,
    onToggleSecurityLock: (Boolean) -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit,
    onSyncNow: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenHelp: () -> Unit,
    onLogout: () -> Unit
) {
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

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
                Text("Preferences", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                SettingsToggleRow(
                    icon = Icons.Rounded.DarkMode,
                    title = "Dark Mode",
                    subtitle = "Low-glare clinical palette for night rounds",
                    checked = state.preferences.isDarkMode,
                    testTag = "settings_dark_mode_switch",
                    onCheckedChange = onToggleDarkMode
                )
                HorizontalDivider()
                SettingsNavRow(
                    icon = Icons.Rounded.Language,
                    title = "Language",
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
                    title = "Offline-Only Mode",
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
                    title = "Security Lock",
                    subtitle = "Require PIN / biometric verification on resume",
                    checked = state.preferences.isSecurityLockEnabled,
                    testTag = "settings_security_switch",
                    onCheckedChange = onToggleSecurityLock
                )
                HorizontalDivider()
                SettingsNavRow(Icons.Rounded.Policy, "Privacy Policy", "Local Room encryption & data handling", { showPrivacyDialog = true })
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
            Text("Logout", fontWeight = FontWeight.Bold)
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
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
