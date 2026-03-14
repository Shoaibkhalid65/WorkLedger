package com.example.progresstracker.ui.dailyTask.tasksList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progresstracker.data.repository.DailyTaskRepository
import com.example.progresstracker.model.DailyTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class TasksListViewModel @Inject constructor(
    private val repository: DailyTaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TasksListUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<TasksListUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _filterState = MutableStateFlow(FilterState())

    init {
        loadDailyTasks()
    }

    private fun loadDailyTasks() {
        viewModelScope.launch {
            repository.observeAllRealTasks().combine(
                _filterState
            ) { tasks, filterState ->
                applyFilters(tasks, filterState)
            }.collect { filteredTasks ->
                _uiState.update {
                    it.copy(
                        tasks = filteredTasks,
                        isLoading = false,
                        searchQuery = _filterState.value.searchQuery,
                        sortOption = _filterState.value.sortOption,
                        selectionOption = _filterState.value.selectionOption
                    )
                }
            }
        }
    }


    private fun applyFilters(
        tasks: List<DailyTask>,
        filterState: FilterState
    ): List<DailyTask> {
        var filteredTasks = tasks
        val currentDateTime = getDateTimeFromMillis(System.currentTimeMillis())
        val currentYear = currentDateTime.year
        val currentMonth = currentDateTime.monthValue
        val currentDayOfMonth = currentDateTime.dayOfMonth
        val currentDayOfWeek = currentDateTime.dayOfWeek



        filteredTasks = when (filterState.selectionOption) {
            SelectionOption.ALL -> filteredTasks
            SelectionOption.THIS_YEAR -> filteredTasks.filter { getDateTimeFromMillis(it.englishDate).year == currentYear }
            SelectionOption.THIS_MONTH -> filteredTasks.filter {
                getDateTimeFromMillis(it.englishDate).let { dt->
                    dt.year == currentYear && dt.monthValue == currentMonth
                }
            }

            SelectionOption.TODAY -> filteredTasks.filter {
                getDateTimeFromMillis(it.englishDate).let { dt ->
                    dt.year == currentYear
                            && dt.monthValue == currentMonth
                            && dt.dayOfMonth == currentDayOfMonth
                }
            }

            SelectionOption.THIS_WEEK -> filteredTasks.filter {
                isInThisWeek(currentDateTime,getDateTimeFromMillis(it.englishDate))
            }
        }


        if (filterState.searchQuery.isNotBlank()) {
            filteredTasks = filteredTasks.filter {
                it.title.contains(filterState.searchQuery, ignoreCase = true)
            }
        }

        filteredTasks = when (filterState.sortOption) {
            SortOption.NEWEST_FIRST -> filteredTasks.sortedByDescending { it.englishDate }
            SortOption.OLDEST_FIRST -> filteredTasks.sortedBy { it.englishDate }
            SortOption.WORK_TIME_HIGH_TO_LOW -> filteredTasks.sortedByDescending { it.totalTaskDuration }
            SortOption.WORK_TIME_LOW_TO_HIGH -> filteredTasks.sortedBy { it.totalTaskDuration }
            SortOption.SATIS_PER_HIGH_TO_LOW -> filteredTasks.sortedByDescending { it.satisfyPercentage.text }
            SortOption.SATIS_PER_LOW_TO_HIGH -> filteredTasks.sortedBy { it.satisfyPercentage.text }

        }

        return filteredTasks
    }

    private fun isInThisWeek(currentDateTime: LocalDateTime, taskDateTime: LocalDateTime): Boolean {
        val today = currentDateTime.toLocalDate()
        val taskDate = taskDateTime.toLocalDate()

        val weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val weekEnd = weekStart.plusDays(6)

        return taskDate in weekStart .. weekEnd
    }

    private fun getDateTimeFromMillis(millis: Long): LocalDateTime {
        val localDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(millis),
            ZoneId.systemDefault()
        )
        return localDateTime
    }


    fun onUpdateTaskClick(taskId: Long) {
        viewModelScope.launch {
            _uiEvent.emit(
                TasksListUiEvent.NavigateToEditTask(taskId)
            )
        }
    }

    fun onCreateTaskClick() {
        viewModelScope.launch {
            _uiEvent.emit(
                TasksListUiEvent.NavigateToCreateTask
            )
        }
    }


    fun deleteDailyTask(taskId: Long) {
        viewModelScope.launch {
            if (taskId != -1L) {
                val dailyTask = repository.getDailyTaskById(taskId)
                repository.deleteDailyTask(dailyTask)
                    .onSuccess { tasksDeleted ->
                        if (tasksDeleted == 0) {
                            _uiEvent.emit(
                                TasksListUiEvent.Error("task not deleted")
                            )
                        } else {
                            _uiEvent.emit(
                                TasksListUiEvent.Success("task deleted successfully")
                            )
                        }

                    }
                    .onFailure {
                        _uiEvent.emit(
                            TasksListUiEvent.Error(it.message ?: "unknown error message")
                        )
                    }
            }
        }
    }

    fun deleteAllDailyTasks() {
        viewModelScope.launch {
            repository.deleteAllDailyTasks()
                .onSuccess { tasksDeleted ->
                    if (tasksDeleted == 0) {
                        _uiEvent.emit(
                            TasksListUiEvent.Error("tasks not deleted")
                        )
                    } else {
                        _uiEvent.emit(
                            TasksListUiEvent.Success("$tasksDeleted tasks deleted successfully")
                        )
                    }
                }
                .onFailure {
                    _uiEvent.emit(
                        TasksListUiEvent.Error(it.message ?: "unknown error message")
                    )
                }
        }
    }

    fun updateShowDeleteDialog(showDialog: Boolean) {
        _uiState.update {
            it.copy(showDeleteDialog = showDialog)
        }
    }


    fun millisToFormattedDuration(millis: Long): String {
        val localDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(millis),
            ZoneId.of("UTC")
        )
        return String.format(
            Locale.getDefault(),
            "%02d:%02d",
            localDateTime.hour,
            localDateTime.minute
        )
    }

    fun updateShowDeleteAllDialog(showDialog: Boolean) {
        _uiState.update {
            it.copy(showDeleteAllDialog = showDialog)
        }
    }

    fun updateTaskToDeleteId(taskId: Long) {
        _uiState.update {
            it.copy(taskToDeleteId = taskId)
        }
    }

    fun updateSearchQuery(searchQuery: String) {
        _filterState.update {
            it.copy(searchQuery = searchQuery)
        }
    }

    fun updateSortOption(sortOption: SortOption) {
        _filterState.update {
            it.copy(sortOption = sortOption)
        }
    }

    fun updateShowSortDropDown(showDropDown: Boolean) {
        _uiState.update {
            it.copy(showSortDropDown = showDropDown)
        }
    }

    fun updateSelectionOption(selectionOption: SelectionOption) {
        _filterState.update {
            it.copy(selectionOption = selectionOption)
        }
    }

    fun formatedDate(epochMillis: Long, isOnlyDateRequired: Boolean = false): String {
        val instant = Instant.ofEpochMilli(epochMillis)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter =
            if (isOnlyDateRequired) DateTimeFormatter.ofPattern("dd MMM yyyy") else DateTimeFormatter.ofPattern(
                "dd MMM yyyy, hh:mm a"
            )
        return localDateTime.format(formatter)
    }


}

data class TasksListUiState(
    val isLoading: Boolean = true,
    val tasks: List<DailyTask> = emptyList(),
    val showDeleteDialog: Boolean = false,
    val showDeleteAllDialog: Boolean = false,
    val taskToDeleteId: Long = -1L,
    val searchQuery: String = "",
    val sortOption: SortOption = SortOption.NEWEST_FIRST,
    val showSortDropDown: Boolean = false,
    val selectionOption: SelectionOption = SelectionOption.ALL
)

sealed class TasksListUiEvent {
    data class NavigateToEditTask(val taskId: Long) : TasksListUiEvent()
    data object NavigateToCreateTask : TasksListUiEvent()
    data class Error(val message: String) : TasksListUiEvent()
    data class Success(val message: String) : TasksListUiEvent()
}

enum class SortOption(val text: String) {
    NEWEST_FIRST("Newest First"),
    OLDEST_FIRST("Oldest First"),
    WORK_TIME_HIGH_TO_LOW("Work Time Desc"),
    WORK_TIME_LOW_TO_HIGH("Work Time Asc"),
    SATIS_PER_HIGH_TO_LOW("Satisfy Desc"),
    SATIS_PER_LOW_TO_HIGH("Satisfy Asc")
}

data class FilterState(
    val searchQuery: String = "",
    val sortOption: SortOption = SortOption.NEWEST_FIRST,
    val selectionOption: SelectionOption = SelectionOption.ALL
)

enum class SelectionOption(val text: String) {
    ALL("All"),
    THIS_YEAR("This Year"),
    THIS_MONTH("This Month"),
    THIS_WEEK("This Week"),
    TODAY("Today")
}

