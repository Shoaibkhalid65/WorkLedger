package com.gshoaib998.progressly.service

import com.gshoaib998.progressly.data.repository.DailyTaskRepository
import com.gshoaib998.progressly.model.TaskDuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingServiceManager @Inject constructor(
    private val repository: DailyTaskRepository
) {
    private val _elapsedMillis = MutableStateFlow(0L)
    val elapsedMillis = _elapsedMillis.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    private val _startTime = MutableStateFlow(0L)
    val startTime = _startTime.asStateFlow()

    private val _events = MutableSharedFlow<TrackingEvent>(replay = 0)
    val events = _events.asSharedFlow()


    val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    fun startTracking(startTime: Long) {
        _startTime.value = startTime
        _isTracking.value = true
        _elapsedMillis.value = 0L
    }

    fun stopTracking() {
        _startTime.value = 0L
        _isTracking.value = false
        _elapsedMillis.value = 0L
    }

    fun updateElapsed(elapsed: Long) {
        _elapsedMillis.value = elapsed
    }

    // ── Called ONLY by the Service (notification path) ────────
    // Works whether the app is alive or dead
    fun saveAndStop(onComplete: () -> Unit) {
        val startTime = _startTime.value
        val endTime = startTime + _elapsedMillis.value

        if (startTime == 0L || endTime <= startTime) {
            stopTracking()
            onComplete()
            return
        }

        coroutineScope.launch {
            repository.createOrUpdateTaskDuration(
                TaskDuration(startTime = startTime, endTime = endTime)
            )
            stopTracking()
            // Signal the UI if it is alive — if not, this is a no-op
            _events.emit(TrackingEvent.StopAndSave)
            onComplete()
        }
    }

    fun cancelAndStop(onComplete: () -> Unit) {
        coroutineScope.launch {
            stopTracking()
            _events.emit(TrackingEvent.Cancelled)
            onComplete()
        }
    }

    suspend fun emitEvent(event: TrackingEvent) {
        _events.emit(event)
    }

}

sealed class TrackingEvent {
    data object Cancelled : TrackingEvent()
    data object StopAndSave : TrackingEvent()  // no data needed — service already saved
}