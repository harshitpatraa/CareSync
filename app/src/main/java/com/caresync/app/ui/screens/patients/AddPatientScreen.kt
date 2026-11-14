package com.caresync.app.ui.screens.patients

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.caresync.app.model.PatientCategory
import com.caresync.app.theme.CareSyncBlue
import com.caresync.app.theme.CareSyncButtonShape
import com.caresync.app.theme.CareSyncCardShape
import com.caresync.app.theme.CareSyncGreen

@Composable
fun AddPatientScreen(
    onSavePatientOffline: (
        fullName: String,
        age: Int,
        gender: String,
        phone: String,
        villageOrWard: String,
        bloodGroup: String,
        category: PatientCategory,
        isCritical: Boolean,
        allergies: String,
        chronicConditions: String,
        familyHeadName: String,
        familyMembersCount: Int,
        pregnancyWeeks: Int,
        expectedDueDate: String,
        childWeightKg: Float,
        notes: String,
        systolicBp: Int,
        diastolicBp: Int,
        heartRate: Int,
        spo2: Int,
        tempC: Float
    ) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var ageText by remember { mutableStateOf("28") }
    var gender by remember { mutableStateOf("Female") }
    var phone by remember { mutableStateOf("") }
    var ward by remember { mutableStateOf("Ward 4 · Rampur East") }
    var bloodGroup by remember { mutableStateOf("O+") }
    var category by remember { mutableStateOf(PatientCategory.GENERAL) }
    var isCritical by remember { mutableStateOf(false) }

    var allergies by remember { mutableStateOf("None") }
    var chronic by remember { mutableStateOf("None") }
    var familyHead by remember { mutableStateOf("") }
    var familyCount by remember { mutableStateOf("4") }
    var pregnancyWeeks by remember { mutableStateOf("24") }
    var dueDate by remember { mutableStateOf("10 Mar 2027") }
    var childWeight by remember { mutableStateOf("12.0") }
    var notes by remember { mutableStateOf("") }

    var systolic by remember { mutableStateOf("120") }
    var diastolic by remember { mutableStateOf("80") }
    var heartRate by remember { mutableStateOf("78") }
    var spo2 by remember { mutableStateOf("98") }
    var tempC by remember { mutableStateOf("36.8") }
    var formError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            shape = CareSyncButtonShape,
            color = CareSyncGreen.copy(alpha = 0.12f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Rounded.CloudOff, contentDescription = null, tint = CareSyncGreen)
                Text(
                    text = "Offline-First Registration: Saves immediately to local Room storage and queues for CareSync upload.",
                    style = MaterialTheme.typography.bodySmall,
                    color = CareSyncGreen,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Card(
            shape = CareSyncCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Demographics & Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it; formError = null },
                    label = { Text("Patient Full Name *") },
                    modifier = Modifier.fillMaxWidth().testTag("add_patient_name"),
                    shape = CareSyncButtonShape
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = ageText,
                        onValueChange = { ageText = it.filter { c -> c.isDigit() } },
                        label = { Text("Age") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = CareSyncButtonShape
                    )
                    OutlinedTextField(
                        value = bloodGroup,
                        onValueChange = { bloodGroup = it },
                        label = { Text("Blood Group") },
                        modifier = Modifier.weight(1f),
                        shape = CareSyncButtonShape
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Female", "Male", "Other").forEach { g ->
                        FilterChip(
                            selected = gender == g,
                            onClick = { gender = g },
                            label = { Text(g) }
                        )
                    }
                }
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    shape = CareSyncButtonShape
                )
                OutlinedTextField(
                    value = ward,
                    onValueChange = { ward = it },
                    label = { Text("Village / Ward") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = CareSyncButtonShape
                )

                Text("Care Program Category", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PatientCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.label) }
                        )
                    }
                }

                if (category == PatientCategory.PREGNANCY) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = pregnancyWeeks,
                            onValueChange = { pregnancyWeeks = it.filter { c -> c.isDigit() } },
                            label = { Text("Gestation (Weeks)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = CareSyncButtonShape
                        )
                        OutlinedTextField(
                            value = dueDate,
                            onValueChange = { dueDate = it },
                            label = { Text("Expected Due Date") },
                            modifier = Modifier.weight(1f),
                            shape = CareSyncButtonShape
                        )
                    }
                }
                if (category == PatientCategory.CHILD) {
                    OutlinedTextField(
                        value = childWeight,
                        onValueChange = { childWeight = it },
                        label = { Text("Child Weight (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = CareSyncButtonShape
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Tag as Critical Priority", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text("Flags patient in Emergency SOS list", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = isCritical, onCheckedChange = { isCritical = it })
                }
            }
        }

        Card(
            shape = CareSyncCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Baseline Vitals", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = systolic,
                        onValueChange = { systolic = it.filter { c -> c.isDigit() } },
                        label = { Text("Systolic BP") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = CareSyncButtonShape
                    )
                    OutlinedTextField(
                        value = diastolic,
                        onValueChange = { diastolic = it.filter { c -> c.isDigit() } },
                        label = { Text("Diastolic BP") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = CareSyncButtonShape
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = spo2,
                        onValueChange = { spo2 = it.filter { c -> c.isDigit() } },
                        label = { Text("SpO2 %") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = CareSyncButtonShape
                    )
                    OutlinedTextField(
                        value = heartRate,
                        onValueChange = { heartRate = it.filter { c -> c.isDigit() } },
                        label = { Text("Heart Rate") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = CareSyncButtonShape
                    )
                    OutlinedTextField(
                        value = tempC,
                        onValueChange = { tempC = it },
                        label = { Text("Temp °C") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        shape = CareSyncButtonShape
                    )
                }
            }
        }

        Card(
            shape = CareSyncCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Medical History & Family Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = allergies,
                    onValueChange = { allergies = it },
                    label = { Text("Known Allergies") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = CareSyncButtonShape
                )
                OutlinedTextField(
                    value = chronic,
                    onValueChange = { chronic = it },
                    label = { Text("Chronic Conditions") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = CareSyncButtonShape
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = familyHead,
                        onValueChange = { familyHead = it },
                        label = { Text("Family Head Name") },
                        modifier = Modifier.weight(2f),
                        shape = CareSyncButtonShape
                    )
                    OutlinedTextField(
                        value = familyCount,
                        onValueChange = { familyCount = it.filter { c -> c.isDigit() } },
                        label = { Text("Members") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = CareSyncButtonShape
                    )
                }
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Clinical Notes") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = CareSyncButtonShape
                )
            }
        }

        if (formError != null) {
            Text(formError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Button(
            onClick = {
                if (fullName.isBlank()) {
                    formError = "Please enter the patient's full name."
                } else {
                    onSavePatientOffline(
                        fullName,
                        ageText.toIntOrNull() ?: 28,
                        gender,
                        phone,
                        ward,
                        bloodGroup,
                        category,
                        isCritical,
                        allergies,
                        chronic,
                        familyHead,
                        familyCount.toIntOrNull() ?: 3,
                        if (category == PatientCategory.PREGNANCY) pregnancyWeeks.toIntOrNull() ?: 20 else 0,
                        if (category == PatientCategory.PREGNANCY) dueDate else "",
                        if (category == PatientCategory.CHILD) childWeight.toFloatOrNull() ?: 10f else 0f,
                        notes,
                        systolic.toIntOrNull() ?: 120,
                        diastolic.toIntOrNull() ?: 80,
                        heartRate.toIntOrNull() ?: 78,
                        spo2.toIntOrNull() ?: 98,
                        tempC.toFloatOrNull() ?: 36.8f
                    )
                }
            },
            shape = CareSyncButtonShape,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("save_patient_offline_button"),
            colors = ButtonDefaults.buttonColors(containerColor = CareSyncBlue)
        ) {
            Icon(Icons.Rounded.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Patient Offline", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
