package com.caresync.app.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.caresync.app.theme.CareSyncBlue
import com.caresync.app.theme.CareSyncCyan
import com.caresync.app.theme.CareSyncGreen

@Composable
fun CareSyncLogo(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    animateNodes: Boolean = true,
    showBadgeBackground: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "caresync_logo_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "node_pulse"
    )
    val scale = if (animateNodes) pulseScale else 1f

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cx = w / 2f
        val cy = h / 2f

        if (showBadgeBackground) {
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(CareSyncBlue, Color(0xFF1A5FD9))
                ),
                cornerRadius = CornerRadius(w * 0.26f, w * 0.26f),
                size = Size(w, h)
            )
        }

        // Orbiting diamond sync connections connecting the 4 cardinal nodes
        val nodeDist = w * 0.34f
        val topNode = Offset(cx, cy - nodeDist)
        val rightNode = Offset(cx + nodeDist, cy)
        val bottomNode = Offset(cx, cy + nodeDist)
        val leftNode = Offset(cx - nodeDist, cy)

        val linkColor = if (showBadgeBackground) Color.White.copy(alpha = 0.45f) else CareSyncCyan.copy(alpha = 0.55f)
        val strokeW = w * 0.032f

        drawLine(linkColor, topNode, rightNode, strokeWidth = strokeW, cap = StrokeCap.Round)
        drawLine(linkColor, rightNode, bottomNode, strokeWidth = strokeW, cap = StrokeCap.Round)
        drawLine(linkColor, bottomNode, leftNode, strokeWidth = strokeW, cap = StrokeCap.Round)
        drawLine(linkColor, leftNode, topNode, strokeWidth = strokeW, cap = StrokeCap.Round)

        // Medical cross centered cleanly
        val armLength = w * 0.42f
        val armThickness = w * 0.16f
        val crossColor = if (showBadgeBackground) Color.White else CareSyncBlue

        drawRoundRect(
            color = crossColor,
            topLeft = Offset(cx - armThickness / 2f, cy - armLength / 2f),
            size = Size(armThickness, armLength),
            cornerRadius = CornerRadius(armThickness * 0.35f, armThickness * 0.35f)
        )
        drawRoundRect(
            color = crossColor,
            topLeft = Offset(cx - armLength / 2f, cy - armThickness / 2f),
            size = Size(armLength, armThickness),
            cornerRadius = CornerRadius(armThickness * 0.35f, armThickness * 0.35f)
        )

        // Connected healthcare synchronization nodes
        val outerR = w * 0.068f * scale
        drawCircle(color = CareSyncCyan, radius = outerR, center = topNode)
        drawCircle(color = CareSyncGreen, radius = outerR, center = rightNode)
        drawCircle(color = CareSyncCyan, radius = outerR, center = bottomNode)
        drawCircle(color = CareSyncGreen, radius = outerR, center = leftNode)

        // Center synchronization hub dot
        drawCircle(
            color = CareSyncBlue,
            radius = w * 0.065f,
            center = Offset(cx, cy)
        )
        drawCircle(
            color = CareSyncGreen,
            radius = w * 0.036f * scale,
            center = Offset(cx, cy)
        )
    }
}
