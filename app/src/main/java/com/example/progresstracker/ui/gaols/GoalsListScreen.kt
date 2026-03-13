package com.example.progresstracker.ui.gaols

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.progresstracker.model.Goal
import com.example.progresstracker.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GoalsListScreen(
    navHostController: NavHostController,
    goalsListViewModel: GoalsListViewModel = hiltViewModel(),
) {

    val uiState by goalsListViewModel.goalsUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val tabs = remember { listOf("All", "Pending", "Completed") }

    val pagerState = rememberPagerState { 3 }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        goalsListViewModel.events.collect {
            when (it) {
                is GoalsUiEvent.Success -> {
                    showToast(context, "Success ${it.data}")
                }

                is GoalsUiEvent.Error -> {
                    showToast(context, "Error ${it.errorMessage}")
                }

                is GoalsUiEvent.NavigateToCreateScreen -> {
                    navHostController.navigate(Screen.CreateEditGoalScreen.route)
                }

                is GoalsUiEvent.NavigateToEditScreen -> {
                    navHostController.navigate(Screen.CreateEditGoalScreen.createRouteWithId(it.goalId))
                }

                is GoalsUiEvent.ShowDeleteDialog -> {
                    goalsListViewModel.updateGoalToDeleteId(it.goalId)
                    goalsListViewModel.updateShowDeleteDialog(true)
                }

                is GoalsUiEvent.ShowDeleteAllDialog -> {
                    goalsListViewModel.updateShowDeleteAllDialog(true)
                }
            }
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Goals"
                    )
                },
                actions = {
                    IconButton(
                        onClick = { goalsListViewModel.onDeleteAllGoalsClick() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "icon to delete all the goals"
                        )
                    }
                },
                windowInsets = WindowInsets()
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    goalsListViewModel.onCreateGoalClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "add goal"
                )
            }
        },
        contentWindowInsets = WindowInsets()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
                tabs.forEachIndexed { index, string ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = string
                            )
                        }
                    )
                }
            }
            HorizontalPager(
                state = pagerState
            ) { index ->
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator()
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    10.dp,
                                    Alignment.CenterHorizontally
                                )
                            ) {
                                TextField(
                                    value = uiState.searchQuery,
                                    onValueChange = {
                                        goalsListViewModel.updateSearchQuery(it)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Box(modifier = Modifier.clip(RoundedCornerShape(12.dp))) {
                                    IconButton(onClick = {
                                        goalsListViewModel.updateShowSortDropDown(true)
                                    }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Default.Sort,
                                            contentDescription = null
                                        )
                                    }

                                    GoalsSortDropDown(
                                        sortOption = uiState.sortOption,
                                        showDropDown = uiState.showSortDropDown,
                                        onDismiss = {
                                            goalsListViewModel.updateShowSortDropDown(false)
                                        },
                                        onSortOptionUpdated = {
                                            goalsListViewModel.updateSortOption(it)
                                        }
                                    )
                                }
                            }
                        }
                        items(
                            items = when (index) {
                                0 -> uiState.allGoals
                                1 -> uiState.pendingGoals
                                2 -> uiState.completedGoals
                                else -> emptyList()
                            },
                            key = { it.id }
                        ) { goal ->
                            GoalItem(
                                goal,
                                onDeleteGoal = {
                                    goalsListViewModel.onDeleteGoalClick(goal.id)
                                },
                                onEditGoal = {
                                    goalsListViewModel.onEditGoalClick(goal.id)
                                },
                                isCompleted = goal.isCompleted,
                                onToggleChanged = {
                                    goalsListViewModel.onIsCompletedToggleClick(goal, it)
                                },
                                formatedDate = { millis, isOnlyDate ->
                                    goalsListViewModel.formatedDate(millis, isOnlyDate)
                                }
                            )

                        }
                    }
                }
            }
        }
    }
    if (uiState.showDeleteDialog) {
        DeleteDialog(
            title = "Confirm Delete Goal",
            text = "Do you want to delete the goal permanently,this action can't be undone",
            onDismissClick = {
                goalsListViewModel.updateShowDeleteDialog(false)
                goalsListViewModel.updateGoalToDeleteId(-1L)
            }
        ) {
            if (uiState.goalToDeleteId != -1L) {
                goalsListViewModel.deleteGoal(uiState.goalToDeleteId)
            }
        }
    }
    if (uiState.showDeleteAllDialog) {
        DeleteDialog(
            title = "Confirm Delete All Goals",
            text = "Do you want to delete all the goals permanently,this action can't be undone",
            onDismissClick = {
                goalsListViewModel.updateShowDeleteAllDialog(false)
            }
        ) {
            goalsListViewModel.deleteAllGoals()
        }
    }

}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GoalItem(
    goal: Goal,
    onDeleteGoal: () -> Unit,
    onEditGoal: () -> Unit,
    isCompleted: Boolean,
    onToggleChanged: (Boolean) -> Unit,
    formatedDate: (Long, Boolean) -> String
) {
    val containerColor = if (goal.isCompleted) Color.Green else Color.Red
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor.copy(0.3f)
        )
    ) {
        Column(
            modifier = Modifier.wrapContentSize(),
            verticalArrangement = Arrangement.spacedBy(
                4.dp,
                Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = goal.title,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = goal.description,
                style = MaterialTheme.typography.bodyMedium
            )
            if (goal.isCompleted) {
                Text(
                    text = "Completion date : ${formatedDate(goal.completionDate, true)}"
                )
            } else if (goal.expectedCompletionDate != 0L) {
                Text(
                    text = "Expected Completion date : ${
                        formatedDate(
                            goal.expectedCompletionDate,
                            true
                        )
                    }"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Difficulty"
                    )
                    PriorityGraph(goal.difficultyLevel.ordinal, goal.difficultyLevel.name)
                }
                Column {
                    Text(
                        text = "Importance"
                    )
                    PriorityGraph(goal.importanceLevel.ordinal, goal.importanceLevel.name)
                }
                Column {
                    Text(
                        text = "Urgency"
                    )
                    PriorityGraph(goal.urgencyLevel.ordinal, goal.urgencyLevel.name)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        onEditGoal()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "delete goal"
                    )
                }

                IconButton(
                    onClick = {
                        onDeleteGoal()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "delete goal"
                    )
                }

            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ToggleButton(
                    checked = isCompleted,
                    onCheckedChange = {
                        onToggleChanged(it)
                    }
                ) {
                    Text(
                        text = "Is Goal Completed ?"
                    )
                }

            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = formatedDate(goal.createdAt, false)
                )
            }


        }
    }
}

@Composable
fun PriorityGraph(ordinal: Int, name: String) {
    val data = when (ordinal) {
        0 -> PriorityGraphData(Color.Red, 0.0f)
        1 -> PriorityGraphData(Color.Yellow, 0.33f)
        2 -> PriorityGraphData(Color.Green, 0.66f)
        else -> PriorityGraphData(Color.Transparent, 0.99f)
    }

    Column {
        Box(
            modifier = Modifier
                .size(width = 30.dp, height = 80.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, data.color),
                        startY = data.startY
                    ),
                    shape = RoundedCornerShape(2.dp)
                )
        )
        Text(
            text = name
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DeleteDialog(
    title: String,
    text: String,
    onDismissClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissClick,
        confirmButton = {
            Button(onClick = {
                onDeleteClick()
                onDismissClick()
            }) {
                Text(
                    text = "Confirm"
                )
            }
        },
        dismissButton = {
            Button(onClick = { onDismissClick() }) {
                Text(
                    text = "Cancel"
                )
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.DeleteForever,
                contentDescription = "delete goal dialog icon"
            )
        },
        title = {
            Text(
                text = title
            )
        },
        text = {
            Text(
                text = text
            )
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GoalsSortDropDown(
    sortOption: GoalSortOption,
    showDropDown: Boolean,
    onDismiss: () -> Unit,
    onSortOptionUpdated: (GoalSortOption) -> Unit
) {
    DropdownMenu(
        expanded = showDropDown,
        onDismissRequest = onDismiss
    ) {
        GoalSortOption.entries.forEach { option ->
            val isSelected = sortOption == option
            DropdownMenuItem(
                text = { Text(option.text) },
                onClick = {
                    onSortOptionUpdated(option)
                    onDismiss()
                },
                selected = isSelected,
                shapes = MenuDefaults.itemShapes(),
                leadingIcon = {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    }
}


data class PriorityGraphData(
    val color: Color,
    val startY: Float
)
