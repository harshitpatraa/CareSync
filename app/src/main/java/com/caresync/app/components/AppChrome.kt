package com.caresync.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Emergency
import androidx.compose.material.icons.rounded.FolderShared
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.caresync.app.theme.CareSyncBlue
import com.caresync.app.theme.CareSyncCyan
import com.caresync.app.theme.CareSyncGreen
import com.caresync.app.theme.CriticalRed
import com.caresync.app.theme.PendingOrange
import com.caresync.app.utils.CareSyncStrings
import com.caresync.app.utils.LocalCareSyncStrings

data class BottomNavDestination(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

private fun localizedTopLevelTabs(strings: CareSyncStrings): List<BottomNavDestination> = listOf(
    BottomNavDestination("dashboard", strings.navHome, Icons.Rounded.Dashboard, "nav_home"),
    BottomNavDestination("patients", strings.navPatients, Icons.Rounded.People, "nav_patients"),
    BottomNavDestination("ai", strings.navAiCare, Icons.Rounded.AutoAwesome, "nav_ai"),
    BottomNavDestination("alerts", strings.navAlerts, Icons.Rounded.Notifications, "nav_alerts"),
    BottomNavDestination("emergency", strings.navSos, Icons.Rounded.Emergency, "nav_emergency")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareSyncTopBar(
    title: String,
    showBack: Boolean,
    pendingSyncCount: Int,
    isOfflineOnly: Boolean,
    unreadAlertsCount: Int,
    onMenuOrBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSyncPillClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!showBack) {
                    CareSyncLogo(size = 28.dp, animateNodes = false)
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onMenuOrBackClick,
                modifier = Modifier.testTag(if (showBack) "top_back_button" else "top_menu_button")
            ) {
                Icon(
                    imageVector = if (showBack) Icons.AutoMirrored.Rounded.ArrowBack else Icons.Rounded.Menu,
                    contentDescription = if (showBack) "Back" else "Open navigation drawer"
                )
            }
        },
        actions = {
            val pillColor = when {
                isOfflineOnly -> CareSyncCyan.copy(alpha = 0.16f)
                pendingSyncCount > 0 -> PendingOrange.copy(alpha = 0.15f)
                else -> CareSyncGreen.copy(alpha = 0.15f)
            }
            val pillTint = when {
                isOfflineOnly -> CareSyncBlue
                pendingSyncCount > 0 -> PendingOrange
                else -> CareSyncGreen
            }
            Surface(
                onClick = onSyncPillClick,
                shape = RoundedCornerShape(50),
                color = pillColor,
                modifier = Modifier.testTag("top_sync_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isOfflineOnly) Icons.Rounded.CloudOff else Icons.Rounded.CloudDone,
                        contentDescription = "Sync status",
                        tint = pillTint,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (pendingSyncCount > 0) "$pendingSyncCount" else "Sync",
                        style = MaterialTheme.typography.labelSmall,
                        color = pillTint,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier.testTag("top_notifications_button")
            ) {
                BadgedBox(
                    badge = {
                        if (unreadAlertsCount > 0) {
                            Badge(containerColor = CriticalRed) {
                                Text("$unreadAlertsCount", color = Color.White)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Notifications,
                        contentDescription = "Notifications"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun CareSyncBottomBar(
    activeTabId: String,
    unreadAlertsCount: Int,
    onSelectTab: (String) -> Unit
) {
    val strings = LocalCareSyncStrings.current
    val tabs = localizedTopLevelTabs(strings)
    NavigationBar(
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        tabs.forEach { tab ->
            val selected = activeTabId == tab.id
            val isSos = tab.id == "emergency"
            NavigationBarItem(
                selected = selected,
                onClick = { onSelectTab(tab.id) },
                modifier = Modifier.testTag(tab.testTag),
                icon = {
                    if (tab.id == "alerts" && unreadAlertsCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = CriticalRed) {
                                    Text("$unreadAlertsCount", color = Color.White)
                                }
                            }
                        ) {
                            Icon(tab.icon, contentDescription = tab.label)
                        }
                    } else {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = if (isSos) CriticalRed else if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                label = {
                    Text(
                        text = tab.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isSos) CriticalRed else Color.Unspecified
                    )
                }
            )
        }
    }
}

@Composable
fun CareSyncNavigationRail(
    activeTabId: String,
    unreadAlertsCount: Int,
    onSelectTab: (String) -> Unit
) {
    val strings = LocalCareSyncStrings.current
    val tabs = localizedTopLevelTabs(strings)
    NavigationRail(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        CareSyncLogo(size = 38.dp, animateNodes = false)
        Spacer(modifier = Modifier.height(20.dp))
        tabs.forEach { tab ->
            val selected = activeTabId == tab.id
            val isSos = tab.id == "emergency"
            NavigationRailItem(
                selected = selected,
                onClick = { onSelectTab(tab.id) },
                modifier = Modifier.testTag(tab.testTag),
                icon = {
                    if (tab.id == "alerts" && unreadAlertsCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = CriticalRed) {
                                    Text("$unreadAlertsCount", color = Color.White)
                                }
                            }
                        ) {
                            Icon(tab.icon, contentDescription = tab.label)
                        }
                    } else {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = if (isSos) CriticalRed else Color.Unspecified
                        )
                    }
                },
                label = { Text(tab.label) }
            )
        }
    }
}

@Composable
fun CareSyncDrawerContent(
    userName: String,
    userEmail: String,
    pendingSyncCount: Int,
    onNavigate: (String) -> Unit
) {
    val strings = LocalCareSyncStrings.current
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(listOf(CareSyncBlue, Color(0xFF1A5FD9)))
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CareSyncLogo(size = 44.dp, animateNodes = false, showBadgeBackground = false)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "CareSync",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Offline-First Healthcare Sync",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    if (pendingSyncCount > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.White.copy(alpha = 0.18f)
                        ) {
                            Text(
                                text = "$pendingSyncCount record(s) queued offline",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            DrawerItem(strings.navHome, Icons.Rounded.Dashboard, "drawer_dashboard") { onNavigate("dashboard") }
            DrawerItem(strings.navPatients, Icons.Rounded.People, "drawer_patients") { onNavigate("patients") }
            DrawerItem("Health Records", Icons.Rounded.FolderShared, "drawer_records") { onNavigate("records") }
            DrawerItem("Symptom Checker", Icons.Rounded.MedicalServices, "drawer_symptom") { onNavigate("symptom") }
            DrawerItem("AI Chat Assistant", Icons.Rounded.SmartToy, "drawer_chat") { onNavigate("chat") }
            DrawerItem("Medical Assessment", Icons.Rounded.MonitorHeart, "drawer_assessment") { onNavigate("assessment") }
            DrawerItem(strings.emergencyDispatch, Icons.Rounded.Emergency, "drawer_emergency", CriticalRed) { onNavigate("emergency") }
            DrawerItem(strings.navAlerts, Icons.Rounded.Notifications, "drawer_notifications") { onNavigate("notifications") }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))

            DrawerItem("Profile", Icons.Rounded.Person, "drawer_profile") { onNavigate("profile") }
            DrawerItem("Settings & Sync", Icons.Rounded.Settings, "drawer_settings") { onNavigate("settings") }
            DrawerItem("About CareSync", Icons.Rounded.Info, "drawer_about") { onNavigate("about") }
            DrawerItem("Help & Protocols", Icons.AutoMirrored.Rounded.HelpOutline, "drawer_help") { onNavigate("help") }
        }
    }
}

@Composable
private fun DrawerItem(
    label: String,
    icon: ImageVector,
    testTag: String,
    tint: Color? = null,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(label, color = tint ?: Color.Unspecified) },
        selected = false,
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag(testTag),
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint ?: MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}
