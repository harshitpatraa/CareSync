package com.caresync.app.ui.screens.patients

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.FamilyRestroom
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.PregnantWoman
import androidx.compose.material.icons.rounded.Vaccines
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.caresync.app.components.CareSyncEmptyState
import com.caresync.app.components.PatientAvatar
import com.caresync.app.components.RiskLevelBadge
import com.caresync.app.components.SyncStatusBadge
import com.caresync.app.database.HealthRecordEntity
import com.caresync.app.database.VitalsEntity
import com.caresync.app.model.RecordType
import com.caresync.app.theme.CareSyncBlue
import com.caresync.app.theme.CareSyncButtonShape
import com.caresync.app.theme.CareSyncCardShape
import com.caresync.app.theme.CareSyncCyan
import com.caresync.app.theme.CareSyncGreen
import com.caresync.app.theme.CriticalRed
import com.caresync.app.utils.FormatUtils
import com.caresync.app.viewmodel.PatientDetailUiState

@Composable
fun PatientDetailScreen(
    state: PatientDetailUiState,
    onToggleCritical: () -> Unit,
    onLogVitals: (Int, Int, Int, Float, Int, Int) -> Unit,
    onAddRecord: (RecordType, String, String, String, String) -> Unit,
    onOpenAssessment: (String) -> Unit
) {
    val patient = state.patient
    if (patient == null) {
        CareSyncEmptyState(
            title = "Patient record loading",
            message = "Reading local CareSync Room storage...",
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var showVitalsDialog by remember { mutableStateOf(false) }
    var showAddRecordDialog by remember { mutableStateOf(false) }

    val tabs = listOf("Vitals & Overview", "Timeline & History", "Meds & Vaccines", "Family & Docs")

    Column(modifier = Modifier.fillMaxSize()) {
        // Patient Identity Header Card
        Card(
            shape = CareSyncCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PatientAvatar(fullName = patient.fullName, isCritical = patient.isCritical, size = 56.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = patient.fullName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${patient.id} · ${patient.age}y ${patient.gender} · Blood ${patient.bloodGroup}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = patient.villageOrWard,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SyncStatusBadge(rawStatus = patient.syncStatus)
                    RiskLevelBadge(rawRisk = patient.riskLevel, isCritical = patient.isCritical)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { FormatUtils.dialPhone(context, patient.phone) },
                        shape = CareSyncButtonShape,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Call")
                    }
                    OutlinedButton(
                        onClick = onToggleCritical,
                        shape = CareSyncButtonShape,
                        modifier = Modifier.weight(1f).testTag("detail_toggle_critical")
                    ) {
                        Icon(
                            Icons.Rounded.Flag,
                            contentDescription = null,
                            tint = if (patient.isCritical) CriticalRed else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (patient.isCritical) "Unflag" else "Critical")
                    }
                    Button(
                        onClick = { onOpenAssessment(patient.id) },
                        shape = CareSyncButtonShape,
                        colors = ButtonDefaults.buttonColors(containerColor = CareSyncBlue),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.MonitorHeart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Risk AI")
                    }
                }
            }
        }

        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.background
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = FontWeight.SemiBold) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Latest Vitals", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Button(
                                onClick = { showVitalsDialog = true },
                                shape = CareSyncButtonShape,
                                modifier = Modifier.testTag("log_vitals_button")
                            ) {
                                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Log Vitals")
                            }
                        }
                    }
                    if (state.vitalsHistory.isEmpty()) {
                        item {
                            CareSyncEmptyState("No vitals recorded yet", "Tap Log Vitals to capture offline readings.")
                        }
                    } else {
                        items(state.vitalsHistory, key = { it.id }) { v ->
                            VitalsCard(v)
                        }
                    }
                    item {
                        Card(
                            shape = CareSyncCardShape,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Clinical Summary", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("Allergies: ${patient.allergies}", style = MaterialTheme.typography.bodySmall)
                                Text("Chronic Conditions: ${patient.chronicConditions}", style = MaterialTheme.typography.bodySmall)
                                Text("Next Follow-up: ${patient.nextFollowUpDate}", style = MaterialTheme.typography.bodySmall)
                                Text("Notes: ${patient.notes}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
                1 -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Patient Timeline & Medical History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            OutlinedButton(onClick = { showAddRecordDialog = true }, shape = CareSyncButtonShape) {
                                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Note")
                            }
                        }
                    }
                    val timelineItems = state.records.filter {
                        it.recordType == RecordType.TIMELINE.name || it.recordType == RecordType.HISTORY.name
                    }
                    if (timelineItems.isEmpty()) {
                        item { CareSyncEmptyState("No timeline events", "Add clinical observations offline.") }
                    } else {
                        items(timelineItems, key = { it.id }) { rec -> RecordItemCard(rec) }
                    }
                }
                2 -> {
                    item {
                        Card(
                            shape = CareSyncCardShape,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Vaccines, contentDescription = null, tint = CareSyncGreen)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Vaccination Progress: ${patient.vaccinationProgress}%", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { patient.vaccinationProgress / 100f },
                                    color = CareSyncGreen,
                                    modifier = Modifier.fillMaxWidth().height(8.dp)
                                )
                            }
                        }
                    }
                    val medAndVax = state.records.filter {
                        it.recordType == RecordType.MEDICINE.name || it.recordType == RecordType.VACCINATION.name
                    }
                    if (medAndVax.isEmpty()) {
                        item { CareSyncEmptyState("No medicines or vaccines", "Records saved offline appear here.") }
                    } else {
                        items(medAndVax, key = { it.id }) { rec -> RecordItemCard(rec) }
                    }
                }
                3 -> {
                    if (patient.pregnancyWeeks > 0) {
                        item {
                            Card(
                                shape = CareSyncCardShape,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.PregnantWoman, contentDescription = null, tint = CareSyncBlue)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Pregnancy Details", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    }
                                    Text("Gestation: ${patient.pregnancyWeeks} weeks", style = MaterialTheme.typography.bodyMedium)
                                    Text("Expected Due Date: ${patient.expectedDueDate}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                    item {
                        Card(
                            shape = CareSyncCardShape,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.FamilyRestroom, contentDescription = null, tint = CareSyncCyan)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Family Details", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                }
                                Text("Head of Household: ${patient.familyHeadName}", style = MaterialTheme.typography.bodyMedium)
                                Text("Household Members: ${patient.familyMembersCount}", style = MaterialTheme.typography.bodySmall)
                                Text("Contact Phone: ${patient.phone}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    val docs = state.records.filter { it.recordType == RecordType.DOCUMENT.name }
                    if (docs.isEmpty()) {
                        item { CareSyncEmptyState("No uploaded documents", "Offline referral slips and lab reports appear here.") }
                    } else {
                        items(docs, key = { it.id }) { doc -> RecordItemCard(doc) }
                    }
                }
            }
        }
    }

    if (showVitalsDialog) {
        LogVitalsDialog(
            onDismiss = { showVitalsDialog = false },
            onSave = { sys, dia, hr, temp, spo2, sugar ->
                onLogVitals(sys, dia, hr, temp, spo2, sugar)
                showVitalsDialog = false
            }
        )
    }

    if (showAddRecordDialog) {
        AddRecordDialog(
            onDismiss = { showAddRecordDialog = false },
            onSave = { title, sub, detail ->
                onAddRecord(RecordType.TIMELINE, title, sub, detail, "Saved Offline")
                showAddRecordDialog = false
            }
        )
    }
}

@Composable
private fun VitalsCard(vitals: VitalsEntity) {
    Card(
        shape = CareSyncCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(vitals.recordedAtLabel, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                SyncStatusBadge(rawStatus = vitals.syncStatus, compact = true)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                VitalMetric("BP", "${vitals.systolicBp}/${vitals.diastolicBp}")
                VitalMetric("SpO2", "${vitals.spo2Percent}%")
                VitalMetric("HR", "${vitals.heartRate} bpm")
                VitalMetric("Temp", "${vitals.temperatureC}°C")
                VitalMetric("Sugar", "${vitals.bloodSugarMgDl}")
            }
        }
    }
}

@Composable
private fun VitalMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun RecordItemCard(record: HealthRecordEntity) {
    val icon = when (record.recordType) {
        RecordType.MEDICINE.name -> Icons.Rounded.Medication
        RecordType.VACCINATION.name -> Icons.Rounded.Vaccines
        RecordType.DOCUMENT.name -> Icons.Rounded.Description
        else -> Icons.Rounded.MonitorHeart
    }
    Card(
        shape = CareSyncCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(icon, contentDescription = null, tint = CareSyncBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(record.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                SyncStatusBadge(rawStatus = record.syncStatus, compact = true)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(record.subtitle, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            Text(record.detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CareSyncButtonShape, color = MaterialTheme.colorScheme.primaryContainer) {
                    Text(
                        text = record.statusBadge,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Text(record.dateLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun LogVitalsDialog(
    onDismiss: () -> Unit,
    onSave: (Int, Int, Int, Float, Int, Int) -> Unit
) {
    var sys by remember { mutableStateOf("122") }
    var dia by remember { mutableStateOf("80") }
    var hr by remember { mutableStateOf("76") }
    var temp by remember { mutableStateOf("36.9") }
    var spo2 by remember { mutableStateOf("98") }
    var sugar by remember { mutableStateOf("105") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Vitals Offline") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(sys, { sys = it.filter { c -> c.isDigit() } }, label = { Text("Sys BP") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    OutlinedTextField(dia, { dia = it.filter { c -> c.isDigit() } }, label = { Text("Dia BP") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(spo2, { spo2 = it.filter { c -> c.isDigit() } }, label = { Text("SpO2 %") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    OutlinedTextField(hr, { hr = it.filter { c -> c.isDigit() } }, label = { Text("HR bpm") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(temp, { temp = it }, label = { Text("Temp °C") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                    OutlinedTextField(sugar, { sugar = it.filter { c -> c.isDigit() } }, label = { Text("Sugar mg/dL") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    sys.toIntOrNull() ?: 120,
                    dia.toIntOrNull() ?: 80,
                    hr.toIntOrNull() ?: 76,
                    temp.toFloatOrNull() ?: 36.8f,
                    spo2.toIntOrNull() ?: 98,
                    sugar.toIntOrNull() ?: 105
                )
            }) { Text("Save Offline") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun AddRecordDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var sub by remember { mutableStateOf("") }
    var detail by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Clinical Note") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(title, { title = it }, label = { Text("Title (e.g. Follow-up Check)") })
                OutlinedTextField(sub, { sub = it }, label = { Text("Summary") })
                OutlinedTextField(detail, { detail = it }, label = { Text("Prescription / Action") })
            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.isNotBlank()) onSave(title, sub.ifBlank { "Clinical observation" }, detail.ifBlank { "Logged offline" })
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
