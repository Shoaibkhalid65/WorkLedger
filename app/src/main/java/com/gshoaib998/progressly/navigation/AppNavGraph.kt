package com.gshoaib998.progressly.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gshoaib998.progressly.ui.dailyTask.DailyTaskMainScreen
import com.gshoaib998.progressly.ui.dashboard.DashboardScreen
import com.gshoaib998.progressly.ui.gaols.GoalsListScreen
import com.gshoaib998.progressly.ui.goalcreation.CreateEditGoalScreen
import com.gshoaib998.progressly.ui.onboarding.OnboardingScreen
import com.gshoaib998.progressly.ui.settings.AppearanceSettingsScreen
import com.gshoaib998.progressly.ui.taskcreation.CreateEditTaskScreen

@Composable
fun AppNavGraph(showOnboarding: Boolean=false) {
    val navHostController = rememberNavController()
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val bottomBarRoutes =
        setOf(Screen.GoalScreen.route, Screen.DashboardScreen.route, Screen.DailyTaskScreen.route)
    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        bottomBar = {
            Box(modifier = Modifier.animateContentSize()) {
                AnimatedVisibility(
                    visible = showBottomBar,
                    enter = EnterTransition.None,
                    exit = ExitTransition.None
                ) {
                    AppBottomBar(navHostController, currentRoute)
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            navController = navHostController,
            startDestination = if(showOnboarding) Screen.OnboardingScreen.route else Screen.DailyTaskScreen.route
        ) {

            composable(Screen.OnboardingScreen.route) {
                OnboardingScreen(
                    onFinished = {
                        navHostController.navigate(Screen.DailyTaskScreen.route) {
                            popUpTo(Screen.OnboardingScreen.route) { inclusive = true }
                        }
                    }
                )
            }


            composable(Screen.DailyTaskScreen.route) {
                DailyTaskMainScreen(navHostController)
            }


            composable(Screen.CreateEditTaskScreen.route) {
                CreateEditTaskScreen(navHostController)
            }

            composable(
                route = Screen.CreateEditTaskScreen.routeWithArgs,
                arguments = listOf(navArgument("taskId") {
                    type = NavType.LongType
                })
            ) {
                CreateEditTaskScreen(navHostController)
            }



            composable(Screen.GoalScreen.route) {
                GoalsListScreen(navHostController)
            }
            composable(Screen.CreateEditGoalScreen.route) {
                CreateEditGoalScreen(navHostController)
            }
            composable(
                Screen.CreateEditGoalScreen.routeWithArgs,
                arguments = listOf(navArgument("goalId") { type = NavType.LongType })
            ) {
                CreateEditGoalScreen(navHostController)
            }

            composable(Screen.DashboardScreen.route) {
                DashboardScreen(navController = navHostController)
            }

            composable(Screen.AppearanceSettingsScreen.route) {
                AppearanceSettingsScreen(navController = navHostController)
            }
        }
    }
}

@Composable
fun AppBottomBar(navHostController: NavHostController, currentRoute: String?) {
    BottomAppBar(
        modifier = Modifier.fillMaxWidth()
    ) {
        BottomBarDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = {
                    navHostController.navigate(destination.route) {
                        popUpTo(navHostController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (currentRoute == destination.route) destination.selectedIcon else destination.unSelectedIcon,
                        contentDescription = "bottom bar item icon"
                    )
                },
                label = {
                    Text(
                        text = destination.title,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                alwaysShowLabel = false

            )
        }
    }
}

