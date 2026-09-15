package com.example.ui.graphify

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.min

data class GraphifyBarData(
    val label: String,
    val value: Float,
    val color: Color = Indigo600,
    val secondaryValue: Float? = null,
    val tooltip: String = ""
)

data class GraphifySliceData(
    val label: String,
    val value: Float,
    val color: Color
)

data class GraphifyPoint(
    val xLabel: String,
    val yValue: Float
)


@Composable
fun GraphifyDonutChart(
    slices: List<GraphifySliceData>,
    modifier: Modifier = Modifier,
    centerLabel: String = "Total",
    centerValue: String = "",
    strokeWidthDp: Dp = 24.dp
) {
    val total = remember(slices) {
        slices.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)
    }
    val animProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "donut_sweep"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(180.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            val stroke = Stroke(width = strokeWidthDp.toPx(), cap = StrokeCap.Round)
            val chartDiameter = min(size.width, size.height) - strokeWidthDp.toPx()
            val topLeft = Offset((size.width - chartDiameter) / 2f, (size.height - chartDiameter) / 2f)
            val arcSize = Size(chartDiameter, chartDiameter)

            var startAngle = -90f
            slices.forEach { slice ->
                val sweepAngle = (slice.value / total) * 360f * animProgress
                if (sweepAngle > 0f) {
                    drawArc(
                        color = slice.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle - 2f.coerceAtMost(sweepAngle),
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = stroke
                    )
                }
                startAngle += (slice.value / total) * 360f
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = centerLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = centerValue,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


@Composable
fun GraphifyBarChart(
    data: List<GraphifyBarData>,
    modifier: Modifier = Modifier,
    height: Dp = 160.dp,
    maxValue: Float? = null,
    barWidth: Dp = 16.dp
) {
    if (data.isEmpty()) return
    val computedMax = remember(data, maxValue) {
        maxValue ?: (data.maxOfOrNull { it.value }?.coerceAtLeast(10f) ?: 100f)
    }
    val animProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "bar_anim"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEach { bar ->
                val barFraction = remember(bar.value, computedMax, animProgress) {
                    (bar.value / computedMax).coerceIn(0.05f, 1f) * animProgress
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                ) {
                    Text(
                        text = if (bar.value >= 1000) "${(bar.value / 1000).toInt()}k" else "${bar.value.toInt()}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(barWidth)
                            .fillMaxHeight(barFraction)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        bar.color,
                                        bar.color.copy(alpha = 0.7f)
                                    )
                                )
                            )
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = bar.label,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium, fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
            }
        }
    }
}


@Composable
fun GraphifyLineChart(
    points: List<GraphifyPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = Indigo600,
    fillColor: Color = Indigo200.copy(alpha = 0.35f),
    height: Dp = 140.dp
) {
    if (points.isEmpty()) return
    val animProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "line_anim"
    )

    val maxVal = remember(points) { points.maxOfOrNull { it.yValue }?.coerceAtLeast(10f) ?: 100f }
    val minVal = remember(points) { points.minOfOrNull { it.yValue }?.coerceAtLeast(0f) ?: 0f }
    val range = remember(maxVal, minVal) { (maxVal - minVal).coerceAtLeast(1f) }

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            val width = size.width
            val chartHeight = size.height - 24.dp.toPx()
            val stepX = width / (points.size - 1).coerceAtLeast(1)

            val path = Path()
            val fillPath = Path()

            points.forEachIndexed { index, pt ->
                val x = index * stepX
                val normalizedY = 1f - ((pt.yValue - minVal) / range) * animProgress
                val y = normalizedY * chartHeight + 12.dp.toPx()

                if (index == 0) {
                    path.moveTo(x, y)
                    fillPath.moveTo(x, chartHeight + 12.dp.toPx())
                    fillPath.lineTo(x, y)
                } else {
                    val prevX = (index - 1) * stepX
                    val prevNormY = 1f - ((points[index - 1].yValue - minVal) / range) * animProgress
                    val prevY = prevNormY * chartHeight + 12.dp.toPx()

                    val cx1 = prevX + (x - prevX) / 2
                    val cy1 = prevY
                    val cx2 = prevX + (x - prevX) / 2
                    val cy2 = y
                    path.cubicTo(cx1, cy1, cx2, cy2, x, y)
                    fillPath.cubicTo(cx1, cy1, cx2, cy2, x, y)
                }

                drawCircle(
                    color = lineColor,
                    radius = 4.dp.toPx(),
                    center = Offset(x, y)
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.dp.toPx(),
                    center = Offset(x, y)
                )
            }

            fillPath.lineTo(width, chartHeight + 12.dp.toPx())
            fillPath.close()

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(fillColor, fillColor.copy(alpha = 0.05f)),
                    startY = 0f,
                    endY = chartHeight
                )
            )

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            points.forEach { pt ->
                Text(
                    text = pt.xLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
fun GraphifyCircularGauge(
    score: Int,
    title: String,
    modifier: Modifier = Modifier,
    color: Color = Indigo600,
    size: Dp = 110.dp,
    strokeWidth: Dp = 10.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (score.coerceIn(0, 100)) / 100f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "gauge_sweep"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(strokeWidth / 2)) {
            val trackStroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            val diameter = min(this.size.width, this.size.height)
            val topLeft = Offset((this.size.width - diameter) / 2f, (this.size.height - diameter) / 2f)
            val arcSize = Size(diameter, diameter)


            drawArc(
                color = color.copy(alpha = 0.15f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = trackStroke
            )


            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(color.copy(alpha = 0.7f), color, color),
                    center = Offset(this.size.width / 2, this.size.height / 2)
                ),
                startAngle = 135f,
                sweepAngle = 270f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = trackStroke
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score%",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

