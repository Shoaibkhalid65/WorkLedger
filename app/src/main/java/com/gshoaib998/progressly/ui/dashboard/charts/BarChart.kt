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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BarChartData(
    val label: String,      // x-axis label e.g. "Mon"
    val value: Float,       // the actual value e.g. hours
    val unit: String = "h"  // shown in tooltip e.g. "2.5h"
)

@Composable
fun BarChart(
    data: List<BarChartData>,
    modifier: Modifier = Modifier,
    height: Dp = 200.dp,
    barColor: Color = MaterialTheme.colorScheme.primary,
    selectedBarColor: Color = MaterialTheme.colorScheme.tertiary,
    labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    gridColor: Color = MaterialTheme.colorScheme.outlineVariant,
) {
    if (data.isEmpty()) return

    val maxValue = data.maxOf { it.value }.coerceAtLeast(0.1f)
    val textMeasurer = rememberTextMeasurer()

    // Entry animation — bars grow from 0 to full height
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, animationSpec = tween(900))
    }

    var selectedIndex by remember { mutableIntStateOf(-1) }

    val labelStyle = TextStyle(
        fontSize = 11.sp,
        color = labelColor
    )
    val tooltipStyle = TextStyle(
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onPrimary
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .pointerInput(data) {
                detectTapGestures { offset ->
                    // Compute which bar was tapped
                    val labelAreaHeight = 24.dp.toPx()
                    val chartHeight = size.height - labelAreaHeight
                    val barAreaWidth = size.width / data.size
                    val tappedIndex = (offset.x / barAreaWidth).toInt()
                        .coerceIn(data.indices)
                    selectedIndex = if (selectedIndex == tappedIndex) -1 else tappedIndex
                }
            }
    ) {
        val labelAreaHeight = 24.dp.toPx()
        val chartHeight = size.height - labelAreaHeight
        val barAreaWidth = size.width / data.size
        val barWidth = barAreaWidth * 0.55f
        val horizontalPadding = (barAreaWidth - barWidth) / 2f

        // ── Grid lines (3 horizontal) ─────────────────────────
        val gridSteps = 3
        repeat(gridSteps + 1) { step ->
            val y = chartHeight * (1f - step.toFloat() / gridSteps)
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 0.5.dp.toPx()
            )
            // Value label on left side
            val gridValue = maxValue * step / gridSteps
            val gridLabel = "%.1f${data.first().unit}".format(gridValue)
            val measured = textMeasurer.measure(gridLabel, labelStyle)
            drawText(
                textLayoutResult = measured,
                topLeft = Offset(4.dp.toPx(), y - measured.size.height - 2.dp.toPx())
            )
        }

        // ── Bars ──────────────────────────────────────────────
        data.forEachIndexed { index, item ->
            val barHeight = ((item.value / maxValue) * chartHeight * animProgress.value).coerceAtLeast(0f)
            val left = index * barAreaWidth + horizontalPadding
            val top = chartHeight - barHeight
            val color = if (index == selectedIndex) selectedBarColor else barColor

            drawRoundRect(
                color = color,
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )

            // ── X-axis label ─────────────────────────────────
            val labelMeasured = textMeasurer.measure(item.label, labelStyle)
            drawText(
                textLayoutResult = labelMeasured,
                topLeft = Offset(
                    left + barWidth / 2f - labelMeasured.size.width / 2f,
                    chartHeight + 6.dp.toPx()
                )
            )

            // ── Tooltip above selected bar ────────────────────
            if (index == selectedIndex && animProgress.value == 1f) {
                val tooltipText = "%.2f${item.unit}".format(item.value)
                val tooltipMeasured = textMeasurer.measure(tooltipText, tooltipStyle)
                val tooltipPadH = 8.dp.toPx()
                val tooltipPadV = 4.dp.toPx()
                val tooltipWidth = tooltipMeasured.size.width + tooltipPadH * 2
                val tooltipHeight = tooltipMeasured.size.height + tooltipPadV * 2
                val tooltipLeft = (left + barWidth / 2f - tooltipWidth / 2f)
                    .coerceIn(0f, size.width - tooltipWidth)
                val tooltipTop = (top - tooltipHeight - 6.dp.toPx()).coerceAtLeast(0f)

                drawRoundRect(
                    color = color,
                    topLeft = Offset(tooltipLeft, tooltipTop),
                    size = Size(tooltipWidth, tooltipHeight),
                    cornerRadius = CornerRadius(6.dp.toPx())
                )
                drawText(
                    textLayoutResult = tooltipMeasured,
                    topLeft = Offset(
                        tooltipLeft + tooltipPadH,
                        tooltipTop + tooltipPadV
                    )
                )
            }
        }
    }
}

