package com.gshoaib998.progressly.ui.dashboard.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class LineChartData(
    val label: String,
    val value: Float
)

@Composable
fun LineChart(
    data: List<LineChartData>,
    modifier: Modifier = Modifier,
    height: Dp = 200.dp,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    fillColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
    dotColor: Color = MaterialTheme.colorScheme.primary,
    selectedDotColor: Color = MaterialTheme.colorScheme.tertiary,
    labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    gridColor: Color = MaterialTheme.colorScheme.outlineVariant,
) {
    if (data.size < 2) return

    val maxValue = data.maxOf { it.value }.coerceAtLeast(1f)
    val textMeasurer = rememberTextMeasurer()

    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, animationSpec = tween(900))
    }

    var selectedIndex by remember { mutableIntStateOf(-1) }

    val labelStyle = TextStyle(fontSize = 11.sp, color = labelColor)
    val tooltipStyle = TextStyle(fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimary)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .pointerInput(data) {
                detectTapGestures { offset ->
                    val labelAreaHeight = 24.dp.toPx()
                    val chartHeight = size.height - labelAreaHeight
                    val stepX = size.width / (data.size - 1).toFloat()
                    // Find nearest point
                    val nearestIndex = (offset.x / stepX).toInt().coerceIn(data.indices)
                    selectedIndex = if (selectedIndex == nearestIndex) -1 else nearestIndex
                }
            }
    ) {
        val labelAreaHeight = 24.dp.toPx()
        val chartHeight = size.height - labelAreaHeight
        val stepX = size.width / (data.size - 1).toFloat()

        // Helper: x,y for data point at index, applying animation clipping
        fun pointX(index: Int) = index * stepX
        fun pointY(value: Float) = chartHeight * (1f - value / maxValue)

        // ── Grid lines ────────────────────────────────────────
        val gridSteps = 4
        repeat(gridSteps + 1) { step ->
            val y = chartHeight * (1f - step.toFloat() / gridSteps)
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 0.5.dp.toPx()
            )
            val gridLabel = "${(maxValue * step / gridSteps).toInt()}%"
            val measured = textMeasurer.measure(gridLabel, labelStyle)
            drawText(measured, topLeft = Offset(4.dp.toPx(), y - measured.size.height - 2.dp.toPx()))
        }

        // ── Animate: only draw up to animProgress fraction ────
        val visibleCount = (data.size * animProgress.value).toInt()
            .coerceAtLeast(1)
            .coerceAtMost(data.size)

        // ── Fill area under line ──────────────────────────────
        val fillPath = Path().apply {
            moveTo(pointX(0), pointY(data[0].value))
            for (i in 1 until visibleCount) {
                // Smooth cubic bezier between points
                val prev = data[i - 1]
                val curr = data[i]
                val cpX = (pointX(i - 1) + pointX(i)) / 2f
                cubicTo(
                    cpX, pointY(prev.value),
                    cpX, pointY(curr.value),
                    pointX(i), pointY(curr.value)
                )
            }
            lineTo(pointX(visibleCount - 1), chartHeight)
            lineTo(pointX(0), chartHeight)
            close()
        }
        drawPath(fillPath, color = fillColor)

        // ── Line ──────────────────────────────────────────────
        val linePath = Path().apply {
            moveTo(pointX(0), pointY(data[0].value))
            for (i in 1 until visibleCount) {
                val prev = data[i - 1]
                val curr = data[i]
                val cpX = (pointX(i - 1) + pointX(i)) / 2f
                cubicTo(
                    cpX, pointY(prev.value),
                    cpX, pointY(curr.value),
                    pointX(i), pointY(curr.value)
                )
            }
        }
        drawPath(
            linePath,
            color = lineColor,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )

        // ── Dots + x-axis labels ──────────────────────────────
        data.forEachIndexed { index, item ->
            if (index >= visibleCount) return@forEachIndexed
            val cx = pointX(index)
            val cy = pointY(item.value)
            val isSelected = index == selectedIndex
            val dotRadius = if (isSelected) 6.dp.toPx() else 3.5.dp.toPx()
            val color = if (isSelected) selectedDotColor else dotColor

            // White ring then colored dot
            drawCircle(
                color = Color.White,
                radius = dotRadius + 1.5.dp.toPx(),
                center = Offset(cx, cy)
            )
            drawCircle(
                color = color,
                radius = dotRadius,
                center = Offset(cx, cy)
            )

            // X-axis label — show every 5th to avoid crowding
            if (index % 5 == 0 || index == data.lastIndex) {
                val labelMeasured = textMeasurer.measure(item.label, labelStyle)
                drawText(
                    labelMeasured,
                    topLeft = Offset(
                        (cx - labelMeasured.size.width / 2f).coerceIn(0f, size.width - labelMeasured.size.width.toFloat()),
                        chartHeight + 6.dp.toPx()
                    )
                )
            }

            // ── Tooltip on selected point ─────────────────────
            if (isSelected && animProgress.value == 1f) {
                val tooltipText = "${item.label}: ${item.value.toInt()}%"
                val tooltipMeasured = textMeasurer.measure(tooltipText, tooltipStyle)
                val tPadH = 8.dp.toPx()
                val tPadV = 4.dp.toPx()
                val tWidth = tooltipMeasured.size.width + tPadH * 2
                val tHeight = tooltipMeasured.size.height + tPadV * 2
                val tLeft = (cx - tWidth / 2f).coerceIn(0f, size.width - tWidth)
                val tTop = (cy - tHeight - 10.dp.toPx()).coerceAtLeast(0f)

                drawRoundRect(
                    color = color,
                    topLeft = Offset(tLeft, tTop),
                    size = Size(tWidth, tHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                )
                drawText(tooltipMeasured, topLeft = Offset(tLeft + tPadH, tTop + tPadV))
            }
        }
    }
}