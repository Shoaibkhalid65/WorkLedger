package com.example.progresstracker.ui.dailyTask.tasksList

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.progresstracker.model.DailyTask
import com.example.progresstracker.navigation.Screen
import com.example.progresstracker.utils.DateTimeUtils

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
                TasksListUiEvent.NavigateToCreateTask -> {
                    navController.navigate(Screen.CreateEditTaskScreen.route)
                }

                is TasksListUiEvent.NavigateToEditTask -> {
                    navController.navigate(Screen.CreateEditTaskScreen.createRouteWithId(event.taskId))
                }

                is TasksListUiEvent.Success -> {
                    snackbarHostState.showSnackbar(message = event.message)
                }

                is TasksListUiEvent.Error -> {
                    snackbarHostState.showSnackbar(message = event.message)
                }
            }
        }
    }


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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 80.dp),
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Box {
                        IconButton(
                            onClick = {
                                viewModel.updateShowSortDropDown(true)
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.Sort,
                                contentDescription = "sort the tasks"
                            )
                        }

                        DropdownMenu(
                            expanded = uiState.showSortDropDown,
                            onDismissRequest = {
                                viewModel.updateShowSortDropDown(false)
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            SortOption.entries.forEach { sortOption ->
                                val isSelected = uiState.sortOption == sortOption
                                DropdownMenuItem(
                                    selected = isSelected,
                                    onClick = {
                                        viewModel.updateSortOption(sortOption)
                                        viewModel.updateShowSortDropDown(false)
                                    },
                                    text = {
                                        Text(
                                            sortOption.text
                                        )
                                    },
                                    shapes = MenuDefaults.itemShapes(shape = RoundedCornerShape(8.dp)),
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


                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SelectionOption.entries.forEach { selectionOption ->
                        FilterChip(
                            selected = selectionOption == uiState.selectionOption,
                            onClick = {
                                viewModel.updateSelectionOption(selectionOption)
                            },
                            label = {
                                Text(
                                    text = selectionOption.text
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }
            }

            if (uiState.tasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .fillParentMaxHeight(0.7f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(
                                16.dp,
                                Alignment.CenterVertically
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.ImageNotSupported,
                                contentDescription = "No daily task available icon",
                                modifier = Modifier.size(72.dp),
                                tint = MaterialTheme.colorScheme.errorContainer
                            )
                            Text(
                                text = "No Daily Task Found!\nTap + button to create the new task",
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
                items(items = uiState.tasks, key = { it.id }) { dailyTask ->
                    DailyTaskItem(
                        dailyTask = dailyTask,
                        totalDurationFormatted = DateTimeUtils.millisToFormattedDuration(dailyTask.totalTaskDuration),
                        onEditClick = {
                            viewModel.onUpdateTaskClick(it)
                        },
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
    }

    if (uiState.showDeleteAllDialog) {
        DeleteDailyTaskDialog(
            onDismiss = {
                viewModel.updateShowDeleteAllDialog(false)
            }
        ) {
            viewModel.deleteAllDailyTasks()
        }
    }

    if (uiState.showDeleteDialog) {
        DeleteDailyTaskDialog(
            onDismiss = {
                viewModel.updateShowDeleteDialog(false)
                viewModel.updateTaskToDeleteId(-1L)
            }
        ) {
            val taskId = uiState.taskToDeleteId

            if (taskId != -1L) {
                viewModel.deleteDailyTask(taskId)
            }
        }
    }
}


@Composable
fun DailyTaskItem(
    dailyTask: DailyTask,
    totalDurationFormatted: String,
    onEditClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit,
    formatedDate: (Long, Boolean) -> String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dailyTask.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = dailyTask.description,
                fontSize = 15.sp
            )
            if (dailyTask.remarks.isNotEmpty()) {
                Text(
                    text = "Remarks: ${dailyTask.remarks}",
                    fontSize = 14.sp,
                    color = androidx.compose.ui.graphics.Color.Gray
                )
            }

            LinearProgressIndicator(
                progress = {
                    dailyTask.satisfyPercentage.text.div(100f)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Text(
                text = "Total ${dailyTask.durations.size} duration instances",
                fontSize = 14.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Work time:",
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = totalDurationFormatted,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color(0xFF4CAF50)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = formatedDate(dailyTask.englishDate, false),
                    fontSize = 12.sp
                )
                Text(
                    text = DateTimeUtils.calculateIslamicDate(dailyTask.englishDate),
                    fontSize = 12.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = {
                        onEditClick(dailyTask.id)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit"
                    )
                }

                IconButton(
                    onClick = {
                        onDeleteClick(dailyTask.id)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }
        }
    }
}


@Composable
fun DeleteDailyTaskDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                }
            ) {
                Text(
                    text = "Confirm"
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(
                    text = "Cancel"
                )
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.DeleteForever,
                contentDescription = "delete icon"
            )
        },
        text = {
            Text(
                text = "Do you want to delete the daily task(s)? This action cannot be undone."
            )
        },
        title = {
            Text(
                text = "Delete Task(s)"
            )
        }
    )
}
