package com.gshoaib998.progressly.ui.gaols

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.gshoaib998.progressly.model.Goal
import com.gshoaib998.progressly.model.GoalBadgeColor
import com.gshoaib998.progressly.model.badgeColor
import com.gshoaib998.progressly.model.toLabel
import com.gshoaib998.progressly.navigation.Screen
import com.gshoaib998.progressly.ui.components.GoalBadge
import com.gshoaib998.progressly.utils.DateTimeUtils
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
    val density = androidx.compose.ui.platform.LocalDensity.current
    val fabOffsetPx = with(density) { 45.dp.toPx() }

    val fabOffset by animateFloatAsState(
        targetValue = if (snackbarHostState.currentSnackbarData == null) 0f else -fabOffsetPx,
        animationSpec = tween(300),
        label = "fab offset"
    )

    LaunchedEffect(Unit) {
        goalsListViewModel.events.collect {
            when (it) {
                is GoalsUiEvent.Success -> snackbarHostState.showSnackbar(it.data)
                is GoalsUiEvent.Error -> snackbarHostState.showSnackbar(it.errorMessage)
                is GoalsUiEvent.NavigateToCreateScreen ->
                    navHostController.navigate(Screen.CreateEditGoalScreen.route)

                is GoalsUiEvent.NavigateToEditScreen ->
                    navHostController.navigate(Screen.CreateEditGoalScreen.createRouteWithId(it.goalId))

                is GoalsUiEvent.ShowDeleteDialog -> {
                    goalsListViewModel.updateGoalToDeleteId(it.goalId)
                    goalsListViewModel.updateShowDeleteDialog(true)
                }

                is GoalsUiEvent.ShowDeleteAllDialog ->
                    goalsListViewModel.updateShowDeleteAllDialog(true)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Goals",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = { goalsListViewModel.onDeleteAllGoalsClick() }) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Delete all goals",
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
                onClick = { goalsListViewModel.onCreateGoalClick() },
                modifier = Modifier.graphicsLayer { translationY = fabOffset },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add goal") },
                text = { Text("New Goal") },
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
            // ── Tabs ──────────────────────────────────────────────────
            PrimaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, string ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(string, fontWeight = FontWeight.Medium) }
                    )
                }
            }

            HorizontalPager(state = pagerState) { pageIndex ->
                if (uiState.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularWavyProgressIndicator()
                    }
                } else {
                    val goals = when (pageIndex) {
                        0 -> uiState.allGoals
                        1 -> uiState.pendingGoals
                        2 -> uiState.completedGoals
                        else -> emptyList()
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = 12.dp, start = 16.dp, end = 16.dp, bottom = 100.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        // ── Search + Sort ─────────────────────────────
                        item {
                            GoalsSearchBar(
                                query = uiState.searchQuery,
                                onQueryChange = { goalsListViewModel.updateSearchQuery(it) },
                                sortOption = uiState.sortOption,
                                showDropDown = uiState.showSortDropDown,
                                onSortClick = { goalsListViewModel.updateShowSortDropDown(true) },
                                onSortDismiss = { goalsListViewModel.updateShowSortDropDown(false) },
                                onSortOptionUpdated = { goalsListViewModel.updateSortOption(it) }
                            )
                        }

                        // ── Stats chip ────────────────────────────────
                        if (goals.isNotEmpty()) {
                            item {
                                GoalStatsRow(
                                    total = uiState.allGoals.size,
                                    pending = uiState.pendingGoals.size,
                                    completed = uiState.completedGoals.size
                                )
                            }
                        }

                        // ── Empty state ───────────────────────────────
                        if (goals.isEmpty()) {
                            item {
                                GoalsEmptyState(
                                    onCreateClick = { goalsListViewModel.onCreateGoalClick() }
                                )
                            }
                        } else {
                            items(items = goals, key = { it.id }) { goal ->
                                GoalItem(
                                    goal = goal,
                                    onDeleteGoal = { goalsListViewModel.onDeleteGoalClick(goal.id) },
                                    onEditGoal = { goalsListViewModel.onEditGoalClick(goal.id) },
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
            title = "Delete Goal?",
            text = "This goal will be permanently deleted. This action cannot be undone.",
            onDismissClick = {
                goalsListViewModel.updateShowDeleteDialog(false)
                goalsListViewModel.updateGoalToDeleteId(-1L)
            }
        ) {
            if (uiState.goalToDeleteId != -1L) goalsListViewModel.deleteGoal(uiState.goalToDeleteId)
        }
    }

    if (uiState.showDeleteAllDialog) {
        DeleteDialog(
            title = "Delete All Goals?",
            text = "All goals will be permanently deleted. This action cannot be undone.",
            onDismissClick = { goalsListViewModel.updateShowDeleteAllDialog(false) }
        ) {
            goalsListViewModel.deleteAllGoals()
        }
    }
}

// ── Search Bar ────────────────────────────────────────────────────────────────

@Composable
fun GoalsSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    sortOption: GoalSortOption,
    showDropDown: Boolean,
    onSortClick: () -> Unit,
    onSortDismiss: () -> Unit,
    onSortOptionUpdated: (GoalSortOption) -> Unit
) {
    val focusManager = LocalFocusManager.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            placeholder = { Text("Search goals...", style = MaterialTheme.typography.bodyMedium) },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }, modifier = Modifier.size(20.dp)) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Clear",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Box(modifier = Modifier.fillMaxHeight()) {
            FilledTonalIconButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f),
                onClick = onSortClick,
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.AutoMirrored.Default.Sort, contentDescription = "Sort")
            }
            GoalsSortDropDown(
                sortOption = sortOption,
                showDropDown = showDropDown,
                onDismiss = onSortDismiss,
                onSortOptionUpdated = onSortOptionUpdated
            )
        }
    }
}

// ── Stats Row ─────────────────────────────────────────────────────────────────

@Composable
fun GoalStatsRow(total: Int, pending: Int, completed: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatChip("Total", "$total", Modifier.weight(1f))
        StatChip("Pending", "$pending", Modifier.weight(1f))
        StatChip("Done", "$completed", Modifier.weight(1f))
    }
}

@Composable
fun StatChip(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Empty State ───────────────────────────────────────────────────────────────

@Composable
fun GoalsEmptyState(onCreateClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = "No Goals Yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Start by setting your first goal\nand track your progress",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            FilledTonalButton(onClick = onCreateClick, shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Create Goal")
            }
        }
    }
}

// ── Goal Item (polished version of existing good design) ─────────────────────

@Composable
fun GoalItem(
    goal: Goal,
    onDeleteGoal: () -> Unit,
    onEditGoal: () -> Unit,
    isCompleted: Boolean,
    onToggleChanged: (Boolean) -> Unit,
    formatedDate: (Long, Boolean) -> String
) {
    val accentColor = if (isCompleted)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.tertiary
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            topStart = 0.dp, bottomStart = 0.dp, topEnd = 14.dp, bottomEnd = 14.dp
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp)
                    .background(accentColor)
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GoalBadge(
                        label = if (isCompleted) "Completed" else "Pending",
                        color = if (isCompleted) GoalBadgeColor.GREEN else GoalBadgeColor.AMBER
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilledTonalIconButton(
                            onClick = onEditGoal,
                            modifier = Modifier.size(32.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        FilledTonalIconButton(
                            onClick = onDeleteGoal,
                            modifier = Modifier.size(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

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

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

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
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Mark complete",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Switch(
                            checked = isCompleted,
                            onCheckedChange = onToggleChanged,
                            modifier = Modifier.scale(0.72f)
                        )
                    }
                }
            }
        }
    }
}

// ── Delete Dialog ─────────────────────────────────────────────────────────────

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
            Button(
                onClick = { onDeleteClick(); onDismissClick() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) { Text("Delete") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismissClick) { Text("Cancel") }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.DeleteForever,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text(text = title, fontWeight = FontWeight.SemiBold) },
        text = { Text(text = text, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        shape = RoundedCornerShape(20.dp)
    )
}

// ── Sort Dropdown ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GoalsSortDropDown(
    sortOption: GoalSortOption,
    showDropDown: Boolean,
    onDismiss: () -> Unit,
    onSortOptionUpdated: (GoalSortOption) -> Unit
) {
    DropdownMenu(expanded = showDropDown, onDismissRequest = onDismiss,shape = RoundedCornerShape(14.dp)) {
        GoalSortOption.entries.forEach { option ->
            val isSelected = sortOption == option
            DropdownMenuItem(
                text = {
                    Text(
                        option.text,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                onClick = { onSortOptionUpdated(option); onDismiss() },
                selected = isSelected,
                shapes = MenuDefaults.itemShapes(),
                leadingIcon = {
                    if (isSelected) Icon(Icons.Default.Check, contentDescription = null)
                }
            )
        }
    }
}
