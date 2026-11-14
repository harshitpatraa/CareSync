package com.caresync.app.ui.screens.ai

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.caresync.app.components.AppLockGuard
import com.caresync.app.components.RiskLevelBadge
import com.caresync.app.database.PatientEntity
import com.caresync.app.model.ClinicalAssessmentResult
import com.caresync.app.theme.CareSyncBlue
import com.caresync.app.theme.CareSyncButtonShape
import com.caresync.app.theme.CareSyncCardShape
import com.caresync.app.theme.CareSyncCyan
import com.caresync.app.theme.CareSyncGreen
import com.caresync.app.theme.CriticalRed
import com.caresync.app.utils.PdfReferralGenerator
import com.caresync.app.viewmodel.AiToolsUiState

private val commonSymptoms = listOf(
    "Fever", "Cough", "Shortness of breath", "Chest pain",
    "Headache", "Dizziness", "Vomiting", "Diarrhea",
    "Skin rash", "Sore throat", "Abdominal pain", "Swelling"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SymptomCheckerScreen(
    state: AiToolsUiState,
    patients: List<PatientEntity>,
    onToggleSymptom: (String) -> Unit,
    onDurationChange: (Int) -> Unit,
    onSeverityChange: (String) -> Unit,
    onSelectPatient: (String?) -> Unit,
    onAnalyze: () -> Unit,
    onSpeakText: (String) -> Unit,
    onStopSpeaking: () -> Unit,
    onOpenChat: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            shape = CareSyncButtonShape,
            color = CareSyncCyan.copy(alpha = 0.14f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = CareSyncBlue)
                Text(
                    text = "CareSync AI Symptom Checker & Medicine Suggestions — powered by Gemini.",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        state.aiErrorMessage?.let { err ->
            Surface(
                shape = CareSyncButtonShape,
                color = CriticalRed.copy(alpha = 0.12f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = err,
                    color = CriticalRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Card(
            shape = CareSyncCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Link Patient (Optional)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = state.selectedPatientId == null,
                        onClick = { onSelectPatient(null) },
                        label = { Text("Walk-in") }
                    )
                    patients.take(6).forEach { p ->
                        FilterChip(
                            selected = state.selectedPatientId == p.id,
                            onClick = { onSelectPatient(p.id) },
                            label = { Text(p.fullName) }
                        )
                    }
                }

                Text("Select Symptoms", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    commonSymptoms.forEach { sym ->
                        FilterChip(
                            selected = sym in state.selectedSymptoms,
                            onClick = { onToggleSymptom(sym) },
                            label = { Text(sym) },
                            modifier = Modifier.testTag("symptom_${sym.lowercase().replace(" ", "_")}")
                        )
                    }
                }

                Text("Duration: ${state.durationDays} day(s)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Slider(
                    value = state.durationDays.toFloat(),
                    onValueChange = { onDurationChange(it.toInt()) },
                    valueRange = 1f..14f
                )

                Text("Severity", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Mild", "Moderate", "Severe").forEach { sev ->
                        FilterChip(
                            selected = state.severity == sev,
                            onClick = { onSeverityChange(sev) },
                            label = { Text(sev) }
                        )
                    }
                }

                Button(
                    onClick = onAnalyze,
                    enabled = state.selectedSymptoms.isNotEmpty() && !state.isEvaluating,
                    shape = CareSyncButtonShape,
                    colors = ButtonDefaults.buttonColors(containerColor = CareSyncBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("run_symptom_checker_button")
                ) {
                    if (state.isEvaluating) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Rounded.MedicalServices, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyze with Gemini AI", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        AnimatedVisibility(visible = state.symptomResult != null) {
            state.symptomResult?.let { result ->
                AssessmentResultCard(
                    result = result,
                    isSpeaking = state.isSpeaking,
                    onSpeak = {
                        onSpeakText("Risk score ${result.riskScore} out of 100. ${result.primarySummary}. Recommended actions: ${result.recommendedActions.joinToString(". ")}")
                    },
                    onStopSpeaking = onStopSpeaking,
                    onAskFollowUp = onOpenChat
                )
            }
        }
    }
}

@Composable
fun MedicalAssessmentScreen(
    state: AiToolsUiState,
    patients: List<PatientEntity>,
    onSelectPatient: (String?) -> Unit,
    onRunRiskAssessment: (Int, Int, Int, Int, Float, Int, Boolean) -> Unit,
    onAnalyzeImageSample: (String, Uri?) -> Unit,
    onAnalyzePrescription: (String, Uri?) -> Unit,
    onSaveRxToPatient: (String) -> Unit,
    onSpeakText: (String) -> Unit,
    onStopSpeaking: () -> Unit,
    onOpenSymptomChecker: () -> Unit
) {
    val context = LocalContext.current
    var sys by remember { mutableStateOf(state.prefillSystolic) }
    var dia by remember { mutableStateOf(state.prefillDiastolic) }
    var hr by remember { mutableStateOf(state.prefillHeartRate) }
    var spo2 by remember { mutableStateOf(state.prefillSpo2) }
    var temp by remember { mutableStateOf(state.prefillTempC) }
    var sugar by remember { mutableStateOf(state.prefillBloodSugar) }
    var isPregnant by remember { mutableStateOf(state.prefillIsPregnant) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(state.prefillVersion) {
        if (state.prefillVersion > 0) {
            sys = state.prefillSystolic
            dia = state.prefillDiastolic
            hr = state.prefillHeartRate
            spo2 = state.prefillSpo2
            temp = state.prefillTempC
            sugar = state.prefillBloodSugar
            isPregnant = state.prefillIsPregnant
        }
    }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onAnalyzeImageSample("Uploaded Clinical Photo", uri)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = pendingCameraUri
        if (success && uri != null) {
            onAnalyzeImageSample("Captured Clinical Photo", uri)
        }
    }

    val rxPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onAnalyzePrescription("Uploaded Prescription", uri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        state.aiErrorMessage?.let { err ->
            Surface(
                shape = CareSyncButtonShape,
                color = CriticalRed.copy(alpha = 0.12f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = err,
                    color = CriticalRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Disease Risk Assessment powered by Gemini AI Logic
        Card(
            shape = CareSyncCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Disease Risk Assessment", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Surface(shape = CareSyncButtonShape, color = CareSyncGreen.copy(alpha = 0.14f)) {
                        Text(
                            text = if (state.isOfflineOnlyMode) "Offline Clinical" else "Gemini 3.8 Flash",
                            style = MaterialTheme.typography.labelSmall,
                            color = CareSyncGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                if (patients.isNotEmpty()) {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = state.selectedPatientId == null,
                            onClick = { onSelectPatient(null) },
                            label = { Text("Walk-in") }
                        )
                        patients.take(6).forEach { p ->
                            FilterChip(
                                selected = state.selectedPatientId == p.id,
                                onClick = { onSelectPatient(p.id) },
                                label = { Text(p.fullName) }
                            )
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(sys, { sys = it.filter { c -> c.isDigit() } }, label = { Text("Systolic") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = CareSyncButtonShape)
                    OutlinedTextField(dia, { dia = it.filter { c -> c.isDigit() } }, label = { Text("Diastolic") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = CareSyncButtonShape)
                    OutlinedTextField(spo2, { spo2 = it.filter { c -> c.isDigit() } }, label = { Text("SpO2 %") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = CareSyncButtonShape)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(hr, { hr = it.filter { c -> c.isDigit() } }, label = { Text("HR bpm") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = CareSyncButtonShape)
                    OutlinedTextField(temp, { temp = it }, label = { Text("Temp °C") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f), shape = CareSyncButtonShape)
                    OutlinedTextField(sugar, { sugar = it.filter { c -> c.isDigit() } }, label = { Text("Sugar") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = CareSyncButtonShape)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pregnancy / ANC Patient", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = isPregnant, onCheckedChange = { isPregnant = it })
                }
                Button(
                    onClick = {
                        onRunRiskAssessment(
                            sys.toIntOrNull() ?: 120,
                            dia.toIntOrNull() ?: 80,
                            hr.toIntOrNull() ?: 80,
                            spo2.toIntOrNull() ?: 98,
                            temp.toFloatOrNull() ?: 36.8f,
                            sugar.toIntOrNull() ?: 110,
                            isPregnant
                        )
                    },
                    enabled = !state.isEvaluating,
                    shape = CareSyncButtonShape,
                    colors = ButtonDefaults.buttonColors(containerColor = CareSyncBlue),
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("run_risk_assessment_button")
                ) {
                    if (state.isEvaluating) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Rounded.MonitorHeart, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Calculate Disease Risk", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        state.riskResult?.let { result ->
            AssessmentResultCard(
                result = result,
                isSpeaking = state.isSpeaking,
                onSpeak = {
                    onSpeakText("Disease risk score ${result.riskScore} out of 100. ${result.primarySummary}. Recommended actions: ${result.recommendedActions.joinToString(". ")}")
                },
                onStopSpeaking = onStopSpeaking,
                onAskFollowUp = onOpenSymptomChecker
            )
        }

        // Multimodal Clinical Image & Prescription OCR Analysis powered by Gemini Vision
        Card(
            shape = CareSyncCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Clinical Vision & Prescription OCR", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = "Capture or pick a wound, skin rash, or handwritten prescription photo for structured Gemini Vision extraction.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                state.selectedImageUri?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Selected clinical image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(CareSyncCardShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val uri = PdfReferralGenerator.createCameraPhotoUri(context)
                            pendingCameraUri = uri
                            AppLockGuard.suppressNextAutoLock = true
                            runCatching { cameraLauncher.launch(uri) }
                        },
                        shape = CareSyncButtonShape,
                        modifier = Modifier.weight(1f).testTag("camera_clinical_image_button")
                    ) {
                        Icon(Icons.Rounded.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Camera")
                    }
                    OutlinedButton(
                        onClick = {
                            AppLockGuard.suppressNextAutoLock = true
                            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        shape = CareSyncButtonShape,
                        modifier = Modifier.weight(1f).testTag("pick_clinical_image_button")
                    ) {
                        Icon(Icons.Rounded.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pick Photo")
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            AppLockGuard.suppressNextAutoLock = true
                            rxPhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        shape = CareSyncButtonShape,
                        colors = ButtonDefaults.buttonColors(containerColor = CareSyncGreen),
                        modifier = Modifier.weight(1f).testTag("scan_rx_photo_button")
                    ) {
                        Icon(Icons.Rounded.DocumentScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Scan Rx Photo")
                    }
                    OutlinedButton(
                        onClick = { onAnalyzePrescription("Prescription OCR Sample", null) },
                        shape = CareSyncButtonShape,
                        modifier = Modifier.weight(1f).testTag("rx_ocr_sample_button")
                    ) {
                        Text("Sample Rx OCR")
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onAnalyzeImageSample("Skin Rash Sample", null) },
                        shape = CareSyncButtonShape,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Test Rash Scan")
                    }
                    OutlinedButton(
                        onClick = { onAnalyzeImageSample("Wound Dressing Sample", null) },
                        shape = CareSyncButtonShape,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Wound Scan")
                    }
                }

                if (state.isAnalyzingImage) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                state.imageAnalysisResult?.let { analysis ->
                    Surface(shape = CareSyncButtonShape, color = CareSyncCyan.copy(alpha = 0.12f)) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Clinical Image Findings", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = CareSyncBlue)
                                IconButton(onClick = { if (state.isSpeaking) onStopSpeaking() else onSpeakText(analysis) }) {
                                    Icon(
                                        imageVector = if (state.isSpeaking) Icons.Rounded.Stop else Icons.AutoMirrored.Rounded.VolumeUp,
                                        contentDescription = "Speak image findings",
                                        tint = CareSyncBlue
                                    )
                                }
                            }
                            Text(text = analysis, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                state.rxAnalysisResult?.let { rx ->
                    Surface(
                        shape = CareSyncButtonShape,
                        color = CareSyncGreen.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Medication, contentDescription = null, tint = CareSyncGreen)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Extracted Prescription (${rx.medications.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                }
                                IconButton(
                                    onClick = {
                                        if (state.isSpeaking) onStopSpeaking()
                                        else onSpeakText("${rx.summary}. Medications: ${rx.medications.joinToString(". ") { "${it.name} ${it.dosage} ${it.frequency} for ${it.duration}" }}")
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (state.isSpeaking) Icons.Rounded.Stop else Icons.AutoMirrored.Rounded.VolumeUp,
                                        contentDescription = "Speak prescription",
                                        tint = CareSyncGreen
                                    )
                                }
                            }
                            Text(rx.summary, style = MaterialTheme.typography.bodySmall)
                            rx.medications.forEach { med ->
                                Text(
                                    text = "• ${med.name} (${med.dosage}) — ${med.frequency} · ${med.duration}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            if (rx.warnings.isNotEmpty()) {
                                rx.warnings.forEach { w ->
                                    Text("⚠ $w", style = MaterialTheme.typography.labelSmall, color = CriticalRed)
                                }
                            }
                            val targetPatientId = state.selectedPatientId ?: patients.firstOrNull()?.id
                            if (targetPatientId != null && rx.medications.isNotEmpty()) {
                                val targetName = patients.firstOrNull { it.id == targetPatientId }?.fullName ?: "Patient"
                                Button(
                                    onClick = { onSaveRxToPatient(targetPatientId) },
                                    shape = CareSyncButtonShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = CareSyncBlue),
                                    modifier = Modifier.fillMaxWidth().testTag("save_rx_to_patient_button")
                                ) {
                                    Text("Save Meds & Reminders to $targetName", fontWeight = FontWeight.Bold)
                                }
                            }
                            state.rxSavedBanner?.let { banner ->
                                Text(banner, style = MaterialTheme.typography.labelMedium, color = CareSyncGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AssessmentResultCard(
    result: ClinicalAssessmentResult,
    isSpeaking: Boolean,
    onSpeak: () -> Unit,
    onStopSpeaking: () -> Unit,
    onAskFollowUp: () -> Unit
) {
    Card(
        shape = CareSyncCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Risk Score: ${result.riskScore}/100", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (result.isOfflinePrediction) "Offline Clinical Mode" else "Gemini 3.8 Flash Clinical AI",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { if (isSpeaking) onStopSpeaking() else onSpeak() }) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.Rounded.Stop else Icons.AutoMirrored.Rounded.VolumeUp,
                        contentDescription = if (isSpeaking) "Stop audio" else "Read assessment aloud",
                        tint = CareSyncBlue
                    )
                }
                RiskLevelBadge(rawRisk = result.riskLevel.name)
            }

            LinearProgressIndicator(
                progress = { result.riskScore / 100f },
                color = if (result.riskScore >= 70) CriticalRed else CareSyncBlue,
                modifier = Modifier.fillMaxWidth().height(8.dp)
            )

            Text(result.primarySummary, style = MaterialTheme.typography.bodySmall)

            if (result.redFlags.isNotEmpty()) {
                Surface(shape = CareSyncButtonShape, color = CriticalRed.copy(alpha = 0.12f)) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Warning, contentDescription = null, tint = CriticalRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Red Flag Alerts", style = MaterialTheme.typography.labelLarge, color = CriticalRed, fontWeight = FontWeight.Bold)
                        }
                        result.redFlags.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall, color = CriticalRed) }
                    }
                }
            }

            Text("Differential Considerations", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            result.possibleConditions.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }

            Text("Medicine & Supportive Suggestions", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            result.medicineSuggestions.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }

            Text("Recommended Actions", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            result.recommendedActions.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }

            OutlinedButton(onClick = onAskFollowUp, shape = CareSyncButtonShape, modifier = Modifier.fillMaxWidth()) {
                Text("Discuss with CareSync AI Assistant")
            }
        }
    }
}
