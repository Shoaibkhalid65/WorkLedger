package com.gshoaib998.progressly.ui.goalcreation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.gshoaib998.progressly.model.DifficultyLevel
import com.gshoaib998.progressly.model.ImportanceLevel
import com.gshoaib998.progressly.model.UrgencyLevel
import com.gshoaib998.progressly.navigation.Screen
import com.gshoaib998.progressly.ui.taskcreation.SectionCard
import com.gshoaib998.progressly.utils.DateTimeUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditGoalScreen(
    navHostController: NavHostController? = null,
    outerPadding: PaddingValues = PaddingValues(),
    createEditGoalViewModel: CreateEditGoalViewModel = hiltViewModel(),
) {
    val uiState by createEditGoalViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        createEditGoalViewModel.uiEvent.collect { event ->
            when (event) {
                is CreateEditGoalUiEvent.Error -> snackbarHostState.showSnackbar(event.errorMessage)
                CreateEditGoalUiEvent.NavigateToListScreen -> {
                    navHostController?.navigate(Screen.GoalScreen.route) {
                        popUpTo(Screen.CreateEditGoalScreen.route) { inclusive = true }
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .padding(outerPadding)
            .fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isEditMode) "Edit Goal" else "New Goal",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navHostController?.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                windowInsets = WindowInsets(0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Goal Details ──────────────────────────────────────────
            SectionCard(title = "Goal Details") {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = { createEditGoalViewModel.updateTitle(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Title") },
                    placeholder = { Text("What do you want to achieve?") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { createEditGoalViewModel.updateDescription(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Description") },
                    placeholder = { Text("Describe your goal in detail...") },
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // ── Target Date ───────────────────────────────────────────
            SectionCard(title = "Target Date") {
                OutlinedCard(
                    onClick = { createEditGoalViewModel.updateDisplayStateOfDatePickerDialog(true) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (uiState.expectedCompletionDate != 0L)
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (uiState.expectedCompletionDate != 0L)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = if (uiState.expectedCompletionDate != 0L)
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Expected Completion",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (uiState.expectedCompletionDate != 0L)
                                    DateTimeUtils.formatedDate(uiState.expectedCompletionDate, true)
                                else "Select a target date",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (uiState.expectedCompletionDate != 0L)
                                    FontWeight.SemiBold else FontWeight.Normal,
                                color = if (uiState.expectedCompletionDate != 0L)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // ── Priority Settings ─────────────────────────────────────
            SectionCard(title = "Priority Settings") {
                LevelSelector(
                    label = "Difficulty",
                    entries = DifficultyLevel.entries.reversed(),
                    selected = uiState.difficultyLevel,
                    labelOf = { it.name },
                    onSelect = { createEditGoalViewModel.updateDifficultyLevel(it) }
                )
                LevelSelector(
                    label = "Importance",
                    entries = ImportanceLevel.entries.reversed(),
                    selected = uiState.importanceLevel,
                    labelOf = { it.name },
                    onSelect = { createEditGoalViewModel.updateImportanceLevel(it) }
                )
                LevelSelector(
                    label = "Urgency",
                    entries = UrgencyLevel.entries.reversed(),
                    selected = uiState.urgencyLevel,
                    labelOf = { it.name },
                    onSelect = { createEditGoalViewModel.updateUrgencyLevel(it) }
                )
            }

            // ── Save Button ───────────────────────────────────────────
            Button(
                onClick = {
                    createEditGoalViewModel.createOrUpdateGoal(uiState.toModel())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (uiState.isEditMode) "Update Goal" else "Save Goal",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    if (uiState.showDatePickerDialog) {
        ExpectedCompletionDatePickerDialog(
            onDateSelected = { createEditGoalViewModel.updateExpectedCompletionDate(it) },
            onDismiss = { createEditGoalViewModel.updateDisplayStateOfDatePickerDialog(false) }
        ) { msg ->
            scope.launch { snackbarHostState.showSnackbar(msg) }
        }
    }
}

// ── Generic Level Selector ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> LevelSelector(
    label: String,
    entries: List<T>,
    selected: T,
    labelOf: (T) -> String,
    onSelect: (T) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            entries.forEachIndexed { index, level ->
                SegmentedButton(
                    selected = selected == level,
                    onClick = { onSelect(level) },
                    shape = SegmentedButtonDefaults.itemShape(index, entries.size),
                    label = {
                        Text(
                            text = labelOf(level),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
            }
        }
    }
}

// ── Date Picker Dialog ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpectedCompletionDatePickerDialog(
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
    onError: (String) -> Unit
) {
    val state = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { onDateSelected(it) } ?: onError("Date is null!")
                onDismiss()
            }) { Text("Confirm") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        shape = RoundedCornerShape(16.dp)
    ) {
        DatePicker(state)
    }
}