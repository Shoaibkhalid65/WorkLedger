package com.example.progresstracker.ui.taskcreation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progresstracker.data.repository.DailyTaskRepository
import com.example.progresstracker.model.DailyTask
import com.example.progresstracker.model.SatisfyPercentage
import com.example.progresstracker.model.TaskDuration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateEditTaskViewModel @Inject constructor(
    private val repository: DailyTaskRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val taskId: Long? = savedStateHandle.get<Long>("taskId")

    private val _uiState = MutableStateFlow(CreateEditTaskUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<CreateEditTaskUiEvent>()
    val uiEvent = _uiEvents.asSharedFlow()

    init {
        taskId?.let {
            loadTaskForEditing(it)
        }
    }

    private fun loadTaskForEditing(taskId: Long) {
        viewModelScope.launch {
            val task = repository.getDailyTaskById(taskId)
            if (task != null) {
                _uiState.update {
                    task.toUiState()
                }
            } else {
                _uiEvents.emit(CreateEditTaskUiEvent.Error("Task not found"))
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updateRemarks(remarks: String) {
        _uiState.update { it.copy(remarks = remarks) }
    }

    fun updateSatisfyPercentage(satisfyPercentage: SatisfyPercentage) {
        _uiState.update { it.copy(satisfyPercentage = satisfyPercentage) }
    }

    fun addTaskDuration() {
        val updatedDurations =
            _uiState.value.taskDurations.toMutableList().apply { add(TaskDurationUiState()) }
        _uiState.update { it.copy(taskDurations = updatedDurations) }
    }

    fun removeTaskDuration(durationId: String) {
        _uiState.update { uiState ->
            uiState.copy(taskDurations = uiState.taskDurations.filter { it.durationId != durationId })
        }
    }

    fun onTaskDurationUpdated(taskDurationUiState: TaskDurationUiState) {
        _uiState.update { uiState ->
            uiState.copy(taskDurations = uiState.taskDurations.map {
                if (it.durationId == taskDurationUiState.durationId) taskDurationUiState else it
            })
        }
    }

    fun updateShowTimePickerDialog(showDialog: Boolean) {
        _uiState.update {
            it.copy(showTimePickerDialog = showDialog)
        }
    }

    fun updateShowPercentageDialog(showDialog: Boolean) {
        _uiState.update {
            it.copy(showSelectSatisfyPerDialog = showDialog)
        }
    }

    fun updateIsTimePickerForStartTime(isForStartTime: Boolean) {
        _uiState.update {
            it.copy(isTimePickerForStartTime = isForStartTime)
        }
    }

    fun updateSelectedDurationId(id: String?) {
        _uiState.update {
            it.copy(selectedDurationId = id)
        }
    }

    fun createOrUpdateDailyTask(dailyTask: DailyTask) {
        viewModelScope.launch {
            if (dailyTask.title.isEmpty()) {
                _uiEvents.emit(CreateEditTaskUiEvent.Error("Title can't be empty"))
                return@launch
            }
            dailyTask.durations.forEach { duration ->
                if (duration.startTime != 0L && duration.endTime == 0L) {
                    _uiEvents.emit(CreateEditTaskUiEvent.Error("Also select the end time"))
                    return@launch
                }
                if (duration.startTime == 0L && duration.endTime != 0L) {
                    _uiEvents.emit(CreateEditTaskUiEvent.Error("Also select the start time"))
                    return@launch
                }
            }
            repository.createOrUpdateDailyTaskWithDurations(dailyTask)
            _uiEvents.emit(CreateEditTaskUiEvent.NavigateToTasksScreen)
        }
    }

}

data class CreateEditTaskUiState(
    val id: Long? = null,
    val englishDate : Long =0L,
    val title: String = "",
    val description: String = "",
    val remarks: String = "",
    val satisfyPercentage: SatisfyPercentage = SatisfyPercentage.PER_0,
    val taskDurations: List<TaskDurationUiState> = emptyList(),
    val isEditMode: Boolean = false,
    val showTimePickerDialog: Boolean = false,
    val showSelectSatisfyPerDialog: Boolean = false,
    val isTimePickerForStartTime: Boolean = true,
    val selectedDurationId: String? = null
)

data class TaskDurationUiState(
    val id: Long? = null,
    val durationId: String = UUID.randomUUID().toString(),
    val startTime: Long = 0L,
    val endTime: Long = 0L,
)

fun TaskDurationUiState.toModel() = TaskDuration(
    id = id ?: 0L,
    startTime = startTime,
    endTime = endTime
)

fun TaskDuration.toUIState() = TaskDurationUiState(
    id = id,
    startTime = startTime,
    endTime = endTime
)

fun DailyTask.toUiState() = CreateEditTaskUiState(
    id = id,
    englishDate=englishDate,
    title = title,
    description = description,
    remarks = remarks,
    satisfyPercentage = satisfyPercentage,
    taskDurations = durations.map { it.toUIState() },
    isEditMode = true,
)

fun CreateEditTaskUiState.toModel() = DailyTask(
    id = id ?: 0L,
    title = title,
    description = description,
    remarks = remarks,
    satisfyPercentage = satisfyPercentage,
    englishDate = if(englishDate!=0L) englishDate else System.currentTimeMillis(),
    durations = if (taskDurations.isNotEmpty()) taskDurations.map { it.toModel() } else emptyList()
)


sealed class CreateEditTaskUiEvent {
    data class Error(val message: String) : CreateEditTaskUiEvent()
    object NavigateToTasksScreen : CreateEditTaskUiEvent()
}