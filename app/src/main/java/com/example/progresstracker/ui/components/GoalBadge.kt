package com.example.progresstracker.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.progresstracker.model.GoalBadgeColor

@Composable
fun GoalBadge(label: String, color: GoalBadgeColor) {
    val (containerColor, contentColor) = when (color) {
        GoalBadgeColor.RED   -> Color(0xFFFCEBEB) to Color(0xFFA32D2D)
        GoalBadgeColor.AMBER -> Color(0xFFFAEEDA) to Color(0xFF854F0B)
        GoalBadgeColor.GREEN -> Color(0xFFEAF3DE) to Color(0xFF3B6D11)
        GoalBadgeColor.BLUE  -> Color(0xFFE6F1FB) to Color(0xFF185FA5)
        GoalBadgeColor.GRAY  -> Color(0xFFF1EFE8) to Color(0xFF5F5E5A)
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