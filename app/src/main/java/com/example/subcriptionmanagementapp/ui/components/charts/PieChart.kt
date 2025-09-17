package com.example.subcriptionmanagementapp.ui.components.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.subcriptionmanagementapp.ui.theme.*
import com.example.subcriptionmanagementapp.util.formatCurrency
import kotlin.math.cos
import kotlin.math.sin

data class PieData(val label: String, val value: Float, val color: Color)

@Composable
fun PieChart(
        data: List<PieData>,
        modifier: Modifier = Modifier,
        animationDuration: Int = 1000,
        showLegend: Boolean = true,
        showPercentage: Boolean = true
) {
    val totalValue = remember(data) { data.sumOf { it.value.toDouble() }.toFloat() }
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animatedProgress.animateTo(targetValue = 1f, animationSpec = tween(animationDuration))
    }

    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Canvas(modifier = Modifier.size(200.dp).weight(1f)) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val radius = minOf(canvasWidth, canvasHeight) * 0.4f
            val center = Offset(canvasWidth / 2, canvasHeight / 2)

            var startAngle = -90f

            data.forEach { pieData ->
                val sweepAngle = (pieData.value / totalValue) * 360f * animatedProgress.value

                // Draw pie slice
                drawArc(
                        color = pieData.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2)
                )

                // Draw percentage text
                if (showPercentage && animatedProgress.value == 1f) {
                    val percentage = (pieData.value / totalValue) * 100
                    val angle = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
                    val textRadius = radius * 0.7f
                    val textX = (center.x + textRadius * cos(angle)).toFloat()
                    val textY = (center.y + textRadius * sin(angle)).toFloat()

                    drawContext.canvas.nativeCanvas.drawText(
                            "%.1f%%".format(percentage),
                            textX,
                            textY,
                            android.graphics.Paint().apply {
                                color = android.graphics.Color.WHITE
                                textSize = 14.sp.toPx()
                                textAlign = android.graphics.Paint.Align.CENTER
                            }
                    )
                }

                startAngle += sweepAngle
            }
        }

        if (showLegend) {
            Column(
                    modifier = Modifier.weight(1f).padding(start = 16.dp),
                    verticalArrangement = Arrangement.Center
            ) {
                data.forEach { pieData ->
                    Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(16.dp).padding(end = 8.dp)) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawRect(color = pieData.color)
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                    text = pieData.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                            )

                            Text(
                                    text = pieData.value.toDouble().formatCurrency(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (animatedProgress.value == 1f) {
                            val percentage = (pieData.value / totalValue) * 100
                            Text(
                                    text = "%.1f%%".format(percentage),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimplePieChart(
        data: List<PieData>,
        modifier: Modifier = Modifier,
        animationDuration: Int = 1000
) {
    PieChart(
            data = data,
            modifier = modifier,
            animationDuration = animationDuration,
            showLegend = true,
            showPercentage = false
    )
}
