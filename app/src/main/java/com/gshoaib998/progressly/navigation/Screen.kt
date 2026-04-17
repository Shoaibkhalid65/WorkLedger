package com.gshoaib998.progressly.navigation

sealed class Screen(val route: String) {
    object OnboardingScreen : Screen("onboarding")
    object DailyTaskScreen : Screen("daily_task")
    object GoalScreen : Screen("goal")
    object CreateEditTaskScreen : Screen("create_edit_task") {
        fun createRouteWithId(taskId: Long) = "$route/$taskId"

        const val routeWithArgs = "create_edit_task/{taskId}"
    }

    object CreateEditGoalScreen : Screen("create_edit_goal") {
        fun createRouteWithId(goalId: Long): String = "$route/$goalId"

        const val routeWithArgs = "create_edit_goal/{goalId}"
    }

    object DashboardScreen : Screen("dashboard")

    object AppearanceSettingsScreen : Screen("appearance_settings")

}