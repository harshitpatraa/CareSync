package com.caresync.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.SyncProblem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.caresync.app.model.RiskLevel
import com.caresync.app.model.SyncStatus
import com.caresync.app.theme.CareSyncBlue
import com.caresync.app.theme.CareSyncCyan
import com.caresync.app.theme.CareSyncGreen
import com.caresync.app.theme.CriticalRed
import com.caresync.app.theme.PendingOrange
import com.caresync.app.theme.SyncedGreen
import com.caresync.app.theme.WarningAmber
import com.caresync.app.utils.FormatUtils

@Composable
fun SyncStatusBadge(
    rawStatus: String,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val status = FormatUtils.parseSyncStatus(rawStatus)
    val (bgColor, fgColor, icon) = when (status) {
        SyncStatus.SYNCED -> Triple(
            SyncedGreen.copy(alpha = 0.14f),
            SyncedGreen,
            Icons.Rounded.CloudDone
        )
        SyncStatus.PENDING_UPLOAD -> Triple(
            PendingOrange.copy(alpha = 0.14f),
            PendingOrange,
            Icons.Rounded.CloudUpload
        )
        SyncStatus.OFFLINE_DRAFT -> Triple(
            CareSyncCyan.copy(alpha = 0.15f),
            CareSyncBlue,
            Icons.Rounded.CloudOff
        )
        SyncStatus.CONFLICT -> Triple(
            CriticalRed.copy(alpha = 0.14f),
            CriticalRed,
            Icons.Rounded.SyncProblem
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (compact) 8.dp else 10.dp,
                vertical = if (compact) 3.dp else 5.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = status.label,
                tint = fgColor,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = status.label,
                style = MaterialTheme.typography.labelSmall,
                color = fgColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun RiskLevelBadge(
    rawRisk: String,
    isCritical: Boolean = false,
    modifier: Modifier = Modifier
) {
    val risk = if (isCritical) RiskLevel.CRITICAL else FormatUtils.parseRiskLevel(rawRisk)
    val (bg, fg) = when (risk) {
        RiskLevel.CRITICAL -> CriticalRed.copy(alpha = 0.15f) to CriticalRed
        RiskLevel.HIGH -> PendingOrange.copy(alpha = 0.15f) to PendingOrange
        RiskLevel.MODERATE -> WarningAmber.copy(alpha = 0.18f) to Color(0xFFB45309)
        RiskLevel.LOW -> CareSyncGreen.copy(alpha = 0.15f) to SyncedGreen
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = bg
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (risk == RiskLevel.CRITICAL) {
                Icon(
                    imageVector = Icons.Rounded.ErrorOutline,
                    contentDescription = "Critical",
                    tint = fg,
                    modifier = Modifier.size(13.dp)
                )
            }
            Text(
                text = risk.label,
                style = MaterialTheme.typography.labelSmall,
                color = fg,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PatientAvatar(
    fullName: String,
    isCritical: Boolean,
    size: Dp = 46.dp,
    modifier: Modifier = Modifier
) {
    val initials = FormatUtils.initials(fullName)
    val bgColor = if (isCritical) CriticalRed.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer
    val textColor = if (isCritical) CriticalRed else MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
