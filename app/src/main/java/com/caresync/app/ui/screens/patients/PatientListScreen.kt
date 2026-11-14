package com.caresync.app.ui.screens.patients

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.caresync.app.components.AppLockGuard
import com.caresync.app.components.CareSyncEmptyState
import com.caresync.app.components.PatientAvatar
import com.caresync.app.components.RiskLevelBadge
import com.caresync.app.components.SyncStatusBadge
import com.caresync.app.database.PatientEntity
import com.caresync.app.theme.CareSyncBlue
import com.caresync.app.theme.CareSyncButtonShape
import com.caresync.app.theme.CareSyncCardShape
import com.caresync.app.theme.CriticalRed
import com.caresync.app.utils.FormatUtils
import com.caresync.app.utils.LocalCareSyncStrings
import com.caresync.app.utils.PdfReferralGenerator
import com.caresync.app.utils.QrUtils
import com.caresync.app.viewmodel.PatientFilter
import com.caresync.app.viewmodel.PatientsUiState
import kotlinx.coroutines.launch

@Composable
fun PatientListScreen(
    state: PatientsUiState,
    onSearchChange: (String) -> Unit,
    onFilterChange: (PatientFilter) -> Unit,
    onPatientClick: (String) -> Unit,
    onToggleCritical: (PatientEntity) -> Unit,
    onResolveQrPatientId: (String) -> String?,
    onAddPatientClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val strings = LocalCareSyncStrings.current

    var showQrDialog by remember { mutableStateOf(false) }
    var manualQrInput by remember { mutableStateOf("") }
    var qrFeedback by remember { mutableStateOf<String?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    fun handleQrImageUri(uri: Uri) {
        scope.launch {
            qrFeedback = "Decoding CareSync QR..."
            val decoded = QrUtils.decodeQrFromUri(context, uri)
            if (decoded == null) {
                qrFeedback = "No readable CareSync QR code detected in image."
                return@launch
            }
            val extractedId = QrUtils.extractPatientIdFromQr(decoded)
            val canonicalId = extractedId?.let(onResolveQrPatientId)
            if (canonicalId != null) {
                showQrDialog = false
                qrFeedback = null
                onPatientClick(canonicalId)
            } else {
                qrFeedback = "Decoded ($decoded), but no matching local patient was found."
            }
        }
    }

    val qrPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) handleQrImageUri(uri)
    }

    val qrCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = pendingCameraUri
        if (success && uri != null) {
            handleQrImageUri(uri)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPatientClick,
                containerColor = CareSyncBlue,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CareSyncButtonShape,
                modifier = Modifier.testTag("fab_add_patient")
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add patient")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = { Text("Search name, ID, ward...") },
                        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                        trailingIcon = {
                            if (state.searchQuery.isNotBlank()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(Icons.Rounded.Close, contentDescription = "Clear search")
                                }
                            }
                        },
                        singleLine = true,
                        shape = CareSyncButtonShape,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("patient_search_input")
                    )
                    FilledTonalIconButton(
                        onClick = {
                            qrFeedback = null
                            manualQrInput = ""
                            showQrDialog = true
                        },
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("patient_scan_qr_button")
                    ) {
                        Icon(Icons.Rounded.QrCodeScanner, contentDescription = strings.scanQr, tint = CareSyncBlue)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PatientFilter.entries.forEach { filter ->
                        FilterChip(
                            selected = state.activeFilter == filter,
                            onClick = { onFilterChange(filter) },
                            label = { Text(filter.label) },
                            modifier = Modifier.testTag("filter_${filter.name.lowercase()}")
                        )
                    }
                }
            }

            if (state.patients.isEmpty()) {
                CareSyncEmptyState(
                    title = "No matching patients",
                    message = "Try adjusting your search or register a new patient offline.",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp, top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.patients, key = { it.id }) { patient ->
                        PatientListCard(
                            patient = patient,
                            onClick = { onPatientClick(patient.id) },
                            onToggleCritical = { onToggleCritical(patient) }
                        )
                    }
                }
            }
        }
    }

    if (showQrDialog) {
        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            title = { Text("Scan CareSync Patient QR") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Capture a printed Patient QR Card or choose an image to jump directly to the patient's chart offline.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val uri = PdfReferralGenerator.createCameraPhotoUri(context)
                                pendingCameraUri = uri
                                AppLockGuard.suppressNextAutoLock = true
                                runCatching { qrCameraLauncher.launch(uri) }
                            },
                            shape = CareSyncButtonShape,
                            modifier = Modifier.weight(1f).testTag("qr_take_photo_button")
                        ) {
                            Icon(Icons.Rounded.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Camera")
                        }
                        OutlinedButton(
                            onClick = {
                                AppLockGuard.suppressNextAutoLock = true
                                qrPhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            shape = CareSyncButtonShape,
                            modifier = Modifier.weight(1f).testTag("qr_pick_image_button")
                        ) {
                            Icon(Icons.Rounded.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Gallery")
                        }
                    }
                    OutlinedTextField(
                        value = manualQrInput,
                        onValueChange = { manualQrInput = it; qrFeedback = null },
                        label = { Text("Or enter Patient ID (e.g. PT-1001)") },
                        singleLine = true,
                        shape = CareSyncButtonShape,
                        modifier = Modifier.fillMaxWidth()
                    )
                    qrFeedback?.let { msg ->
                        Text(msg, style = MaterialTheme.typography.bodySmall, color = CriticalRed)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val extracted = QrUtils.extractPatientIdFromQr(manualQrInput) ?: manualQrInput.trim()
                        val canonical = onResolveQrPatientId(extracted)
                        if (canonical != null) {
                            showQrDialog = false
                            onPatientClick(canonical)
                        } else {
                            qrFeedback = "No matching patient ID found locally."
                        }
                    },
                    enabled = manualQrInput.isNotBlank()
                ) {
                    Text("Open Chart")
                }
            },
            dismissButton = {
                TextButton(onClick = { showQrDialog = false }) { Text("Close") }
            }
        )
    }
}

@Composable
private fun PatientListCard(
    patient: PatientEntity,
    onClick: () -> Unit,
    onToggleCritical: () -> Unit
) {
    val categoryLabel = FormatUtils.parseCategory(patient.category).label
    Card(
        shape = CareSyncCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("patient_card_${patient.id}")
            .clip(CareSyncCardShape)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PatientAvatar(fullName = patient.fullName, isCritical = patient.isCritical)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = patient.fullName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        SyncStatusBadge(rawStatus = patient.syncStatus, compact = true)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${patient.id} · ${patient.age}y ${patient.gender} · $categoryLabel",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = patient.villageOrWard,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    RiskLevelBadge(rawRisk = patient.riskLevel, isCritical = patient.isCritical)
                    Surface(
                        onClick = onToggleCritical,
                        shape = CareSyncButtonShape,
                        color = if (patient.isCritical) CriticalRed.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Flag,
                            contentDescription = "Toggle critical status",
                            tint = if (patient.isCritical) CriticalRed else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
