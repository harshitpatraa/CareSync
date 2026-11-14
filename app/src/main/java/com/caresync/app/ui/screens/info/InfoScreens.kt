package com.caresync.app.ui.screens.info

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.caresync.app.R
import com.caresync.app.components.CareSyncLogo
import com.caresync.app.theme.CareSyncCardShape

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(6.dp))
        CareSyncLogo(size = 84.dp, animateNodes = true)
        Text("CareSync", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            "Version 1.0.0 · Offline-First Healthcare Synchronization",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Image(
            painter = painterResource(id = R.drawable.img_caresync_hero),
            contentDescription = "CareSync synchronized healthcare",
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(CareSyncCardShape),
            contentScale = ContentScale.Crop
        )

        Card(
            shape = CareSyncCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Mission & Architecture", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "CareSync empowers community health workers, field clinics, and mobile triage teams to register patients, monitor maternal and child health, run offline clinical risk checks, and dispatch emergency alerts—even with zero internet connectivity.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "Built with Jetpack Compose, Material Design 3, Room offline database, Hilt DI, and a resilient pending sync queue.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun HelpScreen() {
    val faqs = listOf(
        "How does Offline Sync work?" to "Every patient registration, vitals update, and clinical note is saved immediately to the local Room database and tagged Pending Sync. Tap Sync Now on the Home Dashboard or Settings when internet is available.",
        "How do I flag a Critical Patient?" to "Open the Patients tab and tap the Flag icon on any patient card, or open a patient's profile and tap Critical. Critical patients appear automatically in the Emergency SOS screen.",
        "Are AI Symptom Checker & Risk Scores available offline?" to "Yes. CareSync includes an offline clinical decision-support engine that evaluates symptoms, gestation weeks, and vitals locally without requiring network connectivity.",
        "How do I use Voice Input in AI Chat?" to "Tap the microphone button beside the chat box to launch speech recognition, or type directly to query clinical protocols."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Help & Field Protocols", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        faqs.forEach { (q, a) ->
            var open by remember { mutableStateOf(false) }
            Card(
                shape = CareSyncCardShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().clickable { open = !open }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(q, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Icon(if (open) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore, contentDescription = null)
                    }
                    AnimatedVisibility(visible = open) {
                        Text(
                            text = a,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
