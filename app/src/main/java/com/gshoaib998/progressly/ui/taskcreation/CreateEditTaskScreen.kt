package com.gshoaib998.progressly.ui.taskcreation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.gshoaib998.progressly.model.SatisfyPercentage
import com.gshoaib998.progressly.navigation.Screen
import com.gshoaib998.progressly.utils.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditTaskScreen(
    navHostController: NavHostController,
    viewModel: CreateEditTaskViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                CreateEditTaskUiEvent.NavigateToTasksScreen -> {
                    navHostController.navigate(Screen.DailyTaskScreen.route) {
                        popUpTo(Screen.CreateEditTaskScreen.route) { inclusive = true }
                    }
                }

                is CreateEditTaskUiEvent.Error -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isEditMode) "Edit Task" else "New Task",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                windowInsets = WindowInsets(0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Task Details Section ──────────────────────────────────
            SectionCard(title = "Task Details") {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Title") },
                    placeholder = { Text("What do you want to work on?") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Description") },
                    placeholder = { Text("Add more details...") },
                    minLines = 2,
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = uiState.remarks,
                    onValueChange = { viewModel.updateRemarks(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Remarks") },
                    placeholder = { Text("Any notes or observations...") },
                    minLines = 2,
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // ── Satisfaction Section ──────────────────────────────────
            SectionCard(title = "Today's Satisfaction") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "How satisfied are you?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${uiState.satisfyPercentage.text}%",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    FilledTonalButton(
                        onClick = { viewModel.updateShowPercentageDialog(true) },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Change")
                    }
                }
            }

            // ── Duration Section ──────────────────────────────────────
            SectionCard(
                title = "Work Durations",
                trailing = {
                    TextButton(
                        onClick = { viewModel.addTaskDuration() }
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Add")
                    }
                }
            ) {
                if (uiState.taskDurations.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "No durations added yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    uiState.taskDurations.forEachIndexed { index, state ->
                        if (index > 0) HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                        TaskDurationItem(
                            index = index,
                            taskDurationUiState = state,
                            onRemove = { viewModel.removeTaskDuration(it) },
                            updateShowTimePicker = { viewModel.updateShowTimePickerDialog(it) },
                            updateIsTimePickerForStartTime = {
                                viewModel.updateIsTimePickerForStartTime(
                                    it
                                )
                            },
                            millisToFormatedTime = { DateTimeUtils.millisToFormattedTime(it) },
                            onDurationSelected = { viewModel.updateSelectedDurationId(state.durationId) },
                            millisToFormatedDuration = { DateTimeUtils.millisToFormattedDuration(it) }
                        )
                    }
                }
            }

            // ── Save Button ───────────────────────────────────────────
            Button(
                onClick = { viewModel.createOrUpdateDailyTask(uiState.toModel()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (uiState.isEditMode) "Update Task" else "Save Task",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        // ── Dialogs ───────────────────────────────────────────────────
        if (uiState.showSelectSatisfyPerDialog) {
            SelectSatisfyPercentageDialog(
                onDismiss = { viewModel.updateShowPercentageDialog(false) }
            ) { percentage ->
                viewModel.updateSatisfyPercentage(percentage)
            }
        }

        if (uiState.showTimePickerDialog) {
            val selectedDuration =
                uiState.taskDurations.find { it.durationId == uiState.selectedDurationId }
            selectedDuration?.let { state ->
                TimePickerDialog(onDismiss = { viewModel.updateShowTimePickerDialog(false) }) { hour, minute ->
                    val timeInMillis = DateTimeUtils.timePickerToMillis(hour, minute)
                    if (uiState.isTimePickerForStartTime) {
                        viewModel.onTaskDurationUpdated(state.copy(startTime = timeInMillis))
                    } else {
                        viewModel.onTaskDurationUpdated(state.copy(endTime = timeInMillis))
                    }
                }
            }
        }
    }
}

// ── Section Card ─────────────────────────────────────────────────────────────

@Composable
fun SectionCard(
    title: String,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp, MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                trailing?.invoke()
            }
            content()
        }
    }
}

// ── Task Duration Item ────────────────────────────────────────────────────────

@Composable
fun TaskDurationItem(
    index: Int,
    taskDurationUiState: TaskDurationUiState,
    onRemove: (String) -> Unit,
    updateShowTimePicker: (Boolean) -> Unit,
    updateIsTimePickerForStartTime: (Boolean) -> Unit,
    millisToFormatedTime: (Long) -> String,
    onDurationSelected: () -> Unit,
    millisToFormatedDuration: (Long) -> String
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${index + 1}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Text(
                    text = "Duration",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = { onRemove(taskDurationUiState.durationId) },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TimeChip(
                modifier = Modifier.weight(1f),
                label = "Start",
                time = if (taskDurationUiState.startTime == 0L) null
                else millisToFormatedTime(taskDurationUiState.startTime),
                onClick = {
                    onDurationSelected()
                    updateShowTimePicker(true)
                    updateIsTimePickerForStartTime(true)
                }
            )
            TimeChip(
                modifier = Modifier.weight(1f),
                label = "End",
                time = if (taskDurationUiState.endTime == 0L) null
                else millisToFormatedTime(taskDurationUiState.endTime),
                onClick = {
                    onDurationSelected()
                    updateShowTimePicker(true)
                    updateIsTimePickerForStartTime(false)
                }
            )
        }

        if (taskDurationUiState.startTime != 0L && taskDurationUiState.endTime != 0L) {
            val crossDuration = taskDurationUiState.endTime - taskDurationUiState.startTime
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Total Duration",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = millisToFormatedDuration(crossDuration),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun TimeChip(
    modifier: Modifier = Modifier,
    label: String,
    time: String?,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (time != null)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (time != null) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = time ?: "Tap to set",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (time != null) FontWeight.SemiBold else FontWeight.Normal,
                color = if (time != null) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Satisfy Percentage Dialog ─────────────────────────────────────────────────

@Composable
fun SelectSatisfyPercentageDialog(
    onDismiss: () -> Unit,
    onConfirm: (SatisfyPercentage) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Satisfaction Level",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                SatisfyPercentage.entries.forEach { satisfyPercentage ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                onConfirm(satisfyPercentage)
                                onDismiss()
                            },
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${satisfyPercentage.text}%",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            val level = when {
                                satisfyPercentage.text >= 80 -> "Excellent"
                                satisfyPercentage.text >= 60 -> "Good"
                                satisfyPercentage.text >= 40 -> "Average"
                                else -> "Needs work"
                            }
                            Text(
                                text = level,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

// ── Time Picker Dialog ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(onDismiss: () -> Unit, onConfirm: (Int, Int) -> Unit) {
    val state = rememberTimePickerState()
    TimePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                onConfirm(state.hour, state.minute)
                onDismiss()
            }) { Text("Confirm") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Select Time") }
    ) {
        TimePicker(state)
    }
}
