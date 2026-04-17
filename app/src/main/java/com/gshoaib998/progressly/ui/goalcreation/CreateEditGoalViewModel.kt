package com.gshoaib998.progressly.ui.goalcreation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gshoaib998.progressly.data.repository.GoalRepository
import com.gshoaib998.progressly.model.DifficultyLevel
import com.gshoaib998.progressly.model.Goal
import com.gshoaib998.progressly.model.ImportanceLevel
import com.gshoaib998.progressly.model.UrgencyLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateEditGoalViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val goalId: Long? = savedStateHandle.get<Long>("goalId")

    private val _uiState = MutableStateFlow(CreateEditGoalUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<CreateEditGoalUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        goalId?.let { goalId ->
            loadGoalForEditing(goalId)
        }
    }

    private fun loadGoalForEditing(goalId: Long) {
        viewModelScope.launch {
            goalRepository.getGoalById(goalId).collect { goal ->
                _uiState.update {
                    goal.toUiState()
                }
            }
        }
    }


    fun createOrUpdateGoal(goal: Goal) {
        viewModelScope.launch {
            if (goal.title.isEmpty()) {
                _uiEvent.emit(CreateEditGoalUiEvent.Error("Title can't be empty"))
                return@launch
            }
            val result = goalRepository.createOrUpdateGoal(goal)
            result.onSuccess {
                _uiEvent.emit(CreateEditGoalUiEvent.NavigateToListScreen)
            }
            result.onFailure {
                _uiEvent.emit(CreateEditGoalUiEvent.Error(it.message ?: "unknown error"))
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updateExpectedCompletionDate(expectedCompletionDate: Long) {
        _uiState.update { it.copy(expectedCompletionDate = expectedCompletionDate) }
    }

    fun updateDifficultyLevel(difficultyLevel: DifficultyLevel) {
        _uiState.update { it.copy(difficultyLevel = difficultyLevel) }
    }

    fun updateImportanceLevel(importanceLevel: ImportanceLevel) {
        _uiState.update { it.copy(importanceLevel = importanceLevel) }
    }

    fun updateUrgencyLevel(urgencyLevel: UrgencyLevel) {
        _uiState.update { it.copy(urgencyLevel = urgencyLevel) }
    }

    fun updateDisplayStateOfDatePickerDialog(showDatePickerDialog: Boolean) {
        _uiState.update { it.copy(showDatePickerDialog = showDatePickerDialog) }
    }

}

data class CreateEditGoalUiState(
    val goalId: Long? = null,
    val title: String = "",
    val description: String = "",
    val expectedCompletionDate: Long = 0L,
    val difficultyLevel: DifficultyLevel = DifficultyLevel.EASY,
    val importanceLevel: ImportanceLevel = ImportanceLevel.AVERAGE,
    val urgencyLevel: UrgencyLevel = UrgencyLevel.NOT_URG,
    val isEditMode: Boolean = false,
    val showDatePickerDialog: Boolean = false
)

sealed class CreateEditGoalUiEvent() {
    data class Error(val errorMessage: String) : CreateEditGoalUiEvent()
    data object NavigateToListScreen : CreateEditGoalUiEvent()
}

fun Goal.toUiState() = CreateEditGoalUiState(
    goalId = id,
    title = title,
    description = description,
    expectedCompletionDate = expectedCompletionDate,
    difficultyLevel = difficultyLevel,
    importanceLevel = importanceLevel,
    urgencyLevel = urgencyLevel,
    isEditMode = true
)

fun CreateEditGoalUiState.toModel() = Goal(
    id = goalId ?: 0L,
    createdAt = System.currentTimeMillis(),
    title = title,
    description = description,
    isCompleted = false,
    expectedCompletionDate = expectedCompletionDate,
    completionDate = 0L,
    difficultyLevel = difficultyLevel,
    importanceLevel = importanceLevel,
    urgencyLevel = urgencyLevel
)
