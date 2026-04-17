package com.gshoaib998.progressly.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Task
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomBarDestination(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector
) {
    DailyTaskMainScreen(
        route = Screen.DailyTaskScreen.route,
        title = "Daily Tasks",
        selectedIcon = Icons.Default.Task,
        unSelectedIcon = Icons.Outlined.Task
    ),
    Dashboard(                                      // NEW — center
        route = Screen.DashboardScreen.route,
        title = "Dashboard",
        selectedIcon = Icons.Default.Dashboard,
        unSelectedIcon = Icons.Outlined.Dashboard
    ),
    GoalMainScreen(
        route = Screen.GoalScreen.route,
        title = "Goals",
        selectedIcon = Icons.Default.EmojiEvents,
        unSelectedIcon = Icons.Outlined.EmojiEvents
    )
}
