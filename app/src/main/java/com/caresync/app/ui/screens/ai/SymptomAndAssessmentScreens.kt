package com.caresync.app.ui.screens.ai

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.caresync.app.components.RiskLevelBadge
import com.caresync.app.database.PatientEntity
import com.caresync.app.model.ClinicalAssessmentResult
import com.caresync.app.theme.CareSyncBlue
import com.caresync.app.theme.CareSyncButtonShape
import com.caresync.app.theme.CareSyncCardShape
import com.caresync.app.theme.CareSyncCyan
import com.caresync.app.theme.CareSyncGreen
import com.caresync.app.theme.CriticalRed
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
                    text = "CareSync AI Symptom Checker & Medicine Suggestions — offline-first & Gemini powered.",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    softWrap = true,
                    modifier = Modifier.weight(1f)
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
                    modifier = Modifier.padding(12.dp),
                    softWrap = true
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
                        label = { Text("Walk-in", softWrap = false) }
                    )
                    patients.forEach { p ->
                        FilterChip(
                            selected = state.selectedPatientId == p.id,
                            onClick = { onSelectPatient(p.id) },
                            label = { Text(p.fullName, softWrap = false) }
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
                            label = { Text(sym, softWrap = false) },
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
                            label = { Text(sev, softWrap = false) }
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
                        Text("Analyze Clinical Symptoms", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        AnimatedVisibility(visible = state.symptomResult != null) {
            state.symptomResult?.let { result ->
                AssessmentResultCard(result = result, onAskFollowUp = onOpenChat)
            }
        }
    }
}

@Composable
fun MedicalAssessmentScreen(
    state: AiToolsUiState,
    patients: List<PatientEntity> = emptyList(),
    selectedPatientId: String? = null,
    onSelectPatient: (String?) -> Unit = {},
    onRunRiskAssessment: (Int, Int, Int, Int, Float, Int, Boolean) -> Unit,
    onAnalyzeImageSample: (String, android.net.Uri?) -> Unit,
    onOpenSymptomChecker: () -> Unit
) {
    var sys by remember { mutableStateOf("120") }
    var dia by remember { mutableStateOf("80") }
    var hr by remember { mutableStateOf("76") }
    var spo2 by remember { mutableStateOf("98") }
    var temp by remember { mutableStateOf("36.8") }
    var sugar by remember { mutableStateOf("110") }
    var isPregnant by remember { mutableStateOf(false) }

    val activePatientId = state.selectedPatientId ?: selectedPatientId
    val currentPatient = patients.find { it.id == activePatientId }

    LaunchedEffect(state.prefillVitals) {
        state.prefillVitals?.let { v ->
            sys = v.systolicBp.toString()
            dia = v.diastolicBp.toString()
            hr = v.heartRate.toString()
            spo2 = v.spo2Percent.toString()
            temp = v.temperatureC.toString()
            sugar = v.bloodSugarMgDl.toString()
        }
    }

    LaunchedEffect(currentPatient) {
        currentPatient?.let { p ->
            isPregnant = p.pregnancyWeeks > 0
        }
    }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onAnalyzeImageSample("Uploaded Clinical Photo", uri)
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
                    modifier = Modifier.padding(12.dp),
                    softWrap = true
                )
            }
        }

        // Patient Linking Strip
        Card(
            shape = CareSyncCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Person, contentDescription = null, tint = CareSyncBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Select Patient for Risk Assessment", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = activePatientId == null,
                        onClick = { onSelectPatient(null) },
                        label = { Text("Walk-in Patient", softWrap = false) }
                    )
                    patients.forEach { p ->
                        FilterChip(
                            selected = activePatientId == p.id,
                            onClick = { onSelectPatient(p.id) },
                            label = { Text(p.fullName, softWrap = false) }
                        )
                    }
                }
                if (currentPatient != null) {
                    Surface(
                        shape = CareSyncButtonShape,
                        color = CareSyncBlue.copy(alpha = 0.08f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Linked: ${currentPatient.fullName} (${currentPatient.age}y, ${currentPatient.gender}, ${currentPatient.category}) · ${if (state.prefillVitals != null) "Latest vitals auto-filled" else "Enter baseline vitals below"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CareSyncBlue,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            softWrap = true
                        )
                    }
                }
            }
        }

        // Disease Risk Assessment Card with Phone-Friendly 2-Column Layout
        Card(
            shape = CareSyncCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Vitals Risk Assessment",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        softWrap = true,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(shape = CareSyncButtonShape, color = CareSyncGreen.copy(alpha = 0.14f)) {
                        Text(
                            text = "Dual Engine",
                            style = MaterialTheme.typography.labelSmall,
                            color = CareSyncGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            softWrap = false
                        )
                    }
                }

                // Row 1: Blood Pressure
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = sys,
                        onValueChange = { sys = it.filter { c -> c.isDigit() } },
                        label = { Text("Systolic (mmHg)", softWrap = true) },
                        placeholder = { Text("120") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = CareSyncButtonShape
                    )
                    OutlinedTextField(
                        value = dia,
                        onValueChange = { dia = it.filter { c -> c.isDigit() } },
                        label = { Text("Diastolic (mmHg)", softWrap = true) },
                        placeholder = { Text("80") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = CareSyncButtonShape
                    )
                }

                // Row 2: Heart Rate & Oxygen Saturation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = hr,
                        onValueChange = { hr = it.filter { c -> c.isDigit() } },
                        label = { Text("Heart Rate (bpm)", softWrap = true) },
                        placeholder = { Text("76") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = CareSyncButtonShape
                    )
                    OutlinedTextField(
                        value = spo2,
                        onValueChange = { spo2 = it.filter { c -> c.isDigit() } },
                        label = { Text("SpO2 (%)", softWrap = true) },
                        placeholder = { Text("98") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = CareSyncButtonShape
                    )
                }

                // Row 3: Temperature & Blood Sugar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = temp,
                        onValueChange = { temp = it },
                        label = { Text("Temperature (°C)", softWrap = true) },
                        placeholder = { Text("36.8") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        shape = CareSyncButtonShape
                    )
                    OutlinedTextField(
                        value = sugar,
                        onValueChange = { sugar = it.filter { c -> c.isDigit() } },
                        label = { Text("Sugar (mg/dL)", softWrap = true) },
                        placeholder = { Text("110") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = CareSyncButtonShape
                    )
                }

                // Row 4: Pregnancy / ANC Patient Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Pregnancy / ANC Patient", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, softWrap = true)
                        Text("Enables gestational hypertension & pre-eclampsia triage watch", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, softWrap = true)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(checked = isPregnant, onCheckedChange = { isPregnant = it })
                }

                Button(
                    onClick = {
                        onRunRiskAssessment(
                            sys.toIntOrNull() ?: 120,
                            dia.toIntOrNull() ?: 80,
                            hr.toIntOrNull() ?: 76,
                            spo2.toIntOrNull() ?: 98,
                            temp.toFloatOrNull() ?: 36.8f,
                            sugar.toIntOrNull() ?: 110,
                            isPregnant
                        )
                    },
                    enabled = !state.isEvaluating,
                    shape = CareSyncButtonShape,
                    colors = ButtonDefaults.buttonColors(containerColor = CareSyncBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("run_risk_assessment_button")
                ) {
                    if (state.isEvaluating) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Evaluating Vitals Risk...", softWrap = false)
                    } else {
                        Icon(Icons.Rounded.MonitorHeart, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Calculate Disease Risk", fontWeight = FontWeight.Bold, softWrap = false)
                    }
                }
            }
        }

        AnimatedVisibility(visible = state.riskResult != null) {
            state.riskResult?.let { result ->
                AssessmentResultCard(result = result, onAskFollowUp = onOpenSymptomChecker)
            }
        }

        // Multimodal Clinical Image Analysis
        Card(
            shape = CareSyncCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Clinical Image Analysis (Multimodal AI)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, softWrap = true)
                Text(
                    text = "Select a wound, skin rash, or prescription photo via Android Photo Picker for multimodal clinical assessment.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    softWrap = true
                )

                state.selectedImageUri?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Selected clinical photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(CareSyncCardShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        shape = CareSyncButtonShape,
                        modifier = Modifier.weight(1f).testTag("pick_clinical_image_button")
                    ) {
                        Icon(Icons.Rounded.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pick Photo", softWrap = false)
                    }
                    OutlinedButton(
                        onClick = { onAnalyzeImageSample("Skin Rash Sample", null) },
                        shape = CareSyncButtonShape,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Rash Scan", softWrap = false)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onAnalyzeImageSample("Wound Dressing Sample", null) },
                        shape = CareSyncButtonShape,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Wound Scan", softWrap = false)
                    }
                    OutlinedButton(
                        onClick = { onAnalyzeImageSample("Prescription OCR Sample", null) },
                        shape = CareSyncButtonShape,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Rx OCR", softWrap = false)
                    }
                }

                if (state.isAnalyzingImage) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                state.imageAnalysisResult?.let { analysis ->
                    Surface(shape = CareSyncButtonShape, color = CareSyncCyan.copy(alpha = 0.12f)) {
                        Text(
                            text = analysis,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(12.dp),
                            softWrap = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AssessmentResultCard(
    result: ClinicalAssessmentResult,
    onAskFollowUp: () -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (result.riskScore / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "risk_score_progress"
    )

    Card(
        shape = CareSyncCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = "Risk Score: ${result.riskScore}/100",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        softWrap = true
                    )
                    Text(
                        text = if (result.isOfflinePrediction) "Offline Clinical Intelligence" else "Live Cloud Gemini AI",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        softWrap = true
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                RiskLevelBadge(rawRisk = result.riskLevel.name)
            }

            LinearProgressIndicator(
                progress = { animatedProgress },
                color = if (result.riskScore >= 70) CriticalRed else if (result.riskScore >= 45) CareSyncCyan else CareSyncBlue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CareSyncButtonShape)
            )

            Text(
                text = result.primarySummary,
                style = MaterialTheme.typography.bodyMedium,
                softWrap = true
            )

            if (result.redFlags.isNotEmpty()) {
                Surface(
                    shape = CareSyncButtonShape,
                    color = CriticalRed.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Warning, contentDescription = null, tint = CriticalRed, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Urgent Red Flags",
                                style = MaterialTheme.typography.titleSmall,
                                color = CriticalRed,
                                fontWeight = FontWeight.Bold,
                                softWrap = true
                            )
                        }
                        result.redFlags.forEach { flag ->
                            Text("• $flag", style = MaterialTheme.typography.bodySmall, color = CriticalRed, softWrap = true)
                        }
                    }
                }
            }

            if (result.possibleConditions.isNotEmpty()) {
                HorizontalDivider()
                Text("Differential Considerations", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, softWrap = true)
                result.possibleConditions.forEach { cond ->
                    Text("• $cond", style = MaterialTheme.typography.bodySmall, softWrap = true)
                }
            }

            if (result.medicineSuggestions.isNotEmpty()) {
                HorizontalDivider()
                Text("Supportive Care & Medicine Protocols", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, softWrap = true)
                result.medicineSuggestions.forEach { med ->
                    Text("• $med", style = MaterialTheme.typography.bodySmall, softWrap = true)
                }
            }

            if (result.recommendedActions.isNotEmpty()) {
                HorizontalDivider()
                Text("Recommended Next Steps", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, softWrap = true)
                result.recommendedActions.forEach { act ->
                    Text("• $act", style = MaterialTheme.typography.bodySmall, softWrap = true)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            OutlinedButton(
                onClick = onAskFollowUp,
                shape = CareSyncButtonShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Discuss Assessment in AI Assistant", softWrap = true)
            }
        }
    }
}
