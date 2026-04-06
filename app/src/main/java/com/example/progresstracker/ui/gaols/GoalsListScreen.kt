package com.example.progresstracker.ui.gaols

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.progresstracker.model.Goal
import com.example.progresstracker.model.GoalBadgeColor
import com.example.progresstracker.model.badgeColor
import com.example.progresstracker.model.toLabel
import com.example.progresstracker.navigation.Screen
import com.example.progresstracker.ui.components.GoalBadge
import com.example.progresstracker.utils.DateTimeUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GoalsListScreen(
    navHostController: NavHostController,
    goalsListViewModel: GoalsListViewModel = hiltViewModel(),
) {
    val uiState by goalsListViewModel.goalsUiState.collectAsStateWithLifecycle()

    val tabs = remember { listOf("All", "Pending", "Completed") }
    val pagerState = rememberPagerState { 3 }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val density = LocalDensity.current
    val fabOffsetPx = with(density) { 45.dp.toPx() }
    val snackbarOffset = with(density) { 80.dp.toPx() }

    val fabOffset by animateFloatAsState(
        targetValue = if (snackbarHostState.currentSnackbarData == null) 0f else -fabOffsetPx,
        animationSpec = tween(300),
        label = "fab offset"
    )

    LaunchedEffect(Unit) {
        goalsListViewModel.events.collect {
            when (it) {
                is GoalsUiEvent.Success -> {
                    snackbarHostState.showSnackbar(it.data)
                }

                is GoalsUiEvent.Error -> {
                    snackbarHostState.showSnackbar(it.errorMessage)
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
                },
                modifier = Modifier.graphicsLayer {
                    translationY = fabOffset
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "add goal"
                )
            }
        },
        contentWindowInsets = WindowInsets(),
        snackbarHost = {
            SnackbarHost(
                snackbarHostState,
                snackbar = { snackbarData ->
                    Snackbar(
                        snackbarData,
                    )
                },
                modifier = Modifier.graphicsLayer {
                    translationY = snackbarOffset
                }
            )
        }
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
                        contentPadding = PaddingValues(top = 12.dp, start = 12.dp, end = 12.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
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
                        if (uiState.allGoals.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillParentMaxWidth()
                                        .fillParentMaxHeight(0.7f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ImageNotSupported,
                                            contentDescription = "No goals available icon",
                                            modifier = Modifier.size(72.dp),
                                            tint = MaterialTheme.colorScheme.errorContainer
                                        )
                                        Text(
                                            text = "No Goals Found!\nTap + button to create the new goal",
                                            color = MaterialTheme.colorScheme.errorContainer,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        } else {

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
                                        DateTimeUtils.formatedDate(millis, isOnlyDate)
                                    }
                                )

                            }
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

@Composable
fun GoalItem(
    goal: Goal,
    onDeleteGoal: () -> Unit,
    onEditGoal: () -> Unit,
    isCompleted: Boolean,
    onToggleChanged: (Boolean) -> Unit,
    formatedDate: (Long, Boolean) -> String
) {
    // Left border color: green for completed, amber for pending
    val accentColor = if (isCompleted) Color(0xFF3B6D11) else Color(0xFFBA7517)

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(
            topStart = 0.dp, bottomStart = 0.dp,
            topEnd = 12.dp, bottomEnd = 12.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        // Colored left accent bar
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(accentColor)
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .fillMaxWidth()
            ) {

                // ── Top row: status badge + action icons ──────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GoalBadge(
                        label = if (isCompleted) "Completed" else "Pending",
                        color = if (isCompleted) GoalBadgeColor.GREEN else GoalBadgeColor.AMBER
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = onEditGoal,
                            modifier = Modifier
                                .border(
                                    0.5.dp,
                                    MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(8.dp)
                                )
                                .size(32.dp),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit goal",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(
                            onClick = onDeleteGoal,
                            modifier = Modifier
                                .border(
                                    0.5.dp,
                                    MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(8.dp)
                                )
                                .size(32.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete goal",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // ── Title + description ───────────────────────────────
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (goal.description.isNotBlank()) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = goal.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(10.dp))

                // ── Priority badges ───────────────────────────────────
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    GoalBadge(
                        label = goal.difficultyLevel.toLabel(),
                        color = goal.difficultyLevel.badgeColor()
                    )
                    GoalBadge(
                        label = goal.importanceLevel.toLabel(),
                        color = goal.importanceLevel.badgeColor()
                    )
                    GoalBadge(
                        label = goal.urgencyLevel.toLabel(),
                        color = goal.urgencyLevel.badgeColor()
                    )
                }

                Spacer(Modifier.height(10.dp))

                // ── Date chip ─────────────────────────────────────────
                val dateLabel = when {
                    isCompleted && goal.completionDate != 0L ->
                        "Completed ${formatedDate(goal.completionDate, true)}"
                    goal.expectedCompletionDate != 0L ->
                        "Due ${formatedDate(goal.expectedCompletionDate, true)}"
                    else -> null
                }

                dateLabel?.let {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = it,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // ── Divider ───────────────────────────────────────────
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // ── Bottom row: created date + toggle ─────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Created ${formatedDate(goal.createdAt, true)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Mark complete",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Switch(
                            checked = isCompleted,
                            onCheckedChange = onToggleChanged,
                            modifier = Modifier.scale(0.75f)  // compact size
                        )
                    }
                }
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
