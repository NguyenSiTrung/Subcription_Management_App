package com.example.subcriptionmanagementapp.ui.components.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.subcriptionmanagementapp.ui.theme.PrimaryColor
import com.example.subcriptionmanagementapp.util.formatCurrency

data class LineData(val label: String, val value: Float)

@Composable
fun LineChart(
        data: List<LineData>,
        modifier: Modifier = Modifier,
        lineColor: Color = PrimaryColor,
        lineWidth: Float = 3f,
        pointRadius: Float = 5f,
        animationDuration: Int = 1000,
        showPoints: Boolean = true,
        showValues: Boolean = true
) {
    val maxValue = remember(data) { data.maxOfOrNull { it.value } ?: 1f }
    val minValue = remember(data) { data.minOfOrNull { it.value } ?: 0f }
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animatedProgress.animateTo(targetValue = 1f, animationSpec = tween(animationDuration))
    }

    val density = LocalDensity.current
    val textPaint =
            remember(density) {
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = with(density) { 12.sp.toPx() }
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            }

    Canvas(modifier = modifier.fillMaxWidth().height(200.dp)) {
        val chartWidth = size.width
        val chartHeight = size.height
        val padding = 40f

        // Calculate points
        val points =
                data.mapIndexed { index, lineData ->
                    val x = padding + (chartWidth - 2 * padding) * index / (data.size - 1)
                    val y =
                            chartHeight -
                                    padding -
                                    ((lineData.value - minValue) / (maxValue - minValue)) *
                                            (chartHeight - 2 * padding)
                    Offset(x, y)
                }

        // Draw line
        if (points.size > 1) {
            val path =
                    Path().apply {
                        moveTo(points[0].x, points[0].y)

                        for (i in 1 until points.size) {
                            val progress = animatedProgress.value * data.size
                            if (i <= progress) {
                                lineTo(points[i].x, points[i].y)
                            }
                        }
                    }

            drawPath(
                    path = path,
                    color = lineColor,
                    style =
                            androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = lineWidth,
                                    cap = StrokeCap.Round
                            )
            )
        }

        // Draw points and values
        points.forEachIndexed { index, point ->
            val progress = animatedProgress.value * data.size
            if (index <= progress) {
                // Draw point
                if (showPoints) {
                    drawCircle(color = lineColor, radius = pointRadius, center = point)
                }

                // Draw label
                drawContext.canvas.nativeCanvas.drawText(
                        data[index].label,
                        point.x,
                        chartHeight - 10f,
                        textPaint
                )

                // Draw value
                if (showValues && animatedProgress.value == 1f) {
                    drawContext.canvas.nativeCanvas.drawText(
                            data[index].value.toDouble().formatCurrency(),
                            point.x,
                            point.y - 20f,
                            textPaint
                    )
                }
            }
        }
    }
}

@Composable
fun SimpleLineChart(
        data: List<LineData>,
        modifier: Modifier = Modifier,
        animationDuration: Int = 1000
) {
    Column(modifier = modifier.fillMaxWidth()) {
        LineChart(
                data = data,
                modifier = Modifier.fillMaxWidth().weight(1f),
                animationDuration = animationDuration,
                showPoints = true,
                showValues = false
        )

        Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEach { lineData ->
                Text(text = lineData.label, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
