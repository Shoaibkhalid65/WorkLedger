package com.gshoaib998.progressly.ui.dailyTask.tasksList

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TaskAlt
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
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.gshoaib998.progressly.model.DailyTask
import com.gshoaib998.progressly.model.GoalBadgeColor
import com.gshoaib998.progressly.navigation.Screen
import com.gshoaib998.progressly.ui.components.GoalBadge
import com.gshoaib998.progressly.utils.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TasksListScreen(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
    viewModel: TasksListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                TasksListUiEvent.NavigateToCreateTask ->
                    navController.navigate(Screen.CreateEditTaskScreen.route)

                is TasksListUiEvent.NavigateToEditTask ->
                    navController.navigate(Screen.CreateEditTaskScreen.createRouteWithId(event.taskId))

                is TasksListUiEvent.Success ->
                    snackbarHostState.showSnackbar(event.message)

                is TasksListUiEvent.Error ->
                    snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularWavyProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp, top = 12.dp, bottom = 100.dp
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ── Search + Sort ─────────────────────────────────────────────
        item {
            TasksSearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                sortOption = uiState.sortOption,
                showDropDown = uiState.showSortDropDown,
                onSortClick = { viewModel.updateShowSortDropDown(true) },
                onSortDismiss = { viewModel.updateShowSortDropDown(false) },
                onSortOptionSelected = {
                    viewModel.updateSortOption(it)
                    viewModel.updateShowSortDropDown(false)
                }
            )
        }

        // ── Filter chips ──────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SelectionOption.entries.forEach { selectionOption ->
                    FilterChip(
                        selected = selectionOption == uiState.selectionOption,
                        onClick = { viewModel.updateSelectionOption(selectionOption) },
                        label = {
                            Text(
                                text = selectionOption.text,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectionOption == uiState.selectionOption,
                            borderColor = MaterialTheme.colorScheme.outlineVariant,
                            selectedBorderColor = Color.Transparent,
                            borderWidth = 0.5.dp
                        )
                    )
                }
            }
        }

        // ── Empty state ───────────────────────────────────────────────
        if (uiState.tasks.isEmpty()) {
            item {
                TasksEmptyState()
            }
        } else {
            items(items = uiState.tasks, key = { it.id }) { dailyTask ->
                DailyTaskItem(
                    dailyTask = dailyTask,
                    totalDurationFormatted = DateTimeUtils.millisToFormattedDuration(
                        dailyTask.totalTaskDuration
                    ),
                    onEditClick = { viewModel.onUpdateTaskClick(it) },
                    onDeleteClick = {
                        viewModel.updateShowDeleteDialog(true)
                        viewModel.updateTaskToDeleteId(it)
                    },
                    formatedDate = { millis, isOnlyDate ->
                        DateTimeUtils.formatedDate(millis, isOnlyDate)
                    }
                )
            }
        }
    }

    // ── Dialogs ───────────────────────────────────────────────────────
    if (uiState.showDeleteAllDialog) {
        DeleteDailyTaskDialog(
            title = "Delete All Tasks?",
            text = "All daily tasks will be permanently deleted. This action cannot be undone.",
            onDismiss = { viewModel.updateShowDeleteAllDialog(false) },
            onConfirm = { viewModel.deleteAllDailyTasks() }
        )
    }

    if (uiState.showDeleteDialog) {
        DeleteDailyTaskDialog(
            title = "Delete Task?",
            text = "This task will be permanently deleted. This action cannot be undone.",
            onDismiss = {
                viewModel.updateShowDeleteDialog(false)
                viewModel.updateTaskToDeleteId(-1L)
            },
            onConfirm = {
                val taskId = uiState.taskToDeleteId
                if (taskId != -1L) viewModel.deleteDailyTask(taskId)
            }
        )
    }
}

// ── Search Bar ────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TasksSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    sortOption: SortOption,
    showDropDown: Boolean,
    onSortClick: () -> Unit,
    onSortDismiss: () -> Unit,
    onSortOptionSelected: (SortOption) -> Unit
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
            placeholder = {
                Text("Search tasks...", style = MaterialTheme.typography.bodyMedium)
            },
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
                    IconButton(
                        onClick = { onQueryChange("") },
                        modifier = Modifier.size(20.dp)
                    ) {
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
                onClick = onSortClick,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
            ) {
                Icon(Icons.AutoMirrored.Default.Sort, contentDescription = "Sort")
            }
            DropdownMenu(
                expanded = showDropDown,
                onDismissRequest = onSortDismiss,
                shape = RoundedCornerShape(14.dp)
            ) {
                SortOption.entries.forEach { option ->
                    val isSelected = sortOption == option
                    DropdownMenuItem(
                        text = {
                            Text(
                                option.text,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        onClick = { onSortOptionSelected(option) },
                        selected = isSelected,
                        shapes = MenuDefaults.itemShapes(),
                        leadingIcon = {
                            if (isSelected) Icon(Icons.Default.Check, contentDescription = null)
                        }
                    )
                }
            }
        }
    }
}

// ── Empty State ───────────────────────────────────────────────────────────────

@Composable
fun TasksEmptyState() {
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
                        imageVector = Icons.Default.TaskAlt,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = "No Tasks Yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Tap the button below to log\nyour first task for today",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Daily Task Item ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DailyTaskItem(
    dailyTask: DailyTask,
    totalDurationFormatted: String,
    onEditClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit,
    formatedDate: (Long, Boolean) -> String
) {
    val satisfyValue = dailyTask.satisfyPercentage.text
    val accentColor = when {
        satisfyValue >= 80 -> MaterialTheme.colorScheme.primary
        satisfyValue >= 50 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }
    val badgeColor = when {
        satisfyValue >= 80 -> GoalBadgeColor.GREEN
        satisfyValue >= 50 -> GoalBadgeColor.AMBER
        else -> GoalBadgeColor.RED
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            topStart = 0.dp, bottomStart = 0.dp,
            topEnd = 14.dp, bottomEnd = 14.dp
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Colored left accent bar
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
                // ── Top row: badge + actions ──────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GoalBadge(
                        label = "$satisfyValue% Satisfied",
                        color = badgeColor
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilledTonalIconButton(
                            onClick = { onEditClick(dailyTask.id) },
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
                            onClick = { onDeleteClick(dailyTask.id) },
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

                // ── Title ─────────────────────────────────────────────
                Text(
                    text = dailyTask.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (dailyTask.description.isNotBlank()) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = dailyTask.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (dailyTask.remarks.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = dailyTask.remarks,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontStyle = FontStyle.Italic,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(12.dp))

                // ── Satisfaction progress bar ─────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Satisfaction",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$satisfyValue%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = accentColor
                        )
                    }
                    LinearProgressIndicator(
                        progress = { satisfyValue / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp),
                        strokeCap = StrokeCap.Round,
                        color = accentColor,
                        trackColor = accentColor.copy(alpha = 0.12f)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // ── Work time chip ────────────────────────────────────
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = totalDurationFormatted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // ── Bottom row: dates ─────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatedDate(dailyTask.englishDate, false),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = DateTimeUtils.calculateIslamicDate(dailyTask.englishDate),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

// ── Delete Dialog ─────────────────────────────────────────────────────────────

@Composable
fun DeleteDailyTaskDialog(
    title: String = "Delete Task(s)?",
    text: String = "Do you want to delete the task(s)? This action cannot be undone.",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = { onConfirm(); onDismiss() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) { Text("Delete") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        },
        icon = {
            Icon(
                Icons.Default.DeleteForever,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text(title, fontWeight = FontWeight.SemiBold) },
        text = { Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        shape = RoundedCornerShape(20.dp)
    )
}
