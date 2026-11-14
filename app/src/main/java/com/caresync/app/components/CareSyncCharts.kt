package com.caresync.app.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.caresync.app.database.VitalsEntity
import com.caresync.app.theme.CareSyncBlue
import com.caresync.app.theme.CareSyncCardShape
import com.caresync.app.theme.CareSyncCyan
import com.caresync.app.theme.CareSyncGreen
import com.caresync.app.theme.CriticalRed
import com.caresync.app.theme.PendingOrange

private enum class VitalSeries(val label: String) {
    BP("BP (Sys/Dia)"),
    SPO2("SpO2 %"),
    HR("Heart Rate")
}

@Composable
fun VitalsTrendChartCard(
    vitals: List<VitalsEntity>,
    modifier: Modifier = Modifier
) {
    val chronological = remember(vitals) { vitals.take(10).reversed() }
    if (chronological.isEmpty()) return

    var selectedSeries by remember { mutableStateOf(VitalSeries.BP) }
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)

    Card(
        shape = CareSyncCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Rounded.ShowChart, contentDescription = null, tint = CareSyncBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Vitals Trend (${chronological.size} readings)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VitalSeries.entries.forEach { series ->
                    FilterChip(
                        selected = selectedSeries == series,
                        onClick = { selectedSeries = series },
                        label = { Text(series.label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            val (minRange, maxRange, thresholdValue, thresholdLabel) = when (selectedSeries) {
                VitalSeries.BP -> Quad(50f, 200f, 140f, "140 mmHg High Sys")
                VitalSeries.SPO2 -> Quad(82f, 102f, 92f, "92% Low SpO2")
                VitalSeries.HR -> Quad(45f, 150f, 120f, "120 bpm Tachy")
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(148.dp)
                    .padding(horizontal = 4.dp, vertical = 6.dp)
            ) {
                val w = size.width
                val h = size.height
                val span = (maxRange - minRange).coerceAtLeast(1f)

                fun yFor(value: Float): Float {
                    val norm = ((value - minRange) / span).coerceIn(0f, 1f)
                    return h - (norm * h)
                }

                // Subtle horizontal grid lines
                listOf(0.25f, 0.5f, 0.75f).forEach { frac ->
                    val y = h * frac
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Clinical threshold dashed line
                val thresholdY = yFor(thresholdValue)
                drawLine(
                    color = CriticalRed.copy(alpha = 0.65f),
                    start = Offset(0f, thresholdY),
                    end = Offset(w, thresholdY),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                )

                val count = chronological.size
                fun xFor(index: Int): Float =
                    if (count <= 1) w / 2f else (index.toFloat() / (count - 1).toFloat()) * w

                fun drawMetricLine(values: List<Float>, lineColor: Color) {
                    if (values.size == 1) {
                        drawCircle(
                            color = lineColor,
                            radius = 5.dp.toPx(),
                            center = Offset(xFor(0), yFor(values[0]))
                        )
                        return
                    }
                    val path = Path()
                    values.forEachIndexed { idx, v ->
                        val pt = Offset(xFor(idx), yFor(v))
                        if (idx == 0) path.moveTo(pt.x, pt.y) else path.lineTo(pt.x, pt.y)
                    }
                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                    values.forEachIndexed { idx, v ->
                        val pt = Offset(xFor(idx), yFor(v))
                        drawCircle(color = Color.White, radius = 4.5.dp.toPx(), center = pt)
                        drawCircle(color = lineColor, radius = 3.dp.toPx(), center = pt)
                    }
                }

                when (selectedSeries) {
                    VitalSeries.BP -> {
                        drawMetricLine(chronological.map { it.systolicBp.toFloat() }, CareSyncBlue)
                        drawMetricLine(chronological.map { it.diastolicBp.toFloat() }, CareSyncCyan)
                    }
                    VitalSeries.SPO2 -> {
                        drawMetricLine(chronological.map { it.spo2Percent.toFloat() }, CareSyncGreen)
                    }
                    VitalSeries.HR -> {
                        drawMetricLine(chronological.map { it.heartRate.toFloat() }, PendingOrange)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    when (selectedSeries) {
                        VitalSeries.BP -> {
                            LegendDot("Systolic", CareSyncBlue)
                            LegendDot("Diastolic", CareSyncCyan)
                        }
                        VitalSeries.SPO2 -> LegendDot("SpO2 %", CareSyncGreen)
                        VitalSeries.HR -> LegendDot("HR bpm", PendingOrange)
                    }
                }
                Text(
                    text = "Alert line: $thresholdLabel",
                    style = MaterialTheme.typography.labelSmall,
                    color = CriticalRed
                )
            }
        }
    }
}

@Composable
private fun LegendDot(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
