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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.subcriptionmanagementapp.ui.theme.PrimaryColor
import com.example.subcriptionmanagementapp.util.formatCurrency

data class BarData(
    val label: String,
    val value: Float,
    val color: Color = PrimaryColor
)

@Composable
fun BarChart(
    data: List<BarData>,
    modifier: Modifier = Modifier,
    barWidth: Float = 30f,
    barSpacing: Float = 20f,
    animationDuration: Int = 1000
) {
    val maxValue = remember(data) { data.maxOfOrNull { it.value } ?: 1f }
    val animatedProgress = remember { Animatable(0f) }
    
    LaunchedEffect(data) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(animationDuration)
        )
    }
    
    val density = LocalDensity.current
    val textPaint = remember(density) {
        androidx.compose.ui.graphics.Paint().apply {
            color = Color.Black
            textSize = 12.sp.toPx()
            textAlign = androidx.compose.ui.graphics.Paint.Align.CENTER
        }
    }
    
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val chartWidth = size.width
        val chartHeight = size.height
        val totalBarWidth = barWidth + barSpacing
        val totalWidth = totalBarWidth * data.size - barSpacing
        val startX = (chartWidth - totalWidth) / 2
        
        data.forEachIndexed { index, barData ->
            val barHeight = (barData.value / maxValue) * chartHeight * 0.8f * animatedProgress.value
            val x = startX + index * totalBarWidth
            val y = chartHeight - barHeight
            
            // Draw bar
            drawRect(
                color = barData.color,
                topLeft = Offset(x, y),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
            )
            
            // Draw label
            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    barData.label,
                    x + barWidth / 2,
                    chartHeight + 20f,
                    textPaint
                )
            }
            
            // Draw value
            if (animatedProgress.value == 1f) {
                drawIntoCanvas {
                    it.nativeCanvas.drawText(
                        formatCurrency(barData.value.toDouble()),
                        x + barWidth / 2,
                        y - 10f,
                        textPaint
                    )
                }
            }
        }
    }
}

@Composable
fun SimpleBarChart(
    data: List<BarData>,
    modifier: Modifier = Modifier,
    animationDuration: Int = 1000
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        BarChart(
            data = data,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            animationDuration = animationDuration
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEach { barData ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(12.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRect(color = barData.color)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = barData.label,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}