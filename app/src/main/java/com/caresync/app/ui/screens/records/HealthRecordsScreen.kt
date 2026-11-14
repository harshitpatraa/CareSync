package com.caresync.app.ui.screens.records

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.caresync.app.components.CareSyncEmptyState
import com.caresync.app.database.HealthRecordEntity
import com.caresync.app.model.RecordType
import com.caresync.app.ui.screens.patients.RecordItemCard

@Composable
fun HealthRecordsScreen(
    records: List<HealthRecordEntity>,
    patientIdFilter: String? = null
) {
    var selectedType by remember { mutableStateOf<RecordType?>(null) }

    val visibleRecords = records.filter { rec ->
        val matchesPatient = patientIdFilter == null || rec.patientId == patientIdFilter
        val matchesType = selectedType == null || rec.recordType == selectedType?.name
        matchesPatient && matchesType
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                text = "All Patient Health Records",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedType == null,
                    onClick = { selectedType = null },
                    label = { Text("All (${records.size})") }
                )
                RecordType.entries.forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(type.label) }
                    )
                }
            }
        }

        if (visibleRecords.isEmpty()) {
            CareSyncEmptyState(
                title = "No health records found",
                message = "Try another category or log new records offline.",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(visibleRecords, key = { it.id }) { rec ->
                    Column {
                        Text(
                            text = rec.patientName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                        )
                        RecordItemCard(rec)
                    }
                }
            }
        }
    }
}
