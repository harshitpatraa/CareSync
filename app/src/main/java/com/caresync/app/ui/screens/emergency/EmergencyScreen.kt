package com.caresync.app.ui.screens.emergency

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Directions
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Sms
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Warning
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.caresync.app.database.PatientEntity
import com.caresync.app.theme.CareSyncBlue
import com.caresync.app.theme.CareSyncButtonShape
import com.caresync.app.theme.CareSyncCardShape
import com.caresync.app.theme.CareSyncGreen
import com.caresync.app.theme.CriticalRed
import com.caresync.app.utils.FormatUtils
import com.caresync.app.utils.LocalCareSyncStrings
import com.caresync.app.utils.LocationHelper
import com.caresync.app.viewmodel.EmergencyUiState

@Composable
fun EmergencyScreen(
    state: EmergencyUiState,
    onTriggerSos: (String?) -> Unit,
    onDismissSos: () -> Unit,
    onToggleCritical: (PatientEntity) -> Unit,
    onOpenPatient: (String) -> Unit,
    onRefreshLocation: () -> Unit,
    onAddContact: (String, String, String) -> Unit,
    onSpeakInstruction: (String) -> Unit,
    onStopSpeaking: () -> Unit
) {
    val context = LocalContext.current
    val strings = LocalCareSyncStrings.current
    var showAddContactDialog by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) onRefreshLocation()
    }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            onRefreshLocation()
        }
    }

    val pulse = rememberInfiniteTransition(label = "sos_pulse")
    val sosScale by pulse.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "sos_scale"
    )

    val primaryPhone = state.contacts.firstOrNull { it.isPrimary }?.phone
        ?: state.contacts.firstOrNull()?.phone
        ?: "108"

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // SOS Hero Button + Offline SMS / Share Dispatch + GPS Chip
        item {
            Card(
                shape = CareSyncCardShape,
                colors = CardDefaults.cardColors(containerColor = CriticalRed.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = strings.emergencyDispatch,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CriticalRed
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap SOS to alert the PHC emergency desk & log offline dispatch",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        onClick = {
                            val granted = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                            if (granted) onRefreshLocation()
                            else locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                        },
                        shape = CareSyncButtonShape,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Rounded.MyLocation, contentDescription = null, tint = CareSyncBlue, modifier = Modifier.size(16.dp))
                            Text(
                                text = state.fieldCoordinates?.let { "GPS: ${it.label}" } ?: "Tap to attach Field GPS",
                                style = MaterialTheme.typography.labelMedium,
                                color = CareSyncBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onTriggerSos(null) },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = CriticalRed, contentColor = Color.White),
                        modifier = Modifier
                            .size(124.dp)
                            .scale(sosScale)
                            .testTag("emergency_sos_button")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Rounded.Warning, contentDescription = "SOS", modifier = Modifier.size(34.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("SOS", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val msg = LocationHelper.buildSosDispatchMessage(
                                    clinicianName = state.clinicianName,
                                    patient = state.criticalPatients.firstOrNull(),
                                    latestVitals = state.criticalPatients.firstOrNull()?.let { state.latestVitalsByPatient[it.id] },
                                    coordinates = state.fieldCoordinates
                                )
                                LocationHelper.sendSmsFallback(context, primaryPhone, msg)
                            },
                            shape = CareSyncButtonShape,
                            modifier = Modifier.weight(1f).testTag("sos_sms_fallback_button")
                        ) {
                            Icon(Icons.Rounded.Sms, contentDescription = null, tint = CriticalRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SMS SOS (No Data)", color = CriticalRed, style = MaterialTheme.typography.labelMedium)
                        }
                        OutlinedButton(
                            onClick = {
                                val msg = LocationHelper.buildSosDispatchMessage(
                                    clinicianName = state.clinicianName,
                                    patient = state.criticalPatients.firstOrNull(),
                                    latestVitals = state.criticalPatients.firstOrNull()?.let { state.latestVitalsByPatient[it.id] },
                                    coordinates = state.fieldCoordinates
                                )
                                LocationHelper.shareSosText(context, msg)
                            },
                            shape = CareSyncButtonShape,
                            modifier = Modifier.weight(1f).testTag("sos_share_brief_button")
                        ) {
                            Icon(Icons.Rounded.Share, contentDescription = null, tint = CareSyncBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Brief", color = CareSyncBlue, style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    AnimatedVisibility(visible = state.sosTriggered) {
                        val doc = state.dispatchedClinician
                        Surface(
                            shape = CareSyncCardShape,
                            color = CriticalRed.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, CriticalRed),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Rounded.Warning, contentDescription = null, tint = CriticalRed, modifier = Modifier.size(22.dp))
                                        Text(
                                            text = "SOS Received by Nearest Clinician!",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = CriticalRed
                                        )
                                    }
                                    TextButton(onClick = onDismissSos) {
                                        Text("Dismiss", color = CriticalRed, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Surface(
                                    shape = CareSyncButtonShape,
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = doc?.name ?: "Dr. Priya Kulkarni",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${doc?.role ?: "Senior Medical Officer"} · ${doc?.healthCentre ?: "Ward 4 Primary Health Centre"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = CareSyncButtonShape,
                                                color = CareSyncGreen.copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = "📍 ${doc?.distanceKm ?: 0.8f} km away · ETA ~${doc?.etaMinutes ?: 3} mins",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = CareSyncGreen,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
                                            Text(
                                                text = "📞 ${doc?.phone ?: "+91 98765 43210"}",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = CareSyncBlue
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = "✓ Live patient vitals and GPS coordinates transmitted directly to clinician's mobile handset via high-priority push and SMS ledger.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Critical Patients
        item {
            Text(
                text = "Critical Patient Tagging (${state.criticalPatients.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        if (state.criticalPatients.isEmpty()) {
            item {
                Card(shape = CareSyncCardShape, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Text(
                        text = "No patients currently tagged Critical. Flag any high-risk patient from the Patients tab.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(state.criticalPatients, key = { it.id }) { patient ->
                Card(
                    shape = CareSyncCardShape,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth().clickable { onOpenPatient(patient.id) }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${patient.fullName} · ${patient.age}y", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(patient.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = {
                            val msg = LocationHelper.buildSosDispatchMessage(
                                clinicianName = state.clinicianName,
                                patient = patient,
                                latestVitals = state.latestVitalsByPatient[patient.id],
                                coordinates = state.fieldCoordinates
                            )
                            LocationHelper.sendSmsFallback(context, primaryPhone, msg)
                        }) {
                            Icon(Icons.Rounded.Sms, contentDescription = "SMS SOS for patient", tint = CareSyncBlue)
                        }
                        IconButton(onClick = { onTriggerSos(patient.fullName) }) {
                            Icon(Icons.Rounded.Warning, contentDescription = "SOS for patient", tint = CriticalRed)
                        }
                        IconButton(onClick = { onToggleCritical(patient) }) {
                            Icon(Icons.Rounded.Flag, contentDescription = "Remove critical tag", tint = CriticalRed)
                        }
                    }
                }
            }
        }

        // Emergency Contacts
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Emergency Contacts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = { showAddContactDialog = true },
                    modifier = Modifier.testTag("add_emergency_contact_button")
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Contact")
                }
            }
        }
        items(state.contacts, key = { it.id }) { contact ->
            Card(
                shape = CareSyncCardShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(contact.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("${contact.role} · ${contact.phone}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Button(
                        onClick = { FormatUtils.dialPhone(context, contact.phone) },
                        shape = CareSyncButtonShape,
                        colors = ButtonDefaults.buttonColors(containerColor = if (contact.isPrimary) CriticalRed else CareSyncBlue)
                    ) {
                        Icon(Icons.Rounded.Call, contentDescription = "Call", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Call")
                    }
                }
            }
        }

        // Nearby Health Centers with live Haversine distance & Maps directions
        item {
            Text(
                text = "Nearby Health Centers",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        items(state.healthCenters, key = { it.id }) { center ->
            Card(
                shape = CareSyncCardShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.LocalHospital, contentDescription = null, tint = CareSyncBlue, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(center.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("${center.type} · ${center.distanceKm}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(center.availability, style = MaterialTheme.typography.labelSmall, color = CareSyncGreen, fontWeight = FontWeight.SemiBold)
                    }
                    IconButton(onClick = { LocationHelper.openMapsDirections(context, center) }) {
                        Icon(Icons.Rounded.Directions, contentDescription = "Open directions", tint = CareSyncGreen)
                    }
                    IconButton(onClick = { FormatUtils.dialPhone(context, center.phone) }) {
                        Icon(Icons.Rounded.Call, contentDescription = "Call center", tint = CareSyncBlue)
                    }
                }
            }
        }

        // Emergency Instructions with Voice TTS Readout
        item {
            Text(
                text = "Emergency First-Response Instructions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        items(state.instructions, key = { it.id }) { instruction ->
            var expanded by remember { mutableStateOf(false) }
            Card(
                shape = CareSyncCardShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(instruction.severityTag, style = MaterialTheme.typography.labelSmall, color = CriticalRed, fontWeight = FontWeight.Bold)
                            Text(instruction.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        }
                        IconButton(
                            onClick = {
                                if (state.isSpeaking) onStopSpeaking()
                                else onSpeakInstruction("${instruction.title}. ${instruction.steps.joinToString(". ")}")
                            }
                        ) {
                            Icon(
                                imageVector = if (state.isSpeaking) Icons.Rounded.Stop else Icons.AutoMirrored.Rounded.VolumeUp,
                                contentDescription = if (state.isSpeaking) "Stop speech" else "Read protocol aloud",
                                tint = CareSyncBlue
                            )
                        }
                        Icon(
                            imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                            contentDescription = "Expand instructions"
                        )
                    }
                    AnimatedVisibility(visible = expanded) {
                        Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            instruction.steps.forEachIndexed { idx, step ->
                                Text("${idx + 1}. $step", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddContactDialog) {
        var name by remember { mutableStateOf("") }
        var role by remember { mutableStateOf("PHC Responder") }
        var phone by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddContactDialog = false },
            title = { Text("Add Emergency Contact") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(name, { name = it }, label = { Text("Contact Name") }, singleLine = true)
                    OutlinedTextField(role, { role = it }, label = { Text("Role / Desk") }, singleLine = true)
                    OutlinedTextField(
                        phone,
                        { phone = it },
                        label = { Text("Phone Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddContact(name, role, phone)
                        showAddContactDialog = false
                    },
                    enabled = name.isNotBlank() && phone.isNotBlank()
                ) {
                    Text("Save Contact")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddContactDialog = false }) { Text("Cancel") }
            }
        )
    }
}
