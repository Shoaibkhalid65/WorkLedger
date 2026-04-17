package com.gshoaib998.progressly.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SchemeDonut(
    colors: List<Color>,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    strokeWidth: Dp = 14.dp,
    isSelected: Boolean = false,
    selectedRingColor: Color = Color.Unspecified
) {
    val sliceCount = colors.size
    val sweepAngle = 360f / sliceCount
    val gapAngle = 3f  // small gap between slices

    Canvas(modifier = modifier.size(size)) {
        val canvasSize = this.size
        val stroke = strokeWidth.toPx()
        val radius = (canvasSize.minDimension - stroke) / 2f
        val topLeft = Offset(
            x = (canvasSize.width - radius * 2) / 2f,
            y = (canvasSize.height - radius * 2) / 2f
        )
        val arcSize = Size(radius * 2, radius * 2)

        colors.forEachIndexed { index, color ->
            val startAngle = index * sweepAngle + gapAngle / 2f - 90f
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle - gapAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke)
            )
        }

        // Selection ring drawn outside the donut
        if (isSelected && selectedRingColor != Color.Unspecified) {
            val ringRadius = radius + stroke / 2f + 3.dp.toPx()
            val ringTopLeft = Offset(
                x = (canvasSize.width - ringRadius * 2) / 2f,
                y = (canvasSize.height - ringRadius * 2) / 2f
            )
            drawArc(
                color = selectedRingColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = ringTopLeft,
                size = Size(ringRadius * 2, ringRadius * 2),
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}

