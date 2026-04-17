package com.gshoaib998.progressly.ui.gaols

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gshoaib998.progressly.data.repository.GoalRepository
import com.gshoaib998.progressly.model.Goal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalsListViewModel @Inject constructor(
    private val repository: GoalRepository
) : ViewModel() {

    private val _goalsUiState = MutableStateFlow(GoalUiState())
    val goalsUiState = _goalsUiState.asStateFlow()

    private val _events = MutableSharedFlow<GoalsUiEvent>()
    val events = _events.asSharedFlow()

    private val _filterState = MutableStateFlow(GoalFilterState())


    init {
        loadGoals()
    }

    private fun loadGoals() {
        viewModelScope.launch {
            repository.observeAllGoals().combine(_filterState){ goals,filterState ->
                 applyFilters(goals,filterState)
            }.collect { goals->
                _goalsUiState.update { uiState ->
                    uiState.copy(
                        isLoading = false,
                        allGoals = goals,
                        completedGoals = goals.filter { it.isCompleted },
                        pendingGoals = goals.filterNot { it.isCompleted },
                        searchQuery = _filterState.value.searchQuery,
                        sortOption = _filterState.value.sortOption
                    )
                }
            }
        }
    }

    private fun applyFilters(goals: List<Goal>, filterState: GoalFilterState): List<Goal> {
        var filteredGoals= goals

        if(filterState.searchQuery.isNotBlank()){
            filteredGoals=filteredGoals.filter {
                it.title.contains(filterState.searchQuery, ignoreCase = true)
            }
        }

        filteredGoals = when(filterState.sortOption){
            GoalSortOption.NEWEST_FIRST -> filteredGoals.sortedByDescending { it.createdAt }
            GoalSortOption.OLDEST_FIRST -> filteredGoals.sortedBy { it.createdAt }
            GoalSortOption.URGENCY_LEVEL -> filteredGoals.sortedBy { it.urgencyLevel }
            GoalSortOption.IMPORTANCE_LEVEL -> filteredGoals.sortedBy { it.importanceLevel }
            GoalSortOption.DIFFICULTY_LEVEL -> filteredGoals.sortedBy { it.difficultyLevel }
        }

        return filteredGoals
    }

    fun onEditGoalClick(goalId: Long) {
        viewModelScope.launch {
            _events.emit(GoalsUiEvent.NavigateToEditScreen(goalId))
        }
    }

    fun onCreateGoalClick() {
        viewModelScope.launch {
            _events.emit(GoalsUiEvent.NavigateToCreateScreen)
        }
    }

    fun deleteGoal(goalId: Long) {
        viewModelScope.launch {
            val goal = repository.getGoalById(goalId).first()
            val goalsDeletedResult = repository.deleteGoal(goal)
            if (goalsDeletedResult.isSuccess) {
                val goalsDeleted = goalsDeletedResult.getOrNull()
                goalsDeleted?.let {
                    if (it == 0) {
                        _events.emit(GoalsUiEvent.Error("goal not deleted"))
                    } else {
                        _events.emit(GoalsUiEvent.Success("$it goal deleted"))
                    }
                }
            }
        }
    }

    fun deleteAllGoals() {
        viewModelScope.launch {
            val goalsDeletedResult = repository.deleteAllGoals()
            if (goalsDeletedResult.isSuccess) {
                val goalsDeleted = goalsDeletedResult.getOrNull()
                goalsDeleted?.let {
                    if (it == 0) {
                        _events.emit(GoalsUiEvent.Error("goal not deleted"))
                    } else {
                        _events.emit(GoalsUiEvent.Success("$it goal deleted"))
                    }
                }
            }
        }
    }

    fun onDeleteGoalClick(goalId: Long) {
        viewModelScope.launch {
            _events.emit(GoalsUiEvent.ShowDeleteDialog(goalId))
        }
    }

    fun onDeleteAllGoalsClick() {
        viewModelScope.launch {
            _events.emit(GoalsUiEvent.ShowDeleteAllDialog)
        }
    }


    fun onIsCompletedToggleClick(goal: Goal, isChecked: Boolean) {
        viewModelScope.launch {
            val updatedGoal = if (isChecked) {
                goal.copy(isCompleted = true, completionDate = System.currentTimeMillis())
            } else {
                goal.copy(isCompleted = false, completionDate = 0L)
            }
            val result = repository.createOrUpdateGoal(updatedGoal)
            result.onFailure {
                _events.emit(GoalsUiEvent.Error(it.message ?: "unknown error message"))
            }
            result.onSuccess {
                _events.emit(GoalsUiEvent.Success("Goal Completed status changed Successfully"))
            }
        }
    }

    fun updateShowDeleteDialog(showDeleteDialog: Boolean) {
        _goalsUiState.update { it.copy(showDeleteDialog = showDeleteDialog) }
    }

    fun updateShowDeleteAllDialog(showDeleteAllDialog: Boolean) {
        _goalsUiState.update { it.copy(showDeleteAllDialog = showDeleteAllDialog) }
    }

    fun updateGoalToDeleteId(id: Long) {
        _goalsUiState.update { it.copy(goalToDeleteId = id) }
    }

    fun updateSearchQuery(query: String) {
        _filterState.update {
            it.copy(searchQuery = query)
        }
    }

    fun updateSortOption(sortOption: GoalSortOption) {
        _filterState.update {
            it.copy(sortOption = sortOption)
        }
    }

    fun updateShowSortDropDown(showDropDown: Boolean) {
        _goalsUiState.update {
            it.copy(showSortDropDown = showDropDown)
        }
    }

}


sealed class GoalsUiEvent {
    data class Success(val data: String) : GoalsUiEvent()
    data class Error(val errorMessage: String) : GoalsUiEvent()
    data class NavigateToEditScreen(val goalId: Long) : GoalsUiEvent()
    object NavigateToCreateScreen : GoalsUiEvent()
    data class ShowDeleteDialog(val goalId: Long) : GoalsUiEvent()
    object ShowDeleteAllDialog : GoalsUiEvent()
}


data class GoalUiState(
    val isLoading: Boolean = true,
    val allGoals: List<Goal> = emptyList(),
    val pendingGoals: List<Goal> = emptyList(),
    val completedGoals: List<Goal> = emptyList(),
    val completionDate: Long = 0L,
    val showDeleteDialog: Boolean = false,
    val showDeleteAllDialog: Boolean = false,
    val goalToDeleteId: Long = -1L,
    val searchQuery: String = "",
    val sortOption: GoalSortOption = GoalSortOption.NEWEST_FIRST,
    val showSortDropDown: Boolean = false
)


enum class GoalSortOption(val text: String) {
    NEWEST_FIRST("Newest First"),
    OLDEST_FIRST("Oldest First"),
    URGENCY_LEVEL("Urgency ↓"),
    DIFFICULTY_LEVEL("Difficulty ↓"),
    IMPORTANCE_LEVEL("Importance ↓")
}

data class GoalFilterState(
    val searchQuery: String = "",
    val sortOption: GoalSortOption = GoalSortOption.NEWEST_FIRST
)