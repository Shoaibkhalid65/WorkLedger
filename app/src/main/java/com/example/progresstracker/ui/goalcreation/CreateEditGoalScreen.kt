package com.example.progresstracker.ui.goalcreation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.progresstracker.model.DifficultyLevel
import com.example.progresstracker.model.ImportanceLevel
import com.example.progresstracker.model.UrgencyLevel
import com.example.progresstracker.navigation.Screen
import com.example.progresstracker.utils.DateTimeUtils
import kotlinx.coroutines.launch

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditGoalScreen(
    navHostController: NavHostController?=null,
    outerPadding: PaddingValues = PaddingValues(),
    createEditGoalViewModel: CreateEditGoalViewModel = hiltViewModel(),
) {
    val uiState by createEditGoalViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        createEditGoalViewModel.uiEvent.collect { event ->
            when (event) {
                is CreateEditGoalUiEvent.Error -> {
                    snackbarHostState.showSnackbar(event.errorMessage)
                }

                CreateEditGoalUiEvent.NavigateToListScreen -> {
                    navHostController?.navigate(Screen.GoalScreen.route) {
                        popUpTo(Screen.CreateEditGoalScreen.route) {
                            inclusive = true
                        }
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
                        text = if (uiState.isEditMode) "Edit Goal" else "Create Goal"
                    )
                },
                windowInsets = WindowInsets()
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = {
                    createEditGoalViewModel.updateTitle(it)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Enter title..."
                    )
                },
                minLines = 1,
                maxLines = 2
            )
            OutlinedTextField(
                value = uiState.description,
                onValueChange = {
                    createEditGoalViewModel.updateDescription(it)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Enter description..."
                    )
                },
                minLines = 2,
                maxLines = 3
            )

            Button(
                onClick = {
                    createEditGoalViewModel.updateDisplayStateOfDatePickerDialog(true)
                }
            ) {
                Text(
                    text = if (uiState.expectedCompletionDate != 0L) {
                        DateTimeUtils.formatedDate(
                            uiState.expectedCompletionDate,
                            isOnlyDateRequired = true
                        )
                    } else {
                        "Select Expected Completion Date"
                    }
                )
            }

            Text(
                text = "Select Difficulty Level"
            )

            SingleChoiceSegmentedButtonRow {
                DifficultyLevel.entries.reversed().forEachIndexed { index, level ->
                    SegmentedButton(
                        uiState.difficultyLevel == level, onClick = {
                            createEditGoalViewModel.updateDifficultyLevel(level)
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index,
                            DifficultyLevel.entries.size
                        )
                    ) {
                        Text(
                            text = level.name
                        )
                    }
                }
            }

            Text(
                text = "Select Importance Level"
            )

            SingleChoiceSegmentedButtonRow {
                ImportanceLevel.entries.reversed().forEachIndexed { index, level ->
                    SegmentedButton(
                        uiState.importanceLevel == level, onClick = {
                            createEditGoalViewModel.updateImportanceLevel(level)
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index,
                            DifficultyLevel.entries.size
                        )
                    ) {
                        Text(
                            text = level.name
                        )
                    }
                }
            }

            Text(
                text = "Select Urgency Level"
            )

            SingleChoiceSegmentedButtonRow {
                UrgencyLevel.entries.reversed().forEachIndexed { index, level ->
                    SegmentedButton(
                        uiState.urgencyLevel == level, onClick = {
                            createEditGoalViewModel.updateUrgencyLevel(level)
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index,
                            DifficultyLevel.entries.size
                        )
                    ) {
                        Text(
                            text = level.name
                        )
                    }
                }
            }

            Button(
                onClick = {
                    val goal = uiState.toModel()
                    createEditGoalViewModel.createOrUpdateGoal(goal)
                }
            ) {
                Text(
                    text = "Save"
                )
            }


        }
    }
    if (uiState.showDatePickerDialog) {
        ExpectedCompletionDatePickerDialog(
            onDateSelected = {
                createEditGoalViewModel.updateExpectedCompletionDate(it)
            },
            onDismiss = {
                createEditGoalViewModel.updateDisplayStateOfDatePickerDialog(false)
            }) {
            scope.launch {
                snackbarHostState.showSnackbar(message = "Error : $it")
            }
        }
    }
}


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
                state.selectedDateMillis?.let {
                    onDateSelected(it)
                } ?: onError("Date is Null!")
                onDismiss()
            }) {
                Text(
                    text = "Confirm"
                )
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
            }) {
                Text(
                    text = "Cancel"
                )
            }
        },
        shape = RoundedCornerShape(12.dp)
    ) {
        DatePicker(state)
    }
}