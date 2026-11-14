package com.caresync.app.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CalendarToday
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
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material.icons.rounded.Vaccines
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.caresync.app.theme.CriticalRed
import com.caresync.app.theme.PendingOrange
import com.caresync.app.theme.WarningAmber
import com.caresync.app.viewmodel.DashboardUiState

@Composable
fun HomeDashboardScreen(
    state: DashboardUiState,
    onSyncNow: () -> Unit,
    onOpenPatients: (String?) -> Unit,
    onOpenAddPatient: () -> Unit,
    onOpenPatientDetail: (String) -> Unit,
    onOpenSymptomChecker: () -> Unit,
    onOpenAiChat: () -> Unit,
    onOpenAssessment: () -> Unit,
    onOpenRecords: () -> Unit,
    onOpenEmergency: () -> Unit
) {
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
                                    text = "CareSync Field Hub",
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
                                            "All local records synchronized"
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
                                            contentDescription = "Sync now",
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Sync Now", fontWeight = FontWeight.Bold)
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
                    text = "Quick Actions",
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

        // Overview Grid: Today's Patients, Pending Visits, Emergency Cases, Follow Ups, Vaccination Status, Pregnancy Tracking, Child Monitoring
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Clinical Care Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardStatCard(
                        title = "Today's Patients",
                        value = "${state.todaysPatientsCount}",
                        subtitle = "Active home/OPD rounds",
                        icon = Icons.Rounded.Today,
                        accentColor = CareSyncBlue,
                        testTag = "card_todays_patients",
                        onClick = { onOpenPatients("TODAY") },
                        modifier = Modifier.weight(1f)
                    )
                    DashboardStatCard(
                        title = "Pending Visits",
                        value = "${state.pendingVisitsCount}",
                        subtitle = "Awaiting checkup",
                        icon = Icons.Rounded.PendingActions,
                        accentColor = PendingOrange,
                        testTag = "card_pending_visits",
                        onClick = { onOpenPatients("PENDING_SYNC") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardStatCard(
                        title = "Emergency Cases",
                        value = "${state.emergencyCasesCount}",
                        subtitle = "Critical priority",
                        icon = Icons.Rounded.Emergency,
                        accentColor = CriticalRed,
                        testTag = "card_emergency_cases",
                        onClick = { onOpenPatients("CRITICAL") },
                        modifier = Modifier.weight(1f)
                    )
                    DashboardStatCard(
                        title = "Follow Ups",
                        value = "${state.followUpsCount}",
                        subtitle = "Scheduled reviews",
                        icon = Icons.Rounded.EventRepeat,
                        accentColor = CareSyncCyan,
                        testTag = "card_follow_ups",
                        onClick = { onOpenPatients(null) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardStatCard(
                        title = "Pregnancy Tracking",
                        value = "${state.pregnancyCount}",
                        subtitle = "ANC mothers monitored",
                        icon = Icons.Rounded.PregnantWoman,
                        accentColor = Color(0xFFEC4899),
                        testTag = "card_pregnancy_tracking",
                        onClick = { onOpenPatients("PREGNANCY") },
                        modifier = Modifier.weight(1f)
                    )
                    DashboardStatCard(
                        title = "Child Monitoring",
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
                    title = "Vaccination Status",
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

        // Recent Activities
        item {
            Text(
                text = "Recent Activities",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(state.recentActivities.take(6), key = { it.id }) { activity ->
            Card(
                shape = CareSyncCardShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = activity.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = activity.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = activity.timestampLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
