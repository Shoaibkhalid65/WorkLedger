package com.example.progresstracker.ui.dailyTask

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.progresstracker.ui.dailyTask.taskDuration.TaskDurationScreen
import com.example.progresstracker.ui.dailyTask.taskDuration.TaskDurationViewModel
import com.example.progresstracker.ui.dailyTask.tasksList.TasksListScreen
import com.example.progresstracker.ui.dailyTask.tasksList.TasksListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyTaskMainScreen(
    navController: NavHostController,
    tasksListViewModel: TasksListViewModel = hiltViewModel(),
    taskDurationViewModel: TaskDurationViewModel = hiltViewModel()
) {
    val titles = listOf("Tasks", "Durations")
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { titles.size }
    val snackbarHostState = remember { SnackbarHostState() }

    val isTasksScreen = pagerState.currentPage == 0
    val density = LocalDensity.current
    val fabOffsetPx = with(density) { 45.dp.toPx() }

    val fabOffset by animateFloatAsState(
        targetValue = if (snackbarHostState.currentSnackbarData == null) 0f else -fabOffsetPx,
        animationSpec = tween(300),
        label = "fab offset"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Crossfade(targetState = isTasksScreen, label = "title") { isTasks ->
                        Text(
                            text = if (isTasks) "My Tasks" else "My Durations",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (isTasksScreen) tasksListViewModel.updateShowDeleteAllDialog(true)
                            else taskDurationViewModel.updateShowAllDeletionDialog(true)
                        }
                    ) {
                        Icon(
                            Icons.Default.DeleteSweep,
                            contentDescription = "Delete all",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                windowInsets = WindowInsets(0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (isTasksScreen) tasksListViewModel.onCreateTaskClick()
                    else taskDurationViewModel.updateShowCreationDialog(true)
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = {
                    Crossfade(targetState = isTasksScreen, label = "fab text") { isTasks ->
                        Text(
                            text = if (isTasks) "New Task" else "Track Duration",
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                modifier = Modifier.graphicsLayer { translationY = fabOffset },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        },
        contentWindowInsets = WindowInsets(0),
        snackbarHost = {
            SnackbarHost(
                snackbarHostState,
                modifier = Modifier.graphicsLayer {
                    translationY = with(density) { 80.dp.toPx() }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Use PrimaryTabRow (fixed, not scrollable) to match GoalsListScreen
            PrimaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                titles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(title, fontWeight = FontWeight.Medium) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { index ->
                when (index) {
                    0 -> TasksListScreen(snackbarHostState, navController)
                    1 -> TaskDurationScreen(snackbarHostState)
                }
            }
        }
    }
}