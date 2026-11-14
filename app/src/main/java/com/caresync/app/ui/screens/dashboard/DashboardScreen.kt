package com.caresync.app.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.Emergency
import androidx.compose.material.icons.rounded.EventRepeat
import androidx.compose.material.icons.rounded.FolderShared
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.PendingActions
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.PregnantWoman
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material.icons.rounded.Vaccines
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.caresync.app.R
import com.caresync.app.components.DashboardStatCard
import com.caresync.app.components.HealthAnalyticsCard
import com.caresync.app.components.QuickActionTile
import com.caresync.app.components.RiskLevelBadge
import com.caresync.app.theme.CareSyncBlue
import com.caresync.app.theme.CareSyncButtonShape
import com.caresync.app.theme.CareSyncCardShape
import com.caresync.app.theme.CareSyncCyan
import com.caresync.app.theme.CareSyncGreen
import com.caresync.app.theme.CareSyncOutlineDark
import com.caresync.app.theme.CareSyncPurple
import com.caresync.app.theme.CriticalRed
import com.caresync.app.theme.PendingOrange
import com.caresync.app.theme.WarningAmber
import com.caresync.app.utils.LocalCareSyncStrings
import com.caresync.app.viewmodel.DashboardUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeDashboardScreen(
    state: DashboardUiState,
    onSyncNow: () -> Unit,
    onScanWardOutbreak: () -> Unit,
    onSpeakOutbreak: () -> Unit,
    onStopSpeaking: () -> Unit,
    onOpenPatients: (String?) -> Unit,
    onOpenAddPatient: () -> Unit,
    onOpenPatientDetail: (String) -> Unit,
    onOpenSymptomChecker: () -> Unit,
    onOpenAiChat: () -> Unit,
    onOpenAssessment: () -> Unit,
    onOpenRecords: () -> Unit,
    onOpenEmergency: () -> Unit
) {
    val strings = LocalCareSyncStrings.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Greeting + Offline Sync Card
        item {
            Card(
                shape = CareSyncCardShape,
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(CareSyncBlue, Color(0xFF1958D6), Color(0xFF0097B2))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.fieldHubTitle,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                                Text(
                                    text = state.userName,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = state.clinicName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                            Image(
                                painter = painterResource(id = R.drawable.img_caresync_hero),
                                contentDescription = "CareSync telemetry illustration",
                                modifier = Modifier
                                    .size(width = 88.dp, height = 58.dp)
                                    .clip(CareSyncButtonShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            shape = CareSyncButtonShape,
                            color = Color.White.copy(alpha = 0.16f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (state.pendingSyncCount > 0) {
                                            "${state.pendingSyncCount} offline record(s) queued"
                                        } else {
                                            strings.allSynced
                                        },
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Last sync: ${state.syncState.lastSyncLabel}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                                Button(
                                    onClick = onSyncNow,
                                    enabled = !state.syncState.isSyncing,
                                    shape = CareSyncButtonShape,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = CareSyncBlue
                                    ),
                                    modifier = Modifier.testTag("dashboard_sync_button")
                                ) {
                                    if (state.syncState.isSyncing) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Rounded.CloudSync,
                                            contentDescription = strings.syncNow,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(strings.syncNow, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        AnimatedVisibility(visible = state.syncFeedbackMessage != null) {
                            Text(
                                text = state.syncFeedbackMessage.orEmpty(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Quick Actions Strip
        item {
            Column {
                Text(
                    text = strings.quickActions,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile("Add Patient", Icons.Rounded.PersonAdd, CareSyncBlue, "qa_add_patient", onOpenAddPatient, Modifier.width(104.dp))
                    QuickActionTile("Symptoms", Icons.Rounded.MedicalServices, CareSyncCyan, "qa_symptoms", onOpenSymptomChecker, Modifier.width(104.dp))
                    QuickActionTile("AI Chat", Icons.Rounded.SmartToy, CareSyncGreen, "qa_ai_chat", onOpenAiChat, Modifier.width(104.dp))
                    QuickActionTile("Risk Check", Icons.Rounded.MonitorHeart, CareSyncBlue, "qa_risk", onOpenAssessment, Modifier.width(104.dp))
                    QuickActionTile("Records", Icons.Rounded.FolderShared, WarningAmber, "qa_records", onOpenRecords, Modifier.width(104.dp))
                    QuickActionTile("SOS Alert", Icons.Rounded.Emergency, CriticalRed, "qa_sos", onOpenEmergency, Modifier.width(104.dp))
                }
            }
        }

        // Overview Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = strings.clinicalOverview,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardStatCard(
                        title = strings.todaysPatients,
                        value = "${state.todaysPatientsCount}",
                        subtitle = "Active rounds · 1,234 today",
                        icon = Icons.Rounded.Today,
                        accentColor = CareSyncPurple,
                        testTag = "card_todays_patients",
                        badgeText = "18.2%",
                        isBadgePositive = true,
                        onClick = { onOpenPatients("TODAY") },
                        modifier = Modifier.weight(1f)
                    )
                    DashboardStatCard(
                        title = strings.pendingVisits,
                        value = "${state.pendingVisitsCount}",
                        subtitle = "Awaiting checkup",
                        icon = Icons.Rounded.PendingActions,
                        accentColor = PendingOrange,
                        testTag = "card_pending_visits",
                        badgeText = "3.2%",
                        isBadgePositive = false,
                        onClick = { onOpenPatients("PENDING_VISIT") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardStatCard(
                        title = strings.emergencyCases,
                        value = "${state.emergencyCasesCount}",
                        subtitle = "Critical priority",
                        icon = Icons.Rounded.Emergency,
                        accentColor = CriticalRed,
                        testTag = "card_emergency_cases",
                        badgeText = "2.1%",
                        isBadgePositive = true,
                        onClick = { onOpenPatients("CRITICAL") },
                        modifier = Modifier.weight(1f)
                    )
                    DashboardStatCard(
                        title = strings.followUps,
                        value = "${state.followUpsCount}",
                        subtitle = "Scheduled reviews",
                        icon = Icons.Rounded.EventRepeat,
                        accentColor = CareSyncCyan,
                        testTag = "card_follow_ups",
                        badgeText = "31.1%",
                        isBadgePositive = true,
                        onClick = { onOpenPatients(null) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardStatCard(
                        title = strings.pregnancyTracking,
                        value = "${state.pregnancyCount}",
                        subtitle = "ANC mothers monitored",
                        icon = Icons.Rounded.PregnantWoman,
                        accentColor = Color(0xFFEC4899),
                        testTag = "card_pregnancy_tracking",
                        onClick = { onOpenPatients("PREGNANCY") },
                        modifier = Modifier.weight(1f)
                    )
                    DashboardStatCard(
                        title = strings.childMonitoring,
                        value = "${state.childMonitoringCount}",
                        subtitle = "Growth & pediatric care",
                        icon = Icons.Rounded.ChildCare,
                        accentColor = CareSyncGreen,
                        testTag = "card_child_monitoring",
                        onClick = { onOpenPatients("CHILD") },
                        modifier = Modifier.weight(1f)
                    )
                }

                DashboardStatCard(
                    title = strings.vaccinationStatus,
                    value = "${state.vaccinationAvgProgress}%",
                    subtitle = "Cohort immunization coverage across ${state.totalPatientsCount} patients",
                    icon = Icons.Rounded.Vaccines,
                    accentColor = CareSyncGreen,
                    testTag = "card_vaccination_status",
                    onClick = onOpenRecords,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Ward Outbreak & Trend Radar (Gemini AI Cohort Intelligence)
        item {
            Card(
                shape = CareSyncCardShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Rounded.Radar, contentDescription = null, tint = CareSyncBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(strings.outbreakRadarTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (state.isOfflineOnly) "Offline Cohort Mode" else "Gemini 3.8 Flash Surveillance",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (state.outbreakInsight != null) {
                            IconButton(
                                onClick = { if (state.isSpeaking) onStopSpeaking() else onSpeakOutbreak() },
                                modifier = Modifier.testTag("outbreak_tts_button")
                            ) {
                                Icon(
                                    imageVector = if (state.isSpeaking) Icons.Rounded.Stop else Icons.AutoMirrored.Rounded.VolumeUp,
                                    contentDescription = if (state.isSpeaking) "Stop speech" else "Speak cohort briefing",
                                    tint = CareSyncBlue
                                )
                            }
                        }
                    }

                    state.outbreakError?.let { err ->
                        Text(err, style = MaterialTheme.typography.bodySmall, color = CriticalRed)
                    }

                    state.outbreakInsight?.let { insight ->
                        Surface(
                            shape = CareSyncButtonShape,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = insight.headline,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    RiskLevelBadge(rawRisk = insight.alertLevel.name)
                                }
                                Text(insight.summary, style = MaterialTheme.typography.bodySmall)
                                if (insight.affectedWards.isNotEmpty()) {
                                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        insight.affectedWards.forEach { ward ->
                                            Surface(shape = CareSyncButtonShape, color = CareSyncCyan.copy(alpha = 0.14f)) {
                                                Text(
                                                    text = ward,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = CareSyncBlue,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                insight.recommendations.forEach { rec ->
                                    Text("• $rec", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = onScanWardOutbreak,
                        enabled = !state.isScanningOutbreak,
                        shape = CareSyncButtonShape,
                        modifier = Modifier.fillMaxWidth().testTag("scan_ward_outbreak_button")
                    ) {
                        if (state.isScanningOutbreak) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Scanning Ward Cohort...")
                        } else {
                            Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.scanCohortButton, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Critical Patients Watchlist (if any)
        if (state.criticalPatients.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Priority Critical Cases",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CriticalRed
                        )
                        Text(
                            text = "Open SOS",
                            style = MaterialTheme.typography.labelLarge,
                            color = CriticalRed,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onOpenEmergency() }
                        )
                    }
                    state.criticalPatients.forEach { patient ->
                        Card(
                            shape = CareSyncCardShape,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CareSyncCardShape)
                                .clickable { onOpenPatientDetail(patient.id) }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${patient.fullName} · ${patient.age}y",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = patient.notes,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                RiskLevelBadge(rawRisk = patient.riskLevel, isCritical = true)
                            }
                        }
                    }
                }
            }
        }

        // Health Analytics
        item {
            HealthAnalyticsCard(
                weeklyData = state.weeklyVisitsTrend,
                syncedCount = state.syncedCount,
                totalCount = state.totalPatientsCount
            )
        }

        // Recent Activities (Styled as Glowing Violet Card from Reference UI)
        item {
            Card(
                shape = CareSyncCardShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, CareSyncOutlineDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF2E1065).copy(alpha = 0.6f),
                                    Color(0xFF1E1B4B).copy(alpha = 0.4f),
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Recent Activity",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Live clinical events, vitals & field ledger",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFC084FC)
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "Active",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    state.recentActivities.take(5).forEachIndexed { idx, activity ->
                        val isAlert = activity.categoryTag.contains("Emergency", ignoreCase = true) || idx % 3 == 1
                        val iconBg = if (isAlert) Color(0xFF381A12) else Color(0xFF143825)
                        val iconTint = if (isAlert) Color(0xFFFB923C) else Color(0xFF4ADE80)

                        Surface(
                            shape = CareSyncButtonShape,
                            color = Color(0xFF181930).copy(alpha = 0.7f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CareSyncOutlineDark),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = iconBg,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = if (isAlert) "!" else "✓",
                                            color = iconTint,
                                            fontWeight = FontWeight.ExtraBold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = activity.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = activity.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                                Text(
                                    text = activity.timestampLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFA1A1AA)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
