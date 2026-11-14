package com.caresync.app.ui.screens.emergency

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.caresync.app.database.PatientEntity
import com.caresync.app.theme.CareSyncBlue
import com.caresync.app.theme.CareSyncButtonShape
import com.caresync.app.theme.CareSyncCardShape
import com.caresync.app.theme.CareSyncGreen
import com.caresync.app.theme.CriticalRed
import com.caresync.app.utils.FormatUtils
import com.caresync.app.viewmodel.EmergencyUiState

@Composable
fun EmergencyScreen(
    state: EmergencyUiState,
    onTriggerSos: (String?) -> Unit,
    onDismissSos: () -> Unit,
    onToggleCritical: (PatientEntity) -> Unit,
    onOpenPatient: (String) -> Unit
) {
    val context = LocalContext.current
    val pulse = rememberInfiniteTransition(label = "sos_pulse")
    val sosScale by pulse.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "sos_scale"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // SOS Hero Button
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
                        text = "CareSync Emergency Dispatch",
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

                    AnimatedVisibility(visible = state.sosTriggered) {
                        Surface(
                            shape = CareSyncButtonShape,
                            color = CriticalRed,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "SOS Dispatched${state.lastSosPatient?.let { " for $it" } ?: ""}! Alert saved to CareSync queue.",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(onClick = onDismissSos) {
                                    Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
                                }
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
            Text(
                text = "Emergency Contacts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
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

        // Nearby Health Center Placeholder
        item {
            Text(
                text = "Nearby Health Centers (Offline Directory)",
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
                    IconButton(onClick = { FormatUtils.dialPhone(context, center.phone) }) {
                        Icon(Icons.Rounded.Call, contentDescription = "Call center", tint = CareSyncBlue)
                    }
                }
            }
        }

        // Emergency Instructions
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
}
