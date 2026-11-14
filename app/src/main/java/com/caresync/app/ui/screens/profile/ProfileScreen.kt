package com.caresync.app.ui.screens.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.caresync.app.theme.CareSyncBlue
import com.caresync.app.theme.CareSyncButtonShape
import com.caresync.app.theme.CareSyncCardShape
import com.caresync.app.theme.CareSyncGreen
import com.caresync.app.theme.CriticalRed
import com.caresync.app.utils.FormatUtils
import com.caresync.app.viewmodel.SettingsUiState

@Composable
fun ProfileScreen(
    state: SettingsUiState,
    onUpdateProfile: (name: String, role: String, workerId: String, clinicName: String, district: String, phone: String) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenHelp: () -> Unit,
    onLogout: () -> Unit
) {
    val profile = state.profile
    var showEditDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            shape = CareSyncCardShape,
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(CareSyncBlue, Color(0xFF1A5FD9))))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.22f),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = FormatUtils.initials(profile.name),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(profile.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(profile.role, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.88f))
                        Text(profile.email, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
                    }
                    IconButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.testTag("edit_profile_button")
                    ) {
                        Icon(Icons.Rounded.Edit, contentDescription = "Edit profile", tint = Color.White)
                    }
                }
            }
        }

        Card(
            shape = CareSyncCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ProfileRow(Icons.Rounded.Badge, "Worker ID", profile.workerId)
                ProfileRow(Icons.Rounded.LocalHospital, "Assigned Hub", profile.clinicName)
                ProfileRow(Icons.Rounded.Map, "District / Sector", profile.district)
                ProfileRow(Icons.Rounded.Phone, "Phone", profile.phone)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Synced Cohort Records", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "${state.syncedPatientCount} / ${state.totalPatientCount} Synced",
                        style = MaterialTheme.typography.titleSmall,
                        color = CareSyncGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        OutlinedButton(
            onClick = onOpenSettings,
            shape = CareSyncButtonShape,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Icon(Icons.Rounded.Settings, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Settings & Offline Sync Queue")
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = onOpenAbout,
                shape = CareSyncButtonShape,
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Icon(Icons.Rounded.Info, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("About")
            }
            OutlinedButton(
                onClick = onOpenHelp,
                shape = CareSyncButtonShape,
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Icon(Icons.AutoMirrored.Rounded.HelpOutline, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Help")
            }
        }

        Button(
            onClick = onLogout,
            shape = CareSyncButtonShape,
            colors = ButtonDefaults.buttonColors(containerColor = CriticalRed),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout from CareSync", fontWeight = FontWeight.Bold)
        }
    }

    if (showEditDialog) {
        var name by remember { mutableStateOf(profile.name) }
        var role by remember { mutableStateOf(profile.role) }
        var workerId by remember { mutableStateOf(profile.workerId) }
        var clinicName by remember { mutableStateOf(profile.clinicName) }
        var district by remember { mutableStateOf(profile.district) }
        var phone by remember { mutableStateOf(profile.phone) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Clinician Profile") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(name, { name = it }, label = { Text("Full Name") }, singleLine = true)
                    OutlinedTextField(role, { role = it }, label = { Text("Clinical Role") }, singleLine = true)
                    OutlinedTextField(workerId, { workerId = it }, label = { Text("Worker ID") }, singleLine = true)
                    OutlinedTextField(clinicName, { clinicName = it }, label = { Text("Assigned Hub") }, singleLine = true)
                    OutlinedTextField(district, { district = it }, label = { Text("District / Circle") }, singleLine = true)
                    OutlinedTextField(
                        phone,
                        { phone = it },
                        label = { Text("Contact Phone") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateProfile(name, role, workerId, clinicName, district, phone)
                        showEditDialog = false
                    },
                    enabled = name.isNotBlank()
                ) {
                    Text("Save Profile")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun ProfileRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = CareSyncBlue, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}
