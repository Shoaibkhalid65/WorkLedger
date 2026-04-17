package com.gshoaib998.progressly.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gshoaib998.progressly.model.GoalBadgeColor

@Composable
fun GoalBadge(label: String, color: GoalBadgeColor) {
    val containerColor = when (color) {
        GoalBadgeColor.RED   -> MaterialTheme.colorScheme.errorContainer
        GoalBadgeColor.AMBER -> MaterialTheme.colorScheme.tertiaryContainer
        GoalBadgeColor.GREEN -> MaterialTheme.colorScheme.primaryContainer
        GoalBadgeColor.BLUE  -> MaterialTheme.colorScheme.secondaryContainer
        GoalBadgeColor.GRAY  -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when (color) {
        GoalBadgeColor.RED   -> MaterialTheme.colorScheme.onErrorContainer
        GoalBadgeColor.AMBER -> MaterialTheme.colorScheme.onTertiaryContainer
        GoalBadgeColor.GREEN -> MaterialTheme.colorScheme.onPrimaryContainer
        GoalBadgeColor.BLUE  -> MaterialTheme.colorScheme.onSecondaryContainer
        GoalBadgeColor.GRAY  -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = label,
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}