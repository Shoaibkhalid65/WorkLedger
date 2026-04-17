package com.gshoaib998.progressly.ui.dailyTask.taskDuration

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gshoaib998.progressly.data.repository.DailyTaskRepository
import com.gshoaib998.progressly.model.TaskDuration
import com.gshoaib998.progressly.service.TrackingEvent
import com.gshoaib998.progressly.service.TrackingForegroundService
import com.gshoaib998.progressly.service.TrackingServiceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDurationViewModel @Inject constructor(
    private val repository: DailyTaskRepository,
    private val trackingServiceManager: TrackingServiceManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskDurationUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<TaskDurationUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadTaskDurations()
        observeTrackingState()
        observeServiceEvents()
    }

    // ── Private loaders ───────────────────────────────────────

    private fun loadTaskDurations() {
        viewModelScope.launch {
            repository.observeDurationsForMaxTaskIdToShow().collect { durations ->
                _uiState.update { it.copy(durations = durations, isLoading = false) }
            }
        }
    }

    private fun observeTrackingState() {
        viewModelScope.launch {
            trackingServiceManager.isTracking.collect { isTracking ->
                _uiState.update { it.copy(isTracking = isTracking) }
            }
        }
        viewModelScope.launch {
            trackingServiceManager.elapsedMillis.collect { elapsed ->
                _uiState.update { it.copy(elapsedMillis = elapsed) }
            }
        }
        viewModelScope.launch {
            trackingServiceManager.startTime.collect { startTime ->
                _uiState.update { it.copy(startTime = startTime) }
            }
        }
    }

    private fun observeServiceEvents() {
        viewModelScope.launch {
            trackingServiceManager.events.collect { event ->
                when (event) {
                    is TrackingEvent.Cancelled -> {
                        _uiState.update { it.copy(showCreationDialog = false) }
                    }
                    is TrackingEvent.StopAndSave -> {
                        // Duration already saved by the Service — just close the dialog
                        _uiState.update { it.copy(showCreationDialog = false) }
                    }
                }
            }
        }
    }

    // ── Public actions (called from UI buttons) ───────────────

    fun startTracking() {
        val startTime = System.currentTimeMillis()
        trackingServiceManager.startTracking(startTime)

        val intent = TrackingForegroundService.startIntent(context, startTime)
        context.startForegroundService(intent)

        _uiState.update { it.copy(showCreationDialog = true) }
    }

    // In-app "Save & Stop" button — app is alive, so ViewModel saves directly
    fun stopAndSave() {
        val startTime = trackingServiceManager.startTime.value
        val endTime = startTime + trackingServiceManager.elapsedMillis.value
        saveDuration(startTime = startTime, endTime = endTime)
        sendActionToService(TrackingForegroundService.ACTION_CANCEL) // just kill the service, no double-save
        _uiState.update { it.copy(showCreationDialog = false) }
    }

    // In-app "Discard" button
    fun cancelTracking() {
        sendActionToService(TrackingForegroundService.ACTION_CANCEL)
        _uiState.update { it.copy(showCreationDialog = false) }
    }

    private fun sendActionToService(action: String) {
        context.startService(
            Intent(context, TrackingForegroundService::class.java).apply {
                this.action = action
            }
        )
    }

    // ── Duration persistence ──────────────────────────────────

    private fun saveDuration(startTime: Long, endTime: Long) {
        if (startTime == 0L || endTime <= startTime) {
            viewModelScope.launch {
                _uiEvent.emit(TaskDurationUiEvent.Error("Invalid duration times"))
            }
            return
        }
        viewModelScope.launch {
            repository.createOrUpdateTaskDuration(
                TaskDuration(startTime = startTime, endTime = endTime)
            ).onSuccess {
                _uiEvent.emit(TaskDurationUiEvent.Success("Duration saved"))
            }.onFailure {
                _uiEvent.emit(TaskDurationUiEvent.Error(it.message ?: "Failed to save"))
            }
        }
    }

    // ── Dialog state ──────────────────────────────────────────

    fun updateShowCreationDialog(show: Boolean) {
        _uiState.update { it.copy(showCreationDialog = show) }
    }

    fun updateShowDeletionDialog(show: Boolean) {
        _uiState.update { it.copy(showDeleteDialog = show) }
    }

    fun updateShowAllDeletionDialog(show: Boolean) {
        _uiState.update { it.copy(showDeleteAllDialog = show) }
    }

    fun updateDurationToDeleteId(id: Long) {
        _uiState.update { it.copy(durationToDeleteId = id) }
    }

    // ── Deletion ──────────────────────────────────────────────

    fun deleteTaskDuration(durationId: Long) {
        viewModelScope.launch {
            if (durationId != -1L) {
                val taskDuration = repository.observeDurationById(durationId).first()
                repository.deleteTaskDuration(taskDuration)
                    .onSuccess { _uiEvent.emit(TaskDurationUiEvent.Success("Duration deleted")) }
                    .onFailure { _uiEvent.emit(TaskDurationUiEvent.Error(it.message ?: "Error")) }
            }
        }
    }

    fun deleteTaskDurationsOfMaxId() {
        viewModelScope.launch {
            repository.deleteAllTaskDurationsOfMaxId()
                .onSuccess { n ->
                    if (n == 0) _uiEvent.emit(TaskDurationUiEvent.Error("No durations deleted"))
                    else _uiEvent.emit(TaskDurationUiEvent.Success("$n durations deleted"))
                }
        }
    }
}

// ── State ─────────────────────────────────────────────────────────────────────

data class TaskDurationUiState(
    val isLoading: Boolean = true,
    val durations: List<TaskDuration> = emptyList(),
    val isTracking: Boolean = false,
    val startTime: Long = 0L,
    val elapsedMillis: Long = 0L,
    val showCreationDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val showDeleteAllDialog: Boolean = false,
    val durationToDeleteId: Long = -1L,
)

// ── Events ────────────────────────────────────────────────────────────────────

sealed class TaskDurationUiEvent {
    data class Success(val message: String) : TaskDurationUiEvent()
    data class Error(val message: String) : TaskDurationUiEvent()
}
